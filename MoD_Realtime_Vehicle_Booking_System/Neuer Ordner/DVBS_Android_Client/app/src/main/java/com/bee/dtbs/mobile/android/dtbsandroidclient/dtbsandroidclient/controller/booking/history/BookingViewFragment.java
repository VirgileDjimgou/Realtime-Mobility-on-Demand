package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.booking.history;

import android.app.Fragment;
import android.os.Bundle;
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
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.tasks.CancelTaxiBookingAsyncTask;

/**
 * Controller class for viewing a single booking.
 *
 * @author robertnorthard
 */
public class BookingViewFragment extends Fragment {

    // TAG used for logging.
    private static final String TAG = BookingViewFragment.class.getName();

    private TextView bookingCost;
    private TextView bookingCostDescription;
    private TextView bookingReference;
    private TextView startAddress;
    private TextView destinationAddress;
    private TextView bookingTime;
    private Button cancelTaxi;
    private CurrencyFormatter currencyFormatter;
    private Booking activeBooking;

    public BookingViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(com.bee.dtbs.mobile.android.dtbsandroidclient.R.layout.fragment_booking_view, container, false);

        this.bookingCost = (TextView)v.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_booking_view_cost);
        this.bookingCostDescription = (TextView)v.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_booking_view_cost_description);
        this.bookingReference = (TextView)v.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_booking_view_id);
        this.startAddress = (TextView)v.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_booking_view_pickup_address);
        this.destinationAddress = (TextView)v.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_booking_view_destination_address);
        this.bookingTime = (TextView)v.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_booking_view_book_time);
        this.cancelTaxi = (Button)v.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.btn_booking_view_cancel_booking);

        this.activeBooking = AllBookings.getInstance().findItem(getArguments().getLong(DtbsPreferences.BOOKING_ID));
        this.currencyFormatter = new SterlingFormatter();

        this.initialiseDisplay();

        return v;
    }

    private void initialiseDisplay(){

        if(!this.activeBooking.getState().equals("COMPLETED_BOOKING")){
            this.bookingCostDescription.setText("Estimated cost");
        }

        if(!(this.activeBooking.getState().equals("AWAITING_TAXI") || this.activeBooking.getState().equals("TAXI_DISPATCHED"))){
            this.cancelTaxi.setVisibility(View.INVISIBLE);
        }

        this.startAddress.setText(this.activeBooking.getRoute().getStartAddress().getAddress());
        this.destinationAddress.setText(this.activeBooking.getRoute().getEndAddress().getAddress());
        this.bookingReference.setText(this.activeBooking.getId()+"");
        this.bookingTime.setText(this.activeBooking.getTimestamp().toString());
        this.bookingCost.setText(this.currencyFormatter.format(this.activeBooking.getCost()));

        this.cancelTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelTaxiBookingAsyncTask task = new CancelTaxiBookingAsyncTask(getActivity());
                task.execute(activeBooking.getId());
                cancelTaxi.setVisibility(View.INVISIBLE);
            }
        });
    }
}
