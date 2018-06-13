package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.utils.http;

/**
 * Represents different HTTP content types.
 *
 * @author robertnorthard
 */
public enum HttpContentTypes {
    JSON("application/json"), XML("application/xml");

    String post;

    HttpContentTypes(String post) {
        this.post = post;
    }

    @Override
    public String toString(){
        return this.post;
    }
}
