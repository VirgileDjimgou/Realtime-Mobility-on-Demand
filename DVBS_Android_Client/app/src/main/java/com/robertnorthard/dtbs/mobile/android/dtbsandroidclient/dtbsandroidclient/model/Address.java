package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model;

import java.io.Serializable;

/**
 * Created by robertnorthard on 13/02/16.
 */
public class Address implements Serializable{
    private Location location;
    private String address;

    public Address(){

    }

    public Address(Location location, String streetAddress) {
        this.location = location;
        this.address = streetAddress;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
