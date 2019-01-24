package com.android.gudana.hify.utils;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;

import com.android.gudana.R;
import com.android.gudana.chat.ChatApplication;
import com.android.gudana.chat.network.Start_Call_AsyncTask;
import com.google.firebase.database.ServerValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Test_RestHttp_Server_Activity extends AppCompatActivity {

    public static String username, session;
    public static int user_id;
    HashMap<String, Emitter.Listener> eventListeners = new HashMap<>();

    private Socket mSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test__rest_http__server_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        try {

            eventListeners.put("Call_id_room", Call_id_room);
            setListeningToEvents(true);


        } catch (Exception ex) {
            ex.printStackTrace();
        }


        try {
            mSocket = IO.socket(((ChatApplication) getApplication()).getURL());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mSocket.connect();


        try {
            JSONObject info= new JSONObject();
            info.put("Call_id_room", "0123456789");
            mSocket.emit("Call_id_room", info);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Button finsich_call = (Button) findViewById(R.id.finsich_call);
        Button start_call = (Button) findViewById(R.id.start_call);


            start_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("room_id", "-546jhgjj");
                        jsonObject.put("id_caller", "caliuz54jhgj");
                        jsonObject.put("id_receiver", Config.Chat_Activity_otherUserId);
                        jsonObject.put("timestamp", ServerValue.TIMESTAMP);
                        jsonObject.put("available_caller", true);
                        jsonObject.put("available_receiver", false);
                        jsonObject.put("room_status", true); //
                        jsonObject.put("call_type", "video");
                        jsonObject.put("reason_interrupted_call", " -- ");

                        // send custom room
                        mSocket.emit("join", "gsdhgjsdghj call id ");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    (new Start_Call_AsyncTask("username", 123456, "session_id", "menber_id")).execute();
                }
            });


        }


    private final Emitter.Listener Call_id_room = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject json;
            final int msg_user_id, message_id;
            final String msg_username, message_contents, datetimeutc;
            try {
                json = (JSONObject) args[0];

                msg_user_id = json.getInt("user_id");
                message_id = json.getInt("message_id");
                msg_username = json.getString("username");
                message_contents = json.getString("message");
                datetimeutc = json.getString("datetimeutc");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };




    private void setListeningToEvents(boolean start_listening) {

        try{

            for(Map.Entry eventListener: eventListeners.entrySet()) {
                if(start_listening) {
                    mSocket.on((String) eventListener.getKey(), (Emitter.Listener) eventListener.getValue());
                } else {
                    mSocket.off((String) eventListener.getKey(), (Emitter.Listener) eventListener.getValue());
                }
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

}
