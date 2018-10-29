package com.android.gudana.fcm;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

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
import java.util.Scanner;

/**
 * Created by allwithoutblue on 1/29/18.
 */

public class CustomFcm_Util {

    // you can have this key on firebase console / cloud messaging settings .... this key is only valid with your app/ project on firebase .... cloud
    private static final String AUTH_KEY = "key=AAAAl35zMOc:APA91bG47ILY95DCXjhrthxlOUCUfrbDc7wh7QDirjJCfk3g81z81tCfxRplup2UkOP_bXsHM8Tr7YX0rhyYdAlk7yOWtKPGfgheQIlNfGZtb_S8RYAUtZNu25vcTcP26NYTI09mzbdm9wt7x3rNVmrTPc9P8Vr_-A";
    private TextView mTextView;

     public CustomFcm_Util(){
     }

    public void showToken() {
        Log.i("token", FirebaseInstanceId.getInstance().getToken());
    }

    public void subscribe() {
        FirebaseMessaging.getInstance().subscribeToTopic("news");
    }

    public void subcribeNews(){
        FirebaseMessaging.getInstance().subscribeToTopic("news");
    }

    public void unsubscribe() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
    }


    // implement the method   to send  notificatin  with image  like WhatApp for example
    //  how to send notification  double notification   ...


    public void sendWithOtherThread(final String type ,
                                    final String Id_User_Fcm ,
                                    final String Titel ,
                                    final String SenderID,
                                    final String SenderName,
                                    final String Sender_Icon_URL,
                                    final String TimeSend,
                                    final String Room_Call_ID,
                                    final String message ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // pushNotification(type , Id_User_Fcm);
                JSONObject jPayload = new JSONObject();
                // JSONObject jNotification = new JSONObject();
                JSONObject jData = new JSONObject();
                try {

                    // jNotification.put("title", "yX Realtime Mobility on Demand 2018");
                    // jNotification.put("body", "Firebase Cloud Messaging  from yX Realtime Mobility on Demand");

                    /*
                    jNotification.put("title", Titel);
                    jNotification.put("body", message);
                    jNotification.put("picture", Sender_Icon_URL);
                    jNotification.put("SenderID", SenderID);
                    jNotification.put("SenderName", SenderName);
                    jNotification.put("Sender_Icon_URL", Sender_Icon_URL);
                    jNotification.put("TimeSend", TimeSend);
                    jNotification.put("msg", message);
                    jNotification.put("sound", "default");
                    jNotification.put("badge", "1");
                    jNotification.put("click_action", ".MainActivity");
                    jNotification.put("icon", "ic_bee");
                    */

                    // https://firebasestorage.googleapis.com/v0/b/beewallet-114e7.appspot.com/o/logo_message_2.jpg?alt=media&token=655833c9-3073-4792-9a8a-e68246e80c0b
                    jData.put("title", Titel);
                    jData.put("body", message);
                    jData.put("picture", Sender_Icon_URL);
                    jData.put("SenderID", SenderID);
                    jData.put("SenderName", SenderName);
                    jData.put("Sender_Icon_URL", Sender_Icon_URL);
                    jData.put("Room_id", Room_Call_ID);
                    jData.put("TimeSend", TimeSend);



                    switch(type) {
                        case "tokens":
                            JSONArray ja = new JSONArray();
                            ja.put("c5pBXXsuCN0:APA91bH8nLMt084KpzMrmSWRS2SnKZudyNjtFVxLRG7VFEFk_RgOm-Q5EQr_oOcLbVcCjFH6vIXIyWhST1jdhR8WMatujccY5uy1TE0hkppW_TSnSBiUsH_tRReutEgsmIMmq8fexTmL");
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
                            // jPayload.put("to", FirebaseInstanceId.getInstance().getToken());
                            jPayload.put("to", Id_User_Fcm);
                    }

                    jPayload.put("priority", "high");
                    //jPayload.put("notification", jNotification);
                    jPayload.put("data", jData);

                    URL url = new URL("https://fcm.googleapis.com/fcm/send");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Authorization", AUTH_KEY);
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
                            //  mTextView.setText(resp);
                        }
                    });
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void pushNotification(String type , String Id_User_Fcm) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();
        try {
            jNotification.put("title", "Bee Realtime Mobility on Demand 2018");
            jNotification.put("body", "Firebase Cloud Messaging  from Bee Realtime Mobility on Demand");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "Start_Main");
            jNotification.put("icon", "ic_bee");

            // https://firebasestorage.googleapis.com/v0/b/beewallet-114e7.appspot.com/o/logo_message_2.jpg?alt=media&token=655833c9-3073-4792-9a8a-e68246e80c0b
            jData.put("picture", "https://firebasestorage.googleapis.com/v0/b/beewallet-114e7.appspot.com/o/logo_message_2.jpg?alt=media&token=655833c9-3073-4792-9a8a-e68246e80c0b");
            // jData.put("picture", "http://opsbug.com/static/google-io.jpg");


            switch(type) {
                case "tokens":
                    JSONArray ja = new JSONArray();
                    ja.put("c5pBXXsuCN0:APA91bH8nLMt084KpzMrmSWRS2SnKZudyNjtFVxLRG7VFEFk_RgOm-Q5EQr_oOcLbVcCjFH6vIXIyWhST1jdhR8WMatujccY5uy1TE0hkppW_TSnSBiUsH_tRReutEgsmIMmq8fexTmL");
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
                    // jPayload.put("to", FirebaseInstanceId.getInstance().getToken());
                    jPayload.put("to", Id_User_Fcm);
            }

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jData);

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", AUTH_KEY);
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
                   //  mTextView.setText(resp);
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
