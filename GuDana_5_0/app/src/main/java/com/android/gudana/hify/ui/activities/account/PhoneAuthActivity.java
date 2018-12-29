package com.android.gudana.hify.ui.activities.account;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import es.dmoral.toasty.Toasty;


/**
 * Created by Djimgou Patrick
 * Created on 09-oct-17.
 */

public class PhoneAuthActivity extends AppCompatActivity implements
        View.OnClickListener {

    EditText mPhoneNumberField, mVerificationField;
    Button mStartButton;
    private String phoneNumber;
    private CountryCodePicker ccp;
    public static FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;

    private static final String TAG = "PhoneAuthActivity";

    // firebase ...
    private DatabaseReference userDB;
    public static  FirebaseUser user_Global;
    private Context context;
    public ProgressDialog mDialog;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        mPhoneNumberField = (EditText) findViewById(R.id.field_phone_number);
        mVerificationField = (EditText) findViewById(R.id.field_verification_code);
        mStartButton = (Button) findViewById(R.id.button_start_verification);


        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                Toast.makeText(getApplicationContext(), "Updated " + selectedCountry.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        mStartButton.setOnClickListener(this);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait... we proceed your registration ");
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                try{

                    mDialog.dismiss();
                    dialog.dismiss();
                    Toasty.info(PhoneAuthActivity.this, "Registration Cancelled", Toast.LENGTH_SHORT).show();
                    Intent LoginEmail = new Intent(PhoneAuthActivity.this, StartLoginActivity.class);
                    startActivity(LoginEmail);
                    PhoneAuthActivity.this.finish();

                }catch(Exception ex){
                    ex.printStackTrace();
                }

            }
        });
        mDialog.setCanceledOnTouchOutside(false);
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

                            // check if User ist already registered  ...

                            userDB = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_Global.getUid());
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
                                                mDialog.dismiss();
                                                String Phone = map.get("phone").toString();
                                                // save User Info and continue  normaly  ... user is already registerd

                                                /*
                                                Intent intent = new Intent(PhoneAuthActivity.this, MainActivity_GuDDana.class);
                                                startActivity(intent);
                                                PhoneAuthActivity.this.finish();
                                                */
                                            }else{

                                                // start with the registration prozess...
                                                mDialog.dismiss();
                                                //Toasty.error(context, "Start Registration ... ", Toast.LENGTH_LONG).show();
                                                //Intent intent = new Intent(PhoneAuthActivity.this, RegisterActivity.class);
                                                //startActivity(intent);
                                                //PhoneAuthActivity.this.finish();

                                            }

                                        }catch(Exception ex){
                                            Toast.makeText(PhoneAuthActivity.this, ex.toString() , Toast.LENGTH_LONG).show();
                                            ex.printStackTrace();
                                        }

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


    private void startPhoneNumberVerification(String phoneNumber) {
        mDialog.show();
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


    private boolean validatePhoneNumber() {
        String ContryCode  = ccp.getSelectedCountryCode().trim();
        this.phoneNumber = mPhoneNumberField.getText().toString().trim();

        Toast.makeText(getApplicationContext(), "Phone number ist : " + ContryCode+this.phoneNumber, Toast.LENGTH_LONG).show();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError("Invalid phone number.");
            return false;
        }
        this.phoneNumber = "+"+ContryCode+phoneNumber;
        return true;
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            new AlertDialog.Builder(PhoneAuthActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("connected user ID ist "+currentUser.getUid().toString())
                    .setMessage("Are you sure you want to continue as User "+currentUser.getPhoneNumber().toString()+ "  ?")
                    .setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            try{
                                FirebaseAuth.getInstance().signOut();

                                // EmailLoginActivity.this.finish();
                                // finish();
                            }catch(Exception ex){
                                ex.printStackTrace();
                            }
                        }

                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            startActivity(new Intent(PhoneAuthActivity.this, MainActivity_GuDDana.class));
                            finish();

                        }

                    })
                    .show();

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PhoneAuthActivity.this, StartLoginActivity.class);
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
                if(this.phoneNumber.equalsIgnoreCase("+4917671792085")){
                    Toasty.info(context, "Valid Phone number .", Toast.LENGTH_SHORT).show();
                }
                //startPhoneNumberVerification("+4917671792085");
                startPhoneNumberVerification(phoneNumber);
                break;
        }

    }

}
