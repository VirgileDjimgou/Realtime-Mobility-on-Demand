package com.android.gudana.hify.ui.activities;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.gudana.BootNavigation.BaseActivity;
import com.android.gudana.GuDFeed.GuDFeed_Fragment;
import com.android.gudana.GuDFeed.activities.create_post;
// import com.android.gudana.apprtc.CallIncomingActivity_rtc;
import com.android.gudana.apprtc.linphone.LinphoneManager;
import com.android.gudana.chatapp.fragments.CallFragment;
import com.android.gudana.hify.adapters.DrawerAdapter;
import com.android.gudana.hify.models.DrawerItem;
import com.android.gudana.hify.models.SimpleItem;
import com.android.gudana.hify.ui.activities.account.LoginActivity;
import com.android.gudana.hify.ui.activities.account.UpdateAvailable;
import com.android.gudana.hify.ui.activities.friends.FriendProfile;
import com.android.gudana.hify.ui.activities.notification.NotificationActivity;
import com.android.gudana.hify.ui.activities.notification.NotificationImage;
import com.android.gudana.hify.ui.activities.notification.NotificationImageReply;
import com.android.gudana.hify.ui.activities.notification.NotificationReplyActivity;
import com.android.gudana.hify.ui.activities.post.CommentsActivity;
import com.android.gudana.hify.ui.activities.post.PostImage;
import com.android.gudana.hify.ui.activities.post.PostText;
import com.android.gudana.hify.ui.fragment.All_ChatsFragment;
import com.android.gudana.hify.ui.fragment.Dashboard;
import com.android.gudana.hify.ui.fragment.FriendsFragment;
import com.android.gudana.hify.ui.fragment.ProfileFragment;
import com.android.gudana.hify.utils.Config;
import com.android.gudana.hify.utils.NetworkUtil;
import com.android.gudana.hify.utils.database.UserHelper;
import com.android.gudana.R;
// import com.android.gudana.linphone.DialerFragment;
import com.android.gudana.tindroid.AccountInfoFragment;
import com.android.gudana.tindroid.Cache;
import com.android.gudana.tindroid.ChatListAdapter;
import com.android.gudana.tindroid.ContactListFragment;
import com.android.gudana.tindroid.ContactsFragment;
import com.android.gudana.tindroid.UiUtils;
import com.android.gudana.tindroid.account.Utils;
import com.android.gudana.tindroid.db.BaseDb;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.tapadoo.alerter.Alerter;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import it.sephiroth.android.library.bottomnavigation.BadgeProvider;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import it.sephiroth.android.library.bottomnavigation.FloatingActionButtonBehavior;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.util.Log.INFO;
import static android.util.Log.VERBOSE;
import static it.sephiroth.android.library.bottomnavigation.MiscUtils.log;

// add  tindroid contactActivity

import android.support.v4.app.FragmentTransaction;
import android.widget.ProgressBar;

import com.android.gudana.tindroid.media.VxCard;
import co.tinode.tinodesdk.MeTopic;
import co.tinode.tinodesdk.NotConnectedException;
import co.tinode.tinodesdk.NotSynchronizedException;
import co.tinode.tinodesdk.PromisedReply;
import co.tinode.tinodesdk.Tinode;
import co.tinode.tinodesdk.model.Description;
import co.tinode.tinodesdk.model.MsgServerInfo;
import co.tinode.tinodesdk.model.MsgServerPres;
import co.tinode.tinodesdk.model.PrivateType;
import co.tinode.tinodesdk.model.Subscription;

// add  libray    contactActivity

/**
 * Created by amsavarthan on 29/3/18.
 */

