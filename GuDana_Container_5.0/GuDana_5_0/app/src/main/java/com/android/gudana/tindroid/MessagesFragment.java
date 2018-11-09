package com.android.gudana.tindroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.android.gudana.R;
import com.android.gudana.chatapp.activities.ChatActivity;
import com.android.gudana.fcm.CustomFcm_Util;
import com.android.gudana.group_chat.model.Message;
import com.android.gudana.group_chat.ui.ChatMessagesActivity;
import com.android.gudana.group_chat.utils.OpenNavi;
import com.android.gudana.hify.ui.activities.MainActivity_GuDDana;
import com.android.gudana.tindroid.db.BaseDb;
import com.android.gudana.tindroid.db.StoredTopic;
import com.android.gudana.tindroid.media.VxCard;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.firebase.ui.database.FirebaseListAdapter;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nightonke.boommenu.Animation.BoomEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import co.tinode.tinodesdk.ComTopic;
import co.tinode.tinodesdk.LargeFileHelper;
import co.tinode.tinodesdk.NotConnectedException;
import co.tinode.tinodesdk.PromisedReply;
import co.tinode.tinodesdk.Storage;
import co.tinode.tinodesdk.Topic;
import co.tinode.tinodesdk.model.Drafty;
import co.tinode.tinodesdk.model.MsgServerCtrl;
import co.tinode.tinodesdk.model.ServerMessage;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import es.dmoral.toasty.Toasty;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Fragment handling message display and message sending.
 */
public class MessagesFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<MessagesFragment.UploadResult> {
    private static final String TAG = "MessageFragment";

    private static final int MESSAGES_TO_LOAD = 20;
    private static final int ACTION_ATTACH_FILE = 100;
    private static final int ACTION_ATTACH_IMAGE = 101;

    // Maximum size of file to send in-band. 256KB.
    private static final long MAX_INBAND_ATTACHMENT_SIZE = 1 << 17;
    // Maximum size of file to upload. 8MB.
    private static final long MAX_ATTACHMENT_SIZE = 1 << 23;
    private static final int READ_DELAY = 1000;
    private static String Sender_uid = "";
    private static String Name_Sender = "unknow";
    protected ComTopic<VxCard> mTopic;

    private LinearLayoutManager mMessageViewLayoutManager;
    private MessagesListAdapter mMessagesAdapter;
    private SwipeRefreshLayout mRefresher;

    // It cannot be local.
    @SuppressWarnings("FieldCanBeLocal")
    private UploadProgress mUploadProgress;
    private String mTopicName = null;
    private Timer mNoteTimer = null;
    private String mMessageToSend = null;
    private PromisedReply.FailureListener<ServerMessage> mFailureListener;
    private BoomMenuButton bmb ;
    private static final int CONTACT_PICKER_REQUEST = 23 ;
    private static final int PLACE_PICKER_REQUEST = 3;
    private int number_of_files_to_send = 0;
    private ProgressBar update_work_background ;
    private String messageId;
    private static String url_image = "";


    // add
    private EmojiconEditText mMessageField;
    private Button mSendButton;
    private String chatName;
    // private ListView mMessageList;
    //private Toolbar mToolBar;
    private String currentUserEmail;
    private EmojIconActions emojIcon;

    // boom menu
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
    public static Chronometer record_time_voice ;
    private boolean activate_correspondant_sound = false;
    //File
    private File filePathImageCamera;
    String filePath = Environment.getExternalStorageDirectory() + "/voice_mail.wav";
    //String filePaths_doc =  Environment.getExternalStorageDirectory().getPath();
    private ArrayList<String> docPaths = new ArrayList<>();
    private ArrayList<String> photoPaths = new ArrayList<>();
    private static final int REQUEST = 112;
    //Audio Runtime Permissions
    private boolean permissionToRecordAccepted = false;
    private boolean permissionToWriteAccepted = false;
    private String [] permissions = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private ImageView btEmoji;
    public static MediaPlayer mediaPlayer_song_out;
    public static  MediaPlayer mediaPlayer_song_in;
    private String Chat_uid;
    private String Chat_picture;
    MessageActivity activity;
    private RecyclerView ml;
    private static  String  TokenFCM_OtherUser = "";

    private static FirebaseFirestore mFirestore;
    private static FirebaseUser currentUser;


    public MessagesFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.tin_fragment_messages, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        activity = (MessageActivity) getActivity();

