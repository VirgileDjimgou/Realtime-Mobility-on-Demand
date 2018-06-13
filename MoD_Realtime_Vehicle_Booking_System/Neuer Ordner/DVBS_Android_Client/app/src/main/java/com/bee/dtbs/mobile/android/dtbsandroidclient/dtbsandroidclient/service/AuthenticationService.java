package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service;

import android.util.Log;

import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Account;
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
 * A service class for handling all authentication related activities.
 *
 * @author djimgou patrick  virgile
 */
public class AuthenticationService {

    // TAG used for logging.
    private static final String TAG = AuthenticationService.class.getName();

    private RestClient restClient;

    /**
     * Default constructor for class authentication service.
     */
    public AuthenticationService(){
        this.restClient = RestClient.getInstance();
    }

    /**
     * Authenticate user.
     *
     * @param username username to authenticate with.
     * @param password password in plain text.
     * @param gcmRegId Google cloud messenger registration id.
     * @throws IOException network error.
     * @throws IllegalArgumentException  if rest response code not equal to 0.
     */
    public Account login(String username, String password, String gcmRegId)
            throws IOException, JSONException {

        JSONObject params = new JSONObject();

        Account account = null;

        try {
            // construct request params
            params.put("username", username);
            params.put("password", password);
            params.put("gcm_reg_id", gcmRegId);

            JSONObject response = this.restClient.sendData(
                    ConfigService.getProperty("dtbs.endpoint.auth.login"), HttpMethod.POST, params);

            JSONObject data = null;

            if(response.getString("status").equals("0")){
                data = response.getJSONObject("data");
                account = DataMapper.getInstance().readObject(data.toString(), Account.class);
                account.setPassword(password);
                return account;

            }else{
                throw new IllegalArgumentException(response.getString("data"));
            }

        } catch (IOException e) {
            Log.e(TAG,e.toString());
            throw e;
        }
    }

    /**
     * Reset password with provided username.
     *
     * @param username username to send temporary access code for.
     * @return true if reset sent else false.
     * @throws IOException network error.
     * @throws IllegalArgumentException  if rest response code not equal to 0.
     */
    public void forgottenPassword(String username)
            throws IOException, JSONException {

        try{
            String url = ConfigService.getProperty("dtbs.endpoint.account.username.reset");

            Map<String, String> tokens = new HashMap<>();
            tokens.put("username", username);

            JSONObject response = this.restClient.sendData(
                    ConfigService.parseProperty(url, tokens), HttpMethod.POST, null);

            if(!response.getString("status").equals("0")) {
                throw new IllegalArgumentException(response.getString("data"));
            }
        } catch (IOException e) {
            Log.e(TAG,e.toString());
            throw e;
        }
    }

    /**
     * Reset password with temporary access code.
     *
     * @param username username.
     * @param temporaryAccessCode temporary access code.
     * @param newPassword new password.
     * @return true if password reset successful else false.
     * @throws IOException network error.
     * @throws IllegalArgumentException  if rest response code not equal to 0.
     */
    public void resetPassword(String username, String temporaryAccessCode, String newPassword) throws IOException, JSONException {
        JSONObject params = new JSONObject();

        try{
            String url = ConfigService.getProperty("dtbs.endpoint.account.username.reset.code");

            Map<String, String> tokens = new HashMap<>();
            tokens.put("username",username.trim());
            tokens.put("code",temporaryAccessCode.trim());

            params.put("password", newPassword.trim());

            JSONObject response = this.restClient.sendData(
                    ConfigService.parseProperty(url, tokens), HttpMethod.POST, params);

            if(!response.getString("status").equals("0")) {
                throw new IllegalArgumentException(response.getString("data"));
            }
        } catch (IOException e) {
            Log.e(TAG,e.toString());
            throw e;
        }
    }

    /**
     * Register new user account.
     *
     * @param account account to register.
     */
    public void registerAccount(Account account) throws JSONException, IOException{

        try{
            String url = ConfigService.getProperty("dtbs.endpoint.account.register");

            JSONObject params = new JSONObject(DataMapper.getInstance().getObjectAsJson(account));

            JSONObject response = this.restClient.sendData(
                    url, HttpMethod.POST, params);

            if(!response.getString("status").equals("0")) {
                throw new IllegalArgumentException(response.getString("data"));
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG,e.toString());
            throw e;
        }
    }


    /**
     * Log a user out.
     *
     * @param username username to authenticate with.
     * @param password password in plain text.
     * @throws IOException network error.
     * @throws IllegalArgumentException  if rest response code not equal to 0.
     */
    public void logout(String username, String password)
            throws IOException, JSONException {

        JSONObject params = new JSONObject();

        try {
            // construct request params
            params.put("username", username);
            params.put("password", password);

            JSONObject response = this.restClient.sendData(
                    ConfigService.getProperty("dtbs.endpoint.auth.logoout"), HttpMethod.POST, params);

            JSONObject data = null;

            if(!response.getString("status").equals("0")){
                throw new IllegalArgumentException(response.getString("data"));
            }

        } catch (IOException e) {
            Log.e(TAG,e.toString());
            throw e;
        }
    }
}