public class MainActivity_GuDDana extends BaseActivity implements DrawerAdapter.OnItemSelectedListener ,
        BottomNavigation.OnMenuItemSelectionListener ,
        ContactListFragment.OnContactsInteractionListener {

    // POS_DASHBOARD   CHAT  POS_SEND_FRIEND   POS_ABOUT  POS_LOGOUT

    static final String TAG = com.android.gudana.BootNavigation.MainActivity.class.getSimpleName();
    static final String FRAGMENT_CONTACTS = "contacts";
    static final String FRAGMENT_EDIT_ACCOUNT = "edit_account";


    private ChatListAdapter mChatListAdapter;
    private FirebaseFirestore mFirestore;
    private MeListener mMeTopicListener = null;
    private MeTopic mMeTopic = null;

    private static final int POS_DASHBOARD = 0;
    private static final int CHAT = 1 ;
    private static final int POS_SEND_FRIEND = 2;
    private static final int POS_ABOUT = 3;
    private static final int POS_LOGOUT = 4;
    public static  boolean loadpost_firstime = false;
    public static boolean mode_public = true ; // if  not than mode private  ....in this mode you see once the post of your friends
    // in Public mode you see all the post on the Networks  ... ....

    public static String userId;
    public static MainActivity_GuDDana activity;
    DrawerAdapter adapter;
    View sheetView;
    private String[] screenTitles;
    private Drawable[] screenIcons;
    private SlidingRootNav slidingRootNav;
    private FirebaseAuth mAuth;
    private FirebaseUser currentuser;
    private FirebaseFirestore firestore;
    private UserHelper userHelper;
    private StorageReference storageReference;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private Intent resultIntent;
    public static CircleImageView imageView;
    public static TextView username;
    private AuthCredential credential;
    private Fragment mCurrentFragment;
    LinphoneManager ViCall ;

    // public static url_icon_
    public BroadcastReceiver NetworkChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int status = NetworkUtil.getConnectivityStatusString(context);
            Log.i("Network reciever", "OnReceive");
            if (!"android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                if (status != NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                    performUploadTask();
                    try {
                        Snackbar.make(findViewById(R.id.activity_main), "Syncing...", Snackbar.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    };
    private BottomSheetDialog mBottomSheetDialog;
    private Toolbar toolbar;
    private MenuItem add_post,refresh;
    private boolean mState=true;
    private AdView mAdView;

    public static void startActivity(Context context) {
        Intent intent=new Intent(context,MainActivity_GuDDana.class);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Tinode tinode = Cache.getTinode();

        tinode.setListener(new MainActivity_GuDDana.ContactsEventListener(tinode.isConnected()));

        UiUtils.setupToolbar(this, null, null, false);

        if (mMeTopic == null) {
            mMeTopic = tinode.getMeTopic();
            if (mMeTopic == null) {
                // The very first launch of the app.
                mMeTopic = new MeTopic<>(tinode, mMeTopicListener);
                Log.d(TAG, "Initialized NEW 'me' topic");
            } else {
                mMeTopic.setListener(mMeTopicListener);
                Log.d(TAG, "Loaded existing 'me' topic");
            }
        } else {
            mMeTopic.setListener(mMeTopicListener);
        }


        if (!mMeTopic.isAttached()) {
            topicAttach();
        } else {
            Log.d(TAG, "onResume() called: topic is attached");
        }


        slidingRootNav.closeMenu(true);
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMeTopic != null) {
            mMeTopic.setListener(null);
        }
        getSharedPreferences("fcm_activity",MODE_PRIVATE).edit().putBoolean("active",false).apply();

    }

    @Override
    protected void onStart() {
        super.onStart();

        getSharedPreferences("fcm_activity",MODE_PRIVATE).edit().putBoolean("active",true).apply();

        username = findViewById(R.id.username);
        imageView = findViewById(R.id.profile_image);
        if(currentuser!=null)
        {
            try {
                performUploadTask();
            }catch (Exception e){
                Log.e("Error","."+e.getLocalizedMessage());
            }

            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Config.REGISTRATION_COMPLETE));

            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Config.PUSH_NOTIFICATION));


        }else{
            LoginActivity.startActivityy(this);
            finish();
        }
    }

    /*

    @Override
    public void onBackPressed() {
        if(!toolbar.getTitle().toString().equals("Dashboard")){

            toolbar.setTitle("Dashboard");
            try {
                getSupportActionBar().setTitle("Dashboard");
            }catch (Exception e){
                Log.e("Error",e.getMessage());
            }

            //this.invalidateOptionsMenu();
            mState=true;
            showFragment(new Dashboard());
            if(slidingRootNav.isMenuOpened()) {
                slidingRootNav.closeMenu(true);
            }
            adapter.setSelected(POS_DASHBOARD);

        }else if(slidingRootNav.isMenuOpened()){
            slidingRootNav.closeMenu(true);
        }else{
            super.onBackPressed();
        }
    }

    */


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hi_activity_main);

        askPermission();

        mChatListAdapter = new ChatListAdapter(this);
        mMeTopicListener = new MeListener();

        final ViewGroup root = (ViewGroup) findViewById(R.id.activity_main);
        final CoordinatorLayout coordinatorLayout;
        if (root instanceof CoordinatorLayout) {
            coordinatorLayout = (CoordinatorLayout) root;
        } else {
            coordinatorLayout = null;
        }


        activity=this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Dashboard");
        try {
            getSupportActionBar().setTitle("Dashboard");
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        // add bottom OnCreate  ...

        final int statusbarHeight = getStatusBarHeight();
        final boolean translucentStatus = hasTranslucentStatusBar();
        final boolean translucentNavigation = hasTranslucentNavigation();

        log(TAG, VERBOSE, "translucentStatus: %b", translucentStatus);

        if (translucentStatus) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) root.getLayoutParams();
            params.topMargin = -statusbarHeight;

            params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
            params.topMargin = statusbarHeight;
        }

        if (translucentNavigation) {
            final ViewPager viewPager = getViewPager();
            if (null != viewPager) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewPager.getLayoutParams();
                params.bottomMargin = -getNavigationBarHeight();
            }
        }

        initializeBottomNavigation(savedInstanceState);
        initializeUI(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        userHelper = new UserHelper(this);
        firestore = FirebaseFirestore.getInstance();

        registerReceiver(NetworkChangeReceiver
                , new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        mAuth = FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser();

        if (currentuser == null) {

            LoginActivity.startActivityy(this);
            this.finish();

        } else {

            mCurrentFragment = new Dashboard();
            //firebaseMessagingService();

            askPermission();

            userId = currentuser.getUid();
            storageReference = FirebaseStorage.getInstance().getReference().child("images").child(currentuser.getUid() + ".jpg");

            mAdView = findViewById(R.id.adView);

            slidingRootNav = new SlidingRootNavBuilder(this)
                    .withToolbarMenuToggle(toolbar)
                    .withMenuOpened(false)
                    .withContentClickableWhenMenuOpened(false)
                    .withSavedState(savedInstanceState)
                    .withMenuLayout(R.layout.hi_activity_main_drawer)
                    .inject();

            screenIcons = loadScreenIcons();
            screenTitles = loadScreenTitles();

            /*
            adapter = new DrawerAdapter(Arrays.asList(
                    createItemFor(POS_DASHBOARD).setChecked(true),
                    createItemFor (POS_SEND_MESSAGE),
                    createItemFor(CHAT),
                    createItemFor(POS_FRIENDS),
                    createItemFor(POS_ABOUT),
                    new SpaceItem(48),
                    createItemFor(POS_LOGOUT),
                    createItemFor(POS_SEND_REQUEST),
                    createItemFor(POS_SEND_FRIEND)));

                    */

            adapter = new DrawerAdapter(Arrays.asList(
                    createItemFor(0).setChecked(true),
                    createItemFor (1),
                    createItemFor(2),
                    createItemFor(3),
                    createItemFor(4),
                    new SpaceItem(18)));

            adapter.setListener(this);

            RecyclerView list = findViewById(R.id.list);
            list.setNestedScrollingEnabled(false);
            list.setLayoutManager(new LinearLayoutManager(this));
            list.setAdapter(adapter);

            adapter.setSelected(POS_DASHBOARD);
            setUserProfile();

            mBottomSheetDialog = new BottomSheetDialog(this);
            sheetView = getLayoutInflater().inflate(R.layout.hi_bottom_sheet_dialog, null);
            mBottomSheetDialog.setContentView(sheetView);

            LinearLayout text_post = sheetView.findViewById(R.id.text_post);
            LinearLayout photo_post = sheetView.findViewById(R.id.image_post);
            LinearLayout hybrid_post = sheetView.findViewById(R.id.hybrid_post);

            hybrid_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBottomSheetDialog.dismiss();
                    create_post.startActivity(MainActivity_GuDDana.this);

                }
            });

            text_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBottomSheetDialog.dismiss();
                    PostText.startActivity(MainActivity_GuDDana.this);

                }
            });

            photo_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBottomSheetDialog.dismiss();
                    PostImage.startActivity(MainActivity_GuDDana.this);
                }
            });


            // set offline capiblities    ...
            try{
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);

            }catch (Exception ex){
                ex.printStackTrace();
            }

            // update User Id Token Tindroid  Server
            Update_Uid_Tindroid();
            // chatSeenDatabase.keepSynced(true); // For offline use

        }

    }

    //##############################

    protected void initializeBottomNavigation(final Bundle savedInstanceState) {
        if (null == savedInstanceState) {
            getBottomNavigation().setDefaultSelectedIndex(0);
            final BadgeProvider provider = getBottomNavigation().getBadgeProvider();
            provider.show(R.id.bbn_item3);
            provider.show(R.id.bbn_item4);
        }
    }

    protected void initializeUI(final Bundle savedInstanceState) {
        final FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        if (null != floatingActionButton) {

            /*
            if (hasTranslucentNavigation()) {
                final ViewGroup.LayoutParams params = floatingActionButton.getLayoutParams();
                if (CoordinatorLayout.LayoutParams.class.isInstance(params)) {
                    CoordinatorLayout.LayoutParams params1 = (CoordinatorLayout.LayoutParams) params;
                    if (FloatingActionButtonBehavior.class.isInstance(params1.getBehavior())) {
                        ((FloatingActionButtonBehavior) params1.getBehavior()).setNavigationBarHeight(getNavigationBarHeight());
                    }
                }
            }
            */
        }

        final ViewPager viewPager = getViewPager();
        if (null != viewPager) {

            getBottomNavigation().setOnMenuChangedListener(new BottomNavigation.OnMenuChangedListener() {
                @Override
                public void onMenuChanged(final BottomNavigation parent) {

                    viewPager.setAdapter(new ViewPagerAdapter(MainActivity_GuDDana.this, parent.getMenuItemCount()));
                    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(
                                final int position, final float positionOffset, final int positionOffsetPixels) { }

                        @Override
                        public void onPageSelected(final int position) {
                            if (getBottomNavigation().getSelectedIndex() != position) {
                                getBottomNavigation().setSelectedIndex(position, false);
                            }
                        }

                        @Override
                        public void onPageScrollStateChanged(final int state) { }
                    });
                }
            });

        }


        //   inflate menu   to 5 items   ... and change background color  ..
        BottomNavigation navigation = getBottomNavigation();

        if (null == navigation) {
            // return 0; ... do something to avoid  Exception   ...
            navigation.inflateMenu(R.menu.bottombar_menu_5items);
        }else{
            navigation.inflateMenu(R.menu.bottombar_menu_5items);
        }
    }


    @Override
    public void onMenuItemSelect(final int itemId, final int position, final boolean fromUser) {
        log(TAG, INFO, "onMenuItemSelect(" + itemId + ", " + position + ", " + fromUser + ")");
        if (fromUser) {
            getBottomNavigation().getBadgeProvider().remove(itemId);
            if (null != getViewPager()) {
                getViewPager().setCurrentItem(position);
            }
        }
    }

    @Override
    public void onMenuItemReselect(@IdRes final int itemId, final int position, final boolean fromUser) {
        log(TAG, INFO, "onMenuItemReselect(" + itemId + ", " + position + ", " + fromUser + ")");

        if (fromUser) {
            final FragmentManager manager = getSupportFragmentManager();
            GuDFeed_Fragment fragment = (GuDFeed_Fragment) manager.findFragmentById(R.id.fragment);
            if (null != fragment) {
                // fragment.scrollToTop();
            }
        }

    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final int mCount;

        public ViewPagerAdapter(final AppCompatActivity activity, int count) {
            super(activity.getSupportFragmentManager());
            this.mCount = count;
        }

        @Override
        public Fragment getItem(final int position) {

            Log.d( "position ", String.valueOf(position));

            //fragments[3] = new HistoryListFragment();
            //fragments[4] = new ContactsListFragment();
            //fragments[5] = new DialerFragment();


            switch (position) {
                case 0:
                    //  Home Dashorad
                    return  new  Dashboard();
                case 1:
                    // ChatFragment  fragment
                    //return new All_ChatsFragment();
                    return new ContactsFragment();

                case 2:
                    // Call fragment
                    //return new FriendsFragment();
                    return new CallFragment();
                case 3:
                    //  Story   ... like status    Whatsapp
                    // return new GuDStory_Fragment();
                    return new FriendsFragment();
                case 4:
                    // user profil fragment
                    // return new FriendsFragment();
                    return new ProfileFragment();

                    // return new GuDStory_Fragment();
            }

            return null;
        }

        @Override
        public int getCount() {
            return mCount;
        }
    }


    // ##################

    private void askPermission() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS,


                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.CALL_PHONE


                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.isAnyPermissionPermanentlyDenied()){
                            Toast.makeText(MainActivity_GuDDana.this, "You have denied some permissions permanently, if the app force close try granting permission from settings.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();

    }


    private void setUserProfile() {

        Cursor rs = userHelper.getData(1);
        rs.moveToFirst();

        try{

            String nam = rs.getString(rs.getColumnIndex(UserHelper.CONTACTS_COLUMN_NAME));
            String imag = rs.getString(rs.getColumnIndex(UserHelper.CONTACTS_COLUMN_IMAGE));

            if (!rs.isClosed()) {
                rs.close();
            }

            username = findViewById(R.id.username);
            imageView = findViewById(R.id.profile_image);
            username.setText(nam);
            Glide.with(this)
                    .setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.default_user_art_g_2))
                    .load(imag)
                    .into(imageView);

        }catch (Exception ex){
            ex.printStackTrace();
        }


    }


    private void Update_Uid_Tindroid(){

        String TindroidUniqueId = Cache.getTinode().getMyId();
        if(TindroidUniqueId != null){

            if(TindroidUniqueId.trim()!= null){

                //dialog.setMessage("Updating Details....");
                //dialog.show();
                // userMap.put("uid_tindroid", Uid_tindroid);

                mFirestore = FirebaseFirestore.getInstance();
                final DocumentReference userDocument=mFirestore.collection("Users").document(mAuth.getCurrentUser().getUid());
                Map<String,Object> map=new HashMap<>();
                map.put("uid_tindroid",TindroidUniqueId);

                userDocument.update(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toasty.info(MainActivity_GuDDana.this, "User Id updated", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("Update","failed: "+e.getMessage());
                                Toasty.error(MainActivity_GuDDana.this, "User Id update failed", Toast.LENGTH_SHORT).show();

                            }
                        });

            }

        }

    }

    public void showDialog(){

        new BottomDialog.Builder(this)
                .setTitle("Information")
                .setContent("Email has not been verified, please verify and continue.")
                .setPositiveText("Send again")
                .setPositiveBackgroundColorResource(R.color.colorAccentt)
                .setCancelable(true)
                .onPositive(new BottomDialog.ButtonCallback() {
                    @Override
                    public void onClick(@NonNull final BottomDialog dialog) {
                        mAuth.getCurrentUser().sendEmailVerification()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dialog.dismiss();
                                        Toast.makeText(MainActivity_GuDDana.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Error", e.getMessage());
                                    }
                                });
                    }
                })
                .setNegativeText("Ok")
                .onNegative(new BottomDialog.ButtonCallback() {
                    @Override
                    public void onClick(@NonNull BottomDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    @Override
    public void onItemSelected(int position) {

        android.support.v4.app.Fragment selectedScreen;
        Log.d("position" , Integer.toString(position));

        switch (position) {

            // POS_DASHBOARD   CHAT  POS_SEND_FRIEND  POS_SEND_REQUEST POS_ABOUT  POS_LOGOUT
            case POS_DASHBOARD:
                toolbar.setTitle("Dashboard");
                try {
                    getSupportActionBar().setTitle("Dashboard");
                }catch (Exception e){
                    Log.e("Error",e.getMessage());
                }
                this.invalidateOptionsMenu();
                mState=true;
                selectedScreen = new Dashboard();
                showFragment(selectedScreen);
                slidingRootNav.closeMenu(true);

                return;

            case CHAT:
                toolbar.setTitle("GuDTalk");


                /*
                if(currentuser.isEmailVerified()) {
                    toolbar.setTitle("Flash Messages");
                    try {
                        getSupportActionBar().setTitle("Flash Messages");
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                    }
                    this.invalidateOptionsMenu();
                    mState = false;
                    selectedScreen = new FlashMessage();
                    showFragment(selectedScreen);
                    slidingRootNav.closeMenu(true);
                }else{
                    showDialog();
                }
                */



                try {
                    getSupportActionBar().setTitle("Secure Talk");

                this.invalidateOptionsMenu();
                mState=false;
                selectedScreen = new FriendsFragment();
                showFragment(selectedScreen);
                slidingRootNav.closeMenu(true);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("Error",e.getMessage());
                }



                return;



            case POS_SEND_FRIEND:

                if(currentuser.isEmailVerified()) {
                    toolbar.setTitle("Manage FRIEND");
                    try {
                        getSupportActionBar().setTitle("Manage Friend");
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                    }
                    this.invalidateOptionsMenu();
                    mState = false;
                    selectedScreen = new FriendsFragment();
                    showFragment(selectedScreen);
                    slidingRootNav.closeMenu(true);
                }else{
                    showDialog();
                }
                return ;


            case POS_ABOUT:

                // test incomming call
                // Intent intent = new Intent(this, CallIncomingActivity_rtc.class);
                // startActivity(intent);


                /*

                // test about  ...
                if(currentuser.isEmailVerified()) {
                    toolbar.setTitle("About");
                    try {
                        getSupportActionBar().setTitle("About");
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                    }
                    this.invalidateOptionsMenu();
                    mState = false;
                    selectedScreen = new About();
                    showFragment(selectedScreen);
                    slidingRootNav.closeMenu(true);
                }else{
                    showDialog();
                }

                */

                return;

            case POS_LOGOUT:

                if (currentuser != null && isOnline()) {

                    new MaterialDialog.Builder(this)
                            .title("Logout")
                            .content("Are you sure do you want to logout from this account?")
                            .positiveText("Yes")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    logout();
                                    dialog.dismiss();
                                }
                            }).negativeText("No")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            }).show();

                } else {

                    new MaterialDialog.Builder(this)
                            .title("Logout")
                            .content("A technical occurred while logging you out, Check your network connection and try again.")
                            .positiveText("Done")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            }).show();

                }

                return;

            default:
                selectedScreen = new Dashboard();
                showFragment(selectedScreen);

        }

        slidingRootNav.closeMenu(true);

    }


    public void logout() {
        performUploadTask();
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setIndeterminate(true);
        mDialog.setMessage("Logging you out...");
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        Map<String, Object> tokenRemove = new HashMap<>();
        tokenRemove.put("token_id", "");

        firestore.collection("Users").document(userId).update(tokenRemove).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                userHelper.deleteContact(1);
                mAuth.signOut();
                LoginActivity.startActivityy(MainActivity_GuDDana.this);
                mDialog.dismiss();
                finish();
                overridePendingTransitionExit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Logout Error", e.getMessage());
            }
        });


    }

    private void showFragment(android.support.v4.app.Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        mCurrentFragment=fragment;
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.colorAccentt))
                .withSelectedTextTint(color(R.color.colorAccentt));
    }

    @NonNull
    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
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

    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.hi_slide_from_right, R.anim.hi_slide_to_left);
    }

    protected void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.hi_slide_from_left, R.anim.hi_slide_to_right);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
        Cache.getTinode().setListener(null);

    }

    public void performUploadTask(){

        try{

            if(isOnline()){

                Cursor rc =userHelper.getData(1);
                rc.moveToFirst();

                try{

                    final String nam = rc.getString(rc.getColumnIndex(UserHelper.CONTACTS_COLUMN_NAME));
                    final String emai = rc.getString(rc.getColumnIndex(UserHelper.CONTACTS_COLUMN_EMAIL));
                    final String imag = rc.getString(rc.getColumnIndex(UserHelper.CONTACTS_COLUMN_IMAGE));
                    final String password = rc.getString(rc.getColumnIndex(UserHelper.CONTACTS_COLUMN_PASS));
                    final String usernam = rc.getString(rc.getColumnIndex(UserHelper.CONTACTS_COLUMN_USERNAME));
                    final String loc = rc.getString(rc.getColumnIndex(UserHelper.CONTACTS_COLUMN_LOCATION));
                    final String bi = rc.getString(rc.getColumnIndex(UserHelper.CONTACTS_COLUMN_BIO));

                    if(!rc.isClosed()){
                        rc.close();
                    }

                    FirebaseFirestore.getInstance().collection("Users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            String name = documentSnapshot.getString("name");
                            String image = documentSnapshot.getString("image");
                            final String email = documentSnapshot.getString("email");
                            String bio = documentSnapshot.getString("bio");
                            String usrname = documentSnapshot.getString("username");
                            String location = documentSnapshot.getString("location");

                            username.setText(name);
                            try{

                                Glide.with(MainActivity_GuDDana.this)
                                        .setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.default_user_art_g_2))
                                        .load(image)
                                        .into(imageView);

                            }catch(Exception ex){
                                ex.printStackTrace();

                            }


                            if (!image.equals(imag)) {
                                String url_image = image;
                                // firebase  ...
                                storageReference.putFile(Uri.parse(imag)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {

                                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(final Uri downloadUri) {
                                                    Map<String, Object> userMap = new HashMap<>();
                                                    userMap.put("image", downloadUri.toString());

                                                    FirebaseFirestore.getInstance().collection("Users").document(userId).update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            userHelper.updateContactImage(1, downloadUri.toString());
                                                            Glide.with(MainActivity_GuDDana.this)
                                                                    .setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.default_user_art_g_2))
                                                                    .load(downloadUri)
                                                                    .into(imageView);

                                                        }

                                                    });
                                                }
                                            });

                                        }
                                    }
                                });
                            }

                            if (!bio.equals(bi)) {
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("bio", bi);

                                FirebaseFirestore.getInstance().collection("Users").document(userId).update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        userHelper.updateContactBio(1, bi);

                                    }

                                });
                            }

                            if (!location.equals(loc)) {
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("location", loc);
                                FirebaseFirestore.getInstance().collection("Users").document(userId).update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        userHelper.updateContactLocation(1, loc);

                                    }

                                });
                            }

                            if (!name.equals(nam)) {
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("name", nam);
                                // save data  on satic config
                                FirebaseFirestore.getInstance().collection("Users").document(userId).update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        userHelper.updateContactName(1, nam);
                                        username.setText(nam);
                                    }

                                });
                            }

                            if (!currentuser.getEmail().equals(emai)) {


                                credential = EmailAuthProvider
                                        .getCredential(currentuser.getEmail(), password);

                                currentuser.reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                currentuser.updateEmail(emai).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {

                                                            if (!email.equals(emai)) {
                                                                Map<String, Object> userMap = new HashMap<>();
                                                                userMap.put("email", emai);

                                                                FirebaseFirestore.getInstance().collection("Users").document(userId).update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                        userHelper.updateContactEmail(1, emai);
                                                                    }

                                                                });
                                                            }

                                                        } else {

                                                            Log.e("Update email error", task.getException().getMessage() + "..");

                                                        }

                                                    }
                                                });

                                            }
                                        });
                            }
                        }
                    });


                }catch(Exception ex){
                    ex.printStackTrace();
                }

            }

        }catch(Exception ex){
            ex.printStackTrace();

        }

    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        unregisterReceiver(NetworkChangeReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onDestroy();

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void onViewProfileClicked(View view) {

        toolbar.setTitle("My Profile");
        try {
            getSupportActionBar().setTitle("My Profile");
        }catch (Exception e){
            Log.e("Error",e.getMessage());
        }
        this.invalidateOptionsMenu();
        mState=false;
        showFragment(new ProfileFragment());
        slidingRootNav.closeMenu(true);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.hi_menu_posts, menu);


        add_post=menu.findItem(R.id.action_new);
        refresh=menu.findItem(R.id.action_refresh);

        if(mState){
            add_post.setVisible(true);
            refresh.setVisible(true);
        }else{
            add_post.setVisible(false);
            refresh.setVisible(false);
        }

        //return super.onCreateOptionsMenu(menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_new:
                if(currentuser.isEmailVerified()) {
                    mBottomSheetDialog.show();
                }else{
                    showDialog();
                }
                return true;

            case R.id.action_refresh:

                // you donot show  reload the whole fragment .... once the listeners  methode from firestore

                // reload Option  we need to set flag reload to null
                loadpost_firstime = false;
                showFragment(new Dashboard());
                return true;

            case  R.id.public_mode:
                new BottomDialog.Builder(MainActivity_GuDDana.this)
                        .setTitle("Public Mode")
                        .setContent("Are you sure do you want to continue in a public mode  ?  " +
                                "you will  see all posts and offers from your  on GuDana network")
                        .setPositiveText("Yes")
                        .setPositiveBackgroundColorResource(R.color.colorAccentt)
                        .setNegativeText("No")
                        .onPositive(new BottomDialog.ButtonCallback() {
                            @Override
                            public void onClick(@NonNull BottomDialog dialog) {

                                mode_public = true;
                                loadpost_firstime = false;
                                showFragment(new Dashboard());
                                dialog.dismiss();
                            }
                        }).onNegative(new BottomDialog.ButtonCallback() {
                    @Override
                    public void onClick(@NonNull BottomDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();

                return true;


            case R.id.private_mode:

                new BottomDialog.Builder(MainActivity_GuDDana.this)
                        .setTitle("Private Mode")
                        .setContent("Are you sure do you want to continue in a private mode  ?  " +
                                "you will only see posts and offers from your friends on GuDana network")
                        .setPositiveText("Yes")
                        .setPositiveBackgroundColorResource(R.color.red_error)
                        .setNegativeText("No")
                        .onPositive(new BottomDialog.ButtonCallback() {
                            @Override
                            public void onClick(@NonNull BottomDialog dialog) {

                                mode_public = false;
                                loadpost_firstime = false;
                                showFragment(new Dashboard());
                                dialog.dismiss();
                            }
                        }).onNegative(new BottomDialog.ButtonCallback() {
                    @Override
                    public void onClick(@NonNull BottomDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    */

    // get profil get  ...

// ContactActivity Methode ...

    /**
     * This interface callback lets the main contacts list fragment notify
     * this activity that a tin_contact has been selected.
     *
     * @param contactUri The tin_contact Uri to the selected tin_contact.
     */
    @Override
    public void onContactSelected(Uri contactUri) {
        // Otherwise single pane layout, start a new ContactDetailActivity with
        // the tin_contact Uri
        //Intent intent = new Intent(this, ContactDetailActivity.class);
        //intent.setData(contactUri);
        //startActivity(intent);
    }

    /**
     * This interface callback lets the main contacts list fragment notify
     * this activity that a tin_contact is no longer selected.
     */
    @Override
    public void onSelectionCleared() {
    }


    @SuppressWarnings("unchecked")
    private void topicAttach() {
        try {
            setProgressIndicator(true);
            mMeTopic.subscribe(null, mMeTopic
                    .getMetaGetBuilder()
                    .withGetDesc()
                    .withGetSub()
                    .build())
                    .thenApply(new PromisedReply.SuccessListener() {
                        @Override
                        public PromisedReply onSuccess(Object result) throws Exception {
                            setProgressIndicator(false);
                            return null;
                        }
                    }, new PromisedReply.FailureListener() {
                        @Override
                        public PromisedReply onFailure(Exception err) throws Exception {
                            setProgressIndicator(false);
                            return null;
                        }
                    });
        } catch (NotSynchronizedException ignored) {
            setProgressIndicator(false);
            /* */
        } catch (NotConnectedException ignored) {
            /* offline - ignored */
            setProgressIndicator(false);
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        } catch (Exception err) {
            Log.i(TAG, "Subscription failed", err);
            setProgressIndicator(false);
            Toast.makeText(this,
                    "Failed to attach", Toast.LENGTH_LONG).show();
        }
    }

    private void datasetChanged() {
        mChatListAdapter.resetContent();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChatListAdapter.notifyDataSetChanged();
            }
        });
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
                ProgressBar progressBar = findViewById(R.id.toolbar_progress_bar);
                if (progressBar != null) {
                    progressBar.setVisibility(active ? View.VISIBLE : View.GONE);
                }
            }
        });
    }

    public ChatListAdapter getChatListAdapter() {
        return mChatListAdapter;
    }

    private class MeListener extends MeTopic.MeListener<VxCard> {

        @Override
        public void onInfo(MsgServerInfo info) {
            Log.d(TAG, "Contacts got onInfo update '" + info.what + "'");
        }

        @Override
        public void onPres(MsgServerPres pres) {
            if (pres.what.equals("msg")) {
                datasetChanged();
            } else if (pres.what.equals("off") || pres.what.equals("on")) {
                datasetChanged();
            }
        }

        @Override
        public void onMetaSub(final Subscription<VxCard,PrivateType> sub) {
            if (sub.pub != null) {
                sub.pub.constructBitmap();
            }
        }

        @Override
        public void onMetaDesc(final Description<VxCard,PrivateType> desc) {
            if (desc.pub != null) {
                desc.pub.constructBitmap();
            }
        }

        @Override
        public void onSubsUpdated() {
            datasetChanged();
        }

        @Override
        public void onContUpdate(final Subscription<VxCard,PrivateType> sub) {
            // Method makes no sense in context of MeTopic.
            throw new UnsupportedOperationException();
        }
    }

    public void showAccountInfoFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_EDIT_ACCOUNT);
        FragmentTransaction trx = fm.beginTransaction();
        if (fragment == null) {
            fragment = new AccountInfoFragment();
            trx.add(R.id.contentFragment, fragment, FRAGMENT_EDIT_ACCOUNT);
        }
        trx.addToBackStack(FRAGMENT_EDIT_ACCOUNT)
                .show(fragment)
                .commit();
    }

    public void selectTab(final int pageIndex) {
        FragmentManager fm = getSupportFragmentManager();
        ContactsFragment contacts = (ContactsFragment) fm.findFragmentByTag(FRAGMENT_CONTACTS);
        contacts.selectTab(pageIndex);
    }

    private class ContactsEventListener extends UiUtils.EventListener {
        ContactsEventListener(boolean online) {
            super(MainActivity_GuDDana.this, online);
        }

        @Override
        public void onLogin(int code, String txt) {
            super.onLogin(code, txt);
            topicAttach();
        }
    }
}