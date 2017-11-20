package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model;

/**
 * Created by robertnorthard on 06/04/16.
 */
public enum TaxiStates {
    ON_DUTY("ON_DUTY");

    private String description;

    TaxiStates(String description){
        this.description = description;
    }

    public String toString(){
        return this.description;
    }
}
