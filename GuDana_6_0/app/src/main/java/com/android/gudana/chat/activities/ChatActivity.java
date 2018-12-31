package com.android.gudana.chat.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.MoD.MoD_Live_Location_sharing_Activity;
import com.android.gudana.R;
import com.android.gudana.apprtc.CallFragment;
import com.android.gudana.apprtc.ConnectActivity;
import com.android.gudana.chat.ChatApplication;
import com.android.gudana.chat.adapters.MessageAdapter;
import com.android.gudana.chat.model.User;
import com.android.gudana.chat.model.message;
import com.android.gudana.chat.model.user_room_message_db;
import com.android.gudana.chatapp.models.Message;
import com.android.gudana.chatapp.models.StaticConfigUser_fromFirebase;
import com.android.gudana.chatapp.utils_v2.FileOpen;
import com.android.gudana.fcm.CustomFcm_Util;
import com.android.gudana.hify.utils.AndroidMultiPartEntity;
import com.android.gudana.hify.utils.Config;
import com.android.gudana.hify.utils.JSONParser;
import com.android.gudana.hify.utils.database.UserHelper;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
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
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;
import com.nightonke.boommenu.BoomMenuButton;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import es.dmoral.toasty.Toasty;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import org.json.JSONArray;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Random;

import static io.opencensus.tags.TagValue.MAX_LENGTH;


