package com.bee.passenger.ubersplash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bee.passenger.R;
import com.bee.passenger.activity.Facebook_Activity;
import com.bee.passenger.activity.EmailLoginActivity;
import com.bee.passenger.activity.PhoneAuthActivity;
import com.bee.passenger.activity.SplaschScreen;
import com.bee.passenger.data.StaticConfig;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity_App extends AppCompatActivity {

    public static final String VIDEO_NAME = "welcome_video.mp4";

    public static final String AppTypeDriver_or_Rider = "Driver";

    private VideoView mVideoView;

    private Button Email_login, facebook_butt , phone_butt;

    private FormView formView;
    private String Type_Of_User = "Rider";


    private ViewGroup contianer;
    private TextView appName;
    private EditText inputEmail, inputPassword;
    public  static FirebaseAuth auth;
    public  static  FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;
    private Switch switch_Select;
    private LovelyProgressDialog waitingDialog;
    public static FirebaseUser user;
    private static String TAG = "MainActivity_App";
    FloatingActionButton fab;
    private boolean firstTimeAccess;
    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


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
        setContentView(R.layout.activity_main_splash);
        getSupportActionBar().hide();

        findView();

        File videoFile = getFileStreamPath(VIDEO_NAME);
        if (!videoFile.exists()) {
            videoFile = copyVideoFile();
        }

        playVideo(videoFile);

        playAnim();

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

    }

    private void findView() {
        mVideoView = (VideoView) findViewById(R.id.videoView);

        contianer = (ViewGroup) findViewById(R.id.container);
        //appName = (TextView) findViewById(R.id.appName);


        inputEmail = (EditText) findViewById(R.id.edit1);
        inputPassword = (EditText) findViewById(R.id.edit2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);





    }



    private void playVideo(File videoFile) {
        mVideoView.setVideoPath(videoFile.getPath());
        mVideoView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        });
    }



    private void playAnim() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(appName, "alpha", 0,1);
        anim.setDuration(8000);
        anim.setRepeatCount(1000);
        anim.setRepeatMode(ObjectAnimator.RESTART);
        anim.setStartDelay(500);
        // anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.start();
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                appName.setVisibility(View.INVISIBLE);
            }
        });
    }



    @NonNull
    private File copyVideoFile() {
        File videoFile;
        try {
            FileOutputStream fos = openFileOutput(VIDEO_NAME, MODE_PRIVATE);
            InputStream in = getResources().openRawResource(R.raw.welcome_video);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = in.read(buff)) != -1) {
                fos.write(buff, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        videoFile = getFileStreamPath(VIDEO_NAME);
        if (!videoFile.exists())
            throw new RuntimeException("video file has problem, are you sure you have welcome_video.mp4 in res/raw folder?");
        return videoFile;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }

     //  function to start right  navigation drawer ... also  for Driver or for  Rider
    private void Driver_Or_Rider(){
        //

        if(switch_Select.isChecked()){
            Intent intent = new Intent(MainActivity_App.this, SplaschScreen.class);
            startActivity(intent);
            finish();

        }else{

            Intent intent = new Intent(MainActivity_App.this, SplaschScreen .class);
            startActivity(intent);
            finish();
        }


    }



    private boolean validate(String emailStr, String password) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return (password.length() > 0 || password.equals(";")) && matcher.find();
    }

    public void clickPhone(View view) {
        // AlertBaustelle();
        Toast.makeText(this, "Phone Authentification ", Toast.LENGTH_SHORT).show();
        Intent Phone_auth = new Intent(this, PhoneAuthActivity.class);
        // startActivity(new Intent(this, MainActivityPhone.class));
        // donate.putExtra("TO_ADDRESS", "0xa9981a33f6b1A18da5Db58148B2357f22B44e1e0");
        startActivity(Phone_auth);
    }

}
