package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.gps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.GeocodeService;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.communication.event.TaxiEventsService;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.config.DtbsPreferences;

/**
 * GPS Location listener.
 *
 * @author robertnorthard
 */
@SuppressWarnings("ResourceType")
public class GpsLocationListener extends Service implements android.location.LocationListener {

    // TAG used for logging.
    private static final String TAG = GpsLocationListener.class.getName();

    private Context context;
    private GeocodeService geocodeService;
    private TaxiEventsService taxiEventsService;

    private Location location;
    private String address;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 10;

    protected LocationManager locationManager;

    public GpsLocationListener(){
        this.context = this;
        this.geocodeService = new GeocodeService();
        this.taxiEventsService = TaxiEventsService.getInstance();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        locationManager = (LocationManager) this.context
                .getSystemService(LOCATION_SERVICE);

        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                MIN_TIME_BW_UPDATES, this);

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Invoke on GPS location change.
     *
     * @param location updated user's location.
     */
    @Override
    public void onLocationChanged(final Location location) {

        // update last known location
        this.location = location;

        try {
            address = geocodeService.addressReverseLookup(
                    location.getLatitude(),
                    location.getLongitude());

        } catch (Exception e) {
            address = null;
        }

        com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Location
                newLocation = new com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Location(
                location.getLatitude(),
                location.getLongitude());

        taxiEventsService.updateLocation(newLocation);

        broadcastLocationEvent();
    }

    /**
     * Broadcast location event.
     */
    private void broadcastLocationEvent(){
        Intent intent = new Intent(DtbsPreferences.LOCATION_EVENT);

        intent.putExtra("latitude", this.location.getLatitude());
        intent.putExtra("longitude", this.location.getLongitude());
        intent.putExtra("address", this.address);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }
}
