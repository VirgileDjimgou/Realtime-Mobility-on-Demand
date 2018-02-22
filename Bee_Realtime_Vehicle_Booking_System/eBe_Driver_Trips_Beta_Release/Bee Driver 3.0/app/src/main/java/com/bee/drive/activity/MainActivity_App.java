package com.bee.drive.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.VideoView;

import com.bee.drive.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.regex.Pattern;




import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MainActivity_App extends AppCompatActivity {

    public static final String VIDEO_NAME = "welcome_video.mp4";

    public static final String AppTypeDriver_or_Rider = "Driver";

    private VideoView mVideoView;

    private Button Email_login, facebook_butt , phone_butt;

    private String Type_Of_User = "Rider";

    public  static FirebaseAuth auth;
    public  static  FirebaseAuth.AuthStateListener mAuthListener;
    private LovelyProgressDialog waitingDialog;
    public static FirebaseUser user;
    private static String TAG = "MainActivity_App";

    Animation uptodown;
    RelativeLayout layoutAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_main_splash_v2);
        getSupportActionBar().hide();


        // print KeyHashes  ...
        printhashkey();
        //Get Firebase auth instance
        // auth = FirebaseAuth.getInstance();
        waitingDialog = new LovelyProgressDialog(this).setCancelable(false);

        Email_login = (Button) findViewById(R.id.email_connect);
        // call Facebook Login...
        Email_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Facebook = new Intent(MainActivity_App.this, EmailLoginActivity.class);
                startActivity(Facebook);
                finish();
            }
        });

        facebook_butt = (Button) findViewById(R.id.facebook_login);
        // call Facebook Login...
        facebook_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Facebook = new Intent(MainActivity_App.this, Facebook_Activity.class);
                startActivity(Facebook);
                finish();
            }
        });

        // Call Phone Login
        phone_butt = (Button) findViewById(R.id.phone_connect);
        phone_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // AlertBaustelle();
                Intent Phone_auth = new Intent(MainActivity_App.this, PhoneAuthActivity.class);
                // startActivity(new Intent(this, MainActivityPhone.class));
                // donate.putExtra("TO_ADDRESS", "0xa9981a33f6b1A18da5Db58148B2357f22B44e1e0");
                startActivity(Phone_auth);
                finish();
            }
        });


        layoutAnimation = (RelativeLayout) findViewById(R.id.container);
        uptodown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        layoutAnimation.setAnimation(uptodown);

    }

    // print KeyHashes
    public void printhashkey(){

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.bee.drive",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
            Log.e("KeyHash:", e.toString());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.e("KeyHash:", e.toString());

        }

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



}
