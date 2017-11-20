package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.utils.datamapper;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import java.io.IOException;

/**
 * Class represents a DataMapper singleton.
 *
 * @author robertnorthard
 */
public class DataMapper extends ObjectMapper {

    // TAG used for logging.
    private static final String TAG = DataMapper.class.getName();

    private static DataMapper dataMapper;

    private DataMapper() {
        // private as singleton
    }

    /**
     * Return an instance of data mapper. If it is null create a new instance.
     *
     * @return a instance of data mapper.
     */
    public static DataMapper getInstance() {
        if (DataMapper.dataMapper == null) {
            synchronized (DataMapper.class) {
                DataMapper.dataMapper = new DataMapper();
                DataMapper.dataMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                DataMapper.dataMapper.setPropertyNamingStrategy(
                        PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
            }
        }

        return DataMapper.dataMapper;
    }

    /**
     * Get object as JSON.
     * @param obj object to convert to a string.
     * @return object converted to JSON.
     */
    public String getObjectAsJson(Object obj) {
        try {
            return this.writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            Log.e(TAG, ex.getMessage());
            return null;
        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage());
            return null;
        }
    }

    /**
     * Read JSON and return corresponding object.
     *
     * @param json json to convert to object.
     * @param type type of object.
     * @return object representation of JSON.
     */
    public <T> T readObject(String json, Class<T> type){

        try {
            return type.cast(this.readValue(json, type));
        } catch (IOException e) {
            return null;
        }
    }
}

