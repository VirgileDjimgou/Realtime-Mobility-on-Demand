package com.android.gudana.hify.ui.activities.account;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.gudana.chatapp.models.StaticConfigUser_fromFirebase;
import com.android.gudana.gpslocationtracking.LocationTrack;
import com.android.gudana.hify.ui.activities.MainActivity_GuDDana;
import com.android.gudana.hify.utils.AnimationUtil;
import com.android.gudana.hify.utils.database.UserHelper;
import com.android.gudana.R;
import com.android.gudana.tindroid.Cache;
import com.android.gudana.tindroid.CredentialsFragment;
import com.android.gudana.tindroid.Test_Login_Tinode_server_Activity;
import com.android.gudana.tindroid.UiUtils;
import com.android.gudana.tindroid.account.Utils;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import co.tinode.tinodesdk.PromisedReply;
import co.tinode.tinodesdk.Tinode;
import co.tinode.tinodesdk.model.AuthScheme;
import co.tinode.tinodesdk.model.ServerMessage;
import es.dmoral.toasty.Toasty;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LoginActivity extends AppCompatActivity {

    /// ad tindroid Attrbut   ..


    private static final int PERMISSIONS_REQUEST_ID = 100;

    public static final String EXTRA_CONFIRM_CREDENTIALS = "confirmCredentials";
    public static final String EXTRA_ADDING_ACCOUNT = "addNewAccount";

    static final String FRAGMENT_LOGIN = "login";
    static final String FRAGMENT_SIGNUP = "signup";
    static final String FRAGMENT_SETTINGS = "settings";
    static final String FRAGMENT_CREDENTIALS = "cred";

    static final String PREFS_LAST_LOGIN = "pref_lastLogin";

    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    private Bundle mResultBundle = null;

    // end add Attrbut


    private static final String TAG = "LoginFragment";
    private ArrayList<String> permissions = new ArrayList<>();
    private ArrayList<String> permissionsToRequest;


    LocationTrack locationTrack;
    public static Activity activity;
    private EditText email,password;
    private Button login,register;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private UserHelper userHelper;
    private ProgressDialog mDialog;

    public static void startActivityy(Context context) {
        Intent intent=new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hi_activity_login);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


        // get intent  video or audio  ....
        boolean EmailValidation  = getIntent().getBooleanExtra("Email_Confirmation", false);
        if(EmailValidation == true){
            // Message to the user  ...the should  chechk his Email  to valide  the confirmation  that the  server  are send
            new LovelyInfoDialog(LoginActivity.this)
                    .setTopColorRes(R.color.bgnew)
                    .setIcon(R.mipmap.ic_infos)
                    //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                    //.setNotShowAgainOptionEnabled(0)
                    //.setNotShowAgainOptionChecked(true)
                    .setTitle("a confirmation email has been sent to you")
                    .setMessage("please check your email for confirmation and follow the link to verify your email adress")
                    .show();

        }else{
            // normal Start  ... without  Email Confirmation
        }

        activity = this;
        mAuth= FirebaseAuth.getInstance();
        mFirestore= FirebaseFirestore.getInstance();
        userHelper = new UserHelper(this);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait..");
        mDialog.setIndeterminate(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Fade fade = new Fade();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fade.excludeTarget(findViewById(R.id.layout), true);
                fade.excludeTarget(android.R.id.statusBarBackground, true);
                fade.excludeTarget(android.R.id.navigationBarBackground, true);
                getWindow().setEnterTransition(fade);
                getWindow().setExitTransition(fade);
            }
        }

        /*
        // settings gps

        Button TestCredential = (Button) findViewById(R.id.test_credentials);
        TestCredential.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginEmail = new Intent(LoginActivity.this, Test_Login_Tinode_server_Activity.class);
                startActivity(LoginEmail);
                // LoginActivity.this.finish();
            }
        });

        */


        askPermission();
        // control if Gps activated  ...
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.
        locationTrack = new LocationTrack(LoginActivity.this);
        // chech if gps is enable  ....
        if (locationTrack.canGetLocation()) {

            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();

            // Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else {

            //  you must activated settings
            // locationTrack.showSettingsAlert();

            locationTrack.showSettingsAlert();
        }




    }

    private void askPermission() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO


                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.isAnyPermissionPermanentlyDenied()){
                            Toast.makeText(LoginActivity.this, "You have denied some permissions permanently, if the app force close try granting permission from settings.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();

    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }


    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void performLogin() {

        final String email_, pass_;
        email_ = email.getText().toString();
        pass_ = password.getText().toString();

        //perform login firebase and the data on as static config  ...

        if (!TextUtils.isEmpty(email_) && !TextUtils.isEmpty(pass_)) {
            mDialog.show();

            //
            mAuth.signInWithEmailAndPassword(email_, pass_).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull final Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        if (task.getResult().getUser().isEmailVerified()) {

                            final String token_id = FirebaseInstanceId.getInstance().getToken();
                            Log.i("TOKEN",token_id);
                            final String current_id = task.getResult().getUser().getUid();

                            mFirestore.collection("Users").document(current_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.getString("token_id").equals(token_id) || documentSnapshot.getString("token_id").equals("") ) {

                                        Map<String, Object> tokenMap = new HashMap<>();
                                        tokenMap.put("token_id", token_id);

                                        mFirestore.collection("Users").document(current_id).update(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                FirebaseFirestore.getInstance().collection("Users").document(current_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                        userHelper.insertContact(
                                                                documentSnapshot.getString("username")
                                                                ,documentSnapshot.getString("name")
                                                                , documentSnapshot.getString("email")
                                                                , documentSnapshot.getString("image")
                                                                , pass_
                                                                ,documentSnapshot.getString("location")
                                                                ,documentSnapshot.getString("bio")
                                                        );
                                                         // i need  something  sorry
                                                        //  im principe we need to save the data   ..  also in
                                                        //save  the  current user data ...
                                                        StaticConfigUser_fromFirebase.USER_NAME = documentSnapshot.getString("username");
                                                        StaticConfigUser_fromFirebase.STR_EXTRA_EMAIL = documentSnapshot.getString("email");


                                                        mDialog.dismiss();

                                                        //MainActivity_GuDDana.startActivity(LoginActivity.this);
                                                        LoginTindroidCredential(email_ , pass_);
                                                        //finish();

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e("Error", ".." + e.getMessage());
                                                    }
                                                });


                                                // Update Users token  ....
                                                // Loggin user with data he gave us

                                                FirebaseAuth.getInstance().signInWithEmailAndPassword(email_.toString(), pass_.toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                                                {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task)
                                                    {
                                                        if(task.isSuccessful())
                                                        {
                                                            String token = FirebaseInstanceId.getInstance().getToken();
                                                            String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                                            // Updating user device token

                                                            FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("token").setValue(token).addOnCompleteListener(new OnCompleteListener<Void>()
                                                            {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task)
                                                                {
                                                                    if(task.isSuccessful())
                                                                    {

                                                                        if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
                                                                        {
                                                                            // Show animation and start activity
                                                                            Toast.makeText(getApplicationContext(), "token and User Id updated  ", Toast.LENGTH_LONG).show();

                                                                        }
                                                                        else
                                                                        {

                                                                            Toast.makeText(getApplicationContext(), "Your email is not verified, we have sent you a new one.", Toast.LENGTH_LONG).show();
                                                                            FirebaseAuth.getInstance().signOut();

                                                                        }
                                                                    }
                                                                    else
                                                                    {

                                                                    }
                                                                }
                                                            });
                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                                        }
                                                    }
                                                });

                                                // end login firebase   ....

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                mDialog.dismiss();
                                                //Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                new LovelyInfoDialog(LoginActivity.this)
                                                        .setTopColorRes(R.color.colorPrimary)
                                                        .setIcon(R.mipmap.ic_infos)
                                                        //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                                                        //.setNotShowAgainOptionEnabled(0)
                                                        //.setNotShowAgainOptionChecked(true)
                                                        .setTitle("Infos")
                                                        .setMessage("Error: " + e.getMessage())
                                                        .show();
                                            }
                                        });
                                    } else {
                                        mDialog.dismiss();
                                        new BottomDialog.Builder(LoginActivity.this)
                                                .setTitle("Information")
                                                .setContent("This account is being used in another device, do you want to logout from that Device  and  use your Credential in this Device  ? " )
                                                .setPositiveText("try with another Credential")
                                                .setPositiveBackgroundColorResource(R.color.colorAccentt)
                                                .setCancelable(true)
                                                .onPositive(new BottomDialog.ButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull BottomDialog dialog) {
                                                        dialog.dismiss();
                                                    }
                                                })

                                                .setNegativeText("Logout")
                                                .setNegativeTextColorResource(R.color.red)
                                                //.setNegativeTextColor(ContextCompat.getColor(this, R.color.colorAccent)
                                                .onNegative(new BottomDialog.ButtonCallback() {
                                                    @Override
                                                    public void onClick(BottomDialog dialog) {
                                                        Log.d("logout ", "Do something!");

                                                        // logout from another device  and login again ...
                                                        final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
                                                        mDialog.setIndeterminate(true);
                                                        mDialog.setMessage("Logging you out...");
                                                        mDialog.setCancelable(false);
                                                        mDialog.setCanceledOnTouchOutside(false);
                                                        mDialog.show();

                                                        Map<String, Object> tokenRemove = new HashMap<>();
                                                        tokenRemove.put("token_id", "");
                                                        final String current_id = task.getResult().getUser().getUid();
                                                        mFirestore.collection("Users").document(current_id).update(tokenRemove).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                userHelper.deleteContact(1);
                                                                mAuth.signOut();
                                                                //LoginActivity.startActivityy(LoginActivity.this);
                                                                mDialog.dismiss();
                                                                //finish();
                                                                Toasty.info(LoginActivity.this, " You have successfully logged out! ", Toast.LENGTH_LONG).show();

                                                                overridePendingTransitionExit();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.e("Logout Error", e.getMessage());
                                                            }
                                                        });
                                                    }
                                                })
                                                .show();

                                        if (mAuth.getCurrentUser() != null) {
                                            mAuth.signOut();
                                        }



                                    }
                                }
                            });


                        } else{

                            mDialog.dismiss();
                            new BottomDialog.Builder(LoginActivity.this)
                                    .setTitle("Information")
                                    .setContent("Email has not been verified, please verify and continue.")
                                    .setPositiveText("Send again")
                                    .setPositiveBackgroundColorResource(R.color.colorAccentt)
                                    .setCancelable(true)
                                    .onPositive(new BottomDialog.ButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull final BottomDialog dialog) {
                                          task.getResult()
                                                  .getUser()
                                                  .sendEmailVerification()
                                                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                      @Override
                                                      public void onSuccess(Void aVoid) {
                                                          dialog.dismiss();
                                                          //Toast.makeText(LoginActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                                                          new LovelyInfoDialog(LoginActivity.this)
                                                                  .setTopColorRes(R.color.colorPrimary)
                                                                  .setIcon(R.mipmap.ic_infos)
                                                                  //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                                                                  //.setNotShowAgainOptionEnabled(0)
                                                                  //.setNotShowAgainOptionChecked(true)
                                                                  .setTitle("Infos")
                                                                  .setMessage("Verification email sent")
                                                                  .show();
                                                      }
                                                  })
                                                  .addOnFailureListener(new OnFailureListener() {
                                                      @Override
                                                      public void onFailure(@NonNull Exception e) {
                                                          Log.e("Error",e.getMessage());
                                                      }
                                                  });
                                        }
                                    })
                                    .setNegativeText("Ok")
                                    .onNegative(new BottomDialog.ButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull BottomDialog dialog) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();

                            if (mAuth.getCurrentUser() != null) {
                                mAuth.signOut();
                            }

                        }

                    } else {
                        if (task.getException().getMessage().contains("The password is invalid")) {
                            Toast.makeText(LoginActivity.this, "Error: The password you have entered is invalid.", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                            new LovelyInfoDialog(LoginActivity.this)
                                    .setTopColorRes(R.color.colorPrimary)
                                    .setIcon(R.mipmap.ic_infos)
                                    //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                                    //.setNotShowAgainOptionEnabled(0)
                                    //.setNotShowAgainOptionChecked(true)
                                    .setTitle("Infos")
                                    .setMessage("Error: The password you have entered is invalid.")
                                    .show();

                        } else if (task.getException().getMessage().contains("There is no user record")) {
                            Toast.makeText(LoginActivity.this, "Error: Invalid user, Please register using the button below.", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                            new LovelyInfoDialog(LoginActivity.this)
                                    .setTopColorRes(R.color.colorPrimary)
                                    .setIcon(R.mipmap.ic_infos)
                                    //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                                    //.setNotShowAgainOptionEnabled(0)
                                    //.setNotShowAgainOptionChecked(true)
                                    .setTitle("Infos")
                                    .setMessage("Error: Invalid user, Please register using the button below.")
                                    .show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                            new LovelyInfoDialog(LoginActivity.this)
                                    .setTopColorRes(R.color.colorPrimary)
                                    .setIcon(R.mipmap.ic_infos)
                                    //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                                    //.setNotShowAgainOptionEnabled(0)
                                    //.setNotShowAgainOptionChecked(true)
                                    .setTitle("Infos")
                                    .setMessage("Error: " + task.getException().getMessage())
                                    .show();
                        }

                    }
                }
            });

        } else if (TextUtils.isEmpty(email_)) {

            AnimationUtil.shakeView(email, this);

        } else if (TextUtils.isEmpty(pass_)) {

            AnimationUtil.shakeView(password, this);

        } else {

            AnimationUtil.shakeView(email, this);
            AnimationUtil.shakeView(password, this);

        }

    }



    @Override
    public void finish() {
        super.finish();
        overridePendingTransitionExit();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransitionEnter();
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.hi_slide_from_right, R.anim.hi_slide_to_left);
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    protected void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.hi_slide_from_left, R.anim.hi_slide_to_right);
    }

    public void onLogin(View view) {
        performLogin();
    }

    public void onRegister(View view) {
        RegisterActivity.startActivity(this, this, findViewById(R.id.button));
    }


    // login Methode tindroid  ...

    public void LoginTindroidCredential(String loginInput , String  passwordInput) {
        // final com.android.gudana.tindroid.LoginActivity parent = (com.android.gudana.tindroid.LoginActivity) getActivity();

        //EditText loginInput = parent.findViewById(R.id.editLogin);
        //EditText passwordInput = parent.findViewById(R.id.editPassword);

        final boolean[] response = {false};

        final Button signIn = (Button) findViewById(R.id.button);

        final String login = loginInput.toString().trim();

        final String password = passwordInput.toString().trim();


        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String hostName = sharedPref.getString(Utils.PREFS_HOST_NAME, Cache.HOST_NAME);
        boolean tls = sharedPref.getBoolean(Utils.PREFS_USE_TLS, false);
        final Tinode tinode = Cache.getTinode();
        try {
            Log.d(TAG, "CONNECTING");
            // This is called on the websocket thread.
            tinode.connect(hostName, tls)
                    .thenApply(
                            new PromisedReply.SuccessListener<ServerMessage>() {
                                @Override
                                public PromisedReply<ServerMessage> onSuccess(ServerMessage ignored) throws Exception {

                                    Intent StartActivity = new Intent(LoginActivity.this, MainActivity_GuDDana.class);
                                    startActivity(StartActivity);

                                    // Toast.makeText(LoginActivity.this, "bad Credential ... tindroid Server Response", Toast.LENGTH_SHORT).show();
                                    return tinode.loginBasic(
                                            login,
                                            password);
                                }
                            },
                            null)
                    .thenApply(
                            new PromisedReply.SuccessListener<ServerMessage>() {
                                @Override
                                public PromisedReply<ServerMessage> onSuccess(final ServerMessage msg) {
                                    sharedPref.edit().putString(PREFS_LAST_LOGIN, login).apply();

                                    final Account acc = addAndroidAccount(
                                            tinode.getMyId(),
                                            AuthScheme.basicInstance(login, password).toString(),
                                            tinode.getAuthToken());

                                    if (msg.ctrl.code >= 300 && msg.ctrl.text.contains("validate credentials")) {
                                        LoginActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                //FragmentTransaction trx = parent.getSupportFragmentManager().beginTransaction();
                                                CredentialsFragment cf = new CredentialsFragment();
                                                Iterator<String> it = msg.ctrl.getStringIteratorParam("cred");
                                                if (it != null) {
                                                    cf.setMethod(it.next());
                                                }
                                                //trx.replace(R.id.contentFragment, cf);
                                                //trx.commit();
                                            }
                                        });
                                    } else {
                                        // Force immediate sync, otherwise Contacts tab may be unusable.
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                                        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                                        ContentResolver.requestSync(acc, Utils.SYNC_AUTHORITY, bundle);
                                        UiUtils.onLoginSuccess(LoginActivity.this, signIn);

                                    }
                                    return null;
                                }
                            },
                            new PromisedReply.FailureListener<ServerMessage>() {
                                @Override
                                public PromisedReply<ServerMessage> onFailure(Exception err) {
                                    Log.d(TAG, "Login failed", err);
                                    Toasty.error(LoginActivity.this, err.toString(), Toast.LENGTH_SHORT).show();
                                    //parent.reportError(err, signIn, R.string.error_login_failed);
                                    return null;
                                }
                            });
        } catch (Exception err) {
            Log.e(TAG, "Something went wrong", err);
            err.printStackTrace();
            Toasty.error(this, "Login  Failed ", Toast.LENGTH_SHORT).show();
            // parent.reportError(err, signIn, R.string.error_login_failed);
        }

    }


    private Account addAndroidAccount(final String uid, final String secret, final String token) {
        final AccountManager am = AccountManager.get(this.getBaseContext());
        final Account acc = Utils.createAccount(uid);
        am.addAccountExplicitly(acc, secret, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.notifyAccountAuthenticated(acc);
        }
        if (!TextUtils.isEmpty(token)) {
            am.setAuthToken(acc, Utils.TOKEN_TYPE, token);
        }
        return acc;
    }

}
