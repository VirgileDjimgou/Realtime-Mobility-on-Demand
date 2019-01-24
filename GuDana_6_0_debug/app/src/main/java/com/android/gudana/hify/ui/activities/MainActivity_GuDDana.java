package com.android.gudana.hify.ui.activities;

import android.Manifest;
import android.app.ActivityManager;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.gudana.chat.fragments.Calls_Fragment;
// import com.android.gudana.chatapp.fragments.CallFragment;
import com.android.gudana.hify.adapters.BottomBarAdapter;
import com.android.gudana.hify.adapters.NoSwipePager;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.gudana.apprtc.linphone.LinphoneManager;
import com.android.gudana.chat.activities.MenuActivity;
import com.android.gudana.chat.fragments.ChatFragment;
import com.android.gudana.chat.fragments.Friends_Contact_Fragment;
import com.android.gudana.gpslocationtracking.LocationTrack;
import com.android.gudana.hify.adapters.DrawerAdapter;
import com.android.gudana.hify.models.DrawerItem;
import com.android.gudana.hify.models.SimpleItem;
import com.android.gudana.hify.ui.activities.account.StartLoginActivity;
import com.android.gudana.hify.ui.activities.post.PostImage;
import com.android.gudana.hify.ui.activities.post.PostText;
import com.android.gudana.hify.ui.fragment.Dashboard;
import com.android.gudana.hify.ui.fragment.FriendsFragment;
import com.android.gudana.hify.ui.fragment.ProfileFragment;
import com.android.gudana.hify.utils.Config;
import com.android.gudana.hify.utils.NetworkUtil;
import com.android.gudana.hify.utils.Test_RestHttp_Server_Activity;
import com.android.gudana.hify.utils.database.UserHelper;
import com.android.gudana.R;
import com.android.gudana.hify.utils.database.live_location_sharing_db;
import com.android.gudana.service.SensorService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.android.gudana.hify.utils.Config.cacheExpiration;

import com.android.gudana.chat.ChatApplication;
import com.android.gudana.chat.fragments.FriendsListFragment;
import com.android.gudana.chat.fragments.RoomsFragment;
import com.android.gudana.chat.model.User;
import com.android.gudana.chat.network.Logout;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by amsavarthan on 29/3/18.
 */

public class MainActivity_GuDDana extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {


    private String username_chat, session;
    private int user_id = -1;
    public static Fragment roomsFragment   ,Friends_Contact_Fragment ;
    public static ChatFragment ChatFragment;
    public static Dashboard Dashboard;
    //public static CallFragment CallFragment;
    public static FriendsFragment FriendsFragment;
    public  static  ProfileFragment ProfileFragment;

    Fragment friendsListFragment;
    private Intent intent;
    public CoordinatorLayout Coordinate_Layout;
    ChatApplication chatApplication;


    private final int[] colors = {R.color.bottomtab_0, R.color.bottomtab_1, R.color.bottomtab_2};
    public static NoSwipePager viewPager;
    public static AHBottomNavigation bottomNavigation;
    public BottomBarAdapter pagerAdapter;
    public static boolean notificationVisible = false;


    // notification  Item navigation ...
    public static  Integer unreadChat = 0;
    public static  Integer missedCall = 0;

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

    // Remote Config keys
    public static final String Server_MASTER = "Server_MASTER";
    public static final String Server_BACKUP = "Server_BACKUP";
    public static final String Server_Debug = "Server_Debug";

    public static FirebaseRemoteConfig mFirebaseRemoteConfig;
    public static TextView mWelcomeTextView;


    // variable for backroung service

    Intent mServiceIntent;
    private SensorService mSensorService;
    Context ctx;
    private LocationTrack locationTrack;

    public Context getCtx() {
        return ctx;
    }


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
                        Snackbar.make(findViewById(R.id.CoordinatorLayout01), "Syncing...", Snackbar.LENGTH_LONG).show();
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

