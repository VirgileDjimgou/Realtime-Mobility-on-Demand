/*
 *  Copyright 2015 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.android.gudana.apprtc;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.R;
import com.android.gudana.apprtc.linphone.LinphoneManager;
//import com.android.gudana.tindroid.MessageActivity_fire_tinode;
import com.android.gudana.chat.activities.ChatActivity;
import com.android.gudana.hify.utils.Config;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.RendererCommon.ScalingType;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static com.android.gudana.apprtc.ConnectActivity.room_is_voice_Server;

/**
 * Fragment for call control.
 */
public class CallFragment extends Fragment {
  private View controlView;
  private TextView contactView ;
  public static  Chronometer call_time_voice ;
  private ImageButton disconnectButton;
  private ImageButton cameraSwitchButton;
  private ImageButton videoScalingButton;
  private ImageButton toggleMuteButton;
  private TextView captureFormatText;
  private SeekBar captureFormatSlider;
  private OnCallEvents callEvents;
  private ScalingType scalingType;
  private boolean videoCallEnabled = true;
  private CircleImageView contactPicture;
  private ValueEventListener mListener;


  public static  LinphoneManager ViCall ;
  public static DatabaseReference CallRoomDb;

  public static boolean running = false;

  // start chehck avaibility
  Timer myTimer = new Timer();


  /**
   * Call control interface for container activity.
   */
  public interface OnCallEvents {
    void onCallHangUp();
    void onCameraSwitch();
    void onVideoScalingSwitch(ScalingType scalingType);
    void onCaptureFormatChange(int width, int height, int framerate);
    boolean onToggleMic();
  }

