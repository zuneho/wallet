package com.zune.wallet.domain.persistance.wallet.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WalletTxType {
    DEPOSIT("입금", true, false),
    WITHDRAW("출금", false, true),
    PURCHASE_PRODUCT("상품 구매", false, true),
    CANCEL_PRODUCT("상품 구매 취소", true, false);

    private final String description;
    private final boolean depositType;
    private final boolean withdrawType;
}