        mMessageViewLayoutManager = new LinearLayoutManager(activity) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                // This is a hack for IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionViewHolder
                // It happens when two uploads are started at the same time.
                // See discussion here:
                // https://stackoverflow.com/questions/31759171/recyclerview-and-java-lang-indexoutofboundsexception-inconsistency-detected-in
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {
                    Log.e("probe", "meet a IOOBE in RecyclerView");
                }
            }
        };
        // mMessageViewLayoutManager.setStackFromEnd(true);
        mMessageViewLayoutManager.setReverseLayout(true);

        ml = activity.findViewById(R.id.messages_container);
        ml.setLayoutManager(mMessageViewLayoutManager);

        // Creating a strong reference from this Fragment, otherwise it will be immediately garbage collected.
        mUploadProgress = new UploadProgress();
        // This needs to be rebound on activity creation.
        FileUploader.setProgressHandler(mUploadProgress);

        mRefresher = activity.findViewById(R.id.swipe_refresher);
        mMessagesAdapter = new MessagesListAdapter(activity, mRefresher);
        ml.setAdapter(mMessagesAdapter);
        mRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!mMessagesAdapter.loadNextPage() && !StoredTopic.isAllDataLoaded(mTopic)) {
                    try {
                        mTopic.getMeta(mTopic.getMetaGetBuilder().withGetEarlierData(MESSAGES_TO_LOAD).build())
                                .thenApply(
                                        new PromisedReply.SuccessListener<ServerMessage>() {
                                            @Override
                                            public PromisedReply<ServerMessage> onSuccess(ServerMessage result) {
                                                mRefresher.setRefreshing(false);
                                                return null;
                                            }
                                        },
                                        new PromisedReply.FailureListener<ServerMessage>() {
                                            @Override
                                            public PromisedReply<ServerMessage> onFailure(Exception err) {
                                                mRefresher.setRefreshing(false);
                                                return null;
                                            }
                                        }
                                );
                    } catch (Exception e) {
                        mRefresher.setRefreshing(false);
                    }
                } else {
                    mRefresher.setRefreshing(false);
                }
            }
        });

        mFailureListener = new UiUtils.ToastFailureListener(getActivity());

        // Send message on button click
        getActivity().findViewById(R.id.chatSendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendText();
            }
        });



        // Send Message  when you press Enter Key
        EmojiconEditText editor = activity.findViewById(R.id.editMessage);
        // Send message on Enter
        editor.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        sendText();
                        return true;
                    }
                });

        // Send notification on key presses
        editor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (count > 0 || before > 0) {
                    activity.sendKeyPress();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        // bmb Button ...
        try{
            //bmb.setDraggable(true);
            // boom menu    ...
            bmb = (BoomMenuButton) activity.findViewById(R.id.bmb);
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

                            FilePickerBuilder.getInstance().setMaxCount(99)
                                    .setSelectedFiles(docPaths)
                                    .enableVideoPicker(true)
                                    .enableDocSupport(true)
                                    .showGifs(true)
                                    .enableSelectAll(true)
                                    .showFolderView(true)
                                    .setActivityTheme(R.style.LibAppTheme)
                                    .pickFile(MessagesFragment.this);

                            //openFileSelector("*/*", R.string.select_file, ACTION_ATTACH_FILE);

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
                                    .pickPhoto(MessagesFragment.this);

                            //openFileSelector("image/*", R.string.select_image, ACTION_ATTACH_IMAGE);
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
                            ImagePicker.cameraOnly().start(MessagesFragment.this); // Could be Activity, Fragment, Support Fragment
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


        // button Emoji  ...


        // Checking if root layout changed to detect soft keyboard

        final RelativeLayout root = getActivity().findViewById(R.id.chat_root);
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            int previousHeight = root.getRootView().getHeight() - root.getHeight() - ml.getHeight();

            @Override
            public void onGlobalLayout()
            {
                int height = root.getRootView().getHeight() - root.getHeight() - ml.getHeight();

                if(previousHeight != height)
                {
                    if(previousHeight > height)
                    {
                        previousHeight = height;
                    }
                    else if(previousHeight < height)
                    {
                        ml.scrollToPosition(ml.getAdapter().getItemCount()  - 1);

                        previousHeight = height;
                    }
                }
            }
        });

        final EmojiconEditText mMessageField = activity.findViewById(R.id.editMessage);


        btEmoji = (ImageView) getActivity().findViewById(R.id.buttonEmoji);
        emojIcon = new EmojIconActions(MessagesFragment.this.getContext(),root,  mMessageField,btEmoji);
        emojIcon.ShowEmojIcon();

        // ######################### /// ###########

        // init voice button   and voice recorder   ...
        //Implement voice selection

        mrecordVoiceButton =(ImageButton) getActivity().findViewById(R.id.recordVoiceButton);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/voice_mail.3gp";
        mrecordVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // test notificaton
                //TestSendNotification();
                showDiag(mrecordVoiceButton);
            }
        });

        // init Media Player

        mediaPlayer_song_out = MediaPlayer.create(MessagesFragment.this.getContext(), R.raw.hify_sound);
        mediaPlayer_song_in = MediaPlayer.create(MessagesFragment.this.getContext(), R.raw.stairs);


        // get Topic Name and slit that to extract  firebase uid

        Bundle bundle = getArguments();
        String name = bundle.getString("topic");
        ComTopic<VxCard> mTopic_name = (ComTopic<VxCard>) Cache.getTinode().getTopic(name);
        Log.d("test", mTopic_name.toString());

        // init fireabse  notifier
        FCM_Message_Sender = new CustomFcm_Util();

        //  init  firebase  and firestore initialisation

        mFirestore = FirebaseFirestore.getInstance();
        currentUser= FirebaseAuth.getInstance().getCurrentUser();

        GetCorrespondantInformation_and_your_profile();

    }


    // location intent    ...
    private void locationPlacesIntent(){
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(MessagesFragment.this.getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    // pick contact    ...
    private void pickContact(){

        new MultiContactPicker.Builder(MessagesFragment.this) //Activity/fragment context
                // .theme(R.style.MyCustomPickerTheme) //Optional - default: MultiContactPicker.Azure
                .hideScrollbar(false) //Optional - default: false
                .showTrack(true) //Optional - default: true
                .searchIconColor(Color.WHITE) //Option - default: White
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                .handleColor(ContextCompat.getColor(MessagesFragment.this.getContext() , R.color.colorPrimary)) //Optional - default: Azure Blue
                .bubbleColor(ContextCompat.getColor(MessagesFragment.this.getContext(), R.color.colorPrimary)) //Optional - default: Azure Blue
                .bubbleTextColor(Color.WHITE) //Optional - default: White
                .showPickerForResult(CONTACT_PICKER_REQUEST);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onResume() {
        super.onResume();

        Bundle bundle = getArguments();
        String oldTopicName = mTopicName;
        mTopicName = bundle.getString("topic");
        mMessageToSend = bundle.getString("messageText");

        mTopic = (ComTopic<VxCard>) Cache.getTinode().getTopic(mTopicName);

        setHasOptionsMenu(true);

        // Check periodically if all messages were read;
        mNoteTimer = new Timer();
        mNoteTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendReadNotification();
            }
        }, READ_DELAY, READ_DELAY);

        mRefresher.setRefreshing(false);

        if (mTopicName != null) {
            mMessagesAdapter.swapCursor(mTopicName, null,  !mTopicName.equals(oldTopicName));
            runMessagesLoader();
        }
    }

    void runMessagesLoader() {
        mMessagesAdapter.runLoader();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Stop reporting read messages
        mNoteTimer.cancel();
        mNoteTimer = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mUploadProgress = null;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // her i must implement some kind of filters to hide  a  video call feature whenn  it is  group chat    ....
        inflater.inflate(R.menu.tin_menu_topic, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_clear: {
                // TODO: implement Topic.deleteMessages
                return true;
            }
            case R.id.action_mute: {
                // TODO: implement setting notifications to off
                return true;
            }
            case R.id.action_delete: {
                showDeleteTopicConfirmationDialog();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Confirmation dialog "Do you really want to do X?"
    private void showDeleteTopicConfirmationDialog() {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        final AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(activity);
        confirmBuilder.setNegativeButton(android.R.string.cancel, null);
        confirmBuilder.setMessage(R.string.confirm_delete_topic);

        confirmBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    mTopic.delete().thenApply(new PromisedReply.SuccessListener<ServerMessage>() {
                        @Override
                        public PromisedReply<ServerMessage> onSuccess(ServerMessage result) {
                            Intent intent = new Intent(getActivity(), MainActivity_GuDDana.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                            getActivity().finish();
                            return null;
                        }
                    }, mFailureListener);
                } catch (NotConnectedException ignored) {
                    Toast.makeText(activity, R.string.no_connection, Toast.LENGTH_SHORT).show();
                } catch (Exception ignored) {
                    Toast.makeText(activity, R.string.action_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
        confirmBuilder.show();
    }

    public void notifyDataSetChanged() {
        mMessagesAdapter.notifyDataSetChanged();
    }

    void openFileSelector(String mimeType, int title, int resultCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mimeType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {

            startActivityForResult(Intent.createChooser(intent, getActivity().getString(title)), resultCode);

        } catch (ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), R.string.file_manager_not_found, Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("jhkhj", "jhkjh");
        switch (requestCode) {

            case ACTION_ATTACH_IMAGE:


            case ACTION_ATTACH_FILE: {
                final Bundle args = new Bundle();
                args.putParcelable("uri", data.getData());
                args.putInt("requestCode", requestCode);
                args.putString("topic", mTopicName);
                final FragmentActivity activity = getActivity();
                if (activity == null) {
                    return;
                }

                // Must use unique ID for each upload. Otherwise trouble.
                activity.getSupportLoaderManager().initLoader(Cache.getUniqueCounter(), args, this);
                break;
            }

            case FilePickerConst.REQUEST_CODE_DOC: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));

                    number_of_files_to_send = docPaths.size();

                    for (String object : docPaths) {
                        System.out.println(object);
                        String[] split = object.split("\\.");
                        String ext = split[split.length - 1];
                        File file = new File(object);
                        String filename = file.getName();


                        Uri uri_file = Uri.fromFile(new File(file.getPath()));
                        final Bundle args = new Bundle();
                        args.putParcelable("uri", uri_file);
                        args.putInt("requestCode", ACTION_ATTACH_FILE); // image picker   request code   ....
                        args.putString("topic", mTopicName);
                        final FragmentActivity activity = getActivity();
                        if (activity == null) {
                            return;
                        }
                        // Must use unique ID for each upload. Otherwise trouble.
                        activity.getSupportLoaderManager().initLoader(Cache.getUniqueCounter(), args, this);
                    }

                    Log.e("jhkhj", "jhkjh");

                }
                break;
            }



            case FilePickerConst.REQUEST_CODE_PHOTO: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                    number_of_files_to_send = photoPaths.size();

                    for (String object : photoPaths) {
                        System.out.println(object);
                        String[] split = object.split("\\.");
                        String ext = split[split.length - 1];
                        File file = new File(object);
                        String filename = file.getName();

                        // doc_file__upload_images_to_firebase(Uri.fromFile(new File(object.toString())), ext , filename);
                        Uri uri_file = Uri.fromFile(new File(file.getPath()));
                        final Bundle args = new Bundle();
                        args.putParcelable("uri", uri_file);
                        args.putInt("requestCode", ACTION_ATTACH_IMAGE); // image picker   request code   ....
                        args.putString("topic", mTopicName);
                        final FragmentActivity activity = getActivity();
                        if (activity == null) {
                            return;
                        }
                        // Must use unique ID for each upload. Otherwise trouble.
                        activity.getSupportLoaderManager().initLoader(Cache.getUniqueCounter(), args, this);

                    }

                    Log.e("jhkhj", "jhkjh");
                }
                break;
            }


                // contact
            // contact  ...  if
            case CONTACT_PICKER_REQUEST: {
                if (resultCode == RESULT_OK) {
                    List<ContactResult> results = MultiContactPicker.obtainResult(data);
                    int i = 0;
                    do {
                        // sendMessage_location_contact("text" , results.get(i).getDisplayName()+ " : " + results.get(i).getPhoneNumbers());
                        sendText_Contact(results.get(i).getDisplayName() + "  Tel: " + results.get(i).getPhoneNumbers());
                        ContactResult element = results.get(i);
                        i++;
                    }
                    while (i < results.size());
                } else if (resultCode == RESULT_CANCELED) {
                    System.out.println("User closed the picker without selecting items.");
                }
                break;
            }


            // Place upload  .....

            case PLACE_PICKER_REQUEST: {
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(MessagesFragment.this.getContext(), data);
                    if (place != null) {
                        LatLng latLng = place.getLatLng();
                        sendText_Location(latLng.latitude + ":" + latLng.longitude);
                        // sendMessage_location_contact("location" , latLng.latitude+":"+latLng.longitude);
                        // ChatModel chatModel = new ChatModel(userModel, Calendar.getInstance().getTime().getTime()+"",mapModel);
                        //mFirebaseDatabaseReference.child(CHAT_REFERENCE).push().setValue(chatModel);
                    } else {
                        //PLACE IS NULL
                        Log.e("no place", "not place");
                    }
                }
                break;
            }

        }

        super.onActivityResult(requestCode, resultCode, data);


        // images Picker
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            List<Image> images = ImagePicker.getImages(data);
            // progressStatus =+1;
            number_of_files_to_send = images.size();
            //update_work_background.setVisibility(View.VISIBLE);
            for (Image uri_img : images) {
                Uri uri_file = Uri.fromFile(new File(uri_img.getPath()));
                final String imageLocation = "Photos" + "/" + messageId;
                final String uniqueId = UUID.randomUUID().toString();

                final Bundle args = new Bundle();
                args.putParcelable("uri", uri_file);
                args.putInt("requestCode", ACTION_ATTACH_IMAGE); // image picker   request code   ....
                args.putString("topic", mTopicName);
                final FragmentActivity activity = getActivity();
                if (activity == null) {
                    return;
                }
                // Must use unique ID for each upload. Otherwise trouble.
                activity.getSupportLoaderManager().initLoader(Cache.getUniqueCounter(), args, this);

            }

        }


    }


    public void sendText() {
        final Activity activity = getActivity();
        final EmojiconEditText inputField = activity.findViewById(R.id.editMessage);
        String message = inputField.getText().toString().trim();
        // notifyDataSetChanged();
        if (!message.equals("")) {
            if (sendMessage(Drafty.parse(message))) {
                // Message is successfully queued, clear text from the input field and redraw the list.
                inputField.setText("");

                // send Notification

                try{

                    FCM_Message_Sender.sendWithOtherThread("token" ,
                            TokenFCM_OtherUser ,
                            "Message",
                            FirebaseAuth.getInstance().getUid(),
                            Name_Sender,
                            url_image,
                            getDateAndTime(),
                            "room_disable",
                            message);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    public void sendText_Contact(String msg_contact) {
        final Activity activity = getActivity();
        //final TextView inputField = activity.findViewById(R.id.editMessage);
        String message = msg_contact.toString().trim();
        // notifyDataSetChanged();
        if (!message.equals("")) {
            if (sendMessage(Drafty.parse(message))) {
                // Message is successfully queued, clear text from the input field and redraw the list.
                // inputField.setText("");
                try{

                    FCM_Message_Sender.sendWithOtherThread("token" ,
                            TokenFCM_OtherUser ,
                            "Message",
                            FirebaseAuth.getInstance().getUid(),
                            Name_Sender,
                            url_image,
                            getDateAndTime(),
                            "room_disable",
                            message);

                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }
        }
    }

    public void sendText_Location(String msg_contact) {
        final Activity activity = getActivity();
        //final TextView inputField = activity.findViewById(R.id.editMessage);
        String message = msg_contact.toString().trim();
        // notifyDataSetChanged();
        if (!message.equals("")) {
            if (sendMessage(Drafty.parse(message))) {
                // Message is successfully queued, clear text from the input field and redraw the list.
                // inputField.setText("");
                try{

                    FCM_Message_Sender.sendWithOtherThread("token" ,
                            TokenFCM_OtherUser ,
                            "Message",
                            FirebaseAuth.getInstance().getUid(),
                            Name_Sender,
                            url_image,
                            getDateAndTime(),
                            "room_disable",
                            message);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private boolean sendMessage(Drafty content) {
        if (mTopic != null) {
            try {
                PromisedReply<ServerMessage> reply = mTopic.publish(content);
                runMessagesLoader(); // Shows pending message
                reply.thenApply(new PromisedReply.SuccessListener<ServerMessage>() {
                    @Override
                    public PromisedReply<ServerMessage> onSuccess(ServerMessage result) {
                        // Updates message list with "delivered" icon.
                        runMessagesLoader();
                        Play_Song_out_message();
                        return null;
                    }
                }, mFailureListener);
            } catch (NotConnectedException ignored) {
                Log.d(TAG, "sendMessage -- NotConnectedException", ignored);
                Toasty.info(MessagesFragment.this.getContext(), "not connected  ", Toast.LENGTH_LONG).show();

            } catch (Exception ignored) {
                Log.d(TAG, "sendMessage -- Exception", ignored);
                Toasty.info(getActivity(), ignored.toString(), Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
        return false;
    }

    // Send image in-band
    public static Drafty draftyImage(String mimeType, byte[] bits, int width, int height, String fname) {
        Drafty content = Drafty.parse(" ");
        content.insertImage(0, mimeType, bits, width, height, fname);
        Play_Song_out_message();



        try{

            FCM_Message_Sender.sendWithOtherThread("token" ,
                    TokenFCM_OtherUser ,
                    "Message",
                    FirebaseAuth.getInstance().getUid(),
                    Name_Sender,
                    url_image,
                    getDateAndTime(),
                    "room_disable",
                    "new Image");

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return content;
    }

    // Send file in-band
    public static Drafty draftyFile(String mimeType, byte[] bits, String fname) {
        Drafty content = new Drafty();
        content.attachFile(mimeType, bits, fname);

        Play_Song_out_message();

        try{

            FCM_Message_Sender.sendWithOtherThread("token" ,
                    TokenFCM_OtherUser ,
                    "Message",
                    FirebaseAuth.getInstance().getUid(),
                    Name_Sender,
                    url_image,
                    getDateAndTime(),
                    "room_disable",
                    "new File ");

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return content;
    }

    // Send file as a link.
    public static Drafty draftyAttachment(String mimeType, String fname, String refUrl, long size) {
        Drafty content = new Drafty();
        content.attachFile(mimeType, fname, refUrl, size);

        // to play sund for  nification
        Play_Song_out_message();

        // send notification
        try{

            FCM_Message_Sender.sendWithOtherThread("token" ,
                    TokenFCM_OtherUser ,
                    "Message",
                    FirebaseAuth.getInstance().getUid(),
                    Name_Sender,
                    url_image,
                    getDateAndTime(),
                    "room_disable",
                    "new Attachment ");

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return content;
    }

    public void sendReadNotification() {
        if (mTopic != null) {
            mTopic.noteRead();
        }
    }

    public void topicSubscribed() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        if (mTopic.getAccessMode().isWriter()) {
            ((TextView) activity.findViewById(R.id.editMessage)).setText(TextUtils.isEmpty(mMessageToSend) ? "" : mMessageToSend);
            activity.findViewById(R.id.sendMessagePanel).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.sendMessageDisabled).setVisibility(View.GONE);
            mMessageToSend = null;
        } else {
            activity.findViewById(R.id.sendMessagePanel).setVisibility(View.GONE);
            activity.findViewById(R.id.sendMessageDisabled).setVisibility(View.VISIBLE);
        }
    }

    private int findItemPositionById(long id) {
        int position = -1;
        final int first = mMessageViewLayoutManager.findFirstVisibleItemPosition();
        final int last = mMessageViewLayoutManager.findLastVisibleItemPosition();
        if (last == RecyclerView.NO_POSITION) {
            return position;
        }

        for (int i = first; i <= last && !isDetached(); i++) {
            if (mMessagesAdapter.getItemId(i) == id) {
                position = i;
                break;
            }
        }
        return position;
    }

    @NonNull
    @Override
    public Loader<UploadResult> onCreateLoader(int id, Bundle args) {
        return new FileUploader(getActivity(), args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<UploadResult> loader, final UploadResult data) {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            // Kill the loader otherwise it will keep uploading the same file whenever the activity
            // is created.
            activity.getSupportLoaderManager().destroyLoader(loader.getId());
        }

        // Avoid processing the same result twice;
        if (data.processed) {
            return;
        } else {
            data.processed = true;
        }

        if (data.msgId > 0) {
            try {
                mTopic.syncOne(data.msgId).thenApply(new PromisedReply.SuccessListener<ServerMessage>() {
                    @Override
                    public PromisedReply<ServerMessage> onSuccess(ServerMessage result) {
                        if (activity != null && result != null) {
                            // Log.d(TAG, "onLoadFinished - onSuccess " + result.ctrl.id);
                            activity.runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       int pos = findItemPositionById(data.msgId);
                                       if (pos >= 0) {
                                           runMessagesLoader();
                                       }
                                   }
                               }
                            );
                        }
                        return null;
                    }
                }, null);
            } catch (Exception ex) {
                Log.d(TAG, "Failed to sync", ex);
                Toast.makeText(activity, R.string.failed_to_send_message, Toast.LENGTH_LONG).show();
            }
        } else if (data.error != null) {
            runMessagesLoader();
            Toast.makeText(activity, data.error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<UploadResult> loader) {
    }

    public void setProgressIndicator(boolean active) {
        if (!isAdded()) {
            return;
        }
        mRefresher.setRefreshing(active);
    }

    private static class FileUploader extends AsyncTaskLoader<UploadResult> {
        private static WeakReference<UploadProgress> sProgress;
        private final Bundle mArgs;
        private UploadResult mResult = null;

        FileUploader(Activity activity, Bundle args) {
            super(activity);
            mArgs = args;
        }

        static void setProgressHandler(UploadProgress progress) {
            sProgress = new WeakReference<>(progress);
        }

        @Override
        public void onStartLoading() {
            if (mResult != null) {
                deliverResult(mResult);
            } else {
                Storage store = BaseDb.getInstance().getStore();
                // Create blank message here to avoid the crash.
                long msgId = store.msgDraft(Cache.getTinode().getTopic(mArgs.getString("topic")), new Drafty());
                mArgs.putLong("msgId", msgId);
                UploadProgress p = sProgress.get();
                if (p != null) {
                    p.onStart(msgId);
                }
                forceLoad();
            }
        }

        @Nullable
        @Override
        public UploadResult loadInBackground() {
            // Don't upload again if upload was completed already.
            if (mResult == null) {
                mResult = doUpload(getId(), getContext(), mArgs, sProgress);
            }
            return mResult;
        }
    }

    private static Bundle getFileDetails(final Context context, Uri uri) {
        final ContentResolver resolver = context.getContentResolver();
        String fname = null;
        Long fsize = 0L;

        String mimeType = resolver.getType(uri);
        if (mimeType == null) {
            mimeType = UiUtils.getMimeType(uri);
        }

        Cursor cursor = resolver.query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            fname = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            fsize = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
            cursor.close();
        }

        // Still no size? Try opening directly.
        if (fsize == 0) {
            String path = UiUtils.getPath(context, uri);
            if (path != null) {
                File file = new File(path);
                if (fname == null) {
                    fname = file.getName();
                }
                fsize = file.length();
            }
        }

        Bundle result = new Bundle();
        result.putString("mime", mimeType);
        result.putString("name", fname);
        result.putLong("size", fsize);
        return result;
    }


    private static UploadResult doUpload(final int loaderId, final Context context, final Bundle args,
                                 final WeakReference<UploadProgress> callbackProgress) {

        final UploadResult result = new UploadResult();
        Storage store = BaseDb.getInstance().getStore();

        final int requestCode = args.getInt("requestCode");
        final String topicName = args.getString("topic");
        final Uri uri = args.getParcelable("uri");
        result.msgId = args.getLong("msgId");

        if (uri == null) {
            Log.d(TAG, "Received null URI");
            result.error = "Null URI";
            return result;
        }

        final Topic topic = Cache.getTinode().getTopic(topicName);

        Drafty content = null;
        boolean success = false;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            int imageWidth = 0, imageHeight = 0;

            Bundle fileDetails = getFileDetails(context, uri);
            String fname = fileDetails.getString("name");
            Long fsize = fileDetails.getLong("size");
            String mimeType = fileDetails.getString("mime");

            if (fsize == 0) {
                Log.d(TAG, "File size is zero "+uri);
                result.error = context.getString(R.string.invalid_file);
                return result;
            }

            if (fname == null) {
                fname = context.getString(R.string.default_attachment_name);
            }

            final ContentResolver resolver = context.getContentResolver();
            if (requestCode == ACTION_ATTACH_IMAGE && fsize > MAX_INBAND_ATTACHMENT_SIZE) {
                is = resolver.openInputStream(uri);
                // Resize image to ensure it's under the maximum in-band size.
                Bitmap bmp = BitmapFactory.decodeStream(is, null, null);
                bmp = UiUtils.scaleBitmap(bmp);
                imageWidth = bmp.getWidth();
                imageHeight = bmp.getHeight();
                is.close();

                is = UiUtils.bitmapToStream(bmp, mimeType);
                fsize = (long) is.available();
            }

            if (fsize > MAX_ATTACHMENT_SIZE) {
                Log.d(TAG, "File is too big, size="+fsize);
                result.error = context.getString(R.string.attachment_too_large,
                        UiUtils.bytesToHumanSize(fsize), UiUtils.bytesToHumanSize(MAX_ATTACHMENT_SIZE));
            } else {
                if (is == null) {
                    is = resolver.openInputStream(uri);
                }

                if (requestCode == ACTION_ATTACH_FILE && fsize > MAX_INBAND_ATTACHMENT_SIZE) {

                    // Update draft with file data.
                    store.msgDraftUpdate(topic, result.msgId, draftyAttachment(mimeType, fname, uri.toString(), -1));

                    UploadProgress start = callbackProgress.get();
                    if (start != null) {
                        start.onStart(result.msgId);
                        // This assignment is needed to ensure that the loader does not keep
                        // a strong reference to activity while potentially slow upload process
                        // is running.
                        start = null;
                    }

                    // Upload then send message with a link. This is a long-running blocking call.
                    final LargeFileHelper uploader = Cache.getTinode().getFileUploader();
                    MsgServerCtrl ctrl = uploader.upload(is, fname, mimeType, fsize,
                            new LargeFileHelper.FileHelperProgress() {
                                @Override
                                public void onProgress(long progress, long size) {
                                    UploadProgress p = callbackProgress.get();
                                    if (p != null) {
                                        if (!p.onProgress(loaderId, result.msgId, progress, size)) {
                                            uploader.cancel();
                                        }
                                    }
                                }
                            });
                    success = (ctrl != null && ctrl.code == 200);
                    if (success) {
                        content = draftyAttachment(mimeType, fname, ctrl.getStringParam("url"), fsize);
                    }
                } else {
                    baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[16384];
                    int len;
                    while ((len = is.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }

                    byte[] bits = baos.toByteArray();
                    if (requestCode == ACTION_ATTACH_FILE) {
                        store.msgDraftUpdate(topic, result.msgId, draftyFile(mimeType, bits, fname));
                    } else {
                        if (imageWidth == 0) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            InputStream bais = new ByteArrayInputStream(bits);
                            BitmapFactory.decodeStream(bais, null, options);
                            bais.close();

                            imageWidth = options.outWidth;
                            imageHeight = options.outHeight;
                        }
                        store.msgDraftUpdate(topic, result.msgId,
                                draftyImage(mimeType, bits, imageWidth, imageHeight, fname));
                    }
                    success = true;
                    UploadProgress start = callbackProgress.get();
                    if (start != null) {
                        start.onStart(result.msgId);
                    }
                }
            }
        } catch (IOException | NullPointerException ex) {
            result.error = ex.getMessage();
            if (!"cancelled".equals(result.error)) {
                Log.e(TAG, "Failed to attach file", ex);
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {}
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException ignored) {}
            }
        }

        if (result.msgId > 0) {
            if (success) {
                // Success: mark message as ready for delivery. If content==null it won't be saved.
                store.msgReady(topic, result.msgId, content);
            } else {
                // Failure: discard draft.
                store.msgDiscard(topic, result.msgId);
                result.msgId = -1;
            }
        }

        return result;
    }

    static class UploadResult {
        String error;
        long msgId = -1;
        boolean processed = false;

        UploadResult() {
        }

        public String toString() {
            return "msgId=" + msgId + ", error='" + error + "'";
        }
    }

    private class UploadProgress {

        UploadProgress() {
        }

        void onStart(final long msgId) {
            // Reload the cursor.
            runMessagesLoader();
        }

        // Returns true to continue the upload, false to cancel.
        boolean onProgress(final int loaderId, final long msgId, final long progress, final long total) {
            // DEBUG -- slow down the upload progress.
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // debug


            // Check for cancellation.
            Integer oldLoaderId = mMessagesAdapter.getLoaderMapping(msgId);
            if (oldLoaderId == null) {
                mMessagesAdapter.addLoaderMapping(msgId, loaderId);
            } else if (oldLoaderId != loaderId) {
                // Loader id has changed, cancel.
                return false;
            }

            Activity activity = getActivity();
            if (activity == null) {
                return true;
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final int position = findItemPositionById(msgId);
                    if (position < 0) {
                        return;
                    }
                    mMessagesAdapter.notifyItemChanged(position,
                            total > 0 ? (float) progress / total : (float) progress);
                }
            });

            return true;
        }
    }






    // ################################################## Voice Message   ####################################################


    private void UploadVoiceMsg(){

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

        final View dialogView = View.inflate(MessagesFragment.this.getContext(),R.layout.dialog_gps_navi_choice,null);

        final Dialog dialog = new Dialog(MessagesFragment.this.getContext(),R.style.MyAlertDialogStyle);
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
                OpenNavi.Open_Street_View(MessagesFragment.this.getContext() , message_location_parsed );

            }
        });

        ImageView map_view = (ImageView) dialog.findViewById(R.id.map_view);
        map_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealShow_navigation(dialogView, false, dialog , Startposition);
                OpenNavi.Open_map(MessagesFragment.this.getContext() , message_location_parsed);
            }
        });


        ImageView navi_view = (ImageView) dialog.findViewById(R.id.navi_view);
        navi_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealShow_navigation(dialogView, false, dialog , Startposition);
                OpenNavi.Open_navi(MessagesFragment.this.getContext() , message_location_parsed);

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

        final View dialogView = View.inflate(MessagesFragment.this.getContext(),R.layout.dialog_voice_record,null);

        final Dialog dialog = new Dialog(MessagesFragment.this.getContext(),R.style.MyAlertDialogStyle);
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
                stopRecording_and_send_voice_msg(); // stop recording and send voice mail
                revealShow(dialogView, false, dialog , Startposition);

            }
        });


        // send recorded  voice mail ...
        ImageView  SendRecordedVoice = (ImageView) dialog.findViewById(R.id.start_record);
        SendRecordedVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording_and_send_voice_msg(); // stop recording and send voice mail
                revealShow(dialogView, false, dialog , Startposition);

            }
        });

        TextView deleteTextView = (TextView) dialog.findViewById(R.id.delete_textview);
        deleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // maybe  a confirmation dialog her ???  to ask user if  he want to close this dialog windows  ...

                new BottomDialog.Builder(MessagesFragment.this.getContext())
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

                new BottomDialog.Builder(MessagesFragment.this.getContext())
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

    private void stopRecording_and_send_voice_msg() {

        try {
                        mRecorder.stop();
                        mRecorder.release();
                        mRecorder = null;
                        record_time_voice.stop();

                        // get  uri msg   ...
                        Uri uri_voice_msg = Uri.fromFile(new File(mFileName));

                        //Uri uri_file = Uri.fromFile(new File(file.getPath()));
                        final Bundle args = new Bundle();
                        args.putParcelable("uri", uri_voice_msg);
                        args.putInt("requestCode", ACTION_ATTACH_FILE); // image picker   request code   ....
                        args.putString("topic", mTopicName);
                        final FragmentActivity activity = getActivity();
                        if (activity == null) {
                            return;
                        }
                        // Must use unique ID for each upload. Otherwise trouble.
                        activity.getSupportLoaderManager().initLoader(Cache.getUniqueCounter(), args, this);

            // uploadAudio();
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


    public static void Play_Song_out_message() {

        try {

            mediaPlayer_song_out.start();

            // after that send notification

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public  void TestSendNotification(){
        try{
            Log.d("send", "send");

            FCM_Message_Sender.sendWithOtherThread("token" ,
                    TokenFCM_OtherUser ,
                    "Message",
                    "u2j7FkqofqV7EqNW5mqhmwmBUn73",
                    "chicikolon user",
                    url_image,
                    getDateAndTime(),
                    "room_disable",
                    "test message  ");

            Log.d("send", "send");

        }catch (Exception ex){
            ex.printStackTrace();
        }

    }



    public static void   GetCorrespondantInformation_and_your_profile(){

        try{

            // get Users Informations
            mFirestore.collection("Users")
                    .document(MessageActivity.otherUserId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            TokenFCM_OtherUser =documentSnapshot.getString("token_id");

                        }
                    });


            // get your information
            mFirestore.collection("Users")
                    .document(FirebaseAuth.getInstance().getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            url_image =documentSnapshot.getString("image");
                            Sender_uid = documentSnapshot.getString("id");
                            Name_Sender = documentSnapshot.getString("name");

                        }
                    });

        }catch(Exception ex){
            ex.printStackTrace();
        }


    }
    public static void Play_Song_in_message() {

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
}
