package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.formater.time;

import android.os.Build;

import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Tests for HourMinutesSecondsFormatterTest.
 *
 * @author robertnorthard
 */
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricGradleTestRunner.class)
public class HourMinutesSecondsFormatterTest {

    private TimeFormatter formatter;

    /**
     * Constructor to initialise formatter.
     */
    public HourMinutesSecondsFormatterTest(){
        this.formatter = new HourMinutesSecondsFormatter();
    }

    /**
     * Test method format of HourMinutesSecond formatter.
     */
    @Test
    public void testFormat0() {
        int time = 000000;
        String expected = "00:00:00";
        String actual = this.formatter.format(time);
        assertEquals(expected,actual);
    }

    /**
     * Test method format of HourMinutesSecond formatter.
     */
    @Test
    public void testFormatMaxInt() {
        int time = Integer.MAX_VALUE;
        String expected = "596523:14:07";
        String actual = this.formatter.format(time);
        assertEquals(expected,actual);
    }

    /**
     * Test method format of HourMinutesSecond formatter.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFormatMinInt() {
        int time = Integer.MIN_VALUE;
        String expected = "00:00:00";
        String actual = this.formatter.format(time);
        assertEquals(expected,actual);
    }

    /**
     * Test method format of HourMinutesSecond formatter.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFormatEquivalenceNegatives1() {
        int time = -1;
        String expected = "00:00:00";
        String actual = this.formatter.format(time);
        assertEquals(expected,actual);
    }

    /**
     * Test method format of HourMinutesSecond formatter.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFormatEquivalenceNegatives2() {
        int time = -2;
        String expected = "00:00:00";
        String actual = this.formatter.format(time);
        assertEquals(expected,actual);
    }

    /**
     * Test method format of HourMinutesSecond formatter.
     */
    @Test
    public void testFormatEquivalenceSecond1() {
        int time = 1;
        String expected = "00:00:01";
        String actual = this.formatter.format(time);
        assertEquals(expected,actual);
    }

    /**
     * Test method format of HourMinutesSecond formatter.
     */
    @Test
    public void testFormatEquivelenceSecond2() {
        int time = 2;
        String expected = "00:00:02";
        String actual = this.formatter.format(time);
        assertEquals(expected,actual);
    }


    /**
     * Test method format of HourMinutesSecond formatter.
     */
    @Test
    public void testFormatEquivalenceHour1() {
        int time = 3600;
        String expected = "01:00:00";
        String actual = this.formatter.format(time);
        assertEquals(expected,actual);
    }

    /**
     * Test method format of HourMinutesSecond formatter.
     */
    @Test
    public void testFormatEquivalenceHour2() {
        int time = 7200;
        String expected = "02:00:00";
        String actual = this.formatter.format(time);
        assertEquals(expected,actual);
    }


    /**
     * Test method format of HourMinutesSecond formatter.
     */
    @Test
    public void testFormatEquivalenceMinute1() {
        int time = 60;
        String expected = "00:01:00";
        String actual = this.formatter.format(time);
        assertEquals(expected,actual);
    }

    /**
     * Test method format of HourMinutesSecond formatter.
     */
    @Test
    public void testFormatEquivalenceMinute2() {
        int time = 120;
        String expected = "00:02:00";
        String actual = this.formatter.format(time);
        assertEquals(expected,actual);
    }

    /**
     * Test method format of HourMinutesSecond formatter.
     */
    @Test
    public void testFormatEquivalenceMinute3() {
        int time = 240;
        String expected = "00:04:00";
        String actual = this.formatter.format(time);
        assertEquals(expected,actual);
    }
}