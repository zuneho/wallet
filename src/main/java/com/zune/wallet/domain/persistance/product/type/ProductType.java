package com.zune.wallet.domain.persistance.product.type;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductType {
    PHYSICAL_PRODUCT("실물 상품"),
    COUPON("쿠폰");

    private final String description;
}
