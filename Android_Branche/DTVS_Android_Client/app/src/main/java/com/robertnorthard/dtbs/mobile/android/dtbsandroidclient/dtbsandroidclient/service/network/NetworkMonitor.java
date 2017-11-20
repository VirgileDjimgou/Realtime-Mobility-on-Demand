package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.config.DtbsPreferences;

/**
 *
 * Response to network changes.
 *
 * Inspired from: http://stackoverflow.com/questions/10733121/broadcastreceiver-when-wifi-or-3g-network-state-changed
 * @author robertnorthard
 */
public class NetworkMonitor extends BroadcastReceiver{

    private static final String TAG = NetworkMonitor.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        this.onNetworkChange(isConnected(context));
        Intent i = new Intent(DtbsPreferences.NETWORK_STATE_EVENT);
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }

    public boolean isConnected(Context context){
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return wifi != null && wifi.isConnectedOrConnecting() ||
                mobile != null && mobile.isConnectedOrConnecting();
    }

    public void onNetworkChange(boolean isConnected){
        if (isConnected) {
            Log.e("Network", "Network Connected.");
        } else {
            Log.e("Network", "Network disconnected.");
        }
    }
}

