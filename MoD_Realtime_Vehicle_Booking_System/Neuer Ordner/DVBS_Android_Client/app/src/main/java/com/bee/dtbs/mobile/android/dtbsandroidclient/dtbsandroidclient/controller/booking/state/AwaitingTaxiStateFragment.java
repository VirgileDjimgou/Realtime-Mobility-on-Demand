package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.booking.state;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.cache.AllBookings;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Booking;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.BookingService;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.config.DtbsPreferences;

import org.json.JSONException;

import java.io.IOException;

/**
 * Represents an awaiting taxi booking state.
 */
public class AwaitingTaxiStateFragment extends Fragment implements BookingState {

    private static final String TAG = AwaitingTaxiStateFragment.class.getName();

    private Button btnCancelBooking;
    private Booking activeBooking;
    private Fragment nextFragment;

    public AwaitingTaxiStateFragment() {
        // fragments require empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(com.bee.dtbs.mobile.android.dtbsandroidclient.R.layout.fragment_awaiting_taxi_state, container, false);

        this.btnCancelBooking = (Button)v.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.btn_cancel_booking);
        this.activeBooking = AllBookings.getInstance().getActive();

        this.btnCancelBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AsyncTask<Long, Void, Void>() {

                    private BookingService bookingService = new BookingService();

                    private final ProgressDialog dialog = new ProgressDialog(getActivity());
                    private Exception exception = null;
                    private AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

                    /**
                     * Invoked on cancel booking button press.
                     */
                    protected void onPreExecute() {
                        this.dialog.setMessage("Canceling booking...");
                        this.dialog.show();
                    }


                    @Override
                    protected Void doInBackground(Long... params) {

                        try {
                            bookingService.cancelBooking(params[0]);
                        } catch (IOException | JSONException | IllegalArgumentException e) {
                            exception = e;
                        }

                        return null;
                    }

                    /**
                     * Handler to manage result of background task.
                     *
                     * @param result result of background task.
                     */
                    @Override
                    protected void onPostExecute(final Void result) {

                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(com.bee.dtbs.mobile.android.dtbsandroidclient.R.string.btn_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.cancel();
                            }
                        });

                        if (this.dialog.isShowing()) {
                            this.dialog.dismiss();
                        }

                        if ((exception != null)) {
                            alertDialog.setMessage(exception.getMessage());
                            alertDialog.show();
                        } else {
                            cancelBooking();
                        }
                    }
                }.execute(activeBooking.getId());

            }
        });

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter(DtbsPreferences.BOOKING_EVENTS_TOPIC));

        return v;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            nextFragment = new TaxiDispatchedStateFragment();
            nextFragment.setArguments(intent.getExtras());
            taxiDispatched();
        }
    };

    @Override
    public void awaitTaxi(Booking booking) {
        throw new IllegalStateException("Already awaiting taxi.");
    }

    @Override
    public void requestTaxi() {
        throw new IllegalStateException("Taxi already requested.");
    }

    @Override
    public void taxiDispatched(){
        try{
            View view  = getActivity().findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.content_map_state_frame);

            // notify map fragment that it should be redrawn.
            Intent intent = new Intent(DtbsPreferences.MAP_REDRAW_EVENTS_TOPIC);
            LocalBroadcastManager.getInstance(AwaitingTaxiStateFragment.this.getActivity().getBaseContext()).sendBroadcast(intent);

            if(view != null) {
                getFragmentManager().beginTransaction()
                        .replace(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.content_map_state_frame, nextFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }catch(Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void pickupPassenger() {
        throw new IllegalStateException("Awaiting taxi allocation.");
    }

    @Override
    public void dropOffPassenger() {
        throw new IllegalStateException("Awaiting taxi allocation.");
    }

    @Override
    public void cancelBooking() {

        AllBookings.getInstance().setActiveBooking(null);

        // notify map fragment that it should be redrawn.
        Intent intent = new Intent(DtbsPreferences.MAP_REDRAW_EVENTS_TOPIC);
        LocalBroadcastManager.getInstance(AwaitingTaxiStateFragment.this.getActivity().getBaseContext()).sendBroadcast(intent);

        try{
            View view  = getActivity().findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.content_map_state_frame);

            if(view != null) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.content_map_state_frame, new RequestRideStateFragment())
                        .addToBackStack(null)
                        .commit();
            }
        }catch(Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }
}
