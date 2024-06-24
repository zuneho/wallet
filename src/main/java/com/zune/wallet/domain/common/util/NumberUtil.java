package com.zune.wallet.domain.common.util;

import lombok.experimental.UtilityClass;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@UtilityClass
public class NumberUtil {

    private final Locale DEFAULT_LOCALE = Locale.KOREA;
    private final DecimalFormat korFormatter = new DecimalFormat("#,### 원");
    private final DecimalFormat usFormatter = new DecimalFormat("#,###.00 $", DecimalFormatSymbols.getInstance(Locale.US));

    public static String doubleToFormatedString(double value) {
        return doubleToFormatedString(value, DEFAULT_LOCALE);
    }

    public static String doubleToFormatedString(double value, Locale locale) {
        if (Locale.KOREA == locale || Locale.KOREAN == locale) {
            return korFormatter.format(value);
        }
        return usFormatter.format(value);
    }
}
