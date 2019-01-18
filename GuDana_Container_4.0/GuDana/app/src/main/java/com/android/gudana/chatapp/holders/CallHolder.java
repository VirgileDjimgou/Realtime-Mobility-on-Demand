package com.android.gudana.chatapp.holders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.R;
import com.android.gudana.chatapp.activities.FullScreenActivity;
import com.android.gudana.chatapp.activities.ProfileActivity;
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
    public static  DatabaseReference userDatabase_profile;
    public static ValueEventListener userListener_profile;

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


    public void setHolder(String UserId , String call_node_id, String message, long timestamp, long seen)
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

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(UserId).child("call_History").child("Call_room").child(call_node_id);

        // userDatabase.keepSynced(true); // For offline use
        userListener= userDatabase.addValueEventListener (new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                try
                {
                    if(dataSnapshot.exists()){


                        // set the image of  the  corespondant
                        if(dataSnapshot.child("id_receiver").getValue().toString() != null){
                            final String Corespondant_User_Id =  dataSnapshot.child("id_receiver").getValue().toString();

                            // get the  correspondat images ....  and name
                            if(userDatabase_infos != null && userListener_infos != null)
                            {
                                userDatabase_infos.removeEventListener(userListener_infos);
                            }

                            // Initialize/Update ca_user data

                            userDatabase_infos = FirebaseDatabase.getInstance().getReference().child("Users").child(Corespondant_User_Id);

                            // userDatabase.keepSynced(true); // For offline use
                            userListener_infos= userDatabase_infos.addValueEventListener (new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    try {
                                        if (dataSnapshot.exists()) {


                                            if(dataSnapshot.child("name").getValue().toString() != null){
                                                final String name = dataSnapshot.child("name").getValue().toString();
                                                userName.setText(name);


                                            }

                                            if(dataSnapshot.child("image").getValue().toString() !=null){
                                                final String image = dataSnapshot.child("image").getValue().toString();

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


                        }


                        // call duration
                        if(dataSnapshot.child("call_duration").getValue().toString() != null) {
                            final String call_duration  = dataSnapshot.child("call_duration").getValue().toString();
                            CallDuration.setText(call_duration);
                        }else {
                            CallDuration.setText("Unknow");
                        }


                        //  call event time
                        if( dataSnapshot.child("timestamp").getValue().toString() != null) {
                            final String timestapms  = dataSnapshot.child("timestamp").getValue().toString();

                            try{
                                userTime.setText(new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(timestapms));


                            }catch(Exception ex){
                                // set unknow  format
                                userTime.setText("unknow");
                                ex.printStackTrace();
                            }

                        }else {

                            userTime.setText("unknow ");
                        }


                        // call Type   video or audio call
                        if(dataSnapshot.child("call_type").getValue().toString() != null){
                            final String call_type = dataSnapshot.child("call_type").getValue().toString();
                            if(call_type.equalsIgnoreCase("video"))
                            {
                                typeCall.setImageResource(R.mipmap.ic_video);

                            }
                            else
                            {
                                typeCall.setImageResource(R.mipmap.ic_call_history);
                            }

                        }




                        // call attribut  missed call  outgoing call ... incomming call
                        if(dataSnapshot.child("call_attribut").getValue() != null){
                            final long call_attribut  = (long)dataSnapshot.child("call_attribut").getValue();

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


        // userDatabase_infos.addValueEventListener(userListener_infos);
      }


    }
