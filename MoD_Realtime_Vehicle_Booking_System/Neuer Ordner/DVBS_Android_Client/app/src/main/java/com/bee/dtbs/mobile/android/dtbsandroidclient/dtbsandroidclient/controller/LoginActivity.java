package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.AuthenticationService;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.config.DtbsPreferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.driver.DriverActivity;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.passenger.PassengerMainActivity;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Account;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.gcm.GcmRegistrationIntentService;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.utils.http.RestClient;

import org.json.JSONException;

import java.io.IOException;

/**
 * A fragment representing a collection of bookings.
 *
 * @author djimgou patrick virgile
 */
public class LoginActivity extends AppCompatActivity {

    // TAG used for logging.
    private static final String TAG = LoginActivity.class.getName();

    private Button btnLogin;
    private EditText txtUsername;
    private EditText txtPassword;
    private Button btnRegister;
    private Button btnForgotPassword;
    private LoginActivity context = this;

    private AuthenticationService authenticationService;

    /**
     * Default constructor.
     */
    public LoginActivity() {
        this.authenticationService = new AuthenticationService();
    }

    /**
     * Invokes once activity has initialised.
     *
     * @param savedInstanceState a mapping from String values to various parcelable types.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.bee.dtbs.mobile.android.dtbsandroidclient.R.layout.activity_login);
        this.getSupportActionBar().hide();

        txtUsername = (EditText) findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_username);
        txtPassword = (EditText) findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_password);
        btnLogin = (Button) findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.btn_login);
        btnRegister = (Button) findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.btn_register);
        btnForgotPassword = (Button) findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.btn_forgotten_password);

        // Acquire Google Cloud messenger registration token.
        if(this.checkPlayServices()){
            Intent gcmRegistration = new Intent(this, GcmRegistrationIntentService.class);
            startService(gcmRegistration);
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(newIntent);
            }
        });

        // register login button event handler
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateUserInput()) {

                /* Async authentication task to prevent blocking UI thread */
                    new AsyncTask<String, Void, Account>() {

                        private final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
                        private Exception exception = null;
                        private AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();

                        /**
                         * Invoked on sign in button press.
                         */
                        protected void onPreExecute() {
                            this.dialog.setMessage(getString(com.bee.dtbs.mobile.android.dtbsandroidclient.R.string.progress_sign_in_message));
                            this.dialog.show();
                        }

                        /**
                         * Authentication background task.
                         *
                         * @param params username and password to authenticate with.
                         * @return user's account if authenticated, else null.
                         */
                        @Override
                        protected Account doInBackground(String... params) {

                            Account account = null;

                            try {
                                account = authenticationService.login(params[0], params[1],
                                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this)
                                                .getString(DtbsPreferences.GCM_TOKEN, "notSet"));

                            } catch (IOException | JSONException | IllegalArgumentException ex) {
                                exception = ex;
                            }

                            return account;
                        }

                        /**
                         * Handle to manage result of background task.
                         *
                         * @param result result of background task.
                         */
                        @Override
                        protected void onPostExecute(final Account result) {

                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(com.bee.dtbs.mobile.android.dtbsandroidclient.R.string.btn_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.cancel();
                                }
                            });

                            if (this.dialog.isShowing()) {
                                this.dialog.dismiss();
                            }

                            if ((exception != null)) {
                                alertDialog.setMessage(exception.getMessage());
                                alertDialog.show();
                            }else{
                                Account.setInstance(result);
                                RestClient.getInstance().setAuthHeader(result.getUsername(), result.getPassword());
                                Intent newIntent;
                                if(result.isDriver()){
                                    newIntent = new Intent(LoginActivity.this, DriverActivity.class);
                                }else{
                                    newIntent = new Intent(LoginActivity.this, PassengerMainActivity.class);
                                }
                                startActivity(newIntent);
                            }
                        }

                    }.execute(txtUsername.getText().toString(), txtPassword.getText().toString());
                }
            }
        });

        // register forgotten password lister
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txtUsername.getText().toString().length() == 0){
                    txtUsername.setError(
                            getString(com.bee.dtbs.mobile.android.dtbsandroidclient.R.string.blank_field));
                    return;
                }

                /* Async authentication task to prevent blocking UI thread */
                new AsyncTask<String, Void, Void>() {

                    private final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
                    private Exception exception = null;
                    private AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                    private String username;

                    /**
                     * Invoked on sign in button press.
                     */
                    protected void onPreExecute() {
                        this.dialog.setMessage(getString(com.bee.dtbs.mobile.android.dtbsandroidclient.R.string.progress_reset_message));
                        this.dialog.show();
                    }

                    /**
                     * Authentication background task.
                     *
                     * @param params username and password to authenticate with.
                     */
                    @Override
                    protected Void doInBackground(String... params) {

                        username = params[0];
                        Account.getInstance().setUsername(username);

                        try {
                             authenticationService.forgottenPassword(username);
                        } catch (IOException | JSONException| IllegalArgumentException ex) {
                            exception = ex;
                        }

                        return null;
                    }

                    /**
                     * Handle to manage result of background task.
                     *
                     */
                    @Override
                    protected void onPostExecute(final Void type) {

                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(com.bee.dtbs.mobile.android.dtbsandroidclient.R.string.btn_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.cancel();
                            }
                        });

                        if (this.dialog.isShowing()) {
                            this.dialog.dismiss();
                        }

                        if(!(exception == null)){
                            alertDialog.setMessage(exception.getMessage());
                            alertDialog.show();
                        }else{
                            Intent newIntent = new Intent(LoginActivity.this, ForgottenPasswordActivity.class);
                            newIntent.putExtra("username",username);
                            startActivity(newIntent);
                        }
                    }

                }.execute(txtUsername.getText().toString());
            }
        });
    }

    /**
     * Return true if user has no empty username/password fields, else false.
     *
     * @return true if user has no empty username/password fields, else false.
     */
    private boolean validateUserInput() {
        if (txtUsername.getText().toString().length() == 0) {
            txtUsername.setError(
                    getString(com.bee.dtbs.mobile.android.dtbsandroidclient.R.string.blank_field));
            return false;
        }
        if (txtPassword.getText().toString().length() == 0) {
            txtPassword.setError(
                    getString(com.bee.dtbs.mobile.android.dtbsandroidclient.R.string.blank_field));
            return false;
        }
        return true;
    }

    /**
     * Check if google play services installed and available.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
