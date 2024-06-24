package com.zune.wallet.api.wallet.service.model;

import com.zune.wallet.domain.persistance.wallet.type.WalletTxType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WalletTxRequest {
    @NotNull(message = "금액을 입력해주세요")
    public final Double amount;

    @NotNull(message = "올바른 요청이 아닙니다.")
    public final WalletTxType eventType;

    @Builder
    public WalletTxRequest(Double amount, WalletTxType eventType) {
        this.amount = amount;
        this.eventType = eventType;
    }

    public boolean isDeposit() {
        return this.eventType != null && WalletTxType.DEPOSIT == this.eventType;
    }

    public boolean isWithdraw() {
        return this.eventType != null && WalletTxType.WITHDRAW == this.eventType;
    }
}
