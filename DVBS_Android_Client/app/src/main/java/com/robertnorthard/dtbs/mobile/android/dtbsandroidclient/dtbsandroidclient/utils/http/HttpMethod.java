package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.utils.http;

/**
 * Represents different HTTP methods.
 *
 * @author robertnorthard
 */
public enum HttpMethod {
    POST("POST"), GET("GET"), DELETE("DELETE"), PUT("PUT"), OPTIONS("OPTIONS");

    String post;

    HttpMethod(String post) {
        this.post = post;
    }

    @Override
    public String toString(){
        return this.post;
    }
}
