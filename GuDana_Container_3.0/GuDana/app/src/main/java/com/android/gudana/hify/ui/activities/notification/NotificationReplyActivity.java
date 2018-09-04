package com.android.gudana.hify.ui.activities.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.hify.ui.activities.friends.SendActivity;
import com.android.gudana.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NotificationReplyActivity extends AppCompatActivity {

    private TextView nameTxt, messageTxt;
    private String msg, reply;
    private CircleImageView imageView;

    private TextView replyFor;
    private String user_id, current_id;
    private Button mSend;
    private EditText message;
    private FirebaseFirestore mFirestore;
    private ProgressBar mBar;
    private TextView username;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hi_activity_notification_reply);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        /*int[] drawable={R.drawable.gradient_1,R.drawable.gradient_4,R.drawable.gradient_6,R.drawable.gradient_5,R.drawable.gradient_8,R.drawable.gradient_10,R.drawable.gradient_9};
        findViewById(R.id.main_layout).setBackground(getResources().getDrawable(drawable[new Random().nextInt(drawable.length)]));
*/
        nameTxt = (TextView) findViewById(R.id.name);
        messageTxt = (TextView) findViewById(R.id.messagetxt);
        imageView = (CircleImageView) findViewById(R.id.circleImageView);
        replyFor = (TextView) findViewById(R.id.replytxt);

        current_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        username = (TextView) findViewById(R.id.user_name);
        mSend = (Button) findViewById(R.id.send);
        message = (EditText) findViewById(R.id.message);
        mBar = (ProgressBar) findViewById(R.id.progressBar);


        msg = getIntent().getStringExtra("message");
        reply = getIntent().getStringExtra("reply_for");
        user_id = getIntent().getStringExtra("from_id");

        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String image_ = documentSnapshot.getString("image");
                CircleImageView imageView=findViewById(R.id.currentProfile);

                Glide.with(NotificationReplyActivity.this)
                        .setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.default_user_art_g_2))
                        .load(image_)
                        .into(imageView);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        mFirestore.collection("Users").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String name = documentSnapshot.getString("name");
                nameTxt.setText(name);

                String image_ = documentSnapshot.getString("image");

                Glide.with(NotificationReplyActivity.this)
                        .setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.default_user_art_g_2))
                        .load(image_)
                        .into(imageView);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageView.setVisibility(View.GONE);
                nameTxt.setVisibility(View.GONE);
            }
        });

        replyFor.setText(reply);
        messageTxt.setText(msg);


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null && getIntent().getStringExtra("notification_id") != null) {
            notificationManager.cancel(Integer.parseInt(getIntent().getStringExtra("notification_id")));
        }

        initReply();

    }

    private void initReply() {


        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message_ = message.getText().toString();

                if (!TextUtils.isEmpty(message_)) {
                    mBar.setVisibility(View.VISIBLE);
                    Map<String, Object> notificationMessage = new HashMap<>();
                    notificationMessage.put("reply_for", msg);
                    notificationMessage.put("message", message_);
                    notificationMessage.put("from", current_id);
                    notificationMessage.put("notification_id", String.valueOf(System.currentTimeMillis()));
                    notificationMessage.put("timestamp", String.valueOf(System.currentTimeMillis()));

                    mFirestore.collection("Users/" + user_id + "/Notifications_reply").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            Toast.makeText(NotificationReplyActivity.this, "Hify sent!", Toast.LENGTH_SHORT).show();
                            message.setText("");
                            mBar.setVisibility(View.GONE);
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NotificationReplyActivity.this, "Error sending Hify: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            mBar.setVisibility(View.GONE);
                        }
                    });

                }

            }
        });

    }


    public void onSendNew(View view) {
        SendActivity.startActivityExtra(NotificationReplyActivity.this, user_id);
        overridePendingTransitionExit();
    }


}
