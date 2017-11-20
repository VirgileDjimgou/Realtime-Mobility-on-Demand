package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.booking.history;

import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.R;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.cache.AllBookings;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Booking;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.BookingService;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.config.DtbsPreferences;

import org.json.JSONException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * A fragment representing a collection of bookings.
 *
 * @author robertnorthard
 */
public class BookingHistoryFragment extends ListFragment {

    // TAG used for logging.
    private static final String TAG = BookingHistoryFragment.class.getName();

    private List<Booking> bookings = null;
    private BookingHistoryListAdapter bookingHistoryListAdapter;

    public BookingHistoryFragment() {
        /*
         * Mandatory empty constructor for the fragment manager to instantiate the
         * fragment (e.g. upon screen orientation changes).
         */
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    bookings = new BookingService().findAllBookings();
                    AllBookings.getInstance().addBookings(bookings);

                    // reverse list most recent first
                    Collections.reverse(bookings);

                    bookingHistoryListAdapter
                            = new BookingHistoryListAdapter(getActivity(), bookings);

                } catch (IOException| JSONException e) {
                    Log.e(TAG, e.getMessage());
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                setListAdapter(bookingHistoryListAdapter);
            }
        }.execute();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        getListView()
                .setEmptyView(getActivity()
                        .getLayoutInflater()
                        .inflate(R.layout.booking_history_list_empty_view, null, true));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Bundle data = new Bundle();
        data.putLong(DtbsPreferences.BOOKING_ID, this.bookings.get(position).getId());
        BookingViewFragment bookingViewFragment = new BookingViewFragment();
        bookingViewFragment.setArguments(data);

        // notify map fragment that it should be redrawn.
        Intent intent = new Intent(DtbsPreferences.MAP_REDRAW_EVENTS_TOPIC);
        LocalBroadcastManager.getInstance(BookingHistoryFragment.this.getActivity().getBaseContext()).sendBroadcast(intent);

        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, bookingViewFragment, "BookingViewFragment").commit();

        super.onListItemClick(l, v, position, id);
    }
}
