package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.driver;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.R;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.driver.booking.state.OnDutyAvailableBookingStateFragment;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.gps.GpsLocationListener;


public class DriverMapFragment extends Fragment {

    private MapView mapView;
    private GoogleMap map;

    private static final int CAMERA_ZOOM_LEVEL = 14;
    private static final int CAMERA_ANIMATE_DURATION = 2000;

    public DriverMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_driver_map, container, false);

        Intent gpsService = new Intent(getActivity(), GpsLocationListener.class);
        getActivity().startService(gpsService);

        mapView = (MapView) v.findViewById(R.id.driver_map);
        mapView.onCreate(savedInstanceState);

        MapsInitializer.initialize(this.getActivity());
        map = this.initialiseMap(mapView);

        this.setBookingState(new OnDutyAvailableBookingStateFragment());

        return v;
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
     * Invoked when fragment becomes active.
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
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
     * Set map state based on booking.
     *
     * @param fragment map booking state.
     */
    public void setBookingState(Fragment fragment){
        getFragmentManager().beginTransaction()
                .replace(R.id.content_driver_taxi_booking_state, fragment, "DriverMapFragment").commit();
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

            // move camera to user's new location
            moveCamera(intent.getDoubleExtra("latitude", 0),
                    intent.getDoubleExtra("longitude", 0));

        }
    };
}
