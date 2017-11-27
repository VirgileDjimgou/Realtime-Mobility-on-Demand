package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by djimgou patrick virgile  on 13/02/16.
 */
public class Booking implements Serializable {
    private long id;
    private Date timestamp;
    private Date startTime;
    private Date endTime;
    private int numberPassengers;
    private double cost;
    private Account passenger;
    private Taxi taxi;
    private Route route;
    private String state;

    public Booking(){}

    public Booking(Account passenger, Route route, int numberPassengers){
        this.passenger = passenger;
        this.route = route;
        this.numberPassengers = numberPassengers;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getNumberPassengers() {
        return numberPassengers;
    }

    public void setNumberPassengers(int numberPassengers) {
        this.numberPassengers = numberPassengers;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Account getPassenger() {
        return passenger;
    }

    public void setPassenger(Account passenger) {
        this.passenger = passenger;
    }

    public Taxi getTaxi() {
        return this.taxi;
    }

    public void setTaxi(Taxi taxi) {
        this.taxi = taxi;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean awaitingTaxiDispatch(){
        return this.taxi == null;
    }

    public boolean isPassengerPickedUp(){
        return this.state.equals("PASSENGER_PICKED_UP");
    }
}
