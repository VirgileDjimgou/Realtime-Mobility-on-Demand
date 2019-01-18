package com.android.gudana.hify.ui.activities.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import android.text.TextUtils;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.gudana.chat.ChatApplication;
import com.android.gudana.chat.model.User;
import com.android.gudana.chat.network.JSONParser;
import com.android.gudana.gpslocationtracking.LocationTrack;
import com.android.gudana.hify.utils.AnimationUtil;
import com.android.gudana.hify.utils.Config;
import com.android.gudana.hify.utils.PathUtil;
import com.android.gudana.hify.utils.database.UserHelper;
import com.android.gudana.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobsandgeeks.saripaar.Validator;
import com.yalantis.ucrop.UCrop;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

public class RegisterActivity extends AppCompatActivity implements Validator.ValidationListener {

    private static final String TAG = "SignUpFragment";

    private static final int PICK_IMAGE =100 ;
    public Uri imageUri;

    private static final int PIC_CROP = 1;
    public StorageReference storageReference;
    public ProgressDialog mDialog;
    public String name_, pass_, pass_2, email_,username_,location_ = "--##--";
    private EditText name,password_2,username;

    @NotEmpty
    private EditText email;


    @NotEmpty
    private EditText password;

    private CircleImageView profile_image;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    LocationTrack locationTrack;
    String UserAdresse = "";
    private ProgressDialog mDialog_compress_image;
    Button register ;

    // Chat

    ChatApplication chatApplication;
    private boolean signingUp = false;
    private Validator validator;


