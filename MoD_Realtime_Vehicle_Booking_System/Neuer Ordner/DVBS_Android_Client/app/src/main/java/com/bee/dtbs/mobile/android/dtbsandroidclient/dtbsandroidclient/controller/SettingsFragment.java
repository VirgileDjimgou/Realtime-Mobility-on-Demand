package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * A controller to handle user preferences.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(com.bee.dtbs.mobile.android.dtbsandroidclient.R.xml.preferences);
    }
}
