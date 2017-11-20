package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.R;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Account;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.AuthenticationService;

import org.json.JSONException;

import java.io.IOException;

/**
 * Controller class for forgotten password.
 *
 * @author robertnorthard
 */
public class ForgottenPasswordActivity extends AppCompatActivity {
    private EditText txtAccessCode;
    private EditText txtNewPassword;
    private EditText txtNewPassword2;
    private Button btnResetPassword;
    private String username;

    private AuthenticationService authenticationService;

    public ForgottenPasswordActivity(){
        authenticationService = new AuthenticationService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username = getIntent().getStringExtra("username");

        txtAccessCode = (EditText)findViewById(R.id.txt_access_code);
        txtNewPassword = (EditText)findViewById(R.id.txt_new_password);
        txtNewPassword2 = (EditText)findViewById(R.id.txt_new_password2);
        btnResetPassword = (Button)findViewById(R.id.btn_reset_password);


        // register forgotten password lister
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!validateFields(getString(R.string.blank_field), txtAccessCode, txtNewPassword, txtNewPassword2)){
                    return;
                }

                if(!txtNewPassword.getText().toString().equals(txtNewPassword2.getText().toString())){
                    txtNewPassword.setError(getString(R.string.password_match));
                    txtNewPassword2.setError(getString(R.string.password_match));
                    return;
                }

                /* Async authentication task to prevent blocking UI thread */
                new AsyncTask<String, Void, Void>() {

                    private final ProgressDialog dialog = new ProgressDialog(ForgottenPasswordActivity.this);
                    private Exception exception = null;
                    private AlertDialog alertDialog = new AlertDialog.Builder(ForgottenPasswordActivity.this).create();

                    /**
                     * Invoked on sign in button press.
                     */
                    protected void onPreExecute() {
                        this.dialog.setMessage(getString(R.string.progress_reset_message));
                        this.dialog.show();
                    }

                    /**
                     * Authentication background task.
                     *
                     * @param params username and password to authenticate with.
                     * @return user's account if authenticated, else null.
                     */
                    @Override
                    protected Void doInBackground(String... params) {
                        try {
                            authenticationService.resetPassword(Account.getInstance().getUsername(), params[1], params[2]);
                        } catch (IOException | JSONException | IllegalArgumentException ex) {
                            exception = ex;
                        }

                        return null;
                    }

                    /**
                     * Handler to manage result of background task.
                     *
                     */
                    @Override
                    protected void onPostExecute(final Void result) {

                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
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
                            Intent newIntent = new Intent(ForgottenPasswordActivity.this, LoginActivity.class);
                            startActivity(newIntent);
                        }
                    }

                }.execute(username,
                        txtAccessCode.getText().toString(),
                        txtNewPassword2.getText().toString());
            }
        });
    }

    /**
     * Validate fields, if empty display error message.
     *
     * @param errorMessage error message to set.
     * @param fields fields to check.
     * @return true if fields validate else false.
     */
    private boolean validateFields(String errorMessage, EditText... fields){

        if(fields == null){
            throw new IllegalArgumentException();
        }

        boolean error = true;

        for(int i = 0; i < fields.length; i++){
            if(fields[i].getText().toString().trim().length() == 0){
                fields[i].setError(errorMessage);
                error = false;
            }else{
                fields[i].setError(null);
            }
        }

        return error;
    }
}
