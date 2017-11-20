package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.utils.http;

import android.os.Build;

import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Tests for authentication utils.
 *
 * @author robertnorthard
 */
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricGradleTestRunner.class)
public class AuthenticationUtilsTest {

    /**
     * Test method base64Encode with invalid parameter.
     */
    @Test
    public void testBase64EncodeValidParameter() {
        String value = "john.doe";
        String expected = "am9obi5kb2U=";
        String actual = AuthenticationUtils.base64Encode(value);
        assertEquals(expected, actual);
    }

    /**
     * Test method base64Encode with invalid parameter.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBase64EncodeInvalidParameter() {
        String actual = AuthenticationUtils.base64Encode(null);
    }

    /**
     * Test method basicAuthEncode with valid parameters.
     */
    @Test
    public void testBasicAuthEncodeValidParameter() {
        String username = "john.doe";
        String password = "password";
        String expected = "Basic am9obi5kb2U6cGFzc3dvcmQ=";
        String actual = AuthenticationUtils.basicAuthEncode(username, password);
        assertEquals(expected, actual);
    }

    /**
     * Test method basicAuthEncode with invalid parameters.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBasicAuthEncodeInvalidParameters() {
        AuthenticationUtils.basicAuthEncode(null, null);
    }
}