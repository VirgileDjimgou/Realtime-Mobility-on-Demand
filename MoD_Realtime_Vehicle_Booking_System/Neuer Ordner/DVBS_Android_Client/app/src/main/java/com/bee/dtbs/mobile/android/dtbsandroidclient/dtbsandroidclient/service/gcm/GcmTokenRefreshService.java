package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Implements InstanceIDListenerService to deal with token refreshes.
 * Gcm tokens may be refreshed due to expiry or due to the code being compromised.
 *
 * @author djimgou patrick  virgile
 */
public class GcmTokenRefreshService extends InstanceIDListenerService {

    /**
     * Invoked on Gcm token expiry.
     */
    @Override
    public void onTokenRefresh(){
        // start Gcm registration service
        Intent intent = new Intent(this, GcmRegistrationIntentService.class);
        startService(intent);
    }
}
