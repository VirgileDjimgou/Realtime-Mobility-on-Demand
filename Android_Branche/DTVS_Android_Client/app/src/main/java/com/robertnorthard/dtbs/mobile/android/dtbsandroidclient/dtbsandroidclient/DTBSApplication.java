package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient;

import android.app.Application;

/**
 * Bootstraps application and maintains global state.
 *
 * @author robertnorthard
 */
public class DTBSApplication extends Application {
    private static DTBSApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static DTBSApplication getInstance() {
        return instance;
    }
}