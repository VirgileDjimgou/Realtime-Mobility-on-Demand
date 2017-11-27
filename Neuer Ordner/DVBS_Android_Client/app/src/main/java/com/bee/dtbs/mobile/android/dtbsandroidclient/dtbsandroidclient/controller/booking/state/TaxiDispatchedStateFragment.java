package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.booking.state;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Booking;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.config.DtbsPreferences;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.cache.AllBookings;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.utils.datamapper.DataMapper;

/**
 * Represents a taxi dispatched booking state.
 */
public class TaxiDispatchedStateFragment extends Fragment implements BookingState{

    private static final String TAG = TaxiDispatchedStateFragment.class.getName();

    private TextView txtDriverRegistration;
    private TextView txtDriverName;

    private Booking activeBooking;

    private AllBookings allBookings = AllBookings.getInstance();
    private Fragment nextFragment;

    public TaxiDispatchedStateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(com.bee.dtbs.mobile.android.dtbsandroidclient.R.layout.fragment_taxi_dispatched_state, container, false);

        this.txtDriverName = (TextView)v.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_driver_name);
        this.txtDriverRegistration = (TextView)v.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_registration);

        if(getArguments().get("data") == null){
            this.activeBooking = AllBookings.getInstance().findItem(getArguments().getLong(DtbsPreferences.ACTIVE_BOOKING));
        }else{
            this.activeBooking = DataMapper.getInstance().readObject(getArguments().get("data").toString(), Booking.class);
        }

        allBookings.getActive().setState(this.activeBooking.getState());
        allBookings.getInstance().getActive().setTaxi(this.activeBooking.getTaxi());

        this.txtDriverName.setText(this.activeBooking.getTaxi().getAccount().getCommonName());
        this.txtDriverRegistration.setText(this.activeBooking.getTaxi().getVehicle().getNumberplate());

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter(DtbsPreferences.BOOKING_EVENTS_TOPIC));

        return v;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("status").equals("PASSENGER_PICKED_UP")) {

                View view  = getActivity().findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.content_map_state_frame);

                if(view != null) {
                    nextFragment = new PassengerPickedupStateFragment();
                    nextFragment.setArguments(intent.getExtras());
                    pickupPassenger();
                }
            }
        }
    };

    @Override
    public void awaitTaxi(Booking booking) {
        throw new IllegalStateException("Taxi already dispatched.");
    }

    @Override
    public void requestTaxi() {
        throw new IllegalStateException("Taxi already requested.");
    }

    @Override
    public void pickupPassenger() {
        try{
            View view  = getActivity().findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.content_map_state_frame);

            if(view != null) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.content_map_state_frame, nextFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }catch(Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void dropOffPassenger() {
        throw new IllegalStateException("Passenger not picked up");
    }

    @Override
    public void cancelBooking() {
        throw new IllegalStateException("Booking cancelled.");
    }

    @Override
    public void taxiDispatched() {
        throw new IllegalStateException("Taxi already dispatched.");
    }
}
