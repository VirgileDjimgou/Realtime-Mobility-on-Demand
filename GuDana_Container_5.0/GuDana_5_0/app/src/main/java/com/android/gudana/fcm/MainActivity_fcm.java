package com.android.gudana.fcm;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.gudana.R;
import com.android.gudana.chatapp.models.StaticConfigUser_fromFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

import static com.android.gudana.R.id.txt;

public class MainActivity_fcm extends AppCompatActivity {
	private static final String FCM_AUTH_KEY = "key=AAAAl35zMOc:APA91bG47ILY95DCXjhrthxlOUCUfrbDc7wh7QDirjJCfk3g81z81tCfxRplup2UkOP_bXsHM8Tr7YX0rhyYdAlk7yOWtKPGfgheQIlNfGZtb_S8RYAUtZNu25vcTcP26NYTI09mzbdm9wt7x3rNVmrTPc9P8Vr_-A";
	// private static  String FCM_AUTH_KEY = "key=AAAAinoVD1o:APA91bFCvqGv9MnIPqQ0TZIGmwr93EQ4TeH-9pi0cXVowV-GFhPybWYHPWO-eb2OZJmAMJLrMcIaCPBAjhXfjdyOZP8BRyBgvceBrktnOSC2oSLk4iUORTW5B-THzV1obqmpLu7yYzIt";
	private TextView mTextView;

	CustomFcm_Util FCM_Message_Sender ;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_fcm);
		mTextView = findViewById(txt);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			String tmp = "";
			for (String key : bundle.keySet()) {
				Object value = bundle.get(key);
				tmp += key + ": " + value + "\n\n";
			}
			mTextView.setText(tmp);
		}

		// AUTH_KEY = FirebaseInstanceId.getInstance().getToken();
		FCM_Message_Sender = new CustomFcm_Util();

	}


    public void senNotification(View view){

		String IdfcmUser = FirebaseInstanceId.getInstance().getToken();

		// bee Realtime  betrieb system ....


	}


	public String onTokenRefresh() {
		// Get updated InstanceID token.
		String refreshedToken = FirebaseInstanceId.getInstance().getToken();
		Log.d("Token Instance", "Refreshed token: " + refreshedToken);

		// Toast.makeText(getContext() , refreshedToken.toString() , Toast.LENGTH_LONG).show();
		// If you want to send messages to this application instance or
		// manage this apps subscriptions on the server side, send the
		// Instance ID token to your app server.
		// sendRegistrationToServer(refreshedToken);

		try{
			String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
			DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers/"+ customerId);
			HashMap map = new HashMap();
			map.put("IdFcm" , refreshedToken);

			driverRef.updateChildren(map);

		}catch (Exception ex){
			ex.printStackTrace();
		}

		return refreshedToken;
	}


	public void showToken(View view) {
		mTextView.setText(FirebaseInstanceId.getInstance().getToken());
		Log.i("token", FirebaseInstanceId.getInstance().getToken());
	}

	public void subscribe(View view) {
		FirebaseMessaging.getInstance().subscribeToTopic("news");
		mTextView.setText(R.string.subscribed);
	}

	public void subcribeNews(){

		FirebaseMessaging.getInstance().subscribeToTopic("news");
		mTextView.setText(R.string.subscribed);

	}

	public void unsubscribe(View view) {
		FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
		mTextView.setText(R.string.unsubscribed);
	}

	public void sendToken(View view) {
		sendWithOtherThread("token");
	}

	public void sendTokens(View view) {
		sendWithOtherThread("tokens");
	}

	public void sendTopic(View view) {
		sendWithOtherThread("topic");
	}

	private void sendWithOtherThread(final String type) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				pushNotification(type);
			}
		}).start();
	}

	private void pushNotification(String type) {
		JSONObject jPayload = new JSONObject();
		JSONObject jNotification = new JSONObject();
		JSONObject jData = new JSONObject();

		try {
			jNotification.put("title", "Bee Realtime Mobility on Demand 2018");
			jNotification.put("body", "Firebase Cloud Messaging  from Bee Realtime Mobility on Demand (App)");
			jNotification.put("sound", "default");
			jNotification.put("badge", "1");
			jNotification.put("click_action", "Start_Main");
			jNotification.put("icon", "ic_bee");

			jData.put("picture", "http://opsbug.com/static/google-io.jpg");

			switch(type) {
				case "tokens":
					JSONArray ja = new JSONArray();
					//ja.put("c5pBXXsuCN0:APA91bH8nLMt084KpzMrmSWRS2SnKZudyNjtFVxLRG7VFEFk_RgOm-Q5EQr_oOcLbVcCjFH6vIXIyWhST1jdhR8WMatujccY5uy1TE0hkppW_TSnSBiUsH_tRReutEgsmIMmq8fexTmL");
					ja.put(FirebaseInstanceId.getInstance().getToken());
					jPayload.put("registration_ids", ja);
					break;
				case "topic":
					jPayload.put("to", "/topics/news");
					break;
				case "condition":
					jPayload.put("condition", "'sport' in topics || 'news' in topics");
					break;
				default:
					jPayload.put("to", FirebaseInstanceId.getInstance().getToken());
			}

			jPayload.put("priority", "high");
			jPayload.put("notification", jNotification);
			jPayload.put("data", jData);

			URL url = new URL("https://fcm.googleapis.com/fcm/send");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", FCM_AUTH_KEY);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			// Send FCM message content.
			OutputStream outputStream = conn.getOutputStream();
			outputStream.write(jPayload.toString().getBytes());

			// Read FCM response.
			InputStream inputStream = conn.getInputStream();
			final String resp = convertStreamToString(inputStream);

			Handler h = new Handler(Looper.getMainLooper());
			h.post(new Runnable() {
				@Override
				public void run() {
					mTextView.setText(resp);
				}
			});
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}

	private String convertStreamToString(InputStream is) {
		Scanner s = new Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next().replace(",", ",\n") : "";
	}
}