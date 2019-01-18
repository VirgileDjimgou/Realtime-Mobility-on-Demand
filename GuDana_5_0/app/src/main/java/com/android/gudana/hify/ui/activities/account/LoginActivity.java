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
import android.os.AsyncTask;
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

import com.android.gudana.chat.activities.MenuActivity;
import com.android.gudana.chat.activities.WelcomeActivity;
import com.android.gudana.chatapp.activities.PhoneAuthActivity;
import com.android.gudana.gpslocationtracking.LocationTrack;
import com.android.gudana.hify.ui.activities.MainActivity_GuDDana;
import com.android.gudana.hify.utils.AnimationUtil;
import com.android.gudana.hify.utils.Config;
import com.android.gudana.hify.utils.database.UserHelper;
import com.android.gudana.R;
//import com.android.gudana.tindroid.Cache;
//import com.android.gudana.tindroid.CredentialsFragment;
//import com.android.gudana.tindroid.Test_Login_Tinode_server_Activity;
//import com.android.gudana.tindroid.UiUtils;
//import com.android.gudana.tindroid.account.Utils;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
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
import es.dmoral.toasty.Toasty;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import com.android.gudana.chat.ChatApplication;
import com.android.gudana.chat.model.User;
import com.android.gudana.chat.network.JSONParser;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class LoginActivity extends AppCompatActivity  implements Validator.ValidationListener  {

    /// ad tindroid Attrbut   ..


    private boolean signingUp = false;
    private Validator validator;
    ChatApplication chatApplication;


    // end add Attrbut


    private static final String TAG = "LoginFragment";

    LocationTrack locationTrack;
    public static Activity activity;
    //private EditText email, password;

    @NotEmpty
    private EditText email;

    @NotEmpty
    private EditText password;


    private Button login, register;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private UserHelper userHelper;
    private ProgressDialog mDialog;
    private String username_str , password_str;

    public static void startActivityy(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            askPermission();
            //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            FirebaseApp.initializeApp(this);
            setContentView(R.layout.hi_activity_login);
        }catch (Exception ex){
            ex.printStackTrace();
        }


        chatApplication = ((ChatApplication) getApplication());
        validator = new Validator(LoginActivity.this);
        validator.setValidationListener(this);


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


        // get intent  video or audio  ....
        boolean EmailValidation = getIntent().getBooleanExtra("Email_Confirmation", false);
        if (EmailValidation == true) {
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

        } else {
            // normal Start  ... without  Email Confirmation
        }

        activity = this;
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        userHelper = new UserHelper(this);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait..");
        mDialog.setIndeterminate(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

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


        locationTrack = new LocationTrack(LoginActivity.this);
        // chech if gps is enable  ....
        if (locationTrack.canGetLocation()) {

            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();

            // Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else {
            askPermission();
            //  you must activated settings
            // locationTrack.showSettingsAlert();

            locationTrack.showSettingsAlert();
        }

        askPermission();

    }


    @Override
    protected void onResume() {
        super.onResume();

        askPermission();

    }

    private void askPermission() {

        Dexter.withActivity(LoginActivity.this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CALL_PHONE


                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Toast.makeText(LoginActivity.this, "You have denied some permissions permanently, if the app force close try granting permission from settings.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();

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
                            Log.i("TOKEN", token_id);
                            final String current_id = task.getResult().getUser().getUid();

                            mFirestore.collection("Users").document(current_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.getString("token_id").equals(token_id) || documentSnapshot.getString("token_id").equals("")) {

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
                                                                , documentSnapshot.getString("name")
                                                                , documentSnapshot.getString("email")
                                                                , documentSnapshot.getString("image")
                                                                , pass_
                                                                , documentSnapshot.getString("location")
                                                                , documentSnapshot.getString("bio")
                                                        );
                                                        // i need  something  sorry
                                                        //  im principe we need to save the data   ..  also in
                                                        //save  the  current user data ...
                                                        //StaticConfigUser_fromFirebase.USER_NAME = documentSnapshot.getString("username");
                                                        //StaticConfigUser_fromFirebase.STR_EXTRA_EMAIL = documentSnapshot.getString("email");


                                                        // check  Chat Loging  ...

                                                        signingUp = false ;

                                                        username_str = email_.toString().trim();
                                                        password_str = pass_.toString();
                                                        validator.validate();


                                                        //mDialog.dismiss();
                                                        //MainActivity_GuDDana.startActivity(LoginActivity.this);
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

                                                FirebaseAuth.getInstance().signInWithEmailAndPassword(email_.toString(), pass_.toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            String token = FirebaseInstanceId.getInstance().getToken();
                                                            String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                                            // Updating user device token

                                                            FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("token").setValue(token).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {

                                                                        if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                                                            // Show animation and start activity
                                                                            Toast.makeText(getApplicationContext(), "token and User Id updated  ", Toast.LENGTH_LONG).show();

                                                                        } else {

                                                                            Toast.makeText(getApplicationContext(), "Your email is not verified, we have sent you a new one.", Toast.LENGTH_LONG).show();
                                                                            FirebaseAuth.getInstance().signOut();

                                                                        }
                                                                    } else {

                                                                    }
                                                                }
                                                            });
                                                        } else {
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
                                                .setContent("This account is being used in another device, do you want to logout from that Device  and  use your Credential in this Device  ? ")
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
                                            mDialog.cancel();

                                        }


                                    }
                                }
                            });


                        } else {

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
                                                            Log.e("Error", e.getMessage());
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
                                mDialog.dismiss();
                                mDialog.cancel();
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
    public void onBackPressed()
    {super.onBackPressed();

        Intent intent = new Intent(LoginActivity.this, StartLoginActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();

        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure you want to close the application?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        */
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
        askPermission();

        RegisterActivity.startActivity(this, this, findViewById(R.id.button));
    }


    // cHat ... Login Server

    @Override
    public void onValidationSucceeded() {
        //username_str = username.getText().toString().trim();
        //password_str = password.getText().toString();

        String url_str = Config.URL_CHAT_SERVER.trim();
        if (!url_str.startsWith("https://") && !url_str.startsWith("http://")) {
            url_str = "http://" + url_str;
        }

        if (!url_str.matches(".*:([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$.*")) {
            url_str = url_str + ":5000";
        }

        Log.i(TAG, "Connecting to " + url_str);

        (new LoginActivity.LoginAsyncTask(url_str, username_str, password_str, true)).execute();
        Toasty.info(getApplicationContext(), "Logging in...", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }


    class LoginAsyncTask extends AsyncTask<String, String, JSONObject> {

        private int user_id;
        private final String url, username, password;
        private final boolean rememberMe;

        public LoginAsyncTask(String url, String username, String password, boolean rememberMe) {
            this.url = url;
            this.username = username;
            this.password = password;
            this.rememberMe = rememberMe;
        }

        public void showServerError() {
            Toast.makeText(
                    getApplicationContext(),
                    "Unable to login; please try again later",
                    Toast.LENGTH_LONG
            ).show();
        }

        public void showInvalidAuthError() {
            Toasty.error(
                    getApplicationContext(),
                    "Invalid username or password",
                    Toast.LENGTH_LONG
            ).show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject input_json = new JSONObject();
            try {
                input_json.put("username", username);
                input_json.put("password", password);
            } catch(JSONException e) {
                e.printStackTrace();
                return null;
            }

            JSONParser jsonParser = new JSONParser();

            JSONObject output_json = jsonParser.getJSONFromUrl(
                    url + "/login",
                    input_json
            );

            Log.i("login", "output_json");

            return output_json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            boolean authenticated;
            if(json == null) {
                showServerError();
                return;
            }

            try {
                authenticated = json.getBoolean("authenticated");
            } catch (JSONException e) {
                e.printStackTrace();
                showServerError();
                return;
            }

            if(!authenticated) {
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showInvalidAuthError();
                    }
                });
                return;
            }

            String session;

            try {
                session = json.getString("session");
                user_id = json.getInt("user_id");
            } catch (JSONException e) {
                e.printStackTrace();
                showServerError();
                return;
            }

            chatApplication.setURL(url);
            chatApplication.setUser(new User(user_id, username, session));

            if(rememberMe) {
                chatApplication.rememberCredentials();
            }


            mDialog.dismiss();
            MainActivity_GuDDana.startActivity(LoginActivity.this);
            //finish();

            //Intent menuIntent = new Intent(LoginActivity.this, MenuActivity.class);
            //startActivity(menuIntent);
        }
    }

}
