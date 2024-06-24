package com.zune.wallet.domain.common.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateUtil {
    private static final String KOREAN_DATE_TIME_FORMAT = "yyyy년 MM월 dd일 HH시 mm분 ss초";

    public static String toKor(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            return localDateTime.format(DateTimeFormatter.ofPattern(KOREAN_DATE_TIME_FORMAT));
        }
        return StringUtils.EMPTY;
    }
}
