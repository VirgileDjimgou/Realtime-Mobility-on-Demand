

package com.android.gudana.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.android.gudana.R;
import com.android.gudana.gpslocationtracking.LocationTrack;
import com.android.gudana.hify.utils.Config;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.github.sac.Ack;
import io.github.sac.BasicListener;
import io.github.sac.Emitter;
import io.github.sac.ReconnectStrategy;
import io.github.sac.Socket;

/**
 * Created by fabio on 30/01/2016.
 */
public class SensorService extends Service {

    // notification service


    public static  int currentNotificationID = 0;
    // private NotificationManager notificationManager;
    // private NotificationCompat.Builder notificationBuilder;
    public static  String notificationText;
    public static  Bitmap icon;
    public static  int combinedNotificationCounter;


    public static NotificationManager notificationManager = null ;
    public static NotificationCompat.Builder notificationBuilder;
    public static Boolean NotifierExist = false;

    // gps share
    private LatLng RT_gps_position;
    LocationTrack locationTrack;
    //private live_location_sharing_db live_location;
    String url="ws://35.237.206.152:8000/socketcluster/"; // default Adresse
    //String SocketClusterChannel = "GuDana-Location-Sharing+random_name";
    Socket socket;
    Handler Typinghandler=new Handler();
    String Username="Demo";
    String UserType = "sub"; // That means connected als subscriber (sub) or publischer (pub)
    //Context ServiceContext;
    //String ServiceName;
    JSONObject LiveLoc;

    private Integer Thread_Worker_location_sharing = 0;

    public int counter=0;
    public SensorService( Context applicationContext ) {
        super();
        try{
            if(NotifierExist != true) {
                NotifierExist = true;
                // to avoid  more than one time intialisation  of notification manager    ... on instance of notification
                notificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
            }

            if(icon == null){
                icon = BitmapFactory.decodeResource(applicationContext.getResources(),
                        R.mipmap.ic_launcher);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public SensorService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Bundle extras = intent.getExtras();

        try{

            if(extras == null) {
                Log.d("Service","null");
            } else {
                Log.d("Service","not null");
                String data = (String) extras.get("From");
                try {

                    JSONObject obj = new JSONObject(data);
                    LiveLoc = obj;
                    try {
                        startTimer();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + "data " + "\"");
                }
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }


        return START_STICKY;
    }


    public void initService(JSONObject LiveLocation){
        LiveLoc = LiveLocation;
        //System.out.println(LiveLoc.getCONTACTS_TABLE_MATRITCULE_LIVE());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() throws InterruptedException {
        //set a new Timer
        timer = new Timer();
        try{

            //live_location = new live_location_sharing_db(ServiceContext);
            locationTrack = new LocationTrack(this);

            // init socket
            socket = new Socket(url);

            socket.setListener(new BasicListener() {

                public void onConnected(Socket socket, Map<String, List<String>> headers) {
                    try {
                        socket.createChannel(Config.UID_EVENT_LOCATION_LIVE_CHANNEL).subscribe(new Ack() {
                            @Override
                            public void call(String name, Object error, Object data) {
                                if (error==null){
                                    Log.i ("Success","subscribed to channel "+name);
                                    System.out.println("Succes + Subscribed to channel ");
                                    //Toast.makeText(ServiceContext, "Service : subscribed to channel "+name, Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.i("Success ","Connected to endpoint");
                    System.out.println("Connected to endpoint ");
                    //Toast.makeText(ServiceContext, "Service : Connected to endpoint", Toast.LENGTH_SHORT).show();
                }

                public void onDisconnected(Socket socket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
                    Log.i("Success ","Disconnected from end-point");
                }

                public void onConnectError(Socket socket,WebSocketException exception) {
                    Log.i("Success ","Got connect error "+ exception);
                }

                public void onSetAuthToken(String token, Socket socket) {
                    socket.setAuthToken(token);
                }

                public void onAuthentication(Socket socket,Boolean status) {
                    if (status) {
                        Log.i("Success ","socket is authenticated");
                    } else {
                        Log.i("Success ","Authentication is required (optional)");
                    }
                }

            });

            socket.setReconnection(new ReconnectStrategy().setMaxAttempts(10).setDelay(3000));
            socket.connectAsync();
            try {
                socket.onSubscribe(Config.UID_EVENT_LOCATION_LIVE_CHANNEL,new Emitter.Listener() {
                    @Override
                    public void call(String name, final Object data) {

                        try {
                            JSONObject object = (JSONObject) data;
                            Log.i ("Received  Json Data ",((JSONObject) data).toString());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            //initialize the TimerTask's job
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            initializeTimerTask();

            //schedule the timer, to wake up every 1 second
            timer.schedule(timerTask, 2000, 2000); //
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                //  send gps location to  all client for reatime  purpose  ...
                // chehck all the client to broacst  adress   and the client must be local persisted   ...

                try {
                JSONObject object=new JSONObject();

                    object.put("Event",Config.UID_EVENT_LOCATION_LIVE_CHANNEL);
                    object.put("User_Id_Sender", FirebaseAuth.getInstance().getUid());
                    object.put("is_LiveLocation",true);
                    object.put("latitude",locationTrack.getLatitude());
                    object.put("longitude", locationTrack.getLongitude());

                    socket.getChannelByName(Config.UID_EVENT_LOCATION_LIVE_CHANNEL).publish(object, new Ack() {
                        @Override
                        public void call(String name, Object error, Object data) {
                            if (error==null){
                                Log.i ("Success","Publish sent successfully");
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    //Toasty.error(ServiceContext, "Live location sharing error ", Toast.LENGTH_SHORT).show();
                }
                try {
                    Log.i("in timer", Config.UID_EVENT_LOCATION_LIVE_CHANNEL+ "  in timer ++++  "+ (counter++));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}