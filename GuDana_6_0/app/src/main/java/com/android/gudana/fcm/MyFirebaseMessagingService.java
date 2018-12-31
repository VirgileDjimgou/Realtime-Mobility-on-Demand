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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
//import android.widget.Toast;

import com.android.gudana.R;
import com.android.gudana.apprtc.CallFragment;
import com.android.gudana.apprtc.CallIncomingActivity;
import com.android.gudana.chat.activities.ChatActivity;
import com.android.gudana.hify.ui.activities.MainActivity_GuDDana;
import com.android.gudana.hify.utils.Config;
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

import java.util.Map;
import java.util.concurrent.ExecutionException;

//import es.dmoral.toasty.Toasty;

import static com.android.gudana.service.SensorService.notificationManager;
import static com.android.gudana.service.SensorService.notificationBuilder;
import static com.android.gudana.service.SensorService.*;


//import com.android.gudana.tindroid.MessageActivity_fire_tinode;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
	// added

	//private int currentNotificationID = 0;
	// private NotificationManager notificationManager;
	// private NotificationCompat.Builder notificationBuilder;
	//private String notificationText;
	//private Bitmap icon;
	//private int combinedNotificationCounter;



	public static final String FCM_PARAM = "picture";
	private int numMessages = 0;
	private  DatabaseReference CallRoom;
	private  ValueEventListener EventListener;


	public static final String FCM_ICON_SENDER = "picture";
	public static final String FCM_name_Sender = "SenderName";
	public static final String FCM_message_sender = "body";
	public static final String fcm_msg = "msg";
	private static final String CHANNEL_NAME = "FCM";
	private static final String CHANNEL_DESC = "Xshaka Cloud Messaging";

	private String senderName = "";
	private String  MessageSended = "";
	private String msg = "";
	private String TimeSend = "";
	private String notificationTitle  = "";
	private String senderID = "";
	private String room_call = "FCM";
	private Boolean NotifierExist = false;


	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("create", "hcreated");
	}

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);


		if(NotifierExist != true) {
			NotifierExist = true;
			// to avoid  more than one time intialisation  of notification manager    ... on instance of notification
			notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		}

		if(icon == null){
			icon = BitmapFactory.decodeResource(this.getResources(),
					R.mipmap.ic_launcher);
		}


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
					// Checking if CreateGroupChatActivity is not open or if its, it should have a different userId from current

					/*
					if(Config.Chat_Activity_running!=false )
					{
						//sendNotification( data);
						sendNotification_unreadMessage(data);
						//setDataForSimpleNotification();
						//setDataForNotificationWithActionButton();

					}
					*/

					if(!Config.Chat_Activity_running || Config.Chat_Activity_running&& !Config.Chat_Activity_otherUserId.equals(senderID))
					{
						//sendNotification( data);
						sendNotification_unreadMessage(data);
						//setDataForSimpleNotification();
						//setDataForNotificationWithActionButton();

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
				//Toasty.info(this, "Xshaka Notification  activated ...", Toast.LENGTH_SHORT).show();

			}

		}catch (Exception ex){
			ex.printStackTrace();
		}

	}


	public void sendNotification(Map<String, String> data) {
		Bundle bundle = new Bundle();
		bundle.putString(FCM_ICON_SENDER, data.get(FCM_ICON_SENDER));

		Intent intent = new Intent(this, MainActivity_GuDDana.class);
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
				bigPicture = Glide.with(MyFirebaseMessagingService.this)
						.asBitmap()
						.load(picture_url)
						.apply(RequestOptions.circleCropTransform())
						.into(100,100).get();


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
				.setContentInfo("GuDDana Cloud Notification")
				//.setLargeIcon(getRoundedRectBitmap(bigPicture,12))
				.setLargeIcon(bigPicture)
				.setColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setLights(Color.RED, 1000, 300)
				.setDefaults(Notification.DEFAULT_VIBRATE)
				.setNumber(++numMessages)
				.setSmallIcon(R.drawable.xshaka_icon);



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



	private void sendNotification(Notification notification) {
		Intent notificationIntent = new Intent(this, fcm_MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		notificationBuilder.setContentIntent(contentIntent);
		notification = notificationBuilder.build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;

		currentNotificationID++;
		int notificationId = currentNotificationID;
		if (notificationId == Integer.MAX_VALUE - 1)
			notificationId = 0;


//        if (notificationId >= 4)
//            notificationId = 4;


		notificationManager.notify(notificationId, notification);
		// notificationManager.cancel(currentNotificationID-1);
	}



	public void sendNotification_unreadMessage(Map<String, String> data){

		Intent sendMessageIntent= null;
		// get image  ...
		Bitmap bigPicture = null;
		try {
			String picture_url = data.get(FCM_ICON_SENDER);
			senderName = data.get(FCM_name_Sender);
			MessageSended = data.get(FCM_message_sender);
			msg = data.get(fcm_msg);
			senderID = data.get("SenderID");
			TimeSend = data.get("TimeSend");

			// added

			notificationTitle = this.getString(R.string.app_name);
			notificationText = "Lor .";


			sendMessageIntent = new Intent(this, ChatActivity.class);
			sendMessageIntent.putExtra("userid", senderID);
			//startActivity(sendMessageIntent);


			if (picture_url != null && !"".equals(picture_url)) {
				// download image with glide  ...
				RequestOptions cropOptions = new RequestOptions();
				bigPicture = Glide.with(MyFirebaseMessagingService.this)
						.asBitmap()
						.load(picture_url)
						.apply(RequestOptions.circleCropTransform())
						.into(100,100).get();


			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}


		notificationBuilder = new NotificationCompat.Builder(this)
				.setContentTitle(senderName)
				.setContentText(MessageSended)
				.setAutoCancel(true)
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				// .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.win))
				.setContentInfo("GuDDana Cloud Notification")
				//.setLargeIcon(getRoundedRectBitmap(bigPicture,12))
				.setLargeIcon(bigPicture)
				.setPriority(Notification.PRIORITY_HIGH)
				.setColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText))
				.setLights(Color.RED, 1000, 300)
				.setDefaults(Notification.DEFAULT_VIBRATE)
				.setNumber(++numMessages)
				.setSmallIcon(R.drawable.xshaka_icon);



		sendNotification(notificationBuilder.build());

	}

	private void setDataForNotificationWithActionButton() {

		notificationBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setLargeIcon(icon)
				.setContentTitle(notificationTitle)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText))
				.setContentText(notificationText);

		Intent answerIntent = new Intent(this, AnswerReceiveActivity.class);
		answerIntent.setAction("Yes");
		PendingIntent pendingIntentYes = PendingIntent.getActivity(this, 1, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notificationBuilder.addAction(R.drawable.thumbs_up, "Yes", pendingIntentYes);

		answerIntent.setAction("No");
		PendingIntent pendingIntentNo = PendingIntent.getActivity(this, 1, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notificationBuilder.addAction(R.drawable.thumbs_down, "No", pendingIntentNo);

/*        answerIntent.setAction("No1");
        PendingIntent pendingIntentNo1 = PendingIntent.getActivity(this, 1, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.addAction(R.drawable.thumbs_down, "No", pendingIntentNo1);

        answerIntent.setAction("No2");
        PendingIntent pendingIntentNo2 = PendingIntent.getActivity(this, 1, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.addAction(R.drawable.thumbs_down, "No", pendingIntentNo2);

        answerIntent.setAction("No3");
        PendingIntent pendingIntentNo3 = PendingIntent.getActivity(this, 1, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.addAction(R.drawable.thumbs_down, "No", pendingIntentNo3);*/

		sendNotification(notificationBuilder.build());
	}



	private void clearAllNotifications() {
		if (notificationManager != null) {
			currentNotificationID = 0;
			notificationManager.cancelAll();
		}
	}


	private void ManageCall(final Map<String, String> data){
		String  System_echo_  = "hello jeune ";
		String ferreadi = "accept und  ";
		String hello = "helloprint system infos ";

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
												try{
													CallRoom.child("Call_room").child(room_call).removeEventListener(EventListener);
													EventListener = null;

												}catch (Exception Ex){
													Ex.printStackTrace();
												}
												new MyAsyncTask(MyFirebaseMessagingService.this, data).execute();

												// sendNotification(data);
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

											new MyAsyncTask(MyFirebaseMessagingService.this, data).execute();

											// sendNotification(data);
											// or  for missed call  in history   ...
											// send unavailibilty notfication  to another user  ...s
											ChatActivity.missedCallNotification(getApplication(),
													"video",
													senderID,
													room_call ,
													"Firebase messaging  notification" ,
													"missed call");

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
							//Toasty.error(getApplicationContext(), ex.toString() , Toast.LENGTH_LONG).show();
							ex.printStackTrace();
						}

					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					//Toasty.error(getApplicationContext(),databaseError.toString(), Toast.LENGTH_LONG).show();

				}
			});



		}catch(Exception ex){
			ex.printStackTrace();
		}
	}


	// to start some task in background
	public class MyAsyncTask extends AsyncTask<Void, Void, String> {

		private Context context;
		private Map<String, String> datal;

		MyAsyncTask(Context context, Map<String, String> data) {
			this.context = context;
			this.datal = data;
		}

		@Override
		protected String doInBackground(Void... params) {
			try {

				sendNotification(datal);

				/*
				return Glide.with(context)
						.asBitmap()
						.load(url)
						.apply(RequestOptions.circleCropTransform())
						.into(100,100).get();

				return Glide.with(context)
						.load(url)
						.asBitmap()
						.into(256, 256)
						.get();

						*/

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return  "hello";

		}

		@Override
		protected void onPostExecute(String bitmap) {
			super.onPostExecute(bitmap);

			if (null != bitmap) {
				Log.d("test", "test");
				// show notification using bitmap
			} else {
				// couldn't fetch the bitmap
			}
		}
	}

}

