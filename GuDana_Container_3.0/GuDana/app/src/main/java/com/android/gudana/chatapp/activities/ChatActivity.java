package com.android.gudana.chatapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.BuildConfig;
import com.android.gudana.R;
import com.android.gudana.chatapp.adapters.MessageAdapter;
import com.android.gudana.chatapp.models.Message;
import com.android.gudana.project_3.model.MapModel;
import com.android.gudana.project_3.model.User;
import com.android.gudana.project_3.utils.Constants;
import com.android.gudana.project_3.utils.EmailEncoding;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nightonke.boommenu.BoomMenuButton;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import jp.wasabeef.glide.transformations.CropCircleTransformation;


import com.github.clans.fab.FloatingActionMenu;
import com.nightonke.boommenu.Animation.BoomEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.onegravity.contactpicker.ContactElement;
import com.onegravity.contactpicker.contact.Contact;
import com.onegravity.contactpicker.contact.ContactDescription;
import com.onegravity.contactpicker.contact.ContactSortOrder;
import com.onegravity.contactpicker.core.ContactPickerActivity;
import com.onegravity.contactpicker.group.Group;
import com.onegravity.contactpicker.picture.ContactPictureType;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import jp.wasabeef.glide.transformations.CropCircleTransformation;


import com.github.clans.fab.FloatingActionMenu;
import com.nightonke.boommenu.Animation.BoomEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.onegravity.contactpicker.ContactElement;
import com.onegravity.contactpicker.contact.Contact;
import com.onegravity.contactpicker.contact.ContactDescription;
import com.onegravity.contactpicker.contact.ContactSortOrder;
import com.onegravity.contactpicker.core.ContactPickerActivity;
import com.onegravity.contactpicker.group.Group;
import com.onegravity.contactpicker.picture.ContactPictureType;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

public class ChatActivity extends AppCompatActivity  implements  View.OnClickListener
{
    private final String TAG = "CA/ChatActivity";

    // Will handle all changes happening in database

    private DatabaseReference userDatabase, chatDatabase;
    private ValueEventListener userListener, chatListener;

    // Will handle old/new messages between users

    private Query messagesDatabase;
    private ChildEventListener messagesListener;

    private MessageAdapter messagesAdapter;
    private final List<Message> messagesList = new ArrayList<>();

    // User data

    private String currentUserId;

    // ca_activity_chat views

    private EmojiconEditText messageEditText;
    private RecyclerView recyclerView;
    private Button sendButton;
    private ImageView sendPictureButton;

    // ca_chat_bar views

    private TextView appBarName, appBarSeen;

    // Will be used on Notifications to detairminate if user has chat window open

    public static String otherUserId;
    public static boolean running = false;


    // add ..
    private String messageId;
    private ImageButton mSendButton;
    private String chatName;
    private ListView mMessageList;
    private Toolbar mToolBar;
    private String currentUserEmail;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessageDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;
    private FirebaseListAdapter<Message> mMessageListAdapter;
    private FirebaseAuth mFirebaseAuth;

    private static final int GALLERY_INTENT=2;
    private StorageReference mStorage;
    private ProgressDialog mProgress;

    private ImageButton mrecordVoiceButton;
    // private TextView mRecordLable;

    private MediaRecorder mRecorder;
    private String mFileName = null;

    private static final String LOG_TAG = "Record_log";
    private ValueEventListener mValueEventListener;

