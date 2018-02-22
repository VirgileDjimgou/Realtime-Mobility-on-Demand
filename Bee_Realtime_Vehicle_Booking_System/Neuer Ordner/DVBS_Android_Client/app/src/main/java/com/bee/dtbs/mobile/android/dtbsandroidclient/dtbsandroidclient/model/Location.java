package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Created by djimgou patrick virgile on 13/02/16.
 */
public class Location  implements Serializable{
    private double latitude;
    private double longitude;

    public Location(){

    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitiude) {
        this.latitude = latitiude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Convert to maindenhead head grid reference.
     * Code reference: https://github.com/Smerty/jham/blob/master/src/main/java/org/smerty/jham/Location.java
     * @param latitudeIn latitude.
     * @param longitudeIn longitude.
     * @return a string representation of the maidenhead grid reference for the latitude and longitude.
     */
    @JsonIgnore
    public static String toMaidenhead(final double latitudeIn,
                                      final double longitudeIn) {

        double longitude = longitudeIn + 180;
        longitude /= 2;
        char lonFirst = (char) ('A' + (longitude / 10));
        char lonSecond = (char) ('0' + longitude % 10);
        char lonThird = (char) ('A' + (longitude % 1) * 24);

        double latitude = latitudeIn + 90;
        char latFirst = (char) ('A' + (latitude / 10));
        char latSecond = (char) ('0' + latitude % 10);
        char latThird = (char) ('A' + (latitude % 1) * 24);

        StringBuilder sb = new StringBuilder();
        sb.append(lonFirst);
        sb.append(latFirst);
        sb.append(lonSecond);
        sb.append(latSecond);
        sb.append(("" + lonThird).toLowerCase());
        sb.append(("" + latThird).toLowerCase());

        return sb.toString();
    }
}
