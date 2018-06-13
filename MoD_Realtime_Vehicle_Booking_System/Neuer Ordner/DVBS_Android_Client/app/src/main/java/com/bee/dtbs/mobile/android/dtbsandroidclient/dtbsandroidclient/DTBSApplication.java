package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient;

import android.app.Application;

/**
 * Bootstraps application and maintains global state.
 *
 * @author djimgou patrick  virgile
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