    //Audio Runtime Permissions
    private boolean permissionToRecordAccepted = false;
    private boolean permissionToWriteAccepted = false;
    private String [] permissions = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};

    private static final int CONTACT_PICKER_REQUEST = 23 ;


    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;
    private static final int PLACE_PICKER_REQUEST = 3;
    static final String CHAT_REFERENCE = "chatmodel";

    //Views UI
    private ListView rvListMessage;
    private LinearLayoutManager mLinearLayoutManager;
    private ImageView btEmoji;
    private EmojiconEditText edMessage;
    private View contentRoot;
    private EmojIconActions emojIcon;
    private ImageButton recordVoiceButton_2;


    // record audio
    // String filePath = Environment.getExternalStorageDirectory() + "/recorded_audio.wav";
    int requestCode_record = 0;


    // boom menu
    private BoomMenuButton bmb , bmb_ham;

    //File
    private File filePathImageCamera;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 200:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                permissionToWriteAccepted  = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) ChatActivity.super.finish();
        if (!permissionToWriteAccepted ) ChatActivity.super.finish();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.ca_activity_chat);
        setContentView(R.layout.p3_messages_activity);
        running = true;

        // messageEditText = findViewById(R.id.chat_message);
        messageEditText = (EmojiconEditText)findViewById(R.id.editTextMessage);

        recyclerView = findViewById(R.id.chat_recycler);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        otherUserId = getIntent().getStringExtra("userid");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messagesAdapter = new MessageAdapter(messagesList);

        recyclerView.setAdapter(messagesAdapter);

        // Action bar related

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.ca_chat_bar, null);

        appBarName = actionBarView.findViewById(R.id.chat_bar_name);
        appBarSeen = actionBarView.findViewById(R.id.chat_bar_seen);

        actionBar.setCustomView(actionBarView);


        // adad

        // Will handle the send button to send a message


        //Check Permissions at runtime
        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }


        ImageButton button = findViewById(R.id.recordVoiceButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                AndroidAudioRecorder.with(ChatActivity.this)
                        // Required
                        .setFilePath(mFileName)
                        .setColor( getResources().getColor(R.color.colorPrimaryDark))
                        .setRequestCode(requestCode_record)

                        // Optional
                        .setSource(AudioSource.MIC)
                        .setChannel(AudioChannel.STEREO)
                        .setSampleRate(AudioSampleRate.HZ_48000)
                        .setAutoStart(true)
                        .setKeepDisplayOn(true)

                        // Start recording
                        .record();

            }
        });

        sendButton = findViewById(R.id.chat_send);
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendMessage();
            }
        });

        sendPictureButton = findViewById(R.id.chat_send_picture);
        sendPictureButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), 1);
            }
        });


        // Will handle typing feature, 0 means no typing, 1 typing, 2 deleting and 3 thinking (5+ sec delay)

        messageEditText.addTextChangedListener(new TextWatcher()
        {
            private Timer timer = new Timer();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if(messagesList.size() > 0)
                {
                    if(charSequence.length() == 0)
                    {
                        FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId).child("typing").setValue(0);

                        timer.cancel();
                    }
                    else if(i2 > 0)
                    {
                        FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId).child("typing").setValue(1);

                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(new TimerTask()
                        {
                            @Override
                            public void run()
                            {
                                FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId).child("typing").setValue(3);
                            }
                        }, 5000);
                    }
                    else if(i1 > 0)
                    {
                        FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId).child("typing").setValue(2);

                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(new TimerTask()
                        {
                            @Override
                            public void run()
                            {
                                FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId).child("typing").setValue(3);
                            }
                        }, 5000);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
            }
        });

        // Checking if root layout changed to detect soft keyboard

        final RelativeLayout root = findViewById(R.id.chat_root);
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            int previousHeight = root.getRootView().getHeight() - root.getHeight() - recyclerView.getHeight();

            @Override
            public void onGlobalLayout()
            {
                int height = root.getRootView().getHeight() - root.getHeight() - recyclerView.getHeight();

                if(previousHeight != height)
                {
                    if(previousHeight > height)
                    {
                        previousHeight = height;
                    }
                    else if(previousHeight < height)
                    {
                        recyclerView.scrollToPosition(messagesList.size() - 1);

                        previousHeight = height;
                    }
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        running = true;

        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("online").setValue("true");

        loadMessages();
        initDatabases();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        running = false;

        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("online").setValue(ServerValue.TIMESTAMP);

        if(messagesList.size() > 0 && messageEditText.getText().length() > 0)
        {
            FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId).child("typing").setValue(0);
        }

        removeListeners();
    }

    @Override
    public void onBackPressed()
    {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == CONTACT_PICKER_REQUEST){
            if(resultCode == RESULT_OK) {
                List<ContactResult> results = MultiContactPicker.obtainResult(data);
                Log.d("MyTag", results.get(0).getDisplayName());
                int i = 0;
                do{

                    edMessage.setText(results.get(i).getDisplayName()+ " : " + results.get(i).getPhoneNumbers());
                    sendMessage(contentRoot);
                    ContactResult element = results.get(i);
                    i++;
                }
                while (i < results.size());
            } else if(resultCode == RESULT_CANCELED){
                System.out.println("User closed the picker without selecting items.");
            }
        }




        mStorage = FirebaseStorage.getInstance().getReference(); //make global
        super.onActivityResult(requestCode, requestCode, data);

        if(requestCode ==GALLERY_INTENT && resultCode == RESULT_OK){

            mProgress.setMessage("Sending the image...");
            mProgress.show();

            Uri uri = data.getData();
            //Keep all images for a specific chat grouped together
            final String imageLocation = "Photos" + "/" + messageId;
            final String imageLocationId = imageLocation + "/" + uri.getLastPathSegment();
            final String uniqueId = UUID.randomUUID().toString();
            final StorageReference filepath = mStorage.child(imageLocation).child(uniqueId + "/image_message");
            final String downloadURl = filepath.getPath();
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //create a new message containing this image
                    addImageToMessages(downloadURl);
                    mProgress.dismiss();
                }
            });
        }

        else if (requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                if (place!=null){
                    LatLng latLng = place.getLatLng();
                    MapModel mapModel = new MapModel(latLng.latitude+"",latLng.longitude+"");
                    // ChatModel chatModel = new ChatModel(userModel, Calendar.getInstance().getTime().getTime()+"",mapModel);
                    //mFirebaseDatabaseReference.child(CHAT_REFERENCE).push().setValue(chatModel);
                }else{
                    //PLACE IS NULL
                    Log.e("no place", "not place");
                }
            }
        }


        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // Great! User has recorded and saved the audio file
                Log.e("jhkhj", "jhkjh");
                uploadAudio_v2();
            } else if (resultCode == RESULT_CANCELED) {
                // Oops! User has canceled the recording
                Log.e("jhkhj", "jhkjh");
            }
        }



        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            Uri url = data.getData();

            DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId).child(otherUserId).push();
            final String messageId = messageRef.getKey();

            DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(otherUserId).push();
            final String notificationId = notificationRef.getKey();

            StorageReference file = FirebaseStorage.getInstance().getReference().child("message_images").child(messageId + ".jpg");

            file.putFile(url).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                {
                    if(task.isSuccessful())
                    {
                        String imageUrl = task.getResult().getDownloadUrl().toString();

                        Map messageMap = new HashMap();
                        messageMap.put("message", imageUrl);
                        messageMap.put("type", "image");
                        messageMap.put("from", currentUserId);
                        messageMap.put("to", otherUserId);
                        messageMap.put("timestamp", ServerValue.TIMESTAMP);

                        HashMap<String, String> notificationData = new HashMap<>();
                        notificationData.put("from", currentUserId);
                        notificationData.put("type", "message");

                        Map userMap = new HashMap();
                        userMap.put("Messages/" + currentUserId + "/" + otherUserId + "/" + messageId, messageMap);
                        userMap.put("Messages/" + otherUserId + "/" + currentUserId + "/" + messageId, messageMap);

                        userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/message", "You have sent a picture.");
                        userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/timestamp", ServerValue.TIMESTAMP);
                        userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/seen", ServerValue.TIMESTAMP);

                        userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/message", "Has send you a picture.");
                        userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/timestamp", ServerValue.TIMESTAMP);
                        userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/seen", 0);

                        userMap.put("Notifications/" + otherUserId + "/" + notificationId, notificationData);

                        FirebaseDatabase.getInstance().getReference().updateChildren(userMap, new DatabaseReference.CompletionListener()
                        {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                            {
                                sendButton.setEnabled(true);

                                if(databaseError != null)
                                {
                                    Log.d(TAG, "sendMessage(): updateChildren failed: " + databaseError.getMessage());
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void initDatabases()
    {
        // Initialize/Update realtime other user data such as name and online status

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(otherUserId);
        userListener = new ValueEventListener()
        {
            Timer timer;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                try
                {
                    String name = dataSnapshot.child("name").getValue().toString();

                    appBarName.setText(name);

                    final String online = dataSnapshot.child("online").getValue().toString();

                    if(online.equals("true"))
                    {
                        if(timer != null)
                        {
                            timer.cancel();
                            timer = null;
                        }

                        appBarSeen.setText("Online");
                    }
                    else
                    {
                        if(appBarSeen.getText().length() == 0)
                        {
                            appBarSeen.setText("Last Seen: " + getTimeAgo(Long.parseLong(online)));
                        }
                        else
                        {
                            timer = new Timer();
                            timer.schedule(new TimerTask()
                            {
                                @Override
                                public void run()
                                {
                                    ChatActivity.this.runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            appBarSeen.setText("Last Seen: " + getTimeAgo(Long.parseLong(online)));
                                        }
                                    });
                                }
                            }, 2000);
                        }
                    }
                }
                catch(Exception e)
                {
                    Log.d(TAG, "setDatabase(): usersOtherUserListener exception: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d(TAG, "setDatabase(): usersOtherUserListener failed: " + databaseError.getMessage());
            }
        };
        userDatabase.addValueEventListener(userListener);

        //Check if last message is unseen and mark it as seen with current timestamp

        chatDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId);
        chatListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                try
                {
                    if(dataSnapshot.hasChild("seen"))
                    {
                        long seen = (long) dataSnapshot.child("seen").getValue();

                        if(seen == 0)
                        {
                            chatDatabase.child("seen").setValue(ServerValue.TIMESTAMP);
                        }
                    }
                }
                catch(Exception e)
                {
                    Log.d(TAG, "setDatabase(): chatCurrentUserListener exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d(TAG, "setDatabase(): chatCurrentUserListener failed: " + databaseError.getMessage());
            }
        };
        chatDatabase.addValueEventListener(chatListener);
    }

    private void loadMessages()
    {
        messagesList.clear();

        // Load/Update all messages between current and other user

        messagesDatabase = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId).child(otherUserId);
        messagesListener = new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                try
                {
                    Message message = dataSnapshot.getValue(Message.class);

                    messagesList.add(message);
                    messagesAdapter.notifyDataSetChanged();

                    recyclerView.scrollToPosition(messagesList.size() - 1);
                }
                catch(Exception e)
                {
                    Log.d(TAG, "loadMessages(): messegesListener exception: " + e.getMessage());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s)
            {
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d(TAG, "loadMessages(): messegesListener failed: " + databaseError.getMessage());
            }
        };
        messagesDatabase.addChildEventListener(messagesListener);
    }

    private void removeListeners()
    {
        try
        {
            chatDatabase.removeEventListener(chatListener);
            chatListener = null;

            userDatabase.removeEventListener(userListener);
            userListener = null;

            messagesDatabase.removeEventListener(messagesListener);
            messagesListener = null;
        }
        catch(Exception e)
        {
            Log.d(TAG, "exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendMessage()
    {
        sendButton.setEnabled(false);

        String message = messageEditText.getText().toString();

        if(message.length() == 0)
        {
            Toast.makeText(getApplicationContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();

            sendButton.setEnabled(true);
        }
        else
        {
            messageEditText.setText("");

            // Pushing message/notification so we can get keyIds

            DatabaseReference userMessage = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId).child(otherUserId).push();
            String pushId = userMessage.getKey();

            DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(otherUserId).push();
            String notificationId = notificationRef.getKey();

            // "Packing" message

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("type", "text");
            messageMap.put("from", currentUserId);
            messageMap.put("to", otherUserId);
            messageMap.put("timestamp", ServerValue.TIMESTAMP);

            HashMap<String, String> notificationData = new HashMap<>();
            notificationData.put("from", currentUserId);
            notificationData.put("type", "message");

            Map userMap = new HashMap();
            userMap.put("Messages/" + currentUserId + "/" + otherUserId + "/" + pushId, messageMap);
            userMap.put("Messages/" + otherUserId + "/" + currentUserId + "/" + pushId, messageMap);

            userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/message", message);
            userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/timestamp", ServerValue.TIMESTAMP);
            userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/seen", ServerValue.TIMESTAMP);

            userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/message", message);
            userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/timestamp", ServerValue.TIMESTAMP);
            userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/seen", 0);

            userMap.put("Notifications/" + otherUserId + "/" + notificationId, notificationData);

            // Updating database with the new data including message, chat and notification

            FirebaseDatabase.getInstance().getReference().updateChildren(userMap, new DatabaseReference.CompletionListener()
            {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                {
                    sendButton.setEnabled(true);

                    if(databaseError != null)
                    {
                        Log.d(TAG, "sendMessage(): updateChildren failed: " + databaseError.getMessage());
                    }
                }
            });
        }
    }

    private String getTimeAgo(long time)
    {
        final long diff = System.currentTimeMillis() - time;

        if(diff < 1)
        {
            return " just now";
        }
        if(diff < 60 * 1000)
        {
            if(diff / 1000 < 2)
            {
                return diff / 1000 + " second ago";
            }
            else
            {
                return diff / 1000 + " seconds ago";
            }
        }
        else if(diff < 60 * (60 * 1000))
        {
            if(diff / (60 * 1000) < 2)
            {
                return diff / (60 * 1000) + " minute ago";
            }
            else
            {
                return diff / (60 * 1000) + " minutes ago";
            }
        }
        else if(diff < 24 * (60 * (60 * 1000)))
        {
            if(diff / (60 * (60 * 1000)) < 2)
            {
                return diff / (60 * (60 * 1000)) + " hour ago";
            }
            else
            {
                return diff / (60 * (60 * 1000)) + " hours ago";
            }
        }
        else
        {
            if(diff / (24 * (60 * (60 * 1000))) < 2)
            {
                return diff / (24 * (60 * (60 * 1000))) + " day ago";
            }
            else
            {
                return diff / (24 * (60 * (60 * 1000))) + " days ago";
            }
        }
    }



    // ######################################//// #######################################

    private void pickContact(){

        new MultiContactPicker.Builder(ChatActivity.this) //Activity/fragment context
                // .theme(R.style.MyCustomPickerTheme) //Optional - default: MultiContactPicker.Azure
                .hideScrollbar(false) //Optional - default: false
                .showTrack(true) //Optional - default: true
                .searchIconColor(Color.WHITE) //Option - default: White
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                .handleColor(ContextCompat.getColor(ChatActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                .bubbleColor(ContextCompat.getColor(ChatActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                .bubbleTextColor(Color.WHITE) //Optional - default: White
                .showPickerForResult(CONTACT_PICKER_REQUEST);
    }


    /*
    private void bindViews(){
        contentRoot = findViewById(R.id.contentRootchat);
        edMessage = (EmojiconEditText)findViewById(R.id.editTextMessage);
        recordVoiceButton_2 = (ImageButton) findViewById(R.id.recordVoiceButton_2);

        btEmoji = (ImageView)findViewById(R.id.buttonEmoji);
        emojIcon = new EmojIconActions(this,contentRoot,edMessage,btEmoji);
        emojIcon.ShowEmojIcon();
        rvListMessage = (ListView) findViewById(R.id.messageListView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
    }

*/

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.recordVoiceButton:
                // sendMessageFirebase();

                break;
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }


    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(ChatActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    ChatActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }else{
            // we already have permission, lets go ahead and call camera intent
            photoCameraIntent();
        }
    }

    /**
     * location places picker
     */
    private void locationPlacesIntent(){
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * photo camera picker
     */
    private void photoCameraIntent(){
        String nomeFoto = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        filePathImageCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), nomeFoto+"camera.jpg");
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(ChatActivity.this,
                BuildConfig.APPLICATION_ID + ".provider",
                filePathImageCamera);
        it.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
        startActivityForResult(it, IMAGE_CAMERA_REQUEST);
    }


    private void photoGalleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }



    public void openVoiceRecorder(){
        //Implement voice selection
        mrecordVoiceButton =(ImageButton) findViewById(R.id.recordVoiceButton);
        // mRecordLable = (TextView) findViewById(R.id.recordLable);


        mrecordVoiceButton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                    startRecording();

                    //mRecordLable.setText("Recording started...");
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP){

                    stopRecording();

                    //mRecordLable.setText("Recording stopped...");

                }
                return false;
            }
        });

        //on complete: sendVoice()
    }

    private void startRecording() {

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(mFileName);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        uploadAudio();
    }

    private void uploadAudio_v2() {
        mProgress = new ProgressDialog(ChatActivity.this);


        try{

            mStorage = FirebaseStorage.getInstance().getReference();
            mProgress.setMessage("Sending the Audio...");
            mProgress.show();

            Uri uri = Uri.fromFile(new File(mFileName));
            //Keep all voice for a specific chat grouped together
            final String voiceLocation = "Voice" + "/" + messageId;
            final String voiceLocationId = voiceLocation + "/" + uri.getLastPathSegment();
            final String uniqueId = UUID.randomUUID().toString();
            final StorageReference filepath = mStorage.child(voiceLocation).child(uniqueId + "/audio_message.3gp");
            final String downloadURl = filepath.getPath();

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    addVoiceToMessages(downloadURl);
                    mProgress.dismiss();
                    // mRecordLable.setText("Tap and Hold the Phone Button to Record");

                }
            });

        }catch(Exception ex){
            mProgress.dismiss();
            ex.printStackTrace();
        }


    }


    private void uploadAudio() {
        mProgress = new ProgressDialog(ChatActivity.this);


        try{

            mStorage = FirebaseStorage.getInstance().getReference();
            mProgress.setMessage("Sending the Audio...");
            mProgress.show();

            Uri uri = Uri.fromFile(new File(mFileName));
            //Keep all voice for a specific chat grouped together
            final String voiceLocation = "Voice" + "/" + messageId;
            final String voiceLocationId = voiceLocation + "/" + uri.getLastPathSegment();
            final String uniqueId = UUID.randomUUID().toString();
            final StorageReference filepath = mStorage.child(voiceLocation).child(uniqueId + "/audio_message.3gp");
            final String downloadURl = filepath.getPath();

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    addVoiceToMessages(downloadURl);
                    mProgress.dismiss();
                    // mRecordLable.setText("Tap and Hold the Phone Button to Record");

                }
            });

        }catch(Exception ex){
            mProgress.dismiss();
            ex.printStackTrace();
        }


    }


    public void addListeners(){
        edMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

        });
    }

    //If voice message add them to Firebase.Storage
    public void addVoiceToMessages(String voiceLocation){
        final DatabaseReference pushRef = mMessageDatabaseReference.push();
        final String pushKey = pushRef.getKey();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String timestamp = dateFormat.format(date);
        //Create message object with text/voice etc
        com.android.gudana.project_3.model.Message message = new com.android.gudana.project_3.model.Message(EmailEncoding.commaEncodePeriod(mFirebaseAuth.getCurrentUser().getEmail()), "Message: Voice Sent", "VOICE", voiceLocation, timestamp);
        //Create HashMap for Pushing
        HashMap<String, Object> messageItemMap = new HashMap<String, Object>();
        HashMap<String,Object> messageObj = (HashMap<String, Object>) new ObjectMapper()
                .convertValue(message, Map.class);
        messageItemMap.put("/" + pushKey, messageObj);
        mMessageDatabaseReference.updateChildren(messageItemMap)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        edMessage.setText("");
                    }
                });
    }


    //Send image messages from here
    public void addImageToMessages(String imageLocation){
        final DatabaseReference pushRef = mMessageDatabaseReference.push();
        final String pushKey = pushRef.getKey();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String timestamp = dateFormat.format(date);
        //Create message object with text/voice etc
        com.android.gudana.project_3.model.Message message =
                new com.android.gudana.project_3.model.Message(EmailEncoding.commaEncodePeriod(mFirebaseAuth.getCurrentUser().getEmail()),
                        "Message: Image Sent", "IMAGE", imageLocation, timestamp);
        //Create HashMap for Pushing
        HashMap<String, Object> messageItemMap = new HashMap<String, Object>();
        HashMap<String,Object> messageObj = (HashMap<String, Object>) new ObjectMapper()
                .convertValue(message, Map.class);
        messageItemMap.put("/" + pushKey, messageObj);
        mMessageDatabaseReference.updateChildren(messageItemMap)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        edMessage.setText("");
                    }
                });
    }



    public void sendMessage(View view){
        //final DatabaseReference messageRef = mFirebaseDatabase.getReference(Constants.MESSAGE_LOCATION);
        final DatabaseReference pushRef = mMessageDatabaseReference.push();
        final String pushKey = pushRef.getKey();

        String messageString = edMessage.getText().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String timestamp = dateFormat.format(date);
        //Create message object with text/voice etc
        com.android.gudana.project_3.model.Message message = new com.android.gudana.project_3.model.Message(EmailEncoding.commaEncodePeriod(mFirebaseAuth.getCurrentUser().getEmail()), messageString, timestamp);
        //Create HashMap for Pushing
        HashMap<String, Object> messageItemMap = new HashMap<String, Object>();
        HashMap<String,Object> messageObj = (HashMap<String, Object>) new ObjectMapper()
                .convertValue(message, Map.class);
        messageItemMap.put("/" + pushKey, messageObj);
        mMessageDatabaseReference.updateChildren(messageItemMap)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        edMessage.setText("");
                    }
                });
    }


    private void playSound(Uri uri){
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(uri.toString());
        }catch(Exception e){

        }
        mediaPlayer.prepareAsync();
        //You can show progress dialog here untill it prepared to play
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //Now dismis progress dialog, Media palyer will start playing
                mp.start();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                // dissmiss progress bar here. It will come here when MediaPlayer
                //  is not able to play file. You can show error message to user
                return false;
            }
        });
    }

    /*
    private void initializeScreen() {
        mMessageList = (ListView) findViewById(R.id.messageListView);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mSendButton = (ImageButton)findViewById(R.id.sendButton);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUserEmail = EmailEncoding.commaEncodePeriod(mFirebaseAuth.getCurrentUser().getEmail());
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child(Constants.USERS_LOCATION);
        mMessageDatabaseReference = mFirebaseDatabase.getReference().child(Constants.MESSAGE_LOCATION
                + "/" + messageId);

        mToolBar.setTitle(chatName);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
    */
}