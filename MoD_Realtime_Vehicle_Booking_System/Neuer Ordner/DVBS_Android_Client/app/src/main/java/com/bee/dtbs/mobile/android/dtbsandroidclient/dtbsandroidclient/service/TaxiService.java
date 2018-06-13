package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service;

import android.util.Log;

import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Taxi;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.config.ConfigService;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.utils.datamapper.DataMapper;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.utils.http.HttpMethod;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.utils.http.RestClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Taxi service for handling all operations related to taxis.
 *
 * @author djimgou patrick  virgile
 */
public class TaxiService {

    // TAG used for logging.
    private static final String TAG = TaxiService.class.getName();

    private RestClient restClient;

    /**
     * Default constructor for class taxi service.
     */
    public TaxiService(){
        this.restClient = RestClient.getInstance();
    }

    /**
     * Find taxi with the specified ID.
     * @param id id of taxi.
     * @return taxi with the specified id, else null.
     */
    public Taxi findTaxi(long id){

        String url = ConfigService.getProperty("dtbs.endpoint.taxi.find.id");

        Map<String,String> tokens = new HashMap<>();
        tokens.put("id", String.valueOf(id));

        try {
            JSONObject json = this.restClient.sendData(ConfigService.parseProperty(url, tokens), HttpMethod.GET, null);


         return DataMapper.getInstance().readObject(json.getString("data"),Taxi.class);

        } catch (IOException |JSONException e) {
            Log.e(TAG, e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

}
