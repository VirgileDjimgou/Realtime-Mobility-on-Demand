package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.formater.time;

/**
 * Format provided time.
 *
 * @author djimgou patrick virgile
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
