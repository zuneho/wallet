package com.zune.wallet.api.common.converter;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.zune.wallet.domain.common.util.DateUtil;

import java.time.LocalDateTime;

public class LocalDateTimeToKorConverter extends StdConverter<LocalDateTime, String> {
    @Override
    public String convert(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return DateUtil.toKor(value);
    }
}
