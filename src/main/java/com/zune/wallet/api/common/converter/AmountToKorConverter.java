package com.zune.wallet.api.common.converter;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.zune.wallet.domain.common.util.NumberUtil;

import java.util.Locale;

public class AmountToKorConverter extends StdConverter<Double, String> {

    @Override
    public String convert(Double value) {
        if (value == null) {
            return null;
        }
        return NumberUtil.doubleToFormatedString(value, Locale.KOREA);
    }
}
