package com.zune.wallet.api.wallet.service.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zune.wallet.api.common.converter.AmountToKorConverter;
import com.zune.wallet.api.common.converter.LocalDateTimeToKorConverter;
import com.zune.wallet.domain.persistance.wallet.WalletTransaction;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WalletHistoryResponse {
    private final Long id;
    private final Long memberId;
    private final String type;
    private final Long purchaseId;
    @JsonSerialize(converter = AmountToKorConverter.class)
    private final double amount;
    @JsonSerialize(converter = LocalDateTimeToKorConverter.class)
    private final LocalDateTime createdAt;

    public static WalletHistoryResponse of(WalletTransaction tx) {
        return WalletHistoryResponse.builder()
                .id(tx.getId())
                .memberId(tx.getMemberId())
                .type(tx.getType().getDescription())
                .purchaseId(tx.getPurchaseId())
                .amount(tx.getAmount())
                .createdAt(tx.getCreatedAt())
                .build();
    }

}
