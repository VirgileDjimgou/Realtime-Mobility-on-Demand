/*
CallIncomingActivity.java
Copyright (C) 2015  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package com.android.gudana.apprtc;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;

import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.R;
import com.android.gudana.apprtc.compatibility.Compatibility;
import com.android.gudana.apprtc.linphone.LinphoneManager;
import com.android.gudana.chat.activities.ChatActivity;
import com.android.gudana.hify.ui.activities.MainActivity_GuDDana;
import com.android.gudana.hify.utils.Config;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.core.LinphoneCall;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

//import com.android.gudana.tindroid.MessageActivity_fire_tinode;


public class CallIncomingActivity extends Activity{
	private static CallIncomingActivity instance;

	// timer task
	Timer timer = new Timer();

	private TextView name, number;
	private Button  accept, decline;
	private CircleImageView contactPicture;

	private LinphoneCall mCall;
	// private LinphoneCoreListenerBase mListener;
	private boolean isScreenActive, alreadyAcceptedOrDeniedCall;
	private float answerX;
	private float declineX;
	LinphoneManager ViCall ;
	private Context mContext;

	private FirebaseFirestore mFirestore;
	private FirebaseUser currentUser;
	public static DatabaseReference SanityChechCall_Db;
	private ValueEventListener mListener;

	private String call_server_id;
	private String call_type = "video";
	private String user_id = null;
	private String room_id = null;
	Timer myTimer = new Timer();
	String message;
	JSONObject jsonObj;



	public static CallIncomingActivity instance() {
		return instance;
	}

	public static boolean isInstanciated() {
		return instance != null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		if (getResources().getBoolean(R.bool.orientation_portrait_only)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}


		mFirestore = FirebaseFirestore.getInstance();
		currentUser= FirebaseAuth.getInstance().getCurrentUser();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.call_incoming);

		name = (TextView) findViewById(R.id.contact_name);
		number = (TextView) findViewById(R.id.contact_number);
		contactPicture = (CircleImageView) findViewById(R.id.image);

		// set this flag so this activity will stay in front of the keyguard
		int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
		getWindow().addFlags(flags);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		isScreenActive = Compatibility.isScreenOn(pm);
		isScreenActive = true;

		final int screenWidth = getResources().getDisplayMetrics().widthPixels;

		accept = (Button) findViewById(R.id.accept);
		decline = (Button) findViewById(R.id.decline);
		accept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isScreenActive) {
					answer();

				} else {
					decline.setEnabled(false);
					//decline.setVisibility(View.GONE);
				}
			}
		});


		decline.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isScreenActive) {

					ViCall.stopRinging();
					decline();
				} else {
					accept.setEnabled(false);
					//accept.setEnabled(false);
					//accept.setVisibility(View.GONE);
				}
			}
		});

		instance = this;


		user_id = getIntent().getStringExtra("userid");
		room_id = getIntent().getStringExtra("room_id_call");
		message = getIntent().getStringExtra("message");

		// sett context ...
		mContext = CallIncomingActivity.this.getApplicationContext();

		// so extrem important to avoid user  call this activity without   ...once the fcm should  call this activity
		if(user_id != null && room_id != null){
			ViCall = new LinphoneManager(CallIncomingActivity.this.getApplicationContext());
			/// start ringing  and vibrate
			try{

				ViCall.startRinging();

			}catch(Exception ex){
				ex.printStackTrace();

			}

			askPermission();
			InitCallerProfil(user_id);
			// start chechker

		}else{

			// Start MainActivitivty
			Intent IntentMain = new Intent(CallIncomingActivity.this, MainActivity_GuDDana.class);
			startActivity(IntentMain);
			finish();

		}

		// create jsonObject  ...
		final JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("index", room_id);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// start chehck avaibility 
		myTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				TimerMethod(jsonObject);
			}

		}, 0, 2000);

	}

	private void TimerMethod(JSONObject jsonObject)
	{
		System.out.println("echos ...");

		// start ac call  ...
		try{
			new Caller_Availability(jsonObject).execute();

		}catch (Exception ex){
			ex.printStackTrace();
		}

	}


	private void askPermission() {

		Dexter.withActivity(CallIncomingActivity.this)
				.withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
						Manifest.permission.ACCESS_FINE_LOCATION,
						Manifest.permission.ACCESS_COARSE_LOCATION,
						Manifest.permission.READ_EXTERNAL_STORAGE,
						Manifest.permission.CAMERA,
						Manifest.permission.RECORD_AUDIO

				)
				.withListener(new MultiplePermissionsListener() {
					@Override
					public void onPermissionsChecked(MultiplePermissionsReport report) {
						if(report.isAnyPermissionPermanentlyDenied()){
							Toasty.warning(CallIncomingActivity.this, "You have denied some permissions permanently, if the app force close try granting permission from settings.", Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

					}
				}).check();

	}

	//  get  users data   and print  that on the screen
	public void InitCallerProfil(String CallerUserId) {

		name.setText("GuDaba User");
		FirebaseFirestore.getInstance().collection("Users")
				.document(CallerUserId)
				.get()
				.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
					@Override
					public void onSuccess(DocumentSnapshot documentSnapshot) {

						String friend_name = "";
						friend_name=documentSnapshot.getString("name");
						//friend_email=documentSnapshot.getString("email");
						String friend_image =documentSnapshot.getString("image");
						//friend_token=documentSnapshot.getString("token");

						name.setText(friend_name);
						//email.setText(friend_email);
						//location.setText(documentSnapshot.getString("location"));
						//bio.setText(documentSnapshot.getString("bio"));

						Glide.with(mContext)
								.setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.default_user_art_g_2))
								.load(friend_image)
								.into(contactPicture);
					}
				});

	}


	@Override
	protected void onResume() {
		super.onResume();
		instance = this;

		alreadyAcceptedOrDeniedCall = false;
		mCall = null;

	}
	
	@Override
	protected void onStart() {
		super.onStart();
		checkAndRequestCallPermissions();
	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@Override
	protected void onDestroy() {

		//SanityChechCall_Db.child("Call_room").child(room_id).removeEventListener(mListener);
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/*
		if (LinphoneManager.isInstanciated() && (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)) {
			LinphoneManager.getLc().terminateCall(mCall);
			finish();
		}
		*/
		return super.onKeyDown(keyCode, event);
	}

	private void decline() {

		//reset Call
		try{
			final JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("index", room_id);

			} catch (JSONException e) {
				e.printStackTrace();
			}

			new ChatActivity.Reset_Call(jsonObject).execute();

		}catch (Exception ex){
			ex.printStackTrace();
		}



		try{
			myTimer.cancel();
		}catch (Exception ex){
			ex.printStackTrace();
		}


		try{

			decline.setEnabled(false);
			ViCall.stopRinging();
			// put the  call  dispo enable

		}catch(Exception ex){


			ex.printStackTrace();
		}
		
		finish();
	}

	private void answer() {

		try{
			myTimer.cancel();
		}catch (Exception ex){
			ex.printStackTrace();
		}



		try{

			accept.setEnabled(false);
			ViCall.stopRinging();

			try {
				jsonObj = new JSONObject(message);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			call_type = jsonObj.getString("call_type");

			Intent intentaudio = new Intent(CallIncomingActivity.this, ConnectActivity.class);
			intentaudio.putExtra("vid_or_aud", call_type);
			intentaudio.putExtra("user_id", user_id);
			intentaudio.putExtra("call_channel", room_id );
			startActivity(intentaudio);
			finish();

		}catch(Exception ex){

			ex.printStackTrace();
		}

		if (alreadyAcceptedOrDeniedCall) {
			return;
		}
		alreadyAcceptedOrDeniedCall = true;


	}


	private void checkAndRequestCallPermissions() {
		ArrayList<String> permissionsList = new ArrayList<String>();
		
		int recordAudio = getPackageManager().checkPermission(Manifest.permission.RECORD_AUDIO, getPackageName());
		//Log.i("[Permission] Record lin_audio permission is " + (recordAudio == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));
		int camera = getPackageManager().checkPermission(Manifest.permission.CAMERA, getPackageName());
		// Log.i("[Permission] Camera permission is " + (camera == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));
		

		
		if (permissionsList.size() > 0) {
			String[] permissions = new String[permissionsList.size()];
			permissions = permissionsList.toArray(permissions);
			ActivityCompat.requestPermissions(this, permissions, 0);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		for (int i = 0; i < permissions.length; i++) {
			Toasty.info(instance, "Permission ", Toast.LENGTH_SHORT).show();

		}
	}


	public class Caller_Availability extends AsyncTask<String, String, JSONObject> {

		JSONObject jsonObject_local;

		public Caller_Availability( JSONObject jsonObject) {
			//this.roomsFragment = roomsFragment;
			this.jsonObject_local = jsonObject;
		}


		@Override
		protected JSONObject doInBackground(String... params) {
		    JSONObject json_return = null;
		    try{

                String Server_url_api_start_call = (Config.URL_CHAT_SERVER.trim()+"/check_caller").trim();
                json_return = (new com.android.gudana.chat.network.JSONParser()).getJSONFromUrl(Server_url_api_start_call, jsonObject_local);

            }catch (Exception ex){
		        ex.printStackTrace();
            }

            if(json_return == null){
		    	// reset call ...
				ViCall.stopRinging();
				decline();

			}

            return  json_return;

		}

		@Override
		protected void onPostExecute(final JSONObject jsonObject_result) {

			try{


				if(jsonObject_result == null) {

					Log.d("receive ", "onPostExecute: ");
					System.out.println(jsonObject_result);

				}else{

					Boolean  Response = jsonObject_result.getBoolean("caller_available");
					if(Response == false && Response != null){
						try{
							if(isScreenActive) {

								ViCall.stopRinging();
								decline();
							} else {
								accept.setEnabled(false);
								//accept.setEnabled(false);
								//accept.setVisibility(View.GONE);
							}

						}catch (Exception ex){
							ex.printStackTrace();
						}


					}else{
						System.out.println("voice server  unreachable ...");

					}

				}

			}catch (Exception ex){
				System.out.println("voice server  unreachable ...");
				ex.printStackTrace();
			}

		}
	}

}