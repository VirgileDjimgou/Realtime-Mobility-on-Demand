package com.android.gudana.hify.ui.activities.post;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.gudana.hify.utils.AnimationUtil;
import com.android.gudana.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import java.util.HashMap;
import java.util.Map;

import me.grantland.widget.AutofitTextView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PostText extends AppCompatActivity {

    AutofitTextView preview_text;
    EditText text;
    FirebaseFirestore mFirestore;
    FirebaseUser mCurrentUser;
    String color="7";
    private FrameLayout mImageholder;
    private FirebaseAuth mAuth;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PostText.class);
        context.startActivity(intent);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void hashtag_dialog(){

        new LovelyInfoDialog(this)
                .setTopColorRes(R.color.blue)
                .setIcon(R.mipmap.ic_hashtag)
                .setTitle("Hashtag GuDana")
                .setMessage("Add hashtag  to help people on GuDana Network see your post or your services : use the symbol hashtag  # " +
                        "to start you own or choose GuDana sugested hashtag ")
                //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                .setNotShowAgainOptionEnabled(0)
                .setNotShowAgainOptionChecked(true)
                .show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hi_activity_post_text);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("New Text Post");

        try {
            getSupportActionBar().setTitle("New Text Post");
        } catch (Exception e) {
            e.printStackTrace();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        text = findViewById(R.id.text);
        preview_text = findViewById(R.id.text_preview);
        mImageholder = findViewById(R.id.image_holder);


        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                preview_text.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        hashtag_dialog();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.hi_menu_text_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_post:
                if (!TextUtils.isEmpty(text.getText().toString()))
                    sendPost();
                else
                    AnimationUtil.shakeView(text, PostText.this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendPost() {

        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Posting...");
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        mFirestore.collection("Users").document(mCurrentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Map<String, Object> postMap = new HashMap<>();
                postMap.put("userId", documentSnapshot.getString("id"));
                postMap.put("username", documentSnapshot.getString("username"));
                postMap.put("name", documentSnapshot.getString("name"));
                postMap.put("userimage", documentSnapshot.getString("image"));
                postMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
                postMap.put("image_count",0);
                postMap.put("likes", "0");
                postMap.put("favourites", "0");
                postMap.put("description", text.getText().toString());
                postMap.put("color", color);
                postMap.put("is_video_post", false);



                mFirestore.collection("Posts")
                        .add(postMap)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                mDialog.dismiss();
                                Toast.makeText(PostText.this, "Post sent", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mDialog.dismiss();
                                Log.e("Error sending post", e.getMessage());
                            }
                        });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mDialog.dismiss();
                Log.e("Error getting user", e.getMessage());
            }
        });

    }

    public void onFabClicked(View view) {

        switch (view.getId()) {

            case R.id.fab1:
                mImageholder.setBackground(getResources().getDrawable(R.drawable.gradient_2));
                color = "7";
                return;

            case R.id.fab2:
                mImageholder.setBackground(getResources().getDrawable(R.drawable.gradient_7));
                color = "2";
                return;

            case R.id.fab3:
                mImageholder.setBackground(getResources().getDrawable(R.drawable.gradient_8));
                color = "3";
                return;

            case R.id.fab4:
                mImageholder.setBackground(getResources().getDrawable(R.drawable.gradient_4));
                color = "4";
                return;

            case R.id.fab5:
                mImageholder.setBackground(getResources().getDrawable(R.drawable.gradient_1));
                color = "5";
                return;

            case R.id.fab6:
                mImageholder.setBackground(getResources().getDrawable(R.drawable.gradient_3));
                color = "6";
                return;

            case R.id.fab7:
                mImageholder.setBackground(getResources().getDrawable(R.drawable.gradient_9));
                color = "1";
                return;

            case R.id.fab8:
                mImageholder.setBackground(getResources().getDrawable(R.drawable.gradient_11));
                color = "8";
        }

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();

    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }




}
