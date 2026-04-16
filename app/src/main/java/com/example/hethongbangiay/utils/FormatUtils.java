package com.example.hethongbangiay.utils;

import java.text.NumberFormat;
import java.util.Locale;

public final class FormatUtils {

    private static final Locale LOCALE_VIETNAM = new Locale("vi", "VN");

    private FormatUtils() {
    }

    public static String formatCurrency(double amount) {
        return newNumberFormat().format(amount) + " đ";
    }

    public static String formatCount(int count) {
        return newNumberFormat().format(count);
    }

    private static NumberFormat newNumberFormat() {
        return NumberFormat.getInstance(LOCALE_VIETNAM);
    }
}
