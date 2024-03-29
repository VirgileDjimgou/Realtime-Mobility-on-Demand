package beetech.com.wallet.ubersplash;

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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import beetech.com.wallet.R;
import beetech.com.wallet.activity.Facebook_Activity;
import beetech.com.wallet.activity.EmailLoginActivity_driver;
import beetech.com.wallet.activity.PhoneAuthActivity;
import beetech.com.wallet.activity.SplaschScreen;
import beetech.com.wallet.data.StaticConfig;

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


public class MainActivity_Driver extends AppCompatActivity {

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
    private static String TAG = "MainActivity_Driver";
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
        setContentView(R.layout.activity_main_splash_driver);
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
                Intent Facebook = new Intent(MainActivity_Driver.this, EmailLoginActivity_driver.class);
                startActivity(Facebook);
                finish();
            }
        });

        facebook_butt = (Button) findViewById(R.id.facebook_login);
        // call Facebook Login...
        facebook_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Facebook = new Intent(MainActivity_Driver.this, Facebook_Activity.class);
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
                Intent Phone_auth = new Intent(MainActivity_Driver.this, PhoneAuthActivity.class);
                // startActivity(new Intent(this, MainActivityPhone.class));
                // donate.putExtra("TO_ADDRESS", "0xa9981a33f6b1A18da5Db58148B2357f22B44e1e0");
                startActivity(Phone_auth);
                finish();
            }
        });

    }

    private void initFirebase() {
        //Khoi tao thanh phan de dang nhap, dang ky
        auth= FirebaseAuth.getInstance();
        // authUtils = new MainActivity_Driver.AuthUtils();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    StaticConfig.UID = user.getUid();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    if (firstTimeAccess) {
                        startActivity(new Intent(MainActivity_Driver.this, SplaschScreen.class));
                        // startActivity(new Intent(EmailLoginActivity_driver.this, MapsActivity.class));

                        // startActivity(new Intent(EmailLoginActivity_driver.this, LocationsOverviewActivity.class));

                        MainActivity_Driver.this.finish();
                    }
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                firstTimeAccess = false;
            }
        };

        //Khoi tao dialog waiting khi dang nhap
        waitingDialog = new LovelyProgressDialog(this).setCancelable(false);
    }

    private void findView() {
        mVideoView = (VideoView) findViewById(R.id.videoView);

        contianer = (ViewGroup) findViewById(R.id.container);
        //appName = (TextView) findViewById(R.id.appName);


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
                            "you are  selected Moto-Driver Option !", Toast.LENGTH_LONG).show();
                    Type_Of_User = "Moto-Driver";
                }
                else {
                    // If the switch button is off

                    // Show the switch button checked status as toast message
                    Toast.makeText(getApplicationContext(),
                            "you are selected Car-Drider Option !", Toast.LENGTH_LONG).show();
                    Type_Of_User = "Car-Driver";
                }
            }
        });


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
            Intent intent = new Intent(MainActivity_Driver.this, SplaschScreen.class);
            startActivity(intent);
            finish();

        }else{

            Intent intent = new Intent(MainActivity_Driver.this, SplaschScreen .class);
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