public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "activities/ChatActivity";
    public static final int CodeLiveLocation = 500;

    public static String splitter_pattern_message = "#568790%%%7689%&&57#";
    public static  String Type_Text = "text";
    public static  String Type_image = "images";
    public static  String Type_voice = "voice";
    public static String Type_Doc  = "doc";
    public static String Type_map = "map";
    public static  String Type_live_location = "live_loc";
    public static String  stype_of_message = Type_Text;

    // ############### import from  old Chat  #######################

    public  static String pushId_callRoom = "";

    // Will handle all changes happening in database

    private DatabaseReference userDatabase, chatDatabase;
    private ValueEventListener userListener, chatListener;

    // Will handle old/new messages between users

    private Query messagesDatabase;
    private ChildEventListener messagesListener;

    private final List<Message> messagesList = new ArrayList<>();

    // User data
    public static  String currentUserId;
    public static  String NameCurrentUser = "";
    public static  String url_Icon_currentUser = "";
    // ca_activity_chat views

    private EmojiconEditText messageEditText;
    private ListView  listView_messages;
    private ImageView sendPictureButton;
    // ca_chat_bar views

    private TextView appBarName, appBarSeen;
    private CircleImageView messageImageRight = null;
    // Will be used on Notifications to detairminate if user has chat window open

    public static String  OtherUserIdPhone = "" , PhoneCorrespondant = "";
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
    public static Chronometer record_time_voice ;


    //Views UI
    private ImageView btEmoji;
    private EmojIconActions emojIcon;

    private int progressStatus = 0;
    private int number_of_files_to_send = 0;
    public static  Handler handler = new Handler();
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
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Context mContext=ChatActivity.this;
    private static final int EX_FILE_PICKER_RESULT = 55;


    public static  RelativeLayout data_processing = null;
    public static  String call_type = "video";
    // public static  boolean Call_dispo = true;
    public static  boolean callmelder_notification =  false;
    public static CustomFcm_Util FCM_Message_Sender ;
    private UserHelper userHelper;
    public  static String TokenFCM_OtherUser = "";
    public static  ImageButton Startposition_Custom_dialog;
    private ImageView menudialog;

    // ############ End  import from  end import #######################

    public static final int ROOM = 0, FRIEND = 1;
    private ListView listViewMessages;
    private MessageAdapter adapter;
    private LinearLayout footerView;
    private TextView usersTypingTextView;
    private TextView isTypingTextView;
    private boolean typing = false;
    private boolean first_history = true;
    private boolean first_message_history_lock = false;

    private final ArrayList<String> usersTyping = new ArrayList<>();
    private Resources res;

    private JSONObject info;
    private Socket mSocket;

    // This array keeps track of which list items have not been
    // sent to the server.
    private ArrayList<Integer> not_on_server_indices = new ArrayList<>();

    int type;

    int user_id;
    String username;
    private   Button btnSend;

    private String room_name ;
    int room_id ;

    HashMap<String, Emitter.Listener> eventListeners = new HashMap<>();

    private user_room_message_db message_db;
    private String room_uid = "";
    private String token_id, image_url;

    public static void startActivity(Context context){
        Intent intent = new Intent(context,ChatActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


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
            if (!permissionToRecordAccepted ) ChatActivity.super.finish();
            if (!permissionToWriteAccepted ) ChatActivity.super.finish();


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_chat);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        res = getResources();

        Intent intent = getIntent();
        ChatApplication chatApplication = (ChatApplication) this.getApplication();
        User user = chatApplication.getUser();
        user_id = user.getUserID();
        username = user.getUsername();
        // TODO: deal with default value
        type = intent.getIntExtra("type", -1);

        // will only be used when type == ROOM
        room_name = intent.getStringExtra("room_name");
        room_id = intent.getIntExtra("room_id", -1);
        room_uid = intent.getStringExtra("room_uid");
        TokenFCM_OtherUser = token_id = intent.getStringExtra("token_id");
        image_url = intent.getStringExtra("image_url");
        Config.Chat_Activity_otherUserId = getIntent().getStringExtra("userid");



        // will only be used when type == FRIEND
        String friend_username = intent.getStringExtra("friend_username");
        int friend_user_id = intent.getIntExtra("friend_user_id", -1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(room_name);

        // init sqlite helper
        message_db = new user_room_message_db(ChatActivity.this);

        /*
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            // Remove "Chat" placeholder
            actionBar.setTitle(room_name);

            actionBar.setDisplayShowCustomEnabled(true);
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.chat_actionbar_chat, null);

            if(type == ROOM) {
                ((TextView) v.findViewById(R.id.textView_actionBar_title)).setText(room_name);
                ((ImageView) v.findViewById(R.id.imageView_actionBar_icon)).setImageResource(R.mipmap.ic_room_white);
            } else if(type == FRIEND) {
                ((TextView) v.findViewById(R.id.textView_actionBar_title)).setText(friend_username);
                ((ImageView) v.findViewById(R.id.imageView_actionBar_icon)).setImageResource(R.mipmap.ic_user_white);
            }

            actionBar.setCustomView(v);
        }

        */


        try {
            mSocket = IO.socket(((ChatApplication) getApplication()).getURL());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

        mSocket.connect();


        info = user.serializeToJSON();

            info.put("type", type);

            if(type == ROOM) {
                info.put("room_name", room_name);
                info.put("room_id", room_id);
            } else if(type == FRIEND) {
                Log.i(TAG, "friend_username=" + friend_username);
                info.put("friend_username", friend_username);
                info.put("friend_user_id", friend_user_id);
            }


        try{

            eventListeners.put("received message", onMessageReceive);
            eventListeners.put("broadcast", onBroadcast);
            //eventListeners.put("history", onHistory);
            eventListeners.put("typing", onTyping);
            eventListeners.put("stop typing", onStopTyping);
            setListeningToEvents(true);


        }catch(Exception ex){
            ex.printStackTrace();
        }

        mSocket.emit("join", info);
       // mSocket.emit("fetch messages", info);
        first_message_history_lock = true;

        listViewMessages = (ListView) findViewById(R.id.listView_messages);
        messageEditText = (EmojiconEditText) findViewById(R.id.txt_message);

        footerView = (LinearLayout) findViewById(R.id.layout_typing);
        footerView.setVisibility(View.GONE);

        usersTypingTextView = (TextView) footerView.findViewById(R.id.users_typing);
        isTypingTextView = (TextView) footerView.findViewById(R.id.is_typing);

        adapter = new MessageAdapter(ChatActivity.this, username);

        listViewMessages.setAdapter(adapter);

        listViewMessages.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (adapter.getCount() == 0) {
                    return;
                }

                if (firstVisibleItem == 0) {
                    // check if we reached the top or bottom of the list
                    View v = listViewMessages.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset < 2) {
                        // reached the top:
                        Log.d("ChatActivity", "first_message_history_lock=" + first_message_history_lock);
                        if (!first_message_history_lock) {
                            try {
                                JSONObject json = new JSONObject(info.toString());
                                if(adapter.getCount() > 0) {
                                    json.put("before_msg_id", adapter.getFirstID());
                                    json.put("after_msg_id", adapter.getLastID());
                                }

                                // get message  on local datasore  sqlite  ...
                                //mSocket.emit("fetch messages", json);
                                first_message_history_lock = true;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });


        } catch (Exception e) {
            e.printStackTrace();
        }

        btnSend = (Button) findViewById(R.id.btn_send);

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String message = messageEditText.getText().toString();
                btnSend.setEnabled(!message.isEmpty());

                if (!message.isEmpty() && !typing) {
                    typing = true;
                    mSocket.emit("typing", info);
                } else if (message.isEmpty() && typing) {
                    typing = false;
                    mSocket.emit("stop typing", info);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String msg = messageEditText.getText().toString();
                messageEditText.setText("");

                // Don't send message if string is empty
                if (!msg.isEmpty()) {
                    // define type of message ... extrem important
                    stype_of_message = Type_Text;
                    try {
                        System.out.println("Send Notification");
                        FCM_Message_Sender.sendWithOtherThread("token",
                                TokenFCM_OtherUser,
                                "Friend Request",
                                FirebaseAuth.getInstance().getUid(),
                                room_name,
                                image_url,
                                getDateAndTime(),
                                "room_disable",
                                msg.trim());

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    new SendMessageTask(msg.trim()).execute();
                    // send notification parraleli

                }
            }
        });

         // add import old Chat  #############################
        ListView listView_messages = findViewById(R.id.listView_messages);


        Config.Chat_Activity_running = true;
        //ViCall = new LinphoneManager(CreateGroupChatActivity.this.getApplicationContext());
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }


        // messageEditText = findViewById(R.id.chat_message);


        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get the widgets reference from XML layout
        infos_progress_layout = (RelativeLayout) findViewById(R.id.infos_progress);
        infos_progress_layout.setVisibility(View.GONE);
        infos_progress_files = (TextView) findViewById(R.id.infos_uploading_);
        infos_progress_stop = (TextView) findViewById(R.id.textViewToday);
        progress_bar =(ProgressBar) findViewById(R.id.progressbar_timerview);
        progress_bar.setMax(100);
        // progress_bar.setMax(200);



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


        menudialog = (ImageView)findViewById(R.id.bmb);
        menudialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiag_media_menu(Startposition_Custom_dialog);
            }
        });


        final CoordinatorLayout root = findViewById(R.id.chat_root);
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            int previousHeight = root.getRootView().getHeight() - root.getHeight() - listViewMessages.getHeight();

            @Override
            public void onGlobalLayout()
            {
                int height = root.getRootView().getHeight() - root.getHeight() - listViewMessages.getHeight();

                if(previousHeight != height)
                {
                    if(previousHeight > height)
                    {
                        previousHeight = height;
                    }
                    else if(previousHeight < height)
                    {
                        listViewMessages.scrollListBy(listViewMessages.getHeight() - 1);

                        previousHeight = height;
                    }
                }
            }
        });

        btEmoji = (ImageView)findViewById(R.id.buttonEmoji);
        emojIcon = new EmojIconActions(this,root, messageEditText,btEmoji);
        emojIcon.ShowEmojIcon();


        //Check Permissions at runtime
        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }

        context = ChatActivity.this.getApplicationContext();
        //initializeScreen();
        // mToolBar.setTitle(chatName);
        //showMessages();
        //addListeners();

        try{

            openVoiceRecorder();
            // hide Keyboard
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

            askPermission();


            // get User Imformation from remote Database or from local Cache ...
            GetInformation_from_Users();

        }catch(Exception ex){
            ex.printStackTrace();
        }
        // appBarName.setText("GuDana User");
        // create  fcm Utill to send call  notification and  special notification   ... to other users
        FCM_Message_Sender = new CustomFcm_Util();


        // enable offline capaibilities
        // get All message
        getAllmessage();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
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
                    call_infos_notification(ChatActivity.this.context ,"audio");
                    // call Button

                }

                //Toasty.info(getApplicationContext(), R.string.not_imp6565lemented, Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_call_video:


                if(CallFragment.running == true ){
                    Toasty.warning(context, "you can not start  two calls at the same time  ! ", Toast.LENGTH_LONG).show();

                }else {
                    call_infos_notification(ChatActivity.this.context ,"video");
                    // disable call Button  ...

                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        try{


            mSocket.emit("leave", info);
            mSocket.disconnect();
            setListeningToEvents(false);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        Log.i(TAG, "Destroying...");

        super.onDestroy();
    }

    private void setListeningToEvents(boolean start_listening) {

        try{

            for(Map.Entry eventListener: eventListeners.entrySet()) {
                if(start_listening) {
                    mSocket.on((String) eventListener.getKey(), (Emitter.Listener) eventListener.getValue());
                } else {
                    mSocket.off((String) eventListener.getKey(), (Emitter.Listener) eventListener.getValue());
                }
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private final Emitter.Listener onMessageReceive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject json;
            final int msg_user_id, message_id;
            final String msg_username, message_contents, datetimeutc;
            try {
                json = (JSONObject) args[0];

                msg_user_id = json.getInt("user_id");
                message_id = json.getInt("message_id");
                msg_username = json.getString("username");
                message_contents = json.getString("message");
                datetimeutc = json.getString("datetimeutc");

                // save message  on local  database  for offline use  ...
                new Save_offline_MessageTask (json).execute();

                // test offline  use ...must be removed     immediatly after test purpose
                new Save_offline_MessageTask (json).execute();
                new Save_offline_MessageTask (json).execute();
                new Save_offline_MessageTask (json).execute();
                // end test


                if(user_id == msg_user_id && not_on_server_indices.size() > 0) {
                    /** TODO: remove assumption that messages are received in order
                      * Proper way is to sort messages by their IDs in ascending order
                      **/

                    ChatActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MessageAdapter.MessageItem msg_item = (MessageAdapter.MessageItem) adapter.getItem(not_on_server_indices.get(0));
                            msg_item.savedToServer(message_id, datetimeutc);

                            // In case there are messages that were sent to server before this one,
                            // move it to the end of the list.
                            // adapter.moveItemToEndOfList(not_on_server_indices.get(0));

                            listViewMessages.setAdapter(listViewMessages.getAdapter());
                            not_on_server_indices.remove(0);
                        }
                    });
                } else {
                    final MessageAdapter.MessageItem msgItem = new MessageAdapter.MessageItem(message_id, user_id, msg_username, message_contents, datetimeutc);

                    ChatActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.addItem(msgItem);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void getAllmessage(){

        int Numb_Event = 0;
        Numb_Event = message_db.getNumberOfmessage();
        Cursor rs = message_db.getData_message(Numb_Event);
        rs.moveToFirst();

        if(Numb_Event > 0){


            for(int i=0; i<Numb_Event; i++){
                String room_uid =rs.getString(rs.getColumnIndex(user_room_message_db.ROOM_UID));
                String content = rs.getString(rs.getColumnIndex(user_room_message_db.CONTENT));
                String room_id = rs.getString(rs.getColumnIndex(user_room_message_db.ROOM_ID));
                System.out.println("The value of message  is: "+content);
                //rs.moveToNext();
                //Toasty.info(context,"Live Location : "+Integer.toString(i)+"  : "+room_uid +"  "+ content+"   "+room_id , Toast.LENGTH_SHORT).show();
            }


            // get all  Raw from Table   ...
            //Cursor  cursor = live_location.rawQuery("select * from table",null);
            List<message> data = message_db.getAllMessage();
            for(message msg : data){
                System.out.println("message on local db  :"+msg);

                try {
                    JSONObject jsonObj = new JSONObject(msg.getCONTENT());
                    parse_offline_message(jsonObj);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // System.out.println(" #### ");
            }
            rs.close();

        }else{
            // keine  Event registered ..

        }


    }


    public void parse_offline_message(JSONObject json) {
        final int msg_user_id, message_id;
        final String msg_username, message_contents, datetimeutc;
        try {
            //json = (JSONObject) args[0];

            msg_user_id = json.getInt("user_id");
            message_id = json.getInt("message_id");
            msg_username = json.getString("username");
            message_contents = json.getString("message");
            datetimeutc = json.getString("datetimeutc");

            // save message  on local  database  for offline use  ...
            //new Save_offline_MessageTask (json).execute();

            if(user_id == msg_user_id && not_on_server_indices.size() > 0) {
                /** TODO: remove assumption that messages are received in order
                 * Proper way is to sort messages by their IDs in ascending order
                 **/

                ChatActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MessageAdapter.MessageItem msg_item = (MessageAdapter.MessageItem) adapter.getItem(not_on_server_indices.get(0));
                        msg_item.savedToServer(message_id, datetimeutc);

                        // In case there are messages that were sent to server before this one,
                        // move it to the end of the list.
                        // adapter.moveItemToEndOfList(not_on_server_indices.get(0));

                        listViewMessages.setAdapter(listViewMessages.getAdapter());
                        not_on_server_indices.remove(0);
                    }
                });
            } else {
                final MessageAdapter.MessageItem msgItem = new MessageAdapter.MessageItem(message_id, user_id, msg_username, message_contents, datetimeutc);

                ChatActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addItem(msgItem);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            usersTyping.add((String) args[0]);

            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try{
                        String usersTypingStr = usersTyping.toString();
                        usersTypingTextView.setText(usersTypingStr.substring(1, usersTypingStr.length() - 1));

                        if (usersTyping.size() == 1) {
                            // show view
                            footerView.setVisibility(View.VISIBLE);
                            isTypingTextView.setText(res.getString(R.string.is_typing));
                        }

                        if (usersTyping.size() == 2) {
                            isTypingTextView.setText(res.getString(R.string.are_typing));
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }


                }
            });
        }
    };

    private final Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            usersTyping.remove((String) args[0]);

            if (usersTyping.isEmpty()) {
                ChatActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String usersTypingStr = usersTyping.toString();
                        try{

                            usersTypingTextView.setText(usersTypingStr.substring(1, usersTypingStr.length() - 1));

                            if (usersTyping.size() == 1) {
                                isTypingTextView.setText(res.getString(R.string.is_typing));
                            }

                            if (usersTyping.isEmpty()) {
                                // hide view
                                footerView.setVisibility(View.GONE);
                            }
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                });
            }
        }
    };

    private final Emitter.Listener onBroadcast = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final MessageAdapter.BroadcastItem broadcastItem = new MessageAdapter.BroadcastItem((String) args[0]);

            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.addItem(broadcastItem);
                }
            });
        }
    };

    private final Emitter.Listener onHistory = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject json;
            JSONArray arr;

            Log.i("ChatActivity", "receiving message history");

            final ArrayList<Object> items_before = new ArrayList<>();
            final ArrayList<Object> items_after = new ArrayList<>();

            try {
                json = (JSONObject) args[0];
                arr = json.getJSONArray("messages");
                JSONObject jsonObject;
                int messageID;

                for (int i = 0; i < arr.length(); i++) {
                    jsonObject = arr.getJSONObject(i);

                    MessageAdapter.MessageItem messageItem = new MessageAdapter.MessageItem(
                            jsonObject.getInt("message_id"),
                            jsonObject.getInt("user_id"),
                            jsonObject.getString("username"),
                            jsonObject.getString("message"),
                            jsonObject.getString("datetimeutc")
                    );

                    messageID = jsonObject.getInt("message_id");

                    if(adapter.getCount() > 0) {
                        if (messageID < adapter.getFirstID()) {
                            items_before.add(messageItem);
                        } else if (messageID > adapter.getLastID()) {
                            items_after.add(messageItem);
                        }
                    } else {
                        items_before.add(messageItem);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try{

                        if(first_history) {
                            adapter.prependItems(items_before);
                            first_history = false;
                        } else {
                            // https://stackoverflow.com/questions/22051556/maintain-scroll-position-when-adding-to-listview-with-reverse-endless-scrolling
                            // https://stackoverflow.com/questions/8276128/retaining-position-in-listview-after-calling-notifydatasetchanged
                            // save index and top position
                            int index = listViewMessages.getFirstVisiblePosition();
                            View v = listViewMessages.getChildAt(0);
                            int top = (v == null) ? 0 : v.getTop();
                            int oldCount = adapter.getCount();

                            // notify dataset changed or re-assign adapter here
                            adapter.prependItems(items_before);
                            adapter.addItems(items_after);

                            // restore the position of listview
                            listViewMessages.setSelectionFromTop(index + adapter.getCount() - oldCount, top);
                        }

                        // if we haven't reached the start of the messages, release first message history lock
                        if (items_before.size() > 0) first_message_history_lock = false;


                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                }
            });
        }
    };



    public   void call_infos_notification(final Context context_call ,final  String CallType){


        // Pushing message/notification so we can get keyIds
        DatabaseReference Call_Room = FirebaseDatabase.getInstance().getReference().child("Call_room").child(currentUserId).child(Config.Chat_Activity_otherUserId).push();
        pushId_callRoom = Call_Room.getKey();

        Map callroom_map = new HashMap();
        callroom_map.put("room_id", pushId_callRoom);
        callroom_map.put("id_caller", currentUserId);
        callroom_map.put("id_receiver", Config.Chat_Activity_otherUserId);
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
                        Intent intentvideo = new Intent(ChatActivity.this.context, ConnectActivity.class);
                        intentvideo.putExtra("vid_or_aud", call_type);
                        intentvideo.putExtra("user_id", currentUserId);
                        intentvideo.putExtra("room_id", pushId_callRoom);
                        startActivity(intentvideo);

                    }else {
                        // audio call

                        callmelder_notification = true;  // send a notification for  to registread the call
                        // make web rtc call
                        call_type = "audio";
                        Intent intentaudio = new Intent(ChatActivity.this.context, ConnectActivity.class);
                        ConnectActivity.received_call = "caller";
                        intentaudio.putExtra("vid_or_aud", call_type);
                        intentaudio.putExtra("user_id", currentUserId);
                        intentaudio.putExtra("room_id", pushId_callRoom);
                        startActivity(intentaudio);

                    }
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


    private class SendMessageTask extends AsyncTask<String, String, Void> {
        private final String message_contents;

        public SendMessageTask(String message_contents) {
            this.message_contents = stype_of_message + splitter_pattern_message + message_contents;
        }

        @Override
        protected Void doInBackground(String... args) {
            JSONObject inputJson;

            try {
                inputJson = new JSONObject(info.toString());
                inputJson.put("message", message_contents);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            mSocket.emit("send message", inputJson);
            return null;
        }

        @Override
        protected void onPostExecute(Void a) {
            Log.i("socket", "sent message to server");
            final MessageAdapter.MessageItem msgItem = new MessageAdapter.MessageItem(user_id, username, message_contents);
            not_on_server_indices.add(adapter.addItem(msgItem));

            // send  firebase cloud notification
        }
    }



    // private  message saver  on Sqlite  database
    private class Save_offline_MessageTask extends AsyncTask<String, String, Void> {
        private final JSONObject message_contents;

        public Save_offline_MessageTask(JSONObject message) {
            this.message_contents = message;
        }

        @Override
        protected Void doInBackground(String... args) {
            JSONObject inputJson;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());


            try {
                message new_message = new message(Integer.toString(room_id), room_uid , Integer.toString(user_id)
                ,FirebaseAuth.getInstance().getUid(),message_contents.toString() , timeStamp);

                message_db.insert_new_message(new_message);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void a) {
            Log.i("Sqlite saver", "message locally persisted ");
        }
    }



    // #####################################   import from old chat   #################################

    /// new import   ...

    //gps_menu navigation



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
                            Toasty.info(ChatActivity.this, "You have denied some permissions permanently, if the app force close try granting permission from settings.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data)
    {

        Log.e("jhkhj", Integer.toString(requestCode));

        switch (requestCode) {
            case CodeLiveLocation :
                if(resultCode == Activity.RESULT_OK){
                    // get the resukt Activity  and send that to smend   for remote  savec
                    // conjvert to local  jason
                    //new SendMessageTask("received");

                    final String result_local =data.getStringExtra("result");
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            //String result_local = "test live ";
                            stype_of_message = Type_live_location;
                            String message_contents = result_local;
                            message_contents = stype_of_message + splitter_pattern_message + message_contents;
                            JSONObject inputJson = null;

                            try {
                                inputJson = new JSONObject(info.toString());
                                inputJson.put("message", message_contents);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            mSocket.emit("send message", inputJson);
                            try {
                                // extrem important don't remove   .....
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.i("socket", "sent message to server");
                            MessageAdapter.MessageItem msgItem = new MessageAdapter.MessageItem(user_id, username, message_contents);
                            not_on_server_indices.add(adapter.addItem(msgItem));
                        }
                    });

                    break;
                }
                break;
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

                        // doc_file__upload_images_to_firebase(Uri.fromFile(new File(object.toString())), ext , filename);

                        new UploadFileToServer_chat(file, Config.FILE_UPLOAD_URL,"Doc").execute();

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

                        if(FileOpen.isImagesFile(file)){
                            //new UploadFileToServer(imagesList.get(i).getPath(), Config.IMAGES_UPLOAD_URL,finalI).execute();
                            new Compressor(this)
                                    .compressToFileAsFlowable(file)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<File>() {
                                        @Override
                                        public void accept(File file) {
                                            //compressedImage = file;
                                            new UploadFileToServer_chat(file, Config.IMAGES_UPLOAD_URL,"Images").execute();

                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) {
                                            throwable.printStackTrace();
                                            //showError(throwable.getMessage());
                                        }
                                    });

                        }else{
                            // dont' compress anything  (that problaby is  Video  or another  media that we can not compress  )
                            new UploadFileToServer_chat(file, Config.IMAGES_UPLOAD_URL,"Doc").execute();

                        }



                        // doc_file__upload_images_to_firebase(Uri.fromFile(new File(object.toString())), ext , filename);

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
                    //images_upload_images_to_firebase(uri_file , 0);

                    //new UploadFileToServer(imagesList.get(i).getPath(), Config.IMAGES_UPLOAD_URL,finalI).execute();
                    new Compressor(this)
                            .compressToFileAsFlowable(new File(uri_file.getPath()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<File>() {
                                @Override
                                public void accept(File file) {
                                    //compressedImage = file;
                                    new UploadFileToServer_chat(file, Config.IMAGES_UPLOAD_URL,"Images").execute();

                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) {
                                    throwable.printStackTrace();
                                    //showError(throwable.getMessage());
                                }
                            });
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
                        //sendMessage_location_contact("text" , results.get(i).getDisplayName()+ " : " + results.get(i).getPhoneNumbers());
                        String msg = results.get(i).getDisplayName()+ " : " + results.get(i).getPhoneNumbers();
                        stype_of_message = Type_Text;
                        new SendMessageTask(msg.trim()).execute();
                        ContactResult element = results.get(i);
                        i++;

                        // send notification
                        try {
                            System.out.println("Send Notification");
                            FCM_Message_Sender.sendWithOtherThread("token",
                                    TokenFCM_OtherUser,
                                    "Message",
                                    FirebaseAuth.getInstance().getUid(),
                                    room_name,
                                    image_url,
                                    getDateAndTime(),
                                    "room_disable",
                                    msg.trim());

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
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
                        //messageEditText.setText("location");
                        //sendMessage("location");
                        sendMessage_location("location" , latLng.latitude+":"+latLng.longitude);
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



    public static  void missedCallNotification(final Context context_call , final String CallType ,
                                               final String missedCallerId , final String Room_Id ,
                                               String ClassName_func , final String reason){


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


    private void images_upload_images_to_firebase(String  Url_media){

        try{

            String msg = Url_media;
            final String messageId = random().replaceAll("\\s+","").trim();
            stype_of_message = Type_image;
            new SendMessageTask(msg.trim()).execute();
            //String imageUrl = task.getResult().getDownloadUrl().toString();
            String imageUrl = Url_media;
            // update   ...  progression UI
            progressStatus = progressStatus +1;
            infos_progress_files.setText("Uploaded "+String.valueOf(progressStatus) +"/"+String.valueOf(number_of_files_to_send) + " files");
            // progress_bar.setProgress(progressStatus);

            if(progressStatus >= number_of_files_to_send){

                infos_progress_layout.setVisibility(View.GONE);
                progressStatus = 0;
                number_of_files_to_send = 0;

            }

            btnSend.setEnabled(true);
            // send notification ...
            try {
                System.out.println("Send Notification");
                FCM_Message_Sender.sendWithOtherThread("token",
                        TokenFCM_OtherUser,
                        "Message",
                        FirebaseAuth.getInstance().getUid(),
                        room_name,
                        image_url,
                        getDateAndTime(),
                        "room_disable",
                        msg.trim());

            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private void upload_messagev_voice(String uri){

        try{

            String msg = uri;
            stype_of_message = Type_voice;
            new SendMessageTask(msg.trim()).execute();
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

            btnSend.setEnabled(true);
            try {
                System.out.println("Send Notification");
                FCM_Message_Sender.sendWithOtherThread("token",
                        TokenFCM_OtherUser,
                        "Message",
                        FirebaseAuth.getInstance().getUid(),
                        room_name,
                        image_url,
                        getDateAndTime(),
                        "room_disable",
                        msg.trim());

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }catch (Exception ex){
            ex.printStackTrace();

        }
    }

    private void voice_upload_images_to_firebase(Uri Url_media ){

        Uri uri = Uri.fromFile(new File(mFileName));
        new UploadFileToServer_chat(new File(mFileName) , Config.FILE_UPLOAD_URL,"Voice").execute();
    }

    private void doc_file__upload_to_Server(String uri ){

        try{
            String msg = uri;
            // final String messageId = random().replaceAll("\\s+","").trim();
            stype_of_message = Type_Doc;
            new SendMessageTask(msg.trim()).execute();


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

            btnSend.setEnabled(true);

            try {
                System.out.println("Send Notification");
                FCM_Message_Sender.sendWithOtherThread("token",
                        TokenFCM_OtherUser,
                        "Message",
                        FirebaseAuth.getInstance().getUid(),
                        room_name,
                        image_url,
                        getDateAndTime(),
                        "room_disable",
                        msg.trim());

            } catch (Exception ex) {
                ex.printStackTrace();
            }




        }catch (Exception ex){
            ex.printStackTrace();

        }

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

    private void pickContact(){

        new MultiContactPicker.Builder(ChatActivity.this) //Activity/fragment context
                // .theme(R.style.MyCustomPickerTheme) //Optional - default: MultiContactPicker.Azure
                .hideScrollbar(false) //Optional - default: false
                .showTrack(true) //Optional - default: true
                .searchIconColor(Color.WHITE) //Option - default: White
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                .handleColor(ContextCompat.getColor(ChatActivity.this, R.color.purple)) //Optional - default: Azure Blue
                .bubbleColor(ContextCompat.getColor(ChatActivity.this, R.color.purple)) //Optional - default: Azure Blue
                .bubbleTextColor(Color.WHITE) //Optional - default: White
                .showPickerForResult(CONTACT_PICKER_REQUEST);
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

    public void openVoiceRecorder(){
        //Implement voice selection
        mrecordVoiceButton =(ImageButton) findViewById(R.id.recordVoiceButton);

        // start position for all  dialog
        Startposition_Custom_dialog = (ImageButton) findViewById(R.id.recordVoiceButton);
        // mRecordLable = (TextView) findViewById(R.id.recordLable);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/recorded_audio.3gp";

        mrecordVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiag(mrecordVoiceButton);
                startRecording();
            }
        });

/*
        mrecordVoiceButton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                    Toasty.info(ChatActivity.this, "Start recording  ", Toast.LENGTH_LONG).show();
                    startRecording();
                    //mRecordLable.setText("Recording started...");
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP){

                    Toasty.info(ChatActivity.this, "Stop recording  ", Toast.LENGTH_LONG).show();

                    stopRecording();

                    //mRecordLable.setText("Recording stopped...");

                }
                return false;
            }
        });

        */

        //on complete: sendVoice()
    }

    private void startRecording() {


        //DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId).child(otherUserId).push();
        final String messageId = random().replaceAll("\\s+","").trim();
        //currentUserId.trim()+random();
        // init path audio media
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/recorded_audio_"+currentUserId.trim().toString()+messageId+".3gp";


        mRecorder = new MediaRecorder();
        try{

            record_time_voice.setFormat("Time- %s"); // set the format for a chronometer
            record_time_voice.start();

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

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void stopRecording() {
       try{
           if(mRecorder != null){
               mRecorder.stop();
           }

       }catch(Exception ex){
           ex.printStackTrace();
       }
        try{
            if(mRecorder != null){
                mRecorder.release();
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }

        try{
            if(record_time_voice != null){
                record_time_voice.stop();
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }


        try{
            mRecorder = null;
        }catch (Exception ex){
           ex.printStackTrace();
        }

        try {
            voice_upload_images_to_firebase(Uri.fromFile(new File(mFileName)));
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }

        // generate  random integer
        final int random = new Random().nextInt(100000000) + 2000; // [0, 60] + 20 => [20, 80]
        String numberAsString = randomStringBuilder.toString().trim() + Integer.toString(random).trim();
        numberAsString.replaceAll("\\s+","").trim();

        return numberAsString;
}

    public static void Open_map(Context context , String message){

        String[] LatLong = message.split(":");
        System.out.println(LatLong[0]);
        System.out.println(LatLong[1]);


        // Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
        Uri gmmIntentUri = Uri.parse("geo:"+Double.parseDouble(LatLong[0])+","+Double.parseDouble(LatLong[1]));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }


    }

    public static void Open_navi(Context context ,String message){

        String[] LatLong = message.split(":");
        System.out.println(LatLong[0]); // latiudute
        System.out.println(LatLong[1]); // longitude

        Uri gmmIntentUri = Uri.parse("google.navigation:q="+Double.parseDouble(LatLong[0])+","+Double.parseDouble(LatLong[1]));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }

    }

    public static void Open_street_view(Context mContext, String message){

        String[] LatLong = message.split(":");
        System.out.println(LatLong[0]);
        System.out.println(LatLong[1]);

        // Uses a PanoID to show an image from Maroubra beach in Sydney, Australia
        Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+Double.parseDouble(LatLong[0])+","+Double.parseDouble(LatLong[1])+"&cbp=0,30,0,0,-15");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(mapIntent);
        }



    }

    public static  void showDiag_gps_menu(final Context mContext , final ImageButton Startposition , final String msg) {

        final View dialogView = View.inflate(mContext,R.layout.dialog_gps_navi_choice,null);

        final Dialog dialog = new Dialog(mContext,R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(dialogView);

        ImageView closeDialogImg = (ImageView) dialog.findViewById(R.id.closeDialogImg);
        closeDialogImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealShow_voice(dialogView, false, dialog , Startposition);
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow_voice(dialogView, true, null , Startposition);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK){

                    revealShow_voice(dialogView, false, dialog , Startposition);
                    return true;
                }

                return false;
            }
        });



        ImageView navi_view = (ImageView) dialog.findViewById(R.id.navi_view);
        navi_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Open_navi(mContext , msg);

            }
        });


        ImageView map_view = (ImageView) dialog.findViewById(R.id.map_view);
        map_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Open_map(mContext ,msg);

            }
        });

        ImageView  street_view_360 = (ImageView) dialog.findViewById(R.id.street_view_360);
        street_view_360.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Open_street_view(mContext , msg);

            }
        });


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }

    // media Menu  voice navigation
    private void showDiag_media_menu(final ImageButton Startposition) {

        final View dialogView = View.inflate(this,R.layout.dialog_media_menu,null);

        final Dialog dialog = new Dialog(this,R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(dialogView);
        ImageView imageView = (ImageView)dialog.findViewById(R.id.closeDialogImg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                revealShow(dialogView, false, dialog , Startposition);
            }
        });



        ImageView live_tracking = (ImageView)dialog.findViewById(R.id.live_tracking);
        live_tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                revealShow(dialogView, false, dialog , Startposition);
                Intent LiveLocationSharing = new Intent(ChatActivity.this , MoD_Live_Location_sharing_Activity.class);
                startActivityForResult(LiveLocationSharing, CodeLiveLocation);
                //startActivity(LiveLocationSharing);
                //Toasty.info(context, "live Tracking ist  not enable on this Release", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView feat1 = (ImageView)dialog.findViewById(R.id.feat1);
        feat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // custom notification ....

                try {
                    System.out.println("Send Notification");
                    FCM_Message_Sender.sendWithOtherThread("token",
                            TokenFCM_OtherUser,
                            "Friend Request",
                            FirebaseAuth.getInstance().getUid(),
                            room_name,
                            image_url,
                            getDateAndTime(),
                            "room_disable",
                            "you are a hev Friend Request");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }





                FCM_Message_Sender.sendWithOtherThread("token" ,
                        TokenFCM_OtherUser ,
                        "image",
                        FirebaseAuth.getInstance().getUid(),
                        room_name,
                        image_url,
                        getDateAndTime(),
                        "room_disable",
                        "photo message");


            }
        });

        ImageView f2 = (ImageView)dialog.findViewById(R.id.f2);
        f2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toasty.info(context, "Surprise surpise ;) ....Comming Soon ", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView f3 = (ImageView)dialog.findViewById(R.id.f3);
        f3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toasty.info(context, "Surprise surpise ;) ....Comming Soon ", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView f4 = (ImageView)dialog.findViewById(R.id.f4);
        f4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toasty.info(context, "Surprise surpise ;) ....Comming Soon ", Toast.LENGTH_SHORT).show();
            }
        });


        ImageView Gallery = (ImageView)dialog.findViewById(R.id.Gallery);
        Gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                revealShow(dialogView, false, dialog , Startposition);

                ImagePicker.create(ChatActivity.this)
                        .start(); // start image picker activity with request code


            }
        });

        ImageView document = (ImageView)dialog.findViewById(R.id.document);
        document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                revealShow(dialogView, false, dialog , Startposition);

                // Toast.makeText(CreateGroupChatActivity.this, "Button " + index + " is pressed.", Toast.LENGTH_SHORT).show();
                //FilePickerBuilder.getInstance().set
                FilePickerBuilder.getInstance().setMaxCount(10)
                        .setSelectedFiles(docPaths)
                        .enableVideoPicker(true)
                        .enableDocSupport(true)
                        .showGifs(true)
                        .enableSelectAll(true)
                        .showFolderView(true)
                        // .setActivityTheme(R.style.LibAppTheme)
                        .pickFile(ChatActivity.this);            }
        });

        ImageView camera = (ImageView)dialog.findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                MY_CAMERA_PERMISSION_CODE);
                    } else {
                        //Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        revealShow(dialogView, false, dialog , Startposition);
                        ImagePicker.cameraOnly().start(ChatActivity.this); // Could be Activity, Fragment, Support Fragment
                    }
                }
            }
        });

        ImageView location = (ImageView)dialog.findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealShow(dialogView, false, dialog , Startposition);
                locationPlacesIntent();
            }
        });

        ImageView Contact = (ImageView)dialog.findViewById(R.id.Contact);
        Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                revealShow(dialogView, false, dialog , Startposition);
                pickContact();
            }
        });

        ImageView media = (ImageView)dialog.findViewById(R.id.media);
        media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                revealShow(dialogView, false, dialog , Startposition);

                // Toast.makeText(CreateGroupChatActivity.this, "Button " + index + " is pressed.", Toast.LENGTH_SHORT).show();
                //FilePickerBuilder.getInstance().set
                FilePickerBuilder.getInstance().setMaxCount(10)
                        .setSelectedFiles(docPaths)
                        .enableVideoPicker(true)
                        .enableDocSupport(true)
                        .showGifs(true)
                        .enableSelectAll(true)
                        .showFolderView(true)
                        // .setActivityTheme(R.style.LibAppTheme)
                        .pickPhoto(ChatActivity.this);
            }
        });



        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow(dialogView, true, null , Startposition);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK){

                    revealShow(dialogView, false, dialog , Startposition);
                    return true;
                }

                return false;
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void sendMessage_location(final String type , String message)
    {
        btnSend.setEnabled(false);
        if(message.length() == 0)
        {
            Toasty.info(getApplicationContext(), "invalid message !", Toast.LENGTH_SHORT).show();
            btnSend.setEnabled(true);
        }
        else
        {


            String msg = message;
            final String messageId = random().replaceAll("\\s+","").trim();
            stype_of_message = Type_map;
            new SendMessageTask(msg.trim()).execute();
            //String imageUrl = task.getResult().getDownloadUrl().toString();
            //play_sound();

            try {
                System.out.println("Send Notification");
                FCM_Message_Sender.sendWithOtherThread("token",
                        TokenFCM_OtherUser,
                        "Message",
                        FirebaseAuth.getInstance().getUid(),
                        room_name,
                        image_url,
                        getDateAndTime(),
                        "room_disable",
                        msg.trim());

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    // record voice navigation
    private void showDiag(final ImageButton Startposition) {

        final View dialogView = View.inflate(this,R.layout.dialog_voice_record,null);

        final Dialog dialog = new Dialog(this,R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(dialogView);



        record_time_voice = (Chronometer) dialog.findViewById(R.id.chrono);
        ImageView imageView = (ImageView)dialog.findViewById(R.id.closeDialogImg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopRecording_and_close_dialog();
                revealShow(dialogView, false, dialog , Startposition);
            }
        });

        TextView SendTextview = (TextView) dialog.findViewById(R.id.send_textview);
        SendTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording(); // stop recording and send voice mail
                revealShow(dialogView, false, dialog , Startposition);

            }
        });


        // send recorded  voice mail ...
        ImageView  SendRecordedVoice = (ImageView) dialog.findViewById(R.id.start_record);
        SendRecordedVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording(); // stop recording and send voice mail
                revealShow(dialogView, false, dialog , Startposition);

            }
        });

        TextView deleteTextView = (TextView) dialog.findViewById(R.id.delete_textview);
        deleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // maybe  a confirmation dialog her ???  to ask user if  he want to close this dialog windows  ...

                new BottomDialog.Builder(ChatActivity.this)
                        .setTitle("voice Mail")
                        .setContent("Your message will be deleted !")
                        .setPositiveText("are you sure ? ")
                        .setPositiveBackgroundColorResource(R.color.red)
                        .setCancelable(true)
                        .onPositive(new BottomDialog.ButtonCallback() {
                            @Override
                            public void onClick(@NonNull BottomDialog bottom_dialog) {

                                stopRecording_and_close_dialog();
                                bottom_dialog.dismiss();
                                revealShow(dialogView, false, dialog , Startposition);
                            }
                        })

                        .setNegativeText("no")
                        .setNegativeTextColorResource(R.color.blue)
                        //.setNegativeTextColor(ContextCompat.getColor(this, R.color.colorAccent)
                        .onNegative(new BottomDialog.ButtonCallback() {
                            @Override
                            public void onClick(BottomDialog dialog) {
                                Log.d("stay ", "Do something!");

                            }
                        })
                        .show();

            }
        });

        ImageView deleteRecord = (ImageView) dialog.findViewById(R.id.delete_record_and_close);
        deleteRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // maybe  a confirmation dialog her ???  to ask user if  he want to close this dialog windows  ...

                new BottomDialog.Builder(ChatActivity.this)
                        .setTitle("voice Mail")
                        .setContent("Your message will be deleted !")
                        .setPositiveText("are you sure ? ")
                        .setPositiveBackgroundColorResource(R.color.red)
                        .setCancelable(true)
                        .onPositive(new BottomDialog.ButtonCallback() {
                            @Override
                            public void onClick(@NonNull BottomDialog bottom_dialog) {

                                stopRecording_and_close_dialog();
                                bottom_dialog.dismiss();
                                revealShow(dialogView, false, dialog , Startposition);
                            }
                        })

                        .setNegativeText("no")
                        .setNegativeTextColorResource(R.color.blue)
                        //.setNegativeTextColor(ContextCompat.getColor(this, R.color.colorAccent)
                        .onNegative(new BottomDialog.ButtonCallback() {
                            @Override
                            public void onClick(BottomDialog dialog) {
                                Log.d("stay ", "Do something!");

                            }
                        })
                        .show();

            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow(dialogView, true, null , Startposition);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK){

                    revealShow(dialogView, false, dialog , Startposition);
                    return true;
                }

                return false;
            }
        });



        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }

    private void revealShow(View dialogView, boolean b, final Dialog dialog , ImageButton atarter_button) {

        final View view = dialogView.findViewById(R.id.dialog);

        int w = view.getWidth();
        int h = view.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        int cx = (int) (atarter_button.getX() + (atarter_button.getWidth()/2));
        int cy = (int) (atarter_button.getY())+ atarter_button.getHeight() + 56;


        if(b){
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx,cy, 0, endRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(900);
            revealAnimator.start();

            // start  voice record  ...
            //startRecording();

        } else {

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(900);
            anim.start();

            // stotp recording and send   voice
        }

    }

    // record voice navigation
    public static void showDiag_voice(final Context dialogContext , final String msg_url) {

        final View dialogView = View.inflate(dialogContext,R.layout.dialog_voice_player,null);
        final Dialog dialog = new Dialog(dialogContext,R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        final Uri[] voicefile = {null};
        final MediaPlayer[] mediaPlayer = new MediaPlayer[1];


        dialog.setContentView(dialogView);

        final LinearLayout prepare_voice = (LinearLayout)dialog.findViewById(R.id.view_prepare_voice);
        prepare_voice.setVisibility(View.VISIBLE);



        final SeekBar seekBar_view = (SeekBar) dialog.findViewById(R.id.SeekBar_view);
        final ImageView ButtonPlayStop = (ImageView)dialog.findViewById(R.id.ButtonPlayStop);
        ButtonPlayStop.setEnabled(false);
        seekBar_view.setEnabled(false);



        ImageView imageView = (ImageView)dialog.findViewById(R.id.closeDialogImg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ButtonPlayStop.setFocusable(false);
                ButtonPlayStop.setBackground(dialogContext.getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));

                try{
                    mediaPlayer[0].stop();
                    mediaPlayer[0].release();
                    mediaPlayer[0].reset();
                }catch (Exception ex){
                    ex.printStackTrace();
                }

                revealShow_voice(dialogView, false, dialog , Startposition_Custom_dialog);
            }
        });


        ButtonPlayStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Uri voicefile_local = voicefile[0];

                    //reset
                    if (ButtonPlayStop.isFocusable() == false) {
                        ButtonPlayStop.setBackground(dialogContext.getResources().getDrawable(R.drawable.ic_pause_black_24dp));
                        ButtonPlayStop.setFocusable(true);
                        try{
                            mediaPlayer[0].start();
                            startPlayProgressUpdater(dialogContext, ButtonPlayStop, mediaPlayer[0],seekBar_view , handler);
                        }catch (IllegalStateException e) {
                            mediaPlayer[0].pause();
                        }
                    }else {
                        ButtonPlayStop.setFocusable(false);
                        ButtonPlayStop.setBackground(dialogContext.getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                        mediaPlayer[0].pause();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


        // start  download  of voice  message
        //revealShow_voice(dialogView, true, dialog , Startposition_Custom_dialog);
        //update_work_background.setVisibility(View.VISIBLE);
        FileLoader.with(dialogContext)
                //.load("https://firebasestorage.googleapis.com/v0/b/gudana-cloud-technology.appspot.com/o/post_video%2F%232GbnW-3null?alt=media&token=d11d6cc2-0068-43b2-a39c-575cba04ea15")
                .load(msg_url)
                .fromDirectory("Gudana_dir_files", FileLoader.DIR_EXTERNAL_PRIVATE)
                .asFile(new FileRequestListener<File>() {
                    @Override
                    public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                        // Glide.with(MainActivity_GuDDana.this).load(response.getBody()).into(iv);
                        // open video in cache
                        try {
                            prepare_voice.setVisibility(View.GONE);
                            prepare_voice.setVisibility(View.GONE);
                            // init Media player


                            //update_work_background.setVisibility(View.GONE);
                            //File myUriFile = response.getDownloadedFile().getAbsoluteFile();
                            voicefile[0] = Uri.parse(response.getDownloadedFile().getPath());
                            mediaPlayer[0] = MediaPlayer.create(dialogContext, voicefile[0]);
                            seekBar_view.setMax(mediaPlayer[0].getDuration());
                            seekBar_view.setOnTouchListener(new View.OnTouchListener() {
                                @Override public boolean onTouch(View v, MotionEvent event) {
                                    seekChange(v , mediaPlayer[0]);
                                    return false; }
                            });

                            ButtonPlayStop.setEnabled(true);
                            seekBar_view.setEnabled(true);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(FileLoadRequest request, Throwable t) {
                        Log.d("error", "onError: " + t.getMessage());
                    }
                });

        // revealShow(dialogView, false, dialog , Startposition);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow_voice(dialogView, true, null , Startposition_Custom_dialog);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK){

                    revealShow_voice(dialogView, false, dialog , Startposition_Custom_dialog);
                    return true;
                }

                return false;
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }

    public static  void revealShow_voice(View dialogView, boolean b, final Dialog dialog , ImageButton atarter_button) {

        final View view = dialogView.findViewById(R.id.dialog);

        int w = view.getWidth();
        int h = view.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        int cx = (int) (atarter_button.getX() + (atarter_button.getWidth()/2));
        int cy = (int) (atarter_button.getY())+ atarter_button.getHeight() + 56;


        if(b){
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx,cy, 0, endRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(900);
            revealAnimator.start();

        } else {

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(600);
            anim.start();

        }

    }

    // audio  lecteur ######################  audio player  ...

    public static void startPlayProgressUpdater(final Context mContext , final ImageView buttonPlayStop , final MediaPlayer mediaPlayer , final SeekBar seekBar, final Handler handler) {
        try{

            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            if (mediaPlayer.isPlaying()) {
                Runnable notification = new Runnable() {
                    public void run() {
                        startPlayProgressUpdater(mContext , buttonPlayStop,mediaPlayer,seekBar , handler);
                    }
                };
                handler.postDelayed(notification,200);
            }else{
                mediaPlayer.pause();
                buttonPlayStop.setBackground(mContext.getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                //seekBar.setProgress(0);
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    // This is event handler thumb moving event
    public static  void seekChange(View v , MediaPlayer mediaPlayer){
        if(mediaPlayer.isPlaying()){
            SeekBar sb = (SeekBar)v;
            mediaPlayer.seekTo(sb.getProgress());
        }
    }

    private void stopRecording_and_close_dialog() {

        try {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            record_time_voice.stop();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer_chat extends AsyncTask<Void, Integer, String> {
        long totalSize = 0;
        private File filePath = null;
        private String Url_Server = null;
        private String UploadType ;
        private String url_file_uploaded;
        private JSONParser jsonParser = new JSONParser();


        public UploadFileToServer_chat(File filePath , String Url_Server_to_upload , String Type_Upload) {
            super();
            this.filePath = filePath;
            this.Url_Server = Url_Server_to_upload;
            this.UploadType = Type_Upload;
            // do stuff
        }

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            //progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            //progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            //progressBar.setProgress(progress[0]);

            // updating percentage value
            //txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(this.Url_Server);
            //HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);


            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                // File sourceFile = new File(filePath);
                File sourceFile = filePath;

                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("website",
                        new StringBody("www.gudana.com"));
                entity.addPart("email", new StringBody("abc@gmail.com"));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("PostImagesClasses", "Response from server: " + result);
            JSONObject json_data = null;

            try {
                json_data = new JSONObject(result);
                Boolean error = json_data.getBoolean("error");
                String url_file = json_data.getString("file_path");
                String message = json_data.getString("message");

                if(error == false){

                    Toasty.info(context, "file uploaded ", Toast.LENGTH_SHORT).show();
                    this.url_file_uploaded = url_file;
                    //fileDoneList.remove(finalI);
                    //fileDoneList.add(finalI,"done");
                    // uploadedImagesUrl.add(this.url_file_uploaded);
                    //uploadListAdapter.notifyDataSetChanged();
                    // send message uploaded
                    if(this.UploadType.equalsIgnoreCase("Images")){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                images_upload_images_to_firebase(url_file_uploaded);
                                // Do stuff…
                            }
                        });

                        //images_upload_images_to_firebase(this.url_file_uploaded);
                    }

                    if(this.UploadType.equalsIgnoreCase("Voice")){

                        runOnUiThread(new Runnable() {
                            public void run() {

                                upload_messagev_voice(url_file_uploaded);
                                // Do stuff…
                            }
                        });
                    }

                    if(this.UploadType.equalsIgnoreCase("Doc")){

                        runOnUiThread(new Runnable() {
                            public void run() {

                                doc_file__upload_to_Server(url_file_uploaded);
                                // Do stuff…
                            }
                        });
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            super.onPostExecute(result);
        }

    }

    public static void download_and_open_document(final Context mContext, String url , String filename) throws IOException {

        File rootPath = new File(Environment.getExternalStorageDirectory(), "Gudana_directory");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath,filename);
        ChatActivity.data_processing.setVisibility(View.VISIBLE);

        FileLoader.with(mContext)
                //.load("https://firebasestorage.googleapis.com/v0/b/gudana-cloud-technology.appspot.com/o/post_video%2F%232GbnW-3null?alt=media&token=d11d6cc2-0068-43b2-a39c-575cba04ea15")
                .load(url)
                .fromDirectory("Gudana_dir_files", FileLoader.DIR_EXTERNAL_PRIVATE)
                .asFile(new FileRequestListener<File>() {
                    @Override
                    public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                        // Glide.with(MainActivity_GuDDana.this).load(response.getBody()).into(iv);
                        // open video in cache
                        try {
                            //update_work_background.setVisibility(View.GONE);
                            File myUriFile = response.getDownloadedFile().getAbsoluteFile();
                            ChatActivity.data_processing.setVisibility(View.GONE);
                            Uri uri_file = Uri.parse(response.getDownloadedFile().getAbsolutePath());
                            try {
                                FileOpen.openFile(mContext,myUriFile);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(FileLoadRequest request, Throwable t) {
                        Log.d("error", "onError: " + t.getMessage());
                    }
                });

    }


}
