package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.cache;

import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Booking;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by robertnorthard on 20/02/16.
 */
public class AllBookings implements Cache<Long,Booking> {

    private Map<Long,Booking> bookings;
    private Booking active;

    private static AllBookings singleton;

    public static AllBookings getInstance(){
        if(AllBookings.singleton == null){
            synchronized (AllTaxis.class){
                AllBookings.singleton = new AllBookings();
            }
        }
        return AllBookings.singleton;
    }

    private AllBookings(){
        this.bookings = new ConcurrentHashMap<>();
    }

    public void addBookings(List<Booking> bookings){
        for(Booking b: bookings){
            this.addItem(b);
        }
    }

    @Override
    public void addItem(Booking item) {
        this.bookings.put(item.getId(), item);
    }

    @Override
    public void removeItem(Long id) {
        this.removeItem(id);
    }

    @Override
    public Booking findItem(Long id) {
        return this.bookings.get(id);
    }

    @Override
    public Collection<Booking> findAll() {
        return this.bookings.values();
    }

    @Override
    public boolean hasItem(Long id) {
        return this.bookings.containsKey(id);
    }

    public boolean taxiIsActive(Long id){
        if(this.active != null) {
            if (this.active.getTaxi() != null) {
                if (this.active.getTaxi().getId() == id) {
                    return true;
                }
            }
        }
        return false;
    }

    public Booking getActive(){
        return this.active;
    }

    public boolean activeBooking(){
        return this.active != null;
    }

    public void setActiveBooking(Booking booking){
        this.active = booking;
    }
}
