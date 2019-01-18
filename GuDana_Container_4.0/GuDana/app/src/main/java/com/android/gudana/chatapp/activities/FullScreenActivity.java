package com.android.gudana.chatapp.activities;

/**
 * This is a part of ChatApp Project (https://github.com/h01d/ChatApp)
 * Licensed under Apache License 2.0
 *
 * @author  Raf (https://github.com/h01d)
 * @version 1.1
 * @since   27/02/2018
 */

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class FullScreenActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ca_activity_full_screen);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle(R.string.see_all_users);

        final String url = getIntent().getStringExtra("imageUrl");

        final ImageView image= findViewById(R.id.a_fullscreen_image);
        final TextView message = findViewById(R.id.a_fullscreen_message);

        message.setText("Loading Picture...");
        message.setVisibility(View.VISIBLE);

        Picasso.with(getApplicationContext())
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(image, new Callback()
                {
                    @Override
                    public void onSuccess()
                    {
                        message.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError()
                    {
                        Picasso.with(getApplicationContext())
                                .load(url)
                                .into(image, new Callback()
                                {
                                    @Override
                                    public void onSuccess()
                                    {
                                        message.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError()
                                    {
                                        message.setVisibility(View.VISIBLE);
                                        message.setText("Error: Could not load picture.");
                                    }
                                });
                    }
                });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
