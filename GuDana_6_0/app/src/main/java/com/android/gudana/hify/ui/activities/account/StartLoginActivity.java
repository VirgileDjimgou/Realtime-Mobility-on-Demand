package com.android.gudana.hify.ui.activities.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.android.gudana.R;

public class StartLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Button connect_with_email  = (Button) findViewById(R.id.connect_with_email);
        connect_with_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_start_email = new Intent(StartLoginActivity.this, LoginActivity.class);
                StartLoginActivity.this.startActivity(intent_start_email);
                StartLoginActivity.this.finish();

            }
        });


        Button PhoneLogin = (Button) findViewById(R.id.PhoneLogin);
        PhoneLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_start_email = new Intent(StartLoginActivity.this, PhoneAuthActivity.class);
                StartLoginActivity.this.startActivity(intent_start_email);
                StartLoginActivity.this.finish();

            }
        });


    }

}
