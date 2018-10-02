package com.android.gudana.fcm;

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

import com.android.gudana.R;
import com.android.gudana.apprtc.CallFragment;
import com.android.gudana.apprtc.CallIncomingActivity;
import com.android.gudana.chatapp.activities.ChatActivity;
import com.android.gudana.hify.ui.activities.MainActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

import static com.bumptech.glide.request.RequestOptions.centerCropTransform;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
	public static final String FCM_PARAM = "picture";
	private int numMessages = 0;
	private  DatabaseReference CallRoom;
	private  ValueEventListener EventListener;


	public static final String FCM_ICON_SENDER = "picture";
	public static final String FCM_name_Sender = "SenderName";
	public static final String FCM_message_sender = "body";
	public static final String fcm_msg = "msg";
	private static final String CHANNEL_NAME = "FCM";
	private static final String CHANNEL_DESC = "Firebase Cloud Messaging";

    private String senderName = "";
	private String  MessageSended = "";
	private String msg = "";
	private String TimeSend = "";
	private String notificationTitle  = "";
	private String senderID = "";
    private String room_call = "FCM";



    @Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);
		RemoteMessage.Notification notification = remoteMessage.getNotification();
		Map<String, String> data = remoteMessage.getData();
		Log.d("FROM", remoteMessage.getFrom());

		// pparse notification  ....

		try{
			notificationTitle = data.get("title");
			senderID = data.get("SenderID");
			String picture_url = data.get(FCM_ICON_SENDER);
			senderName = data.get(FCM_name_Sender);
			MessageSended = data.get(FCM_message_sender);
			msg = data.get(fcm_msg);
			TimeSend = data.get("TimeSend");
            room_call = data.get("Room_id");

			if(notificationTitle != null){


				if(notificationTitle.equals("call")) // if Notification titel equal call    ...
				{
					// start a call activity   ...
					ManageCall(data);

				}

				if(notificationTitle.equals("Message"))
				{
					// If it's a message notification
					// Checking if ChatActivity is not open or if its, it should have a different userId from current

					if(!ChatActivity.running || ChatActivity.running && !ChatActivity.otherUserId.equals(senderID))
					{
							sendNotification( data);

					}
				}
				else if(notificationTitle.equals("Friend Request"))
				{
					// If it's friend request notification
					sendNotification( data);

				}
				else if(notificationTitle.equals("friend request declined"))
				{

					sendNotification( data);
				}
				else if(notificationTitle.equals("new friend"))
				{
					// If it's a new friend
					sendNotification( data);
				}

			}else {

				// to send a notification for  your phone  ...s
				// sendNotification(remoteMessage, data);
				Toasty.info(this, "Gudana Notification  activated ...", Toast.LENGTH_SHORT).show();

			}

		}catch (Exception ex){
			ex.printStackTrace();
		}

	}

	private void sendNotification(Map<String, String> data) {
		Bundle bundle = new Bundle();
		bundle.putString(FCM_ICON_SENDER, data.get(FCM_ICON_SENDER));

		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtras(bundle);

		// get image  ...
		Bitmap bigPicture = null;
		try {
			String picture_url = data.get(FCM_ICON_SENDER);
			senderName = data.get(FCM_name_Sender);
			MessageSended = data.get(FCM_message_sender);
			msg = data.get(fcm_msg);
			TimeSend = data.get("TimeSend");


			if (picture_url != null && !"".equals(picture_url)) {
				// download image with glide  ...
				RequestOptions cropOptions = new RequestOptions();
				bigPicture = Glide.with(this).asBitmap().load(picture_url).apply(RequestOptions.circleCropTransform()).into(100,100).get();

			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
				.setContentTitle(senderName)
				.setContentText(MessageSended)
				.setAutoCancel(true)
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				// .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.win))
				.setContentIntent(pendingIntent)
				.setContentInfo("GuDana Cloud Notification")
				//.setLargeIcon(getRoundedRectBitmap(bigPicture,12))
				.setLargeIcon(bigPicture)
				.setColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setLights(Color.RED, 1000, 300)
				.setDefaults(Notification.DEFAULT_VIBRATE)
				.setNumber(++numMessages)
				.setSmallIcon(R.mipmap.ic_launcher);



		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(
					getString(R.string.notification_channel_id), CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
			);
			channel.setDescription(CHANNEL_DESC);
			channel.setShowBadge(true);
			channel.canShowBadge();
			channel.enableLights(true);
			channel.setLightColor(Color.RED);
			channel.enableVibration(true);
			channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});

			assert notificationManager != null;
			notificationManager.createNotificationChannel(channel);
		}

		assert notificationManager != null;
		notificationManager.notify(0, notificationBuilder.build());
	}


	private void ManageCall(final Map<String, String> data){

		try{

			CallRoom = FirebaseDatabase.getInstance().getReference().child("Call_room").child(room_call);
			CallRoom.keepSynced(true); // because  of keepsychronised for  offline  usage  ...to avoid the lecture of cache data
			EventListener = CallRoom.addValueEventListener (new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					 DataSnapshot dataSnapshot_local = dataSnapshot;
					 if(dataSnapshot_local.exists()){
						try{
							Map<String, Object> map_call = (Map<String, Object>) dataSnapshot.getValue();
							// test if the recors Phone already exist  ...if not than
							// than you are a new user   ...
							if(map_call.get("id_receiver")!=null){
								// than this user is already registered ...
								String  Call_availibilty  =  map_call.get("id_receiver").toString();
								if(Call_availibilty.equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

									// once after that we  can start  the  call oder
									// and of cour se remove the listener     ..  otherwise  we will startoo many listeners    ....
									// check if you are in  a conversation a the moment  ....
									if(map_call.get("room_status")!=null){

										boolean  room_status  = (boolean) map_call.get("room_status");
										if(room_status == true) { // that meams  this room ist  Op

											if(CallFragment.running == false){ // you are available ...
												try {

													// remove listeners ...
													CallRoom.child("Call_room").child(room_call).removeEventListener(EventListener);

													// always chehck is the notification ist not old  ...
													Intent CallStart = new Intent(getApplication(), CallIncomingActivity.class);
													CallStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
													CallStart.putExtra("userid", senderID);
													CallStart.putExtra("room_id_call", room_call);
													startActivity(CallStart);

												}catch(Exception ex){
													ex.printStackTrace();
												}

											}else{ // if not you will get a notification
												EventListener = null;
												CallRoom.child("Call_room").child(room_call).removeEventListener(EventListener);
												sendNotification(data);
												// or  for missed call  in history   ...
												// send unavailibilty notfication  to another user  ...s
												ChatActivity.missedCallNotification(getApplication(),
														room_call,
														"video",
														senderID ,
														"Firebase messaging  notification" ,
														"missed call");
											}

										}else{
											EventListener = null;
											CallRoom.child("Call_room").child(room_call).removeEventListener(EventListener);
											sendNotification(data);
											// or  for missed call  in history   ...
											// send unavailibilty notfication  to another user  ...s

											ChatActivity.missedCallNotification(getApplication(),
													room_call,
											"video",
													senderID ,
													"Firebase messaging  notification" ,
													"missed call");

										}

									}



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

}

