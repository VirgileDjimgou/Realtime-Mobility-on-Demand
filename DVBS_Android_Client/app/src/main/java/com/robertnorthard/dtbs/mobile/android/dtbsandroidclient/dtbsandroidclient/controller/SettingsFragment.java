package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.R;

/**
 * A controller to handle user preferences.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
