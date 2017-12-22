package beetech.com.wallet.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import beetech.com.wallet.R;
import beetech.com.wallet.data.SharedPreferenceHelper;
import beetech.com.wallet.data.StaticConfig;
import beetech.com.wallet.model.User;
import beetech.com.wallet.ubersplash.MainActivity_App;


import beetech.com.wallet.R;
import beetech.com.wallet.ubersplash.MainActivity_App;


public class Facebook_Activity extends AppCompatActivity {
    private static final String TAG = "AndroidBash";
    public static  FirebaseUser user;
    private EditText email;
    private EditText password;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mProgressDialog;
    private LovelyProgressDialog waitingDialog;

    //Add YOUR Firebase Reference URL instead of the following URL
    // Firebase mRef=new Firebase("https://androidbashfirebaseupdat-bd094.firebaseio.com/users/");

    //FaceBook callbackManager
    private CallbackManager callbackManager;
    private EditText firstName ; PhoneNumber;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_login_activity);

        waitingDialog = new LovelyProgressDialog(this).setCancelable(false);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + mUser.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };




        //FaceBook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                signInWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
        //

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }


    //FaceBook
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    //



    private void signInWithFacebook(AccessToken token) {
        Log.d(TAG, "signInWithFacebook:" + token);

        waitingDialog.setIcon(R.drawable.ic_add_friend)
                .setTitle("Registering....")
                .setTopColorRes(R.color.colorPrimary)
                .show();


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else{

                            //Create a new User and Save it in Firebase database
                            User newUser = new User();


                            String uid_facebook=task.getResult().getUser().getUid();
                            String name_facebook=task.getResult().getUser().getDisplayName();
                            String email_facebook=task.getResult().getUser().getEmail();
                            String image_facebook=task.getResult().getUser().getPhotoUrl().toString();
                            Toast.makeText(getApplicationContext(), uid_facebook +"   "+ email_facebook , Toast.LENGTH_LONG).show();;


                            newUser.firsName = name_facebook;
                            newUser.LastName = name_facebook;
                            newUser.phone = "enter your Phone Number";
                            newUser.DriverType = "Select a Type of driver";
                            newUser.email = email_facebook;
                            newUser.name = name_facebook;
                            newUser.avata = image_facebook;
                            FirebaseDatabase.getInstance().getReference().child("Driver/"+ uid_facebook).setValue(newUser);

                            Toast.makeText(getApplicationContext(), "Register and Login success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), SplaschScreen.class));
                            finish();

                            // mRef.child(uid).setValue(user);
                            waitingDialog.dismiss();

                        }

                        // hideProgressDialog();
                    }
                });
    }



}