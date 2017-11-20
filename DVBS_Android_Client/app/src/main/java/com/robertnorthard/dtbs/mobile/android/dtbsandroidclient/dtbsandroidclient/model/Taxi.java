package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Created by robertnorthard on 13/02/16.
 */
public class Taxi implements Serializable {

    private Long id;
    private Vehicle vehicle;
    private Account account;
    private Location location;
    private String state;

    @JsonIgnore
    private int timeFromPassenger;

    public Taxi(){

    }

    public int getTimeFromPassenger() {
        return timeFromPassenger;
    }

    public void setTimeFromPassenger(int timeFromPassenger) { this.timeFromPassenger = timeFromPassenger; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean onDuty(){
        return this.getState().equals(TaxiStates.ON_DUTY.toString());
    }
}
