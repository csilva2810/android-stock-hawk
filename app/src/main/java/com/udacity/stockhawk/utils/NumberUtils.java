package com.udacity.stockhawk.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by carlinhos on 3/16/17.
 */

public class NumberUtils {

    private static DecimalFormat moneyFormat, moneyFormatWithPlus, percentFormat;

    static {
        moneyFormat = (DecimalFormat) NumberFormat.getCurrencyInstance();

        moneyFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance();
        moneyFormatWithPlus.setMaximumFractionDigits(2);
        moneyFormatWithPlus.setMinimumFractionDigits(2);
        moneyFormatWithPlus.setNegativePrefix("$-");
        moneyFormatWithPlus.setPositivePrefix("$");

        percentFormat = (DecimalFormat) NumberFormat.getCurrencyInstance();
        percentFormat.setMaximumFractionDigits(2);
        percentFormat.setMinimumFractionDigits(2);
        percentFormat.setNegativePrefix("%-");
        percentFormat.setPositivePrefix("%");
    }

    public static String formatMoney(float value) {
        return moneyFormat.format(value);
    }

    public static String formatMoneyWithPlus(float value) {
        return moneyFormatWithPlus.format(value);
    }

    public static String formatPercent(float value) {
        return percentFormat.format(value);
    }

}
