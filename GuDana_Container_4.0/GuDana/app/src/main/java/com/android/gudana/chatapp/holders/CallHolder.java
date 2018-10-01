package com.android.gudana.chatapp.holders;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.gudana.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This is a part of ChatApp Project (https://github.com/h01d/ChatApp)
 * Licensed under Apache License 2.0
 *
 * @author  Raf (https://github.com/h01d)
 * @version 1.1
 * @since   27/02/2018
 */

public class CallHolder extends RecyclerView.ViewHolder
{
    private final String TAG = "CA/CallHolder";

    private Activity activity;
    private View view;
    private Context context;

    // Will handle ca_user data

    private DatabaseReference userDatabase;
    private ValueEventListener userListener;


    // user infos profil

    private DatabaseReference userDatabase_infos;
    private ValueEventListener userListener_infos;

    public CallHolder(Activity activity, View view, Context context)
    {
        super(view);

        this.activity = activity;
        this.view = view;
        this.context = context;
    }

    public View getView()
    {
        return view;
    }


    public void setHolder(String userid, String message, long timestamp, long seen)
    {
        final TextView userName = view.findViewById(R.id.user_name);
        final TextView CallDuration = view.findViewById(R.id.user_status);
        final TextView userTime = view.findViewById(R.id.user_timestamp);
        final CircleImageView userImage = view.findViewById(R.id.user_image);
        final ImageView userOnline = view.findViewById(R.id.user_online);
        final ImageView typeCall = view.findViewById(R.id.call_type);


        // FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        CallDuration.setText(message);

        userTime.setVisibility(View.VISIBLE);
        userTime.setText(new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(timestamp));

        if(seen == 0L)
        {
            CallDuration.setTypeface(null, Typeface.BOLD);
            userTime.setTypeface(null, Typeface.BOLD);
        }
        else
        {
            CallDuration.setTypeface(null, Typeface.NORMAL);
            userTime.setTypeface(null, Typeface.NORMAL);
        }

        if(userDatabase != null && userListener != null)
        {
            userDatabase.removeEventListener(userListener);
        }

        // Initialize/Update ca_user data

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("call_History").child("Call_room");

        // userDatabase.keepSynced(true); // For offline use
        userListener= userDatabase.addValueEventListener (new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                try
                {
                    if(dataSnapshot.exists()){

                        final String call_type = dataSnapshot.child("call_type").getValue().toString();
                        final String call_duration  = dataSnapshot.child("call_duration").getValue().toString();
                        final int call_attribut  = (int)dataSnapshot.child("call_attribut").getValue();
                        final String timestapms  = dataSnapshot.child("timestamp").getValue().toString();


                        // call duration
                        if(call_duration != null) {
                            CallDuration.setText(call_duration);
                        }else {
                            CallDuration.setText("Unknow");
                        }


                        //  call event time
                        if(timestapms != null) {
                            userTime.setText(new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(timestapms));

                        }else {

                            userTime.setText("unknow ");
                        }


                        // call Type   video or audio call

                        if(call_type.equalsIgnoreCase("video"))
                        {
                            typeCall.setImageResource(R.mipmap.ic_video);

                        }
                        else
                        {
                            typeCall.setImageResource(R.mipmap.ic_call_history);
                        }


                        // call attribut  missed call  outgoing call ... incomming call

                        if(call_attribut == 0)
                        {
                            userOnline.setImageResource(R.mipmap.incoming_call);

                        }

                        if(call_attribut == 1)
                        {
                            userOnline.setImageResource(R.mipmap.outgoing_call);

                        }
                        if(call_attribut == 2)
                        {
                            userOnline.setImageResource(R.mipmap.missed_call);
                        }

                    }

                }
                catch(Exception e)
                {
                    Log.d(TAG, "userListener exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d(TAG, "userListener failed: " + databaseError.getMessage());
            }
        });
        // userDatabase.addValueEventListener(userListener);


        // get user infos    ...

        if(userDatabase_infos != null && userListener_infos != null)
        {
            userDatabase_infos.removeEventListener(userListener_infos);
        }
        userDatabase_infos = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

        //userDatabase_infos.keepSynced(true); // For offline use
        userListener_infos= userDatabase_infos.addValueEventListener (new ValueEventListener() {
            Timer timer; // Will be used to avoid flickering online status when changing activity

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                try
                {
                    if(dataSnapshot.exists()){

                        final String name = dataSnapshot.child("name").getValue().toString();
                        final String image = dataSnapshot.child("image").getValue().toString();

                        userName.setText(name);

                        if(!image.equals("default"))
                        {
                            Picasso.with(context)
                                    .load(image)
                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                    .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
                                    .centerCrop()
                                    .placeholder(R.drawable.user)
                                    .into(userImage, new Callback()
                                    {
                                        @Override
                                        public void onSuccess()
                                        {

                                        }

                                        @Override
                                        public void onError()
                                        {
                                            Picasso.with(context)
                                                    .load(image)
                                                    .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
                                                    .centerCrop()
                                                    .placeholder(R.drawable.user)
                                                    .error(R.drawable.user)
                                                    .into(userImage);
                                        }
                                    });
                        }
                        else
                        {
                            userImage.setImageResource(R.drawable.user);
                        }

                    }
                }
                catch(Exception e)
                {
                    Log.d(TAG, "userListener exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d(TAG, "userListener failed: " + databaseError.getMessage());
            }
        });
        // userDatabase_infos.addValueEventListener(userListener_infos);
      }

    }
