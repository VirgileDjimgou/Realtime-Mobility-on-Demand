package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.booking.state;

import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Booking;

/**
 * An interface to represent a booking state and operations to transition between states.
 */
public interface BookingState {

    void awaitTaxi(Booking booking);

    void requestTaxi();

    void pickupPassenger();

    void dropOffPassenger();

    void cancelBooking();

    void taxiDispatched();
}