    public static void startActivity(Activity activity, Context context, View view) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hi_activity_register);

        chatApplication = ((ChatApplication) RegisterActivity.this.getApplication());

        validator = new Validator(this);
        validator.setValidationListener(this);


        // set offline capiblities    ...
        try{
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        }catch (Exception ex){
            ex.printStackTrace();
        }

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        mAuth= FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference().child("images");
        firebaseFirestore= FirebaseFirestore.getInstance();
        imageUri=null;
        UserHelper userHelper = new UserHelper(this);


        name=(EditText)findViewById(R.id.name);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        username=(EditText)findViewById(R.id.username);
        password_2 = (EditText)findViewById(R.id.password_repeat);

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
                    Toasty.info(RegisterActivity.this, "Registration Cancelled", Toast.LENGTH_SHORT).show();
                    Intent LoginEmail = new Intent(RegisterActivity.this, com.android.gudana.hify.ui.activities.account.LoginActivity.class);
                    startActivity(LoginEmail);
                    RegisterActivity.this.finish();

                }catch(Exception ex){
                    ex.printStackTrace();
                }

            }
        });
        mDialog.setCanceledOnTouchOutside(false);


        register = findViewById(R.id.button);

        profile_image=findViewById(R.id.profile_image);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Fade fade = new Fade();
            fade.excludeTarget(findViewById(R.id.layout), true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fade.excludeTarget(android.R.id.statusBarBackground, true);
                fade.excludeTarget(android.R.id.navigationBarBackground, true);
                getWindow().setEnterTransition(fade);
                getWindow().setExitTransition(fade);
            }
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(imageUri!=null){
                    username_=username.getText().toString();
                    name_=name.getText().toString();
                    email_=email.getText().toString();
                    pass_=password.getText().toString();
                    pass_2 = password_2.getText().toString();

                    mDialog.show();

                    if (TextUtils.isEmpty(username_)) {

                        AnimationUtil.shakeView(username, RegisterActivity.this);
                        mDialog.dismiss();

                    }

                    if (TextUtils.isEmpty(name_)) {

                        AnimationUtil.shakeView(name, RegisterActivity.this);
                        mDialog.dismiss();

                    }
                    if (TextUtils.isEmpty(email_)) {

                        AnimationUtil.shakeView(email, RegisterActivity.this);
                        mDialog.dismiss();

                    }
                    if (TextUtils.isEmpty(pass_) ||  TextUtils.isEmpty(pass_2)){


                        // chehck password  are correct  ...
                        AnimationUtil.shakeView(password, RegisterActivity.this);
                        AnimationUtil.shakeView(password_2, RegisterActivity.this);

                        mDialog.dismiss();

                    }

                    if (TextUtils.isEmpty(location_)) {

                        mDialog.dismiss();

                    }

                    if (!TextUtils.isEmpty(name_) || !TextUtils.isEmpty(email_) ||
                            !TextUtils.isEmpty(pass_) || !TextUtils.isEmpty(username_) || !TextUtils.isEmpty(location_)) {

                        if(pass_.equals(pass_2)){

                            // mDialog.show();
                            register.setEnabled(false);
                            firebaseFirestore.collection("Usernames")
                                    .document(username_)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if(!documentSnapshot.exists()){
                                                registerUser("xxxxxxwwwwwwwww");
                                                // onSignUp(email_.trim() ,name_.trim() ,email_.trim(),pass_.trim());

                                            }else{
                                                register.setEnabled(true);

                                                //Toast.makeText(RegisterActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                                                AnimationUtil.shakeView(username, RegisterActivity.this);
                                                mDialog.dismiss();
                                                new LovelyInfoDialog(RegisterActivity.this)
                                                        .setTopColorRes(R.color.colorPrimary)
                                                        .setIcon(R.mipmap.ic_infos)
                                                        //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                                                        //.setNotShowAgainOptionEnabled(0)
                                                        //.setNotShowAgainOptionChecked(true)
                                                        .setTitle("Infos ")
                                                        .setMessage("Username already exists")
                                                        .show();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("Error",e.getMessage());

                                            register.setEnabled(true);
                                            mDialog.dismiss();
                                            Toasty.error(RegisterActivity.this, "error!", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }else{
                            register.setEnabled(true);
                            mDialog.dismiss();
                            Toasty.error(RegisterActivity.this, "please  chechk your password !", Toast.LENGTH_SHORT).show();
                        }

                    }else{

                        AnimationUtil.shakeView(username, RegisterActivity.this);
                        AnimationUtil.shakeView(name, RegisterActivity.this);
                        AnimationUtil.shakeView(email, RegisterActivity.this);
                        AnimationUtil.shakeView(password, RegisterActivity.this);
                        mDialog.dismiss();
                        register.setEnabled(true);
                        Toasty.error(RegisterActivity.this, "error", Toast.LENGTH_SHORT).show();

                    }

                }else{
                    AnimationUtil.shakeView(profile_image, RegisterActivity.this);
                    //Toast.makeText(RegisterActivity.this, "We recommend you to set a profile picture", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                    register.setEnabled(true);
                    mDialog.dismiss();
                    Toasty.error(RegisterActivity.this, "error", Toast.LENGTH_SHORT).show();

                    new LovelyInfoDialog(RegisterActivity.this)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.mipmap.ic_infos)
                            //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                            //.setNotShowAgainOptionEnabled(0)
                            //.setNotShowAgainOptionChecked(true)
                            .setTitle("Infos ")
                            .setMessage("We recommend you to set a profile picture")
                            .show();
                }
            }
        });


        locationTrack = new LocationTrack(RegisterActivity.this);
        // chech if gps is enable  ....
        if (locationTrack.canGetLocation()) {

            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            location_ = getAddress(latitude, longitude);

        } else {
            locationTrack.showSettingsAlert();
        }


    }


    @Override
    protected void onResume() {
        super.onResume();


    }


    //get the adresse
    public String getAddress(double lat, double lng) {

        String Adresse = "";
        Geocoder geocoder = new Geocoder(RegisterActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            //add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            //add = add + "\n" + obj.getSubThoroughfare();

            Log.v("IGA", "Address" + add);
            Adresse = add;
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return  Adresse;

    }

    private void registerUser(final String Uid_tindroid) {

        mAuth.createUserWithEmailAndPassword(email_, pass_).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Map<String,Object> usernameMap=new HashMap<String, Object>();
                    usernameMap.put("username",username_);

                    firebaseFirestore.collection("Usernames")
                            .document(username_)
                            .set(usernameMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    task.getResult()
                                            .getUser()
                                            .sendEmailVerification()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    final String userUid = task.getResult().getUser().getUid();
                                                    final StorageReference user_profile = storageReference.child(userUid + ".jpg");
                                                    user_profile.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                                                            if (task.isSuccessful()) {

                                                               user_profile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                   @Override
                                                                   public void onSuccess(Uri uri) {

                                                                       String token_id = FirebaseInstanceId.getInstance().getToken();
                                                                       Map<String, Object> userMap = new HashMap<>();
                                                                       userMap.put("id", userUid);
                                                                       userMap.put("name", name_);
                                                                       userMap.put("image", uri.toString());
                                                                       userMap.put("email", email_);
                                                                       userMap.put("bio",getString(R.string.default_bio));
                                                                       userMap.put("username", username_);
                                                                       userMap.put("location", location_);
                                                                       userMap.put("uid_tindroid", Uid_tindroid);
                                                                       userMap.put("token_id", FirebaseInstanceId.getInstance().getToken()); // hier we must put the token id    ....   ...

                                                                       firebaseFirestore.collection("Users").document(userUid).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                           @Override
                                                                           public void onSuccess(Void aVoid) {

                                                                               FirebaseAuth.getInstance().signOut();
                                                                               //onSignUp(email_.trim() ,name_.trim() ,email_.trim(),pass_.trim() , userUid);

                                                                           }
                                                                       }).addOnFailureListener(new OnFailureListener() {
                                                                           @Override
                                                                           public void onFailure(@NonNull Exception e) {
                                                                               mDialog.dismiss();
                                                                               register.setEnabled(true);
                                                                               //Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                               new LovelyInfoDialog(RegisterActivity.this)
                                                                                       .setTopColorRes(R.color.colorPrimary)
                                                                                       .setIcon(R.mipmap.ic_infos)
                                                                                       //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                                                                                       //.setNotShowAgainOptionEnabled(0)
                                                                                       //.setNotShowAgainOptionChecked(true)
                                                                                       .setTitle("Infos")
                                                                                       .setMessage("Error : "+e.getMessage())
                                                                                       .show();
                                                                           }
                                                                       });


                                                                       // registering new User s
                                                                       // Registering user with data he gave us
                                                                       FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                                                       if(firebaseUser != null)
                                                                       {
                                                                           String userid = firebaseUser.getUid();

                                                                           // "Packing" user data #
                                                                           // je doi declarer un objet user icic et voir comment ce  se passe ...

                                                                           Map map = new HashMap<>();
                                                                           map.put("token", FirebaseInstanceId.getInstance().getToken()); // i can use this  for cloud mess
                                                                           map.put("name", username_);
                                                                           map.put("email", email_);
                                                                           map.put("status", "Welcome to my GuDana Profile!");
                                                                           map.put("image", uri.toString());
                                                                           map.put("cover", uri.toString());
                                                                           map.put("date", ServerValue.TIMESTAMP);

                                                                           // Uploading user data
                                                                           // beause of final dig .... :) ;
                                                                           //StaticConfigUser_fromFirebase.USER_URL_IMAGE = uri.toString();


                                                                           FirebaseDatabase.getInstance().getReference().child("Users").child(userid).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>()
                                                                           {
                                                                               @Override
                                                                               public void onComplete(@NonNull Task<Void> task)
                                                                               {
                                                                                   if(task.isSuccessful())
                                                                                   {

                                                                                       //Intent LoginEmail = new Intent(RegisterActivity.this, com.android.gudana.hify.ui.activities.account.LoginActivity.class);
                                                                                       //LoginEmail.putExtra("Email_Confirmation",true);
                                                                                       //startActivity(LoginEmail);
                                                                                       //RegisterActivity.this.finish();


                                                                                       signingUp = false ;
                                                                                       validator.validate();
                                                                                   }
                                                                                   else
                                                                                   {
                                                                                       mDialog.dismiss();
                                                                                       Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                                                                   }
                                                                               }
                                                                           });
                                                                       }


                                                                   }
                                                               }).addOnFailureListener(new OnFailureListener() {
                                                                           @Override
                                                                           public void onFailure(@NonNull Exception e) {
                                                                               mDialog.dismiss();
                                                                               Toasty.error(RegisterActivity.this, "Registration failure ...please try again", Toast.LENGTH_SHORT).show();

                                                                           }
                                                                });


                                                            } else {
                                                                mDialog.dismiss();
                                                                Toasty.error(RegisterActivity.this, "Registration failure ...please try again", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    mDialog.dismiss();
                                                    task.getResult().getUser().delete();
                                                    Toasty.error(RegisterActivity.this, "Registration failure ...please try again", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    mDialog.dismiss();
                                    Log.e("Error",e.getMessage());
                                    Toasty.error(RegisterActivity.this, "Registration failure ...please try again", Toast.LENGTH_SHORT).show();
                                }
                            });


                } else {
                    mDialog.dismiss();
                    register.setEnabled(true);

                    //Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    new LovelyInfoDialog(RegisterActivity.this)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.mipmap.ic_infos)
                            //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                            //.setNotShowAgainOptionEnabled(0)
                            //.setNotShowAgainOptionChecked(true)
                            .setTitle("Infos")
                            .setMessage("Error : " + task.getException().getMessage())
                            .show();

                }
            }
        });

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // we must cimpresse the image here ....

        Uri realPaturi = null;
        if(requestCode==PICK_IMAGE){
            if(resultCode==RESULT_OK){
                imageUri=data.getData();
                realPaturi = data.getData();
                // start crop activity
                final UCrop.Options options = new UCrop.Options();
                options.setCompressionFormat(Bitmap.CompressFormat.PNG);
                options.setCompressionQuality(100);
                options.setShowCropGrid(true);

                // compress images

                mDialog_compress_image = new ProgressDialog(this);
                mDialog_compress_image.setMessage("wait while we compress selected image ... ");
                mDialog_compress_image.setIndeterminate(true);
                mDialog_compress_image.setCanceledOnTouchOutside(false);
                mDialog_compress_image.setCancelable(false);
                mDialog_compress_image.show();

                try{

                    new Compressor(this)
                            .compressToFileAsFlowable(new File(PathUtil.getPath(RegisterActivity.this, imageUri)))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<File>() {
                                @Override
                                public void accept(File file) {
                                    //compressedImage = file;
                                    // new PostImage.UploadFileToServer(file, Config.IMAGES_UPLOAD_URL,finalI).execute();
                                    // dismis dialog
                                    mDialog_compress_image.dismiss();
                                    System.out.println(file.toURI());
                                    // je ne suis pas sur du concept global
                                    UCrop.of(Uri.fromFile(file), Uri.fromFile(new File(getCacheDir(), "hify_user_profile_picture.png")))
                                            .withAspectRatio(1, 1)
                                            .withOptions(options)
                                            .start(RegisterActivity.this);

                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) {
                                    mDialog_compress_image.dismiss();
                                    Toasty.error(RegisterActivity.this, "please select another image ", Toast.LENGTH_SHORT).show();
                                    throwable.printStackTrace();
                                    //showError(throwable.getMessage());
                                }
                            });

                }catch (Exception ex){
                    mDialog_compress_image.dismiss();
                    Toasty.error(RegisterActivity.this, "please select another image ", Toast.LENGTH_SHORT).show();
                    ex.printStackTrace();
                }


            }
        }
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                imageUri = UCrop.getOutput(data);
                CircleImageView new_profile_image;

                //profile_image = null;
                //profile_image=findViewById(R.id.profile_image);
                if(imageUri!=null){
                    profile_image.setImageURI(imageUri);
                }else{
                    Toasty.error(RegisterActivity.this, "wrong image paths  ...please chehck your image", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == UCrop.RESULT_ERROR) {
                Log.e("Error", "Crop error:" + UCrop.getError(data).getMessage());
            }
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

    public void setProfilepic(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE);
    }

    // add register methode   Tindroid


    @Override
    public void onValidationSucceeded() {
        String username_str = email.getText().toString().trim();
        String password_str = password.getText().toString();


        String url_str = Config.URL_CHAT_SERVER.trim();
        if (!url_str.startsWith("https://") && !url_str.startsWith("http://")) {
            url_str = "http://" + url_str;
        }

        if (!url_str.matches(".*:([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$.*")) {
            url_str = url_str + ":5000";
        }

        Log.i(TAG, "Connecting to " + url_str);

        (new RegisterActivity.SignUpAsyncTask(url_str, username_str, password_str, true)).execute();
        Toast.makeText(getApplicationContext(), "Signing up...", Toast.LENGTH_SHORT).show();


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


    class SignUpAsyncTask extends AsyncTask<String, String, JSONObject> {

        final String url, username, password;
        private int user_id;
        private final boolean rememberMe;

        public SignUpAsyncTask(String url, String username, String password, boolean rememberMe) {
            this.url = url;
            this.username = username;
            this.password = password;
            this.rememberMe = rememberMe;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject input_json = new JSONObject();
            try {
                input_json.put("username", username);
                input_json.put("password", password);
            } catch(JSONException e) {
                e.printStackTrace();
            }

            JSONParser jsonParser = new JSONParser();
            JSONObject output_json = jsonParser.getJSONFromUrl(url + "/signup", input_json);
            Log.i("login", "output_json");

            return output_json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            boolean registered = false;
            if(json == null) return;
            try {
                registered = json.getBoolean("registered");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(!registered) {
                RegisterActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(getApplicationContext(), "Unable to register", Toast.LENGTH_LONG);
                        toast.show();
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
                return;
            }

            chatApplication.setURL(url);
            chatApplication.setUser(new User(user_id, username, session));

            if(rememberMe) {
                chatApplication.rememberCredentials();
            }


            mDialog.dismiss();
            Toast.makeText(getApplicationContext(), "User registered .", Toast.LENGTH_LONG).show();
            Intent LoginEmail = new Intent(RegisterActivity.this, com.android.gudana.hify.ui.activities.account.LoginActivity.class);
            LoginEmail.putExtra("Email_Confirmation",true);
            startActivity(LoginEmail);
            RegisterActivity.this.finish();


            //Intent menuIntent = new Intent(RegisterActivity.this, MenuActivity.class);
            //startActivity(menuIntent);
        }
    }
}
