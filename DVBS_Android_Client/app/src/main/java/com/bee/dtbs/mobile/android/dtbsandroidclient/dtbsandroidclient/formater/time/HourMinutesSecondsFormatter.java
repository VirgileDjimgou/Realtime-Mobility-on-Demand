package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.formater.time;

/**
 * A hour minutes seconds time formatter class that realises the time formatter interface.
 *
 * @author djimgou patrick virgile
 */

/**
 *
 * @author djimgou patrick virgile
 */
public class HourMinutesSecondsFormatter implements TimeFormatter {

    /**
     * Return time a string.
     *
     * @param time time to format.
     * @return time formatted as a string.
     * @throws IllegalArgumentException if time less than 0.
     */
    @Override
    public String format(int time) {

        if(time < 0){
            throw new IllegalArgumentException("Time must be more than 0");
        }

        int hours = time / 3600;
        int minutes = (time % 3600) / 60;
        int seconds = (time % 3600) % 60;

        return this.padIndex(hours) + ":"
                + this.padIndex(minutes)
                + ":" + this.padIndex(seconds);
    }

    /**
     * Pad integer by one digit.
     *
     * @param value value to pad.
     * @return a string representation of a digit passed by one zero.
     */
    private String padIndex(int value){
        return String.format("%02d",value);
    }
}
