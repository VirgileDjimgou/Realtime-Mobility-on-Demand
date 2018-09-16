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
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.R;
import com.android.gudana.chatapp.activities.ChatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.webrtc.RendererCommon.ScalingType;

import java.util.Map;

import es.dmoral.toasty.Toasty;

import static com.android.gudana.chatapp.activities.ChatActivity.Call_dispo;
import static com.android.gudana.chatapp.activities.ChatActivity.callmelder_notification;

/**
 * Fragment for call control.
 */
public class CallFragment extends Fragment {
  private View controlView;
  private TextView contactView ;
  private Chronometer call_time_voice ;
  private ImageButton disconnectButton;
  private ImageButton cameraSwitchButton;
  private ImageButton videoScalingButton;
  private ImageButton toggleMuteButton;
  private TextView captureFormatText;
  private SeekBar captureFormatSlider;
  private OnCallEvents callEvents;
  private ScalingType scalingType;
  private boolean videoCallEnabled = true;
  public static DatabaseReference userDB;


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

    // Add buttons click events.
    disconnectButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Call_dispo = true;
        callEvents.onCallHangUp();
        // send notification ...to tell that your are not available anymore ....
        ChatActivity.resetCallparameter(controlView.getContext() , ConnectActivity.user_id);
        getActivity().finish();
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


    // send notification audio call or video call    ... to another user
      if(callmelder_notification){
          // send a notification for  to registread the call
          ChatActivity.call_infos_notification(controlView.getContext());
          callmelder_notification = false;
      }else{
          // another kind of notification  ...
      }
      // start chronometer  ....
    call_time_voice.setFormat("Time Running - %s"); // set the format for a chronometer
    call_time_voice.start();


    // start call status  ...
    Check_Correspondantavailibility(controlView.getContext(), ConnectActivity.user_id);
    return controlView;
  }


  public void Check_Correspondantavailibility(final Context context , String UserID){

    try{

      // ViCall.stopRinging();

      userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(UserID);
      // Set the  Driver Response to true ...
      //HashMap map = new HashMap();
      //map.put("Authentified" , "await");
      //userDB.updateChildren(map);
      userDB.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          if(dataSnapshot.exists()){
            try{
              Map<String, Object> map_call = (Map<String, Object>) dataSnapshot.getValue();
              // test if the recors Phone already exist  ...if not than
              // than you are a new user   ...
              if(map_call.get("call_possible")!=null){
                // than this user is already registered ...
                boolean availibilty  = (boolean) map_call.get("call_possible");
                if(availibilty = false) {
                  // than we must stop the call  ...
                  // ViCall.stopRinging();
                  // put the  call  dispo enable
                  Call_dispo = true;
                  getActivity().finish();
                }

              }else{

              }


            }catch(Exception ex){
              Toasty.error(context, ex.toString() , Toast.LENGTH_LONG).show();
              ex.printStackTrace();
            }

          }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
          Toasty.error(context,databaseError.toString(), Toast.LENGTH_LONG).show();

        }
      });



    }catch(Exception ex){

      Call_dispo = true;
      ex.printStackTrace();
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
  }

  // TODO(sakal): Replace with onAttach(Context) once we only support API level 23+.
  @SuppressWarnings("deprecation")
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    callEvents = (OnCallEvents) activity;
  }

  @Override
  public void onDestroy() {
    // set the avaibility  ...
    Call_dispo = true;
    super.onDestroy();
  }

  @Override
  public void onDetach() {
    Call_dispo = true;
    super.onDetach();
  }
}
