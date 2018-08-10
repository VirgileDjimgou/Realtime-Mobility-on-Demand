package com.android.rivchat.project_3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.rivchat.R;
import com.android.rivchat.project_3.ui.*;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p3_activity_main);
        Intent intent = new Intent(this, ChatListActivity.class);
        startActivity(intent);
    }
}
