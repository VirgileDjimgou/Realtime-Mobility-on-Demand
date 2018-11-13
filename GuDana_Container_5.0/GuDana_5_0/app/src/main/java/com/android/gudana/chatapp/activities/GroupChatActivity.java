package com.android.gudana.chatapp.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.BuildConfig;
import com.android.gudana.R;
import com.android.gudana.apprtc.CallFragment;
import com.android.gudana.apprtc.ConnectActivity;
import com.android.gudana.chatapp.adapters.MessageAdapter;
import com.android.gudana.chatapp.models.Message;
import com.android.gudana.chatapp.models.StaticConfigUser_fromFirebase;
import com.android.gudana.fcm.CustomFcm_Util;
import com.android.gudana.hify.utils.database.UserHelper;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nightonke.boommenu.Animation.BoomEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import es.dmoral.toasty.Toasty;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

// import com.android.gudana.linphone.CallOutgoingActivity;


public class GroupChatActivity extends AppCompatActivity  implements  View.OnClickListener
{
    public  static String pushId_callRoom = "";
    private final String TAG = "CA/GroupChatActivity";

    // Will handle all changes happening in database

    private DatabaseReference userDatabase, chatDatabase;
    private ValueEventListener userListener, chatListener;

    // Will handle old/new messages between users

    private Query messagesDatabase;
    private ChildEventListener messagesListener;
    private MessageAdapter messagesAdapter;
    private final List<Message> messagesList = new ArrayList<>();

    // User data

    public static  String currentUserId;
    public static  String NameCurrentUser = "";
    public static  String url_Icon_currentUser = "";

    // ca_activity_chat views

    private EmojiconEditText messageEditText;
    private RecyclerView recyclerView;
    private Button sendButton;
    private ImageView sendPictureButton;

    // ca_chat_bar views

    private TextView appBarName, appBarSeen;
    private  CircleImageView messageImageRight = null;

    // Will be used on Notifications to detairminate if user has chat window open

    public static String otherUserId , OtherUserIdPhone = "" , PhoneCorrespondant = "";
    public static boolean running = false;

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
    private static final int VOICE_PICKER_REQUEST = 4;

    static final String CHAT_REFERENCE = "chatmodel";

    //Views UI
    private ListView rvListMessage;
    private LinearLayoutManager mLinearLayoutManager;
    private ImageView btEmoji;
    //private EmojiconEditText edMessage;
    private View contentRoot;
    private EmojIconActions emojIcon;
    private ImageButton recordVoiceButton_2;
    private ProgressBar uploadProgress;

    private int progressStatus = 0;
    private int number_of_files_to_send = 0;
    private Handler handler = new Handler();
    private ProgressBar progress_bar;
    private RelativeLayout infos_progress_layout;
    private RelativeLayout  voice_recording_ui;
    private TextView infos_progress_files;
    private TextView infos_progress_stop;
    private Context context;
    public static  DatabaseReference userDB;
    public static DatabaseReference userDB_current;
    // private static boolean CallPossible_button  =  true;

    // record audio
    // String filePath = Environment.getExternalStorageDirectory() + "/recorded_audio.wav";
    int requestCode_record = 200;

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    // boom menu
    private BoomMenuButton bmb ;

    //File
    private File filePathImageCamera;

    String filePath = Environment.getExternalStorageDirectory() + "/recorded_audio.wav";
    //String filePaths_doc =  Environment.getExternalStorageDirectory().getPath();
    private ArrayList<String> docPaths = new ArrayList<>();
    private ArrayList<String> photoPaths = new ArrayList<>();
    int requestCode = 0;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Context mContext=GroupChatActivity.this;
    private static final int REQUEST = 112;
    private static final int EX_FILE_PICKER_RESULT = 55;


    public static  RelativeLayout data_processing = null;
    public static  String call_type = "video";
    // public static  boolean Call_dispo = true;
    public static  boolean callmelder_notification =  false;
    public static CustomFcm_Util FCM_Message_Sender ;
    private UserHelper userHelper;
    public  static String TokenFCM_OtherUser = "";


