package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.config;

import android.util.Log;

import java.util.Map;
import java.util.Properties;

/**
 * Represents a configuration service.
 *
 * @author djimgou patrick  virgile
 */
public class ConfigService {

    // TAG used for logging.
    private static final String TAG = ConfigService.class.getName();

    private static ConfigService configService = null;
    private static Properties properties = null;

    /**
     * Represents configuration loaders
     */
    private static final ConfigLoaderStrategy[] LOADERS = new ConfigLoaderStrategy[] {
            new AssetsConfigLoaderStrategy()
    };

    private ConfigService() {
        properties = ConfigService.getConfig("application.properties");
    }

    public static Properties getConfig(String conf) {
        try {
            for (ConfigLoaderStrategy configLoader : LOADERS) {
                Properties properties = configLoader.getConfig(conf);

                if (properties != null)
                    return properties;
            }
        } catch (RuntimeException e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    /**
     * Return property value for the provided key.
     *
     * @param key return property value  for the provided key.
     * @return property value for the provided key. Null if not found.
     */
    public static String getProperty(String key){
        if(configService == null){
            synchronized (ConfigService.class){
                ConfigService.configService = new ConfigService();
            }
        }

        return properties == null ? null : properties.getProperty(key);
    }

    /**
     * Tokenise a property token with a provided value.
     * e.g. api.endpoint=http://127.0.0.1/api/{id} becomes http://127.0.0.1/api/10
     *
     * @param property property value to parse.
     * @param tokens tokens to value map.
     */
    public static String parseProperty(String property, Map<String,String> tokens) {

        if(property == null || tokens == null || tokens.isEmpty()){
            throw new IllegalArgumentException();
        }

        for(String key : tokens.keySet()){
            property = property.replace("{" + key.trim() + "}", tokens.get(key));
        }

        return property;
    }
}