package com.android.gudana.chatapp.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.android.gudana.R;
import com.android.gudana.apprtc.CallFragment;
import com.android.gudana.apprtc.CallIncomingActivity;
import com.android.gudana.apprtc.ConnectActivity;
import com.android.gudana.chatapp.activities.ChatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;

// add  bew import from MyFirebase notification  ...

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.gudana.R;
import com.android.gudana.hify.ui.activities.MainActivity;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import es.dmoral.toasty.Toasty;



public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService
{

    public static final String FCM_PARAM = "picture";
    private static final String CHANNEL_NAME = "FCM";
    private static final String CHANNEL_DESC = "Firebase Cloud Messaging";
    private int numMessages = 0;
    public static DatabaseReference userDB;
    private ValueEventListener mListener_Check_Caller;


    String notificationTitle = "";
    String notificationMessage = "";
    String notificationAction = "";
    String senderID = "";
    String notification_user_icon_url = "";
    String SenderName = "";



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);


        try{

            final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            if(Build.VERSION.SDK_INT >= 26)
            {
                // API 26+ is required to provide a channel Id

                NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.default_notification_channel_id), "My Notifications", NotificationManager.IMPORTANCE_HIGH);

                //notificationChannel.setDescription("Channel description");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Notification.DEFAULT_LIGHTS);
                //notificationChannel.setVibrationPattern(new long[]{0, 100, 100, 100, 100, 100});
                notificationChannel.enableVibration(true);

                notificationChannel.setDescription(CHANNEL_DESC);
                notificationChannel.setShowBadge(true);
                notificationChannel.canShowBadge();
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
                notificationManager.createNotificationChannel(notificationChannel);

                //
            }


            notificationTitle = remoteMessage.getData().get("title");
            notificationMessage = remoteMessage.getData().get("body");
            notificationAction = remoteMessage.getData().get("click_action");
            senderID = remoteMessage.getData().get("from_user_id");
            notification_user_icon_url = remoteMessage.getData().get("Sender_Icon_URL");
            SenderName = remoteMessage.getData().get("SenderName");
            //TimeMessage = remoteMessage.getData().get("TimeSend");

            if(notificationTitle != null){


                if(notificationTitle.equals("call")) // if Notification titel equal call    ...
                {
                    // start a call activity   ...

                    try{

                        userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(senderID);
                        // Set the  Driver Response to true ...
                        //HashMap map = new HashMap();
                        //map.put("Authentified" , "await");
                        //userDB.updateChildren(map);
                        userDB.keepSynced(true);
                        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    try{
                                        Map<String, Object> map_call = (Map<String, Object>) dataSnapshot.getValue();
                                        // test if the recors Phone already exist  ...if not than
                                        // than you are a new user   ...
                                        if(map_call.get("correspondant")!=null){
                                            // than this user is already registered ...
                                            String  Call_availibilty  =  map_call.get("correspondant").toString();
                                            if(Call_availibilty == ChatActivity.currentUserId) {

                                                // once after that we  can start  the  call oder
                                                // and of cour se remove the listener     ..  otherwise  we will startoo many listeners    ....
                                                // check if you are in  a conversation a the moment  ....
                                                if(CallFragment.running == false){ // you are available ...
                                                    try {

                                                        // always chehck is the notification ist not old  ...
                                                        Intent CallStart = new Intent(getApplication(), CallIncomingActivity.class);
                                                        CallStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        CallStart.putExtra("userid", senderID);
                                                        startActivity(CallStart);

                                                    }catch(Exception ex){
                                                        ex.printStackTrace();
                                                    }

                                                }else{ // if not you will get a notification


                                                    if(!ChatActivity.running || ChatActivity.running && !ChatActivity.otherUserId.equals(senderID))
                                                    {
                                                        // Creating the notification

                                                        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), getString(R.string.default_notification_channel_id))
                                                                .setContentTitle("call  from  other user")
                                                                .setContentText(notificationMessage)
                                                                .setSmallIcon(R.drawable.ic_send_message)
                                                                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                                                                .setAutoCancel(true)
                                                                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                                                        Intent intent = new Intent(notificationAction);
                                                        intent.putExtra("userid", senderID);

                                                        // Extract a unique notification from sender userId so we can have only 1 notification per user

                                                        int notificationId = Integer.parseInt(senderID.replaceAll("[^0-9]", ""));

                                                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), notificationId % 65535, intent, PendingIntent.FLAG_ONE_SHOT);

                                                        notification.setContentIntent(pendingIntent);

                                                        // Pushing notification to device

                                                        notificationManager.notify(notificationId % 65535, notification.build());
                                                    }

                                                }

                                            }else{

                                            }

                                        }


                                    }catch(Exception ex){
                                        Toasty.error(getApplicationContext(), ex.toString() , Toast.LENGTH_LONG).show();
                                        ex.printStackTrace();
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toasty.error(getApplicationContext(),databaseError.toString(), Toast.LENGTH_LONG).show();

                            }
                        });



                    }catch(Exception ex){

                        ex.printStackTrace();
                    }


                }

                if(notificationTitle.equals("Message"))
                {
                    // If it's a message notification
                    // Checking if ChatActivity is not open or if its, it should have a different userId from current

                    if(!ChatActivity.running || ChatActivity.running && !ChatActivity.otherUserId.equals(senderID))
                    {
                        Bitmap IconImage = null;
                        try {
                            // String picture = data.get(FCM_PARAM);
                            if (notification_user_icon_url != null && !"".equals(notification_user_icon_url)) {
                                URL url = new URL(notification_user_icon_url);
                                IconImage = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                //notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bigPicture).setSummaryText(notificationMessage+"summary "));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                                .setContentTitle("echojh")
                                .setContentText(notificationMessage)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(IconImage)
                                .setAutoCancel(true)
                                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                        Intent intent = new Intent(notificationAction);
                        intent.putExtra("userid", senderID);

                        // Extract a unique notification from sender userId so we can have only 1 notification per user

                        int notificationId = Integer.parseInt(senderID.replaceAll("[^0-9]", ""));

                        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId % 65535, intent, PendingIntent.FLAG_ONE_SHOT);

                        notification.setContentIntent(pendingIntent);

                        // Pushing notification to device

                        notificationManager.notify(notificationId % 65535, notification.build());

                    }
                }
                else if(notificationTitle.equals("Friend Request"))
                {
                    // If it's friend request notification

                    // Creating the notification

                    NotificationCompat.Builder notification = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                            .setContentTitle(SenderName)
                            .setContentText(notificationMessage)
                            .setSmallIcon(R.drawable.ic_person_add_white_24dp)
                            .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                            .setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                    Intent intent = new Intent(notificationAction);
                    intent.putExtra("userid", senderID);

                    // Extract a unique notification from sender userId so we can have only 1 notification per user


                    int notificationId = Integer.parseInt(senderID.replaceAll("[^0-9]", ""));

                    // Adding +1 to notification Id se we can have a Friend Request and a Message Notification at the same time
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId + 1 % 65535, intent, PendingIntent.FLAG_ONE_SHOT);

                    notification.setContentIntent(pendingIntent);

                    // Pushing notification to device

                    notificationManager.notify(notificationId + 1 % 65535, notification.build());
                }
                else if(notificationTitle.equals("friend request declined"))
                {
                    // If it's friend request notification

                    // Creating the notification

                    NotificationCompat.Builder notification = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                            .setContentTitle(SenderName)
                            .setContentText(notificationMessage)
                            .setSmallIcon(R.drawable.ic_person_add_white_24dp)
                            .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                            .setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                    Intent intent = new Intent(notificationAction);
                    intent.putExtra("userid", senderID);

                    // Extract a unique notification from sender userId so we can have only 1 notification per user


                    int notificationId = Integer.parseInt(senderID.replaceAll("[^0-9]", ""));

                    // Adding +1 to notification Id se we can have a Friend Request and a Message Notification at the same time

                    PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId + 1 % 65535, intent, PendingIntent.FLAG_ONE_SHOT);

                    notification.setContentIntent(pendingIntent);

                    // Pushing notification to device

                    notificationManager.notify(notificationId + 1 % 65535, notification.build());
                }
                else if(notificationTitle.equals("new friend"))
                {
                    // If it's a new friend

                    // Creating the notification

                    NotificationCompat.Builder notification = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                            .setContentTitle(SenderName)
                            .setContentText(notificationMessage)
                            .setSmallIcon(R.drawable.ic_person_add_white_24dp)
                            .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                            .setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                    Intent intent = new Intent(notificationAction);
                    intent.putExtra("userid", senderID);

                    // Extract a unique notification from sender userId so we can have only 1 notification per user

                    int notificationId = Integer.parseInt(senderID.replaceAll("[^0-9]", ""));

                    // Adding +2 to notification Id se we can have a all notifications at the same time

                    PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId + 2 % 65535, intent, PendingIntent.FLAG_ONE_SHOT);

                    notification.setContentIntent(pendingIntent);

                    // Pushing notification to device

                    notificationManager.notify(notificationId + 2 % 65535, notification.build());
                }

            }else {

                Map<String, String> data = remoteMessage.getData();
                // to send a notification for  your phone  ...s
                // sendNotification(remoteMessage, data);

            }

        }catch(Exception ex){
            ex.printStackTrace();
        }

    }




}
