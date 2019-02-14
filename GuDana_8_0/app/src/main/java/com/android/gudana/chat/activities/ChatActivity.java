package com.android.gudana.chat.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.android.gudana.chat.fragments.Calls_Fragment;
import com.android.gudana.chat.model.Call;
import com.android.gudana.chat.model.User;
import com.android.gudana.chat.model.message;
import com.android.gudana.chat.model.user_room_message_db;
import com.android.gudana.chat.utilities.Utility;
import com.android.gudana.fcm.CustomFcm_Util;
import com.android.gudana.hify.utils.AndroidMultiPartEntity;
import com.android.gudana.hify.utils.Config;
import com.android.gudana.hify.utils.JSONParser;
import com.android.gudana.hify.utils.NetworkUtil;
import com.android.gudana.hify.utils.database.UserHelper;
import com.android.gudana.hify.utils.refresh_token_on_firestore;
import com.android.gudana.hify.utils.utils_v2.FileOpen;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.github.piasy.rxandroidaudio.AudioRecorder;
import com.github.piasy.rxandroidaudio.PlayConfig;
import com.github.piasy.rxandroidaudio.RxAudioPlayer;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.models.sort.SortingTypes;
import es.dmoral.toasty.Toasty;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import id.zelory.compressor.Compressor;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static io.opencensus.tags.TagValue.MAX_LENGTH;

//import com.android.gudana.chatapp.models.Message;
//import com.android.gudana.chatapp.utils_v2.FileOpen;


public class ChatActivity_backup extends AppCompatActivity {
    private static final String TAG = "activities/ChatActivity";
    public static final int CodeLiveLocation = 500;
    public static int Increase = 0;

    public static String splitter_pattern_message = "#568790%%%7689%&&57#";
    public static  String Type_Text = "text";
    public static  String Type_image = "images";
    public static  String Type_voice = "voice";
    public static String Type_Doc  = "doc";
    public static String Type_map = "map";
    public static  String Type_live_location = "live_loc";
    public static String  stype_of_message = Type_Text;
    private MediaPlayer mediaPlayer_song_out;
    public  MediaPlayer mediaPlayer_song_in;

    public ProgressBar audio_recorder_progress;

    // ############### import from  old Chat  #######################

    public  static String pushId_callRoom = "";

    // User data
    public static  String currentUserId;
    public static  String NameCurrentUser = "";
    public static  String url_Icon_currentUser = "";
    // ca_activity_chat views

    private EmojiconEditText messageEditText;

    private ImageButton mrecordVoiceButton;

    private MediaRecorder mRecorder;
    //private String mFileName = null;
    private static final String LOG_TAG = "Record_log";
    private int goto_position=1;

