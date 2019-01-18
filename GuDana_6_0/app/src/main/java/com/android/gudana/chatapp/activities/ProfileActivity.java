package com.android.gudana.chatapp.activities;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import com.flaviofaria.kenburnsview.KenBurnsView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity
{
    private final String TAG = "CA/ProfileActivity";


    private TextView name, status;
    private CircleImageView image;
    private KenBurnsView cover;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

    }

}