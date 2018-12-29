package com.android.gudana.chat.network;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.gudana.chat.ChatApplication;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateRoomAsyncTask extends AsyncTask<String, String, JSONObject> {
    private Activity activity;
    private String username, session, room_name , url_profile_pic , type_id , room_uid , initiator_user_id , member_user_id;
    private int user_id;

    public CreateRoomAsyncTask(Activity activity, String username,
                               String session, String room_name,
                               String url_profile_pic, String type_id,
                               String room_uid, String initiator_user_id,
                               String member_user_id, int user_id) {
        this.activity = activity;
        this.username = username;
        this.session = session;
        this.room_name = room_name;
        this.url_profile_pic = url_profile_pic;
        this.type_id = type_id;
        this.room_uid = room_uid;
        this.initiator_user_id = initiator_user_id;
        this.member_user_id = member_user_id;
        this.user_id = user_id;
    }

    /*
    public CreateRoomAsyncTask(Activity activity, int user_id, String username, String session, String room_name , String url_profile_pic) {
        this.activity = activity;
        this.user_id = user_id;
        this.username = username;
        this.session = session;
        this.room_name = room_name;
        this.url_profile_pic = url_profile_pic;
    }
    */

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("initiator_user_id", initiator_user_id);
            jsonObject.put("member_user_id", member_user_id);
            jsonObject.put("room_uid", room_uid);
            jsonObject.put("type_id", type_id);
            jsonObject.put("user_id", user_id);
            jsonObject.put("username", username);
            jsonObject.put("session", session);
            jsonObject.put("room_name", room_name);
            jsonObject.put("profile_picture_id", url_profile_pic);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        JSONParser jsonParser = new JSONParser();
        return jsonParser.getJSONFromUrl(((ChatApplication) activity.getApplication()).getURL() + "/createroom", jsonObject);
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if(jsonObject == null) {
            Toast.makeText(activity, "Cannot create room '" + room_name + "'", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if(jsonObject.getBoolean("created") && jsonObject.has("room_id")) {
                //room_id = jsonObject.getInt("room_id");
                //Intent intent = new Intent(activity, ChatActivity.class);
                //intent.putExtra("room_name", room_name);
                //intent.putExtra("room_id", room_id);
                //activity.startActivity(intent);
            } else {
                if(jsonObject.has("error")) {
                    Toast.makeText(activity, "Cannot create room '" + room_name + "': " + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "Cannot create room '" + room_name + "'", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Cannot create room '" + room_name + "'", Toast.LENGTH_SHORT).show();
        }
    }
}