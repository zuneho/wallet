package com.zune.wallet.api.purchase.service.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PurchaseRequest {
    @NotNull(message = "상품 정보를 확인할 수 없습니다.")
    private final Long productId;
    @NotNull(message = "상품 가격을 확인할 수 없습니다.")
    private final Double price;

    @Builder
    public PurchaseRequest(Long productId, Double price) {
        this.productId = productId;
        this.price = price;
    }
}
