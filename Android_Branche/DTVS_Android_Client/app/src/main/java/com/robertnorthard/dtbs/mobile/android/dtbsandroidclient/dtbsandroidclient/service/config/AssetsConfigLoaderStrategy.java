package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.config;

import android.annotation.TargetApi;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.DTBSApplication;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;


/**
 * This class facilitates reading properties from files from the assets folder.
 * @author robertnorthard
 */
public class AssetsConfigLoaderStrategy implements ConfigLoaderStrategy {

    // TAG used for logging.
    private static final String TAG = AssetsConfigLoaderStrategy.class.getName();

    /**
     * Return properties from file on the class path.
     *
     * @param file file to read properties from.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public Properties getConfig(String file) {

        AssetManager assetManager = DTBSApplication.getInstance().getAssets();
        Properties properties = new Properties();

        try {
            try (Reader reader = new InputStreamReader(assetManager.open(file))) {
                properties.load(reader);
                return properties;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }
}