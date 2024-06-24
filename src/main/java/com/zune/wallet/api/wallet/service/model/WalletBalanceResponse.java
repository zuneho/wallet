package com.zune.wallet.api.wallet.service.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zune.wallet.api.common.converter.AmountToKorConverter;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WalletBalanceResponse {
    private final Long memberId;
    @JsonSerialize(converter = AmountToKorConverter.class)
    private final double balance;

    public static WalletBalanceResponse build(Long memberId, double balance) {
        return WalletBalanceResponse.builder()
                .memberId(memberId)
                .balance(balance)
                .build();
    }
}
