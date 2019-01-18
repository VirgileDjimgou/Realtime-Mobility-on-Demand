package com.android.gudana.chatapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.gudana.R;
import com.android.gudana.hify.ui.activities.MainActivity_GuDDana;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Created by Djimgou Patrick
 * Created on 09-oct-17.
 */

public class PhoneAuthActivity extends AppCompatActivity implements
        View.OnClickListener {

    EditText mPhoneNumberField, mVerificationField;
    Button mStartButton, mVerifyButton, mResendButton;
    private String phoneNumber;




    private CountryCodePicker ccp;
    public static FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;

    private static final String TAG = "PhoneAuthActivity";

    // firebase ...
    private DatabaseReference userDB;
    public static FirebaseUser user_Global;
    private Context context;
    private static  String Username = "";




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        mPhoneNumberField = (EditText) findViewById(R.id.field_phone_number);
        mVerificationField = (EditText) findViewById(R.id.field_verification_code);
        mStartButton = (Button) findViewById(R.id.button_start_verification);
        context = this.getApplicationContext();




        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                Toast.makeText(getApplicationContext(), "Updated " + selectedCountry.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        mStartButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    mPhoneNumberField.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };

        context = this.getApplicationContext();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            user_Global = task.getResult().getUser();
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            mStartButton.setClickable(false);



                            // check if User ist already registered  ...

                            // userDB = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_Global.getUid());
                            userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(user_Global.getUid());
                            // Set the  Driver Response to true ...
                            HashMap map = new HashMap();
                            map.put("Authentified" , "await");
                            userDB.updateChildren(map);
                            userDB.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    Log.d("Verification" , "passed");

                                    if(dataSnapshot.exists()){

                                        Log.d("Verification" , "passed");

                                        try{

                                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                            // test if the recors Phone already exist  ...if not than
                                            // than you are a new user   ...
                                            if(map.get("phone")!=null){
                                                // than this user is already registered ...
                                                String Phone = map.get("phone").toString();
                                                // save User Info and continue  normaly  ... user is already registerd
                                                // StaticConfigUser_fromFirebase.UID = user_Global.getUid();
                                                saveUserInfo();
                                                Intent intent = new Intent(PhoneAuthActivity.this, MainActivity_GuDDana.class);
                                                startActivity(intent);
                                                PhoneAuthActivity.this.finish();

                                            }else{

                                                Toast.makeText(context , "Start Registration ... " ,Toast.LENGTH_LONG ).show();
                                                String userid = user_Global.getUid();

                                                // "Packing" user data

                                                map = new HashMap<>();
                                                map.put("token", FirebaseInstanceId.getInstance().getToken());
                                                map.put("name", phoneNumber.toString());
                                                map.put("phone", phoneNumber.toString());
                                                map.put("email", "with_phone");
                                                map.put("status", "Welcome to my Profile!");
                                                map.put("image", "default");
                                                map.put("cover", "default");
                                                map.put("date", ServerValue.TIMESTAMP);

                                                // Uploading user data

                                                FirebaseDatabase.getInstance().getReference().child("Users").child(userid).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>()
                                                {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                    {
                                                        if(task.isSuccessful())
                                                        {

                                                            mStartButton.setClickable(true);

                                                            Intent intent = new Intent(PhoneAuthActivity.this, MainActivity_GuDDana.class);
                                                            startActivity(intent);
                                                        }
                                                        else
                                                        {
                                                            mStartButton.setClickable(true);
                                                            Log.d(TAG, "registerData failed: " + task.getException().getMessage());
                                                        }
                                                    }
                                                });
                                                Intent intent = new Intent(PhoneAuthActivity.this, MainActivity_GuDDana.class);
                                                startActivity(intent);
                                                PhoneAuthActivity.this.finish();

                                            }


                                        }catch(Exception ex){
                                            mStartButton.setClickable(true);
                                            Toast.makeText(PhoneAuthActivity.this, ex.toString() , Toast.LENGTH_LONG).show();
                                            ex.printStackTrace();
                                        }
                                        mStartButton.setClickable(true);

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    Toast.makeText(PhoneAuthActivity.this, databaseError.toString() , Toast.LENGTH_LONG).show();
                                }
                            });

                            // startActivity(new Intent(PhoneAuthActivity.this, SplaschScreen.class));
                            // finish();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                mVerificationField.setError("Invalid code.");
                            }
                        }
                    }
                });
    }



    /**
     * Save User Info  and SnapShoot  ...
     */
    void saveUserInfo() {

        try{
            // FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child("Driver/"+ user.getUid()).setValue(newUser);

        }catch (Exception ex){
            ex.printStackTrace();
        }

    }


    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }



    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private boolean validatePhoneNumber() {
        String ContryCode  = ccp.getSelectedCountryCode().toString();
        this.phoneNumber = mPhoneNumberField.getText().toString();

        Toast.makeText(getApplicationContext(), "Phone number ist : " + ContryCode+this.phoneNumber, Toast.LENGTH_LONG).show();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError("Invalid phone number.");
            return false;
        }

        this.phoneNumber = ContryCode+phoneNumber;
        return true;
    }
    @Override
    public void onStart() {
        super.onStart();

        }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PhoneAuthActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_verification:
                if (!validatePhoneNumber()) {
                    return;
                }
                startPhoneNumberVerification(phoneNumber);
                break;
        }

    }

}
