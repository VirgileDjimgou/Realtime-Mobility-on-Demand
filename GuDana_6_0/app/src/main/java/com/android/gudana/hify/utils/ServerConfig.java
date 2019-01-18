package com.android.gudana.hify.utils;


import android.widget.TextView;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;


public class ServerConfig  {

    public  static final String TAG = "MainActivity";

    // Remote Config keys
    public static final String LOADING_PHRASE_CONFIG_KEY = "loading_phrase";
    public static final String WELCOME_MESSAGE_KEY = "welcome_message";
    public static final String WELCOME_MESSAGE_CAPS_KEY = "welcome_message_caps";

    public static FirebaseRemoteConfig mFirebaseRemoteConfig;
    public static TextView mWelcomeTextView;


    // [END display_welcome_message]
}