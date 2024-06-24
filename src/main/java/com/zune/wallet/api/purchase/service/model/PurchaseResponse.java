package com.zune.wallet.api.purchase.service.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zune.wallet.api.common.converter.AmountToKorConverter;
import com.zune.wallet.api.common.converter.LocalDateTimeToKorConverter;
import com.zune.wallet.domain.persistance.purchase.Purchase;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PurchaseResponse {
    private final Long id;
    private final Long memberId;
    private final Long productId;
    private final String productName;
    @JsonSerialize(converter = AmountToKorConverter.class)
    private final double purchaseAmount;
    @JsonSerialize(converter = LocalDateTimeToKorConverter.class)
    private final LocalDateTime purchaseAt;
    @JsonSerialize(converter = LocalDateTimeToKorConverter.class)
    private final LocalDateTime canceledAt;
    private final boolean canceled;

    public static PurchaseResponse of(Purchase purchase) {
        return PurchaseResponse.builder()
                .id(purchase.getId())
                .memberId(purchase.getMemberId())
                .productId(purchase.getProductId())
                .productName(purchase.getProduct().getName())
                .purchaseAmount(purchase.getPurchaseAmount())
                .purchaseAt(purchase.getPurchaseAt())
                .canceledAt(purchase.getCanceledAt())
                .canceled(purchase.isCanceled())
                .build();
    }

}
