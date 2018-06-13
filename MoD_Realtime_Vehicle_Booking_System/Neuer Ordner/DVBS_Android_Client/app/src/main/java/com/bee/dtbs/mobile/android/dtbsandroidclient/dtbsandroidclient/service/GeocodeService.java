package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service;

import android.net.Uri;

import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Location;
import com.google.android.gms.maps.model.LatLng;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.config.ConfigService;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.utils.datamapper.DataMapper;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.utils.http.HttpMethod;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.utils.http.RestClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for encapsulating all operations related to geocoding.
 */
public class GeocodeService {

    // TAG used for logging.
    private static final String TAG = GeocodeService.class.getName();

    private DataMapper dataMapper;

    public GeocodeService(){
        this.dataMapper = DataMapper.getInstance();
    }

    /**
     * Geocode address reverse lookup.
     *
     * @param lat latitude of current location.
     * @param lng longitude of current location.
     * @return address corresponding with current latitude and longitude.
     */
    public String addressReverseLookup(double lat, double lng) throws IOException, JSONException{

        Map<String,String> tokens = new HashMap<>();
        tokens.put("latitude", String.valueOf(lat));
        tokens.put("longitude", String.valueOf(lng));

        String url = ConfigService.getProperty("dtbs.endpoint.geocode.address.reverse.lookup");

        try {
            JSONObject json = RestClient.getInstance().sendData(ConfigService.parseProperty(url, tokens), HttpMethod.GET, null);

            if(json.getString("status").equals("0")) {
                return json.getString("data");
            }else{
                throw new IllegalArgumentException(json.getString("data"));
            }
        } catch (IOException|JSONException e) {
            throw e;
        }
    }

    /**
     * Return lat/lng for specified address.
     *
     * @param address address to lookup lat/lng.
     * @return location (lat/lng) corresponding to provided address.
     *
     * @throws IOException input/output error.
     * @throws JSONException unable to parse JSON response.
     */
    public Location addressLookup(String address) throws IOException, JSONException {
        Map<String,String> tokens = new HashMap<>();
        tokens.put("address", Uri.encode(address));

        String url = ConfigService.getProperty("dtbs.endpoint.geocode.address.lookup");

        try {
            JSONObject json = RestClient.getInstance().sendData(ConfigService.parseProperty(url, tokens), HttpMethod.GET, null);

            if(json.getString("status").equals("0")) {
                return this.dataMapper.readObject(json.getString("data"), Location.class);
            }else{
                throw new IllegalArgumentException(json.getString("data"));
            }
        } catch (IOException|JSONException e) {
            throw e;
        }
    }

    /**
     * Estimate travel time between start and end location.
     *
     * @param startLocation start location.
     * @param endLocation end location.
     * @return estimated travel time between start and end location.
     */
    public int estimateTravelTime(LatLng startLocation, LatLng endLocation) throws IOException, JSONException{

        Map<String,String> tokens = new HashMap<>();
        tokens.put("start_latitude",String.valueOf(startLocation.latitude));
        tokens.put("start_longitude",String.valueOf(startLocation.longitude));
        tokens.put("end_latitude",String.valueOf(endLocation.latitude));
        tokens.put("end_longitude",String.valueOf(endLocation.longitude));

        String url = ConfigService.getProperty("dtbs.endpoint.geocode.route.time");

        try {

            JSONObject json = RestClient.getInstance().sendData(ConfigService.parseProperty(url, tokens), HttpMethod.GET, null);

            if(json.getString("status").equals("0")) {
                return json.getInt("data");
            }else{
                throw new IllegalArgumentException(json.getString("data"));
            }
        } catch (IOException|JSONException e) {
            throw e;
        }
    }

    public String findAddress(String address) throws IOException, JSONException {
        Map<String,String> tokens = new HashMap<>();
        tokens.put("address",String.valueOf(address).replaceAll(" ", "%20"));

        String url = ConfigService.getProperty("dtbs.endpoint.geocode.address.lookup.text");

        try {

            JSONObject json = RestClient.getInstance().sendData(ConfigService.parseProperty(url, tokens), HttpMethod.GET, null);

            if(json.getString("status").equals("0")) {
                return json.getString("data");
            }else{
                throw new IllegalArgumentException(json.getString("data"));
            }
        } catch (IOException|JSONException e) {
            throw e;
        }
    }

    public List<LatLng> getRoute(LatLng startLocation, LatLng endLocation) throws IOException, JSONException {

        Map<String,String> tokens = new HashMap<>();
        tokens.put("start_latitude",startLocation.latitude + "");
        tokens.put("start_longitude",startLocation.longitude + "");
        tokens.put("end_latitude",endLocation.latitude + "");
        tokens.put("end_longitude",endLocation.longitude + "");

        String url = ConfigService.getProperty("dtbs.endpoint.geocode.route");

        try {

            JSONObject json = RestClient.getInstance().sendData(ConfigService.parseProperty(url, tokens), HttpMethod.GET, null);

            if(json.getString("status").equals("0")) {

                List<LatLng> path = new ArrayList<>();

                for(int i = 0; i < json.getJSONObject("data").getJSONArray("path").length(); i++){

                    path.add(new LatLng(
                            Double.parseDouble(json.getJSONObject("data").getJSONArray("path").getJSONObject(i).getString("latitude")),
                            Double.parseDouble(json.getJSONObject("data").getJSONArray("path").getJSONObject(i).getString("longitude"))));
                }

                return path;
            }else{
                throw new IllegalArgumentException(json.getString("data"));
            }
        } catch (IOException|JSONException e) {
            throw e;
        }

    }
}
