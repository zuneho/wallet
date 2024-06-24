package com.zune.wallet.api.purchase.service;

import com.zune.wallet.TestBase;
import com.zune.wallet.api.common.exception.BusinessException;
import com.zune.wallet.api.member.service.MemberService;
import com.zune.wallet.api.member.service.model.MemberCreateRequest;
import com.zune.wallet.api.purchase.service.model.PurchaseRequest;
import com.zune.wallet.api.purchase.service.model.PurchaseResponse;
import com.zune.wallet.api.wallet.service.WalletService;
import com.zune.wallet.api.wallet.service.model.WalletBalanceResponse;
import com.zune.wallet.domain.persistance.wallet.type.WalletTxType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PurchaseServiceTest extends TestBase {

    //H2 db init data.sql 로 미리 생성한 쿠폰 상품들
    private static final Long COUPON_5000_IDX = 1L;
    private static final Long COUPON_10000_IDX = 2L;
    private static final PurchaseRequest COUPON_5000_REQUEST = PurchaseRequest.builder()
            .productId(COUPON_5000_IDX).price(5000.0D)
            .build();
    private static final PurchaseRequest COUPON_10000_REQUEST = PurchaseRequest.builder()
            .productId(COUPON_10000_IDX).price(10000.0D)
            .build();

    @Autowired
    private MemberService memberService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private PurchaseService purchaseService;

    private Long memberId;

    private Long purchaseId5000;

    private Long purchaseId10000;

    @Test
    @Order(1)
    void test_before_회원_생성_및_15000원_입금() {
        MemberCreateRequest request = MemberCreateRequest.builder()
                .email("test9999@test.com")
                .name("test9999")
                .password("1234")
                .withdrawalAccount("123456789")
                .build();

        memberId = memberService.create(request);
        walletService.deposit(WalletTxType.DEPOSIT, memberId, 15000.0D);
    }

    @Test
    @Order(2)
    void test_error_5000원_쿠폰_구매_요청_가격이_잘못됨() {
        PurchaseRequest priceWrongRequest = PurchaseRequest.builder()
                .productId(COUPON_5000_IDX).price(2000.0D)
                .build();

        Exception exception = Assertions.assertThrows(BusinessException.class,
                () -> purchaseService.purchaseProduct(memberId, priceWrongRequest));
        String expectedMessage = "상품 가격이 변경 되었습니다.";
        assertThat(exception.getMessage()).contains(expectedMessage);
    }

    @Test
    @Order(2)
    void test_ok_5000원_쿠폰_구매() {
        purchaseId5000 = purchaseService.purchaseProduct(memberId, COUPON_5000_REQUEST);
        assertThat(purchaseId5000).isNotNull();

        WalletBalanceResponse response = walletService.getBalance(memberId);
        assertThat(response.getBalance()).isEqualTo(10000.0D);
    }


    @Test
    @Order(3)
    void test_ok_10000원_쿠폰_구매() {
        purchaseId10000 = purchaseService.purchaseProduct(memberId, COUPON_10000_REQUEST);
        assertThat(purchaseId10000).isNotNull();

        WalletBalanceResponse response = walletService.getBalance(memberId);
        assertThat(response.getBalance()).isEqualTo(0.0D);
    }

    @Test
    @Order(4)
    void test_error_잔금부족으로_쿠폰_구매불가() {
        Exception exception = Assertions.assertThrows(BusinessException.class,
                () -> purchaseService.purchaseProduct(memberId, COUPON_5000_REQUEST));
        String expectedMessage = "가능한 잔액이 부족 합니다.";
        assertThat(exception.getMessage()).contains(expectedMessage);
    }

    @Test
    @Order(5)
    void test_error_잘못된_주문에_대한_취소_요청시_실패() {
        Long wrongPurchaseId = purchaseId5000 * 100000;
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> purchaseService.cancelPurchase(memberId, wrongPurchaseId));

        String expectedMessage = "올바른 주문 정보를 조회 할 수 없습니다.";
        assertThat(exception.getMessage()).contains(expectedMessage);
    }

    @Test
    @Order(6)
    void test_ok_5000원_쿠폰_주문취소() {
        purchaseService.cancelPurchase(memberId, purchaseId5000);

        //wallet 처리후 잔액 확인
        WalletBalanceResponse response = walletService.getBalance(memberId);
        assertThat(response.getBalance()).isEqualTo(5000.0D);

        //해당 주문을 재 조회 해서 취소 처리된 내역을 확인
        PurchaseResponse purchaseResponse = purchaseService.getPurchase(memberId, purchaseId5000);
        assertThat(purchaseResponse.isCanceled()).isTrue();
        assertThat(purchaseResponse.getCanceledAt()).isNotNull();
    }

    @Test
    @Order(7)
    void test_ok_10000원_쿠폰_주문취소() {
        purchaseService.cancelPurchase(memberId, purchaseId10000);

        //wallet 처리후 잔액 확인
        WalletBalanceResponse response = walletService.getBalance(memberId);
        assertThat(response.getBalance()).isEqualTo(15000.0D);

        //해당 주문을 재 조회 해서 취소 처리된 내역을 확인
        PurchaseResponse purchaseResponse = purchaseService.getPurchase(memberId, purchaseId10000);
        assertThat(purchaseResponse.isCanceled()).isTrue();
        assertThat(purchaseResponse.getCanceledAt()).isNotNull();
    }

    @Test
    @Order(8)
    void test_error_이미_취소한_주문_취소요청시_취소_불가() {
        Exception exception = Assertions.assertThrows(BusinessException.class,
                () -> purchaseService.cancelPurchase(memberId, COUPON_5000_IDX));

        String expectedMessage = "이미 취소 처리된 주문 입니다.";
        assertThat(exception.getMessage()).contains(expectedMessage);

        WalletBalanceResponse response = walletService.getBalance(memberId);
        assertThat(response.getBalance()).isEqualTo(15000.0D);
    }
}