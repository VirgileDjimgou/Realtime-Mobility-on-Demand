package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.R;
import com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.tasks.RegisterAccountAsyncTask;

/**
 * Controller for registering new accounts.
 */
public class RegisterActivity extends AppCompatActivity {

    // TAG used for logging.
    private static final String TAG = RegisterActivity.class.getName();

    private EditText txtCommonName;
    private EditText txtFamilyName;
    private EditText txtUsername;
    private EditText txtEmail;
    private EditText txtPhoneNumber;
    private EditText txtPassword;
    private Button btnRegister;

    /**
     * The system calls this method when creating the fragment.
     * Initialises essential components of the fragment
     * @param savedInstanceState state to restore.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.txtCommonName = (EditText)findViewById(R.id.txt_register_common_name);
        this.txtFamilyName = (EditText)findViewById(R.id.txt_register_family_name);
        this.txtUsername = (EditText)findViewById(R.id.txt_register_username);
        this.txtEmail = (EditText)findViewById(R.id.txt_register_email);
        this.txtPhoneNumber = (EditText)findViewById(R.id.txt_register_phone_number);
        this.txtPassword = (EditText)findViewById(R.id.txt_register_password);
        this.btnRegister = (Button)findViewById(R.id.btn_register_register);

        // register button access listener.
        this.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new RegisterAccountAsyncTask(RegisterActivity.this).execute(txtUsername.getText().toString(),
                        txtCommonName.getText().toString(),
                        txtFamilyName.getText().toString(),
                        txtEmail.getText().toString(),
                        txtPhoneNumber.getText().toString(),
                        txtPassword.getText().toString());
            }
        });
    }
}
