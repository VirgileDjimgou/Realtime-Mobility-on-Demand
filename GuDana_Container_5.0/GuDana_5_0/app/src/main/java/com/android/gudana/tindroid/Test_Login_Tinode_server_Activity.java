package com.android.gudana.tindroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.gudana.R;
import com.android.gudana.hify.ui.activities.account.RegisterActivity;
import com.android.gudana.tindroid.account.Utils;
import com.android.gudana.tindroid.media.VxCard;

import java.util.Iterator;

import co.tinode.tinodesdk.PromisedReply;
import co.tinode.tinodesdk.ServerResponseException;
import co.tinode.tinodesdk.Tinode;
import co.tinode.tinodesdk.model.Credential;
import co.tinode.tinodesdk.model.MetaSetDesc;
import co.tinode.tinodesdk.model.ServerMessage;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class Test_Login_Tinode_server_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test__login__tinode_server_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        AppCompatImageView ImageAvatar = (AppCompatImageView) findViewById(R.id.imageAvatar);
        ImageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // onSignUp("chichikolon","patrick" ,"chichi@yahoo.fr" ,"qwertz");
                onSignUp_custom("pat1","djimgou patrick", "qwertz1@yahoo.fr" ,"qwertz");

                Snackbar.make(view, "Start Registration on Remote Server ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    public void onSignUp(final String login ,final String fullName , final String email  ,final String password) {

        final FloatingActionButton signUp = (FloatingActionButton) findViewById(R.id.fab);
        signUp.setEnabled(false);

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Test_Login_Tinode_server_Activity.this);
        String hostName = sharedPref.getString(Utils.PREFS_HOST_NAME, Cache.HOST_NAME);
        boolean tls = sharedPref.getBoolean(Utils.PREFS_USE_TLS, false);

        final AppCompatImageView avatar = (AppCompatImageView) findViewById(R.id.imageAvatar);

        final Tinode tinode = Cache.getTinode();
        try {
            // This is called on the websocket thread.
            tinode.connect(hostName, tls)
                    .thenApply(
                            new PromisedReply.SuccessListener<ServerMessage>() {
                                @Override
                                public PromisedReply<ServerMessage> onSuccess(ServerMessage ignored_msg) throws Exception {
                                    // Try to create a new account.
                                    Bitmap bmp = null;
                                    try {
                                        bmp = ((BitmapDrawable) avatar.getDrawable()).getBitmap();
                                    } catch (ClassCastException ignored) {
                                        // If image is not loaded, the drawable is a vector.
                                        // Ignore it.
                                        ignored.printStackTrace();
                                    }
                                    VxCard vcard = new VxCard(fullName, bmp);
                                    return tinode.createAccountBasic(
                                            login, password, true, null,
                                            new MetaSetDesc<VxCard,String>(vcard, null),
                                            Credential.append(null, new Credential("email", email)));
                                }
                            }, null)
                    .thenApply(
                            new PromisedReply.SuccessListener<ServerMessage>() {
                                @Override
                                public PromisedReply<ServerMessage> onSuccess(final ServerMessage msg) {
                                    // Flip back to login screen on success;
                                    Test_Login_Tinode_server_Activity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            if (msg.ctrl.code >= 300 && msg.ctrl.text.contains("validate credentials")) {
                                                signUp.setEnabled(true);
                                                CredentialsFragment cf = new CredentialsFragment();
                                                Iterator<String> it = msg.ctrl.getStringIteratorParam("cred");
                                                if (it != null) {
                                                    cf.setMethod(it.next());
                                                }
                                            } else {
                                                // We are requesting immediate login with the new account.
                                                // If the action succeeded, assume we have logged in.
                                                // here we should call the login   call the login Activity with  intent to tell that
                                                // the new users are registerde but he should chech his email to vmake a confirmation  ..
                                                //UiUtils.onLoginSuccess(RegisterActivity.this, signUp);

                                                signUp.setEnabled(true);
                                                Intent LoginEmail = new Intent(Test_Login_Tinode_server_Activity.this, com.android.gudana.hify.ui.activities.account.LoginActivity.class);
                                                LoginEmail.putExtra("Email_Confirmation",true);
                                                startActivity(LoginEmail);
                                                Test_Login_Tinode_server_Activity.this.finish();
                                                //
                                            }
                                        }
                                    });
                                    Toasty.error(Test_Login_Tinode_server_Activity.this, "Registration of new Account failed  ! ", Toast.LENGTH_LONG).show();
                                    return null;
                                }
                            },
                            new PromisedReply.FailureListener<ServerMessage>() {
                                @Override
                                public PromisedReply<ServerMessage> onFailure(Exception err) {
                                    final String cause = ((ServerResponseException)err).getReason();
                                    if (cause != null) {
                                        Test_Login_Tinode_server_Activity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                signUp.setEnabled(true);
                                                switch (cause) {
                                                    case "auth":
                                                        // Invalid login
                                                        // ((EditText) parent.findViewById(R.id.newLogin)).setError(getText(R.string.login_rejected));
                                                        // invalide Login
                                                        Toasty.error(Test_Login_Tinode_server_Activity.this, "Invalid Login ...please check your credentials", Toast.LENGTH_LONG).show();
                                                        break;
                                                    case "email":
                                                        // Duplicate email:
                                                        Toasty.error(Test_Login_Tinode_server_Activity.this, "Invalid Email please check your Email !", Toast.LENGTH_LONG).show();
                                                        // ((EditText) parent.findViewById(R.id.email)).setError(getText(R.string.email_rejected));
                                                        break;
                                                }
                                            }
                                        });
                                    }
                                    // parent.reportError(err, signUp, R.string.error_new_account_failed);
                                    Toasty.error(Test_Login_Tinode_server_Activity.this, "Registration of new Account failed  ! ", Toast.LENGTH_LONG).show();
                                    return null;
                                }
                            });

        } catch (Exception e) {
            Toast.makeText(this, "hmmm ...:) Something went wrong with your registration  ", Toast.LENGTH_SHORT).show();
            Log.e("Test", "Something went wrong", e);
            signUp.setEnabled(true);
        }
    }

    public void onSignUp_custom(final String login_var ,final String fullName_ , final String email_  ,final String password_) {

        //final FloatingActionButton signUp = (FloatingActionButton) findViewById(R.id.fab);
        //signUp.setEnabled(false);
        final String login = login_var.trim();
        final String fullName = fullName_.trim();
        final String email = email_.trim();
        final String password = password_.trim();


        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String hostName = sharedPref.getString(Utils.PREFS_HOST_NAME, Cache.HOST_NAME);
        boolean tls = sharedPref.getBoolean(Utils.PREFS_USE_TLS, false);

        final AppCompatImageView avatar = (AppCompatImageView) findViewById(R.id.imageAvatar);
        // final ImageView avatar = (AppCompatImageView) findViewById(R.id.imageAvatar);


        final Tinode tinode = Cache.getTinode();
        try {
            // This is called on the websocket thread.
            tinode.connect(hostName, tls)
                    .thenApply(
                            new PromisedReply.SuccessListener<ServerMessage>() {
                                @Override
                                public PromisedReply<ServerMessage> onSuccess(ServerMessage ignored_msg) throws Exception {
                                    // Try to create a new account.
                                    Bitmap bmp = null;
                                    try {
                                        bmp = ((BitmapDrawable) avatar.getDrawable()).getBitmap();
                                    } catch (ClassCastException ignored) {
                                        // If image is not loaded, the drawable is a vector.
                                        // Ignore it.
                                        ignored.printStackTrace();
                                    }
                                    VxCard vcard = new VxCard(fullName, bmp);
                                    return tinode.createAccountBasic(
                                            login, password, true, null,
                                            new MetaSetDesc<VxCard,String>(vcard, null),
                                            Credential.append(null, new Credential("email", email)));
                                }
                            }, null)
                    .thenApply(
                            new PromisedReply.SuccessListener<ServerMessage>() {
                                @Override
                                public PromisedReply<ServerMessage> onSuccess(final ServerMessage msg) {
                                    // Flip back to login screen on success;
                                    Test_Login_Tinode_server_Activity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            if (msg.ctrl.code >= 300 && msg.ctrl.text.contains("validate credentials")) {
                                                // signUp.setEnabled(true);
                                                CredentialsFragment cf = new CredentialsFragment();
                                                Iterator<String> it = msg.ctrl.getStringIteratorParam("cred");
                                                if (it != null) {
                                                    cf.setMethod(it.next());
                                                }
                                            } else {
                                                // We are requesting immediate login with the new account.
                                                // If the action succeeded, assume we have logged in.
                                                // here we should call the login   call the login Activity with  intent to tell that
                                                // the new users are registerde but he should chech his email to vmake a confirmation  ..
                                                //UiUtils.onLoginSuccess(RegisterActivity.this, signUp);

                                                // signUp.setEnabled(true);
                                                Intent LoginEmail = new Intent(Test_Login_Tinode_server_Activity.this ,  LoginActivity.class);
                                                LoginEmail.putExtra("Email_Confirmation",true);
                                                startActivity(LoginEmail);
                                                //SignUpFragment.this.finish();
                                                //
                                            }
                                        }
                                    });
                                    //Toasty.error(Test_Login_Tinode_server_Activity.this, "Registration of new Account failed  ! ", Toast.LENGTH_LONG).show();
                                    return null;
                                }
                            },
                            new PromisedReply.FailureListener<ServerMessage>() {
                                @Override
                                public PromisedReply<ServerMessage> onFailure(Exception err) {
                                    final String cause = ((ServerResponseException)err).getReason();
                                    if (cause != null) {
                                        Test_Login_Tinode_server_Activity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // signUp.setEnabled(true);
                                                switch (cause) {
                                                    case "auth":
                                                        // Invalid login
                                                        // ((EditText) parent.findViewById(R.id.newLogin)).setError(getText(R.string.login_rejected));
                                                        // invalide Login
                                                        //Toasty.error(Test_Login_Tinode_server_Activity.this, "Invalid Login ...please check your credentials", Toast.LENGTH_LONG).show();
                                                        break;
                                                    case "email":
                                                        // Duplicate email:
                                                        //Toasty.error(Test_Login_Tinode_server_Activity.this, "Invalid Email please check your Email !", Toast.LENGTH_LONG).show();
                                                        // ((EditText) parent.findViewById(R.id.email)).setError(getText(R.string.email_rejected));
                                                        break;
                                                }
                                            }
                                        });
                                    }
                                    // parent.reportError(err, signUp, R.string.error_new_account_failed);
                                    //Toasty.error(Test_Login_Tinode_server_Activity.this, "Registration of new Account failed  ! ", Toast.LENGTH_LONG).show();
                                    return null;
                                }
                            });

        } catch (Exception e) {
            Toast.makeText(this, "hmmm ...:) Something went wrong with your registration  ", Toast.LENGTH_SHORT).show();
            Log.e("Test", "Something went wrong", e);
            //signUp.setEnabled(true);
        }
    }



}
