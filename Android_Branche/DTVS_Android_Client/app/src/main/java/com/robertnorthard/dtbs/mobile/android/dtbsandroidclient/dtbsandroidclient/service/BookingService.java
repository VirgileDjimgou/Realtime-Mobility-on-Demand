package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service;

import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Booking;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.config.ConfigService;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.utils.datamapper.DataMapper;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.utils.http.HttpMethod;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.utils.http.RestClient;
import com.robertnorthard.dtbs.server.common.dto.BookingDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for encapsulating all operations related to bookings.
 *
 * @author robertnorthard
 */
public class BookingService {

    // TAG used for logging.
    private static final String TAG = BookingService.class.getName();

    // JSON ORM
    private DataMapper dataMapper;
    private RestClient restClient;

    /**
     * Default constructor for class booking service.
     */
    public BookingService(){
        this.dataMapper = DataMapper.getInstance();
        this.restClient = RestClient.getInstance();
    }

    /**
     * Find a booking.
     *
     * @param id id of booking to find.
     * @return booking if found.
     * @throws IllegalArgumentException if booking not found.
     * @throws IOException network error.
     */
    public Booking findBooking(long id)
            throws IOException, JSONException {

        Map<String,String> tokens = new HashMap<>();
        tokens.put("id", String.valueOf(id));

        Booking booking = null;

        try {
            JSONObject response = this.restClient.sendData(
                    ConfigService.parseProperty(ConfigService.getProperty("dtbs.endpoint.booking.id"),tokens), HttpMethod.GET, null);

            JSONObject data = null;

            if(response.getString("status").equals("0")){
                data = response.getJSONObject("data");

                booking =this.dataMapper.readObject(data.toString(), Booking.class);

                return booking;
            }else{
                throw new IllegalArgumentException(response.getString("data"));
            }

        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Cancel a booking.
     *
     * @param id id of booking to cancel.
     *
     * @throws IllegalArgumentException if booking not found.
     * @throws IOException network error.
     */
    public void cancelBooking(long id)
            throws IOException, JSONException {

        Map<String,String> tokens = new HashMap<>();
        tokens.put("id", String.valueOf(id));

        Booking booking = null;

        try {
            JSONObject response = this.restClient.sendData(
                    ConfigService.parseProperty(ConfigService.getProperty("dtbs.endpoint.booking.id.cancel"),tokens), HttpMethod.PUT, null);

            JSONObject data = null;

            if(!response.getString("status").equals("0")){
                throw new IllegalArgumentException(response.getString("data"));
            }

        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Find all bookings.
     *
     * @return a collection of bookings for the authenticated user.
     * @throws IOException network error.
     * @throws IllegalArgumentException response status not equal to 0.
     */
    public List<Booking> findAllBookings() throws IOException, JSONException {

        List<Booking> bookings = new ArrayList<>();

        try{
            JSONObject response = this.restClient.sendData(
                    ConfigService.getProperty("dtbs.endpoint.booking.history"), HttpMethod.GET, null);

            JSONArray data = null;

            if(response.getString("status").equals("0")){

                data = response.getJSONArray("data");

                for(int i = 0; i < data.length(); i++){
                    Booking booking = this.dataMapper.readObject(data.getJSONObject(i).toString(), Booking.class);
                    bookings.add(booking);
                }

                return bookings;
            }else{
                throw new IllegalArgumentException(response.getString("data"));
            }
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Request a ride.
     *
     * @param booking booking to make.
     * @return returned confirmed booking.
     * @throws IOException
     * @throws JSONException
     */
    public Booking bookRide(BookingDto booking) throws IOException,JSONException {

        try{
            JSONObject params = new JSONObject(this.dataMapper.getObjectAsJson(booking));
            JSONObject response = this.restClient.sendData(
                    ConfigService.getProperty("dtbs.endpoint.booking"), HttpMethod.POST, params);

            JSONObject data = null;

            if(response.getString("status").equals("0")){
                data = response.getJSONObject("data");
                return this.dataMapper.readObject(data.toString(), Booking.class);
            }else{
                throw new IllegalArgumentException(response.getString("data"));
            }
        } catch (IOException e) {
            throw e;
        } catch (JSONException e) {
            throw e;
        }
    }

    /**
     * Find active bookings.
     *
     * @return active booking, else null.
     * @throws IOException
     * @throws JSONException
     */
    public Booking findActiveBooking() throws IOException,JSONException {

        try{

            JSONObject response = this.restClient.sendData(
                    ConfigService.getProperty("dtbs.endpoint.booking.active"), HttpMethod.GET, null);

            JSONObject data = null;

            if(response.getString("status").equals("0")){

                data = response.getJSONObject("data");

                return this.dataMapper.readObject(data.toString(), Booking.class);

            }else{
                return null;
            }
        } catch (IOException e) {
            throw e;
        } catch (JSONException e) {
            throw e;
        }
    }
}
