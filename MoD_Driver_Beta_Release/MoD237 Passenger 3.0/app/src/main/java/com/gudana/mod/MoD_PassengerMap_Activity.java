package com.gudana.mod;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.sac.Ack;
import io.github.sac.BasicListener;
import io.github.sac.Emitter;
import io.github.sac.ReconnectStrategy;
import io.github.sac.Socket;

import static com.facebook.FacebookSdk.getApplicationContext;
import static java.lang.System.exit;


public class MoD_PassengerMap_Activity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    MapView mMapView;

    // socket cluster attribut

    String url="ws://35.237.206.152:8000/socketcluster/";
    String SocketClusterChannel = "GuDana-Location-Sharing+random_name";
    Socket socket;
    Handler Typinghandler=new Handler();
    String Username="Demo";
    String UserType = "sub"; // That means connected als subscriber (sub) or publischer (pub)


    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private Button mAcceptDriver , mDeclineDriver, mRequest , passenger_response , driver_response;
    private Boolean requestBol = false;
    private SupportMapFragment mapFragment;
    private String destination = "", position_depart = "",  requestService ="" , requestOptions="" ;
    private LatLng destinationLatLng , position_depart_LatLng;
    private LinearLayout mDriverInfo  ;
    private ImageView mDriverProfileImage;
    private TextView mDriverName, mDriverPhone, mDriverCar;
    private RatingBar mRatingBar;
    private ProgressBar progressBar_Search;

    // attribut Request Setiings ...
    private LinearLayout RequestSettingsLinearLayout;
    private Button request_cancel , mfind_a_rider;
    private EditText TripCost;
    private EditText NumOfPassenger;
    private TextView From_point , ToPoint ;
    private RadioGroup mRadioGroupService , mRadioGroup_Option;

    FloatingActionMenu materialDesignFAM;
    FloatingActionButton DriverInfosDashbord,
                         History,
                         float_logout_disconnect , Settings;

    PlaceAutocompleteFragment Destination_autocompleteFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_costumer_map);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle(R.string.mod_gudana_live_sharing);

        // init Socket Cluster

        socket = new Socket(url);
        socket.setListener(new BasicListener() {

            public void onConnected(Socket socket, Map<String, List<String>> headers) {
                socket.createChannel(SocketClusterChannel).subscribe(new Ack() {
                    @Override
                    public void call(String name, Object error, Object data) {
                        if (error==null){
                            Log.i ("Success","subscribed to channel "+name);
                            Toast.makeText(MoD_PassengerMap_Activity.this, "subscribed to channel "+name, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                Log.i("Success ","Connected to endpoint");
                Toast.makeText(MoD_PassengerMap_Activity.this, "Connected to endpoint", Toast.LENGTH_SHORT).show();
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
        socket.onSubscribe(SocketClusterChannel,new Emitter.Listener() {
            @Override
            public void call(String name, final Object data) {

                try {
                    JSONObject object= (JSONObject) data;
                    Toast.makeText(MoD_PassengerMap_Activity.this, ((JSONObject) data).toString(), Toast.LENGTH_SHORT).show();
                    if (object.opt("istyping")!=null && !object.getString("user").equals(Username)){
                        if (object.getBoolean("istyping")){


                        }else{

                        }

                    }else {
                        if (!object.getBoolean("ismessage")) {

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        //
        passenger_response = (Button) findViewById(R.id.passenger_response);
        driver_response  = (Button)  findViewById(R.id.driver_response);

        From_point = (TextView) findViewById(R.id.from);
        ToPoint = (TextView) findViewById(R.id.to_destination_passenger);
        NumOfPassenger = (EditText) findViewById(R.id.number_of_passenger);


        TripCost = (EditText) findViewById(R.id.cost_proposition);
        destinationLatLng = new LatLng(0.0,0.0);
        mDriverInfo = (LinearLayout) findViewById(R.id.driverInfoCustom);
        mDriverInfo.setVisibility(View.GONE);
        RequestSettingsLinearLayout = (LinearLayout) findViewById(R.id.RequestCustomizer);
        RequestSettingsLinearLayout.setVisibility(View.GONE);

        mDriverProfileImage = (ImageView) findViewById(R.id.profil_img_driver);
        mDriverName = (TextView) findViewById(R.id.driver_name_custom);
        mDriverPhone = (TextView) findViewById(R.id.driverPhone_custom);
        mDriverCar = (TextView) findViewById(R.id.driver_car_infos);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar_custom);
        mRadioGroupService = (RadioGroup) findViewById(R.id.radioGroup);
        mRadioGroupService.check(R.id.BeeBenSkin);

        mRadioGroup_Option = (RadioGroup) findViewById(R.id.radioGroup_type);
        mRadioGroup_Option.check(R.id.standart_service);

        mRequest = (Button) findViewById(R.id.request);
        mAcceptDriver = (Button) findViewById(R.id.accept);

        // android:indeterminateDrawable="@android:drawable/progress_indeterminate_horizontal"
        // android:indeterminate="true"
        progressBar_Search = (ProgressBar) findViewById(R.id.progressBarSearchDriver);
        progressBar_Search.setIndeterminate(false);
        // progressBar_Search.setIndeterminateDrawable("@android:drawable/progress_indeterminate_horizontal");


        //  action to start  a  configuration (price , personne , ...etc )
        mfind_a_rider = (Button) findViewById(R.id.find_a_rider);
        mfind_a_rider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                JSONObject object=new JSONObject();
                try {
                    object.put("user",Username);
                    object.put("ismessage",true);
                    object.put("data","latidute + longitude");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.getChannelByName(SocketClusterChannel).publish(object, new Ack() {
                    @Override
                    public void call(String name, Object error, Object data) {
                        if (error==null){
                            Log.i ("Success","Publish sent successfully");
                        }
                    }
                });

            }
        });



        Destination_autocompleteFragment = (PlaceAutocompleteFragment)
                this.getFragmentManager().findFragmentById(R.id.fragment_destination);
        Destination_autocompleteFragment.setHint("Destination ...");
        Destination_autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                destination = place.getName().toString();
                destinationLatLng = place.getLatLng();
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });
        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);


        DriverInfosDashbord = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item_request_status);
        Settings = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item_settings);
        float_logout_disconnect = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item_logout);
        History =  (FloatingActionButton) findViewById(R.id.History);
        DriverInfosDashbord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked

                // onTokenRefresh();


                //startActivity(new Intent(MoD_PassengerMap_Activity.this, MovingMarkerActivity.class));
                // ActivateTimerProgressbar(true);

            }
        });


        Settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


            }
        });

        History.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


            }
        });


        float_logout_disconnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                // start Rider Status Activity ...

                new AlertDialog.Builder(MoD_PassengerMap_Activity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Sign Out Dialog")
                        .setMessage("Are you sure you want to sign out and close the app ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // FirebaseAuth.getInstance().signOut();

                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

                return;

            }
        });

        // Init Request settings
        onTokenRefresh();
        RequestSettingsInit();
    }

    @Override
    public void onBackPressed() {

        JSONObject object=new JSONObject();
        try {
            object.put("ismessage",false);
            object.put("data","<b><gray>"+Username+"</b></gray> leaved the group");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.getChannelByName(SocketClusterChannel).publish(object);

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

    public void RequestSettingsInit(){

        mDriverInfo.setActivated(false);
        NumOfPassenger.setText("1");
        // Init Cost Trip  ...
        TripCost.setText("0");
        // init destination and location
        From_point.setText("from : ");
        ToPoint.setText("To : ");
        destination = "";
        position_depart = "";
        requestService ="" ;
        requestOptions="" ;
        requestBol = false;
        progressBar_Search.setIndeterminate(false);
        RequestSettingsLinearLayout.setVisibility(View.GONE);
        Destination_autocompleteFragment.setText("");
        // Depart_autocompleteFragment.setText("");
        mfind_a_rider.setEnabled(true);
        mRequest.setEnabled(true);

        passenger_response.setBackgroundColor(getResources().getColor(R.color.accent));
        passenger_response.setText(R.string.wait_your_response);

        driver_response.setBackgroundColor(getResources().getColor(R.color.accent));
        driver_response.setText(R.string.wait_driver_response);
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

        super.onDestroy();

        JSONObject object=new JSONObject();
        try {
            object.put("ismessage",false);
            object.put("data","<b><gray>"+Username+"</b></gray> leaved the group");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.getChannelByName(SocketClusterChannel).publish(object);
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    private int radius = 1;
    private Boolean driverFound = false;
    private String driverFoundID;

    GeoQuery geoQuery;


    /*-------------------------------------------- Map specific functions -----
    |  Function(s) getDriverLocation
    |
    |  Purpose:  Get's most updated driver location and it's always checking for movements.
    |
    |  Note:
    |	   Even tho we used geofire to push the location of the driver we can use a normal
    |      Listener to get it's location with no problem.
    |
    |      0 -> Latitude
    |      1 -> Longitudde
    |
    *-------------------------------------------------------------------*/
    private Marker mDriverMarker;
    private DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        buildGoogleApiClient();

        // mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }



    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(MoD_PassengerMap_Activity.this)
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

            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
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
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

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


    public String onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token Instance", "Refreshed token: " + refreshedToken);

        try{
            String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers/"+ customerId);
            HashMap map = new HashMap();
            map.put("IdFcm" , refreshedToken);

            driverRef.updateChildren(map);

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return refreshedToken;
    }

    /*-------------------------------------------- onRequestPermissionsResult -----
    |  Function onRequestPermissionsResult
    |
    |  Purpose:  Get permissions for our app if they didn't previously exist.
    |
    |  Note:
    |	requestCode: the nubmer assigned to the request that we've made. Each
    |                request has it's own unique request code.
    |
    *-------------------------------------------------------------------*/
    final int LOCATION_REQUEST_CODE = 1;
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mapFragment.getMapAsync(this);


                } else {
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

}
