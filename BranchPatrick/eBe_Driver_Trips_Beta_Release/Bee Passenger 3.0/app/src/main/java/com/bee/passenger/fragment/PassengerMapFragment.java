package com.bee.passenger.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bee.passenger.HistoryActivity;
import com.bee.passenger.cardview.History_CardView;
import com.bee.passenger.data.FriendDB;
import com.bee.passenger.data.GroupDB;
import com.bee.passenger.fcm.CustomFcm_Util;
import com.bee.passenger.fcm.MainActivity_fcm;
import com.bee.passenger.promotions_swipe.PromotionsActivity;
import com.bee.passenger.service.ServiceUtils;
import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.bee.passenger.CustomerSettingsActivity;

import com.bee.passenger.R;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;
import static java.lang.System.exit;

import android.widget.RadioButton;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.iid.FirebaseInstanceId;


public class PassengerMapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    MapView mMapView;


    private Marker pickupMarker;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private Button mAcceptDriver , mDeclineDriver, mRequest;
    private Boolean requestBol = false;
    private SupportMapFragment mapFragment;
    private String destination = "", position_depart = "",  requestService ="" , requestOptions="" ;
    private LatLng destinationLatLng , position_depart_LatLng;
    private LinearLayout mDriverInfo  ;
    private ImageView mDriverProfileImage;
    private TextView mDriverName, mDriverPhone, mDriverCar;
    private RatingBar mRatingBar;
    private LatLng pickupLocation;
    private ProgressBar progressBar_Search;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static View rootView;


    // attribut Request Setiings ...
    private LinearLayout RequestSettingsLinearLayout;
    private Button request_cancel , mfind_a_rider;
    private EditText TripCost;
    private EditText NumOfPassenger;
    private TextView From_point , ToPoint ;
    private RadioGroup mRadioGroupService , mRadioGroup_Option;

    FloatingActionMenu materialDesignFAM;
    FloatingActionButton DriverInfosDashbord,
                         Promotions,
                         History,
                         float_logout_disconnect , Settings;

    CustomFcm_Util FCM_Message_Sender ;
    PlaceAutocompleteFragment Destination_autocompleteFragment;
    PlaceAutocompleteFragment Depart_autocompleteFragment;
    String DriverID_Fcm = "";


    public PassengerMapFragment() {
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GalleryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PassengerMapFragment newInstance(String param1, String param2) {
        PassengerMapFragment fragment = new PassengerMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try{

            rootView = inflater.inflate(R.layout.activity_costumer_map, container, false);

        }catch(Exception ex){

            ex.printStackTrace();
        }


                mMapView = (MapView) rootView.findViewById(R.id.mapView);
                mMapView.onCreate(savedInstanceState);

                mMapView.onResume(); // needed to get the map to display immediately

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                }else{
                mMapView.getMapAsync(this);
                }

                try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
                } catch (Exception e) {
                e.printStackTrace();
                }



        mMapView.onResume(); // needed to get the map to display immediately

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }else{
            mMapView.getMapAsync(this);
        }

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }



        // init FCM Sender  ....
        FCM_Message_Sender = new CustomFcm_Util();

                From_point = (TextView) rootView.findViewById(R.id.from);
        ToPoint = (TextView) rootView.findViewById(R.id.to_destination_passenger);
        NumOfPassenger = (EditText) rootView.findViewById(R.id.number_of_passenger);


        TripCost = (EditText) rootView.findViewById(R.id.cost_proposition);
        destinationLatLng = new LatLng(0.0,0.0);
        mDriverInfo = (LinearLayout) rootView.findViewById(R.id.driverInfoCustom);
        mDriverInfo.setVisibility(View.GONE);
        RequestSettingsLinearLayout = (LinearLayout) rootView.findViewById(R.id.RequestCustomizer);
        RequestSettingsLinearLayout.setVisibility(View.GONE);

        mDriverProfileImage = (ImageView) rootView.findViewById(R.id.profil_img_driver);
        mDriverName = (TextView) rootView.findViewById(R.id.driver_name_custom);
        mDriverPhone = (TextView) rootView.findViewById(R.id.driverPhone_custom);
        mDriverCar = (TextView) rootView.findViewById(R.id.driver_car_infos);
        mRatingBar = (RatingBar) rootView.findViewById(R.id.ratingBar_custom);
        mRadioGroupService = (RadioGroup) rootView.findViewById(R.id.radioGroup);
        mRadioGroupService.check(R.id.BeeBenSkin);

        mRadioGroup_Option = (RadioGroup) rootView.findViewById(R.id.radioGroup_type);
        mRadioGroup_Option.check(R.id.standart_service);

        mRequest = (Button) rootView.findViewById(R.id.request);
        mAcceptDriver = (Button) rootView.findViewById(R.id.accept);

        // android:indeterminateDrawable="@android:drawable/progress_indeterminate_horizontal"
        // android:indeterminate="true"
        progressBar_Search = (ProgressBar) rootView.findViewById(R.id.progressBarSearchDriver);
        progressBar_Search.setIndeterminate(false);
        // progressBar_Search.setIndeterminateDrawable("@android:drawable/progress_indeterminate_horizontal");



        request_cancel = (Button) rootView.findViewById(R.id.request_cancel);
        request_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Cancel Request  Dialog Box ")
                        .setMessage("Are you sure  ? ... you want to cancel your Request")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(getActivity(), "request Cancelled !!! ..." , Toast.LENGTH_LONG).show();
                                RequestSettingsLinearLayout.setVisibility(View.GONE);
                                RequestSettingsInit();
                                mfind_a_rider.setEnabled(true);
                                mRequest.setEnabled(true);
                                endRide();
                                mRequest.setText("Find a Driver");
                                progressBar_Search.setIndeterminate(false);
                                requestBol = false;

                                // after reinit Firebase

                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        mfind_a_rider = (Button) rootView.findViewById(R.id.find_a_rider);
        mfind_a_rider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(destination.isEmpty()){
                    Toast.makeText(getActivity(), "You must enter a valid Destination ....  " , Toast.LENGTH_LONG).show();
                }else{

                    // disable this button ...
                    mfind_a_rider.setEnabled(false);
                    Toast.makeText(getActivity(), "Find a Driver  ....  " , Toast.LENGTH_LONG).show();
                    ToPoint.setText("to : "+destination.toString());
                    From_point.setText("from : "+position_depart.toString());
                    RequestSettingsLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        });



        mAcceptDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try{
                    Toast.makeText(getActivity(), "you are  accepted this Driver  ....  " , Toast.LENGTH_LONG).show();

                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    HashMap map = new HashMap();
                    map.put("ResponsePassenger" , "true");

                    driverRef.updateChildren(map);
                    mAcceptDriver.setEnabled(false);


                    FCM_Message_Sender.sendWithOtherThread("token" ,
                            DriverID_Fcm ,
                            "eBe Realtime Mobility on Demand 2018" ,
                            "Passenger are accepted your ...");

                }catch(Exception ex){

                }
            }
        });

        mDeclineDriver = (Button) rootView.findViewById(R.id.DeclineDriver);
        mDeclineDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try{
                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    HashMap map = new HashMap();
                    map.put("ResponsePassenger" , "false");

                    driverRef.updateChildren(map);
                    mDriverInfo.setVisibility(View.GONE);
                    RequestSettingsInit();
                    RequestSettingsLinearLayout.setVisibility(View.GONE);
                    mfind_a_rider.setEnabled(true);
                    mRequest.setEnabled(true);
                    endRide();
                    mRequest.setText("Find a Driver");
                    progressBar_Search.setIndeterminate(false);
                    requestBol = false;
                    mDeclineDriver.setEnabled(false);


                    FCM_Message_Sender.sendWithOtherThread("token" ,
                            DriverID_Fcm ,
                            "eBe Realtime Mobility on Demand 2018" ,
                            "Passenger unavaible (notification) ...");

                }catch(Exception ex){

                    ex.printStackTrace();
                }

                Toast.makeText(getActivity(), "you are rejected this Driver....the System find another Driver" , Toast.LENGTH_LONG).show();
            }
        });




        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (requestBol){
                    endRide();
                    progressBar_Search.setIndeterminate(false);

                }else{
                    int selectIdServicesType = mRadioGroupService.getCheckedRadioButtonId();

                    final RadioButton radioButtonServices = (RadioButton) rootView.findViewById(selectIdServicesType);

                    if (radioButtonServices.getText() == null){
                        return;
                    }

                    // get Options of Services ... Smart ... Deluxe and so on ...
                    int selectId_Options = mRadioGroup_Option.getCheckedRadioButtonId();

                    final RadioButton radioButtonOptions = (RadioButton) rootView.findViewById(selectId_Options);

                    if (radioButtonOptions.getText() == null){
                        return;
                    }

                    requestService = radioButtonServices.getText().toString();
                    requestOptions = radioButtonOptions.getText().toString();
                    //  get Price that the Client are proposed ...
                    TripCost.getText().toString();
                    // get number of passenger ...
                    NumOfPassenger.getText().toString();
                    requestBol = true;
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                    pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup Here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));
                    mRequest.setText("the system find your Driver...please wait !");
                    progressBar_Search.setIndeterminate(true);
                    mRequest.setEnabled(false);
                    getClosestDriver();
                }
            }
        });

        Depart_autocompleteFragment = (PlaceAutocompleteFragment)
               getActivity().getFragmentManager().findFragmentById(R.id.place_depart);
        Depart_autocompleteFragment.setHint("Start point ...");
        Depart_autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.

                position_depart =  place.getName().toString();
                position_depart_LatLng = place.getLatLng();
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });
        Destination_autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.fragment_destination);
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
        materialDesignFAM = (FloatingActionMenu) rootView.findViewById(R.id.material_design_android_floating_action_menu);


        DriverInfosDashbord = (FloatingActionButton) rootView.findViewById(R.id.material_design_floating_action_menu_item_request_status);
        Settings = (FloatingActionButton) rootView.findViewById(R.id.material_design_floating_action_menu_item_settings);
        float_logout_disconnect = (FloatingActionButton) rootView.findViewById(R.id.material_design_floating_action_menu_item_logout);
        History =  (FloatingActionButton) rootView.findViewById(R.id.History);
        DriverInfosDashbord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked

                onTokenRefresh();

                if(mDriverInfo.isActivated()==true){
                    mDriverInfo.setActivated(false);
                    mDriverInfo.setVisibility(View.GONE);

                }else{
                    mDriverInfo.setActivated(true);
                    mDriverInfo.setVisibility(View.VISIBLE);
                }


            }
        });


        Settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                // start Rider Status Activity ...
                //
                Intent intent = new Intent(getActivity(), MainActivity_fcm.class);
                startActivity(intent);
                return;

            }
        });

        History.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                // start Rider Status Activity ...
                //

                // Intent intent = new Intent(getActivity(), HistoryActivity.class);
                Intent intent = new Intent(getActivity(), History_CardView.class);
                intent.putExtra("customerOrDriver", "Customers");
                startActivity(intent);
                return;
            }
        });


        float_logout_disconnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                // start Rider Status Activity ...

                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Sign Out Dialog")
                        .setMessage("Are you sure you want to sign out and close the app ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                FriendDB.getInstance(getApplicationContext()).dropDB();
                                GroupDB.getInstance(getApplicationContext()).dropDB();
                                ServiceUtils.stopServiceFriendChat(getApplicationContext(), true);
                                getActivity().finish();

                                try {
                                    Thread.sleep(2000);
                                    exit(0);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
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
        return rootView;
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
        Depart_autocompleteFragment.setText("");
        mfind_a_rider.setEnabled(true);
        mRequest.setEnabled(true);
        mAcceptDriver.setEnabled(true);
        mDeclineDriver.setEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        /*
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.mapView);
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();

       */
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
    private int radius = 1;
    private Boolean driverFound = false;
    private String driverFoundID;

    GeoQuery geoQuery;
    private void getClosestDriver(){
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");

        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();

        if(requestBol == false){
            // get out of this function ...
            return;
        }
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound && requestBol){
                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(key);
                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (driverFound){
                                    return;
                                }

                                if(driverMap.get("service").equals(requestService)){
                                    driverFound = true;
                                    driverFoundID = dataSnapshot.getKey();
                                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    HashMap map = new HashMap();
                                    map.put("customerRideId", customerId);
                                    map.put("destination", destination);
                                    map.put("destinationLat", destinationLatLng.latitude);
                                    map.put("destinationLng", destinationLatLng.longitude);
                                    map.put("CustomerPrice" , TripCost.getText().toString());
                                    map.put("NumPassenger", NumOfPassenger.getText().toString());
                                    map.put("OptionsType", requestOptions);
                                    map.put("Service" , requestService);
                                    map.put("ResponsePassenger" , "");
                                    map.put("ResponseDriver" , "");
                                    map.put("IdFcm" , onTokenRefresh());

                                    driverRef.updateChildren(map);

                                    getDriverLocation();
                                    getDriverInfo();
                                    getHasRideEnded();

                                    // Listener Driver and Customer Response
                                    getResponseDriver();
                                    mRequest.setText("Waiting for Driver Response ....");
                                    // progressBar_Search.setIndeterminate(false);
                                    // RequestSettingsInit();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound)
                {
                    radius++;
                    if(requestBol == false){
                        // get out of this function ...
                        return;
                    }
                    // Toast.makeText(getContext() , "next Search  with Radius : " +radius , Toast.LENGTH_LONG).show();
                    getClosestDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

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
    private void getDriverLocation(){
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driversWorking").child(driverFoundID).child("l");
        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && requestBol){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat,locationLng);
                    if(mDriverMarker != null){
                        mDriverMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);

                    if (distance<100){
                        mRequest.setText("Driver's Here");
                        progressBar_Search.setIndeterminate(false);
                    }else{
                        mRequest.setText("Driver Found: " + String.valueOf(distance));
                        progressBar_Search.setIndeterminate(false);
                    }



                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("your driver ist here !").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    /*-------------------------------------------- getDriverInfo -----
    |  Function(s) getDriverInfo
    |
    |  Purpose:  Get all the user information that we can get from the user's database.
    |
    |  Note: --
    |
    *-------------------------------------------------------------------*/
    private void getDriverInfo(){
        RequestSettingsInit();
        mDriverInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    if(dataSnapshot.child("name")!=null){
                        mDriverName.setText(dataSnapshot.child("name").getValue().toString());
                    }
                    if(dataSnapshot.child("phone")!=null){
                        mDriverPhone.setText(dataSnapshot.child("phone").getValue().toString());
                    }
                    if(dataSnapshot.child("car")!=null){
                        mDriverCar.setText(dataSnapshot.child("car").getValue().toString());
                    }
                    if(dataSnapshot.child("profileImageUrl")!=null){
                        Glide.with(getActivity()).load(dataSnapshot.child("profileImageUrl").getValue().toString()).into(mDriverProfileImage);
                    }

                    if(dataSnapshot.child("IdFcm")!=null){
                        String IdfcmUser = dataSnapshot.child("IdFcm").getValue().toString();
                        DriverID_Fcm = IdfcmUser;
                        FCM_Message_Sender.sendWithOtherThread("token" ,
                                IdfcmUser ,
                                "eBe Realtime Mobility on Demand 2018" ,
                                "Request Passenger Notification ...");
                    }

                    int ratingSum = 0;
                    float ratingsTotal = 0;
                    float ratingsAvg = 0;
                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()){
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    if(ratingsTotal!= 0){
                        ratingsAvg = ratingSum/ratingsTotal;
                        mRatingBar.setRating(ratingsAvg);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getResponseDriver(){

        mDriverInfo.setVisibility(View.VISIBLE);

        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
        driverRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){


                    // customerId = dataSnapshot.getValue().toString();

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    String Res_Driver="" ;
                    String Res_Customer="" ;

                    if(map.get("ResponseDriver") != null){

                        Res_Driver = map.get("ResponseDriver").toString();
                        Toast.makeText(getApplicationContext(), map.get("ResponseDriver").toString() , Toast.LENGTH_LONG).show();
                        if(Res_Driver.equalsIgnoreCase("false")){
                            mDriverInfo.setVisibility(View.GONE);
                            RequestSettingsInit();



                        }

                    }

                    if(map.get("ResponsePassenger") != null){

                        Res_Customer = map.get("ResponsePassenger").toString();
                        Toast.makeText(getApplicationContext(), map.get("ResponsePassenger").toString() , Toast.LENGTH_LONG).show();
                        if(Res_Customer.equalsIgnoreCase("false")){
                            mDriverInfo.setVisibility(View.GONE);
                            RequestSettingsInit();
                        }

                    }

                    if(Res_Customer.equalsIgnoreCase("true") || (Res_Driver.equalsIgnoreCase("true"))){
                        // Start with the trip

                        Toast.makeText(getApplicationContext(), "positive Response for a Driver and Customer " , Toast.LENGTH_LONG).show();
                        //getAssignedCustomer();
                        // mRideStatus.setBackgroundColor(mRideStatus.getContext().getResources().getColor(R.color.green));
                    }else{
                        // find the Next Available Driver ...

                        Toast.makeText(getApplicationContext(), "Negative Response of the  Driver or Customer ", Toast.LENGTH_LONG).show();

                        // DeleteProposition

                    }


                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;
    private void getHasRideEnded(){
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest").child("customerRideId");
        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                }else{
                    endRide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void endRide(){
        requestBol = false;
        try{
            geoQuery.removeAllListeners();
            driverLocationRef.removeEventListener(driverLocationRefListener);
            driveHasEndedRef.removeEventListener(driveHasEndedRefListener);
        }catch(Exception ex){

            ex.printStackTrace();
        }

        if (driverFoundID != null){
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
            driverRef.removeValue();
            driverFoundID = null;

        }
        driverFound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if (mDriverMarker != null){
            mDriverMarker.remove();
        }
        mRequest.setText("Find Driver");
        progressBar_Search.setIndeterminate(false);

        mDriverInfo.setVisibility(View.GONE);
        mDriverName.setText("");
        mDriverPhone.setText("");
        mDriverCar.setText("Destination: --");
        mDriverProfileImage.setImageResource(R.mipmap.ic_default_user);
    }

    /*-------------------------------------------- Map specific functions -----
    |  Function(s) onMapReady, buildGoogleApiClient, onLocationChanged, onConnected
    |
    |  Purpose:  Find and update user's location.
    |
    |  Note:
    |	   The update interval is set to 1000Ms and the accuracy is set to PRIORITY_HIGH_ACCURACY,
    |      If you're having trouble with battery draining too fast then change these to lower values
    |
    |
    *-------------------------------------------------------------------*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
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

            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
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

        // Toast.makeText(getContext() , refreshedToken.toString() , Toast.LENGTH_LONG).show();
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        // sendRegistrationToServer(refreshedToken);

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
