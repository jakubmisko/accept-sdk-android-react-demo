/**
 * Copyright (c) 2015 Wirecard. All rights reserved.
 * <p>
 * Accept SDK for Android
 */
package com.wirecard.accept.help;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * work with currencies
 */
public class CurrencyUtils {
    /**
     *
     * format amount based on users locale
     * @param units unit price of payment
     * @param currency of payment
     * @param locale users locale
     * @return formated string
     */
    public static String format(long units, Currency currency, Locale locale) {
        final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        if (currency != null) {
            numberFormat.setCurrency(currency);
            return numberFormat.format(new BigDecimal(units).scaleByPowerOfTen(-currency.getDefaultFractionDigits()).doubleValue());
        } else {
            return numberFormat.format(units);
        }
    }

    /**
     * format amount based on users locale
     * @param amount of payment
     * @param currency of payment
     * @param locale users locale
     * @return formated string
     */
    public static String format(String amount, Currency currency, Locale locale) {
        final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        if (currency != null) {
            numberFormat.setCurrency(currency);
        }
        BigDecimal value = new BigDecimal(amount);
        return numberFormat.format(value.doubleValue());
    }
}