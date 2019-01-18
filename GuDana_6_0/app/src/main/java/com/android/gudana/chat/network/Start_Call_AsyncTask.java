package com.android.gudana.chat.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.android.gudana.chat.ChatApplication;
import com.android.gudana.chat.adapters.RoomAdapter;
import com.android.gudana.chat.fragments.RoomsFragment;
import com.android.gudana.hify.ui.activities.MainActivity_GuDDana;
import com.android.gudana.hify.utils.Config;
import com.google.firebase.database.ServerValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Start_Call_AsyncTask extends AsyncTask<String, String, JSONObject> {

    //RoomsFragment roomsFragment;
    String username, session , member_user_id;
    int user_id = -1;

    public Start_Call_AsyncTask( String username, int user_id, String session , String member_user_id) {
        //this.roomsFragment = roomsFragment;
        this.username = username;
        this.user_id = user_id;
        this.session = session;
        this.member_user_id = member_user_id;
    }


    @Override
    protected JSONObject doInBackground(String... params) {

        //String Server_url = Config.URL_CHAT_SERVER.trim();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id_receiver", Config.Chat_Activity_otherUserId);
            jsonObject.put("timestamp", ServerValue.TIMESTAMP);
            jsonObject.put("available_caller", true);
            jsonObject.put("available_receiver", false);
            jsonObject.put("room_status", true); //
            jsonObject.put("reason_interrupted_call", " -- ");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return (new JSONParser()).getJSONFromUrl(Config.URL_CHAT_SERVER+ "/Start_Call", jsonObject);
    }

    @Override
    protected void onPostExecute(final JSONObject jsonObject) {
        if(jsonObject == null) {

            Log.d("receive ", "onPostExecute: ");
            System.out.println(jsonObject);

        }else{

            Log.d("receive ", "onPostExecute: ");
            System.out.println(jsonObject);

        }

    }
}