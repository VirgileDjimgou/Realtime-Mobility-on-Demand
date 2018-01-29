package com.bee.drive.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bee.drive.HistoryActivity;
import com.bee.drive.Utility.ImageUtils;
import com.bee.drive.activity.MainActivity;
import com.bee.drive.airbnbmapexample.ui.activity.MovingMarkerActivity;
import com.bee.drive.data.SharedPreferenceHelper;
import com.bee.drive.data.StaticConfig;
import com.bee.drive.fcm.MainActivity_fcm;
import com.bee.drive.model.User;
import com.bumptech.glide.Glide;
import com.bee.drive.data.FriendDB;
import com.bee.drive.data.GroupDB;
import com.bee.drive.service.ServiceUtils;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.bee.drive.R;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bee.drive.activity.AboutUsActivity;

import static com.facebook.FacebookSdk.getApplicationContext;

public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, RoutingListener {

    MapView mMapView;
    private GoogleMap googleMap;

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    FloatingActionMenu materialDesignFAM;
    FloatingActionButton Dasboard , History , Adv_Settings, Road_Simulation, About, logout_disconnect;

    private Button mRideStatus;
    private Switch mWorkingSwitch;
    private int status = 0;
    private String customerId = "", destination;
    private LatLng destinationLatLng, pickupLatLng;
    private float rideDistance;
    private Boolean isLoggingOut = false;
    private SupportMapFragment mapFragment;
    private LinearLayout mCustomerInfo;
    private ImageView mCustomerProfileImage;
    private TextView mtrip_cost , mnbOfPassengers , mcustomer_Service ,
                     mServicesOptions, mCustomerName, mCustomerDestination;


    private Button mButtonDeclineDrive , mButtonAcceptDrive ;
    private DatabaseReference userDB;
    private FirebaseAuth mAuth;
    ImageView avatar;
    private Context context;
    // get price proposed by the customers  ...
    Double CustomerPrice =0.0;
    String NumberOfPassengers = "";
    String Type_of_Services ="";
    String Options= "";
    private User myAccount;
    private ProgressBar progressBar;
    private MyCountDownTimer myCountDownTimer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        polylines = new ArrayList<>();

        userDB = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(StaticConfig.UID);

        View rootView = inflater.inflate(R.layout.activity_driver_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        context = rootView.getContext();
        SharedPreferenceHelper prefHelper = SharedPreferenceHelper.getInstance(getApplicationContext());
        myAccount = prefHelper.getUserInfo();

        mMapView.onResume(); // needed to get the map to display immediately

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }else{
            mMapView.getMapAsync(this);
        }

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // add old Activity

        //counter to take  a decisions ...
        progressBar = (ProgressBar) rootView.findViewById(R.id.CounterDriver);


        mCustomerInfo = (LinearLayout) rootView .findViewById(R.id.PassengerInfoCustom);
        // always mask this Layout  after the creation ....
        mCustomerInfo.setVisibility(View.GONE);

        mtrip_cost = (TextView) rootView.findViewById(R.id.trip_cost);
        mcustomer_Service = (TextView) rootView.findViewById(R.id.customer_Service);
        mnbOfPassengers = (TextView) rootView.findViewById(R.id.nbOfPassengers);
        mServicesOptions = (TextView) rootView.findViewById(R.id.ServicesOptions);


        mButtonDeclineDrive  = (Button) rootView.findViewById(R.id.btn_decline_drive) ;
        mButtonDeclineDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Passenger refused ", Toast.LENGTH_SHORT).show();
                // mask the  Dasboard ...
                try{
                String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest");
                // Set the  Driver Response to true ...
                HashMap map = new HashMap();
                map.put("ResponseDriver" , "false");
                driverRef.updateChildren(map);
                mCustomerInfo.setVisibility(View.GONE);

                    endRide();
                }catch(Exception ex){
                    ex.printStackTrace();
                }

            }
        });



        mButtonAcceptDrive = (Button)  rootView.findViewById(R.id.btn_accept_passenger) ;
        mButtonAcceptDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Passenger accepted ", Toast.LENGTH_SHORT).show();

                try{

                    String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest");
                    // Set the  Driver Response to true ...
                    HashMap map = new HashMap();
                    map.put("ResponseDriver" , "true");
                    driverRef.updateChildren(map);
                    mButtonAcceptDrive.setEnabled(false);
                }catch(Exception ex){
                    ex.printStackTrace();
                }


                // Start  the Triggers  to begin with the Trips  ...
                // getAssignedCustomer();

            }
        });



        mCustomerProfileImage = (ImageView) rootView.findViewById(R.id.profil_img_customer);

        mCustomerName = (TextView) rootView .findViewById(R.id.name_customer);
        // mCustomerPhone = (TextView) rootView .findViewById(R.id.customer_phone);
        mCustomerDestination = (TextView) rootView .findViewById(R.id.customer_destination);

        mWorkingSwitch = (Switch) rootView .findViewById(R.id.workingSwitch);
        mWorkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    connectDriver();
                }else{
                    disconnectDriver();
                }
            }
        });

        mRideStatus = (Button) rootView.findViewById(R.id.rideStatus);
        mRideStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                status = 2;
                RiderStatus_check();
            }
        });



        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;


                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //User has previously accepted this permission
                    if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(true);
                    }
                } else {
                    //Not in api-23, no need to prompt
                    googleMap.setMyLocationEnabled(true);
                }


                // googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(-34, 151);
                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });


        materialDesignFAM = (FloatingActionMenu) rootView.findViewById(R.id.material_design_android_floating_action_menu);
        Dasboard = (FloatingActionButton) rootView.findViewById(R.id.customerDashbord);
        History = (FloatingActionButton) rootView.findViewById(R.id.History);
        Road_Simulation = (FloatingActionButton) rootView.findViewById(R.id.menu_road_simulation);
        Adv_Settings = (FloatingActionButton) rootView.findViewById(R.id.menu_item_settings);
        logout_disconnect = (FloatingActionButton) rootView.findViewById(R.id.menu_item_logout);
        About = (FloatingActionButton) rootView.findViewById(R.id.menu_item_about);


        Dasboard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked

                // launch new intent instead of loading fragment

                if(mCustomerInfo.isActivated() == true){
                    mCustomerInfo.setVisibility(View.GONE);
                    mCustomerInfo.setActivated(false);

                }else{


                    mCustomerInfo.setVisibility(View.VISIBLE);
                    mCustomerInfo.setActivated(true);

                }
            }
        });



        History.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                intent.putExtra("customerOrDriver", "Drivers");
                startActivity(intent);
                return;

            }
        });

        Road_Simulation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked

                // launch new intent instead of loading fragment
                // startActivity(new Intent(getActivity(), MovingMarkerActivity.class));
                ActivateTimerProgressbar(true);


            }
        });
        Adv_Settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                // start Rider Status Activity ...
                //
                startActivity(new Intent(getActivity(), MainActivity_fcm.class));

            }
        });
        logout_disconnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu third item clicked

                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Sign Out Dialog")
                        .setMessage("Are you sure you want to sign out and close the app ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                isLoggingOut = true;

                                disconnectDriver();
                                FirebaseAuth.getInstance().signOut();
                                FriendDB.getInstance(getApplicationContext()).dropDB();
                                GroupDB.getInstance(getApplicationContext()).dropDB();
                                ServiceUtils.stopServiceFriendChat(getApplicationContext(), true);
                                getActivity().finish();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                return;

            }
        });

        About.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                // start Rider Status Activity ...
                //
                startActivity(new Intent(getActivity(), AboutUsActivity.class));

            }
        });


        getCustomerProposition();
        return rootView;
    }

    private void ActivateTimerProgressbar(boolean timer){

        if(timer = true){

            try {
                progressBar.setProgress(500);
                myCountDownTimer = new MyCountDownTimer(50000, 100);
                myCountDownTimer.start();

            }catch (Exception ex ){
                ex.printStackTrace();
            }

        }else{

        }
    }
    private void InitViewDashboard(){
        mButtonAcceptDrive.setEnabled(true);
        mButtonDeclineDrive.setEnabled(true);
    }

    private void RiderStatus_check(){

        try{

            switch(status){
                case 1:
                    // prepare the Trip ...
                    // and prind the direction on map ...
                    status=2;
                    erasePolylines();
                    if(destinationLatLng.latitude!=0.0 && destinationLatLng.longitude!=0.0){
                        getRouteToMarker(destinationLatLng);
                    }
                    mRideStatus.setText("Terminate your Ride ");

                    break;
                case 2:
                    recordRide();
                    endRide();
                    break;
            }

        }catch(Exception ex){
            ex.printStackTrace();

        }

    }


    public void getCustomerProposition(){

        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference proposalCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest");
        proposalCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if(dataSnapshot.exists()) {

                    // customerId = dataSnapshot.getValue().toString();
                    mCustomerInfo.setVisibility(View.GONE);
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("destination")!=null){
                        destination = map.get("destination").toString();
                        mCustomerDestination.setText("Destination: " + destination);
                    }
                    else{
                        mCustomerDestination.setText("Destination: --");
                    }

                    Double destinationLat = 0.0;
                    Double destinationLng = 0.0;
                    if(map.get("destinationLat") != null){
                        destinationLat = Double.valueOf(map.get("destinationLat").toString());
                    }
                    if(map.get("destinationLng") != null){
                        destinationLng = Double.valueOf(map.get("destinationLng").toString());
                        destinationLatLng = new LatLng(destinationLat, destinationLng);
                    }


                    if(map.get("OptionsType") != null){
                        Options = map.get("OptionsType").toString();
                        mServicesOptions.setText(map.get("OptionsType").toString());

                    }

                    if(map.get("Service") !=null ){
                        Type_of_Services = map.get("Service").toString();
                        mcustomer_Service.setText(map.get("Service").toString());
                    }

                    if(map.get("NumPassenger") !=null){
                        NumberOfPassengers = map.get("NumPassenger").toString();
                        mnbOfPassengers.setText(map.get("NumPassenger").toString());

                    }

                    // get  numbers of passengers    ...that  shoul'd be max  4 passengers for taxi services  ..
                    // and two passengers  for moto-Taxi Services ...

                    if(map.get("CustomerPrice") != null){
                        CustomerPrice = Double.valueOf(map.get("CustomerPrice").toString());
                        mtrip_cost.setText(map.get("CustomerPrice").toString());

                    }

                    // set info about the propositions of the Customers  ..

                    if(map.get("customerRideId") != null){

                        customerId = map.get("customerRideId").toString();
                        mCustomerInfo.setVisibility(View.VISIBLE);
                        getProfil_Proposed_Customers();
                        ActivateTimerProgressbar(true);
                    }


                    String Res_Driver="" ;
                    String Res_Customer="" ;

                    if(map.get("ResponseDriver") != null){

                        Res_Driver = map.get("ResponseDriver").toString();
                        Toast.makeText(getApplicationContext(), map.get("ResponseDriver").toString() , Toast.LENGTH_LONG).show();
                        if(Res_Driver.equalsIgnoreCase("false")){
                            mCustomerInfo.setVisibility(View.GONE);
                            InitViewDashboard();
                            try{
                                endRide();
                                status = 2;
                                RiderStatus_check();

                            }catch(Exception ex){
                                ex.printStackTrace();
                            }

                        }

                    }

                    if(map.get("ResponsePassenger") != null){

                        Res_Customer = map.get("ResponsePassenger").toString();
                        Toast.makeText(getApplicationContext(), map.get("ResponsePassenger").toString() , Toast.LENGTH_LONG).show();
                        if(Res_Customer.equalsIgnoreCase("false")){
                            mCustomerInfo.setVisibility(View.GONE);
                            try{
                                endRide();
                                InitViewDashboard();
                                status = 2;
                                RiderStatus_check();
                            }catch(Exception ex){
                                ex.printStackTrace();
                            }                        }

                    }

                    if(Res_Customer.equalsIgnoreCase("true") && (Res_Driver.equalsIgnoreCase("true"))){
                        // Start with the trip

                        Toast.makeText(getApplicationContext(), "positive Response for a Driver and Customer " , Toast.LENGTH_LONG).show();
                        getAssignedCustomer();
                        mRideStatus.setBackgroundColor(mRideStatus.getContext().getResources().getColor(R.color.green));
                        mRideStatus.setText("Terminate your Ride here !");
                    }


                }


            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e("getAssignedCustomer" , databaseError.toString());
                Toast.makeText(context , "getAssignedCustomer  : " +databaseError.toString() , Toast.LENGTH_LONG).show();
            }
        });



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
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    private void getAssignedCustomer(){
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest").child("customerRideId");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                try{

                    if(dataSnapshot.exists()){
                        status = 1;
                        RiderStatus_check();
                        customerId = dataSnapshot.getValue().toString();
                        getAssignedCustomerPickupLocation();
                        getAssignedCustomerDestination_and_offers();
                        getAssignedCustomerInfo();
                        Toast.makeText(context, "Assigned  Customer  ...." , Toast.LENGTH_LONG).show();
                    }else{
                        endRide();
                        status = 2 ;
                        RiderStatus_check();
                    }

                }catch(Exception ex){
                    Log.e("getAssignedCustomer" , ex.toString());
                    ex.printStackTrace();
                    Toast.makeText(context, "getAssignedCustomer  : " +ex.toString(), Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e("getAssignedCustomer" , databaseError.toString());
                Toast.makeText(context , "getAssignedCustomer  : " +databaseError.toString() , Toast.LENGTH_LONG).show();
            }
        });
    }

    Marker pickupMarker;
    private DatabaseReference assignedCustomerPickupLocationRef;
    private ValueEventListener assignedCustomerPickupLocationRefListener;
    private void getAssignedCustomerPickupLocation(){
        assignedCustomerPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerId).child("l");
        assignedCustomerPickupLocationRefListener = assignedCustomerPickupLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && !customerId.equals("")){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    pickupLatLng = new LatLng(locationLat,locationLng);
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("pickup location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));
                    getRouteToMarker(pickupLatLng);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    // trhought button Decline and Timer end  ....
    private void declineCustomersOffers(){

        try{

            Toast.makeText(context, "Client Offer declined ... ", Toast.LENGTH_SHORT).show();
            erasePolylines();

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("customerRequest");
            driverRef.removeValue();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
            GeoFire geoFire = new GeoFire(ref);
            geoFire.removeLocation(customerId);
            customerId="";
            rideDistance = 0;

            if(pickupMarker != null){
                pickupMarker.remove();
            }
            if (assignedCustomerPickupLocationRefListener != null){
                assignedCustomerPickupLocationRef.removeEventListener(assignedCustomerPickupLocationRefListener);
            }
            mCustomerInfo.setVisibility(View.GONE);
            mCustomerName.setText("");
            // mCustomerPhone.setText("");
            mCustomerDestination.setText("Destination: --");
            mCustomerProfileImage.setImageResource(R.mipmap.ic_default_user);


        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(context , "endRide " + ex.toString(), Toast.LENGTH_LONG).show();
        }



    }


    private void AcceptCustomersOffers(){

    }

    private void getProfil_Proposed_Customers(){


        // get Infos Profil Customers  ....

        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){


                    try{
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if(map.get("name")!=null){
                            mCustomerName.setText(map.get("name").toString());
                        }
                        if(map.get("phone")!=null){
                            // mCustomerPhone.setText(map.get("phone").toString());
                            Toast.makeText(getContext(), map.get("phone").toString(), Toast.LENGTH_LONG).show();
                        }
                        if(map.get("profileImageUrl")!=null){


                            Glide.with(getActivity()).load(map.get("profileImageUrl").toString()).into(mCustomerProfileImage);
                        }


                    }catch (Exception ex){
                        ex.printStackTrace();
                        Toast.makeText(context, ex.toString() , Toast.LENGTH_LONG).show();

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void getRouteToMarker(LatLng pickupLatLng) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), pickupLatLng)
                .build();
        routing.execute();
    }

    private void getAssignedCustomerDestination_and_offers(){
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest");
        assignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Toast.makeText(context, "Customer Offer  ...." , Toast.LENGTH_LONG).show();

                if(dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("destination")!=null){
                        destination = map.get("destination").toString();
                        mCustomerDestination.setText("Destination: " + destination);
                    }
                    else{
                        mCustomerDestination.setText("Destination: --");
                    }

                    Double destinationLat = 0.0;
                    Double destinationLng = 0.0;
                    if(map.get("destinationLat") != null){
                        destinationLat = Double.valueOf(map.get("destinationLat").toString());
                    }
                    if(map.get("destinationLng") != null){
                        destinationLng = Double.valueOf(map.get("destinationLng").toString());
                        destinationLatLng = new LatLng(destinationLat, destinationLng);
                    }


                    if(map.get("OptionsType") != null){
                        Options = map.get("OptionsType").toString();
                        mServicesOptions.setText(map.get("OptionsType").toString());

                    }

                    if(map.get("Service") !=null ){
                        Type_of_Services = map.get("Service").toString();
                        mcustomer_Service.setText(map.get("Service").toString());
                    }

                    if(map.get("NumPassenger") !=null){
                        NumberOfPassengers = map.get("NumPassenger").toString();
                        mnbOfPassengers.setText(map.get("NumPassenger").toString());

                    }

                    // get  numbers of passengers    ...that  shoul'd be max  4 passengers for taxi services  ..
                    // and two passengers  for moto-Taxi Services ...

                    if(map.get("CustomerPrice") != null){
                        CustomerPrice = Double.valueOf(map.get("CustomerPrice").toString());
                        mtrip_cost.setText(map.get("CustomerPrice").toString());

                    }

                    // set info about the propositions of the Customers  ..

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void getAssignedCustomerInfo(){

        mCustomerInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){

                    Toast.makeText(context, "Customer Infos ...." , Toast.LENGTH_LONG).show();

                    try{
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if(map.get("name")!=null){
                            mCustomerName.setText(map.get("name").toString());
                        }
                        if(map.get("phone")!=null){
                            // mCustomerPhone.setText(map.get("phone").toString());
                        }
                        if(map.get("profileImageUrl")!=null){


                            Glide.with(getActivity()).load(map.get("profileImageUrl").toString()).into(mCustomerProfileImage);
                        }


                    }catch (Exception ex){
                        ex.printStackTrace();
                        Toast.makeText(context, ex.toString() , Toast.LENGTH_LONG).show();

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });




    }


    private void endRide(){

        try{

            mRideStatus.setText("picked customer");
            erasePolylines();

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("customerRequest");
            driverRef.removeValue();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
            GeoFire geoFire = new GeoFire(ref);
            geoFire.removeLocation(customerId);
            customerId="";
            rideDistance = 0;

            if(pickupMarker != null){
                pickupMarker.remove();
            }
            if (assignedCustomerPickupLocationRefListener != null){
                assignedCustomerPickupLocationRef.removeEventListener(assignedCustomerPickupLocationRefListener);
            }
            mCustomerInfo.setVisibility(View.GONE);
            mCustomerName.setText("");
            // mCustomerPhone.setText("");
            mCustomerDestination.setText("Destination: --");
            mRideStatus.setBackgroundColor(mRideStatus.getContext().getResources().getColor(R.color.white));
            mRideStatus.setText("");
            mCustomerProfileImage.setImageResource(R.mipmap.ic_default_user);


        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(context , "endRide " + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void recordRide(){

        try{


            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("history");
            DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("history");
            DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");
            String requestId = historyRef.push().getKey();
            driverRef.child(requestId).setValue(true);
            customerRef.child(requestId).setValue(true);

            HashMap map = new HashMap();
            map.put("driver", userId);
            map.put("customer", customerId);
            map.put("rating", 0);
            map.put("timestamp", getCurrentTimestamp());
            map.put("destination", destination);
            map.put("location/from/lat", pickupLatLng.latitude);
            map.put("location/from/lng", pickupLatLng.longitude);
            map.put("location/to/lat", destinationLatLng.latitude);
            map.put("location/to/lng", destinationLatLng.longitude);
            map.put("distance", rideDistance);
            historyRef.child(requestId).updateChildren(map);


        }catch(Exception ex){

            ex.printStackTrace();
        }
    }

    private Long getCurrentTimestamp() {
        Long timestamp = System.currentTimeMillis()/1000;
        return timestamp;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(getContext()!=null){

            try{

                customerId = customerId;

                if(!customerId.equals("")){
                    rideDistance += mLastLocation.distanceTo(location)/1000;
                }


            }catch(Exception ex){
                mCustomerInfo.setVisibility(View.GONE);
                Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }


                mLastLocation = location;
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("driversAvailable");
                DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("driversWorking");
                GeoFire geoFireAvailable = new GeoFire(refAvailable);
                GeoFire geoFireWorking = new GeoFire(refWorking);

                switch (customerId){
                    case "":
                        geoFireWorking.removeLocation(userId);
                        geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                        break;

                    default:
                        geoFireAvailable.removeLocation(userId);
                        geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                        break;
                }



        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void connectDriver(){
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Toast.makeText(this.getContext(), "Driver connected with Firebase db  ...." , Toast.LENGTH_LONG).show();
    }

    private void disconnectDriver(){

        try{
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        }catch (Exception ex ){
            ex.printStackTrace();
            Toast.makeText(this.getContext(), ex.toString() , Toast.LENGTH_LONG).show();

        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
        Toast.makeText(this.getContext(), "Driver disconnected with Firebase db ...." , Toast.LENGTH_LONG).show();

    }


    final int LOCATION_REQUEST_CODE = 1;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mapFragment.getMapAsync(this);
                } else{
                    Toast.makeText(getContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }



    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getContext(), "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRoutingStart() {
    }
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onRoutingCancelled() {
    }
    private void erasePolylines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }

     // Profil image ...



    // countdown Timer

    public class MyCountDownTimer extends CountDownTimer {

        String valconverted ;
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (millisUntilFinished/500);
            valconverted = String.valueOf(millisUntilFinished);
            progressBar.setProgress(progress);
            // Toast.makeText(getContext(), valconverted  , Toast.LENGTH_SHORT);
        }

        @Override
        public void onFinish() {
            progressBar.setProgress(0);
            // hier i muss end the  Offers
            try{
                mCustomerInfo.setVisibility(View.GONE);
                InitViewDashboard();
                endRide();
                status = 2;
                RiderStatus_check();

            }catch(Exception ex){

                ex.printStackTrace();
            }
        }

    }


}
