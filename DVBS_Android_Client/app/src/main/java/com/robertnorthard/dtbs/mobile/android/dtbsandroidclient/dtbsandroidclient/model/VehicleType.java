package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model;

import java.io.Serializable;

/**
 * Represents a vehicle type.
 *
 * @author robertnorthard
 */
public class VehicleType implements Serializable{

    private String name;
    private String manufacturer;
    private String model;
    private double costPerMile;

    public VehicleType(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getCostPerMile() {
        return costPerMile;
    }

    public void setCostPerMile(double costPerMile) {
        this.costPerMile = costPerMile;
    }
}
