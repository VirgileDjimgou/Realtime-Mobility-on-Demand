package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.cache;

import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Taxi;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.TaxiService;

import java.util.Collection;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Maintains a cache of taxis.
 *
 * @author robertnorthard
 */
public class AllTaxis extends Observable implements Cache<Long,Taxi> {

    // singleton object.
    private static AllTaxis allTaxis;

    private Map<Long, Taxi> taxis;
    private TaxiService taxiService;

    private int estimatedArrivalTime = 0;

    /**
     * Default constructor for class taxi.
     */
    private AllTaxis(){
        this.taxis = new ConcurrentHashMap<>();
        this.taxiService = new TaxiService();
    }

    /**
     * Get singleton of AllTaxis if exists, else create new.
     *
     * @return a singleton object of AllTaxis if it exists else create new.
     */
    public static AllTaxis getInstance(){
        if(AllTaxis.allTaxis == null){
            synchronized (AllTaxis.class){
                AllTaxis.allTaxis = new AllTaxis();
            }
        }
        return AllTaxis.allTaxis;
    }


    /**
     * Calculate average waiting time in seconds from the
     * user's current location.
     *
     * @return average waiting time in seconds.
     */
    public synchronized int getAverageWaitTimeInSeconds() {
        int movingAverage = 0;

        for(Taxi taxi : this.taxis.values()){

            AllBookings bookings = AllBookings.getInstance();

            if(taxi.onDuty() && !bookings.activeBooking()
                    || (bookings.activeBooking() && bookings.getActive().awaitingTaxiDispatch())) {

                movingAverage += taxi.getTimeFromPassenger();
            }else if(bookings.activeBooking() && bookings.taxiIsActive(taxi.getId())){
                movingAverage += taxi.getTimeFromPassenger();
            }
        }

        // prevent divide by zero error
        if(movingAverage == 0){
            throw new IllegalStateException("No taxis available");
        }

        return movingAverage / this.taxis.size();
    }

    /**
     * Get estimated arrival time for a taxi.
     * @return
     */
    public int getEstimatedEta(){
        return this.estimatedArrivalTime;
    }

    /**
     * Set taxi estimated arrival time to destination.
     * @param estimatedArrivalTime
     */
    public void setEstimatedEta(int estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }


    @Override
    public synchronized void addItem(Taxi item) {
        this.taxis.put(item.getId(), item);
        this.notifySubscribers();
    }

    @Override
    public synchronized void removeItem(Long id) {
        this.taxis.remove(id);
        this.notifySubscribers();
    }

    @Override
    public synchronized Taxi findItem(Long id) {
        if (this.taxis.containsKey(id)) {
            return this.taxis.get(id);
        } else {

            // find object add if exists add to object cache.
            Taxi taxi = this.taxiService.findTaxi(id);
            if(taxi != null){
                this.addItem(taxi);
            }

            return taxi;
        }
    }

    @Override
    public synchronized  Collection<Taxi> findAll() {
        return this.taxis.values();
    }

    @Override
    public synchronized boolean hasItem(Long id) {
        if(this.taxis.containsKey(id)){
            return true;
        }
        return false;
    }

    private void notifySubscribers(){
        this.setChanged();
        this.notifyObservers();
    }
}