    public static void startActivity(Context context) {
        Intent intent=new Intent(context,MainActivity_GuDDana.class);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        askPermission();
        slidingRootNav.closeMenu(true);
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStop() {
        super.onStop();
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
            //LoginActivity.startActivityy(this);

            Intent STartLogin = new Intent(MainActivity_GuDDana.this, StartLoginActivity.class);
            MainActivity_GuDDana.this.startActivity(STartLogin);
            MainActivity_GuDDana.this.finish();
        }
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //boolean enabledTranslucentNavigation = true;
        //setTheme(enabledTranslucentNavigation ? R.style.AppTheme_TranslucentNavigation : R.style.AppTheme);

        setContentView(R.layout.hi_activity_main);
        FirebaseApp.initializeApp(this);
        //askPermission();
        // set offline capiblities    ...
        try{
            askPermission();
            ctx = this;
            Coordinate_Layout = (CoordinatorLayout) findViewById(R.id.activity_main);
            Coordinate_Layout.setFitsSystemWindows(true);

            FirebaseApp.initializeApp(MainActivity_GuDDana.this.getApplicationContext());


        }catch (Exception ex){
            ex.printStackTrace();
        }


        intent = getIntent();
        chatApplication = (ChatApplication) MainActivity_GuDDana.this.getApplication();
        User user = chatApplication.getUser();
        username_chat = user.getUsername();
        user_id = user.getUserID();
        session = user.getSession();

        Friends_Contact_Fragment = new Friends_Contact_Fragment();
        ChatFragment = new ChatFragment();
        roomsFragment = new RoomsFragment();
        friendsListFragment = new FriendsListFragment();
        Bundle fragmentArguments = new Bundle();
        fragmentArguments.putInt("user_id", user_id);
        fragmentArguments.putString("username", username_chat);
        fragmentArguments.putString("session", session);
        roomsFragment.setArguments(fragmentArguments);
        friendsListFragment.setArguments(fragmentArguments);

        // set remote Config server ...
        // Get Remote Config instance.
        // [START get_remote_config_instance]
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        // [END get_remote_config_instance]

        // Create a Remote Config Setting to enable developer mode, which you can use to increase
        // the number of fetches available per hour during development. See Best Practices in the
        // README for more information.
        // [START enable_dev_mode]
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        // [END enable_dev_mode]

        // Set default Remote Config parameter values. An app uses the in-app default values, and
        // when you need to adjust those defaults, you set an updated value for only the values you
        // want to change in the Firebase console. See Best Practices in the README for more
        // information.
        // [START set_default_values]
        // mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        // [END set_default_values]

        if(chatApplication.isLoggedIn() && intent.getBooleanExtra("returning user", false)) {

            Snackbar.make(Coordinate_Layout, "Signed in as " + username, Snackbar.LENGTH_LONG)
                    .setAction("Not you?", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new Logout(MainActivity_GuDDana.this, user_id, username_chat, session);
                        }
                    }).show();
        }

        // end added  ...
        askPermission();

        final ViewGroup root = (ViewGroup) findViewById(R.id.CoordinatorLayout01);
        final CoordinatorLayout coordinatorLayout;
        if (root instanceof CoordinatorLayout) {
            coordinatorLayout = (CoordinatorLayout) root;
        } else {
            coordinatorLayout = null;
        }

        try {

        activity=this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(getResources().getColor(R.color.purple));

            getSupportActionBar().setTitle("OkMboa");
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        // add bottom OnCreate  ...

        try{

            userHelper = new UserHelper(this);
            firestore = FirebaseFirestore.getInstance();
        }catch (Exception ex){
            ex.printStackTrace();
        }


        registerReceiver(NetworkChangeReceiver
                , new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        try{
            FirebaseApp.initializeApp(this);
            mAuth = FirebaseAuth.getInstance();
            currentuser = mAuth.getCurrentUser();
        }catch (Exception ex){
            ex.printStackTrace();
        }


        if (currentuser == null) {

            //LoginActivity.startActivityy(this);

            Intent STartLogin = new Intent(MainActivity_GuDDana.this, StartLoginActivity.class);
            MainActivity_GuDDana.this.startActivity(STartLogin);
            MainActivity_GuDDana.this.finish();
            //this.finish();

        } else {


            mCurrentFragment = new Dashboard();
            //firebaseMessagingService();

            askPermission();

            userId = currentuser.getUid();
            storageReference = FirebaseStorage.getInstance().getReference().child("images").child(currentuser.getUid() + ".jpg");

            slidingRootNav = new SlidingRootNavBuilder(this)
                    .withToolbarMenuToggle(toolbar)
                    .withMenuOpened(false)
                    .withContentClickableWhenMenuOpened(false)
                    .withSavedState(savedInstanceState)
                    .withMenuLayout(R.layout.hi_activity_main_drawer)
                    .inject();

            screenIcons = loadScreenIcons();
            screenTitles = loadScreenTitles();


            // start live location
            // always chech if  gps enabled  if not  ... than don't start the gps Sharing service
            locationTrack = new LocationTrack(MainActivity_GuDDana.this);
            // chech if gps is enable  ....
            if (locationTrack.canGetLocation()) {

                double longitude = locationTrack.getLongitude();
                double latitude = locationTrack.getLatitude();

                // String Live_EventName = "live_location_"+random_Utils.getRandom(1000,1000000);
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                String message_live = "Live Location ";
                String Id_live_creator = FirebaseAuth.getInstance().getUid();
                String Active = "true";
                JSONObject object=new JSONObject();
                try {

                    object.put(live_location_sharing_db.CONTACTS_COLUMN_ID_USER, Id_live_creator);
                    object.put(live_location_sharing_db.CONTACTS_COLUMN_MESSAGE, message_live);
                    object.put(live_location_sharing_db.CONTACTS_COLUMN_ACTIV, Active);
                    object.put(live_location_sharing_db.CONTACTS_TABLE_MATRITCULE_LIVE ,Config.UID_EVENT_LOCATION_LIVE_CHANNEL);
                    object.put(live_location_sharing_db.CONTACTS_COLUMN_START_TIME , timeStamp);
                    object.put(live_location_sharing_db.CONTACTS_COLUMN_START_TIME , "test");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /*
                mSensorService = new SensorService(getCtx());
                mServiceIntent = new Intent(getCtx(), mSensorService.getClass());

                mServiceIntent.putExtra("From", object.toString());
                if (!isMyServiceRunning(mSensorService.getClass())) {
                    startService(mServiceIntent);
                }
                */

            } else {
                // stop the service  locaion sharing
                try{
                    stopService(mServiceIntent);

                }catch (Exception ex){
                    ex.printStackTrace();
                }

                // askPermission();
                //  you must activated settings
                // locationTrack.showSettingsAlert();

                // locationTrack.showSettingsAlert();
            }


            // stopService(mServiceIntent);

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
            // chatSeenDatabase.keepSynced(true); // For offline use

        }

        // setup   bottom navigation  ..

        setupViewPager();
        setupBottom_Navigation();

        fetchServerConfig();

        //test botification   ...
        //IncNotification(1);
    }


    // get  server Config



    private void setupBottom_Navigation(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        setupBottomNavStyle();

        //createFakeNotification();

        addBottomNavigationItems();
        bottomNavigation.setCurrentItem(0);

        //bottomNavigation.setTranslucentNavigationEnabled(true);
        Coordinate_Layout.setFitsSystemWindows(true);
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
//                fragment.updateColor(ContextCompat.getColor(MainActivity.this, colors[position]));

                if (!wasSelected)
                    viewPager.setCurrentItem(position);

                // remove notification badge
                int lastItemPos = bottomNavigation.getItemsCount() - 1;
                if (notificationVisible && position == lastItemPos)
                    bottomNavigation.setNotification(new AHNotification(), lastItemPos);

                return true;
            }
        });

    }

    private void setupViewPager() {
        // create test Fragment
        viewPager = (NoSwipePager) findViewById(R.id.NoSwipePager_viewpager);
        viewPager.setPagingEnabled(false);
        pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());

        pagerAdapter.addFragments(new Dashboard());
        pagerAdapter.addFragments(ChatFragment);
        pagerAdapter.addFragments(new Calls_Fragment());
        pagerAdapter.addFragments(new FriendsFragment());
        pagerAdapter.addFragments(new ProfileFragment());


        viewPager.setAdapter(pagerAdapter);
    }


    public static void IncNotification(final Integer Item ) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Item == 1){
                    /// Chat Notification ...
                    AHNotification notification = new AHNotification.Builder()
                            .setText(Integer.toString(unreadChat))
                            .setBackgroundColor(R.color.purple)
                            .setTextColor(Color.WHITE)
                            .build();
                    // Adding notification to Chat item.

                    bottomNavigation.setNotification(notification, Item);

                    if(unreadChat >0){
                        notificationVisible = true;

                    }else {
                        notificationVisible = false;
                    }

                }else if(Item == 2){
                    // Call notification
                    AHNotification notification = new AHNotification.Builder()
                            .setText(Integer.toString(missedCall))
                            .setBackgroundColor(Color.GREEN)
                            .setTextColor(Color.WHITE)
                            .build();
                    // Adding notification to Chat item.

                    bottomNavigation.setNotification(notification, Item);

                    if( missedCall>0){
                        notificationVisible = true;

                    }else {
                        notificationVisible = false;
                    }

                }
            }
        }, 1000);
    }

    private void DeccrementChatNotification() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AHNotification notification = new AHNotification.Builder()
                        .setText("1")
                        .setBackgroundColor(Color.YELLOW)
                        .setTextColor(Color.BLACK)
                        .build();
                // Adding notification to last item.

                bottomNavigation.setNotification(notification, 0);

                notificationVisible = true;
            }
        }, 1000);
    }


    /**
     * Adds styling properties to {@link AHBottomNavigation}
     */
    private void setupBottomNavStyle() {
        /*
        Set Bottom Navigation colors. Accent color for active item,
        Inactive color when its view is disabled.

        Will not be visible if setColored(true) and default current item is set.
         */
        bottomNavigation.setDefaultBackgroundColor(Color.WHITE);
        bottomNavigation.setAccentColor(fetchColor(R.color.colorAccentt));
        bottomNavigation.setInactiveColor(fetchColor(R.color.black_overlay));

        // Colors for selected (active) and non-selected items.
        bottomNavigation.setColoredModeColors(R.color.white,
                fetchColor(R.color.black_overlay));

        //  Enables Reveal effect
        // bottomNavigation.setColored(true);

        //  Displays item Title always (for selected and non-selected items)
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
    }


    /**
     * Adds (items) {@link AHBottomNavigationItem} to {@link AHBottomNavigation}
     * Also assigns a distinct color to each Bottom Navigation item, used for the color ripple.
     */
    private void addBottomNavigationItems() {
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.nav_home, R.drawable.ic_home_purple_24dp, colors[0]);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.nav_chats, R.drawable.ic_chat_purple_24dp, colors[1]);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.nav_calls, R.drawable.ic_call_black_24dp, colors[2]);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.nav_friends, R.drawable.ic_friend, colors[2]);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem(R.string.nav_calls, R.drawable.ic_person_white, colors[2]);


        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);
        bottomNavigation.addItem(item5);
    }






    /**
     * Simple facade to fetch color resource, so I avoid writing a huge line every time.
     *
     * @param color to fetch
     * @return int color value.
     */
    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }


    /**
     * Fetch a welcome message from the Remote Config service, and then activate it.
     */
    public  void fetchServerConfig() {
        //mWelcomeTextView.setText(mFirebaseRemoteConfig.getString(LOADING_PHRASE_CONFIG_KEY));

        // long cacheExpiration = 3600; // 1 hour in seconds.
        // or  for Debug
        ; // 30 seconds.


        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        // See Best Practices in the README for more information.
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(MainActivity_GuDDana.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toasty.info(MainActivity_GuDDana.this, "Fetch Succeeded",
                                    Toast.LENGTH_SHORT).show();

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                            // [START get_config_values]
                            String Server_MASTER_Adr = mFirebaseRemoteConfig.getString(Server_MASTER);
                            String Server_BACKUP_Adr = mFirebaseRemoteConfig.getString(Server_BACKUP);
                            String Media_Server = mFirebaseRemoteConfig.getString("Media_Server");
                            //String Server_MASTER = mFirebaseRemoteConfig.getString(Server_MASTER);
                            Config.Server_Adresse = Server_MASTER_Adr;
                            Config.Media_Server = Media_Server;


                            // File upload url (replace the ip with your server address)// 35.237.197.121
                            Config.FILE_UPLOAD_URL = Media_Server.trim() + "/AndroidFileUpload/fileUpload.php";
                            Config.IMAGES_UPLOAD_URL = Media_Server.trim() + "/AndroidFileUpload/Images_fileUpload.php";
                            Config.VIDEOS_UPLOAD_URL = Media_Server.trim() + "/AndroidFileUpload/Videos_fileUpload.php";
                            Config.URL_CHAT_SERVER = Server_MASTER_Adr.trim() +  ":5000";


                        } else{
                            Toasty.info(MainActivity_GuDDana.this, "get remote Config  failed  ..  chak your  Internet connection ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END fetch_config_with_callback]
    }

    //##############################


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
                    //return  roomsFragment ;
                    return  ChatFragment;

                case 2:
                    // Call fragment
                    return new Dashboard();
                //return Friends_Contact_Fragment;
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

    // ##################

    private void askPermission() {

        Dexter.withActivity(MainActivity_GuDDana.this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
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


            String email = rs.getString(rs.getColumnIndex(UserHelper.CONTACTS_COLUMN_EMAIL));
            String pass = rs.getString(rs.getColumnIndex(UserHelper.CONTACTS_COLUMN_PASS));

           // Toasty.info(activity, email+ "  Pass : "+ pass, Toast.LENGTH_SHORT).show();

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
            // alwaway close cursor
            ex.printStackTrace();
        }finally {
            // this gets called even if there is an exception somewhere above
            if(rs != null)
                rs.close();
        }

        try{

            // this gets called even if there is an exception somewhere above
            if(rs != null)
                rs.close();

        }catch (Exception ex){
            ex.printStackTrace();
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

        Fragment selectedScreen;
        Log.d("position" , Integer.toString(position));

        switch (position) {

            // POS_DASHBOARD   CHAT  POS_SEND_FRIEND  POS_SEND_REQUEST POS_ABOUT  POS_LOGOUT
            case POS_DASHBOARD:
                toolbar.setTitle("OkMboa");
                try {
                    getSupportActionBar().setTitle("OkMboa");
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
                // toolbar.setTitle("GuDTalk");
                Intent menuIntent = new Intent(MainActivity_GuDDana.this, MenuActivity.class);
                startActivity(menuIntent);
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

                Intent  TestApi_Activity = new Intent(MainActivity_GuDDana.this, Test_RestHttp_Server_Activity.class);
                startActivity(TestApi_Activity);


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

                // logging out from Chat server   ...
                new Logout(MainActivity_GuDDana.this, user_id, username_chat, session);


                Intent STartLogin = new Intent(MainActivity_GuDDana.this, StartLoginActivity.class);
                MainActivity_GuDDana.this.startActivity(STartLogin);
                MainActivity_GuDDana.this.finish();

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

    private void showFragment(Fragment fragment) {
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
        super.onPause();
        //Cache.getTinode().setListener(null);

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
                }// alway close cursor
                finally {
                    // this gets called even if there is an exception somewhere above
                    if(rc != null)
                        rc.close();
                }

                try{

                    // this gets called even if there is an exception somewhere above
                    if(rc != null)
                        rc.close();

                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }

        }catch(Exception ex){
            ex.printStackTrace();

        }

    }

    @Override
    protected void onDestroy() {
        try{
            unregisterReceiver(NetworkChangeReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        }catch (Exception ex){
            ex.printStackTrace();
        }

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


    public void unableToConnectSnackBar() {
        Snackbar.make(Coordinate_Layout, "Unable to connect to server", Snackbar.LENGTH_LONG).show();
    }


}
