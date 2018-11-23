package com.android.gudana.tindroid;

import android.Manifest;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.android.gudana.R;
import com.android.gudana.apprtc.CallFragment;
import com.android.gudana.apprtc.ConnectActivity;
import com.android.gudana.fcm.CustomFcm_Util;
import com.android.gudana.hify.ui.activities.MainActivity_GuDDana;
import com.android.gudana.hify.utils.database.UserHelper;
import com.android.gudana.tindroid.account.Utils;
import com.android.gudana.tindroid.media.VxCard;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nightonke.boommenu.BoomMenuButton;

import co.tinode.tinodesdk.ComTopic;
import co.tinode.tinodesdk.NotConnectedException;
import co.tinode.tinodesdk.PromisedReply;
import co.tinode.tinodesdk.Tinode;
import co.tinode.tinodesdk.model.Description;
import co.tinode.tinodesdk.model.MsgServerData;
import co.tinode.tinodesdk.model.MsgServerInfo;
import co.tinode.tinodesdk.model.MsgServerPres;
import co.tinode.tinodesdk.model.PrivateType;
import co.tinode.tinodesdk.model.ServerMessage;
import co.tinode.tinodesdk.model.Subscription;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import static com.android.gudana.tindroid.MessagesFragment.GetCorrespondantInformation_and_your_profile;

/**
 * View to display a single conversation
 */
public class MessageActivity extends AppCompatActivity {


    public  static String pushId_callRoom = "";
    private final String TAG = "CA/ChatActivity";

    // Will handle all changes happening in database

    private DatabaseReference userDatabase, chatDatabase;
    private ValueEventListener userListener, chatListener;

    // Will handle old/new messages between users

    private Query messagesDatabase;
    private ChildEventListener messagesListener;


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
    private CircleImageView messageImageRight = null;
    // Will be used on Notifications to detairminate if user has chat window open

    public static String  OtherUserIdPhone = "" , PhoneCorrespondant = "";
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
    private ImageView btEmoji;
    private EmojIconActions emojIcon;

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

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    // boom menu
    private BoomMenuButton bmb ;

    //File
    private File filePathImageCamera;

    String filePath = Environment.getExternalStorageDirectory() + "/recorded_audio.wav";
    //String filePaths_doc =  Environment.getExternalStorageDirectory().getPath();
    private ArrayList<String> docPaths = new ArrayList<>();
    private ArrayList<String> photoPaths = new ArrayList<>();
    private static final int REQUEST = 112;

    int requestCode = 0;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Context mContext=MessageActivity.this;
    private static final int EX_FILE_PICKER_RESULT = 55;


    public static  RelativeLayout data_processing = null;
    public static  String call_type = "video";
    // public static  boolean Call_dispo = true;
    public static  boolean callmelder_notification =  false;
    public static CustomFcm_Util FCM_Message_Sender ;
    private UserHelper userHelper;
    public  static String TokenFCM_OtherUser = "";

    // public static Context mContext;


    static final String FRAGMENT_MESSAGES = "msg";
    static final String FRAGMENT_INVALID ="invalid";
    static final String FRAGMENT_INFO = "info";
    static final String FRAGMENT_ADD_TOPIC = "add_topic";
    static final String FRAGMENT_EDIT_MEMBERS = "tin_edit_members";
    static final String FRAGMENT_VIEW_IMAGE ="view_image";

    // How long a typing indicator should play its animation, milliseconds.
    private static final int TYPING_INDICATOR_DURATION = 4000;
    private Timer mTypingAnimationTimer;

    private String mMessageText = null;

    private String mTopicName = null;
    private ComTopic<VxCard> mTopic = null;

    private PausableSingleThreadExecutor mMessageSender = null;

