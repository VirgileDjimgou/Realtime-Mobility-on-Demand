package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.utils.http;

import android.util.Base64;

/**
 * Authentication utility class.
 *
 * @author robertnorthard
 */

public class AuthenticationUtils {

    private AuthenticationUtils(){
        // private as utility class.
    }

    /**
     * Return a base64 encoded string representation of the provided textual value.
     *
     * @param text text to encode.
     * @return a base64 encoded string of the provided text.
     * @throws IllegalArgumentException is plain is null.
     */
    public static String base64Encode(String text){

        if(text == null){
            throw new IllegalArgumentException("Plain cannot be null");
        }

        return new String(Base64.encode(text.getBytes(), Base64.NO_WRAP));
    }

    /**
     * Return a http basic authorization header value for the
     * provided username and password.
     *
     * @param username username to encode.
     * @param password password to encode.
     * @return a basic authorization HTTP header value.
     * @throws IllegalArgumentException if username of password is null.
     */
    public static String basicAuthEncode(String username, String password){

        if(username == null || password == null){
            throw new IllegalArgumentException("Username and password cannot be null");
        }

        return  "Basic "
                + AuthenticationUtils.base64Encode(username + ":" + password);
    }
}
