package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.booking.state;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.formatter.currency.SterlingFormatter;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.config.DtbsPreferences;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.cache.AllBookings;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.formatter.currency.CurrencyFormatter;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Booking;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.utils.datamapper.DataMapper;

/**
 * Represents completed booking state
 */
public class BookingCompleteStateFragment extends Fragment implements BookingState {

    private static final String TAG = BookingCompleteStateFragment.class.getName();

    private TextView txtBookingCost;
    private Button btnConfirmBookingCompletition;
    private Booking activeBooking;
    private Fragment nextFragment;
    private TextView tvDestinationLocation;
    private TextView tvETA;

    private CurrencyFormatter currencyFormatter;

    public BookingCompleteStateFragment() {
        // Required empty public constructor
        this.currencyFormatter = new SterlingFormatter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(com.bee.dtbs.mobile.android.dtbsandroidclient.R.layout.fragment_booking_complete_state, container, false);

        this.txtBookingCost = (TextView)v.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_booking_cost);
        this.btnConfirmBookingCompletition = (Button)v.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.btn_confirm_booking_completion);

        if(getArguments().get("data") == null){
            this.activeBooking = AllBookings.getInstance().findItem(getArguments().getLong(DtbsPreferences.ACTIVE_BOOKING));
        }else{
            this.activeBooking = DataMapper.getInstance().readObject(getArguments().get("data").toString(), Booking.class);
        }

        this.txtBookingCost.setText(currencyFormatter.format(this.activeBooking.getCost()));

        this.btnConfirmBookingCompletition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeBooking();
            }
        });

        return v;
    }

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
        throw new IllegalStateException("Passenger already picked up.");
    }

    @Override
    public void dropOffPassenger() {
        throw new IllegalStateException("Passenger already dropped off.");
    }

    @Override
    public void cancelBooking() {
        throw new IllegalStateException("Booking cancelled.");
    }

    @Override
    public void taxiDispatched() {
        throw new IllegalStateException("Booking completed.");
    }

    public void completeBooking(){

        AllBookings.getInstance().setActiveBooking(null);

        // notify map fragment that it should be redrawn.
        Intent intent = new Intent(DtbsPreferences.MAP_REDRAW_EVENTS_TOPIC);
        LocalBroadcastManager.getInstance(BookingCompleteStateFragment.this.getActivity().getBaseContext()).sendBroadcast(intent);

        nextFragment = new RequestRideStateFragment();

        try{
            View view  = getActivity().findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.content_map_state_frame);

            this.tvDestinationLocation = (TextView)getActivity().findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.tv_pickup_location);
            this.tvDestinationLocation.setText(getString(com.bee.dtbs.mobile.android.dtbsandroidclient.R.string.pickup_location));
            this.tvETA = (TextView)getActivity().findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.tv_wait_time);
            this.tvETA.setText(getString(com.bee.dtbs.mobile.android.dtbsandroidclient.R.string.average_wait_time_hint));

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
}