    private DownloadManager mDownloadMgr = null;
    private long mDownloadId = -1;
    public  static String NameUser = "";
    public static String otherUserId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tin_activity_messages);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFragmentVisible(FRAGMENT_EDIT_MEMBERS)) {
                    showFragment(FRAGMENT_INFO, false, null);
                } else if (!isFragmentVisible(FRAGMENT_MESSAGES) && !isFragmentVisible(FRAGMENT_INVALID)) {
                    showFragment(FRAGMENT_MESSAGES, false, null);
                } else {
                    Intent intent = new Intent(MessageActivity.this, MainActivity_GuDDana.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
            }
        });

        mDownloadMgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        registerReceiver(onNotificationClick, new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));
        mMessageSender = new PausableSingleThreadExecutor();
        mMessageSender.pause();


        try{

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            askPermission();

        }catch(Exception ex){
            ex.printStackTrace();
        }
        // appBarName.setText("GuDana User");
        // create  fcm Utill to send call  notification and  special notification   ... to other users
        FCM_Message_Sender = new CustomFcm_Util();

        // init Firebase users

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //otherUserId = getIntent().getStringExtra("userid");

        GetCorrespondantInformation_and_your_profile();

        // to avoid notification  when  the  message ist activated
       running = true;
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
                            Toasty.info(MessageActivity.this, "You have denied some permissions permanently, if the app force close try granting permission from settings.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();

    }


    @Override
    @SuppressWarnings("unchecked")
    public void onResume() {
        super.onResume();
        running = true;

        final Tinode tinode = Cache.getTinode();
        tinode.setListener(new MessageEventListener(tinode.isConnected()));

        final Intent intent = getIntent();

        // Check if the activity was launched by internally-generated intent.
        mTopicName = intent.getStringExtra("topic");

        if (TextUtils.isEmpty(mTopicName)) {
            // mTopicName is empty, so this is an external intent
            Uri contactUri = intent.getData();
            if (contactUri != null) {
                Cursor cursor = getContentResolver().query(contactUri,
                        new String[]{Utils.DATA_PID}, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        mTopicName = cursor.getString(cursor.getColumnIndex(Utils.DATA_PID));
                    }
                    cursor.close();
                }
            }
        }

        if (TextUtils.isEmpty(mTopicName)) {
            Log.e(TAG, "Activity resumed with an empty topic name");
            finish();
            return;
        }

        // Cancel all pending notifications addressed to the current topic
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancel(mTopicName, 0);
        }
        mMessageText = intent.getStringExtra(Intent.EXTRA_TEXT);

        // Get a known topic.
        mTopic = (ComTopic<VxCard>) tinode.getTopic(mTopicName);
        String IdUserfirebase = mTopic.getPub().fn;

        // split methode
        String[] parts = IdUserfirebase.split("#####");
        NameUser = parts[0].trim(); // 004
        otherUserId = parts[1].trim(); // 034556

        // get all information users
        GetCorrespondantInformation_and_your_profile();

        if (mTopic != null) {
            UiUtils.setupToolbar(this, mTopic.getPub(), mTopicName, mTopic.getOnline());
            showFragment(FRAGMENT_MESSAGES, false, null);
        } else {
            // New topic by name, either an actual grp* or p2p* topic name or a usr*
            Log.i(TAG, "Attempt to instantiate an unknown topic: " + mTopicName);
            UiUtils.setupToolbar(this, null, mTopicName, false);
            mTopic = (ComTopic<VxCard>) tinode.newTopic(mTopicName, null);
            showFragment(FRAGMENT_INVALID, false, null);
        }
        mTopic.setListener(new TListener());

        if (!mTopic.isAttached()) {
            topicAttach();
        } else {
            MessagesFragment fragmsg = (MessagesFragment) getSupportFragmentManager()
                    .findFragmentByTag(FRAGMENT_MESSAGES);
            fragmsg.topicSubscribed();
        }
    }



    @Override
    public void onPause() {
        super.onPause();
        mMessageSender.pause();

        // enable notification
        running = false;
        if (mTypingAnimationTimer != null) {
            mTypingAnimationTimer.cancel();
            mTypingAnimationTimer = null;
        }

        Cache.getTinode().setListener(null);
        if (mTopic != null) {
            mTopic.setListener(null);

            // Deactivate current topic
            if (mTopic.isAttached()) {
                try {
                    mTopic.leave();
                } catch (Exception ex) {
                    Log.e(TAG, "something went wrong in Topic.leave", ex);
                }
            }
        }
    }

    private void topicAttach() {

        try {
            setProgressIndicator(true);
            mTopic.subscribe(null,
                    mTopic.getMetaGetBuilder()
                            .withGetDesc()
                            .withGetSub()
                            .withGetData()
                            .withGetDel()
                            .build()).thenApply(new PromisedReply.SuccessListener<ServerMessage>() {
                @Override
                public PromisedReply<ServerMessage> onSuccess(ServerMessage result) {
                    UiUtils.setupToolbar(MessageActivity.this, mTopic.getPub(),
                            mTopicName, mTopic.getOnline());
                    showFragment(FRAGMENT_MESSAGES, false, null);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setProgressIndicator(false);
                            MessagesFragment fragmsg = (MessagesFragment) getSupportFragmentManager()
                                    .findFragmentByTag(FRAGMENT_MESSAGES);
                            fragmsg.topicSubscribed();
                        }
                    });
                    mMessageSender.resume();
                    // Submit pending messages for processing: publish queued, delete marked for deletion.
                    mMessageSender.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mTopic.syncAll();
                            } catch (Exception ignored) {
                            }
                        }
                    });
                    return null;
                }
            }, new PromisedReply.FailureListener<ServerMessage>() {
                @Override
                public PromisedReply<ServerMessage> onFailure(Exception err) {
                    setProgressIndicator(false);
                    showFragment(FRAGMENT_INVALID, false, null);
                    return null;
                }
            });
        } catch (NotConnectedException ignored) {
            Log.d(TAG, "Offline mode, ignore");
            setProgressIndicator(false);
        } catch (Exception ex) {
            setProgressIndicator(false);
            Toast.makeText(this, R.string.action_failed, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "something went wrong", ex);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        running = false;
        mMessageSender.shutdownNow();
        unregisterReceiver(onComplete);
        unregisterReceiver(onNotificationClick);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        UiUtils.setVisibleTopic(hasFocus ? mTopicName : null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mTopic == null || !mTopic.isValid()) {
            return false;
        }

        int id = item.getItemId();
        switch (id) {

            case android.R.id.home: {

                //MainActivity_with_Drawer.tabLayout.getTabAt(3);
                //MainActivity_with_Drawer.mViewPager.setCurrentItem(3);
                //play_sound();
                MessageActivity.this.finish();
                // NavUtils.navigateUpFromSameTask(this);
                break;
            }


            case R.id.action_view_contact: {
                showFragment(FRAGMENT_INFO, false, null);
                return true;
            }


            case R.id.action_topic_edit: {
                showFragment(FRAGMENT_ADD_TOPIC, false, null);
                return true;
            }

            case R.id.action_call_audio: {

                if (CallFragment.running == true) {
                    Toasty.warning(MessageActivity.this.getApplicationContext(), "you can not start  two calls at the same time  ! ", Toast.LENGTH_LONG).show();
                } else {
                    call_infos_notification(MessageActivity.this.context ,"audio");
                    // call Button

                }

                //Toasty.info(getApplicationContext(), R.string.not_imp6565lemented, Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.action_call_video: {

                if (CallFragment.running == true) {
                    Toasty.warning(MessageActivity.this.getApplicationContext(), "you can not start  two calls at the same time  ! ", Toast.LENGTH_LONG).show();
                } else {
                    call_infos_notification(MessageActivity.this.context ,"video");
                    // disable call Button  ...

                }

                break;
            }

            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }



    // call  methode  ...
    public   void call_infos_notification(final Context context_call ,final  String CallType){


        // Pushing message/notification so we can get keyIds

        DatabaseReference Call_Room = FirebaseDatabase.getInstance().getReference().child("Call_room").child(currentUserId).child(otherUserId).push();
        pushId_callRoom = Call_Room.getKey();
        //pushId_callRoom = "gudana"+currentUserId + Integer.toString(getRandomNumber());


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
                    Toasty.info(MessageActivity.this, "sendMessage(): updateChildren failed: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Intent intentvideo = new Intent(MessageActivity.this.getBaseContext(), ConnectActivity.class);
                        intentvideo.putExtra("vid_or_aud", call_type);
                        intentvideo.putExtra("user_id", currentUserId);
                        intentvideo.putExtra("room_id", pushId_callRoom);
                        startActivity(intentvideo);

                    }else {
                        // audio call

                        callmelder_notification = true;  // send a notification for  to registread the call
                        // make web rtc call
                        call_type = "audio";
                        Intent intentaudio = new Intent(MessageActivity.this.getBaseContext(), ConnectActivity.class);
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
        String message = " Xshaka try to call you";
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
                MessagesFragment.TokenFCM_OtherUser ,
                "call" ,
                currentUserId ,
                NameCurrentUser,
                url_Icon_currentUser,
                MessagesFragment.getDateAndTime(),
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

                   Toast.makeText(MessageActivity.this, "Initialisation with GuDana Voice Cloud successful ", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(MessageActivity.this, "sorry GuDana Voice Cloud  is unreachable right now ! .... please try again later ", Toast.LENGTH_LONG).show();

                }
            }
        });


    }


    public static  void resetCallparameter(final Context context_call ,
                                           final String Room_Id ,
                                           String ClassName_func ,
                                           String reason ,
                                           int call_attribut,
                                           final String missedCallerId
                                           ){

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
        callroom_map.put("room_id", Room_Id);
        callroom_map.put("id_caller", FirebaseAuth.getInstance().getCurrentUser().getUid());
        callroom_map.put("id_receiver", missedCallerId);
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
        callroom_map_messages.put("Call_room//" + Room_Id, callroom_map);


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

        // update call availability
        Map<String, Object> map_dispo = null;
        map_dispo = new HashMap<>();
        map_dispo.put("call_availability", true); // not available anymore to take another call
        //userDB.updateChildren(map);


        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).updateChildren(map_dispo).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {

                    Toast.makeText(context_call, "user available to take amother call  ", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(context_call, "sorry GuDana Voice Cloud  is unreachable right now ! ... ", Toast.LENGTH_LONG).show();

                }
            }
        });


    }

    // randomnumber
    // generate random number
    private int getRandomNumber(){
        Random rand = new Random();
        int value = rand.nextInt(100000);
        return  value;
    }

    public static  void missedCallNotification(final Context context_call ,
                                               final String CallType ,
                                               final String missedCallerId ,
                                               final String Room_Id ,
                                               String ClassName_func ,
                                               final String reason){


        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
        callroom_map.put("room_id", Room_Id);
        callroom_map.put("id_caller", missedCallerId);
        callroom_map.put("id_receiver", FirebaseAuth.getInstance().getCurrentUser().getUid());
        callroom_map.put("timestamp", ServerValue.TIMESTAMP);
        callroom_map.put("available_caller", false);
        callroom_map.put("available_receiver", false);
        callroom_map.put("room_status", false); //
        callroom_map.put("call_type", CallType);
        callroom_map.put("call_duration", "1:62");
        callroom_map.put("call_attribut", 0); // incomming Call ...outgoing Call ... missed Call
        callroom_map.put("reason_interrupted_call", reason);
        // the end of call extremely important    ..



        Map callroom_map_messages = new HashMap();
        callroom_map_messages.put("Call_room//" + Room_Id, callroom_map);



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


        // update call availability
        Map<String, Object> map_dispo = null;
        map_dispo = new HashMap<>();
        map_dispo.put("call_availability", true); // not available anymore to take another call
        //userDB.updateChildren(map);


        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).updateChildren(map_dispo).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {

                    Toast.makeText(context_call, "user available to take amother call  ", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(context_call, "sorry GuDana Voice Cloud  is unreachable right now ! ... ", Toast.LENGTH_LONG).show();

                }
            }
        });

    }


    private boolean isFragmentVisible(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        return fragment != null && fragment.isVisible();
    }

    void showFragment(String tag, boolean addToBackstack, Bundle args) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment == null) {
            switch (tag) {
                case FRAGMENT_MESSAGES:
                    fragment = new MessagesFragment();
                    break;
                case FRAGMENT_INFO:
                    fragment = new TopicInfoFragment();
                    break;
                case FRAGMENT_ADD_TOPIC:
                    fragment = new CreateGroupFragment();
                    break;
                case FRAGMENT_EDIT_MEMBERS:
                    fragment = new EditMembersFragment();
                    break;
                case FRAGMENT_VIEW_IMAGE:
                    fragment = new ImageViewFragment();
                    break;
                case FRAGMENT_INVALID:
                    fragment = new InvalidTopicFragment();
                    break;
            }
        }
        if (fragment == null) {
            throw new NullPointerException();
        }

        args = args != null ? args : new Bundle();
        args.putString("topic", mTopicName);
        args.putString("messageText", mMessageText);
        if (fragment.getArguments() != null) {
            fragment.getArguments().putAll(args);
        } else {
            fragment.setArguments(args);
        }

        if (!fragment.isVisible()) {
            FragmentTransaction trx = fm.beginTransaction();
            trx.replace(R.id.contentFragment, fragment, tag);
            if (addToBackstack) {
                trx.addToBackStack(tag);
            }
            trx.commit();
        }
    }

    public void sendKeyPress() {
        if (mTopic != null) {
            mTopic.noteKeyPress();
        }
    }

    public void submitForExecution(Runnable runnable) {
        mMessageSender.submit(runnable);
    }

    public void startDownload(Uri uri, String fname, String mime, Map<String,String> headers) {
        Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .mkdirs();

        DownloadManager.Request req = new DownloadManager.Request(uri);
        if (headers != null) {
            for (Map.Entry<String,String> entry : headers.entrySet()) {
                req.addRequestHeader(entry.getKey(), entry.getValue());
            }
        }

        mDownloadId = mDownloadMgr.enqueue(
                req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                        DownloadManager.Request.NETWORK_MOBILE)
                .setMimeType(mime)
                .setAllowedOverRoaming(false)
                .setTitle(fname)
                .setDescription(getString(R.string.download_title))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setVisibleInDownloadsUi(true)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fname));
    }

    /**
     * Show progress indicator based on current status
     * @param active should be true to show progress indicator
     */
    public void setProgressIndicator(final boolean active) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MessagesFragment fragMsg = (MessagesFragment) getSupportFragmentManager()
                        .findFragmentByTag(FRAGMENT_MESSAGES);
                if (fragMsg != null) {
                    fragMsg.setProgressIndicator(active);
                }
            }
        });
    }

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctx, Intent intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction()) &&
                intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0) == mDownloadId) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(mDownloadId);
                Cursor c = mDownloadMgr.query(query);
                if (c.moveToFirst()) {
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                        URI fileUri = URI.create(c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
                        String mimeType = c.getString(c.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
                        intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(FileProvider.getUriForFile(MessageActivity.this,
                                "com.android.gudana.provider", new File(fileUri)), mimeType);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException ignored) {
                            startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                        }
                    }
                }
                c.close();
            }
        }
    };

    BroadcastReceiver onNotificationClick=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Log.d(TAG, "onNotificationClick" + intent.getExtras());
        }
    };

    private class TListener extends ComTopic.ComListener<VxCard> {

        TListener() {
        }

        @Override
        public void onSubscribe(int code, String text) {
            // Topic name may change after subscription, i.e. new -> grpXXX
            mTopicName = mTopic.getName();
        }

        @Override
        public void onData(MsgServerData data) {
            final MessagesFragment fragment = (MessagesFragment) getSupportFragmentManager().
                    findFragmentByTag(FRAGMENT_MESSAGES);
            if (fragment != null && fragment.isVisible()) {

                // play some music here to tell to other user that a new message are availabale ...

                fragment.Play_Song_in_message();
                fragment.runMessagesLoader();
            }
        }

        @Override
        public void onPres(MsgServerPres pres) {
            Log.d(TAG, "Topic '" + mTopicName + "' onPres what='" + pres.what + "'");
        }

        @Override
        public void onInfo(MsgServerInfo info) {
            switch (info.what) {
                case "read":
                case "recv":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MessagesFragment fragment = (MessagesFragment) getSupportFragmentManager().
                                    findFragmentByTag(FRAGMENT_MESSAGES);
                            if (fragment != null && fragment.isVisible()) {
                                fragment.notifyDataSetChanged();
                            }
                        }
                    });
                    break;
                case "kp":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Show typing indicator as animation over avatar in tin_toolbar
                            mTypingAnimationTimer = UiUtils.toolbarTypingIndicator(MessageActivity.this,
                                    mTypingAnimationTimer, TYPING_INDICATOR_DURATION);
                        }
                    });
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onSubsUpdated() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TopicInfoFragment fragment = (TopicInfoFragment) getSupportFragmentManager().
                            findFragmentByTag(FRAGMENT_INFO);

                    if (fragment != null && fragment.isVisible()) {
                        fragment.notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public void onMetaDesc(final Description<VxCard,PrivateType> desc) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UiUtils.setupToolbar(MessageActivity.this, mTopic.getPub(), mTopic.getName(),
                            mTopic.getOnline());

                    TopicInfoFragment fragment = (TopicInfoFragment) getSupportFragmentManager().
                            findFragmentByTag(FRAGMENT_INFO);
                    if (fragment != null && fragment.isVisible()) {
                        fragment.notifyContentChanged();
                    }
                }
            });
        }

        @Override
        public void onContUpdate(final Subscription<VxCard,PrivateType> sub) {
            onMetaDesc(null);
        }

        @Override
        public void onOnline(final boolean online) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UiUtils.toolbarSetOnline(MessageActivity.this, mTopic.getOnline());
                }
            });

        }
    }

    /**
     * Utility class to send messages queued while offline.
     * The execution is paused while the activity is in background and unpaused
     * when the topic subscription is live.
     */
    private static class PausableSingleThreadExecutor extends ThreadPoolExecutor {
        private boolean isPaused;
        private ReentrantLock pauseLock = new ReentrantLock();
        private Condition unpaused = pauseLock.newCondition();

        public PausableSingleThreadExecutor() {
            super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        }

        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
            pauseLock.lock();
            try {
                while (isPaused) unpaused.await();
            } catch (InterruptedException ie) {
                t.interrupt();
            } finally {
                pauseLock.unlock();
            }
        }

        public void pause() {
            pauseLock.lock();
            try {
                isPaused = true;
            } finally {
                pauseLock.unlock();
            }
        }

        public void resume() {
            pauseLock.lock();
            try {
                isPaused = false;
                unpaused.signalAll();
            } finally {
                pauseLock.unlock();
            }
        }
    }

    private class MessageEventListener extends UiUtils.EventListener {
        MessageEventListener(boolean online) {
            super(MessageActivity.this, online);
        }

        @Override
        public void onLogin(int code, String txt) {
            super.onLogin(code, txt);
            topicAttach();
        }
    }
}
