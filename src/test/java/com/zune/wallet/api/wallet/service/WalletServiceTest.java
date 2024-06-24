package com.zune.wallet.api.wallet.service;

import com.zune.wallet.TestBase;
import com.zune.wallet.api.common.exception.BusinessException;
import com.zune.wallet.api.wallet.service.model.WalletBalanceResponse;
import com.zune.wallet.api.wallet.service.model.WalletHistoryResponse;
import com.zune.wallet.domain.persistance.wallet.type.WalletTxType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WalletServiceTest extends TestBase {

    @Autowired
    private WalletService walletService;

    private final Long memberId = 1L;

    @Test
    @Order(1)
    void test_ok_잔금조회() {
        //회원 1 은 h2 db 에 사정 생성 되어 있음.
        WalletBalanceResponse response = walletService.getBalance(memberId);
        assertThat(response.getBalance()).isEqualTo(0.0D);
    }

    @Test
    @Order(2)
    void test_ok_입금처리_6000만원() {
        WalletBalanceResponse response = walletService.deposit(WalletTxType.DEPOSIT,  memberId, 60_000_000.0D);
        assertThat(response.getBalance()).isEqualTo(60_000_000.0D);
    }

    @Test
    @Order(3)
    void test_error_잔액보다_많은_출금요청() {
        Exception exception = Assertions.assertThrows(BusinessException.class, () -> walletService.withdraw(WalletTxType.WITHDRAW, memberId, 60_000_001.0D));
        String expectedMessage = "가능한 잔액이 부족 합니다.";
        assertThat(exception.getMessage()).contains(expectedMessage);
    }

    @Test
    @Order(4)
    void test_error_일회_한도_초과량_출금요청() {
        Exception exception = Assertions.assertThrows(BusinessException.class, () -> walletService.withdraw(WalletTxType.WITHDRAW, memberId, 10_000_001.0D));
        String expectedMessage = "요청 시 1회 최대 한도는";
        assertThat(exception.getMessage()).contains(expectedMessage);
    }

    @Test
    @Order(5)
    void test_ok_1000만원_출금() {
        WalletBalanceResponse response = walletService.withdraw(WalletTxType.WITHDRAW, memberId, 10_000_000.0D);
        assertThat(response.getBalance()).isEqualTo(50_000_000.0D);
    }

    @Test
    @Order(6)
    void test_ok_1000만원_동시_출금_요청시_1회만_처리되고_나머지는_처리_안됨() throws Exception {
        WalletBalanceResponse response = walletService.getBalance(memberId);
        double lastBalance = response.getBalance();

        var threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();
        CountDownLatch doneSignal = new CountDownLatch(threadCount);

        // Act
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    walletService.withdraw(WalletTxType.WITHDRAW, memberId, 10_000_000.0D);
                    successCount.getAndIncrement();
                } catch (ObjectOptimisticLockingFailureException e) {
                    failCount.getAndIncrement();
                }catch (Exception e){
                    throw new RuntimeException("test is not expected");
                } finally {
                    doneSignal.countDown();
                }
            });
        }
        doneSignal.await();
        executorService.shutdown();

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);

        WalletBalanceResponse nowBalance = walletService.getBalance(memberId);
        assertThat(nowBalance.getBalance()).isEqualTo(lastBalance - 10_000_000.0D);
    }

    @Test
    @Order(7)
    void test_error_1000만원_추가_출금_이후_일일_출금가능_금액_3000만원_한도초과로_출금_불가() {
        WalletBalanceResponse response2 = walletService.withdraw(WalletTxType.WITHDRAW, memberId, 10_000_000.0D);
        assertThat(response2.getBalance()).isEqualTo(30_000_000.0D);

        Exception exception = Assertions.assertThrows(BusinessException.class, () -> walletService.withdraw(WalletTxType.WITHDRAW, memberId, 100.0D));
        String expectedMessage = "요청 시 1일 최대 한도는";
        assertThat(exception.getMessage()).contains(expectedMessage);
    }

    @Test
    @Order(8)
    void test_ok_입출금_내역조회() {
        Page<WalletHistoryResponse> response = walletService.getTransactionHistory(1L, PageRequest.of(0, 30));
        assertThat(response.getContent().isEmpty()).isFalse();
        assertThat(response.getContent().size()).isGreaterThan(2); //입출금 최소 1번씩은 성공함.
    }
}