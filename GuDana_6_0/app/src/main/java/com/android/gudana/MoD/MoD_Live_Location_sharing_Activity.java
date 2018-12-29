package com.android.gudana.MoD;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.firebase.geofire.GeoQuery;
import com.android.gudana.R;
import com.android.gudana.gpslocationtracking.LocationTrack;
import com.android.gudana.hify.utils.Config;
import com.android.gudana.hify.utils.RoundBitmap;
import com.android.gudana.hify.utils.database.UserHelper;
import com.android.gudana.hify.utils.database.live_location_sharing_db;
import com.android.gudana.hify.utils.random_Utils;
import com.android.gudana.service.SensorService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import bolts.Task;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import io.github.sac.Ack;
import io.github.sac.BasicListener;
import io.github.sac.Emitter;
import io.github.sac.ReconnectStrategy;
import io.github.sac.Socket;


public class MoD_Live_Location_sharing_Activity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    MapView mMapView;
    private UserHelper userHelper;
    private live_location_sharing_db live_location;
    // socket cluster attribut

    Handler Typinghandler=new Handler();
    String Username="Demo";
    String UserType = "sub"; // That means connected als subscriber (sub) or publischer (pub)


    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private Button start_aharing;
    private String usernam = "Username";

    private Button option_15 , option_60 , option_8 , option_custom_time;
    private String Selected_options = "option_15";

    PlaceAutocompleteFragment Destination_autocompleteFragment;
    Drawable select_backgrond, normal_select_backgrond;
    private BitmapDescriptor drawable_icon_dsescritor;
    private Bitmap Icon_User = null;
    private MarkerOptions MyPosition;
    private boolean marker_ok = false; // to tell tthat the mar was correctlly initialised   ..
    private ImageView imgView;
    private static final LatLng MELBOURNE = new LatLng(-37.813, 144.962);
    boolean  repeat = true;
    private TextView appBarName, appBarSeen;
    private EmojiconEditText Live_Titel;

    private CircleImageView messageImageRight = null;
    LocationTrack locationTrack;
    private LatLng RT_gps_position;
    private Context context;
    private ImageView buttonEmoji;

    // background  service ...

    Intent mServiceIntent;
    private SensorService mSensorService;
    Context ctx;
    public Context getCtx() {
        return ctx;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_costumer_map);

        askPermission();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");
        context = MoD_Live_Location_sharing_Activity.this;
        ctx = this;


        locationTrack = new LocationTrack(MoD_Live_Location_sharing_Activity.this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.ca_chat_bar, null);

        appBarName = actionBarView.findViewById(R.id.chat_bar_name);
        appBarSeen = actionBarView.findViewById(R.id.chat_bar_seen);
        messageImageRight = actionBarView.findViewById(R.id.icon_image);

        actionBar.setCustomView(actionBarView);

        // init Socket Cluster
        select_backgrond = getResources().getDrawable(R.drawable.rounded_edittext__3);
        normal_select_backgrond = getResources().getDrawable(R.drawable.rounded_edittext);

        Live_Titel = (EmojiconEditText) findViewById(R.id.txt_message);
        Live_Titel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        option_15 = (Button) findViewById(R.id.time_15_minute);
        option_15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selected_options = "option_15";
                option_15.setBackgroundDrawable(select_backgrond);

                // uncheck  other buttonoption
                option_8.setBackgroundDrawable(normal_select_backgrond);
                option_60.setBackgroundDrawable(normal_select_backgrond);
                option_custom_time.setBackgroundDrawable(normal_select_backgrond);



            }
        });

        option_8 = (Button) findViewById(R.id.time_8_hours);
        option_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selected_options = "option_8";

                option_8.setBackgroundDrawable(select_backgrond);

                // uncheck  other buttonoption
                option_15.setBackgroundDrawable(normal_select_backgrond);
                option_60.setBackgroundDrawable(normal_select_backgrond);
                option_custom_time.setBackgroundDrawable(normal_select_backgrond);


            }
        });


        option_60 = (Button) findViewById(R.id.time_60_minute);
        option_60.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selected_options = "option_60";

                option_60.setBackgroundDrawable(select_backgrond);

                // uncheck  other buttonoption
                option_8.setBackgroundDrawable(normal_select_backgrond);
                option_15.setBackgroundDrawable(normal_select_backgrond);
                option_custom_time.setBackgroundDrawable(normal_select_backgrond);


            }
        });


        option_custom_time = (Button) findViewById(R.id.time_custom_time);
        option_custom_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selected_options = "option_custom_time";

                option_custom_time.setBackgroundDrawable(select_backgrond);

                // uncheck  other buttonoption
                option_8.setBackgroundDrawable(normal_select_backgrond);
                option_60.setBackgroundDrawable(normal_select_backgrond);
                option_15.setBackgroundDrawable(normal_select_backgrond);


            }
        });


        // location startegies  ...

        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }else{
            mMapView.getMapAsync(this);
        }

        try {
            MapsInitializer.initialize(this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }



        mMapView.onResume(); // needed to get the map to display immediately

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }else{
            mMapView.getMapAsync(this);
        }

        try {
            MapsInitializer.initialize(this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        buttonEmoji = (ImageView) findViewById(R.id.buttonEmoji);
        buttonEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Cursor rs = live_location.getData(1);
                //rs.moveToFirst();
                //int Number_of_live_event = live_location.getRownumber(rs,"id");
                int Numb_Event =  live_location.getProfilesCount();
                Toast.makeText(context, "Number of Live Evenet : " + Integer.toString(Numb_Event), Toast.LENGTH_SHORT).show();
                Cursor rs = live_location.getData(Numb_Event);
                rs.moveToFirst();

                if(Numb_Event > 0){
                    for(int i=0; i<Numb_Event; i++){
                        String LiveName =rs.getString(rs.getColumnIndex(live_location_sharing_db.CONTACTS_TABLE_MATRITCULE_LIVE));
                        String nam = rs.getString(rs.getColumnIndex(live_location_sharing_db.CONTACTS_COLUMN_MESSAGE));
                        String starttime = rs.getString(rs.getColumnIndex(live_location_sharing_db.CONTACTS_COLUMN_START_TIME));
                        System.out.println("The value of i is: "+LiveName);
                        Toasty.info(MoD_Live_Location_sharing_Activity.this,"Live Location : "+Integer.toString(i)+"  : "+LiveName +"  "+ nam+"   "+starttime , Toast.LENGTH_SHORT).show();
                    }


                    // get all  Raw from Table   ...
                    //Cursor  cursor = live_location.rawQuery("select * from table",null);
                    List<LiveTrackingObjet> data = live_location.getAllLocationEvent();
                    for(LiveTrackingObjet leave : data){
                        System.out.println("name live Event :"+leave);
                        System.out.println(" #### ");
                    }
                    rs.close();

                }else{
                    // keine  Event registered ..

                }

            }
        });

        //  action to start  a  configuration (price , personne , ...etc )
        start_aharing = (Button) findViewById(R.id.btn_send);
        start_aharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create new Live Trackingobje

                try{
                    start_aharing.setEnabled(false);
                    // send new live Location objet
                    String Live_EventName = Config.UID_EVENT_LOCATION_LIVE_CHANNEL;
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                    String message_live = Live_Titel.getText().toString();
                    String Id_live_creator = FirebaseAuth.getInstance().getUid().toString();
                    String Active = "true";

                    LiveTrackingObjet Live = new LiveTrackingObjet
                            (Live_EventName
                                    ,timeStamp
                                    ,Selected_options
                                    ,Id_live_creator
                                    ,message_live
                                    ,Active);


                    JSONObject object=new JSONObject();
                    object.put(live_location_sharing_db.CONTACTS_TABLE_MATRITCULE_LIVE ,Live_EventName);
                    object.put(live_location_sharing_db.CONTACTS_COLUMN_START_TIME , timeStamp);
                    object.put(live_location_sharing_db.CONTACTS_COLUMN_START_TIME , Selected_options);
                    object.put(live_location_sharing_db.CONTACTS_COLUMN_ID_USER, Id_live_creator);
                    object.put(live_location_sharing_db.CONTACTS_COLUMN_MESSAGE, message_live);
                    object.put(live_location_sharing_db.CONTACTS_COLUMN_ACTIV, Active);
                    live_location.insert_live_location(Live);



                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",object.toString());
                    setResult(Activity.RESULT_OK,returnIntent);
                    start_aharing.setEnabled(true);
                    finish();


                }catch (Exception ex){
                    Toasty.warning(context, "error Creation  Live Location .. please chechk your internet Connection !", Toast.LENGTH_SHORT).show();
                    ex.printStackTrace();
                }

            }
        });



        Destination_autocompleteFragment = (PlaceAutocompleteFragment)
                this.getFragmentManager().findFragmentById(R.id.fragment_destination);
        Destination_autocompleteFragment.setHint("Destination ...");
        Destination_autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                //destination = place.getName().toString();
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });
        live_location = new live_location_sharing_db(MoD_Live_Location_sharing_Activity.this);
        if (locationTrack.canGetLocation()) {

            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();

            // Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else {
            askPermission();
            //  you must activated settings
            // locationTrack.showSettingsAlert();

            locationTrack.showSettingsAlert();
        }
    }

    private void askPermission() {

        Dexter.withActivity(MoD_Live_Location_sharing_Activity.this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS,


                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.CALL_PHONE


                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.isAnyPermissionPermanentlyDenied()){
                            Toast.makeText(MoD_Live_Location_sharing_Activity.this, "You have denied some permissions permanently, if the app force close try granting permission from settings.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();

    }


    public void InitMarker_andUserInfos(){
        try {

            userHelper = new UserHelper(MoD_Live_Location_sharing_Activity.this);
            Cursor rs = userHelper.getData(1);
            rs.moveToFirst();

            usernam=rs.getString(rs.getColumnIndex(UserHelper.CONTACTS_COLUMN_USERNAME));
            String nam = rs.getString(rs.getColumnIndex(UserHelper.CONTACTS_COLUMN_NAME));
            String emai = rs.getString(rs.getColumnIndex(UserHelper.CONTACTS_COLUMN_EMAIL));
            final String imag_link = rs.getString(rs.getColumnIndex(UserHelper.CONTACTS_COLUMN_IMAGE));
            String loc=rs.getString(rs.getColumnIndex(UserHelper.CONTACTS_COLUMN_LOCATION));
            String bi=rs.getString(rs.getColumnIndex(UserHelper.CONTACTS_COLUMN_BIO));

            try{

                if(usernam!=null){
                    appBarName.setText(nam);
                    appBarSeen.setText("Live Location GuDana");
                }

            }catch (Exception ex){
                rs.close();
                ex.printStackTrace();
            }

            if (!rs.isClosed()) {
                rs.close();
            }
            // get Gps Position  ..
            if (locationTrack.canGetLocation()) {
                RT_gps_position = new LatLng(locationTrack.getLatitude(), locationTrack.getLongitude());
                // Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
            } else {
                locationTrack.showSettingsAlert();
            }

            rs.close();
            Picasso.with(this).load(imag_link).into(target);


        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            MyPosition =  new MarkerOptions()
                    .draggable(true)
                    .title(usernam)
                    .snippet("your realtime position ")
                    //.icon(BitmapDescriptorFactory.fromBitmap( Icon_User))
                    .icon(BitmapDescriptorFactory.fromBitmap(RoundBitmap.getRoundedShape(bitmap,100,100)))
                    .position(RT_gps_position);
            mMap.addMarker(MyPosition);
            repeat = false;
            marker_ok = true; // to tell that the marker was correcttly initialised
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

            Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.user);
            MyPosition =  new MarkerOptions()
                    .draggable(true)
                    .title(usernam)
                    .snippet("your realtime position ")
                    //.icon(BitmapDescriptorFactory.fromBitmap( Icon_User))
                    .icon(BitmapDescriptorFactory.fromBitmap(RoundBitmap.getRoundedShape(icon,100,100)))
                    .position(RT_gps_position);
            mMap.addMarker(MyPosition);
            repeat = false;
            marker_ok = true; // to tell that the marker was correcttly initialised
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };


    @Override
    public void onBackPressed() {

        JSONObject object=new JSONObject();
        try {
            object.put("ismessage",false);
            object.put("data","<b><gray>"+Username+"</b></gray> leaved the group");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.onBackPressed();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {

        Picasso.with(this).cancelRequest(target);
        super.onDestroy();

        JSONObject object=new JSONObject();
        try {
            object.put("ismessage",false);
            object.put("data","<b><gray>"+Username+"</b></gray> leaved the group");
            mMapView.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        buildGoogleApiClient();

        // mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    // call Asnychrone Task  ...
                    // new MyAsyncTask(MoD_Live_Location_sharing_Activity.this, "infos_user").execute();
                    InitMarker_andUserInfos();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }



    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(MoD_Live_Location_sharing_Activity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(getApplicationContext()!=null){
            mLastLocation = location;

            try{
                if(marker_ok){
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    MyPosition.position(new LatLng(location.getLatitude(), location.getLongitude()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                    //mMap.addMarker(MyPosition);
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try {

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
            if(mGoogleApiClient.isConnected()){
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            }

        }catch(Exception ex){

            ex.printStackTrace();

        }

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    final int LOCATION_REQUEST_CODE = 1;
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //mapFragment.getMapAsync(this);


                } else {
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }


    // Asynchrone Task to   load the profile information


    // to start some task in background
    public class MyAsyncTask extends AsyncTask<Void, Void, String> {

        private Context context;
        private Map<String, String> datal;
        private String url_icon_local;

        MyAsyncTask(Context context, String  Map_Info) {
            this.context = context;
            this.url_icon_local = Map_Info;
        }

        @Override
        protected String doInBackground(Void... params) {
            //StartLiveLocationLoop();
            return  "hello";

        }

        @Override
        protected void onPostExecute(String bitmap) {
            super.onPostExecute(bitmap);


        }
    }


}
