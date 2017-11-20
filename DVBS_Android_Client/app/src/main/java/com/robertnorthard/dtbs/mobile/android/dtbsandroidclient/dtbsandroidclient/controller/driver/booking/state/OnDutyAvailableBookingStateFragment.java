package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.driver.booking.state;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.R;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.config.DtbsPreferences;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDutyAvailablePassengerStateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OnDutyAvailablePassengerStateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnDutyAvailableBookingStateFragment extends Fragment {

    private Fragment nextFragment;

    public OnDutyAvailableBookingStateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_on_duty_available_booking_state, container, false);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter(DtbsPreferences.BOOKING_EVENTS_TOPIC));

        return v;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            nextFragment = new AllocatedJobDriverBookingStateFragment();
            nextFragment.setArguments(intent.getExtras());
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_driver_taxi_booking_state, nextFragment)
                    .addToBackStack(null)
                    .commit();
        }
    };
}
