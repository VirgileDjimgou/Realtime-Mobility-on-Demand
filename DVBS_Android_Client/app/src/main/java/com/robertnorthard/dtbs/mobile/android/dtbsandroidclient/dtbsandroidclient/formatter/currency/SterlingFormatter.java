package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.formatter.currency;

import android.annotation.TargetApi;
import android.os.Build;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Display currency as in sterling.
 *
 * @author robertnorthard
 */
public class SterlingFormatter implements CurrencyFormatter {

    /**
     * Inspired from https://docs.oracle.com/javase/tutorial/i18n/format/numberFormat.html
     * @param currency to convert
     * @return currency formatted as a string.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public String format(double currency) {

        NumberFormat currencyFormatter =
                NumberFormat.getCurrencyInstance(Locale.getDefault());

        return currencyFormatter.format(currency);

    }
}
