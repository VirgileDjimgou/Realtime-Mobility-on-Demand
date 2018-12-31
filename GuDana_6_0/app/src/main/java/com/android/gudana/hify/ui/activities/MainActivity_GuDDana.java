package com.android.gudana.hify.ui.activities;

import android.Manifest;
import android.app.ActivityManager;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.gudana.BootNavigation.BaseActivity;
// import com.android.gudana.apprtc.CallIncomingActivity_rtc;
import com.android.gudana.apprtc.linphone.LinphoneManager;
import com.android.gudana.chat.activities.MenuActivity;
import com.android.gudana.chat.fragments.ChatFragment;
import com.android.gudana.chat.fragments.Friends_Contact_Fragment;
import com.android.gudana.chatapp.fragments.CallFragment;
import com.android.gudana.gpslocationtracking.LocationTrack;
import com.android.gudana.hify.adapters.DrawerAdapter;
import com.android.gudana.hify.models.DrawerItem;
import com.android.gudana.hify.models.SimpleItem;
import com.android.gudana.hify.ui.activities.account.StartLoginActivity;
import com.android.gudana.hify.ui.activities.post.PostImage;
import com.android.gudana.hify.ui.activities.post.PostText;
import com.android.gudana.hify.ui.fragment.Dashboard;
import com.android.gudana.hify.ui.fragment.Friends;
import com.android.gudana.hify.ui.fragment.FriendsFragment;
import com.android.gudana.hify.ui.fragment.ProfileFragment;
import com.android.gudana.hify.utils.Config;
import com.android.gudana.hify.utils.NetworkUtil;
import com.android.gudana.hify.utils.database.UserHelper;
import com.android.gudana.R;
// import com.android.gudana.linphone.DialerFragment;
import com.android.gudana.hify.utils.database.live_location_sharing_db;
import com.android.gudana.hify.utils.random_Utils;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import it.sephiroth.android.library.bottomnavigation.BadgeProvider;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.util.Log.INFO;
import static android.util.Log.VERBOSE;
import static it.sephiroth.android.library.bottomnavigation.MiscUtils.log;

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

public class MainActivity_GuDDana extends BaseActivity implements DrawerAdapter.OnItemSelectedListener ,
        BottomNavigation.OnMenuItemSelectionListener{


    private String username_chat, session;
    private int user_id = -1;
    static Fragment roomsFragment , ChatFragment ,Friends_Contact_Fragment ;
    Fragment friendsListFragment;
    private Intent intent;
    public CoordinatorLayout coordinatorLayout;
    ChatApplication chatApplication;



    // POS_DASHBOARD   CHAT  POS_SEND_FRIEND   POS_ABOUT  POS_LOGOUT

    static final String TAG = com.android.gudana.BootNavigation.MainActivity.class.getSimpleName();
    static final String FRAGMENT_CONTACTS = "contacts";
    static final String FRAGMENT_EDIT_ACCOUNT = "edit_account";


    private FirebaseFirestore mFirestore;

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
        askPermission();
        FirebaseApp.initializeApp(this);
        askPermission();
        // set offline capiblities    ...
        try{
            askPermission();
            ctx = this;
            setContentView(R.layout.hi_activity_main);
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_main);

            FirebaseApp.initializeApp(MainActivity_GuDDana.this.getApplicationContext());

        }catch (Exception ex){
            ex.printStackTrace();
        }

        // added

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



        if(chatApplication.isLoggedIn() && intent.getBooleanExtra("returning user", false)) {

            Snackbar.make(coordinatorLayout, "Signed in as " + username, Snackbar.LENGTH_LONG)
                    .setAction("Not you?", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new Logout(MainActivity_GuDDana.this, user_id, username_chat, session);
                        }
                    }).show();
        }

        // end added  ...
        askPermission();

        final ViewGroup root = (ViewGroup) findViewById(R.id.activity_main);
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
        toolbar.setTitle("OkMboa");

            getSupportActionBar().setTitle("OkMboa");
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

                mSensorService = new SensorService(getCtx());
                mServiceIntent = new Intent(getCtx(), mSensorService.getClass());

                mServiceIntent.putExtra("From", object.toString());
                if (!isMyServiceRunning(mSensorService.getClass())) {
                    startService(mServiceIntent);
                }

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
                    //return  roomsFragment ;
                    return  ChatFragment;

                case 2:
                    // Call fragment
                    return new CallFragment();
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

        android.support.v4.app.Fragment selectedScreen;
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
        Snackbar.make(coordinatorLayout, "Unable to connect to server", Snackbar.LENGTH_LONG).show();
    }


}