  @Override
  public View onCreateView(
          LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    controlView = inflater.inflate(R.layout.rtc_fragment_call, container, false);

    // Create UI controls.
    contactView = (TextView) controlView.findViewById(R.id.contact_name_call);
    call_time_voice = (Chronometer) controlView.findViewById(R.id.call_time_voice);
    disconnectButton = (ImageButton) controlView.findViewById(R.id.button_call_disconnect);
    cameraSwitchButton = (ImageButton) controlView.findViewById(R.id.button_call_switch_camera);
    videoScalingButton = (ImageButton) controlView.findViewById(R.id.button_call_scaling_mode);
    toggleMuteButton = (ImageButton) controlView.findViewById(R.id.button_call_toggle_mic);
    captureFormatText = (TextView) controlView.findViewById(R.id.capture_format_text_call);
    captureFormatSlider = (SeekBar) controlView.findViewById(R.id.capture_format_slider_call);
    contactPicture = (CircleImageView) controlView.findViewById(R.id.image);


    // Add buttons click events.
    disconnectButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // stop vibrate
        ViCall.stopRinging();
        callEvents.onCallHangUp();
        /*
        // send notification ...to tell that your are not available anymore ....
        CreateGroupChatActivity.resetCallparameter(controlView.getContext() ,
                room_is_voice_Server,
                "CallFragment  : disconnect",
                "your correspondent is not available "
        );
        */
        try{

          if(CallFragment.this.getActivity() != null){
            CallFragment.this.getActivity().finish();

          }

        }catch (Exception ex){
          ex.printStackTrace();
        }
      }
    });

    cameraSwitchButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        callEvents.onCameraSwitch();
      }
    });

    videoScalingButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (scalingType == ScalingType.SCALE_ASPECT_FILL) {
          videoScalingButton.setBackgroundResource(R.drawable.ic_action_full_screen);
          scalingType = ScalingType.SCALE_ASPECT_FIT;
        } else {
          videoScalingButton.setBackgroundResource(R.drawable.ic_action_return_from_full_screen);
          scalingType = ScalingType.SCALE_ASPECT_FILL;
        }
        callEvents.onVideoScalingSwitch(scalingType);
      }
    });
    scalingType = ScalingType.SCALE_ASPECT_FILL;

    toggleMuteButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        boolean enabled = callEvents.onToggleMic();
        toggleMuteButton.setAlpha(enabled ? 1.0f : 0.3f);
      }
    });


      // start chronometer  ....
    call_time_voice.setFormat("Time- %s"); // set the format for a chronometer
    call_time_voice.start();


    // start call status  ...
    //Check_Correspondantavailibility(controlView.getContext(), ConnectActivity.user_id);

    // init  caller id
    InitCallerProfil(ConnectActivity.user_id);

    ViCall = new LinphoneManager(controlView.getContext());


    /*
    // Outgoing Call Ringtone methode
    if(ConnectActivity.received_call.equalsIgnoreCase("caller")){
      /// start ringing  and vibrate
      try{

        ViCall.startRinging_without_vibrate(controlView.getContext());
        //ViCall.startRinging();

      }catch(Exception ex){
        ex.printStackTrace();

      }

    }

    */

    // set the running method  to tell that you can not take another call
    running = true;


    // start  timer  to chehck correspondant avalabilty

    // create jsonObject  ...
    final JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("index", room_is_voice_Server);

    } catch (JSONException e) {
      e.printStackTrace();
    }


    myTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        // start ac call  ...
        try{
          TimerMethod(jsonObject);
        }catch (Exception ex){
          ex.printStackTrace();
        }
      }

    }, 0, 2000);


    return controlView;
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

  // init profil but we can optimize that

  public void InitCallerProfil(String CallerUserId) {

    contactView.setText("GuDaba User");
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

                contactView.setText(friend_name);
                //email.setText(friend_email);
                //location.setText(documentSnapshot.getString("location"));
                //bio.setText(documentSnapshot.getString("bio"));

                Glide.with(controlView)
                        .setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.default_user_art_g_2))
                        .load(friend_image)
                        .into(contactPicture);
              }
            });

  }


  // chehck  caller availabilty  ...

  public class Caller_Availability extends AsyncTask<String, String, JSONObject> {

    JSONObject jsonObject_local;

    public Caller_Availability( JSONObject jsonObject) {
      //this.roomsFragment = roomsFragment;
      this.jsonObject_local = jsonObject;
    }


    @Override
    protected JSONObject doInBackground(String... params) {
      JSONObject json_return = null;

      try {

        String Server_url_api_start_call = (Config.URL_CHAT_SERVER.trim()+"/check_caller").trim();
        json_return =  (new com.android.gudana.chat.network.JSONParser()).getJSONFromUrl(Server_url_api_start_call, jsonObject_local);


      }catch (Exception ex){
        ex.printStackTrace();
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
            // than
            Log.d("receive ", "onPostExecute: ");
            System.out.println(jsonObject_result);
            if(Response == false){

              try{

                if(CallFragment.this.getActivity() != null){
                  CallFragment.this.getActivity().finish(); }

              }catch (Exception ex){
                ex.printStackTrace();
              }

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

  // call

  @Override
  public void onStart() {
    super.onStart();

    boolean captureSliderEnabled = false;
    Bundle args = getArguments();
    if (args != null) {
      String contactName = args.getString(CallActivity.EXTRA_ROOMID);
      contactView.setText(contactName);
      videoCallEnabled = args.getBoolean(CallActivity.EXTRA_VIDEO_CALL, true);
      captureSliderEnabled = videoCallEnabled
          && args.getBoolean(CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, false);
    }
    if (!videoCallEnabled) {
      cameraSwitchButton.setVisibility(View.INVISIBLE);
    }
    if (captureSliderEnabled) {
      captureFormatSlider.setOnSeekBarChangeListener(
          new CaptureQualityController(captureFormatText, callEvents));
    } else {
      captureFormatText.setVisibility(View.GONE);
      captureFormatSlider.setVisibility(View.GONE);
    }

    running = true;
  }

  // TODO(sakal): Replace with onAttach(Context) once we only support API level 23+.
  @SuppressWarnings("deprecation")
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    running = true;
    callEvents = (OnCallEvents) activity;
  }

  @Override
  public void onDestroy() {
    // set the avaibility  ...

    try{
      myTimer.cancel();
    }catch (Exception ex){
      ex.printStackTrace();
    }


    try{

      try{
        final JSONObject jsonObject = new JSONObject();
        try {
          jsonObject.put("index", room_is_voice_Server);

        } catch (JSONException e) {
          e.printStackTrace();
        }

        new ChatActivity.Reset_Call(jsonObject).execute();

      }catch (Exception ex){
        ex.printStackTrace();
      }



      running = false;
      ViCall.stopRinging();
      callEvents.onCallHangUp();

    }catch(Exception ex){
      ViCall.stopRinging();
      callEvents.onCallHangUp();
        ex.printStackTrace();
    }
    super.onDestroy();
  }

  @Override
  public void onDetach() {
      try{
        myTimer.cancel();
        // when  the fragment ist in background for example  ...  we  can manage his state in fragment manager ...

      }catch(Exception ex){
          ex.printStackTrace();
      }    super.onDetach();
  }
}
