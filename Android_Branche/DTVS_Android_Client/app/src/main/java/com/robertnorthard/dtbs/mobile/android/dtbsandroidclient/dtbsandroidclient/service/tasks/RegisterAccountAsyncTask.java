package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.R;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.LoginActivity;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Account;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.AuthenticationService;

import org.json.JSONException;

import java.io.IOException;

/**
 * Async task to register a user account.
 *
 * @author robertnorthard
 */
public class RegisterAccountAsyncTask  extends AsyncTask<String,Void,Void> {

    // TAG used for logging.
    private static final String TAG = RegisterAccountAsyncTask.class.getName();

    private AuthenticationService authenticationService;
    private final ProgressDialog dialog;
    private Exception exception;
    private AlertDialog alertDialog;
    private Activity activity;

    public RegisterAccountAsyncTask(Activity activity){
        this.dialog = new ProgressDialog(activity);
        this.activity = activity;
        this.exception = null;
        this.alertDialog = new AlertDialog.Builder(activity).create();
        this.authenticationService = new AuthenticationService();
    }

    protected void onPreExecute() {
        this.dialog.setMessage(this.activity.getString(R.string.pd_registering));
        this.dialog.show();
    }

    /**
     * Invoked in background.
     *
     * @param params task params.
     */
    @Override
    protected Void doInBackground(String... params) {
        try {
            // register new user account
            Account account = new Account();

            account.setUsername(params[0]);
            account.setCommonName(params[1]);
            account.setFamilyName(params[2]);
            account.setEmail(params[3]);
            account.setPhoneNumber(params[4]);
            account.setPassword(params[5]);
            account.setRole("PASSENGER");

            authenticationService.registerAccount(account);

        } catch (IOException | JSONException | IllegalArgumentException ex) {
            Log.e(TAG, ex.getMessage());
            exception = ex;
        }

        return null;
    }

    /**
     * Handle to manage result of background task.
     *
     * @param result result of background task.
     */
    @Override
    protected void onPostExecute(final Void result) {

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
                this.activity.getString(R.string.btn_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.cancel();
            }

        });

        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }

        if(!(exception == null)) {
            alertDialog.setTitle(exception.getMessage());
            alertDialog.show();
        }else{
            Intent newIntent = new Intent(this.activity, LoginActivity.class);
            activity.startActivity(newIntent);
        }
    }
}