    //Audio Runtime Permissions
    private boolean permissionToRecordAccepted = false;
    private boolean permissionToWriteAccepted = false;
    private String [] permissions = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};

    private static final int CONTACT_PICKER_REQUEST = 23 ;
    private static final int PLACE_PICKER_REQUEST = 3;
    public static Chronometer record_time_voice ;


    //Views UI
    private ImageView btEmoji;
    private EmojIconActions emojIcon;

    private int progressStatus = 0;
    private int number_of_files_to_send = 0;
    public static  Handler handler = new Handler();
    private ProgressBar progress_bar;
    private RelativeLayout infos_progress_layout;
    private TextView infos_progress_files;
    private TextView infos_progress_stop;
    private Context context;


    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    // boom menu
    private BoomMenuButton bmb ;


    private ArrayList<String> docPaths = new ArrayList<>();
    private ArrayList<String> photoPaths = new ArrayList<>();
    private static final int REQUEST = 112;

    // Storage Permissions
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Context mContext= ChatActivity_backup.this;


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
    private boolean first_message_history_lock = false;

    private final ArrayList<String> usersTyping = new ArrayList<>();
    private Resources res;

    private JSONObject info;
    private Socket mSocket;


    private ArrayList<Integer> not_on_server_indices = new ArrayList<>();

    int type;

    int user_id;
    String username;
    public static  Button btnSend;

    public static String room_name ;
    int room_id ;

    HashMap<String, Emitter.Listener> eventListeners = new HashMap<>();

    private user_room_message_db message_db;
    private String room_uid = "";
    private String token_id, image_url;
    private FirebaseFirestore mFirestore;
    private int Lastmsg_Id;
    private ImageView back_bottom;
    private TextView room_name_textview , usre_infos;
    private CircleImageView pic_profile;
    private ProgressBar processin;
    private AudioRecorder mAudioRecorder = null;
    private File mAudioFile;


    private RxAudioPlayer mRxAudioPlayer;
    private Disposable mRecordDisposable;
    //private RxPermissions mPermissions;

    public static void startActivity(Context context){
        Intent intent = new Intent(context, ChatActivity_backup.class);
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
            if (!permissionToRecordAccepted ) ChatActivity_backup.super.finish();
            if (!permissionToWriteAccepted ) ChatActivity_backup.super.finish();


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
        setContentView(R.layout.chat_activity_chat_backup);
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

        processin = (ProgressBar) findViewById(R.id.processin);
        processin.setMax(100);
        processin.setProgress(0);


        // will only be used when type == ROOM
        room_name = intent.getStringExtra("room_name");
        room_id = intent.getIntExtra("room_id", -1);
        room_uid = intent.getStringExtra("room_uid");
        TokenFCM_OtherUser = token_id = intent.getStringExtra("token_id");
        image_url = intent.getStringExtra("image_url");
        Config.Chat_Activity_otherUserId = getIntent().getStringExtra("userid");

        refresh_token_on_firestore.refresh_token();

        // will only be used when type == FRIEND
        String friend_username = intent.getStringExtra("friend_username");
        int friend_user_id = intent.getIntExtra("friend_user_id", -1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle(room_name);

        room_name_textview = (TextView) findViewById(R.id.room_name);
        room_name_textview.setText(room_name);


        pic_profile = (CircleImageView) findViewById(R.id.pic_profile);
        usre_infos = (TextView) findViewById(R.id.infos_user) ;

        // init sqlite helper
        message_db = new user_room_message_db(ChatActivity_backup.this);

        mediaPlayer_song_out = MediaPlayer.create(ChatActivity_backup.this, R.raw.stairs);
        mediaPlayer_song_in = MediaPlayer.create(ChatActivity_backup.this, R.raw.relentless);


        ActionBar actionBar = getSupportActionBar();


        //test create  filepicker ...

        // audio recorder  ...
        mAudioRecorder = AudioRecorder.getInstance();
        mAudioFile = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + System.nanoTime() + ".file.m4a");
        mRxAudioPlayer = RxAudioPlayer.getInstance();



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
            eventListeners.put("history", onHistory);
            eventListeners.put("typing", onTyping);
            eventListeners.put("stop typing", onStopTyping);
            eventListeners.put("rethink_db", rethink_db_event_receive);
            setListeningToEvents(true);


        }catch(Exception ex){
            ex.printStackTrace();
        }

        mSocket.emit("join", info);
        //mSocket.emit("fetch messages", info);
        first_message_history_lock = true;

        listViewMessages = (ListView) findViewById(R.id.listView_messages);

        footerView = (LinearLayout) findViewById(R.id.layout_typing);
        footerView.setVisibility(View.GONE);

        usersTypingTextView = (TextView) footerView.findViewById(R.id.users_typing);
        isTypingTextView = (TextView) footerView.findViewById(R.id.is_typing);

        adapter = new MessageAdapter(ChatActivity_backup.this, username);

        listViewMessages.setAdapter(adapter);

        listViewMessages.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                goto_position = totalItemCount;
                if (adapter.getCount() == 0) {
                    return;
                }
                //System.out.println("first : " + Integer.toString(firstVisibleItem - firstVisibleItem) + "  visible : " + Integer.toString(visibleItemCount) + "third  :" + Integer.toString(totalItemCount));
                if((totalItemCount - firstVisibleItem)  >25){
                    back_bottom.setVisibility(View.VISIBLE);
                }else{
                    back_bottom.setVisibility(View.GONE);
                }

                if (firstVisibleItem == 0) {
                    // check if we reached the top or bottom of the list
                    View v = listViewMessages.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset < 2) {
                        // reached the top:
                        Log.d("ChatActivity", "first_message_history_lock=" + first_message_history_lock);
                        if (!first_message_history_lock) {


                        }
                    }
                }
            }
        });


        } catch (Exception e) {
            e.printStackTrace();
        }


        back_bottom = (ImageView) findViewById(R.id.back_bottom);
        back_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listViewMessages.smoothScrollToPosition(goto_position);
            }
        });

        btnSend = (Button) findViewById(R.id.btn_send);

        messageEditText = (EmojiconEditText) findViewById(R.id.txt_message);
        messageEditText = (EmojiconEditText) findViewById(R.id.txt_message);
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

                    new SendMessageTask(msg.trim()).execute();
                    // send notification parraleli

                }
            }
        });

         // add import old Chat  #############################
        // ListView listView_messages = findViewById(R.id.listView_messages);

        Config.Chat_Activity_running = true;
        //ViCall = new Ringing_Vibrate_Manager(CreateGroupChatActivity.this.getApplicationContext());
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

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
        //mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        //mFileName += "/recorded_audio.3gp";


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

        context = ChatActivity_backup.this.getApplicationContext();
        //initializeScreen();
        // mToolBar.setTitle(chatName);
        //showMessages();
        //addListeners();

        try{

            mrecordVoiceButton =(ImageButton) findViewById(R.id.recordVoiceButton);

            Startposition_Custom_dialog = (ImageButton) findViewById(R.id.recordVoiceButton);
            // mRecordLable = (TextView) findViewById(R.id.recordLable);

            mrecordVoiceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDiag(mrecordVoiceButton);
                    startRecording();
                }
            });

            // hide Keyboard
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

            askPermission();

            // get Users infos

        }catch(Exception ex){
            ex.printStackTrace();
        }
        // appBarName.setText("GuDana User");
        // create  fcm Utill to send call  notification and  special notification   ... to other users
        FCM_Message_Sender = new CustomFcm_Util();
        mFirestore = FirebaseFirestore.getInstance();
        get_otherUsers_infos_token_fcm();

        // enable offline capaibilities
        // get All message
        getAllmessage();

        // refresh Token Id in Firestore ...

    }

    public void getUnreeadmessage_onServer() {

            try {
                JSONObject json = new JSONObject(info.toString());
                if(adapter.getCount() > 0) {
                    // json.put("after_id_message", -1); // always -1 to get all message  history ...for new users on Group  ...
                    // get  custom message  from custom position...
                    json.put("after_id_message", Lastmsg_Id-3); // get last message - 3

                }
                // get message  on local datasore  sqlite  ...
                mSocket.emit("fetch_messages_from_msg_id", json);
                first_message_history_lock = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    public void get_otherUsers_infos_token_fcm(){

        // get Users Informations
        mFirestore.collection("Users")
                .document(Config.Chat_Activity_otherUserId )
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        //friend_name = documentSnapshot.getString("name");
                        //friend_email = documentSnapshot.getString("email");
                        //friend_image = documentSnapshot.getString("image");
                        TokenFCM_OtherUser = token_id = documentSnapshot.getString("token_id");
                        String friend_image = documentSnapshot.getString("image");

                        Glide.with(ChatActivity_backup.this)
                                .setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.default_user_art_g_2))
                                .load(friend_image)
                                .into(pic_profile);
                       //pic_profile ...

                    }
                });

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



                //  public Call(long timestamp_call, String ty_video_or_audio , long date_call, String call_duration,
                // Integer callAtribut, String id, String username, String name, String image) {


                SimpleDateFormat sdf_audio_date_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date now = new Date();
                String  call_time_audio = Utility.getAbbreviatedDateTime(now);

                final Call newCall = new Call(call_time_audio , "audio", 133216546, "03 Min",
                        1, Config.Chat_Activity_otherUserId,username, username,image_url);


                if(CallFragment.running == true ){
                    Toasty.warning(context, "you can not start  two calls at the same time  ! ", Toast.LENGTH_LONG).show();

                }else {

                    // update  fragment  call  notidicaton audio
                    Calls_Fragment.newCallItem(newCall);
                    call_infos_notification(ChatActivity_backup.this.context ,"audio");
                    // call Button

                }

                //Toasty.info(getApplicationContext(), R.string.not_imp6565lemented, Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_call_video:


                SimpleDateFormat sdf_video_date_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date now_video = new Date();
                String  call_time_video = Utility.getAbbreviatedDateTime(now_video);
                final Call newCall_audio = new Call(call_time_video , "video", 133216546, "21 Sec",
                        1, Config.Chat_Activity_otherUserId,username, username,image_url);


                if(CallFragment.running == true ){
                    Toasty.warning(context, "you can not start  two calls at the same time  ! ", Toast.LENGTH_LONG).show();

                }else {
                    // update  fragment call   notification video
                    Calls_Fragment.newCallItem(newCall_audio);
                    call_infos_notification(ChatActivity_backup.this.context ,"video");
                    // disable call Button  ...

                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        try{

            Config.Chat_Activity_running = false;
            mSocket.emit("leave", info);
            mSocket.disconnect();
            setListeningToEvents(false);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        Log.i(TAG, "Destroying...");

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Config.Chat_Activity_running = false;
        super.onPause();
    }

    @Override
    protected void onStart() {
        Config.Chat_Activity_running = true;
        super.onStart();
    }

    @Override
    protected void onStop() {
        Config.Chat_Activity_running = false;
        super.onStop();
    }

    @Override
    protected void onResume() {
        Config.Chat_Activity_running = true;
        super.onResume();
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


    public void getAllmessage(){

        int Numb_Event = 0;
        Numb_Event = message_db.getNumberOfmessage();
        Cursor rs = message_db.getData_message(Numb_Event);
        rs.moveToFirst();

        if(Numb_Event > 0){

            // get all  Raw from Table   ...
            //Cursor  cursor = live_location.rawQuery("select * from table",null);
            List<message> data = message_db.getAllMessage(Integer.toString(room_id), room_uid);
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

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // than get unread message on server
        getUnreeadmessage_onServer();

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

            Lastmsg_Id = message_id;

            if(user_id == msg_user_id && not_on_server_indices.size() > 0) {
                /** TODO: remove assumption that messages are received in order
                 * Proper way is to sort messages by their IDs in ascending order
                 **/

                ChatActivity_backup.this.runOnUiThread(new Runnable() {
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
                final MessageAdapter.MessageItem msgItem = new MessageAdapter.MessageItem(message_id,
                        user_id, msg_username,
                        message_contents,
                        datetimeutc);

                ChatActivity_backup.this.runOnUiThread(new Runnable() {
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

            ChatActivity_backup.this.runOnUiThread(new Runnable() {
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
                ChatActivity_backup.this.runOnUiThread(new Runnable() {
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

            ChatActivity_backup.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.addItem(broadcastItem);
                }
            });
        }
    };


    private final Emitter.Listener rethink_db_event_receive = new Emitter.Listener() {
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

                // save message  on local  database  for offline use  ...
                // new Save_offline_MessageTask (json).execute();

                // test offline  use ...must be removed     immediatly after test purpose
                //new Save_offline_MessageTask (json).execute();
                //new Save_offline_MessageTask (json).execute();
                //new Save_offline_MessageTask (json).execute();
                // end test


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };



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

                if(user_id == msg_user_id && not_on_server_indices.size() > 0) {
                    /** TODO: remove assumption that messages are received in order
                     * Proper way is to sort messages by their IDs in ascending order
                     **/

                    ChatActivity_backup.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MessageAdapter.MessageItem msg_item = (MessageAdapter.MessageItem) adapter.getItem(not_on_server_indices.get(0));
                            msg_item.savedToServer(message_id, datetimeutc);

                            // In case there are messages that were sent to server before this one,
                            // move it to the end of the list.
                            // adapter.moveItemToEndOfList(not_on_server_indices.get(0));

                            listViewMessages.setAdapter(listViewMessages.getAdapter());
                            not_on_server_indices.remove(0);
                            Play_Song_in_message();

                            // send notification ....
                            try {

                                System.out.println("send notification ...");
                                FCM_Message_Sender.sendWithOtherThread("token",
                                        TokenFCM_OtherUser,
                                        "Message",
                                        FirebaseAuth.getInstance().getUid(),
                                        room_name,
                                        image_url,
                                        getDateAndTime(),
                                        "room_disable",
                                        msg_item.getMessage().trim());

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }
                    });
                } else {
                    final MessageAdapter.MessageItem msgItem = new MessageAdapter.MessageItem(message_id, user_id, msg_username, message_contents, datetimeutc);

                    ChatActivity_backup.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.addItem(msgItem);
                            Play_Song_in_message();



                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private final Emitter.Listener onHistory = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject json_global;
            JSONArray arr;

            try{

                Log.i("ChatActivity", "receiving message history");
                final ArrayList<Object> items_before = new ArrayList<>();
                final ArrayList<Object> items_after = new ArrayList<>();

                json_global = (JSONObject) args[0];
                arr = json_global.getJSONArray("messages");
                JSONObject jsonObject;
                int messageID;


                for (int i = 0; i < arr.length(); i++) {
                    jsonObject = arr.getJSONObject(i);
                    JSONObject json;
                    final int msg_user_id, message_id;
                    final String msg_username, message_contents, datetimeutc;
                    try {
                        json =  jsonObject;

                        msg_user_id = json.getInt("user_id");
                        message_id = json.getInt("message_id");
                        msg_username = json.getString("username");
                        message_contents = json.getString("message");
                        datetimeutc = json.getString("datetimeutc");

                        if(message_id > Lastmsg_Id){


                            // save message  on local  database  for offline use  ...
                            new Save_offline_MessageTask (json).execute();

                            if(user_id == msg_user_id && not_on_server_indices.size() > 0) {
                                /** TODO: remove assumption that messages are received in order
                                 * Proper way is to sort messages by their IDs in ascending order
                                 **/

                                ChatActivity_backup.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MessageAdapter.MessageItem msg_item = (MessageAdapter.MessageItem) adapter.getItem(not_on_server_indices.get(0));
                                        msg_item.savedToServer(message_id, datetimeutc);

                                        // In case there are messages that were sent to server before this one,
                                        // move it to the end of the list.
                                        // adapter.moveItemToEndOfList(not_on_server_indices.get(0));

                                        listViewMessages.setAdapter(listViewMessages.getAdapter());
                                        not_on_server_indices.remove(0);
                                        Play_Song_in_message();
                                    }
                                });
                            } else {
                                final MessageAdapter.MessageItem msgItem = new MessageAdapter.MessageItem(message_id, user_id, msg_username, message_contents, datetimeutc);

                                ChatActivity_backup.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.addItem(msgItem);
                                        Play_Song_in_message();
                                    }
                                });
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }catch (Exception ex){
                ex.printStackTrace();
            }

      }
    };


    public  void call_infos_notification(final Context context_call ,final  String CallType){


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

        // create jsonObject  ...
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_id", pushId_callRoom);
            jsonObject.put("id_caller", currentUserId);
            jsonObject.put("id_receiver", Config.Chat_Activity_otherUserId);
            jsonObject.put("timestamp", ServerValue.TIMESTAMP);
            jsonObject.put("available_caller", true);
            jsonObject.put("available_receiver", false);
            jsonObject.put("room_status", true); //
            jsonObject.put("call_type", CallType);
            jsonObject.put("reason_interrupted_call", " -- ");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        // start ac call  ...
        try{
            new Start_Call_AsyncTask(jsonObject).execute();

        }catch (Exception ex){
            ex.printStackTrace();
        }

    }


    public class Start_Call_AsyncTask extends AsyncTask<String, String, JSONObject> {

        JSONObject jsonObject_local;

        public Start_Call_AsyncTask( JSONObject jsonObject) {
            //this.roomsFragment = roomsFragment;
            this.jsonObject_local = jsonObject;
        }


        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject json_objet = null;

            try{

                String Server_url_api_start_call = (Config.URL_CHAT_SERVER.trim()+"/Start_Call").trim();
                json_objet = (new com.android.gudana.chat.network.JSONParser()).getJSONFromUrl(Server_url_api_start_call, jsonObject_local);

            }catch (Exception ex){
                ex.printStackTrace();
            }

            return json_objet;

        }

        @Override
        protected void onPostExecute(final JSONObject jsonObject_result) {

            try{


                if(jsonObject_result == null) {

                    Log.d("receive ", "onPostExecute: ");
                    System.out.println(jsonObject_result);

                }else{

                    // get errors

                    Boolean  Response = jsonObject_result.getBoolean("created");
                    String index_call_rethink_db = jsonObject_result.getString("index");
                    String Call_id_rethink_db = jsonObject_result.getString("call_id");
                    if(Response && Response != null && index_call_rethink_db != null && Call_id_rethink_db != null){
                        // than
                        Log.d("receive ", "onPostExecute: ");
                        System.out.println(jsonObject_result);
                        this.jsonObject_local.put("index_call", index_call_rethink_db);
                        this.jsonObject_local.put("call_id_rethink", Call_id_rethink_db);

                        try{

                            // add call_id and send id  to jsonresponse


                            String CallType = this.jsonObject_local.getString("call_type");
                            String pushId_callRoom = this.jsonObject_local.getString("room_id");
                            String currentUserId = this.jsonObject_local.getString("id_caller");

                            if (CallType.equalsIgnoreCase("video")){

                                // video call //

                                // start a call pushId_callRoom

                                callmelder_notification = true;  // send a notification for  to registread the call
                                call_type = "video";
                                Intent intentvideo = new Intent(ChatActivity_backup.this.context, ConnectActivity.class);
                                intentvideo.putExtra("vid_or_aud", call_type);
                                intentvideo.putExtra("user_id", currentUserId);
                                intentvideo.putExtra("room_id", index_call_rethink_db);
                                // intentvideo.putExtra("room_id", pushId_callRoom);
                                startActivity(intentvideo);

                            }else {
                                // audio call

                                callmelder_notification = true;  // send a notification for  to registread the call
                                // make web rtc call
                                call_type = "audio";
                                Intent intentaudio = new Intent(ChatActivity_backup.this.context, ConnectActivity.class);
                                ConnectActivity.received_call = "caller";
                                intentaudio.putExtra("vid_or_aud", call_type);
                                intentaudio.putExtra("user_id", currentUserId);
                                intentaudio.putExtra("room_id", index_call_rethink_db);
                                startActivity(intentaudio);

                            }

                            // send notification ....

                            FCM_Message_Sender.sendWithOtherThread("token" ,
                                    TokenFCM_OtherUser ,
                                    "call" ,
                                    currentUserId ,
                                    NameCurrentUser,
                                    url_Icon_currentUser,
                                    getDateAndTime(),
                                    index_call_rethink_db,
                                    this.jsonObject_local.toString());



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

                                        Toasty.info(ChatActivity_backup.this, "Initialisation with GuDana Voice Cloud successful ", Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Toasty.error(ChatActivity_backup.this, "sorry GuDana Voice Cloud  is unreachable right now ! .... please try again later ", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });


                        }catch (Exception ex){
                            Toasty.error(ChatActivity_backup.this, "Voice  Server Unreachable when trying to call ! ", Toast.LENGTH_SHORT).show();
                            ex.printStackTrace();
                        }


                    }else{

                        Toasty.error(ChatActivity_backup.this, "Voice  Server Unreachable when trying to call ! ", Toast.LENGTH_SHORT).show();
                    }



                }

            }catch (Exception ex){
                Toasty.error(ChatActivity_backup.this, "Voice  Server Unreachable when trying to call ! ", Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }

        }
    }


    // reset call
    public static class Reset_Call extends AsyncTask<String, String, JSONObject> {

        JSONObject jsonObject_local;

        public Reset_Call( JSONObject jsonObject) {
            //this.roomsFragment = roomsFragment;
            this.jsonObject_local = jsonObject;
        }


        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            try{
                String Server_url_api_start_call = (Config.URL_CHAT_SERVER.trim()+"/Reset_Call").trim();
                jsonObject = (new com.android.gudana.chat.network.JSONParser()).getJSONFromUrl(Server_url_api_start_call, jsonObject_local);
            }catch (Exception ex){
                ex.printStackTrace();
            }

            return  jsonObject;
        }

        @Override
        protected void onPostExecute(final JSONObject jsonObject_result) {

            try{

                if(jsonObject_result == null) {

                    Log.d("receive ", "onPostExecute: ");
                    System.out.println(jsonObject_result);

                }else{

                    Boolean  Response = jsonObject_result.getBoolean("reset");
                    if(Response == true && Response != null){
                        try{
                            System.out.println("Call Reseted");

                        }catch (Exception ex){
                            ex.printStackTrace();
                        }

                    }else{
                        System.out.println("voice server  unreachable ...");

                    }

                }

            }catch (Exception ex){
                System.out.println("voice server  unreachable ...");
                ex.printStackTrace();
            }

        }
    }



    public static  void missedCallNotification(final Context context_call , final String CallType ,
                                               final String missedCallerId , final String Room_Id ,
                                               String ClassName_func , final String reason){


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



    // call upload task  ...
    public static void  UploadTask_FileToServer(String fileidentification , ProgressBar Progressbar , ImageView StopUploadTask){
        // new UploadFileToServer_chat(new File(fileidentification) , Config.FILE_UPLOAD_URL,"Voice",Progressbar,StopUploadTask).execute();

    }

    private class SendMessageTask extends AsyncTask<String, String, Void> {
        private final String message_contents;
        //private final String message_raw;

        public SendMessageTask(String message_contents) {
            this.message_contents = stype_of_message + splitter_pattern_message + message_contents;
            // this.message_raw = message_contents;
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
            try {
                System.out.println("Send Notification");
                /*
                FCM_Message_Sender.sendWithOtherThread("token",
                        TokenFCM_OtherUser,
                        "Message",
                        FirebaseAuth.getInstance().getUid(),
                        room_name,
                        image_url,
                        getDateAndTime(),
                        Integer.toString(room_id),
                        this.message_contents );
                        */

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            //Play_Song_out_message();
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
                            Toasty.info(ChatActivity_backup.this, "You have denied some permissions permanently, if the app force close try granting permission from settings.", Toast.LENGTH_LONG).show();
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

        Log.e("request code  ", Integer.toString(requestCode));

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
                    //number_of_files_to_send = docPaths.size();
                    //infos_progress_files.setText(String.valueOf(progressStatus) +"/"+String.valueOf(number_of_files_to_send) + " files");
                    for (String object: docPaths) {
                        System.out.println(object);
                        String[] split = object.split("\\.");
                        String ext = split[split.length - 1];
                        final File file_local =new File(object);
                        String filename=file_local.getName();

                        SimpleDateFormat sdf__date_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date now = new Date();
                        String  call_time = Utility.getAbbreviatedDateTime(now);
                        String msg_uniqueId = UUID.randomUUID().toString();

                        final MessageAdapter.MessageItem msgItem = new MessageAdapter.MessageItem(0, user_id,
                                "###upload_task_to_server###", msg_uniqueId,
                                call_time.toString(), file_local , file_local , true);

                        ChatActivity_backup.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.addItem_and_uploadtask(ChatActivity_backup.this , msgItem);

                            }
                        });

                        // doc_file__upload_images_to_firebase(Uri.fromFile(new File(object.toString())), ext , filename);

                        // new UploadFileToServer_chat(file, Config.FILE_UPLOAD_URL,"Doc",processin).execute();

                    }

                    Log.e("code doc ", "code doc ");

                }
                break;

            case FilePickerConst.REQUEST_CODE_PHOTO:
                if(resultCode== Activity.RESULT_OK && data!=null) {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));


                    //number_of_files_to_send = photoPaths.size();
                    //progress_bar.setMax(number_of_files_to_send);
                    //infos_progress_layout.setVisibility(View.VISIBLE);
                    //progress_bar.setProgress(0);
                    //infos_progress_files.setText(String.valueOf(progressStatus) +"/"+String.valueOf(number_of_files_to_send) + " files");
                    for (String object: photoPaths) {
                        System.out.println(object);
                        String[] split = object.split("\\.");
                        String ext = split[split.length - 1];
                        File local_file =new File(object);
                        String filename=local_file.getName();

                        if(FileOpen.isImagesFile(local_file)){
                            //new UploadFileToServer(imagesList.get(i).getPath(), Config.IMAGES_UPLOAD_URL,finalI).execute();
                            new Compressor(this)
                                    .compressToFileAsFlowable(local_file)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<File>() {
                                        @Override
                                        public void accept(final File file_compressed) {
                                            //compressedImage = file;
                                            // new UploadFileToServer_chat(file, Config.IMAGES_UPLOAD_URL,"Images",processin).execute();

                                            SimpleDateFormat sdf__date_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            Date now = new Date();
                                            String  call_time = Utility.getAbbreviatedDateTime(now);
                                            String msg_uniqueId = UUID.randomUUID().toString();

                                            final MessageAdapter.MessageItem msgItem = new MessageAdapter.MessageItem(0, user_id,
                                                    "image", msg_uniqueId,
                                                    call_time.toString(), local_file ,
                                                    file_compressed , true);

                                            ChatActivity_backup.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    adapter.addItem_and_uploadtask(ChatActivity_backup.this , msgItem);
                                                }
                                            });

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
                            // new UploadFileToServer_chat(file, Config.IMAGES_UPLOAD_URL,"Doc",processin).execute();

                        }

                    }

                    Log.e("code photo ", "code photo ");
                }
                break;
        }

        // Images intent
        try{

            super.onActivityResult(requestCode, resultCode, data);


            // camera only  ...
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

                    File Local_File = new File(uri_img.getPath());
                    //images_upload_images_to_firebase(uri_file , 0);

                    //new UploadFileToServer(imagesList.get(i).getPath(), Config.IMAGES_UPLOAD_URL,finalI).execute();
                    new Compressor(this)
                            .compressToFileAsFlowable(new File(uri_file.getPath()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<File>() {
                                @Override
                                public void accept(final File file_compressed) {
                                    //compressedImage = file;

                                    SimpleDateFormat sdf__date_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date now = new Date();
                                    String  call_time = Utility.getAbbreviatedDateTime(now);
                                    String msg_uniqueId = UUID.randomUUID().toString();

                                    final MessageAdapter.MessageItem msgItem = new MessageAdapter.MessageItem(0, user_id,
                                            "image", msg_uniqueId,
                                            call_time.toString(), Local_File ,
                                            file_compressed , true);

                                    ChatActivity_backup.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.addItem_and_uploadtask(ChatActivity_backup.this, msgItem);
                                        }
                                    });
                                    // new UploadFileToServer_chat(file, Config.IMAGES_UPLOAD_URL,"Images",processin).execute();

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
                            /*
                            FCM_Message_Sender.sendWithOtherThread("token",
                                    TokenFCM_OtherUser,
                                    "Message",
                                    FirebaseAuth.getInstance().getUid(),
                                    room_name,
                                    image_url,
                                    getDateAndTime(),
                                    "room_disable",
                                    msg.trim());
                                    */

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



    public  void images_upload_images_to_server(String  Url_media){

        try{

            String msg = Url_media;
            final String messageId = random().replaceAll("\\s+","").trim();
            stype_of_message = Type_image;
            new SendMessageTask(msg.trim()).execute();


            btnSend.setEnabled(true);
            // send notification ...
            try {

                /*
                FCM_Message_Sender.sendWithOtherThread("token",
                        TokenFCM_OtherUser,
                        "Message",
                        FirebaseAuth.getInstance().getUid(),
                        room_name,
                        image_url,
                        getDateAndTime(),
                        "room_disable",
                        msg.trim());
                        */

            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public void upload_messagev_voice(String uri){

        try{

            String msg = uri;
            stype_of_message = Type_voice;
            new SendMessageTask(msg.trim()).execute();
            // update   ...  progression UI

            btnSend.setEnabled(true);
            try {
                System.out.println("Send Notification");
                /*
                FCM_Message_Sender.sendWithOtherThread("token",
                        TokenFCM_OtherUser,
                        "Message",
                        FirebaseAuth.getInstance().getUid(),
                        room_name,
                        image_url,
                        getDateAndTime(),
                        "room_disable",
                        msg.trim());
                        */

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }catch (Exception ex){
            ex.printStackTrace();

        }
    }

    private void voice_upload_images_to_firebase(File mAudioFile_to_send){

        SimpleDateFormat sdf__date_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String  call_time = Utility.getAbbreviatedDateTime(now);
        String msg_uniqueId = UUID.randomUUID().toString();

        final MessageAdapter.MessageItem msgItem = new MessageAdapter.MessageItem(0, user_id,
                "###upload_task_to_server###", msg_uniqueId, call_time.toString(), mAudioFile_to_send ,
                mAudioFile_to_send , true);

        ChatActivity_backup.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.addItem_and_uploadtask(ChatActivity_backup.this , msgItem);

            }
        });

         //new UploadFileToServer_chat(new File(mFileName) , Config.FILE_UPLOAD_URL,"Voice",processin).execute();
    }

    public void doc_file__upload_to_Server(String uri){

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
                /*
                FCM_Message_Sender.sendWithOtherThread("token",
                        TokenFCM_OtherUser,
                        "Message",
                        FirebaseAuth.getInstance().getUid(),
                        room_name,
                        image_url,
                        getDateAndTime(),
                        "room_disable",
                        msg.trim());
                        */

            } catch (Exception ex) {
                ex.printStackTrace();
            }




        }catch (Exception ex){
            ex.printStackTrace();

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

    private void pickContact(){

        new MultiContactPicker.Builder(ChatActivity_backup.this) //Activity/fragment context
                // .theme(R.style.MyCustomPickerTheme) //Optional - default: MultiContactPicker.Azure
                .hideScrollbar(false) //Optional - default: false
                .showTrack(true) //Optional - default: true
                .searchIconColor(Color.WHITE) //Option - default: White
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                .handleColor(ContextCompat.getColor(ChatActivity_backup.this, R.color.purple)) //Optional - default: Azure Blue
                .bubbleColor(ContextCompat.getColor(ChatActivity_backup.this, R.color.purple)) //Optional - default: Azure Blue
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

    private void startRecording() {

        mAudioFile = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + System.nanoTime() + ".file.m4a");
        mAudioRecorder.prepareRecord(MediaRecorder.AudioSource.MIC,
                MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.AudioEncoder.AAC,
                mAudioFile);

        mAudioRecorder.startRecord();

        record_time_voice.setFormat("Time- %s"); // set the format for a chronometer
        record_time_voice.start();


        /*
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

        */
    }

    private void stopRecording() {


        try{
            if(record_time_voice != null){
                record_time_voice.stop();
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }

        mAudioRecorder.stopRecord();

        if(mAudioFile != null){
            Toasty.info(context, "recorded ", Toast.LENGTH_SHORT).show();
            try {
                voice_upload_images_to_firebase(mAudioFile);
            }catch (Exception ex){
                ex.printStackTrace();
            }

            // play  the file
            Playaudiofile(mAudioFile);

        }else{
            Toasty.error(context, " error file format", Toast.LENGTH_SHORT).show();
        }


    }

    public void Playaudiofile(File audioFile){

        // RxAudioPlayer  mRxAudioPlayer = null;
        PlayConfig ConfigPlayer = PlayConfig.file(audioFile) // play a local file
                //.res(getApplicationContext(), R.raw.audio_record_end) // or play a raw resource
                .looping(true) // loop or not
                .leftVolume(1.0F) // left volume
                .rightVolume(1.0F) // right volume
                .build(); // build this config and play!

        mRxAudioPlayer.play(ConfigPlayer)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(final Disposable disposable) {

                    }

                    @Override
                    public void onNext(final Boolean aBoolean) {
                        // prepared
                    }

                    @Override
                    public void onError(final Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {
                        // play finished
                        // NOTE: if looping, the Observable will never finish, you need stop playing
                        // onDestroy, otherwise, memory leak will happen!
                    }
                });


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


        LinearLayout CloseLayout = (LinearLayout) dialog.findViewById(R.id.closeDialog_layout);
        CloseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealShow(dialogView, false, dialog , Startposition);
            }
        });

        CloseLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                revealShow(dialogView, false, dialog , Startposition);
                return false;
            }
        });

        ImageView live_tracking = (ImageView)dialog.findViewById(R.id.live_tracking);
        live_tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                revealShow(dialogView, false, dialog , Startposition);
                Intent LiveLocationSharing = new Intent(ChatActivity_backup.this , MoD_Live_Location_sharing_Activity.class);
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


                Play_Song_in_message();


                try {

                    /*
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


                try {
                    System.out.println("Send Notification");
                    FCM_Message_Sender.sendWithOtherThread("token",
                            FirebaseInstanceId.getInstance().getToken(),
                            "Friend Request",
                            FirebaseAuth.getInstance().getUid(),
                            room_name,
                            image_url,
                            getDateAndTime(),
                            "room_disable",
                            "you are a hev Friend Request");

                            */

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

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

                //ImagePicker.create(ChatActivity.this).start(); // start image picker activity with request code


                FilePickerBuilder.Companion.getInstance().setMaxCount(15)
                        .setSelectedFiles(docPaths)
                        .enableVideoPicker(true)
                        .enableDocSupport(true)
                        .setActivityTheme(R.style.LibAppTheme)
                        .showGifs(true)
                        .enableSelectAll(true)
                        .showFolderView(true)
                        // .setActivityTheme(R.style.LibAppTheme)
                        .pickPhoto(ChatActivity_backup.this);


            }
        });

        ImageView document = (ImageView)dialog.findViewById(R.id.document);
        document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                revealShow(dialogView, false, dialog , Startposition);


                try {


                    FilePickerBuilder.Companion.getInstance().setMaxCount(10)
                            .setSelectedFiles(docPaths)
                            .setActivityTheme(R.style.LibAppTheme)
                            .sortDocumentsBy(SortingTypes.name)
                            .pickFile(ChatActivity_backup.this);


                    /*
                    FilePickerBuilder.getInstance().setMaxCount(10)
                            .setSelectedFiles(docPaths)
                            .setActivityTheme(R.style.LibAppTheme)
                            .enableVideoPicker(true)
                            .enableDocSupport(true)
                            .showGifs(true)
                            .enableSelectAll(true)
                            .showFolderView(true)
                            // .setActivityTheme(R.style.LibAppTheme)
                            .pickFile(ChatActivity.this);
                            */

                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }
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
                        ImagePicker.cameraOnly().start(ChatActivity_backup.this); // Could be Activity, Fragment, Support Fragment
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
                FilePickerBuilder.Companion.getInstance().setMaxCount(20)
                        .setSelectedFiles(docPaths)
                        .enableVideoPicker(true)
                        .enableDocSupport(true)
                        .showGifs(true)
                        .enableSelectAll(true)
                        .showFolderView(true)
                        // .setActivityTheme(R.style.LibAppTheme)
                        .pickPhoto(ChatActivity_backup.this);
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
                /*
                FCM_Message_Sender.sendWithOtherThread("token",
                        TokenFCM_OtherUser,
                        "Message",
                        FirebaseAuth.getInstance().getUid(),
                        room_name,
                        image_url,
                        getDateAndTime(),
                        "room_disable",
                        msg.trim());
                        */

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
        CountDownTimer mCountDownTimer;
        int i=0;

        dialog.setContentView(dialogView);



        audio_recorder_progress = (ProgressBar) dialog.findViewById(R.id.audio_recorder_progress);
        audio_recorder_progress.setIndeterminate(true);
        // audio_recorder_progress.setProgress(0);

        audio_recorder_progress.setProgress(Increase, true);
        mCountDownTimer=new CountDownTimer(5000,1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.v("Log_tag", "Tick of Progress"+ Increase + millisUntilFinished);
                Increase++;
                audio_recorder_progress.setProgress((int)Increase*100/(5000/1000), true);

            }

            @Override
            public void onFinish() {
                //Do what you want
                Increase++;
                audio_recorder_progress.setProgress(100,true);
            }
        };
        mCountDownTimer.start();



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

                new BottomDialog.Builder(ChatActivity_backup.this)
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

                new BottomDialog.Builder(ChatActivity_backup.this)
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


    private class UploadFileToServer_chat extends AsyncTask<Void, Integer, String> {
        long totalSize = 0;
        private File filePath = null;
        private String Url_Server = null;
        private String UploadType ;
        private String url_file_uploaded;
        private JSONParser jsonParser = new JSONParser();
        private ProgressBar processin_upload;
        String  msg_uniqueId;


        public UploadFileToServer_chat(File filePath , String Url_Server_to_upload , String Type_Upload ,
                                       ProgressBar process_upload , ImageView Stop_uploadTask) {
            super();
            this.filePath = filePath;
            this.Url_Server = Url_Server_to_upload;
            this.UploadType = Type_Upload;

            this.processin_upload = process_upload;
            // do stuff
        }

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero

            // update recyclerview  ...

            processin.setMax(100);
            processin.setProgress(0);

            msg_uniqueId = UUID.randomUUID().toString();
            ChatActivity_backup.data_processing.setVisibility(View.VISIBLE);
            processin.setVisibility(View.VISIBLE);
            processin.setIndeterminate(true);

            // Initrecycler view
            Date currentTime = Calendar.getInstance().getTime();

            final MessageAdapter.MessageItem msgItem = new MessageAdapter.MessageItem(0, user_id, "###upload_task_to_server###", msg_uniqueId, currentTime.toString());

            ChatActivity_backup.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.addItem(msgItem);
                    Play_Song_in_message();
                }
            });


            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(final Integer... progress) {
            // Making progress bar visible
            /*
            runOnUiThread(new Runnable() {
                public void run() {



                    System.out.println(msg_uniqueId +" update  : "+ progress[0]);
                    // updating progress bar value
                    processin_upload.setProgress(progress[0]);
                    // Do stuff…
                }
            });

             */

            processin.post(new Runnable() {
                @Override
                public void run() {
                    if(progress[0]> 0){
                        processin.setIndeterminate(false);
                    }
                    //System.out.println(msg_uniqueId +" update  : "+ progress[0]);
                    processin.setProgress(progress[0]);
                }
            });

        }

        @Override
        protected String doInBackground(Void... params) {

            // always  chek Internet connection
            if(NetworkUtil.isNetworkAvailable(ChatActivity_backup.this)){

                // network is avaiilable true
                return uploadFile();

            }else{
                // return  false network ist not available ...s
                Toasty.error(context, "please check  your internet connection ! ", Toast.LENGTH_SHORT).show();
                return null;
            }


        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            try{


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


            }catch (Exception ex){
                Toasty.error(context, "Error  while sending a file  ", Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }
            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("PostImagesClasses", "Response from server: " + result);
            JSONObject json_data = null;

            try {
                // update view on recyclerview  and wait incomming message  ....
                // create uuid for every message  and file uploaded  ...
                // and after have a aknowlage fpr  message update the view ...  ...

                 String  msg_uniqueId = UUID.randomUUID().toString();


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
                                //images_upload_images_to_firebase(url_file_uploaded);
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
        ChatActivity_backup.data_processing.setVisibility(View.VISIBLE);

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
                            ChatActivity_backup.data_processing.setVisibility(View.GONE);
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

    public void Play_Song_out_message() {

        try {

            mediaPlayer_song_out.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public  void Play_Song_in_message() {

        try {

            mediaPlayer_song_in.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
