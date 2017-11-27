package com.Bee.Booking.ubersplash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.Bee.Booking.CustomerLoginActivity;
import com.Bee.Booking.DriverLoginActivity;
import com.Bee.Booking.R;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity_App extends AppCompatActivity implements View.OnClickListener {

    public static final String VIDEO_NAME = "welcome_video.mp4";

    private VideoView mVideoView;

    private InputType inputType = InputType.NONE;

    private Button buttonLeft, buttonRight;

    private FormView formView;
    private String Type_Of_User;

    private ViewGroup contianer;

    private TextView appName;


    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;
    private Switch switch_Select;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        /*
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity_App.this, MainActivity_profile.class));
            finish();
        }
        */


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

        initView();

        File videoFile = getFileStreamPath(VIDEO_NAME);
        if (!videoFile.exists()) {
            videoFile = copyVideoFile();
        }

        playVideo(videoFile);

        playAnim();

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
    }

    private void findView() {
        mVideoView = (VideoView) findViewById(R.id.videoView);
        buttonLeft = (Button) findViewById(R.id.buttonLeft);
        buttonRight = (Button) findViewById(R.id.buttonRight);
        contianer = (ViewGroup) findViewById(R.id.container);
        formView = (FormView) findViewById(R.id.formView);
        appName = (TextView) findViewById(R.id.appName);


        inputEmail = (EditText) findViewById(R.id.edit1);
        inputPassword = (EditText) findViewById(R.id.edit2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        // Set a checked change listener for switch button
        switch_Select = (Switch) findViewById(R.id.switch1);
        switch_Select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // If the switch button is on

                    // Show the switch button checked status as toast message
                    Toast.makeText(getApplicationContext(),
                            "you are  Driver", Toast.LENGTH_LONG).show();
                    Type_Of_User = "Driver";
                }
                else {
                    // If the switch button is off

                    // Show the switch button checked status as toast message
                    Toast.makeText(getApplicationContext(),
                            "you are rider", Toast.LENGTH_LONG).show();
                    Type_Of_User = "Rider";
                }
            }
        });


        formView.post(new Runnable() {
            @Override
            public void run() {
                int delta = formView.getTop()+formView.getHeight();
                formView.setTranslationY(-1 * delta);
            }
        });
    }

    private void initView() {

        buttonRight.setOnClickListener(this);
        buttonLeft.setOnClickListener(this);
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

    /*

    private void cursorHeartBeatAnimation() {
        Animator animator = AnimatorInflater.loadAnimator(this, R.animator.cursor_heartbeat);
        animator.setRepeatMode(Animation.INFINITE);
        animator.setStartDelay(1500);
        animator.start();
    }

    */

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

    private void Driver_Or_Rider(){

        if(switch_Select.isChecked()){
            Intent intent = new Intent(MainActivity_App.this, DriverLoginActivity.class);
            startActivity(intent);
            finish();

        }else{

            Intent intent = new Intent(MainActivity_App.this, CustomerLoginActivity.class);
            startActivity(intent);
            finish();
        }


    }

    private void Register(){


        // add

        String email_sign_up = inputEmail.getText().toString().trim();
        String password_sign_up = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email_sign_up)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password_sign_up)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            formView.animate().translationY(0).alpha(1).setDuration(500).start();
            return;
        }

        if (password_sign_up.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            formView.animate().translationY(0).alpha(1).setDuration(500).start();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        //create user
        auth.createUserWithEmailAndPassword(email_sign_up, password_sign_up)
                .addOnCompleteListener(MainActivity_App.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(MainActivity_App.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity_App.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            formView.animate().translationY(0).alpha(1).setDuration(500).start();
                            buttonLeft.setText(R.string.button_confirm_signup);
                            buttonRight.setText(R.string.button_cancel_signup);
                        } else {
                            Driver_Or_Rider();
                            //startActivity(new Intent(MainActivity_App.this, MainActivity_profile.class));
                            //finish();
                        }
                    }
                });
    }
    private void Login(){

        final String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            formView.animate().translationY(0).alpha(1).setDuration(500).start();

            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            formView.animate().translationY(0).alpha(1).setDuration(500).start();
            return;
        }


        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Login Dialog")
                .setMessage("Are you sure you want to login as "+Type_Of_User+" ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        progressBar.setVisibility(View.VISIBLE);

                        //authenticate user
                        auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(MainActivity_App.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.
                                        progressBar.setVisibility(View.GONE);
                                        if (!task.isSuccessful()) {
                                            // there was an error
                                            if (password.length() < 6) {
                                                inputPassword.setError(getString(R.string.minimum_password));
                                                formView.animate().translationY(0).alpha(1).setDuration(500).start();

                                                buttonLeft.setText(R.string.button_confirm_login);
                                                buttonRight.setText(R.string.button_cancel_login);
                                            } else {
                                                Toast.makeText(MainActivity_App.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                                buttonLeft.setText(R.string.button_confirm_login);
                                                buttonRight.setText(R.string.button_cancel_login);
                                            }
                                        } else {
                                            Driver_Or_Rider();
                                            //Intent intent = new Intent(MainActivity_App.this, MainActivity_profile.class);
                                            //startActivity(intent);
                                            // finish();
                                        }
                                    }
                                });
                    }

                })
                .setNegativeButton("No", null)
                .show();




    }

    @Override
    public void onClick(View view) {
        int delta = formView.getTop()+formView.getHeight();
        switch (inputType) {
            case NONE:

                formView.animate().translationY(0).alpha(1).setDuration(500).start();
                if (view == buttonLeft) {
                    inputType = InputType.LOGIN;
                    buttonLeft.setText(R.string.button_confirm_login);
                    buttonRight.setText(R.string.button_cancel_login);
                } else if (view == buttonRight) {
                    inputType = InputType.SIGN_UP;
                    buttonLeft.setText(R.string.button_confirm_signup);
                    buttonRight.setText(R.string.button_cancel_signup);
                }

                break;
            case LOGIN:

                formView.animate().translationY(-1 * delta).alpha(0).setDuration(500).start();
                if (view == buttonLeft) {
                    Login();

                } else if (view == buttonRight) {

                }
                inputType = InputType.NONE;
                buttonLeft.setText(R.string.button_login);
                buttonRight.setText(R.string.button_signup);


                // add
                break;
            case SIGN_UP:

                formView.animate().translationY(-1 * delta).alpha(0).setDuration(500).start();
                if (view == buttonLeft) {

                    Register();
                } else if (view == buttonRight) {

                }
                inputType = InputType.NONE;
                buttonLeft.setText(R.string.button_login);
                buttonRight.setText(R.string.button_signup);


                break;
        }
    }

    enum InputType {
        NONE, LOGIN, SIGN_UP;
    }
}
