package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.booking.state;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.maps.model.LatLng;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.R;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.cache.AllBookings;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Booking;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Location;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.BookingService;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.GeocodeService;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.config.DtbsPreferences;
import com.robertnorthard.dtbs.server.common.dto.BookingDto;
import com.robertnorthard.dtbs.server.common.dto.LocationDto;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * A controller class to handler all related activities
 * for booking a ride.
 */
public class BookingFragment extends Fragment implements BookingState {


    private AutoCompleteTextView txtPickupLocation;
    private AutoCompleteTextView txtDestinationLocation;
    private Spinner spinnerPassengerCount;
    private Button btnBookRide;

    public BookingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_booking, container, false);

        String pickupLocation = getArguments().getString(DtbsPreferences.DATA_PICKUP_LOCATION);

        this.txtPickupLocation = (AutoCompleteTextView)v.findViewById(R.id.txt_pickup_location);
        this.txtDestinationLocation = (AutoCompleteTextView)v.findViewById(R.id.txt_destination_location);
        this.spinnerPassengerCount = (Spinner) v.findViewById(R.id.spinner_number_passengers);
        this.btnBookRide = (Button)v.findViewById(R.id.btn_request_ride);

        this.txtDestinationLocation.setAdapter(new AutoCompleteBookingAdapter(getActivity(),R.layout.auto_complete_address_layout));

        this.txtPickupLocation.setText(pickupLocation);
        this.txtPickupLocation.setAdapter(new AutoCompleteBookingAdapter(getActivity(),R.layout.auto_complete_address_layout));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.spinner_number_passengers, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerPassengerCount.setAdapter(adapter);

        // register login button event handler
        this.btnBookRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AsyncTask<String, Void, Booking>() {

                    private GeocodeService geocodeService = new GeocodeService();
                    private BookingService bookingService = new BookingService();

                    private final ProgressDialog dialog = new ProgressDialog(getActivity());
                    private Exception exception = null;
                    private AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

                    /**
                     * Invoked booking request.
                     */
                    protected void onPreExecute() {
                        this.dialog.setMessage("Processing Booking...");
                        this.dialog.show();
                    }

                    /**
                     * Make a booking and if valid, calculate the route to display.
                     */
                    @Override
                    protected Booking doInBackground(String... params) {

                        try {
                            Location startLocation = this.geocodeService.addressLookup(params[0]);
                            Location endLocation = this.geocodeService.addressLookup(params[1]);
                            int numberPassengers = Integer.parseInt(params[2]);

                            BookingDto booking = new BookingDto();
                            booking.setStartLocation(new LocationDto(
                                    startLocation.getLatitude(),
                                    startLocation.getLongitude()
                            ));
                            booking.setEndLocation(new LocationDto(
                                    endLocation.getLatitude(),
                                    endLocation.getLongitude()
                            ));

                            booking.setNumberPassengers(numberPassengers);

                            Booking newBooking = this.bookingService.bookRide(booking);

                            if(booking != null){
                                AllBookings.getInstance().setActiveBooking(newBooking);

                                LatLng currentLocation = new LatLng(
                                        startLocation.getLatitude(),
                                        startLocation.getLongitude());

                                LatLng destinationLocation = new LatLng(endLocation.getLatitude(), endLocation.getLongitude());

                                List<LatLng> route = this.geocodeService.getRoute(currentLocation,destinationLocation);

                                newBooking.getRoute().setLatLngPath(route);
                            }

                            return newBooking;

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
                    protected void onPostExecute(final Booking result) {

                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.cancel();
                            }
                        });

                        if (this.dialog.isShowing()) {
                            this.dialog.dismiss();
                        }

                        if ((exception != null || result == null)) {
                            alertDialog.setMessage(exception.getMessage());
                            alertDialog.show();
                        } else {
                            awaitTaxi(result);
                        }
                    }
                }.execute(txtPickupLocation.getText().toString(), txtDestinationLocation.getText().toString(), spinnerPassengerCount.getSelectedItem().toString());

            }
        });

        return v;
    }

    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void awaitTaxi(Booking booking) {

        // add booking to cache.
        AllBookings.getInstance().addItem(booking);

        Fragment f = new AwaitingTaxiStateFragment();

        FragmentManager fm = getFragmentManager();

        Bundle data = new Bundle();
        data.putLong(DtbsPreferences.ACTIVE_BOOKING, booking.getId());
        f.setArguments(data);

        FragmentTransaction ft = fm.beginTransaction();
        fm.popBackStack();
        ft.replace(R.id.content_map_state_frame, f);
        ft.addToBackStack(null);
        ft.commit();

        // notify map fragment that it should be redrawn.
        Intent intent = new Intent(DtbsPreferences.MAP_REDRAW_EVENTS_TOPIC);
        LocalBroadcastManager.getInstance(BookingFragment.this.getActivity().getBaseContext()).sendBroadcast(intent);
    }

    @Override
    public void requestTaxi() {
        throw new IllegalStateException("Taxi not requested.");
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
        throw new IllegalStateException("Taxi not requested.");
    }
}