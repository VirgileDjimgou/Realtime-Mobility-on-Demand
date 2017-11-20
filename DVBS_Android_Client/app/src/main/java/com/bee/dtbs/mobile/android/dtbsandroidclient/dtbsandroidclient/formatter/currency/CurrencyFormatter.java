package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.formatter.currency;

/**
 * Format provided currency;
 *
 * @author djimgou patrick virgile
 */
public interface CurrencyFormatter {

    /**
     * Return currency.
     *
     * @param currency to convert
     * @return currency formatted as currency.
     */
    String format(double currency);

}
