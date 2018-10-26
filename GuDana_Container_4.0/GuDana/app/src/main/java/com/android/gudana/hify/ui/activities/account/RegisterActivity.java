package com.android.gudana.hify.ui.activities.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.gudana.chatapp.models.StaticConfigUser_fromFirebase;
import com.android.gudana.gpslocationtracking.LocationTrack;
import com.android.gudana.hify.utils.AnimationUtil;
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
import com.yalantis.ucrop.UCrop;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE =100 ;
    public Uri imageUri;

    private static final int PIC_CROP = 1;
    public StorageReference storageReference;
    public ProgressDialog mDialog;
    public String name_, pass_, email_,username_,location_ = "--##--";
    private EditText name,email,password,username;
    private CircleImageView profile_image;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    LocationTrack locationTrack;
    String UserAdresse = "";


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

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait..");
        mDialog.setIndeterminate(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);

        Button register = findViewById(R.id.button);

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
                    if (TextUtils.isEmpty(pass_)) {

                        AnimationUtil.shakeView(password, RegisterActivity.this);
                        mDialog.dismiss();

                    }

                    if (TextUtils.isEmpty(location_)) {

                        mDialog.dismiss();

                    }

                    if (!TextUtils.isEmpty(name_) || !TextUtils.isEmpty(email_) ||
                            !TextUtils.isEmpty(pass_) || !TextUtils.isEmpty(username_) || !TextUtils.isEmpty(location_)) {

                        firebaseFirestore.collection("Usernames")
                                .document(username_)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(!documentSnapshot.exists()){
                                            registerUser();
                                        }else{
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
                                    }
                                });

                    }else{

                        AnimationUtil.shakeView(username, RegisterActivity.this);
                        AnimationUtil.shakeView(name, RegisterActivity.this);
                        AnimationUtil.shakeView(email, RegisterActivity.this);
                        AnimationUtil.shakeView(password, RegisterActivity.this);
                        mDialog.dismiss();

                    }

                }else{
                    AnimationUtil.shakeView(profile_image, RegisterActivity.this);
                    //Toast.makeText(RegisterActivity.this, "We recommend you to set a profile picture", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();

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


    //get the adresse
    public String getAddress(double lat, double lng) {

        String Adresse = "";
        Geocoder geocoder = new Geocoder(RegisterActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            //add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            //add = add + "\n" + obj.getPostalCode();
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

    private void registerUser() {

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
                                                                       userMap.put("token_id", FirebaseInstanceId.getInstance().getToken()); // hier we must put the token id    ....   ...

                                                                       firebaseFirestore.collection("Users").document(userUid).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                           @Override
                                                                           public void onSuccess(Void aVoid) {
                                                                               mDialog.dismiss();
                                                                               //Toast.makeText(RegisterActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                                                                               Toasty.warning(RegisterActivity.this, "A Verification Link has been sent to your email Account . " +
                                                                                       "Please Click on the link to continue the validation process", Toast.LENGTH_SHORT).show();

                                                                               Toasty.warning(RegisterActivity.this, "A Verification Link has been sent to your email Account . " +
                                                                                       "Please Click on the link to continue the validation process", Toast.LENGTH_SHORT).show();

                                                                               FirebaseAuth.getInstance().signOut();


                                                                               finish();
                                                                           }
                                                                       }).addOnFailureListener(new OnFailureListener() {
                                                                           @Override
                                                                           public void onFailure(@NonNull Exception e) {
                                                                               mDialog.dismiss();
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
                                                                           StaticConfigUser_fromFirebase.USER_URL_IMAGE = uri.toString();


                                                                           FirebaseDatabase.getInstance().getReference().child("Users").child(userid).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>()
                                                                           {
                                                                               @Override
                                                                               public void onComplete(@NonNull Task<Void> task)
                                                                               {
                                                                                   if(task.isSuccessful())
                                                                                   {

                                                                                       Toast.makeText(getApplicationContext(), "User registered .", Toast.LENGTH_LONG).show();

                                                                                       // after  save the   User data on Device  for later Use  ....   ...
                                                                                       // StaticConfigUser_fromFirebase.STR_EXTRA_USERNAME = username_;
                                                                                       StaticConfigUser_fromFirebase.STR_EXTRA_EMAIL = email_;
                                                                                       StaticConfigUser_fromFirebase.STR_USER_ID = userUid;
                                                                                       StaticConfigUser_fromFirebase.USER_NAME = name_;
                                                                                       StaticConfigUser_fromFirebase.STR_USER_TOKEN_FCM = FirebaseInstanceId.getInstance().getToken();
                                                                                   }
                                                                                   else
                                                                                   {
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
                                                                           }
                                                                });


                                                            } else {
                                                                mDialog.dismiss();
                                                            }
                                                        }
                                                    });

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    task.getResult().getUser().delete();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Error",e.getMessage());
                                }
                            });


                } else {
                    mDialog.dismiss();
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

        if(requestCode==PICK_IMAGE){
            if(resultCode==RESULT_OK){
                imageUri=data.getData();
                // start crop activity
                UCrop.Options options = new UCrop.Options();
                options.setCompressionFormat(Bitmap.CompressFormat.PNG);
                options.setCompressionQuality(100);
                options.setShowCropGrid(true);

                UCrop.of(imageUri, Uri.fromFile(new File(getCacheDir(), "hify_user_profile_picture.png")))
                        .withAspectRatio(1, 1)
                        .withOptions(options)
                        .start(this);

            }
        }
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                imageUri = UCrop.getOutput(data);
                profile_image.setImageURI(imageUri);
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

}
