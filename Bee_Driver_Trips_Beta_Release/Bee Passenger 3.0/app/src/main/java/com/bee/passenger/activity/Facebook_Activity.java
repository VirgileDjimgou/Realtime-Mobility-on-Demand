package com.bee.passenger.activity;

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

import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bee.passenger.R;

import com.bee.passenger.data.StaticConfig;
import com.bee.passenger.model.User;


public class Facebook_Activity extends AppCompatActivity {
    private static final String TAG = "AndroidBash";
    public static  FirebaseUser user;
    private EditText password;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mProgressDialog;
    private LovelyProgressDialog waitingDialog;

    //Add YOUR Firebase Reference URL instead of the following URL
    // Firebase mRef=new Firebase("https://androidbashfirebaseupdat-bd094.firebaseio.com/users/");

    //FaceBook callbackManager
    private CallbackManager callbackManager;
    private EditText email , PhoneNumber;
    private Spinner Type_of_Driver;
    private Button ValidateRegistration;



    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static String STR_EXTRA_ACTION_REGISTER = "register";

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

    private void ValidationProzess() {

        String Phone = PhoneNumber.getText().toString().trim();
        String Driver_Type = Type_of_Driver.getSelectedItem().toString().trim();
        String email_user = email.getText().toString();
        if(validate(email_user, Driver_Type , Phone)){
            Intent data = new Intent();
            data.putExtra(StaticConfig.STR_EXTRA_PHONE_NUMBER , Phone);
            data.putExtra(StaticConfig.STR_EXTRA_DRIVER_TYPE , Driver_Type);
            data.putExtra(StaticConfig.STR_EXTRA_EMAIL , email_user);
            data.putExtra(StaticConfig.STR_EXTRA_ACTION, STR_EXTRA_ACTION_REGISTER);
            setResult(RESULT_OK, data);
            finish();
        }else {
            Toast.makeText(this, "Invalid email , name , Phone or not match password", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean validate( String emailStr, String Driver_type, String Phone) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return   Driver_type.length() > 0 && Phone.length() > 0 &&   matcher.find();
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