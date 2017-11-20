package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.booking.history;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.formatter.currency.CurrencyFormatter;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.formatter.currency.SterlingFormatter;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Booking;

import java.util.List;

/**
 * A fragment representing a collection of bookings.
 *
 * @author djimgou patrick virgile
 */
public class BookingHistoryListAdapter extends ArrayAdapter<Booking> {

    private final Activity context;
    private final List<Booking> bookings;
    private final CurrencyFormatter currencyFormatter;

    public BookingHistoryListAdapter(Activity context, List<Booking> bookings) {
        super(context, com.bee.dtbs.mobile.android.dtbsandroidclient.R.layout.booking_history_list, bookings);
        this.context=context;
        this.bookings = bookings;
        this.currencyFormatter = new SterlingFormatter();
    }

    @Override
    public View getView(int position,View view,ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(com.bee.dtbs.mobile.android.dtbsandroidclient.R.layout.booking_history_list, null, true);

        TextView txtBookingId = (TextView)rowView.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_booking_id);
        TextView txtBookingState = (TextView)rowView.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_booking_state);
        TextView txtBookingCost = (TextView)rowView.findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_booking_cost);

        Booking booking = this.bookings.get(position);

        txtBookingId.setText(String.valueOf(booking.getId()));
        txtBookingState.setText(booking.getState().replace("_", " "));
        txtBookingCost.setText(currencyFormatter.format(booking.getCost()));

        return rowView;
    }
}
