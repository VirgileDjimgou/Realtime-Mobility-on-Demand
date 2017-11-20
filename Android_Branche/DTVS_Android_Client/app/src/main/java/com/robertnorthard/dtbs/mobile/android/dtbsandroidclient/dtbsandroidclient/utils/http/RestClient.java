package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.utils.http;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * HTTP utilities class sending/receiving RESTful API requests that consume and produce content
 * in JSON.
 *
 * @author robertnorthard
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class RestClient {

    private static RestClient httpUtils;

    // TAG used for logging.
    private static final String TAG = RestClient.class.getName();

    private static final int HTTP_CONNECT_TIMEOUT = 10000;
    private static final int HTTP_READ_TIMEOUT = 10000;
    private static final boolean HTTP_USE_CACHE = false;
    private static final String HTTP_STRING_ENCODING = "UTF-8";

    private String basicAuthHeaderValue;

    private RestClient(){
        // private as singleton.
    }

    /**
     * Return instance of HttpUtils if it exists else create a new instance and return.
     *
     * @return a instance of HttpUtils.
     */
    public static RestClient getInstance(){
        if(RestClient.httpUtils == null){
            synchronized (RestClient.class){
                RestClient.httpUtils = new RestClient();
            }
        }

        return RestClient.httpUtils;
    }

    /**
     * Set HTTP basic authentication credentials.
     *
     * @param username username.
     * @param password password.
     */
    public void setAuthHeader(String username, String password){
        this.basicAuthHeaderValue = AuthenticationUtils.basicAuthEncode(username,password);
    }

    /**
     * Send data and return a json object corresponding to the requested resource.
     *
     * @param resource  url of resource.
     * @param method    HTTP method type.
     * @param jsonParam json data parameters. Null if no parameter.
     * @return a JSON object corresponding to the requested resource.
     */
    public JSONObject sendData(String resource, HttpMethod method, JSONObject jsonParam)
            throws JSONException, IOException {

        JSONObject data = null;

        try {
            URL url = new URL(resource);

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
           if(this.basicAuthHeaderValue != null) {
                http.addRequestProperty("Authorization", this.basicAuthHeaderValue);
           }

            http.setConnectTimeout(HTTP_CONNECT_TIMEOUT);
            http.setReadTimeout(HTTP_READ_TIMEOUT);
            http.setUseCaches(HTTP_USE_CACHE);

            // set HTTP request method.
            http.setRequestMethod(
                    method.toString());

            // Set HTTP content type.
            if (method != HttpMethod.GET && jsonParam != null) {
                http.setRequestProperty(
                        "Content-Type",
                        HttpContentTypes.JSON.toString());
                http.setRequestProperty("Content-Length",String.valueOf(jsonParam.toString().getBytes().length));
            }

            // HTTP GET requests will not have a payload - only query parameters.
            if (jsonParam != null) {
                writeToStream(http.getOutputStream(), jsonParam);
            }

            // Different output stream for errors. Why?
            if(http.getResponseCode() != 200){
                return readResponse(http.getErrorStream());
            }else {
                return readResponse(http.getInputStream());
            }
        } catch (IOException | JSONException e) {
            Log.d(TAG, e.toString());
            throw e;
        }
    }

    /**
     * Write JSON to output stream.
     *
     * @param outputstream outputstream to write to.
     * @param jsonParam JSON parameters.
     * @throws IOException if input/output error.
     */
    public void writeToStream(OutputStream outputstream, JSONObject jsonParam) throws IOException {
        // write bytes
        try (BufferedWriter out = new BufferedWriter(
                     new OutputStreamWriter(outputstream, RestClient.HTTP_STRING_ENCODING));) {
            out.write(jsonParam.toString());
        }
    }

    /**
     * Read input byte stream and convert result to JSON object.
     *
     * @param is input stream to read from.
     * @return a JSON object.
     * @throws IOException input/output error.
     * @throws JSONException parse error.
     */
    public JSONObject readResponse(InputStream is)
            throws IOException, JSONException {

        BufferedReader in = new BufferedReader(new InputStreamReader(
                is));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        return new JSONObject(response.toString());
    }
}
