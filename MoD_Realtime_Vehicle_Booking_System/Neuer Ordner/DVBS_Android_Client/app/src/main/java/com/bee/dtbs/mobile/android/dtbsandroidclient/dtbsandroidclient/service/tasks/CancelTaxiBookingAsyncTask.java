package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.cache.AllBookings;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.BookingService;

import org.json.JSONException;

import java.io.IOException;

/**
 * Async task to cancel a taxi booking.
 *
 * @author djimgou patrick  virgile
 */
public class CancelTaxiBookingAsyncTask  extends AsyncTask<Long,Void,Boolean> {

    // TAG used for logging.
    private static final String TAG = CancelTaxiBookingAsyncTask.class.getName();

    private BookingService bookingService;

    private final ProgressDialog dialog;
    private Exception exception;
    private AlertDialog alertDialog;

    public CancelTaxiBookingAsyncTask(Activity activity){
        this.dialog = new ProgressDialog(activity);
        this.exception = null;
        this.alertDialog = new AlertDialog.Builder(activity).create();
        this.bookingService = new BookingService();
    }


    /**
     * Invoked on cancel booking button press.
     */
    protected void onPreExecute() {
        this.dialog.setMessage("Canceling booking...");
        this.dialog.show();
    }

    /**
     * Invoked in background.
     *
     * @param params task params.
     */
    @Override
    protected Boolean doInBackground(Long... params) {

        try {

            bookingService.cancelBooking(params[0]);
            return true;
        } catch (IOException | JSONException | IllegalArgumentException e) {
            exception = e;
        }

        return false;
    }

    /**
     * Handle to manage result of background task.
     *
     * @param result result of background task.
     */
    @Override
    protected void onPostExecute(final Boolean result) {

        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
            AllBookings.getInstance().setActiveBooking(null);
        }
        if(this.exception != null) {
            alertDialog.setMessage(exception.getMessage());
            alertDialog.show();
        }
    }
}