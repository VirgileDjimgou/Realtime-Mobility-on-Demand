package com.android.gudana.video_player;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.android.gudana.R;
import com.halilibo.bettervideoplayer.BetterVideoCallback;
import com.halilibo.bettervideoplayer.BetterVideoPlayer;

public class BackgroundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vid_activity_background);

        BetterVideoPlayer bvp = findViewById(R.id.bvp);
        bvp.setSource(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.hify_sound));
        bvp.setCallback(new BetterVideoCallback() {
            @Override
            public void onStarted(BetterVideoPlayer player) {

            }

            @Override
            public void onPaused(BetterVideoPlayer player) {

            }

            @Override
            public void onPreparing(BetterVideoPlayer player) {

            }

            @Override
            public void onPrepared(BetterVideoPlayer player) {
                player.setVolume(0,0);
            }

            @Override
            public void onBuffering(int percent) {

            }

            @Override
            public void onError(BetterVideoPlayer player, Exception e) {

            }

            @Override
            public void onCompletion(BetterVideoPlayer player) {

            }

            @Override
            public void onToggleControls(BetterVideoPlayer player, boolean isShowing) {

            }
        });

        findViewById(R.id.example_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),
                    "You can put a background video on your login page ;)",
                    Toast.LENGTH_LONG)
                    .show();
            }
        });
    }
}
