package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model;

import java.io.Serializable;

/**
 * Created by robertnorthard on 13/02/16.
 */
public class Vehicle implements Serializable {

    private String numberplate;
    private int numberSeats;
    private VehicleType vehicleType;

    public Vehicle(){

    }

    public String getNumberplate() {
        return numberplate;
    }

    public void setNumberplate(String numberplate) {
        this.numberplate = numberplate;
    }

    public int getNumberSeats() {
        return numberSeats;
    }

    public void setNumberSeats(int numberSeats) {
        this.numberSeats = numberSeats;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }
}
