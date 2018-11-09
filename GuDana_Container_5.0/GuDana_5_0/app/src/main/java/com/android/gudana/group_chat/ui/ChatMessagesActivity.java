package com.android.gudana.group_chat.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.chatapp.activities.FullScreenActivity;
import com.android.gudana.chatapp.utils.FileOpen;
import com.android.gudana.fcm.CustomFcm_Util;
import com.android.gudana.group_chat.utils.OpenNavi;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.android.gudana.R;
import com.android.gudana.group_chat.model.Message;
import com.android.gudana.group_chat.model.User;
import com.android.gudana.group_chat.utils.Constants;
import com.android.gudana.group_chat.utils.EmailEncoding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;
import com.nightonke.boommenu.Animation.BoomEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import es.dmoral.toasty.Toasty;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import static com.mikepenz.iconics.Iconics.TAG;
//import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ChatMessagesActivity extends AppCompatActivity implements  View.OnClickListener{

    private String messageId;
    private EmojiconEditText mMessageField;
    private Button mSendButton;
    private String chatName;
    private ListView mMessageList;
    //private Toolbar mToolBar;
    private String currentUserEmail;
    private EmojIconActions emojIcon;

    // boom menu
    private BoomMenuButton bmb ;

    private int number_of_files_to_send = 0;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessageDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;
    private FirebaseListAdapter<Message> mMessageListAdapter;
    private FirebaseAuth mFirebaseAuth;
    public static CustomFcm_Util FCM_Message_Sender ;


    private ImageButton mphotoPickerButton;
    private static final int GALLERY_INTENT=2;
    private StorageReference mStorage;
    private ProgressDialog mProgress;

    private ImageButton mrecordVoiceButton;
    private TextView mRecordLable;

    private MediaRecorder mRecorder;
    private String mFileName = null;

    private static final String LOG_TAG = "Record_log";
    private ValueEventListener mValueEventListener;


    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int PLACE_PICKER_REQUEST = 3;
    private static final int CONTACT_PICKER_REQUEST = 23 ;



    public static Chronometer record_time_voice ;

    private boolean activate_correspondant_sound = false;



    //File
    private File filePathImageCamera;

    String filePath = Environment.getExternalStorageDirectory() + "/recorded_audio.wav";
    //String filePaths_doc =  Environment.getExternalStorageDirectory().getPath();
    private ArrayList<String> docPaths = new ArrayList<>();
    private ArrayList<String> photoPaths = new ArrayList<>();
    private static final int REQUEST = 112;

    //Audio Runtime Permissions
    private boolean permissionToRecordAccepted = false;
    private boolean permissionToWriteAccepted = false;
    private String [] permissions = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private ImageView btEmoji;
    private ProgressBar update_work_background ;
    private MediaPlayer mediaPlayer_song_out;
    private MediaPlayer mediaPlayer_song_in;
    private String Chat_uid;
    private String Chat_picture;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 200:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                permissionToWriteAccepted  = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) ChatMessagesActivity.super.finish();
        if (!permissionToWriteAccepted ) ChatMessagesActivity.super.finish();

    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p3_group_chat_messages);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle(chatName);


        Intent intent = this.getIntent();
        //MessageID is the location of the messages for this specific chat
        messageId = intent.getStringExtra(Constants.MESSAGE_ID);
        chatName = intent.getStringExtra(Constants.CHAT_NAME);
        Chat_uid = intent.getStringExtra(Constants.CHAT_UID);
        Chat_picture = intent.getStringExtra(Constants.picture_chat_url);
        actionBar.setTitle(chatName);

        if(messageId == null || Chat_uid == null){
            finish(); // replace this.. nav user back to home
            return;
        }
        // set picture  chat group   ..


        FCM_Message_Sender = new CustomFcm_Util();

        mediaPlayer_song_out = MediaPlayer.create(ChatMessagesActivity.this, R.raw.stairs);
        mediaPlayer_song_in = MediaPlayer.create(ChatMessagesActivity.this, R.raw.relentless);

        //Check Permissions at runtime
        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }


        update_work_background = (ProgressBar) findViewById(R.id.update_work);
        mMessageList = (ListView) findViewById(R.id.messageListView);
        // mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mMessageField = (EmojiconEditText)findViewById(R.id.messageToSend);
        mSendButton = (Button)findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUserEmail = EmailEncoding.commaEncodePeriod(mFirebaseAuth.getCurrentUser().getEmail());
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child(Constants.USERS_LOCATION);
        mMessageDatabaseReference = mFirebaseDatabase.getReference().child(Constants.MESSAGE_LOCATION
                + "/" + messageId);

        // mToolBar.setTitle(chatName);
        showMessages();
        addListeners();
        //openImageSelector();
        openVoiceRecorder();


        try{
            //bmb.setDraggable(true);
            // boom menu    ...
            bmb = (BoomMenuButton) findViewById(R.id.bmb);
            assert bmb != null;
            bmb.setButtonEnum(ButtonEnum.TextOutsideCircle);
            bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_5_4);
            bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_5_4);
            bmb.setBoomEnum(BoomEnum.values()[6]); // random  boom
            bmb.setUse3DTransformAnimation(true);
            bmb.setDuration(500);
            bmb.clearBuilders();

            // first
            TextOutsideCircleButton.Builder builder_0_doc = new TextOutsideCircleButton.Builder()
                    .normalImageRes(R.mipmap.ic_doc_round)
                    .normalText("document")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {

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
                                    .pickFile(ChatMessagesActivity.this);
                        }
                    });

            bmb.addBuilder(builder_0_doc);


            // first
            TextOutsideCircleButton.Builder builder_0_video = new TextOutsideCircleButton.Builder()
                    .normalImageRes(R.mipmap.ic_gallery_round)
                    .normalText("Gallery")
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {

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
                                    .pickPhoto(ChatMessagesActivity.this);
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
                                    ImagePicker.cameraOnly().start(ChatMessagesActivity.this); // Could be Activity, Fragment, Support Fragment

                        }
                    });

            bmb.addBuilder(builder_1_Camera);


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


        // Checking if root layout changed to detect soft keyboard

        final CoordinatorLayout root = findViewById(R.id.chat_root);
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            int previousHeight = root.getRootView().getHeight() - root.getHeight() - mMessageList.getHeight();

            @Override
            public void onGlobalLayout()
            {
                int height = root.getRootView().getHeight() - root.getHeight() - mMessageList.getHeight();

                if(previousHeight != height)
                {
                    if(previousHeight > height)
                    {
                        previousHeight = height;
                    }
                    else if(previousHeight < height)
                    {
                        mMessageList.smoothScrollToPosition(mMessageList.getAdapter().getCount()-1);
                        // recyclerView.scrollToPosition(messagesList.size() - 1);

                        previousHeight = height;
                    }
                }
            }
        });


        btEmoji = (ImageView)findViewById(R.id.buttonEmoji);
        btEmoji = (ImageView)findViewById(R.id.buttonEmoji);
        emojIcon = new EmojIconActions(this,root,  mMessageField,btEmoji);
        emojIcon.ShowEmojIcon();

        // ######################### /// ###########

        // disable  uri exposure stacoverflow  ...
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        askPermission();
        mProgress = new ProgressDialog(this);

        // hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    // pick contact  ...
    private void pickContact(){

        new MultiContactPicker.Builder(ChatMessagesActivity.this) //Activity/fragment context
                // .theme(R.style.MyCustomPickerTheme) //Optional - default: MultiContactPicker.Azure
                .hideScrollbar(false) //Optional - default: false
                .showTrack(true) //Optional - default: true
                .searchIconColor(Color.WHITE) //Option - default: White
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                .handleColor(ContextCompat.getColor(ChatMessagesActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                .bubbleColor(ContextCompat.getColor(ChatMessagesActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                .bubbleTextColor(Color.WHITE) //Optional - default: White
                .showPickerForResult(CONTACT_PICKER_REQUEST);
    }

    // location intent    ...
    private void locationPlacesIntent(){
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMessageListAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMessageListAdapter.stopListening();
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
                            Toast.makeText(ChatMessagesActivity.this, "You have denied some permissions permanently, if the app force close try granting permission from settings.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();

    }

    //Add listener for on completion of image selection
    public void openImageSelector(){
        mphotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
        mphotoPickerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group_chat, menu);
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
                ChatMessagesActivity.this.finish();
                // NavUtils.navigateUpFromSameTask(this);
                return true;


            case R.id.exit_group:

                Toasty.info(this, "Feature not yet implemented... ", Toast.LENGTH_SHORT).show();
                // NavUtils.navigateUpFromSameTask(this);
                break;

            case R.id.search_in_group:
                Toasty.info(this, "Feature not yet implemented... ", Toast.LENGTH_SHORT).show();

                // NavUtils.navigateUpFromSameTask(this);
                break;

            case R.id.group_info:

                Intent intent = new Intent(ChatMessagesActivity.this, ManageGroupActivity.class);
                intent.putExtra(Constants.CHAT_NAME, chatName);
                intent.putExtra(Constants.CHAT_UID, Chat_uid);
                startActivity(intent);
                break;


        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed()
    {
        ChatMessagesActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data){

        // imported from CreateGroupChatActivity  ....

        mStorage = FirebaseStorage.getInstance().getReference(); //make global
        Log.e("jhkhj", "jhkjh");

        switch (requestCode) {

            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));


                    number_of_files_to_send = docPaths.size();
                    //progress_bar.setMax(number_of_files_to_send);
                    //infos_progress_layout.setVisibility(View.VISIBLE);
                    //progress_bar.setProgress(0);
                    //infos_progress_files.setText(String.valueOf(progressStatus) +"/"+String.valueOf(number_of_files_to_send) + " files");
                    for (String object: docPaths) {
                        System.out.println(object);
                        String[] split = object.split("\\.");
                        String ext = split[split.length - 1];
                        File file =new File(object);
                        String filename=file.getName();

                        addDocToMessages_upload(Uri.fromFile(new File(object.toString())) , filename+"."+ext);
                        //doc_file__upload_images_to_firebase(Uri.fromFile(new File(object.toString())), ext , filename);

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
                    //infos_progress_files.setText(String.valueOf(progressStatus) +"/"+String.valueOf(number_of_files_to_send) + " files");
                    for (String object: photoPaths) {
                        System.out.println(object);
                        String[] split = object.split("\\.");
                        String ext = split[split.length - 1];
                        File file =new File(object);
                        String filename=file.getName();

                        final String imageLocation = "Photos" + "/" + messageId;
                        //final String imageLocationId = imageLocation + "/" + uri.getLastPathSegment();
                        final String uniqueId = UUID.randomUUID().toString();
                        final StorageReference filepath = mStorage.child(imageLocation).child(uniqueId + "/image_message");
                        final String downloadURl = filepath.getPath();
                        update_work_background.setVisibility(View.VISIBLE);
                        filepath.putFile(Uri.fromFile(new File(object.toString()))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //create a new message containing this image
                                addImageToMessages(downloadURl);
                                //mProgress.dismiss();

                            }
                        });


                        UploadTask uploadTask = filepath.putFile(Uri.fromFile(new File(object.toString())));
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return filepath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    addImageToMessages(downloadUri.toString());

                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }
                        });

                        //doc_file__upload_images_to_firebase(Uri.fromFile(new File(object.toString())), ext , filename);

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
                update_work_background.setVisibility(View.VISIBLE);
                for (Image uri_img : images) {
                    Uri uri_file = Uri.fromFile(new File(uri_img.getPath()));
                    //images_upload_images_to_firebase(uri_file);
                    // send  taked photo ...
                    //Uri uri = data.getData();
                    //Keep all images for a specific chat grouped together
                    final String imageLocation = "Photos" + "/" + messageId;
                    final String uniqueId = UUID.randomUUID().toString();
                    final StorageReference filepath = mStorage.child(imageLocation).child(uniqueId + "/image_message");

                    UploadTask uploadTask = filepath.putFile(uri_file);
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return filepath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                addImageToMessages(downloadUri.toString());

                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });

                }

            }

            // contact  Upload

            if(requestCode == CONTACT_PICKER_REQUEST){
                if(resultCode == RESULT_OK) {
                    List<ContactResult> results = MultiContactPicker.obtainResult(data);
                    int i = 0;
                    do{

                        // sendMessage_location_contact("text" , results.get(i).getDisplayName()+ " : " + results.get(i).getPhoneNumbers());
                        sendMessage_contact(results.get(i).getDisplayName()+ "  Tel: " + results.get(i).getPhoneNumbers());
                        ContactResult element = results.get(i);
                        i++;
                    } while (i < results.size());

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
                        sendMessage_location(latLng.latitude+ ":"+latLng.longitude);
                        // sendMessage_location_contact("location" , latLng.latitude+":"+latLng.longitude);
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

    public void openVoiceRecorder(){
        //Implement voice selection
        mrecordVoiceButton =(ImageButton) findViewById(R.id.recordVoiceButton);
        mRecordLable = (TextView) findViewById(R.id.recordLable);

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/recorded_audio.3gp";

        mrecordVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiag(mrecordVoiceButton);
            }
        });

        //on complete: sendVoice()
    }

    private void startRecording() {

        mRecorder = new MediaRecorder();
        try{

            // start Chronometer  ...

            // start chronometer  ....
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
        try {


            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            record_time_voice.stop();
            uploadAudio();
        }catch (Exception ex){
            ex.printStackTrace();
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

    private void uploadAudio() {

        mStorage = FirebaseStorage.getInstance().getReference();
        Uri uri = Uri.fromFile(new File(mFileName));
        //Keep all voice for a specific chat grouped together
        final String voiceLocation = "Voice" + "/" + messageId;
        final String voiceLocationId = voiceLocation + "/" + uri.getLastPathSegment();
        final String uniqueId = UUID.randomUUID().toString();
        final StorageReference filepath = mStorage.child(voiceLocation).child(uniqueId + "/audio_message.3gp");
        final String downloadURl = filepath.getPath();

        update_work_background.setVisibility(View.VISIBLE);
        UploadTask uploadTask = filepath.putFile(uri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return filepath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    addVoiceToMessages(downloadUri.toString());

                    //generale_senderToMessages(downloadUri.toString());

                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    public void addListeners(){
        mMessageField.addTextChangedListener(new TextWatcher() {
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
        Message message =
                new Message(EmailEncoding.commaEncodePeriod(mFirebaseAuth.getCurrentUser().getEmail()),
                        "Message: Voice Sent", "VOICE", voiceLocation, timestamp);
        //Create HashMap for Pushing
        HashMap<String, Object> messageItemMap = new HashMap<String, Object>();
        HashMap<String,Object> messageObj = (HashMap<String, Object>) new ObjectMapper()
                .convertValue(message, Map.class);
        messageItemMap.put("/" + pushKey, messageObj);
        mMessageDatabaseReference.updateChildren(messageItemMap)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Play_Song_out_message();
                        update_work_background.setVisibility(View.GONE);
                    }
                });
    }

    //  doc   send
    public void addDocToMessages_upload(Uri uri , String filename){

        mStorage = FirebaseStorage.getInstance().getReference();
        // Uri uri = Uri.fromFile(new File(mFileName));
        //Keep all voice for a specific chat grouped together
        final String voiceLocation = "group_doc" + "/" + messageId;
        final String uniqueId = UUID.randomUUID().toString();
        final StorageReference filepath = mStorage.child(voiceLocation).child(uniqueId + "/"+filename);
        final String downloadURl = filepath.getPath();

        update_work_background.setVisibility(View.VISIBLE);

        /*
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // than send notification to the users s

                generale_senderToMessages(downloadURl);

            }
        });
        */


        UploadTask uploadTask = filepath.putFile(uri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return filepath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    generale_senderToMessages(downloadUri.toString());

                } else {
                    // Handle failures
                    // ...
                }
            }
        });

    }


    public void generale_senderToMessages(String contentlocation_storage){

        final DatabaseReference pushRef = mMessageDatabaseReference.push();
        final String pushKey = pushRef.getKey();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String timestamp = dateFormat.format(date);
        //Create message object with text/voice etc
        Message message =
                new Message(EmailEncoding.commaEncodePeriod(mFirebaseAuth.getCurrentUser().getEmail()),
                        "Document", "document", contentlocation_storage, timestamp);
        //Create HashMap for Pushing
        HashMap<String, Object> messageItemMap = new HashMap<String, Object>();
        HashMap<String,Object> messageObj = (HashMap<String, Object>) new ObjectMapper()
                .convertValue(message, Map.class);
        messageItemMap.put("/" + pushKey, messageObj);
        mMessageDatabaseReference.updateChildren(messageItemMap)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Play_Song_out_message();
                        update_work_background.setVisibility(View.GONE);
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
        Message message =
                new Message(EmailEncoding.commaEncodePeriod(mFirebaseAuth.getCurrentUser().getEmail()),
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
                        Play_Song_out_message();
                        update_work_background.setVisibility(View.GONE);
                    }
                });
    }

    // sendMessage_contact

    public void sendMessage_contact(String msg){

        //final DatabaseReference messageRef = mFirebaseDatabase.getReference(Constants.MESSAGE_LOCATION);
        final DatabaseReference pushRef = mMessageDatabaseReference.push();
        final String pushKey = pushRef.getKey();

        update_work_background.setVisibility(View.VISIBLE);
        if(msg.isEmpty()){
            Toasty.info(getApplicationContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();

        }else {

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date();
            String timestamp = dateFormat.format(date);
            //Create message object with text/voice etc
            Message message = new Message(EmailEncoding.commaEncodePeriod(mFirebaseAuth.getCurrentUser().getEmail()), msg, timestamp);
            //Create HashMap for Pushing
            HashMap<String, Object> messageItemMap = new HashMap<String, Object>();
            HashMap<String,Object> messageObj = (HashMap<String, Object>) new ObjectMapper()
                    .convertValue(message, Map.class);
            messageItemMap.put("/" + pushKey, messageObj);
            // reset message fiels
            //play song
            mMessageDatabaseReference.updateChildren(messageItemMap)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Play_Song_out_message();
                            update_work_background.setVisibility(View.GONE);
                            // mMessageField.setText("");
                        }
                    });
        }

    }


    public void sendMessage(){

        //final DatabaseReference messageRef = mFirebaseDatabase.getReference(Constants.MESSAGE_LOCATION);
        final DatabaseReference pushRef = mMessageDatabaseReference.push();
        final String pushKey = pushRef.getKey();

        update_work_background.setVisibility(View.VISIBLE);
        String messageString = mMessageField.getText().toString();
        if(messageString.isEmpty()){
            Toasty.info(getApplicationContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();

        }else {

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date();
            String timestamp = dateFormat.format(date);
            //Create message object with text/voice etc
            Message message = new Message(EmailEncoding.commaEncodePeriod(mFirebaseAuth.getCurrentUser().getEmail()), messageString, timestamp);
            //Create HashMap for Pushing
            HashMap<String, Object> messageItemMap = new HashMap<String, Object>();
            HashMap<String,Object> messageObj = (HashMap<String, Object>) new ObjectMapper()
                    .convertValue(message, Map.class);
            messageItemMap.put("/" + pushKey, messageObj);
            // reset message fiels
            mMessageField.setText("");
            //play song
            mMessageDatabaseReference.updateChildren(messageItemMap)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Play_Song_out_message();
                            update_work_background.setVisibility(View.GONE);
                            // mMessageField.setText("");
                        }
                    });
        }

    }

    public void sendMessage_location(String MsgLatLong){
        update_work_background.setVisibility(View.VISIBLE);
        //final DatabaseReference messageRef = mFirebaseDatabase.getReference(Constants.MESSAGE_LOCATION);
        final DatabaseReference pushRef = mMessageDatabaseReference.push();
        final String pushKey = pushRef.getKey();

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date();
            String timestamp = dateFormat.format(date);
            //Create message object with text/voice etc
            Message message = new Message(EmailEncoding.commaEncodePeriod(mFirebaseAuth.getCurrentUser().getEmail()), "location", MsgLatLong,timestamp);
            //Create HashMap for Pushing
            HashMap<String, Object> messageItemMap = new HashMap<String, Object>();
            HashMap<String,Object> messageObj = (HashMap<String, Object>) new ObjectMapper()
                    .convertValue(message, Map.class);
            messageItemMap.put("/" + pushKey, messageObj);
            // reset message fiels
            //play song
            mMessageDatabaseReference.updateChildren(messageItemMap)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Play_Song_out_message();
                            update_work_background.setVisibility(View.GONE);

                            // mMessageField.setText("");
                        }
                    });


    }

    private void showMessages() {

        FirebaseListOptions<Message> options = new FirebaseListOptions.Builder<Message>()
                .setQuery(mMessageDatabaseReference, Message.class)
                .setLayout(R.layout.p3_message_item)
                .build();

        mMessageListAdapter = new FirebaseListAdapter<Message>(options) {
            @Override
            protected void populateView(final View view, final Message message, final int position) {
                LinearLayout messageLine = (LinearLayout) view.findViewById(R.id.messageLine);
                TextView messgaeText = (TextView) view.findViewById(R.id.messageTextView);
                TextView senderText = (TextView) view.findViewById(R.id.senderTextView);
                //TextView timeTextView = (TextView) view.findViewById(R.id.timeTextView);
                final CircleImageView leftImage = (CircleImageView) view.findViewById(R.id.leftMessagePic);
                final CircleImageView rightImage = (CircleImageView) view.findViewById(R.id.rightMessagePic);
                LinearLayout individMessageLayout = (LinearLayout)view.findViewById(R.id.individMessageLayout);
                final TextView timeTextView = (TextView) view.findViewById(R.id.timespan);

                LinearLayout voicemessageLayout = (LinearLayout) view.findViewById(R.id.voice_message_layout);
                LinearLayout wait_voice_media_Layout = (LinearLayout) view.findViewById(R.id.voice_download);
                TextView senderName_time_send = (TextView)  view.findViewById(R.id.senderName);
                final TextView download_voice_hint = (TextView) view.findViewById(R.id.download_voicemes);

                // init  voice player ...
                final ImageView buttonPlayStop = (ImageView) view.findViewById(R.id.ButtonPlayStop);
                final Handler handler = new Handler();
                final SeekBar seekBar_view = (SeekBar) view.findViewById(R.id.SeekBar_view);
                download_voice_hint.setText("");

                String time = message.getTimestamp();
                try{
                    // getTimeAgo(Long.parseLong(time)); // and play song  for incomming message  ...
                    if(time != null && time != "" ) {
                        String ampm = "A.M.";
                        String hours = time.substring(0, 2);
                        String minutes = time.substring(3, 5);
                        int numHours = Integer.parseInt(hours);
                        if(numHours == 12){ //if numhours is 12 then its pm
                            ampm = "P.M.";
                        }
                        if (numHours > 12) {
                            numHours -= 12;
                            ampm = "P.M.";
                        }
                        if(numHours == 0){
                            numHours = 12;
                        }
                        hours = Integer.toString(numHours);
                        time = hours + ":" + minutes + " " + ampm;
                    }

                }catch(Exception ex){
                    ex.printStackTrace();
                }
                //Date MessageDate  = TimeSpanConverter.getTimeDate(Long.parseLong(time));
                //Date device_date = new Date();


                timeTextView.setText(time);
                //set message and sender text
                messgaeText.setText(message.getMessage());
                senderText.setText(EmailEncoding.commaDecodePeriod(message.getSender()));
                //If you sent this message, right align
                String mSender = message.getSender();

                if(mSender.equals(currentUserEmail)){
                    //messgaeText.setGravity(Gravity.RIGHT);
                    //senderText.setGravity(Gravity.RIGHT);
                    messageLine.setGravity(Gravity.RIGHT);
                    leftImage.setVisibility(View.GONE);
                    rightImage.setVisibility(View.VISIBLE);

                    messgaeText.setTextColor(getResources().getColor(R.color.white));
                    senderText.setTextColor(getResources().getColor(R.color.white));

                    //profile image back to here
                    mUsersDatabaseReference.child(mSender).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User userInfo = dataSnapshot.getValue(User.class);
                            try{
                                if(userInfo != null && userInfo.getProfilePicLocation() != null){
                                    StorageReference storageRef = FirebaseStorage.getInstance()
                                            .getReference().child(userInfo.getProfilePicLocation());


                                    Glide.with(view.getContext())
                                            .setDefaultRequestOptions(new RequestOptions()
                                                    .circleCrop()
                                                    .centerCrop()
                                                    .fitCenter())
                                            .load(userInfo.getProfilePicLocation())
                                            .into(rightImage);

                                }
                            }catch (Exception e){
                                Log.e("ERR", e.toString());
;                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    individMessageLayout.setBackgroundResource(R.drawable.message_background); // right message
                    //messgaeText.setBackgroundColor(ResourcesCompat.getColor(getResources(),
                    //       R.color.colorAccent, null));
                }else if(mSender.equals("System")){
                    messageLine.setGravity(Gravity.CENTER_HORIZONTAL);
                    leftImage.setVisibility(View.GONE);
                    rightImage.setVisibility(View.GONE);
                }else{

                    // that ist a left  Image ...
                    //messgaeText.setGravity(Gravity.LEFT);
                    //senderText.setGravity(Gravity.LEFT);
                    messageLine.setGravity(Gravity.LEFT);
                    leftImage.setVisibility(View.VISIBLE);
                    rightImage.setVisibility(View.GONE);
                    individMessageLayout.setBackgroundResource(R.drawable.message_background_link);

                    messgaeText.setTextColor(getResources().getColor(R.color.black));
                    senderText.setTextColor(getResources().getColor(R.color.black));
                    //messgaeText.setBackgroundColor(ResourcesCompat.getColor(getResources(),
                    //       R.color.colorPrimary, null));


                    //profile image back to here
                    mUsersDatabaseReference.child(mSender).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User userInfo = dataSnapshot.getValue(User.class);
                            if(userInfo != null && userInfo.getProfilePicLocation() != null){
                                try{
                                    //StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(userInfo.getProfilePicLocation());

                                    Glide.with(view.getContext())
                                            .setDefaultRequestOptions(new RequestOptions()
                                                    .circleCrop()
                                                    .centerCrop()
                                                    .fitCenter())
                                            .load(userInfo.getProfilePicLocation())
                                            .into(leftImage);


                                }catch(Exception e){
                                    Log.e("Err", e.toString());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }


                //If this is multimedia display it
                final ImageView imageView = (ImageView) view.findViewById(R.id.imageMessage);
                final ImageButton activateVoiceMsg = (ImageButton) view.findViewById(R.id.voiceMessageButton);
                if(message.getMultimedia()){
                    if(message.getContentType().equals("IMAGE")) {

                        imageView.setVisibility(View.VISIBLE);
                        activateVoiceMsg.setVisibility(View.GONE);
                        activateVoiceMsg.setImageDrawable(null);

                        Glide.with(view.getContext())
                                .setDefaultRequestOptions(new RequestOptions()
                                        .circleCrop()
                                        .centerCrop()
                                        .fitCenter())
                                .load(message.getContentLocation())
                                .into(imageView);

                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(ChatMessagesActivity.this, FullScreenActivity.class);
                                intent.putExtra("imageUrl", message.getContentLocation());
                                startActivity(intent);
                            }
                        });

                    }

                    if(message.getContentType().equals("location")) {
                        imageView.setVisibility(View.VISIBLE);
                        activateVoiceMsg.setVisibility(View.GONE);
                        activateVoiceMsg.setImageDrawable(null);

                        Glide.with(view.getContext())
                                .setDefaultRequestOptions(new RequestOptions()
                                        .circleCrop()
                                        .centerCrop()
                                        .fitCenter())
                                .load(R.drawable.ic_map_icon)
                                .into(imageView);

                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // make some thing here
                                // call map s loader call navigation  dialog
                                showDiag_navigation(mrecordVoiceButton , message.getLatitude_longitude() );

                            }
                        });
                    }


                    if(message.getContentType().equals("document")) {
                        imageView.setVisibility(View.VISIBLE);
                        activateVoiceMsg.setVisibility(View.GONE);
                        activateVoiceMsg.setImageDrawable(null);

                        Glide.with(view.getContext())
                                .setDefaultRequestOptions(new RequestOptions()
                                        .circleCrop()
                                        .centerCrop()
                                        .fitCenter())
                                .load(R.drawable.icon_file_doc)
                                .into(imageView);

                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // make some thing here
                                // call map s loader
                                //Asynchronously load file as generic file
                                update_work_background.setVisibility(View.VISIBLE);
                                FileLoader.with(ChatMessagesActivity.this)
                                        //.load("https://firebasestorage.googleapis.com/v0/b/gudana-cloud-technology.appspot.com/o/post_video%2F%232GbnW-3null?alt=media&token=d11d6cc2-0068-43b2-a39c-575cba04ea15")
                                        .load(message.getContentLocation())

                                        .fromDirectory("Gudana_dir_files", FileLoader.DIR_EXTERNAL_PRIVATE)
                                        .asFile(new FileRequestListener<File>() {
                                            @Override
                                            public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                                                // Glide.with(MainActivity_GuDDana.this).load(response.getBody()).into(iv);
                                                // open video in cache
                                                try {
                                                    update_work_background.setVisibility(View.GONE);
                                                    File myUriFile = response.getDownloadedFile().getAbsoluteFile();
                                                    FileOpen.openFile(ChatMessagesActivity.this, myUriFile);

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onError(FileLoadRequest request, Throwable t) {
                                                Log.d(TAG, "onError: " + t.getMessage());
                                            }
                                        });

                            }
                        });
                    }


                    if(message.getContentType().equals("VOICE")) {
                        //show play button
                        senderName_time_send.setText(message.getSender()+" "+time);
                        activateVoiceMsg.setVisibility(View.GONE);
                        individMessageLayout.setVisibility(View.GONE);
                        //hide imageviews
                        imageView.setVisibility(View.GONE);
                        imageView.setImageDrawable(null);
                        wait_voice_media_Layout.setVisibility(View.VISIBLE);
                        // set visible the voice layout
                        voicemessageLayout.setVisibility(View.VISIBLE);
                        buttonPlayStop.setFocusable(false);
                        buttonPlayStop.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // je doit finir   les metttres les meme setter dans cett encapsulation



                                download_voice_hint.setText("please  wait ...");
                                // disable clibutton  to avoid twice  parralle download ...
                                buttonPlayStop.setEnabled(false);
                                seekBar_view.setEnabled(false);

                                update_work_background.setVisibility(View.VISIBLE);
                                FileLoader.with(ChatMessagesActivity.this)
                                        //.load("https://firebasestorage.googleapis.com/v0/b/gudana-cloud-technology.appspot.com/o/post_video%2F%232GbnW-3null?alt=media&token=d11d6cc2-0068-43b2-a39c-575cba04ea15")
                                        .load(message.getContentLocation())

                                        .fromDirectory("Gudana_dir_files", FileLoader.DIR_EXTERNAL_PRIVATE)
                                        .asFile(new FileRequestListener<File>() {
                                            @Override
                                            public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                                                // Glide.with(MainActivity_GuDDana.this).load(response.getBody()).into(iv);
                                                // open video in cache
                                                try {
                                                    update_work_background.setVisibility(View.GONE);
                                                    File myUriFile = response.getDownloadedFile().getAbsoluteFile();
                                                    // FileOpen.openFile(ChatMessagesActivity.this, myUriFile);

                                                    download_voice_hint.setText("download complete");

                                                    buttonPlayStop.setEnabled(true);
                                                    seekBar_view.setEnabled(true);
                                                    // init Media player
                                                    final MediaPlayer mediaPlayer = MediaPlayer.create(ChatMessagesActivity.this, Uri.parse(response.getDownloadedFile().getPath()));
                                                    seekBar_view.setMax(mediaPlayer.getDuration());
                                                    seekBar_view.setOnTouchListener(new View.OnTouchListener() {
                                                        @Override public boolean onTouch(View v, MotionEvent event) {
                                                            seekChange(v , mediaPlayer);
                                                            return false; }
                                                    });


                                                    //reset
                                                    if (buttonPlayStop.isFocusable() == false) {
                                                        buttonPlayStop.setBackground(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
                                                        buttonPlayStop.setFocusable(true);
                                                        try{
                                                            mediaPlayer.start();
                                                            startPlayProgressUpdater(buttonPlayStop,mediaPlayer,seekBar_view , handler);
                                                        }catch (IllegalStateException e) {
                                                            mediaPlayer.pause();
                                                        }
                                                    }else {
                                                        buttonPlayStop.setFocusable(false);
                                                        buttonPlayStop.setBackground(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                                                        mediaPlayer.pause();
                                                    }


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onError(FileLoadRequest request, Throwable t) {
                                                Log.d(TAG, "onError: " + t.getMessage());
                                            }
                                        });



                                /*)
                                // the first time download the media on the Server
                                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(message.getContentLocation());
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // playSound(uri);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });

                                */

                            }
                        });
                    }
                }else{
                    activateVoiceMsg.setVisibility(View.GONE);
                    activateVoiceMsg.setImageDrawable(null);
                    imageView.setVisibility(View.GONE);
                    imageView.setImageDrawable(null);
                }
            }
        };
        mMessageList.setAdapter(mMessageListAdapter);
    }

    // audio  lecteur ######################  audio player  ...

    public void startPlayProgressUpdater(final ImageView buttonPlayStop , final MediaPlayer mediaPlayer , final SeekBar seekBar, final Handler handler) {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater(buttonPlayStop,mediaPlayer,seekBar , handler);
                }
            };
            handler.postDelayed(notification,200);
        }else{
            mediaPlayer.pause();
            buttonPlayStop.setBackground(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
            seekBar.setProgress(0);
        }
    }

    // This is event handler thumb moving event
    private void seekChange(View v , MediaPlayer mediaPlayer){
        if(mediaPlayer.isPlaying()){
            SeekBar sb = (SeekBar)v;
            mediaPlayer.seekTo(sb.getProgress());
        }
    }

    //  set focusable   ...

    private void showDiag_navigation(final ImageButton Startposition , final String message_location_parsed) {

        final View dialogView = View.inflate(this,R.layout.dialog_gps_navi_choice,null);

        final Dialog dialog = new Dialog(this,R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(dialogView);
        ImageView imageView = (ImageView)dialog.findViewById(R.id.closeDialogImg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealShow_navigation(dialogView, false, dialog , Startposition);
            }
        });

        // send recorded  voice mail ...
        ImageView  street_view_360 = (ImageView) dialog.findViewById(R.id.street_view_360);
        street_view_360.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealShow_navigation(dialogView, false, dialog , Startposition);
                OpenNavi.Open_Street_View(ChatMessagesActivity.this , message_location_parsed );

            }
        });

        ImageView map_view = (ImageView) dialog.findViewById(R.id.map_view);
        map_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealShow_navigation(dialogView, false, dialog , Startposition);
                OpenNavi.Open_map(ChatMessagesActivity.this , message_location_parsed);
            }
        });


        ImageView navi_view = (ImageView) dialog.findViewById(R.id.navi_view);
        navi_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealShow_navigation(dialogView, false, dialog , Startposition);
                OpenNavi.Open_navi(ChatMessagesActivity.this , message_location_parsed);

            }
        });


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow_navigation(dialogView, true, null , Startposition);
            }
        });


        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK){

                    revealShow_navigation(dialogView, false, dialog , Startposition);
                    return true;
                }

                return false;
            }
        });



        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    private void revealShow_navigation(View dialogView, boolean b, final Dialog dialog , ImageButton atarter_button) {

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

                new BottomDialog.Builder(ChatMessagesActivity.this)
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

                new BottomDialog.Builder(ChatMessagesActivity.this)
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



        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

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
            startRecording();

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

    public void Play_Song_out_message() {

        try {

            mediaPlayer_song_out.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void Play_Song_in_message() {

            try {

                mediaPlayer_song_in.start();

            } catch (Exception ex) {
                ex.printStackTrace();
            }




    }

    private boolean  getTimeAgo(long time)
    {
        boolean response = false;
        final long diff = System.currentTimeMillis() - time;

        if(diff < 3)
        {
           response = true;
           // play song to tell  that it's a new message
            Play_Song_in_message();
        }

        return  response;

    }

    private void SendNotification(String message_to_send){

        // send notification  for other User //...

        FCM_Message_Sender.sendWithOtherThread("token" ,
                "TokenFCM_OtherUser" ,
                "Message",
                FirebaseAuth.getInstance().getUid(),
                chatName,
                "url_Icon_currentUser",
                getDateAndTime(),
                "room_disable",
                message_to_send);
    }

    public static  String getDateAndTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println( sdf.format(cal.getTime()) );

        return  sdf.format(cal.getTime());
    }


    @Override
    public void onClick(View v) {

    }
}