    // public static Context mContext;



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            try{
                switch (requestCode){
                    case 200:
                        permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        permissionToWriteAccepted  = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                        break;
                }
                if (!permissionToRecordAccepted ) GroupChatActivity.super.finish();
                if (!permissionToWriteAccepted ) GroupChatActivity.super.finish();


                if (requestCode == MY_CAMERA_PERMISSION_CODE) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toasty.info(this, "camera permission granted", Toast.LENGTH_LONG).show();
                        Intent cameraIntent = new
                                Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    } else {
                        Toasty.info(this, "camera permission denied", Toast.LENGTH_LONG).show();
                    }

                }


            }catch (Exception ex){
                ex.printStackTrace();

            }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.ca_activity_chat);
        setContentView(R.layout.p3_messages_activity);
        running = true;
        //ViCall = new LinphoneManager(CreateGroupChatActivity.this.getApplicationContext());

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
        messageImageRight = actionBarView.findViewById(R.id.icon_image);



        // adad

        // Will handle the send button to send a message

        // Get the widgets reference from XML layout
        infos_progress_layout = (RelativeLayout) findViewById(R.id.infos_progress);
        infos_progress_layout.setVisibility(View.GONE);
        infos_progress_files = (TextView) findViewById(R.id.infos_uploading_);
        infos_progress_stop = (TextView) findViewById(R.id.textViewToday);
        progress_bar =(ProgressBar) findViewById(R.id.progressbar_timerview);
        progress_bar.setMax(100);
        // progress_bar.setMax(200);


        // init layout voice recording
        voice_recording_ui = (RelativeLayout) findViewById(R.id.voice_recording);
        voice_recording_ui.setVisibility(View.GONE);


        // init dadata processing
        data_processing = (RelativeLayout) findViewById(R.id.data_processing);
        data_processing.setVisibility(View.GONE);



        // init path audio media
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/recorded_audio.3gp";


        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(mContext, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
            } else {
                //do here
            }
        } else {
            //do here
        }



        try{

            //bmb.setDraggable(true);


            // boom menu    ...
            bmb = (BoomMenuButton) findViewById(R.id.bmb);
            assert bmb != null;
            bmb.setButtonEnum(ButtonEnum.TextOutsideCircle);
            bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_6_4);
            bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_6_4);
            bmb.setBoomEnum(BoomEnum.values()[6]); // random  boom
            bmb.setUse3DTransformAnimation(true);
            bmb.setDuration(500);


            Log.e("test" , "test");

            bmb.clearBuilders();

            // first
            TextOutsideCircleButton.Builder builder_0_doc = new TextOutsideCircleButton.Builder()
                    .normalImageRes(R.mipmap.ic_doc_round)
                    .normalText("document")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {

                            AudioManager audioManager =
                                    (AudioManager) GroupChatActivity.this.getSystemService(Context.AUDIO_SERVICE);
                            audioManager.playSoundEffect(SoundEffectConstants.CLICK);

                            // Toast.makeText(CreateGroupChatActivity.this, "Button " + index + " is pressed.", Toast.LENGTH_SHORT).show();
                            //FilePickerBuilder.getInstance().set
                            FilePickerBuilder.getInstance().setMaxCount(99)
                                    .setSelectedFiles(docPaths)
                                    .enableVideoPicker(true)
                                    .enableDocSupport(true)
                                    .showGifs(true)
                                    .enableSelectAll(true)
                                    .showFolderView(true)
                                    .setActivityTheme(R.style.LibAppTheme)
                                    .pickFile(GroupChatActivity.this);
                        }
                    });

            bmb.addBuilder(builder_0_doc);


            // first
            TextOutsideCircleButton.Builder builder_0_video = new TextOutsideCircleButton.Builder()
                    .normalImageRes(R.mipmap.ic_media)
                    .normalText("Media  share")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {

                            AudioManager audioManager =
                                    (AudioManager) GroupChatActivity.this.getSystemService(Context.AUDIO_SERVICE);
                            audioManager.playSoundEffect(SoundEffectConstants.CLICK);

                            // Toast.makeText(CreateGroupChatActivity.this, "Button " + index + " is pressed.", Toast.LENGTH_SHORT).show();
                            //FilePickerBuilder.getInstance().set
                            FilePickerBuilder.getInstance().setMaxCount(99)
                                    .setSelectedFiles(docPaths)
                                    .enableVideoPicker(true)
                                    .enableDocSupport(true)
                                    .showGifs(true)
                                    .enableSelectAll(true)
                                    .showFolderView(true)
                                    .setActivityTheme(R.style.LibAppTheme)
                                    .pickPhoto(GroupChatActivity.this);
                        }
                    });

            bmb.addBuilder(builder_0_video);




            // second
            TextOutsideCircleButton.Builder builder_1_Camera = new TextOutsideCircleButton.Builder()
                    .normalImageRes(R.mipmap.ic_camera_rec_round)
                    .normalText("Camera")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.CAMERA)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                                            MY_CAMERA_PERMISSION_CODE);
                                } else {
                                    //Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    //startActivityForResult(cameraIntent, CAMERA_REQUEST);
                                    ImagePicker.cameraOnly().start(GroupChatActivity.this); // Could be Activity, Fragment, Support Fragment
                                }
                            }
                        }
                    });

            bmb.addBuilder(builder_1_Camera);


            // third
            TextOutsideCircleButton.Builder builder_2_Gallery = new TextOutsideCircleButton.Builder()
                    .normalImageRes(R.mipmap.ic_gallery_round)
                    .normalText("Gallery")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {


                            ImagePicker.create(GroupChatActivity.this)
                                    .theme(R.style.AppThemeFullScreenImage) // must inherit ef_BaseTheme. please refer to sample
                                    .start(); // start image picker activity with request code
                        }
                    });

            bmb.addBuilder(builder_2_Gallery);

            //five
            TextOutsideCircleButton.Builder builder_4_contact = new TextOutsideCircleButton.Builder()
                    .normalImageRes(R.mipmap.ic_contact_round)
                    .normalText("Contact")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            pickContact();
                            // Toasty.info(ChatMessagesActivity.this, "Button " + index + " is pressed.", Toast.LENGTH_SHORT).show();
                        }
                    });

            bmb.addBuilder(builder_4_contact);


            //five
            TextOutsideCircleButton.Builder builder_5_location = new TextOutsideCircleButton.Builder()
                    .normalImageRes(R.mipmap.ic_location_round)
                    .normalText("Location")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            // TextFcm();
                            locationPlacesIntent();

                            // Toasty.info(ChatMessagesActivity.this, "Button " + index + " is pressed.", Toast.LENGTH_SHORT).show();
                        }
                    });

            bmb.addBuilder(builder_5_location);

        }catch(Exception ex){

            ex.printStackTrace();
        }

        // ######################### /// ###########

        sendButton = findViewById(R.id.chat_send);
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendMessage("text");


                AudioManager audioManager =
                        (AudioManager) GroupChatActivity.this.getSystemService(Context.AUDIO_SERVICE);
                audioManager.playSoundEffect(SoundEffectConstants.CLICK);
                view.playSoundEffect(SoundEffectConstants.CLICK);

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


        btEmoji = (ImageView)findViewById(R.id.buttonEmoji);
        btEmoji = (ImageView)findViewById(R.id.buttonEmoji);
        emojIcon = new EmojIconActions(this,root, messageEditText,btEmoji);
        emojIcon.ShowEmojIcon();


        //Check Permissions at runtime
        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }

        context = GroupChatActivity.this.getApplicationContext();
        //initializeScreen();
        // mToolBar.setTitle(chatName);
        //showMessages();
        //addListeners();

        try{

            openVoiceRecorder();
            // hide Keyboard
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

            askPermission();

            initDatabases();

            // get User Imformation from remote Database or from local Cache ...
            GetInformation_from_Users();

        }catch(Exception ex){
            ex.printStackTrace();
        }
        // appBarName.setText("GuDana User");
        // create  fcm Utill to send call  notification and  special notification   ... to other users
        FCM_Message_Sender = new CustomFcm_Util();
    }


    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void askPermission() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.CALL_PHONE


                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.isAnyPermissionPermanentlyDenied()){
                            Toasty.info(GroupChatActivity.this, "You have denied some permissions permanently, if the app force close try granting permission from settings.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();

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
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:

                //MainActivity_with_Drawer.tabLayout.getTabAt(3);
                //MainActivity_with_Drawer.mViewPager.setCurrentItem(3);
                //play_sound();
                this.finish();
                // NavUtils.navigateUpFromSameTask(this);
                break;

            case R.id.action_call_audio:


                if(CallFragment.running == true ){
                    Toasty.warning(context, "you can not start  two calls at the same time  ! ", Toast.LENGTH_LONG).show();

                }else {
                    call_infos_notification(GroupChatActivity.this.context ,"audio");
                    // call Button

                }

                //Toasty.info(getApplicationContext(), R.string.not_imp6565lemented, Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_call_video:


                if(CallFragment.running == true ){
                    Toasty.warning(context, "you can not start  two calls at the same time  ! ", Toast.LENGTH_LONG).show();
                    
                }else {
                    call_infos_notification(GroupChatActivity.this.context ,"video");
                    // disable call Button  ...

                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {


        Log.e("jhkhj", "jhkjh");

        switch (requestCode) {

            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));


                    number_of_files_to_send = docPaths.size();
                    //progress_bar.setMax(number_of_files_to_send);
                    infos_progress_layout.setVisibility(View.VISIBLE);
                    progress_bar.setProgress(0);
                    infos_progress_files.setText(String.valueOf(progressStatus) +"/"+String.valueOf(number_of_files_to_send) + " files");
                    for (String object: docPaths) {
                        System.out.println(object);
                        String[] split = object.split("\\.");
                        String ext = split[split.length - 1];
                        File file =new File(object);
                        String filename=file.getName();

                        doc_file__upload_images_to_firebase(Uri.fromFile(new File(object.toString())), ext , filename);

                    }

                    Log.e("jhkhj", "jhkjh");

                }
                break;



            case FilePickerConst.REQUEST_CODE_PHOTO:
                if(resultCode== Activity.RESULT_OK && data!=null)
                {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));


                    number_of_files_to_send = photoPaths.size();
                    //progress_bar.setMax(number_of_files_to_send);
                    infos_progress_layout.setVisibility(View.VISIBLE);
                    progress_bar.setProgress(0);
                    infos_progress_files.setText(String.valueOf(progressStatus) +"/"+String.valueOf(number_of_files_to_send) + " files");
                    for (String object: photoPaths) {
                        System.out.println(object);
                        String[] split = object.split("\\.");
                        String ext = split[split.length - 1];
                        File file =new File(object);
                        String filename=file.getName();

                        doc_file__upload_images_to_firebase(Uri.fromFile(new File(object.toString())), ext , filename);

                    }

                    Log.e("jhkhj", "jhkjh");
                }
                break;
        }

        // Images intent
        try{

            super.onActivityResult(requestCode, resultCode, data);



            if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images

            List<Image> images = ImagePicker.getImages(data);
            // progressStatus =+1;
            number_of_files_to_send = images.size();
            //progress_bar.setMax(number_of_files_to_send);
            infos_progress_layout.setVisibility(View.VISIBLE);
            progress_bar.setProgress(0);
            infos_progress_files.setText(String.valueOf(progressStatus) +"/"+String.valueOf(number_of_files_to_send) + " files");

            for (Image uri_img : images) {
                Uri uri_file = Uri.fromFile(new File(uri_img.getPath()));
                images_upload_images_to_firebase(uri_file);
            }



        }

            // contact  Upload

            if(requestCode == CONTACT_PICKER_REQUEST){
                if(resultCode == RESULT_OK) {
                    List<ContactResult> results = MultiContactPicker.obtainResult(data);
                    Log.d("MyTag", results.get(0).getDisplayName());
                    int i = 0;
                    do{

                        //messageEditText.setText(results.get(i).getDisplayName()+ " : " + results.get(i).getPhoneNumbers());
                        //sendMessage("text");
                        sendMessage_location_contact("text" , results.get(i).getDisplayName()+ " : " + results.get(i).getPhoneNumbers());
                        ContactResult element = results.get(i);
                        i++;
                    }
                    while (i < results.size());
                } else if(resultCode == RESULT_CANCELED){
                    System.out.println("User closed the picker without selecting items.");
                }
            }


            // Place upload  .....

            else if (requestCode == PLACE_PICKER_REQUEST){
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(this, data);
                    if (place!=null){
                        LatLng latLng = place.getLatLng();
                        sendMessage("location");
                        sendMessage_location_contact("location" , latLng.latitude+":"+latLng.longitude);
                        // ChatModel chatModel = new ChatModel(userModel, Calendar.getInstance().getTime().getTime()+"",mapModel);
                        //mFirebaseDatabaseReference.child(CHAT_REFERENCE).push().setValue(chatModel);
                    }else{
                        //PLACE IS NULL
                        Log.e("no place", "not place");
                    }
                }
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    private void images_upload_images_to_firebase(Uri Url_media ){

        Uri url = Url_media;

        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId).child(otherUserId).push();
        final String messageId = messageRef.getKey();

        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(otherUserId).push();
        final String notificationId = notificationRef.getKey();

        final StorageReference file = FirebaseStorage.getInstance().getReference().child("message_images").child(messageId + ".jpg");

        // after that send video   ......
        // StorageReference file = FirebaseStorage.getInstance().getReference().child("message_doc").child(messageId +"#"+filename+"."+ ext);
        file.putFile(url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        try{

                                //String imageUrl = task.getResult().getDownloadUrl().toString();
                                String imageUrl = uri.toString();
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


                                // update   ...  progression UI
                                progressStatus = progressStatus +1;
                                infos_progress_files.setText("Uploaded "+String.valueOf(progressStatus) +"/"+String.valueOf(number_of_files_to_send) + " files");
                                // progress_bar.setProgress(progressStatus);

                                // stop the  disapear the animation  ...
                                if(progressStatus >= number_of_files_to_send){

                                    infos_progress_layout.setVisibility(View.GONE);
                                    progressStatus = 0;
                                    number_of_files_to_send = 0;

                                }


                                FirebaseDatabase.getInstance().getReference().updateChildren(userMap, new DatabaseReference.CompletionListener()
                                {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                                    {
                                        sendButton.setEnabled(true);


                                        FCM_Message_Sender.sendWithOtherThread("token" ,
                                                TokenFCM_OtherUser ,
                                                "image",
                                                currentUserId,
                                                StaticConfigUser_fromFirebase.USER_NAME,
                                                StaticConfigUser_fromFirebase.USER_URL_IMAGE,
                                                getDateAndTime(),
                                                "room_disable",
                                                "photo message");

                                        if(databaseError != null)
                                        {
                                            Log.d(TAG, "sendMessage(): updateChildren failed: " + databaseError.getMessage());
                                            infos_progress_layout.setVisibility(View.GONE);
                                            progressStatus = 0;
                                            number_of_files_to_send = 0;
                                            progress_bar.setProgress(0);

                                        }
                                    }
                                });


                        }catch (Exception ex){
                            ex.printStackTrace();
                        }

                    }
                });

            }
        });


        // Observe state change events such as progress, pause, and resume
        file.putFile(url).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");
                // Toasty.info(getApplicationContext(), "Upload is " + progress + "% done", Toast.LENGTH_SHORT).show();
                progress_bar.setProgress((int) Math.floor(progress + 0.5d));

            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                Toasty.info(getApplicationContext(), "upload is paused ! ", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void voice_upload_images_to_firebase(Uri Url_media ){


        mStorage = FirebaseStorage.getInstance().getReference();

        Uri uri = Uri.fromFile(new File(mFileName));

        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId).child(otherUserId).push();
        final String messageId = messageRef.getKey();

        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(otherUserId).push();
        final String notificationId = notificationRef.getKey();

        final StorageReference file = FirebaseStorage.getInstance().getReference().child("message_voices").child(messageId + ".3gp");
        // after that send video   ......
        // StorageReference file = FirebaseStorage.getInstance().getReference().child("message_doc").child(messageId +"#"+filename+"."+ ext);
        file.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        try{
                            //Toast.makeText(create_post.this, "Send : " + uri.toString(), Toast.LENGTH_SHORT).show();
                            String imageUrl = uri.toString();
                            Map messageMap = new HashMap();
                            messageMap.put("message", imageUrl);
                            messageMap.put("type", "voice");
                            messageMap.put("from", currentUserId);
                            messageMap.put("to", otherUserId);
                            messageMap.put("timestamp", ServerValue.TIMESTAMP);

                            HashMap<String, String> notificationData = new HashMap<>();
                            notificationData.put("from", currentUserId);
                            notificationData.put("type", "message");

                            Map userMap = new HashMap();
                            userMap.put("Messages/" + currentUserId + "/" + otherUserId + "/" + messageId, messageMap);
                            userMap.put("Messages/" + otherUserId + "/" + currentUserId + "/" + messageId, messageMap);

                            userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/message", "You have sent a voice message.");
                            userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/timestamp", ServerValue.TIMESTAMP);
                            userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/seen", ServerValue.TIMESTAMP);

                            userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/message", "Has send you a voice message.");
                            userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/timestamp", ServerValue.TIMESTAMP);
                            userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/seen", 0);

                            userMap.put("Notifications/" + otherUserId + "/" + notificationId, notificationData);

                            // update   ...  progression UI
                            progressStatus = progressStatus +1;
                            infos_progress_files.setText("Uploaded "+String.valueOf(progressStatus) +"/"+String.valueOf(number_of_files_to_send) + " files");
                            progress_bar.setProgress(progressStatus);

                            // stop the  disapear the animation  ...
                            if(progressStatus >= number_of_files_to_send){

                                infos_progress_layout.setVisibility(View.GONE);
                                progressStatus = 0;
                                number_of_files_to_send = 0;

                            }


                            FirebaseDatabase.getInstance().getReference().updateChildren(userMap, new DatabaseReference.CompletionListener()
                            {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                                {
                                    sendButton.setEnabled(true);

                                    FCM_Message_Sender.sendWithOtherThread("token" ,
                                            TokenFCM_OtherUser ,
                                            "voice",
                                            currentUserId,
                                            StaticConfigUser_fromFirebase.USER_NAME,
                                            StaticConfigUser_fromFirebase.USER_URL_IMAGE,
                                            getDateAndTime(),
                                            "room_disable",
                                            "voice message");


                                    if(databaseError != null)
                                    {
                                        Log.d(TAG, "sendMessage(): updateChildren failed: " + databaseError.getMessage());
                                    }else{
                                        //Toast.makeText(CreateGroupChatActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }catch (Exception ex){
                            ex.printStackTrace();

                        }
                    }
                });

            }
        });


        // Observe state change events such as progress, pause, and resume
        file.putFile(uri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");
                // Toasty.info(getApplicationContext(), "Upload is " + progress + "% done", Toast.LENGTH_SHORT).show();
                progress_bar.setProgress((int) Math.floor(progress + 0.5d));

            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                Toasty.info(getApplicationContext(), "upload is paused ! ", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void play_sound(){
        AudioManager audioManager =
                (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.playSoundEffect(SoundEffectConstants.CLICK);

        //playSoundEffect(SoundEffectConstants.CLICK);
    }

    private void doc_file__upload_images_to_firebase(Uri Url_media , final String ext , final String filename){

        Uri url = Url_media;

        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId).child(otherUserId).push();
        final String messageId = messageRef.getKey();

        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(otherUserId).push();
        final String notificationId = notificationRef.getKey();


        final StorageReference file = FirebaseStorage.getInstance().getReference().child("message_doc").child(messageId +"#"+filename+"."+ ext);
            // after that send video   ......
            // StorageReference file = FirebaseStorage.getInstance().getReference().child("message_doc").child(messageId +"#"+filename+"."+ ext);


        file.putFile(url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            try{
                                //Toast.makeText(create_post.this, "Send : " + uri.toString(), Toast.LENGTH_SHORT).show();
                                String imageUrl = uri.toString();

                                Map messageMap = new HashMap();
                                messageMap.put("message", imageUrl);
                                messageMap.put("type", "doc");
                                messageMap.put("from", currentUserId);
                                messageMap.put("to", otherUserId);
                                messageMap.put("filename", filename);
                                messageMap.put("timestamp", ServerValue.TIMESTAMP);

                                HashMap<String, String> notificationData = new HashMap<>();
                                notificationData.put("from", currentUserId);
                                notificationData.put("type", "message");

                                Map userMap = new HashMap();
                                userMap.put("Messages/" + currentUserId + "/" + otherUserId + "/" + messageId, messageMap);
                                userMap.put("Messages/" + otherUserId + "/" + currentUserId + "/" + messageId, messageMap);

                                userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/message", "You have sent a document.");
                                userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/timestamp", ServerValue.TIMESTAMP);
                                userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/seen", ServerValue.TIMESTAMP);

                                userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/message", "Has send you a document.");
                                userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/timestamp", ServerValue.TIMESTAMP);
                                userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/seen", 0);

                                userMap.put("Notifications/" + otherUserId + "/" + notificationId, notificationData);

                                play_sound();

                                // update   ...  progression UI
                                progressStatus = progressStatus +1;
                                infos_progress_files.setText("Uploaded "+String.valueOf(progressStatus) +"/"+String.valueOf(number_of_files_to_send) + " files");
                                progress_bar.setProgress(progressStatus);

                                // stop the  disapear the animation  ...
                                if(progressStatus >= number_of_files_to_send){

                                    infos_progress_layout.setVisibility(View.GONE);
                                    progressStatus = 0;
                                    number_of_files_to_send = 0;

                                }

                                FirebaseDatabase.getInstance().getReference().updateChildren(userMap, new DatabaseReference.CompletionListener()
                                {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                                    {
                                        sendButton.setEnabled(true);

                                        FCM_Message_Sender.sendWithOtherThread("token" ,
                                                TokenFCM_OtherUser ,
                                                "doc",
                                                currentUserId,
                                                StaticConfigUser_fromFirebase.USER_NAME,
                                                StaticConfigUser_fromFirebase.USER_URL_IMAGE,
                                                getDateAndTime(),
                                                "no room",
                                                "document message");

                                        if(databaseError != null)
                                        {

                                            Log.d(TAG, "sendMessage(): updateChildren failed: " + databaseError.getMessage());
                                            Toasty.info(GroupChatActivity.this.getApplicationContext() , databaseError.getMessage(), Toast.LENGTH_LONG).show();

                                            // after that send your notification   ...

                                        }else{
                                            // Toasty.info(CreateGroupChatActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });


                            }catch (Exception ex){
                                ex.printStackTrace();

                            }

                        }
                    });

                }
            });


    }

    public   void call_infos_notification(final Context context_call ,final  String CallType){


        // Pushing message/notification so we can get keyIds



        DatabaseReference Call_Room = FirebaseDatabase.getInstance().getReference().child("Call_room").child(currentUserId).child(otherUserId).push();
        pushId_callRoom = Call_Room.getKey();


        Map callroom_map = new HashMap();
        callroom_map.put("room_id", pushId_callRoom);
        callroom_map.put("id_caller", currentUserId);
        callroom_map.put("id_receiver", otherUserId);
        callroom_map.put("timestamp", ServerValue.TIMESTAMP);
        callroom_map.put("available_caller", true);
        callroom_map.put("available_receiver", false);
        callroom_map.put("room_status", true); //
        callroom_map.put("call_type", call_type);
        callroom_map.put("reason_interrupted_call", " -- ");
        // the end of call extremely important    ..


        Map callroom_map_messages = new HashMap();
        callroom_map_messages.put("Call_room//" + pushId_callRoom, callroom_map);


        // update call room id
        FirebaseDatabase.getInstance().getReference().updateChildren(callroom_map_messages, new DatabaseReference.CompletionListener()
        {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
            {

                if(databaseError != null)
                {
                    //Log.d("error", "sendMessage(): updateChildren failed: " + databaseError.getMessage());
                    Toasty.info(context_call, "sendMessage(): updateChildren failed: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    // Toast.makeText(CreateGroupChatActivity.this.getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    // we can  start the call intent  ....
                    // start call
                    // so now start the call
                    if (CallType.equalsIgnoreCase("video")){

                        // video call //

                        // start a call pushId_callRoom

                        callmelder_notification = true;  // send a notification for  to registread the call
                        call_type = "video";
                        Intent intentvideo = new Intent(GroupChatActivity.this.context, ConnectActivity.class);
                        intentvideo.putExtra("vid_or_aud", call_type);
                        intentvideo.putExtra("user_id", currentUserId);
                        intentvideo.putExtra("room_id", pushId_callRoom);
                        startActivity(intentvideo);

                    }else {
                        // audio call

                        callmelder_notification = true;  // send a notification for  to registread the call
                        // make web rtc call
                        call_type = "audio";
                        Intent intentaudio = new Intent(GroupChatActivity.this.context, ConnectActivity.class);
                        ConnectActivity.received_call = "caller";
                        intentaudio.putExtra("vid_or_aud", call_type);
                        intentaudio.putExtra("user_id", currentUserId);
                        startActivity(intentaudio);

                    }
                }
            }
        });


        // "Packing" message

        DatabaseReference userMessage = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId).child(otherUserId).push();
        String pushId = userMessage.getKey();

        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(otherUserId).push();
        String notificationId = notificationRef.getKey();

        String type = "call";
        String message = StaticConfigUser_fromFirebase.USER_NAME+" try to call you";
        Map messageMap = new HashMap();
        messageMap.put("message", message);
        messageMap.put("type", type);
        messageMap.put("from", currentUserId);
        messageMap.put("to", otherUserId);
        messageMap.put("timestamp", ServerValue.TIMESTAMP);
        messageMap.put("callId",pushId_callRoom);


        /*
        HashMap<String, String> notificationData = new HashMap<>();
        notificationData.put("from", currentUserId);
        notificationData.put("type", type);
        notificationData.put("callId", ConnectActivity.id_server_app_rtc);
        */

        Map userMap = new HashMap();
        userMap.put("Messages/" + currentUserId + "/" + otherUserId + "/" + pushId, messageMap);
        userMap.put("Messages/" + otherUserId + "/" + currentUserId + "/" + pushId, messageMap);

        userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/message", message);
        userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/timestamp", ServerValue.TIMESTAMP);
        userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/seen", ServerValue.TIMESTAMP);

        userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/message", message);
        userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/timestamp", ServerValue.TIMESTAMP);
        userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/seen", 0);

        //userMap.put("Notifications/" + otherUserId + "/" + notificationId, notificationData);

        // Updating database with the new data including message, chat and notification

        FirebaseDatabase.getInstance().getReference().updateChildren(userMap, new DatabaseReference.CompletionListener()
        {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
            {

                if(databaseError != null)
                {
                    Log.d("error", "sendMessage(): updateChildren failed: " + databaseError.getMessage());
                    // Toast.makeText(CreateGroupChatActivity.this.getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // send notification    ...

        FCM_Message_Sender.sendWithOtherThread("token" ,
                TokenFCM_OtherUser ,
                "call" ,
                currentUserId ,
                NameCurrentUser,
                url_Icon_currentUser,
                getDateAndTime(),
                pushId_callRoom,
                " missed call ");


        Map<String, Object> map = null;
        map = new HashMap<>();
        map.put("call_availability", false); // not available anymore to take another call
               //userDB.updateChildren(map);


        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {

                    Toasty.info(context_call, "Initialisation with GuDana Voice Cloud successful ", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toasty.error(context_call, "sorry GuDana Voice Cloud  is unreachable right now ! .... please try again later ", Toast.LENGTH_LONG).show();

                }
            }
        });




    }


    public static  void resetCallparameter(final Context context_call , final String Room_Id , String ClassName_func , String reason , int call_attribut){


        Toasty.info(context_call, "Call Reset Parameter  : " + ClassName_func, Toast.LENGTH_LONG).show();

        Map<String, Object> map = null;
        map = new HashMap<>();
        map.put("room_status", false); // that meant you are  at the moment online  .....
        map.put("reason_interrupted_call", reason);
        map.put("available_caller", false);
        map.put("available_receiver", false);


        //  a user or the both is not available anymore  ......

        //userDB.updateChildren(map);
        FirebaseDatabase.getInstance().getReference().child("Call_room").child(Room_Id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {

                if(task.isSuccessful())
                {

                    Toasty.info(context_call, "interrupted call ...", Toast.LENGTH_LONG).show();
                    // Call enabledb

                }
                else
                {

                    Toasty.error(context_call, "sorry GuDana Voice Cloud  is unreachable right now ! .... please try again later ", Toast.LENGTH_LONG).show();

                }

            }
        });


        // and Update Call History    ....

        Map callroom_map = new HashMap();
        callroom_map.put("room_id", pushId_callRoom);
        callroom_map.put("id_caller", currentUserId);
        callroom_map.put("id_receiver", otherUserId);
        callroom_map.put("timestamp", ServerValue.TIMESTAMP);
        callroom_map.put("available_caller", true);
        callroom_map.put("available_receiver", false);
        callroom_map.put("room_status", true); //
        callroom_map.put("call_type", call_type);
        callroom_map.put("call_duration", "1:62");
        callroom_map.put("call_attribut", call_attribut); // 0 = incomming Call ... 1= outgoing Call ... 2= missed Call
        callroom_map.put("reason_interrupted_call", " -- ");
        // the end of call extremely important    ..


        Map callroom_map_messages = new HashMap();
        callroom_map_messages.put("Call_room//" + pushId_callRoom, callroom_map);


        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("call_History").updateChildren(callroom_map_messages).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toasty.info(context_call, "call History updated ", Toast.LENGTH_LONG).show();
                    // start call
                    // so now start the call

                }
                else
                {
                    Toasty.error(context_call, "sorry GuDana Voice Cloud  is unreachable right now ! .... " +
                            "please check your internet connection or try again later ", Toast.LENGTH_LONG).show();

                }
            }
        });





    }

    public static  void missedCallNotification(final Context context_call , final String CallType , final String missedCallerId , final String Room_Id , String ClassName_func , final String reason){


        Toasty.info(context_call, "Call Reset Parameter  : " + ClassName_func, Toast.LENGTH_LONG).show();

        Map<String, Object> map = null;
        map = new HashMap<>();
        map.put("room_status", false); // that meant you are  at the moment online  .....
        map.put("reason_interrupted_call", reason);
        map.put("available_caller", false);
        map.put("available_receiver", false);


        //  a user or the both is not available anymore  ......

        //userDB.updateChildren(map);
        FirebaseDatabase.getInstance().getReference().child("Call_room").child(Room_Id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {

                if(task.isSuccessful())
                {

                    //Toasty.info(context_call, "interrupted call ...", Toast.LENGTH_LONG).show();
                    // Call enabledb

                }
                else
                {

                    //Toasty.error(context_call, "sorry GuDana Voice Cloud  is unreachable right now ! .... please try again later ", Toast.LENGTH_LONG).show();

                }

            }
        });


        // and Update Call History    ....

        Map callroom_map = new HashMap();
        callroom_map.put("room_id", Room_Id);
        callroom_map.put("id_caller", missedCallerId);
        callroom_map.put("id_receiver", FirebaseAuth.getInstance().getCurrentUser().getUid());
        callroom_map.put("timestamp", ServerValue.TIMESTAMP);
        callroom_map.put("available_caller", false);
        callroom_map.put("available_receiver", false);
        callroom_map.put("room_status", false); //
        callroom_map.put("call_type", CallType);
        callroom_map.put("call_duration", "1:62");
        callroom_map.put("call_attribut", "missed call"); // incomming Call ...outgoing Call ... missed Call
        callroom_map.put("reason_interrupted_call", reason);
        // the end of call extremely important    ..


        Map callroom_map_messages = new HashMap();
        callroom_map_messages.put("Call_room//" + pushId_callRoom, callroom_map);


        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("call_History").updateChildren(callroom_map_messages).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toasty.info(context_call, "call History updated ", Toast.LENGTH_LONG).show();
                    // start call
                    // so now start the call

                }
                else
                {
                    Toasty.error(context_call, "sorry GuDana Voice Cloud  is unreachable right now ! .... " +
                            "please check your internet connection or try again later ", Toast.LENGTH_LONG).show();

                }
            }
        });





    }


    private void initDatabases()
    {
        // Initialize/Update realtime other user data such as name and online status


        if(userDatabase != null && userListener != null)
        {
            userDatabase.removeEventListener(userListener);
        }


        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(otherUserId);
        userListener = new ValueEventListener()
        {
            Timer timer;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                try
                {
                    // get Image    to profil icon
                    final String Url_image = dataSnapshot.child("image").getValue().toString();
                    TokenFCM_OtherUser = dataSnapshot.child("token").getValue().toString();


                    if(!Url_image.equals("default"))
                    {
                        Picasso.with(context)
                                .load(Url_image)
                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
                                .centerCrop()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.user)
                                .into(messageImageRight, new Callback()
                                {
                                    @Override
                                    public void onSuccess()
                                    {

                                    }

                                    @Override
                                    public void onError()
                                    {
                                        Picasso.with(context)
                                                .load(Url_image)
                                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
                                                .centerCrop()
                                                .placeholder(R.drawable.user)
                                                .error(R.drawable.user)
                                                .into(messageImageRight);
                                    }
                                });
                    }
                    else
                    {
                        messageImageRight.setImageResource(R.drawable.user);
                    }
                    // get phone other users

                    OtherUserIdPhone = dataSnapshot.child("name").getValue().toString();

                    appBarName.setText(OtherUserIdPhone);

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
                            appBarSeen.setText("Seen: " + getTimeAgo(Long.parseLong(online)));
                        }
                        else
                        {
                            timer = new Timer();
                            timer.schedule(new TimerTask()
                            {
                                @Override
                                public void run()
                                {
                                    GroupChatActivity.this.runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            appBarSeen.setText("Seen: " + getTimeAgo(Long.parseLong(online)));
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

    private void GetInformation_from_Users(){

        userDB_current = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        // Set the  Driver Response to true ...
        //HashMap map = new HashMap();
        //map.put("Authentified" , "await");
        //userDB.updateChildren(map);
        userDB_current.keepSynced(true);
        userDB_current.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    try{
                        Map<String, Object> user_data = (Map<String, Object>) dataSnapshot.getValue();
                        // test if the recors Phone already exist  ...if not than
                        // than you are a new user   ...
                        NameCurrentUser   =  user_data.get("name").toString();
                        url_Icon_currentUser =  user_data.get("image").toString();



                    }catch(Exception ex){
                        Toasty.error(getApplicationContext(), ex.toString() , Toast.LENGTH_LONG).show();
                        ex.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toasty.error(getApplicationContext(),databaseError.toString(), Toast.LENGTH_LONG).show();

            }
        });
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

    private void sendMessage(final String type)
    {

        // thsi function ist to send  the text message  and location  message to firebase  and send the  notification
        sendButton.setEnabled(false);

        final String message = messageEditText.getText().toString();

        if(message.length() == 0)
        {
            Toasty.info(getApplicationContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();

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
            messageMap.put("type", type);
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

                    // send notification  for other User //...

                    FCM_Message_Sender.sendWithOtherThread("token" ,
                             TokenFCM_OtherUser ,
                             "Message",
                             currentUserId,
                             NameCurrentUser,
                             url_Icon_currentUser,
                             getDateAndTime(),
                            "room_disable",
                            message);

                    if(databaseError != null)
                    {
                        Log.d(TAG, "sendMessage(): updateChildren failed: " + databaseError.getMessage());
                    }
                }
            });
        }
    }


    private void sendMessage_location_contact(final String type , String message)
    {
        sendButton.setEnabled(false);
        if(message.length() == 0)
        {
            Toasty.info(getApplicationContext(), "invalid message !", Toast.LENGTH_SHORT).show();
            sendButton.setEnabled(true);
        }
        else
        {

            // Pushing message/notification so we can get keyIds

            DatabaseReference userMessage = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId).child(otherUserId).push();
            String pushId = userMessage.getKey();

            DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(otherUserId).push();
            String notificationId = notificationRef.getKey();

            // "Packing" message

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("type", type);
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

            play_sound();

            FirebaseDatabase.getInstance().getReference().updateChildren(userMap, new DatabaseReference.CompletionListener()
            {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                {
                    sendButton.setEnabled(true);
                    // send notification  ...

                    FCM_Message_Sender.sendWithOtherThread("token" ,
                            TokenFCM_OtherUser ,
                            type ,
                            currentUserId ,
                            StaticConfigUser_fromFirebase.USER_NAME,
                            StaticConfigUser_fromFirebase.USER_URL_IMAGE,
                            getDateAndTime(),
                            "no room",
                            " send you a media ");

                    if(databaseError != null)
                    {
                        Log.d(TAG, "sendMessage(): updateChildren failed: " + databaseError.getMessage());
                    }
                }
            });
        }
    }


    public static  String getDateAndTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println( sdf.format(cal.getTime()) );

        return  sdf.format(cal.getTime());
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

        new MultiContactPicker.Builder(GroupChatActivity.this) //Activity/fragment context
                // .theme(R.style.MyCustomPickerTheme) //Optional - default: MultiContactPicker.Azure
                .hideScrollbar(false) //Optional - default: false
                .showTrack(true) //Optional - default: true
                .searchIconColor(Color.WHITE) //Option - default: White
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                .handleColor(ContextCompat.getColor(GroupChatActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                .bubbleColor(ContextCompat.getColor(GroupChatActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                .bubbleTextColor(Color.WHITE) //Optional - default: White
                .showPickerForResult(CONTACT_PICKER_REQUEST);
    }

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
        Uri photoURI = FileProvider.getUriForFile(GroupChatActivity.this,
                BuildConfig.APPLICATION_ID + ".provider",
                filePathImageCamera);
        it.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
        startActivityForResult(it, IMAGE_CAMERA_REQUEST);
    }

    public void openVoiceRecorder(){
        //Implement voice selection
        mrecordVoiceButton =(ImageButton) findViewById(R.id.recordVoiceButton);
        // mRecordLable = (TextView) findViewById(R.id.recordLable);


        mrecordVoiceButton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                    Toasty.info(GroupChatActivity.this, "Start recording  ", Toast.LENGTH_LONG).show();
                    startRecording();
                    //mRecordLable.setText("Recording started...");
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP){

                    Toasty.info(GroupChatActivity.this, "Stop recording  ", Toast.LENGTH_LONG).show();

                    stopRecording();

                    //mRecordLable.setText("Recording stopped...");

                }
                return false;
            }
        });

        //on complete: sendVoice()
    }

    private void startRecording() {

        voice_recording_ui.setVisibility(View.VISIBLE);
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
        voice_recording_ui.setVisibility(View.GONE);
        // uploadAudio();
        voice_upload_images_to_firebase(Uri.fromFile(new File(mFileName)));
    }

}