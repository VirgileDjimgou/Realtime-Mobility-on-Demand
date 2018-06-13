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
import android.widget.EditText;
import android.widget.TextView;

import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Booking;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.config.DtbsPreferences;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.cache.AllBookings;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.utils.datamapper.DataMapper;

/**
 * Represents a passenger picked up state.
 */
public class PassengerPickedupStateFragment extends Fragment implements BookingState{

    private static final String TAG = PassengerPickedupStateFragment.class.getName();

    private TextView txtDriverRegistration;
    private TextView txtDriverName;
    private EditText txtDestinationLocation;
    private TextView tvDestinationLocation;
    private TextView tvEstimateArrivalTime;

    private Booking activeBooking;

    private Fragment nextFragment;

    public PassengerPickedupStateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(com.bee.dtbs.mobile.android.dtbsandroidclient.R.layout.fragment_passenger_pickedup_state, container, false);

        this.txtDriverName = (TextView)v.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_driver_name);
        this.txtDriverRegistration = (TextView)v.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_registration);
        this.txtDestinationLocation = (EditText)getActivity().findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_pickup_location);
        this.tvDestinationLocation = (TextView)getActivity().findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.tv_pickup_location);

        if(getArguments().get("data") == null){
            this.activeBooking = AllBookings.getInstance().findItem(getArguments().getLong(DtbsPreferences.ACTIVE_BOOKING));
        }else{
            this.activeBooking = DataMapper.getInstance().readObject(getArguments().get("data").toString(), Booking.class);
        }

        AllBookings.getInstance().getActive().setState(this.activeBooking.getState());

        this.tvDestinationLocation = (TextView)getActivity().findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.tv_pickup_location);
        this.tvDestinationLocation.setText(getString(com.bee.dtbs.mobile.android.dtbsandroidclient.R.string.msg_destination_address));
        this.txtDestinationLocation = (EditText)getActivity().findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_pickup_location);
        this.txtDestinationLocation.setText(this.activeBooking.getRoute().getEndAddress().getAddress());

        this.tvEstimateArrivalTime = (TextView)getActivity().findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.tv_wait_time);
        this.tvEstimateArrivalTime.setText(getString(com.bee.dtbs.mobile.android.dtbsandroidclient.R.string.msg_estimated_arrival_time));

        this.txtDriverName.setText(this.activeBooking.getTaxi().getAccount().getCommonName());
        this.txtDriverRegistration.setText(this.activeBooking.getTaxi().getVehicle().getNumberplate());

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter(DtbsPreferences.BOOKING_EVENTS_TOPIC));

        return v;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            nextFragment = new BookingCompleteStateFragment();
            nextFragment.setArguments(intent.getExtras());
            dropOffPassenger();
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
        throw new IllegalStateException("Passenger already picked up");
    }

    @Override
    public void dropOffPassenger() {
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
    public void cancelBooking() {
        throw new IllegalStateException("Booking cancelled.");
    }

    @Override
    public void taxiDispatched() {
        throw new IllegalStateException("Passenger already picked up.");
    }
}
