package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.booking.state;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Booking;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.config.DtbsPreferences;


/**
 * Represents a request ride booking state.
 */
public class RequestRideStateFragment extends Fragment implements BookingState {

    private static final String TAG = RequestRideStateFragment.class.getName();

    private Button btnBookTaxi;
    private EditText txtPickupLocation;

    private Fragment nextFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(com.bee.dtbs.mobile.android.dtbsandroidclient.R.layout.fragment_request_ride, container, false);
        txtPickupLocation = (EditText) getActivity().findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_pickup_location);
        btnBookTaxi = (Button)v.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.btn_request_ride1);

        btnBookTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nextFragment = new BookingFragment();

                //bundle required data.
                Bundle data = new Bundle();
                data.putString(DtbsPreferences.DATA_PICKUP_LOCATION, txtPickupLocation.getText().toString());
                nextFragment.setArguments(data);
                requestTaxi();
            }
        });

        return v;
    }

    @Override
    public void awaitTaxi(Booking booking) {
        throw new IllegalStateException("Taxi not requested.");
    }

    @Override
    public void requestTaxi() {
        try {
            View view  = getActivity().findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.content_map_state_frame);

            if(view != null) {
                getFragmentManager()
                        .beginTransaction()
                        .add(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.content_frame, nextFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }catch(Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void pickupPassenger() {
        throw new IllegalStateException("Taxi not requested.");
    }

    @Override
    public void dropOffPassenger() {
        throw new IllegalStateException("Taxi not requested.");
    }

    @Override
    public void cancelBooking() {
        throw new IllegalStateException("Taxi not requested.");
    }

    @Override
    public void taxiDispatched() {
        throw new IllegalStateException("Ride not requested.");
    }
}
