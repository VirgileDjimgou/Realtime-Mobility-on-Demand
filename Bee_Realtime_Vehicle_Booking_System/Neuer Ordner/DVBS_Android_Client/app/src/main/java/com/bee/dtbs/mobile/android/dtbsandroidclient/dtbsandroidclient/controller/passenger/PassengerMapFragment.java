package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.passenger;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.formater.time.TimeFormatter;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Location;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Taxi;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.communication.event.MessageEventBus;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.config.DtbsPreferences;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.cache.AllBookings;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.cache.AllTaxis;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.formater.time.HourMinutesSecondsFormatter;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.gps.GpsLocationListener;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.network.NetworkMonitor;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.tasks.CheckActiveBookingAsyncTask;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;

/**
 * Controller for managing a passengers interaction with the map.
 * Handles all operations related to updating of taxis and a user's location.
 *
 * @author robertnorthard
 */
public class PassengerMapFragment extends Fragment implements Observer {

    // TAG used for logging.
    private static final String TAG = PassengerMapFragment.class.getName();

    /**
     * Constants for UI settings.
     */
    private static final int CAMERA_ZOOM_LEVEL = 14;
    private static final int CAMERA_ANIMATE_DURATION = 2000;

    private MapView mapView;
    private GoogleMap map;
    private EditText pickupLocation;
    private TextView waitTime;
    private TimeFormatter timeFormatter;

    // cache data sources
    private AllBookings allBookings;
    private AllTaxis allTaxis;

    /**
     * Constructor for class passenger map fragment.
     */
    public PassengerMapFragment() {
        this.timeFormatter = new HourMinutesSecondsFormatter();
    }

