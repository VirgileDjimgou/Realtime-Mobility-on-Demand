package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model;

/**
 * @author djimgou patrick  virgile
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
