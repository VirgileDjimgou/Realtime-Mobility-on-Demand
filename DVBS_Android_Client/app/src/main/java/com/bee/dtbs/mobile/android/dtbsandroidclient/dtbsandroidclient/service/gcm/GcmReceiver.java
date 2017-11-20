package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.gcm;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.config.DtbsPreferences;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Handles incoming Google cloud messenger messages.
 *
 * @author djimgou patrick  virgile
 */
public class GcmReceiver extends GcmListenerService {

    // TAG used for logging.
    private static final String TAG = GcmReceiver.class.getName();

    /**
     * Called when a message is received by Google cloud messenger for this app.
     *
     * @param from the message sender id.
     * @param data message data as key-value string pairs.
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {

        // extract message and event type.
        String eventType = data.getString("event");
        String message = data.getString("status");

        this.broadcastEvent(eventType, data);

        // send broadcast notifications while app is in background if property enabled.
        if(PreferenceManager
                .getDefaultSharedPreferences(
                getBaseContext())
                .getBoolean(DtbsPreferences
                        .NOTIFICATIONS_ENABLED_KEY,
                        true)){

            this.sendNotification(getString(com.bee.dtbs.mobile.android.dtbsandroidclient.R.string.app_name),message);
        }

    }

    /**
     * Broadcast event via custom event intent and local broadcast manager.
     */
    private void broadcastEvent(String eventType, Bundle extras){
        Log.d(TAG, "Broadcasting message.");

        Intent intent = new Intent(eventType);
        intent.putExtras(extras);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Send notification.
     *
     * @param title title of notification.
     * @param message message of notification.
     */
    private void sendNotification(String title, String message){

        Log.d(TAG, "Broadcasting message - " + message);

        Context context = getBaseContext();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(com.bee.dtbs.mobile.android.dtbsandroidclient.R.mipmap.ic_launcher).setContentTitle(title)
                .setContentText(message);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(12345, mBuilder.build());
    }
}