    /**
     * The system calls this method when creating the fragment.
     * Initialises essential components of the fragment
     * @param savedInstanceState state to restore.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.allTaxis = AllTaxis.getInstance();
        this.allBookings = AllBookings.getInstance();

        Intent gpsService = new Intent(getActivity(), GpsLocationListener.class);
        getActivity().startService(gpsService);

        /*
         Subscribe to event broadcasters
        */
        this.allTaxis.addObserver(this);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mLocationReceiver,
                new IntentFilter(DtbsPreferences.LOCATION_EVENT));
    }

    /**
     * The system calls this callback when it's time for the
     * fragment to draw its user interface for the first time.
     * To draw a UI for your fragment, you must return a
     * View component from this method that is the root of your fragment's layout.
     * You can return null if the fragment does not provide a UI.
     *
     * @param inflater inflate layout.
     * @param container a collection of views.
     * @param savedInstanceState saved data.
     * @return the inflated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(com.bee.dtbs.mobile.android.dtbsandroidclient.R.layout.fragment_map, container, false);

        pickupLocation = (EditText) v.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_pickup_location);
        waitTime = (TextView)v.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.view_wait_time);
        mapView = (MapView) v.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.map);
        mapView.onCreate(savedInstanceState);

        MapsInitializer.initialize(this.getActivity());
        map = this.initialiseMap(mapView);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRedrawMapMessageReceiver,
                new IntentFilter(DtbsPreferences.MAP_REDRAW_EVENTS_TOPIC));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mNetworkMonitorReceiver,
                new IntentFilter(DtbsPreferences.NETWORK_STATE_EVENT));

        try {
            AllBookings.getInstance().setActiveBooking(new CheckActiveBookingAsyncTask(this).execute().get());
        } catch (InterruptedException|ExecutionException e) {
           Log.e(TAG, e.getMessage());
        }

        return v;
    }

    /**
     * Set map state based on booking.
     *
     * @param fragment map booking state.
     */
    public void setBookingState(Fragment fragment){
        getFragmentManager().beginTransaction()
                .replace(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.content_map_state_frame, fragment, "PassengerMapFragment").commit();
    }

    /**
     * The system calls this method as the first indication that the
     * user is leaving the fragment.
     */
    @Override
    public void onPause(){
        super.onPause();
        AllTaxis.getInstance().deleteObserver(this);
    }

    /**
     * Invoked when fragment becomes active.
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        AllTaxis.getInstance().addObserver(this);
    }

    /**
     * called to do final clean up of the fragment's
     * state but Not guaranteed to be called by the Android platform
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * Invoked when the system is running low on memory.
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     * Initialise map view with user's current location.
     */
    public GoogleMap initialiseMap(MapView mapView){
        GoogleMap map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.setMyLocationEnabled(true);
        map.setBuildingsEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        return map;
    }

    /**
     * Invoked by model/services to inform the view that there has been an update.
     *
     * @param observable callback.
     * @param data the update event.
     */
    @Override
    public void update(Observable observable, Object data){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                redrawGoogleMap();

                /*
                Update user's average taxi wait time.
                Throws illegal state exception if no taxis available, to prevent divide by 0 exception.
                */
                try {

                    if(AllBookings.getInstance().activeBooking()
                            && AllBookings.getInstance().getActive().isPassengerPickedUp()){
                        updateWaitTime(allTaxis.getEstimatedEta());
                    }else{
                        updateWaitTime(allTaxis.getAverageWaitTimeInSeconds());
                    }

                } catch (IllegalStateException ex) {
                    Log.i(TAG, ex.getMessage());
                }
            }
        });
    }

    /**
     * Redraw google maps with taxis.
     * Invoked on taxi location update.
     */
    private void redrawGoogleMap(){

        map.clear();

        AllBookings bookings = AllBookings.getInstance();

        for (Taxi t : allTaxis.findAll()) {

            if(bookings.activeBooking() && bookings.taxiIsActive(t.getId())
                    || (bookings.activeBooking() && bookings.getActive().awaitingTaxiDispatch())){
                this.updateMap(t);
            }else if(!bookings.activeBooking() && t.onDuty()){
                this.updateMap(t);
            }
        }

        if(bookings.getActive() != null){

            Location destinationLocation =
                    bookings
                            .getActive()
                            .getRoute()
                            .getEndAddress()
                            .getLocation();

            LatLng location = new LatLng(
                    destinationLocation.getLatitude(),
                    destinationLocation.getLongitude());

            MarkerOptions options = new MarkerOptions();
            options.position(location);
            options.title("Destination");
            map.addMarker(options);

            List<LatLng> path = allBookings.getActive().getRoute().getLatLngPath();
            PolylineOptions routePolyLine = new PolylineOptions();
            routePolyLine.addAll(path);
            routePolyLine.width(6);
            routePolyLine.color(Color.RED);
            map.addPolyline(routePolyLine);
        }
    }

    /**
     * Update map with taxi.
     */
    private void updateMap(Taxi t){
        LatLng location = new LatLng(
                t.getLocation().getLatitude(),
                t.getLocation().getLongitude());

        MarkerOptions options = new MarkerOptions();
        options.position(location);
        options.title("Numberplate: " + t.getVehicle().getNumberplate());
        options.icon(BitmapDescriptorFactory.fromResource(com.bee.dtbs.mobile.android.dtbsandroidclient.R.drawable.img_taxi_icon));
        map.addMarker(options);
    }

    /**
     * Update wait time.
     *
     * @param time time in seconds.
     */
    private void updateWaitTime(int time){
        waitTime.setText(
                timeFormatter.format(time));
    }

    /**
     * Update address.
     * Display "Address not found" if address is null.
     *
     * @param address new address.
     */
    private void updateAddress(String address){
        if(address == null){
            pickupLocation.setText("Address not found.");
        }else if(AllBookings.getInstance().getActive() == null ||
                (AllBookings.getInstance().getActive() != null
                && !AllBookings.getInstance().getActive().isPassengerPickedUp())){
            pickupLocation.setText(address);
        }
    }

    /**
     * Move cameras to specified latitude and longitude.
     *
     * @param latitude latitude to move camera to.
     * @param longitude longitude to move camera to.
     */
    private void moveCamera(double latitude, double longitude){

        // move camera to user's new location
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(
                        latitude,
                        longitude),
                CAMERA_ZOOM_LEVEL));

        map.animateCamera(
                CameraUpdateFactory
                        .zoomTo(CAMERA_ZOOM_LEVEL),
                CAMERA_ANIMATE_DURATION,
                null);
    }

    // Broadcaster receiver to deal with user location changes.
    private BroadcastReceiver mLocationReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // update user's address.
            updateAddress(intent.getStringExtra("address"));

            // move camera to user's new location
            moveCamera(intent.getDoubleExtra("latitude", 0),
                    intent.getDoubleExtra("longitude", 0));

            redrawGoogleMap();
        }
    };


    private BroadcastReceiver mRedrawMapMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            redrawGoogleMap();
        }
    };

    NetworkMonitor mNetworkMonitorReceiver = new NetworkMonitor() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(isConnected(context)){
                MessageEventBus.getInstance().open();
            }else{
                MessageEventBus.getInstance().close();
            }
        }
    };
}
