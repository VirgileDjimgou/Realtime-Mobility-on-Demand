package com.bee.passenger.activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bee.passenger.R;
import com.bee.passenger.data.FriendDB;
import com.bee.passenger.data.GroupDB;
import com.bee.passenger.data.SharedPreferenceHelper;
import com.bee.passenger.data.StaticConfig;
import com.bee.passenger.model.User;
import com.bee.passenger.service.ServiceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EmailLoginActivity extends AppCompatActivity {
    private static String TAG = "EmailLoginActivity";
    FloatingActionButton fab;
    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private EditText editTextUsername, editTextPassword;
    private LovelyProgressDialog waitingDialog;
    private Button phoneAuthentification;

    private AuthUtils authUtils;
    public static  FirebaseAuth mAuth;
    public static  FirebaseAuth.AuthStateListener mAuthListener;
    public static  FirebaseUser user;
    private boolean firstTimeAccess;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        editTextUsername = (EditText) findViewById(R.id.et_username);
        editTextPassword = (EditText) findViewById(R.id.et_password);
        firstTimeAccess = true;
        initFirebase();
    }


    private void initFirebase() {
        //Khoi tao thanh phan de dang nhap, dang ky
        mAuth = FirebaseAuth.getInstance();
        authUtils = new AuthUtils();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {


                    new AlertDialog.Builder(EmailLoginActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("connected user ID ist "+user.getUid().toString())
                            .setMessage("Are you sure you want to continue as User "+user.getEmail().toString()+ "  ?")
                            .setNegativeButton("No", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    try{
                                        FirebaseAuth.getInstance().signOut();
                                        FriendDB.getInstance(getApplicationContext()).dropDB();
                                        GroupDB.getInstance(getApplicationContext()).dropDB();
                                        ServiceUtils.stopServiceFriendChat(getApplicationContext(), true);
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

                                    // User is signed in
                                    StaticConfig.UID = user.getUid();
                                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                                    if (firstTimeAccess) {
                                        authUtils.saveUserInfo();
                                        // get actual User  ....
                                        startActivity(new Intent(EmailLoginActivity.this, MainActivity.class));
                                        EmailLoginActivity.this.finish();
                                    }
                                    authUtils.saveUserInfo();
                                    startActivity(new Intent(EmailLoginActivity.this, MainActivity.class));
                                    EmailLoginActivity.this.finish();
                                    finish();
                                }

                            })
                            .show();


                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                firstTimeAccess = false;
            }
        };

        //Khoi tao dialog waiting khi dang nhap
        waitingDialog = new LovelyProgressDialog(this).setCancelable(false);
    }


    private void  UserSessionContinueDialog(){


    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @SuppressLint("RestrictedApi")
    public void clickRegisterLayout(View view) {
        getWindow().setExitTransition(null);
        getWindow().setEnterTransition(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation(this, fab, fab.getTransitionName());
            startActivityForResult(new Intent(this, RegisterActivity.class), StaticConfig.REQUEST_CODE_REGISTER, options.toBundle());
        } else {
            startActivityForResult(new Intent(this, RegisterActivity.class), StaticConfig.REQUEST_CODE_REGISTER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StaticConfig.REQUEST_CODE_REGISTER && resultCode == RESULT_OK) {
            authUtils.createUser(
                    data.getStringExtra(StaticConfig.STR_EXTRA_USERNAME),
                    data.getStringExtra(StaticConfig.STR_EXTRA_PHONE_NUMBER),
                    data.getStringExtra(StaticConfig.STR_EXTRA_DRIVER_TYPE),
                    data.getStringExtra(StaticConfig.STR_EXTRA_EMAIL),
                    data.getStringExtra(StaticConfig.STR_EXTRA_PASSWORD));
        }
    }

    public void clickLogin(View view) {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        if (validate(username, password)) {
            authUtils.signIn(username, password);
        } else {
            Toast.makeText(this, "Invalid email or empty password", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED, null);
        Intent MainAct = new Intent(EmailLoginActivity.this, MainActivity_App.class);
        startActivity(MainAct);
        finish();
    }

    private boolean validate(String emailStr, String password) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return (password.length() > 0 || password.equals(";")) && matcher.find();
    }

    public void clickResetPassword(View view) {
        String username = editTextUsername.getText().toString();
        if (validate(username, ";")) {
            authUtils.resetPassword(username);
        } else {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
        }
    }

    class AuthUtils {
        /**
         * Action register
         *
         * @param email
         * @param password
         */
        void createUser(final String Username , final String Phone_numb , final String Driver_Type , String email, String password) {
            waitingDialog.setIcon(R.drawable.ic_add_friend)
                    .setTitle("Registering....")
                    .setTopColorRes(R.color.colorPrimary)
                    .show();



            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(EmailLoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            waitingDialog.dismiss();
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                new LovelyInfoDialog(EmailLoginActivity.this) {
                                    @Override
                                    public LovelyInfoDialog setConfirmButtonText(String text) {
                                        findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dismiss();
                                            }
                                        });
                                        return super.setConfirmButtonText(text);
                                    }
                                }
                                        .setTopColorRes(R.color.colorAccent)
                                        .setIcon(R.drawable.ic_add_friend)
                                        .setTitle("Register false")
                                        .setMessage("Email exist or weak password!")
                                        .setConfirmButtonText("ok")
                                        .setCancelable(false)
                                        .show();
                            } else {
                                User newUser = new User();

                                newUser.phone = Phone_numb;
                                newUser.email = user.getEmail();
                                newUser.name = Username;
                                newUser.avata = StaticConfig.STR_DEFAULT_BASE64;
                                // FirebaseDatabase.getInstance().getReference().child("Driver/"+ user.getUid()).setValue(newUser);

                                FirebaseDatabase.getInstance().getReference().child("Users").child("Customers/"+ user.getUid()).setValue(newUser);

                                Toast.makeText(EmailLoginActivity.this, "Register and Login success", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EmailLoginActivity.this, SplaschScreen.class));
                                EmailLoginActivity.this.finish();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            waitingDialog.dismiss();
                        }
                    })
            ;
        }


        /**
         * Action Login
         *
         * @param email
         * @param password
         */
        void signIn(String email, String password) {
            waitingDialog.setIcon(R.drawable.ic_person_low)
                    .setTitle("Login....")
                    .setTopColorRes(R.color.colorPrimary)
                    .show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(EmailLoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            waitingDialog.dismiss();
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                new LovelyInfoDialog(EmailLoginActivity.this) {
                                    @Override
                                    public LovelyInfoDialog setConfirmButtonText(String text) {
                                        findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dismiss();
                                            }
                                        });
                                        return super.setConfirmButtonText(text);
                                    }
                                }
                                        .setTopColorRes(R.color.colorAccent)
                                        .setIcon(R.drawable.ic_person_low)
                                        .setTitle("Login false")
                                        .setMessage("Email not exist or wrong password!")
                                        .setCancelable(false)
                                        .setConfirmButtonText("Ok")
                                        .show();
                            } else {
                                StaticConfig.UID = user.getUid();
                                saveUserInfo();
                                startActivity(new Intent(EmailLoginActivity.this, SplaschScreen.class));
                                // startActivity(new Intent(EmailLoginActivity.this, MapsActivity.class));
                                // startActivity(new Intent(EmailLoginActivity.this, LocationsOverviewActivity.class));
                                EmailLoginActivity.this.finish();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            waitingDialog.dismiss();
                        }
                    });
        }

        /**
         * Action reset password
         *
         * @param email
         */
        void resetPassword(final String email) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            new LovelyInfoDialog(EmailLoginActivity.this) {
                                @Override
                                public LovelyInfoDialog setConfirmButtonText(String text) {
                                    findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dismiss();
                                        }
                                    });
                                    return super.setConfirmButtonText(text);
                                }
                            }
                                    .setTopColorRes(R.color.colorPrimary)
                                    .setIcon(R.drawable.ic_pass_reset)
                                    .setTitle("Password Recovery")
                                    .setMessage("Sent email to " + email)
                                    .setConfirmButtonText("Ok")
                                    .show();
                        }
                    })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    new LovelyInfoDialog(EmailLoginActivity.this) {
                        @Override
                        public LovelyInfoDialog setConfirmButtonText(String text) {
                            findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dismiss();
                                }
                            });
                            return super.setConfirmButtonText(text);
                        }
                    }
                            .setTopColorRes(R.color.colorAccent)
                            .setIcon(R.drawable.ic_pass_reset)
                            .setTitle("False")
                            .setMessage("False to sent email to " + email)
                            .setConfirmButtonText("Ok")
                            .show();
                }
            });
        }

        /**
         * Save User Info  and SnapShoot  ...
         */
        void saveUserInfo() {

            try{

                // FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child("Driver/"+ user.getUid()).setValue(newUser);

                FirebaseDatabase.getInstance().getReference().child("Users").child("Customers/" + StaticConfig.UID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        waitingDialog.dismiss();
                        HashMap hashUser = (HashMap) dataSnapshot.getValue();
                        User userInfo = new User();
                        userInfo.name = (String) hashUser.get("name");
                        userInfo.email = (String) hashUser.get("email");
                        userInfo.avata = (String) hashUser.get("avata");
                        SharedPreferenceHelper.getInstance(EmailLoginActivity.this).saveUserInfo(userInfo);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }catch (Exception ex){
                ex.printStackTrace();
            }

        }

        /**
         * Init new  User in  Firebase  Realtime DB ...
         */

    }
}
