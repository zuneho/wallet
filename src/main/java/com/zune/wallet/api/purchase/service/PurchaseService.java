package com.zune.wallet.api.purchase.service;

import com.zune.wallet.api.common.exception.BusinessException;
import com.zune.wallet.api.purchase.service.model.PurchaseRequest;
import com.zune.wallet.api.purchase.service.model.PurchaseResponse;
import com.zune.wallet.api.wallet.service.WalletService;
import com.zune.wallet.domain.persistance.product.Product;
import com.zune.wallet.domain.persistance.product.ProductRepository;
import com.zune.wallet.domain.persistance.purchase.Purchase;
import com.zune.wallet.domain.persistance.purchase.PurchaseRepository;
import com.zune.wallet.domain.persistance.wallet.type.WalletTxType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PurchaseService {

    private final WalletService walletService;
    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;

    @Transactional
    public Long purchaseProduct(Long memberId, PurchaseRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("올바른 상품 정보를 조회 할 수 없습니다."));
        if (!product.getPrice().equals(request.getPrice())) {
            throw new BusinessException("상품 가격이 변경 되었습니다.");
        }

        Purchase newPurchase = Purchase.builder() //주문 처리
                .productId(product.getId())
                .purchaseAmount(product.getPrice())
                .memberId(memberId)
                .build();
        purchaseRepository.save(newPurchase);

        Long purchaseId = newPurchase.getId();
        walletService.withdraw(WalletTxType.PURCHASE_PRODUCT, memberId, product.getPrice(), purchaseId); //상품 구매로 인한 출금 처리

        return purchaseId;
    }

    @Transactional
    public void cancelPurchase(Long memberId, Long purchaseId) {
        Purchase purchase = purchaseRepository.findByIdAndMemberId(purchaseId, memberId);
        if (purchase == null) {
            throw new IllegalArgumentException("올바른 주문 정보를 조회 할 수 없습니다.");
        }

        purchase.cancelPurchase();
        walletService.deposit(WalletTxType.CANCEL_PRODUCT, memberId, purchase.getPurchaseAmount(), purchase.getId());
    }

    public PurchaseResponse getPurchase(Long memberId, Long purchaseId) {
        Purchase purchase = purchaseRepository.findByIdAndMemberId(purchaseId, memberId);
        if (purchase == null) {
            throw new IllegalArgumentException("올바른 주문 정보를 조회 할 수 없습니다.");
        }
        return PurchaseResponse.of(purchase);
    }
}
