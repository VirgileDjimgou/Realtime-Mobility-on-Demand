package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.formater.time;

/**
 * Format provided time.
 *
 * @author robertnorthard
 */
public interface TimeFormatter {

    /**
     * Return time a string.
     *
     * @param time time to format.
     * @return time formatted as a string.
     */
    public String format(int time);
}
