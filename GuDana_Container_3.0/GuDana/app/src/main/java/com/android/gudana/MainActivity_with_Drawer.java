package com.android.gudana;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.cardview.Card_Home_fragment;
import com.android.gudana.chatapp.MainActivity;
import com.android.gudana.chatapp.activities.PhoneAuthActivity;
import com.android.gudana.chatapp.activities.ProfileActivity;
import com.android.gudana.chatapp.activities.UsersActivity;
import com.android.gudana.chatapp.activities.WelcomeActivity;
import com.android.gudana.chatapp.fragments.ChatFragment;
import com.android.gudana.chatapp.fragments.FriendsFragment;
import com.android.gudana.chatapp.fragments.RequestsFragment;
import com.android.gudana.linphone.AccountPreferencesFragment;
import com.android.gudana.linphone.CallActivity;
import com.android.gudana.linphone.CallIncomingActivity;
import com.android.gudana.linphone.CallOutgoingActivity;
import com.android.gudana.linphone.ContactsManager;
import com.android.gudana.linphone.DialerFragment;
import com.android.gudana.linphone.EmptyFragment;
import com.android.gudana.linphone.FragmentsAvailable;
import com.android.gudana.linphone.HistoryDetailFragment;
import com.android.gudana.linphone.HistoryListFragment;
import com.android.gudana.linphone.LinphoneContact;
import com.android.gudana.linphone.LinphoneLauncherActivity;
import com.android.gudana.linphone.LinphoneManager;
import com.android.gudana.linphone.LinphonePreferences;
import com.android.gudana.linphone.LinphoneService;
import com.android.gudana.linphone.LinphoneUtils;
import com.android.gudana.linphone.SettingsFragment;
import com.android.gudana.linphone.StatusFragment;
import com.android.gudana.linphone.assistant.AssistantActivity;
import com.android.gudana.linphone.assistant.RemoteProvisioningLoginActivity;
import com.android.gudana.linphone.compatibility.Compatibility;
import com.android.gudana.linphone.purchase.InAppPurchaseActivity;
import com.android.gudana.linphone.ui.AddressText;
import com.android.gudana.linphone.xmlrpc.XmlRpcHelper;
import com.android.gudana.linphone.xmlrpc.XmlRpcListenerBase;
import com.android.gudana.util.Client;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.acra.util.ToastSender;
import org.linphone.core.CallDirection;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCallLog;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.Reason;
import org.linphone.mediastream.Log;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

// import rehanced.com.simpleetherwallet.activities.AnalyticsApplication;

public class MainActivity_with_Drawer extends AppCompatActivity  implements View.OnClickListener, ContactPicked_Drawer, ActivityCompat.OnRequestPermissionsResultCallback{


    // addded   ...
    public static final String PREF_FIRST_LAUNCH = "pref_first_launch";
    private static final int SETTINGS_ACTIVITY = 123;
    private static final int CALL_ACTIVITY = 19;
    private static final int PERMISSIONS_REQUEST_OVERLAY = 206;
    private static final int PERMISSIONS_REQUEST_SYNC = 207;
    private static final int PERMISSIONS_REQUEST_CONTACTS = 208;
    private static final int PERMISSIONS_RECORD_AUDIO_ECHO_CANCELLER = 209;
    private static final int PERMISSIONS_READ_EXTERNAL_STORAGE_DEVICE_RINGTONE = 210;
    private static final int PERMISSIONS_RECORD_AUDIO_ECHO_TESTER = 211;
    private LinphoneAddress.TransportType transport;

    private LinphonePreferences mPrefs;
    private static MainActivity_with_Drawer instance;

    private StatusFragment statusFragment;
    private TextView missedCalls, missedChats;
    private RelativeLayout contacts, history, dialer, chat;
    private View contacts_selected, history_selected, dialer_selected, chat_selected;
    private RelativeLayout mTopBar;
    private ImageView cancel;
    private FragmentsAvailable pendingFragmentTransaction, currentFragment;
    private Fragment fragment;
    private List<FragmentsAvailable> fragmentsHistory;
    private Fragment.SavedState dialerSavedState;
    private boolean newProxyConfig;
    private boolean emptyFragment = false;
    private boolean isTrialAccount = false;
    private OrientationEventListener mOrientationHelper;
    private LinphoneCoreListenerBase mListener;
    private LinearLayout mTabBar;

    private DrawerLayout sideMenu;
    private RelativeLayout sideMenuContent, quitLayout, defaultAccount;
    private ListView accountsList, sideMenuItemList;
    private ImageView menu ;
    private boolean fetchedContactsOnce = false;
    private boolean doNotGoToCallActivity = false;
    private List<String> sideMenuItems;
    private boolean callTransfer = false;
    public static  String password_asterisk = null;
    public static String server_adresse = null;
    public static String  Users_Id_asterisk = null ;

    // add

    //end add

    public static final boolean isInstanciated() {
        return instance != null;
    }

    public static final MainActivity_with_Drawer instance() {
        if (instance != null)
            return instance;
        throw new RuntimeException("MainActivity_with_Drawer not instantiated yet");
    }




    // end add  ..


    private SectionsPagerAdapter mSectionsPagerAdapter;
    public static  ViewPager mViewPager;
    public  Fragment[] fragments;
    public static TabLayout tabLayout;
    private CoordinatorLayout coord;
    private SharedPreferences preferences;
    private AppBarLayout appbar;
    private int generateRefreshCount;
    private Client myClient;

    public static   AccountHeader headerResult = null;

    private DatabaseReference userDB;
    private DatabaseReference AsteriskUsersIndex;
    private ValueEventListener AsteriskListener = null;
    private ValueEventListener postListener = null;
    public static FirebaseUser user_Global;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main_nav);
        setContentView(R.layout.main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        if (!LinphoneManager.isInstanciated()) {
            finish();
            startActivity(getIntent().setClass(this, LinphoneLauncherActivity.class));
            return;
        }

        boolean useFirstLoginActivity = getResources().getBoolean(R.bool.display_account_assistant_at_first_start);
        if (LinphonePreferences.instance().isProvisioningLoginViewEnabled()) {
            Intent wizard = new Intent();
            wizard.setClass(this, RemoteProvisioningLoginActivity.class);
            wizard.putExtra("Domain", LinphoneManager.getInstance().wizardLoginViewDomain);
            startActivity(wizard);
            finish();
            return;
        } else if (savedInstanceState == null && (useFirstLoginActivity && LinphonePreferences.instance().isFirstLaunch())) {
            if (LinphonePreferences.instance().getAccountCount() > 0) {
                LinphonePreferences.instance().firstLaunchSuccessful();
            } else {
                //startActivity(new Intent().setClass(this, AssistantActivity.class));
                //finish();
                //return;
            }
        }


        if (getIntent() != null && getIntent().getExtras() != null) {
            newProxyConfig = getIntent().getExtras().getBoolean("isNewProxyConfig");
        }


        if (getResources().getBoolean(R.bool.use_linphone_tag)) {
            if (getPackageManager().checkPermission(Manifest.permission.WRITE_SYNC_SETTINGS, getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                checkSyncPermission();
            } else {
                ContactsManager.getInstance().initializeSyncAccount(getApplicationContext(), getContentResolver());
            }
        } else {
            ContactsManager.getInstance().initializeContactManager(getApplicationContext(), getContentResolver());
        }

        // setContentView(R.layout.main);

        // add ....


        instance = this;
        fragmentsHistory = new ArrayList<FragmentsAvailable>();
        pendingFragmentTransaction = FragmentsAvailable.UNKNOW;

        // initButtons();
        //initSideMenu();

        currentFragment = FragmentsAvailable.EMPTY;
        if (savedInstanceState == null) {
            changeCurrentFragment(FragmentsAvailable.DIALER, getIntent().getExtras());
        } else {
            currentFragment = (FragmentsAvailable) savedInstanceState.getSerializable("currentFragment");
        }

        mListener = new LinphoneCoreListenerBase(){
            @Override
            public void messageReceived(LinphoneCore lc, LinphoneChatRoom cr, LinphoneChatMessage message) {

            }

            @Override
            public void registrationState(LinphoneCore lc, LinphoneProxyConfig proxy, LinphoneCore.RegistrationState state, String smessage) {
                if (state.equals(LinphoneCore.RegistrationState.RegistrationCleared)) {
                    if (lc != null) {
                        LinphoneAuthInfo authInfo = lc.findAuthInfo(proxy.getIdentity(), proxy.getRealm(), proxy.getDomain());
                        if (authInfo != null)
                            lc.removeAuthInfo(authInfo);
                    }
                }

                // refreshAccounts();

                if(getResources().getBoolean(R.bool.use_phone_number_validation)) {
                    if (state.equals(LinphoneCore.RegistrationState.RegistrationOk)) {
                        LinphoneManager.getInstance().isAccountWithAlias();
                    }
                }

                if(state.equals(LinphoneCore.RegistrationState.RegistrationFailed) && newProxyConfig) {
                    newProxyConfig = false;
                    if (proxy.getError() == Reason.BadCredentials) {
                        //displayCustomToast(getString(R.string.error_bad_credentials), Toast.LENGTH_LONG);
                    }
                    if (proxy.getError() == Reason.Unauthorized) {
                        displayCustomToast(getString(R.string.error_unauthorized), Toast.LENGTH_LONG);
                    }
                    if (proxy.getError() == Reason.IOError) {
                        displayCustomToast(getString(R.string.error_io_error), Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void callState(LinphoneCore lc, LinphoneCall call, LinphoneCall.State state, String message) {
                if (state == LinphoneCall.State.IncomingReceived) {
                    startActivity(new Intent(MainActivity_with_Drawer.instance(), CallIncomingActivity.class));
                } else if (state == LinphoneCall.State.OutgoingInit || state == LinphoneCall.State.OutgoingProgress) {
                    startActivity(new Intent(MainActivity_with_Drawer.instance(), CallOutgoingActivity.class));
                } else if (state == LinphoneCall.State.CallEnd || state == LinphoneCall.State.Error || state == LinphoneCall.State.CallReleased) {
                    resetClassicMenuLayoutAndGoBackToCallIfStillRunning();
                }

                int missedCalls = LinphoneManager.getLc().getMissedCallsCount();
                displayMissedCalls(missedCalls);
            }
        };

        int missedCalls = LinphoneManager.getLc().getMissedCallsCount();
        displayMissedCalls(missedCalls);

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                rotation = 0;
                break;
            case Surface.ROTATION_90:
                rotation = 90;
                break;
            case Surface.ROTATION_180:
                rotation = 180;
                break;
            case Surface.ROTATION_270:
                rotation = 270;
                break;
        }

        LinphoneManager.getLc().setDeviceRotation(rotation);
        mAlwaysChangingPhoneAngle = rotation;


        // ------------------------- Material Drawer ---------------------------------

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.colorPrimaryDark)
                .addProfiles(
                        new ProfileDrawerItem().
                                withName("Gudana Username").
                                withEmail("gudana_user@gud.com").
                                withTextColor( getResources().getColor(R.color.colorWhite)).
                                withNameShown(true).
                                withIcon(getResources().getDrawable(R.mipmap.ic_person_gud_round))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        //headerResult.setSelectionFirstLine("@string/app_name");
        //headerResult.setSelectionFirstLineShown(true);
        //headerResult.setSelectionSecondLine("@string/slogan");
        //headerResult.setSelectionSecondLineShown(true);



        DrawerBuilder wip = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withSelectedItem(-1)

                .addDrawerItems(
                        new PrimaryDrawerItem().withName(getResources().getString(R.string.app_name)).withIcon(R.mipmap.ic_launcher_round),
                        new PrimaryDrawerItem().withName(getResources().getString(R.string.action_settings)).withIcon(R.mipmap.ic_settings_round),
                        new PrimaryDrawerItem().withName(getResources().getString(R.string.help)).withIcon(R.mipmap.ic_help_round),
                        new PrimaryDrawerItem().withName(getResources().getString(R.string.about)).withIcon(R.mipmap.ic_about_round)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        selectItem(position);
                        return false;
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {

                    @Override
                    public void onDrawerOpened(View drawerView) {

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        //changeStatusBarColor();
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        //changeStatusBarTranslucent();
                    }
                });


        Drawer result = wip.build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);



        // ------------------------------------------------------------------------

        coord = findViewById(R.id.main_content);
        appbar = findViewById(R.id.appbar);
        checkAndRequestReadContactsPermission();

        fragments = new Fragment[6];
        fragments[0] = new Card_Home_fragment();
        fragments[1] = new RequestsFragment();
        fragments[2] = new HistoryListFragment();
        fragments[3] = new ChatFragment();
        fragments[4] = new FriendsFragment();
        fragments[5] = new DialerFragment();



        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.mipmap.ic_home);
        tabLayout.getTabAt(1).setIcon(R.mipmap.ic_requests);
        tabLayout.getTabAt(2).setIcon(R.mipmap.ic_call_history);
        tabLayout.getTabAt(3).setIcon(R.mipmap.ic_chat);
        tabLayout.getTabAt(4).setIcon(R.mipmap.ic_friend);
        tabLayout.getTabAt(5).setIcon(R.mipmap.ic_dialer);

        mViewPager.setOffscreenPageLimit(6);
        //tabLayout.getTabAt(3);
        //mViewPager.setCurrentItem(3);



        try{

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            // control your Asterisk Credentials on asterisk Server and Firebase
            user_Global = FirebaseAuth.getInstance().getCurrentUser();
            userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(user_Global.getUid());
            HashMap map = new HashMap();
            //map.put("Authentified" , "await");
            userDB.updateChildren(map);

            this.postListener = new ValueEventListener()  {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    if(dataSnapshot.exists()){

                        try{

                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                            // test if the recors Phone already exist  ...if not than
                            // than you are a new user   ...
                            if(map.get("id_asterisk")!=null){
                                // than this user is already registered ...
                                String GuD_Voice_Id = map.get("id_asterisk").toString();


                                try{

                                    postListener = null;
                                    AsteriskListener = null;
                                    userDB = null;
                                    AsteriskUsersIndex = null;

                                }catch(Exception ex){
                                    ex.printStackTrace();
                                }

                               //  Toast.makeText(MainActivity_with_Drawer.this, "you are already registeterd on  Gudana voice  Server... :  "+ GuD_Voice_Id ,Toast.LENGTH_LONG ).show();

                                // save User Info and continue  normaly  ... user is already registerd
                                // StaticConfig.UID = user_Global.getUid();

                            }else{
                                // we must start a registration on Asterisk Server  ...

                                Toast.makeText(MainActivity_with_Drawer.this , "voice Server Registration  ... " ,Toast.LENGTH_LONG ).show();
                                // get the actual index of Asterisk users stored on  firebase Databases ...
                                AsteriskUsersIndex = FirebaseDatabase.getInstance().getReference().child("asterisk");

                               AsteriskListener = new ValueEventListener()  {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            Map<String, Object> map_ast = (Map<String, Object>) dataSnapshot.getValue();

                                            try{
                                                if(map_ast.get("password")!=null){
                                                    // than this user is already registered ...
                                                    password_asterisk = map_ast.get("password").toString();
                                                    Toast.makeText(MainActivity_with_Drawer.this , "password "+ password_asterisk ,Toast.LENGTH_LONG ).show();

                                                    // save User Info and continue  normaly  ... user is already registerd
                                                    // StaticConfig.UID = user_Global.getUid();

                                                }

                                                if(map_ast.get("server")!=null){
                                                    // than this user is already registered ...
                                                    server_adresse  = map_ast.get("server").toString();

                                                    // save User Info and continue  normaly  ... user is already registerd
                                                    // StaticConfig.UID = user_Global.getUid();

                                                }

                                                if(map_ast.get("index")!=null){
                                                    // than this user is already registered ...
                                                    Users_Id_asterisk  = map_ast.get("index").toString();
                                                    Toast.makeText(MainActivity_with_Drawer.this, "id_voice = ... :  "+ Users_Id_asterisk ,Toast.LENGTH_LONG ).show();

                                                    // update index asterix
                                                    HashMap map_asterisk = new HashMap();
                                                    map_asterisk.put("index" , Integer.parseInt(Users_Id_asterisk)+2);
                                                    AsteriskUsersIndex.updateChildren(map_asterisk);

                                                    // update  child users ... with asterisk  id

                                                    HashMap map_user = new HashMap();
                                                    map_user.put("id_asterisk" , Users_Id_asterisk);
                                                    userDB.updateChildren(map_user);

                                                    /*
                                                    // start Registration asterisk Server
                                                    displayCustomToast("disabled at the moment ", Toast.LENGTH_LONG);
                                                    mPrefs = LinphonePreferences.instance();
                                                    for (int x = 0; x <= 5; x++){
                                                        try{
                                                            mPrefs.deleteAccount(x);
                                                            Thread.sleep(500);

                                                        }catch(Exception ex){
                                                            ex.printStackTrace();
                                                        }

                                                    }
                                                    */


                                                    displayCustomToast("registration voice server ", Toast.LENGTH_LONG);

                                                    try{

                                                        // transport type always udp

                                                        AssistantActivity assist = new AssistantActivity();
                                                        transport = LinphoneAddress.TransportType.LinphoneTransportUdp;
                                                        android.util.Log.e("","");
                                                        assist.genericLogIn(Users_Id_asterisk, password_asterisk, null, server_adresse, transport);

                                                    }catch(Exception ex){
                                                        ex.printStackTrace();
                                                    }


                                                    // remove all listeners   ...
                                                    userDB.removeEventListener(postListener);
                                                    AsteriskUsersIndex.removeEventListener(AsteriskListener);
                                                    postListener = null;
                                                    AsteriskListener = null;
                                                    userDB = null;
                                                    AsteriskUsersIndex = null;

                                                }

                                            }catch(Exception ex){
                                                ex.printStackTrace();
                                            }
                                            // ...
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Failed to read value
                                        Toast.makeText(MainActivity_with_Drawer.this, "error retrieve data asterisk index ", Toast.LENGTH_SHORT).show();
                                    }
                                };
                                AsteriskUsersIndex.addValueEventListener(AsteriskListener);

                            }


                        }catch(Exception ex){
                            ex.printStackTrace();
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Toast.makeText(MainActivity_with_Drawer.this, databaseError.toString() , Toast.LENGTH_LONG).show();
                }
            };
            userDB.addValueEventListener(postListener);

        }catch(Exception ex){
            ex.printStackTrace();
        }



    }

    public void setSelectedPage(int i){
        if(mViewPager != null)
            mViewPager.setCurrentItem(i, true);
    }



    public SharedPreferences getPreferences() {
        return preferences;
    }



    // add

    private boolean isTablet() {
        return getResources().getBoolean(R.bool.isTablet);
    }

    public void hideStatusBar() {
        if (isTablet()) {
            return;
        }

        findViewById(R.id.status).setVisibility(View.GONE);
    }

    public void showStatusBar() {
        if (isTablet()) {
            return;
        }

        if (statusFragment != null && !statusFragment.isVisible()) {
            statusFragment.getView().setVisibility(View.VISIBLE);
        }
        findViewById(R.id.status).setVisibility(View.VISIBLE);
    }

    public void isNewProxyConfig(){
        newProxyConfig = true;
    }

    private void changeCurrentFragment(FragmentsAvailable newFragmentType, Bundle extras) {
        changeCurrentFragment(newFragmentType, extras, false);
    }

    private void changeCurrentFragment(FragmentsAvailable newFragmentType, Bundle extras, boolean withoutAnimation) {
        if (newFragmentType == currentFragment && newFragmentType != FragmentsAvailable.CHAT) {
            return;
        }

        if (currentFragment == FragmentsAvailable.DIALER) {
            try {
                DialerFragment dialerFragment = DialerFragment.instance();
                dialerSavedState = getSupportFragmentManager().saveFragmentInstanceState(dialerFragment);
            } catch (Exception e) {
            }
        }

        fragment = null;

        switch (newFragmentType) {
            case HISTORY_LIST:
                fragment = new HistoryListFragment();
                break;
            case HISTORY_DETAIL:
                fragment = new HistoryDetailFragment();
                break;

            case DIALER:
                fragment = new DialerFragment();
                if (extras == null) {
                    fragment.setInitialSavedState(dialerSavedState);
                }
                break;
            case SETTINGS:
                fragment = new SettingsFragment();
                break;
            case ACCOUNT_SETTINGS:
                fragment = new AccountPreferencesFragment();
                break;
            case ABOUT:
                // fragment = new AboutFragment();
                break;
            case EMPTY:
                fragment = new EmptyFragment();
                break;
            case CHAT_LIST:
                // fragment = new ChatListFragment();
                fragment  = new DialerFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            fragment.setArguments(extras);
            if (isTablet()) {
                changeFragmentForTablets(fragment, newFragmentType, withoutAnimation);
                switch (newFragmentType) {
                    case HISTORY_LIST:
                        ((HistoryListFragment) fragment).displayFirstLog();
                        break;
                    case CONTACTS_LIST:
                        // ((ContactsListFragment) fragment).displayFirstContact();
                        break;
                    case CHAT_LIST:
                        // ((ChatListFragment) fragment).displayFirstChat();
                        break;
                }
            } else {
                changeFragment(fragment, newFragmentType, withoutAnimation);
            }
        }
    }

    private void changeFragment(Fragment newFragment, FragmentsAvailable newFragmentType, boolean withoutAnimation) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

		/*if (!withoutAnimation && !isAnimationDisabled && currentFragment.shouldAnimate()) {
			if (newFragmentType.isRightOf(currentFragment)) {
				transaction.setCustomAnimations(R.anim.slide_in_right_to_left,
						R.anim.slide_out_right_to_left,
						R.anim.slide_in_left_to_right,
						R.anim.slide_out_left_to_right);
			} else {
				transaction.setCustomAnimations(R.anim.slide_in_left_to_right,
						R.anim.slide_out_left_to_right,
						R.anim.slide_in_right_to_left,
						R.anim.slide_out_right_to_left);
			}
		}*/

        if (newFragmentType != FragmentsAvailable.DIALER
                && newFragmentType != FragmentsAvailable.CONTACTS_LIST
                && newFragmentType != FragmentsAvailable.CHAT_LIST
                && newFragmentType != FragmentsAvailable.HISTORY_LIST) {
            transaction.addToBackStack(newFragmentType.toString());
        } else {
            while (fm.getBackStackEntryCount() > 0) {
                fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }

        transaction.replace(R.id.fragmentContainer, newFragment, newFragmentType.toString());
        transaction.commitAllowingStateLoss();
        fm.executePendingTransactions();

        currentFragment = newFragmentType;
    }

    private void changeFragmentForTablets(Fragment newFragment, FragmentsAvailable newFragmentType, boolean withoutAnimation) {
        if (getResources().getBoolean(R.bool.show_statusbar_only_on_dialer)) {
            if (newFragmentType == FragmentsAvailable.DIALER) {
                showStatusBar();
            } else {
                hideStatusBar();
            }
        }
        emptyFragment = false;
        LinearLayout ll = findViewById(R.id.fragmentContainer2);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(newFragmentType == FragmentsAvailable.EMPTY){
            ll.setVisibility(View.VISIBLE);
            emptyFragment = true;
            transaction.replace(R.id.fragmentContainer2, newFragment);
            transaction.commitAllowingStateLoss();
            getFragmentManager().executePendingTransactions();
        } else {
            if (newFragmentType.shouldAddItselfToTheRightOf(currentFragment)) {
                ll.setVisibility(View.VISIBLE);

                if (newFragmentType == FragmentsAvailable.CONTACT_EDITOR) {
                    transaction.addToBackStack(newFragmentType.toString());
                }
                transaction.replace(R.id.fragmentContainer2, newFragment);
            } else {
                if (newFragmentType == FragmentsAvailable.EMPTY) {
                    ll.setVisibility(View.VISIBLE);
                    transaction.replace(R.id.fragmentContainer2, new EmptyFragment());
                    emptyFragment = true;
                }

                if (newFragmentType == FragmentsAvailable.DIALER
                        || newFragmentType == FragmentsAvailable.ABOUT
                        || newFragmentType == FragmentsAvailable.SETTINGS
                        || newFragmentType == FragmentsAvailable.ACCOUNT_SETTINGS) {
                    ll.setVisibility(View.GONE);
                } else {
                    ll.setVisibility(View.VISIBLE);
                    transaction.replace(R.id.fragmentContainer2, new EmptyFragment());
                }

				/*if (!withoutAnimation && !isAnimationDisabled && currentFragment.shouldAnimate()) {
					if (newFragmentType.isRightOf(currentFragment)) {
						transaction.setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.slide_out_right_to_left, R.anim.slide_in_left_to_right, R.anim.slide_out_left_to_right);
					} else {
						transaction.setCustomAnimations(R.anim.slide_in_left_to_right, R.anim.slide_out_left_to_right, R.anim.slide_in_right_to_left, R.anim.slide_out_right_to_left);
					}
				}*/
                transaction.replace(R.id.fragmentContainer, newFragment);
            }
            transaction.commitAllowingStateLoss();
            getFragmentManager().executePendingTransactions();

            currentFragment = newFragmentType;
            if (newFragmentType == FragmentsAvailable.DIALER
                    || newFragmentType == FragmentsAvailable.SETTINGS
                    || newFragmentType == FragmentsAvailable.CONTACTS_LIST
                    || newFragmentType == FragmentsAvailable.CHAT_LIST
                    || newFragmentType == FragmentsAvailable.HISTORY_LIST) {
                try {
                    getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } catch (IllegalStateException e) {

                }
            }
            fragmentsHistory.add(currentFragment);
        }
    }

    public void displayHistoryDetail(String sipUri, LinphoneCallLog log) {
        LinphoneAddress lAddress;
        try {
            lAddress = LinphoneCoreFactory.instance().createLinphoneAddress(sipUri);
        } catch (LinphoneCoreException e) {
            Log.e("Cannot display lin_history details",e);
            //TODO display error message
            return;
        }
        LinphoneContact c = ContactsManager.getInstance().findContactFromAddress(lAddress);

        String displayName = c != null ? c.getFullName() : LinphoneUtils.getAddressDisplayName(sipUri);
        String pictureUri = c != null && c.getPhotoUri() != null ? c.getPhotoUri().toString() : null;

        String status;
        if (log.getDirection() == CallDirection.Outgoing) {
            status = getString(R.string.outgoing);
        } else {
            if (log.getStatus() == LinphoneCallLog.CallStatus.Missed) {
                status = getString(R.string.missed);
            } else {
                status = getString(R.string.incoming);
            }
        }

        String callTime = secondsToDisplayableString(log.getCallDuration());
        String callDate = String.valueOf(log.getTimestamp());

        Fragment fragment2 = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer2);
        if (fragment2 != null && fragment2.isVisible() && currentFragment == FragmentsAvailable.HISTORY_DETAIL) {
            HistoryDetailFragment historyDetailFragment = (HistoryDetailFragment) fragment2;
            historyDetailFragment.changeDisplayedHistory(lAddress.asStringUriOnly(), displayName, pictureUri, status, callTime, callDate);
        } else {
            Bundle extras = new Bundle();
            extras.putString("SipUri", lAddress.asString());
            if (displayName != null) {
                extras.putString("DisplayName", displayName);
                extras.putString("PictureUri", pictureUri);
            }
            extras.putString("CallStatus", status);
            extras.putString("CallTime", callTime);
            extras.putString("CallDate", callDate);

            changeCurrentFragment(FragmentsAvailable.HISTORY_DETAIL, extras);
        }
    }

    public void displayEmptyFragment(){
        changeCurrentFragment(FragmentsAvailable.EMPTY, new Bundle());
    }

    @SuppressLint("SimpleDateFormat")
    private String secondsToDisplayableString(int secs) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.set(0, 0, 0, 0, 0, secs);
        return dateFormat.format(cal.getTime());
    }


    public void displayInapp() {
        startActivity(new Intent(MainActivity_with_Drawer.this, InAppPurchaseActivity.class));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.history) {
            changeCurrentFragment(FragmentsAvailable.HISTORY_LIST, null);
            history_selected.setVisibility(View.VISIBLE);
            LinphoneManager.getLc().resetMissedCallsCount();
            displayMissedCalls(0);
        } else if (id == R.id.contacts) {
            changeCurrentFragment(FragmentsAvailable.CONTACTS_LIST, null);
            contacts_selected.setVisibility(View.VISIBLE);
        } else if (id == R.id.dialer) {
            changeCurrentFragment(FragmentsAvailable.DIALER, null);
            dialer_selected.setVisibility(View.VISIBLE);
        } else if (id == R.id.chat) {
            changeCurrentFragment(FragmentsAvailable.CHAT_LIST, null);
            chat_selected.setVisibility(View.VISIBLE);
        } else if (id == R.id.cancel){
            displayDialer();
        }
    }

    public void updateDialerFragment(DialerFragment fragment) {
        // Hack to maintain soft input flags
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void goToDialerFragment() {
        changeCurrentFragment(FragmentsAvailable.DIALER, null);
        // dialer_selected.setVisibility(View.VISIBLE);
    }


    public void updateStatusFragment(StatusFragment fragment) {
        statusFragment = fragment;
    }

    public void displaySettings() {
        changeCurrentFragment(FragmentsAvailable.SETTINGS, null);
    }

    public void displayDialer() {
        changeCurrentFragment(FragmentsAvailable.DIALER, null);
    }

    public void displayAccountSettings(int accountNumber) {
        Bundle bundle = new Bundle();
        bundle.putInt("Account", accountNumber);
        changeCurrentFragment(FragmentsAvailable.ACCOUNT_SETTINGS, bundle);
        //lin_settings.setSelected(true);
    }

    public List<String> getChatList() {
        ArrayList<String> chatList = new ArrayList<String>();

        LinphoneChatRoom[] chats = LinphoneManager.getLc().getChatRooms();
        List<LinphoneChatRoom> rooms = new ArrayList<LinphoneChatRoom>();

        for (LinphoneChatRoom chatroom : chats) {
            if (chatroom.getHistorySize() > 0) {
                rooms.add(chatroom);
            }
        }

        if (rooms.size() > 1) {
            Collections.sort(rooms, new Comparator<LinphoneChatRoom>() {
                @Override
                public int compare(LinphoneChatRoom a, LinphoneChatRoom b) {
                    LinphoneChatMessage[] messagesA = a.getHistory(1);
                    LinphoneChatMessage[] messagesB = b.getHistory(1);
                    long atime = messagesA[0].getTime();
                    long btime = messagesB[0].getTime();

                    if (atime > btime)
                        return -1;
                    else if (btime > atime)
                        return 1;
                    else
                        return 0;
                }
            });
        }

        for (LinphoneChatRoom chatroom : rooms) {
            chatList.add(chatroom.getPeerAddress().asStringUriOnly());
        }

        return chatList;
    }


    public void displayMissedCalls(final int missedCallsCount) {
        /*
        if (missedCallsCount > 0) {
            missedCalls.setText(missedCallsCount + "");
            missedCalls.setVisibility(View.VISIBLE);
        } else {
            LinphoneManager.getLc().resetMissedCallsCount();
            missedCalls.clearAnimation();
            missedCalls.setVisibility(View.GONE);
        }
        */
    }



    public void displayCustomToast(final String message, final int duration) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.lin_toast, (ViewGroup) findViewById(R.id.toastRoot));

        TextView toastText = layout.findViewById(R.id.toastMessage);
        toastText.setText(message);

        final Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }

    public Dialog displayDialog(String text){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Drawable d = new ColorDrawable(ContextCompat.getColor(this, R.color.colorC));
        d.setAlpha(200);
        dialog.setContentView(R.layout.lin_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(d);

        TextView customText = dialog.findViewById(R.id.customText);
        customText.setText(text);
        return dialog;
    }

    public Dialog displayWrongPasswordDialog(final String username, final String realm, final String domain){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Drawable d = new ColorDrawable(ContextCompat.getColor(this, R.color.colorC));
        d.setAlpha(200);
        dialog.setContentView(R.layout.input_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(d);

        TextView customText = dialog.findViewById(R.id.customText);
        customText.setText(getString(R.string.error_bad_credentials));

        Button retry = dialog.findViewById(R.id.retry);
        Button cancel = dialog.findViewById(R.id.cancel);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPassword = ((EditText) dialog.findViewById(R.id.password)).getText().toString();
                LinphoneAuthInfo authInfo = LinphoneCoreFactory.instance().createAuthInfo(username, null, newPassword, null, realm, domain);
                LinphoneManager.getLc().addAuthInfo(authInfo);
                LinphoneManager.getLc().refreshRegisters();
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    @Override
    public void setAddresGoToDialerAndCall(String number, String name, Uri photo) {
//		Bundle extras = new Bundle();
//		extras.putString("SipUri", number);
//		extras.putString("DisplayName", name);
//		extras.putString("Photo", photo == null ? null : photo.toString());
//		changeCurrentFragment(FragmentsAvailable.DIALER, extras);

        LinphoneManager.AddressType address = new AddressText(this, null);
        address.setDisplayedName(name);
        address.setText(number);
        // LinphoneManager.getInstance().newOutgoingCall(address);
        LinphoneManager.getInstance().newOutgoingCall(number, name);

    }

    public void startIncallActivity(LinphoneCall currentCall) {
        Intent intent = new Intent(this, CallActivity.class);
        startOrientationSensor();
        startActivityForResult(intent, CALL_ACTIVITY);
    }

    /**
     * Register a sensor to track phoneOrientation changes
     */
    private synchronized void startOrientationSensor() {
        if (mOrientationHelper == null) {
            mOrientationHelper = new MainActivity_with_Drawer.LocalOrientationEventListener(this);
        }
        mOrientationHelper.enable();
    }

    private int mAlwaysChangingPhoneAngle = -1;

    private class LocalOrientationEventListener extends OrientationEventListener {
        public LocalOrientationEventListener(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(final int o) {
            if (o == OrientationEventListener.ORIENTATION_UNKNOWN) {
                return;
            }

            int degrees = 270;
            if (o < 45 || o > 315)
                degrees = 0;
            else if (o < 135)
                degrees = 90;
            else if (o < 225)
                degrees = 180;

            if (mAlwaysChangingPhoneAngle == degrees) {
                return;
            }
            mAlwaysChangingPhoneAngle = degrees;

            Log.d("Phone orientation changed to ", degrees);
            int rotation = (360 - degrees) % 360;
            LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
            if (lc != null) {
                lc.setDeviceRotation(rotation);
                LinphoneCall currentCall = lc.getCurrentCall();
                if (currentCall != null && currentCall.cameraEnabled() && currentCall.getCurrentParamsCopy().getVideoEnabled()) {
                    lc.updateCall(currentCall, null);
                }
            }
        }
    }

    public Boolean isCallTransfer(){
        return callTransfer;
    }

    private void initInCallMenuLayout(final boolean callTransfer) {
        // selectMenu(FragmentsAvailable.DIALER);
        DialerFragment dialerFragment = DialerFragment.instance();
        if (dialerFragment != null) {
            dialerFragment.resetLayout(callTransfer);
        }
    }

    public void resetClassicMenuLayoutAndGoBackToCallIfStillRunning() {
        DialerFragment dialerFragment = DialerFragment.instance();
        if (dialerFragment != null) {
            dialerFragment.resetLayout(true);
        }

        if (LinphoneManager.isInstanciated() && LinphoneManager.getLc().getCallsNb() > 0) {
            LinphoneCall call = LinphoneManager.getLc().getCalls()[0];
            if (call.getState() == LinphoneCall.State.IncomingReceived) {
                startActivity(new Intent(MainActivity_with_Drawer.this, CallIncomingActivity.class));
            } else {
                startIncallActivity(call);
            }
        }
    }

    public FragmentsAvailable getCurrentFragment() {
        return currentFragment;
    }

    public void addContact(String displayName, String sipUri)
    {
        Bundle extras = new Bundle();
        extras.putSerializable("NewSipAdress", sipUri);
        changeCurrentFragment(FragmentsAvailable.CONTACT_EDITOR, extras);
    }

    public void editContact(LinphoneContact contact)
    {
        Bundle extras = new Bundle();
        extras.putSerializable("Contact", contact);
        changeCurrentFragment(FragmentsAvailable.CONTACT_EDITOR, extras);
    }

    public void editContact(LinphoneContact contact, String sipAddress)
    {
        Bundle extras = new Bundle();
        extras.putSerializable("Contact", contact);
        extras.putSerializable("NewSipAdress", sipAddress);
        changeCurrentFragment(FragmentsAvailable.CONTACT_EDITOR, extras);
    }

    public void quit() {
        finish();
        stopService(new Intent(Intent.ACTION_MAIN).setClass(this, LinphoneService.class));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_FIRST_USER && requestCode == SETTINGS_ACTIVITY) {
            if (data.getExtras().getBoolean("Exit", false)) {
                quit();
            } else {
                pendingFragmentTransaction = (FragmentsAvailable) data.getExtras().getSerializable("FragmentToDisplay");
            }
        } else if (resultCode == Activity.RESULT_FIRST_USER && requestCode == CALL_ACTIVITY) {
            getIntent().putExtra("PreviousActivity", CALL_ACTIVITY);
            callTransfer = data != null && data.getBooleanExtra("Transfer", false);
            boolean chat = data != null && data.getBooleanExtra("chat", false);
            if(chat){
                pendingFragmentTransaction = FragmentsAvailable.CHAT_LIST;
            }
            if (LinphoneManager.getLc().getCallsNb() > 0) {
                initInCallMenuLayout(callTransfer);
            } else {
                resetClassicMenuLayoutAndGoBackToCallIfStillRunning();
            }
        } else if (requestCode == PERMISSIONS_REQUEST_OVERLAY) {
            if (Compatibility.canDrawOverlays(this)) {
                LinphonePreferences.instance().enableOverlay(true);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onPause() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null)
        {
            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
        }


        getIntent().putExtra("PreviousActivity", 0);

        LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            lc.removeListener(mListener);
        }
        callTransfer = false;

        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.ca_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        switch(item.getItemId())
        {
            case R.id.menuLogout:
                AlertDialog.Builder logoutBuilder = new AlertDialog.Builder(MainActivity_with_Drawer.this);
                logoutBuilder.setTitle("Logout");
                logoutBuilder.setMessage("Are you sure you want to logout?");
                logoutBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);

                        FirebaseAuth.getInstance().signOut();

                        Intent welcomeIntent = new Intent(MainActivity_with_Drawer.this, WelcomeActivity.class);
                        startActivity(welcomeIntent);
                        finish();
                    }
                });
                logoutBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                    }
                });
                AlertDialog logoutDialog = logoutBuilder.create();
                logoutDialog.show();
                return true;
            case R.id.menuChangelog:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://gudana.com/master/CHANGELOG.md")));
                return true;
            case R.id.menuAbout:
                AlertDialog.Builder aboutBuilder = new AlertDialog.Builder(MainActivity_with_Drawer.this);
                aboutBuilder.setTitle("GuDana Beta v12.1");
                aboutBuilder.setMessage("Project is closed  source and licensed under .... .\n\nMake sure you read all Legal content.\n\nRaf, 2018. All Rights Reserved.");
                aboutBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                    }
                });
                AlertDialog aboutDialog = aboutBuilder.create();
                aboutDialog.show();
                return true;
            case R.id.menuLegal:
                AlertDialog.Builder legalBuilder = new AlertDialog.Builder(this);
                legalBuilder.setTitle("Legal");
                legalBuilder.setItems(new CharSequence[]{"License", "Privacy Policy", "Terms and Conditions", "Third Party Notices"}, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position)
                    {
                        switch(position)
                        {
                            case 0:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://gudana.com/blob/master/LICENSE")));
                                break;
                            case 1:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://gudana.com/master/PRIVACY_POLICY.md")));
                                break;
                            case 2:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://gudana.com/master/TERMS_AND_CONDITIONS.md")));
                                break;
                            case 3:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://gudana.com/master/THIRD_PARTY_NOTICES.md")));
                                break;
                        }
                    }
                });

                AlertDialog legalDialog = legalBuilder.create();
                legalDialog.show();
                return true;
            case R.id.menuProfile:
                Intent profileIntent = new Intent(MainActivity_with_Drawer.this, ProfileActivity.class);
                profileIntent.putExtra("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(profileIntent);
                return true;
            case R.id.menuSearch:
                Intent usersIntent = new Intent(MainActivity_with_Drawer.this, UsersActivity.class);
                startActivity(usersIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity_with_Drawer.this);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure you want to close the application?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean checkAndRequestOverlayPermission() {
        Log.i("[Permission] Draw overlays permission is " + (Compatibility.canDrawOverlays(this) ? "granted" : "denied"));
        if (!Compatibility.canDrawOverlays(this)) {
            Log.i("[Permission] Asking for overlay");
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, PERMISSIONS_REQUEST_OVERLAY);
            return false;
        }
        return true;
    }

    public void checkAndRequestReadPhoneStatePermission() {
        checkAndRequestPermission(android.Manifest.permission.READ_PHONE_STATE, 0);
    }

    public void checkAndRequestReadExternalStoragePermission() {
        checkAndRequestPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, 0);
    }

    public void checkAndRequestExternalStoragePermission() {
        checkAndRequestPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, 0);
    }

    public void checkAndRequestCameraPermission() {
        checkAndRequestPermission(android.Manifest.permission.CAMERA, 0);
    }

    public void checkAndRequestReadContactsPermission() {
        checkAndRequestPermission(android.Manifest.permission.READ_CONTACTS, PERMISSIONS_REQUEST_CONTACTS);
    }

    public void checkAndRequestInappPermission() {
        checkAndRequestPermission(android.Manifest.permission.GET_ACCOUNTS, PERMISSIONS_REQUEST_CONTACTS);
    }

    public void checkAndRequestWriteContactsPermission() {
        checkAndRequestPermission(android.Manifest.permission.WRITE_CONTACTS, 0);
    }

    public void checkAndRequestRecordAudioPermissionForEchoCanceller() {
        checkAndRequestPermission(android.Manifest.permission.RECORD_AUDIO, PERMISSIONS_RECORD_AUDIO_ECHO_CANCELLER);
    }

    public void checkAndRequestRecordAudioPermissionsForEchoTester() {
        checkAndRequestPermission(android.Manifest.permission.RECORD_AUDIO, PERMISSIONS_RECORD_AUDIO_ECHO_TESTER);
    }

    public void checkAndRequestReadExternalStoragePermissionForDeviceRingtone() {
        checkAndRequestPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, PERMISSIONS_READ_EXTERNAL_STORAGE_DEVICE_RINGTONE);
    }

    public void checkAndRequestPermissionsToSendImage() {
        ArrayList<String> permissionsList = new ArrayList<String>();

        int readExternalStorage = getPackageManager().checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());
        Log.i("[Permission] Read external storage permission is " + (readExternalStorage == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));
        int camera = getPackageManager().checkPermission(android.Manifest.permission.CAMERA, getPackageName());
        Log.i("[Permission] Camera permission is " + (camera == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));

        if (readExternalStorage != PackageManager.PERMISSION_GRANTED) {
            if (LinphonePreferences.instance().firstTimeAskingForPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Log.i("[Permission] Asking for read external storage");
                permissionsList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            if (LinphonePreferences.instance().firstTimeAskingForPermission(android.Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
                Log.i("[Permission] Asking for camera");
                permissionsList.add(android.Manifest.permission.CAMERA);
            }
        }
        if (permissionsList.size() > 0) {
            String[] permissions = new String[permissionsList.size()];
            permissions = permissionsList.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, 0);
        }
    }

    private void checkSyncPermission() {
        checkAndRequestPermission(android.Manifest.permission.WRITE_SYNC_SETTINGS, PERMISSIONS_REQUEST_SYNC);
    }

    public void checkAndRequestPermission(String permission, int result) {
        int permissionGranted = getPackageManager().checkPermission(permission, getPackageName());
        Log.i("[Permission] " + permission + " is " + (permissionGranted == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));

        if (permissionGranted != PackageManager.PERMISSION_GRANTED) {
            if (LinphonePreferences.instance().firstTimeAskingForPermission(permission) || ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Log.i("[Permission] Asking for " + permission);
                ActivityCompat.requestPermissions(this, new String[] { permission }, result);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions.length <= 0)
            return;

        int readContactsI = -1;
        for (int i = 0; i < permissions.length; i++) {
            Log.i("[Permission] " + permissions[i] + " is " + (grantResults[i] == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));
            if (permissions[i] == android.Manifest.permission.READ_CONTACTS)
                readContactsI = i;
        }

        switch (requestCode) {
            case PERMISSIONS_REQUEST_SYNC:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ContactsManager.getInstance().initializeSyncAccount(getApplicationContext(), getContentResolver());
                } else {
                    ContactsManager.getInstance().initializeContactManager(getApplicationContext(), getContentResolver());
                }
                break;
            case PERMISSIONS_RECORD_AUDIO_ECHO_CANCELLER:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ((SettingsFragment) fragment).startEchoCancellerCalibration();
                } else {
                    ((SettingsFragment) fragment).echoCalibrationFail();
                }
                break;
            case PERMISSIONS_READ_EXTERNAL_STORAGE_DEVICE_RINGTONE:
                if (readContactsI >= 0 && grantResults[readContactsI] == PackageManager.PERMISSION_GRANTED) {
                    ContactsManager.getInstance().enableContactsAccess();
                }
                if (!fetchedContactsOnce) {
                    ContactsManager.getInstance().enableContactsAccess();
                    ContactsManager.getInstance().fetchContactsAsync();
                    fetchedContactsOnce = true;
                }
                if (permissions[0].compareTo(android.Manifest.permission.READ_EXTERNAL_STORAGE) != 0)
                    break;
                boolean enableRingtone = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
                LinphonePreferences.instance().enableDeviceRingtone(enableRingtone);
                LinphoneManager.getInstance().enableDeviceRingtone(enableRingtone);
                break;
            case PERMISSIONS_RECORD_AUDIO_ECHO_TESTER:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    ((SettingsFragment) fragment).startEchoTester();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // firebase chat
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            // If no logged in user send them to login/register

            Intent welcomeIntent = new Intent(MainActivity_with_Drawer.this, WelcomeActivity.class);
            startActivity(welcomeIntent);
            finish();
        }

        // end chat  ...
        ArrayList<String> permissionsList = new ArrayList<String>();

        int contacts = getPackageManager().checkPermission(android.Manifest.permission.READ_CONTACTS, getPackageName());
        Log.i("[Permission] Contacts permission is " + (contacts == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));

        int readPhone = getPackageManager().checkPermission(android.Manifest.permission.READ_PHONE_STATE, getPackageName());
        Log.i("[Permission] Read phone state permission is " + (readPhone == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));

        int ringtone = getPackageManager().checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());
        Log.i("[Permission] Read external storage for ring tone permission is " + (ringtone == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));

        if (ringtone != PackageManager.PERMISSION_GRANTED) {
            if (LinphonePreferences.instance().firstTimeAskingForPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Log.i("[Permission] Asking for read external storage for ring tone");
                permissionsList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        if (readPhone != PackageManager.PERMISSION_GRANTED) {
            if (LinphonePreferences.instance().firstTimeAskingForPermission(android.Manifest.permission.READ_PHONE_STATE) || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE)) {
                Log.i("[Permission] Asking for read phone state");
                permissionsList.add(android.Manifest.permission.READ_PHONE_STATE);
            }
        }
        if (contacts != PackageManager.PERMISSION_GRANTED) {
            if (LinphonePreferences.instance().firstTimeAskingForPermission(android.Manifest.permission.READ_CONTACTS) || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
                Log.i("[Permission] Asking for lin_contacts");
                permissionsList.add(Manifest.permission.READ_CONTACTS);
            }
        } else {
            if (!fetchedContactsOnce) {
                ContactsManager.getInstance().enableContactsAccess();
                ContactsManager.getInstance().fetchContactsAsync();
                fetchedContactsOnce = true;
            }
        }

        if (permissionsList.size() > 0) {
            String[] permissions = new String[permissionsList.size()];
            permissions = permissionsList.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_READ_EXTERNAL_STORAGE_DEVICE_RINGTONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("currentFragment", currentFragment);
        outState.putBoolean("fetchedContactsOnce", fetchedContactsOnce);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fetchedContactsOnce = savedInstanceState.getBoolean("fetchedContactsOnce");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // fireabse chat  ...
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null)
        {
            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("online").setValue("true");
        }



        if (!LinphoneService.isReady()) {
            startService(new Intent(Intent.ACTION_MAIN).setClass(this, LinphoneService.class));
        }

        LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            lc.addListener(mListener);
        }

        if (isTablet()) {
            // Prevent fragmentContainer2 to be visible when rotating the device
            LinearLayout ll = findViewById(R.id.fragmentContainer2);
            if (currentFragment == FragmentsAvailable.DIALER
                    || currentFragment == FragmentsAvailable.ABOUT
                    || currentFragment == FragmentsAvailable.SETTINGS
                    || currentFragment == FragmentsAvailable.ACCOUNT_SETTINGS) {
                ll.setVisibility(View.GONE);
            }
        }

       //  refreshAccounts();

        if(getResources().getBoolean(R.bool.enable_in_app_purchase)){
            isTrialAccount();
        }

        if(LinphonePreferences.instance().isFriendlistsubscriptionEnabled() && LinphoneManager.getLc().getDefaultProxyConfig() != null){
            LinphoneManager.getInstance().subscribeFriendList(true);
        } else {
            LinphoneManager.getInstance().subscribeFriendList(false);
        }

        displayMissedCalls(LinphoneManager.getLc().getMissedCallsCount());

        LinphoneManager.getInstance().changeStatusToOnline();

        if (getIntent().getIntExtra("PreviousActivity", 0) != CALL_ACTIVITY && !doNotGoToCallActivity) {
            if (LinphoneManager.getLc().getCalls().length > 0) {
                LinphoneCall call = LinphoneManager.getLc().getCalls()[0];
                LinphoneCall.State callState = call.getState();

                if (callState == LinphoneCall.State.IncomingReceived) {
                    startActivity(new Intent(this, CallIncomingActivity.class));
                } else if (callState == LinphoneCall.State.OutgoingInit || callState == LinphoneCall.State.OutgoingProgress || callState == LinphoneCall.State.OutgoingRinging) {
                    startActivity(new Intent(this, CallOutgoingActivity.class));
                } else {
                    startIncallActivity(call);
                }
            }
        }
        doNotGoToCallActivity = false;
    }

    @Override
    protected void onDestroy() {
        if (mOrientationHelper != null) {
            mOrientationHelper.disable();
            mOrientationHelper = null;
        }

        instance = null;
        super.onDestroy();

        unbindDrawables(findViewById(R.id.topLayout));
        System.gc();
    }

    private void unbindDrawables(View view) {
        if (view != null && view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras();
        if (extras != null && extras.getBoolean("GoToChat", false)) {
            LinphoneService.instance().removeMessageNotification();
            String sipUri = extras.getString("ChatContactSipUri");
            doNotGoToCallActivity = true;
        } else if (extras != null && extras.getBoolean("GoToHistory", false)) {
            doNotGoToCallActivity = true;
            changeCurrentFragment(FragmentsAvailable.HISTORY_LIST, null);
        } else if (extras != null && extras.getBoolean("GoToInapp", false)) {
            LinphoneService.instance().removeMessageNotification();
            doNotGoToCallActivity = true;
            displayInapp();
        } else if (extras != null && extras.getBoolean("Notification", false)) {
            if (LinphoneManager.getLc().getCallsNb() > 0) {
                LinphoneCall call = LinphoneManager.getLc().getCalls()[0];
                startIncallActivity(call);
            }
        } else {
            DialerFragment dialerFragment = DialerFragment.instance();
            if (dialerFragment != null) {
                if (extras != null && extras.containsKey("SipUriOrNumber")) {
                    if (getResources().getBoolean(R.bool.automatically_start_intercepted_outgoing_gsm_call)) {
                        dialerFragment.newOutgoingCall(extras.getString("SipUriOrNumber"));
                    } else {
                        dialerFragment.displayTextInAddressBar(extras.getString("SipUriOrNumber"));
                    }
                } else {
                    dialerFragment.newOutgoingCall(intent);
                }
            }
            if (LinphoneManager.getLc().getCalls().length > 0) {
                // If a call is ringing, start incomingcallactivity
                Collection<LinphoneCall.State> incoming = new ArrayList<LinphoneCall.State>();
                incoming.add(LinphoneCall.State.IncomingReceived);
                if (LinphoneUtils.getCallsInState(LinphoneManager.getLc(), incoming).size() > 0) {
                    if (CallActivity.isInstanciated()) {
                        CallActivity.instance().startIncomingCallActivity();
                    } else {
                        startActivity(new Intent(this, CallIncomingActivity.class));
                    }
                }
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentFragment == FragmentsAvailable.DIALER
                    || currentFragment == FragmentsAvailable.CONTACTS_LIST
                    || currentFragment == FragmentsAvailable.HISTORY_LIST
                    || currentFragment == FragmentsAvailable.CHAT_LIST) {
                boolean isBackgroundModeActive = LinphonePreferences.instance().isBackgroundModeEnabled();
                if (!isBackgroundModeActive) {
                    stopService(new Intent(Intent.ACTION_MAIN).setClass(this, LinphoneService.class));
                    finish();
                } else if (LinphoneUtils.onKeyBackGoHome(this, keyCode, event)) {
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }



    private int getStatusIconResource(LinphoneCore.RegistrationState state) {
        try {
            if (state == LinphoneCore.RegistrationState.RegistrationOk) {
                return R.drawable.led_connected;
            } else if (state == LinphoneCore.RegistrationState.RegistrationProgress) {
                return R.drawable.led_inprogress;
            } else if (state == LinphoneCore.RegistrationState.RegistrationFailed) {
                return R.drawable.led_error;
            } else {
                return R.drawable.led_disconnected;
            }
        } catch (Exception e) {
            Log.e(e);
        }

        return R.drawable.led_disconnected;
    }

    private void displayMainAccount(){
        defaultAccount.setVisibility(View.VISIBLE);
        ImageView status = defaultAccount.findViewById(R.id.main_account_status);
        TextView address = defaultAccount.findViewById(R.id.main_account_address);
        TextView displayName = defaultAccount.findViewById(R.id.main_account_display_name);


        LinphoneProxyConfig proxy = LinphoneManager.getLc().getDefaultProxyConfig();
        if(proxy == null) {
            displayName.setText(getString(R.string.no_account));
            status.setVisibility(View.GONE);
            address.setText("");
            statusFragment.resetAccountStatus();
            LinphoneManager.getInstance().subscribeFriendList(false);

            defaultAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity_with_Drawer.instance().displayAccountSettings(0);
                }
            });
        } else {
            address.setText(proxy.getAddress().asStringUriOnly());
            displayName.setText(LinphoneUtils.getAddressDisplayName(proxy.getAddress()));
            status.setImageResource(getStatusIconResource(proxy.getState()));
            status.setVisibility(View.VISIBLE);

            defaultAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity_with_Drawer.instance().displayAccountSettings(LinphonePreferences.instance().getDefaultAccountIndex());
                }
            });
        }
    }

    //Inapp Purchase
    private void isTrialAccount() {
        if(LinphoneManager.getLc().getDefaultProxyConfig() != null && LinphonePreferences.instance().getInappPopupTime() != null) {
            XmlRpcHelper helper = new XmlRpcHelper();
            helper.isTrialAccountAsync(new XmlRpcListenerBase() {
                @Override
                public void onTrialAccountFetched(boolean isTrial) {
                    isTrialAccount = isTrial;
                    getExpirationAccount();
                }

                @Override
                public void onError(String error) {
                }
            }, LinphonePreferences.instance().getAccountUsername(LinphonePreferences.instance().getDefaultAccountIndex()), LinphonePreferences.instance().getAccountHa1(LinphonePreferences.instance().getDefaultAccountIndex()));
        }
    }

    private void getExpirationAccount() {
        if(LinphoneManager.getLc().getDefaultProxyConfig() != null && LinphonePreferences.instance().getInappPopupTime() != null) {
            XmlRpcHelper helper = new XmlRpcHelper();
            helper.getAccountExpireAsync(new XmlRpcListenerBase() {
                @Override
                public void onAccountExpireFetched(String result) {
                    if (result != null) {
                        long timestamp = Long.parseLong(result);

                        Calendar calresult = Calendar.getInstance();
                        calresult.setTimeInMillis(timestamp);

                        int diff = getDiffDays(calresult, Calendar.getInstance());
                        if (diff != -1 && diff <= getResources().getInteger(R.integer.days_notification_shown)) {
                            displayInappNotification(timestampToHumanDate(calresult));
                        }
                    }
                }

                @Override
                public void onError(String error) {
                }
            }, LinphonePreferences.instance().getAccountUsername(LinphonePreferences.instance().getDefaultAccountIndex()), LinphonePreferences.instance().getAccountHa1(LinphonePreferences.instance().getDefaultAccountIndex()));
        }
    }

    public void displayInappNotification(String date) {
        Timestamp now = new Timestamp(new Date().getTime());
        if (LinphonePreferences.instance().getInappPopupTime() != null && Long.parseLong(LinphonePreferences.instance().getInappPopupTime()) > now.getTime()) {
            return;
        } else {
            long newDate = now.getTime() + getResources().getInteger(R.integer.time_between_inapp_notification);
            LinphonePreferences.instance().setInappPopupTime(String.valueOf(newDate));
        }
        if(isTrialAccount){
            LinphoneService.instance().displayInappNotification(String.format(getString(R.string.inapp_notification_trial_expire), date));
        } else {
            LinphoneService.instance().displayInappNotification(String.format(getString(R.string.inapp_notification_account_expire), date));
        }

    }

    private String timestampToHumanDate(Calendar cal) {
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat(getResources().getString(R.string.inapp_popup_date_format));
        return dateFormat.format(cal.getTime());
    }

    private int getDiffDays(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            return -1;
        }
        if(cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)){
            return cal1.get(Calendar.DAY_OF_YEAR) - cal2.get(Calendar.DAY_OF_YEAR);
        }
        return -1;
    }


    // end add ...


    public void snackError(String s, int length) {
        if (coord == null) return;
        Snackbar mySnackbar = Snackbar.make(coord, s, length);
        mySnackbar.show();
    }

    public void snackError(String s) {
        snackError(s, Snackbar.LENGTH_SHORT);
    }

    // socket tools
    protected String AsteriskAuthentification( String addr, int port, String message) {

        // Socket socket = null;
        String dstAddress;
        int dstPort;
        String response = "";
        String message_to_send;
        Socket socket = null;
        InputStream inputStream;
        BufferedReader reader;
        OutputStream DataOutput;
        PrintWriter writer;
        Context context;

        try {
            socket = new Socket(addr, port);
            inputStream = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            DataOutput = socket.getOutputStream();
            writer = new PrintWriter(DataOutput, true);

            if(socket != null){

                String ResponseServer = "";
                writer.println("ID_AST#"+message.toString()+"#end");
                ResponseServer = reader.readLine();
                response = ResponseServer;
                Toast.makeText(MainActivity_with_Drawer.this, ResponseServer.toString(), Toast.LENGTH_SHORT).show();

                try {
                    Thread.sleep(500);
                    //Log.d(ResponseServer.toString(), ResponseServer.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }else{
                Toast.makeText(MainActivity_with_Drawer.this, "  error connection  with remote Server ", Toast.LENGTH_SHORT).show();

            }


        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return response;
    }



    private void selectItem(int position) {
        switch (position) {
            case 1: {


                break;
            }
            case 2: {
                //Intent phoneActivity = new Intent(this, LinphoneLauncherActivity.class);
                //startActivity(phoneActivity);

                startActivity(new Intent(MainActivity_with_Drawer.this, AssistantActivity.class));
                break;
            }
            case 3: {
                displayCustomToast("registration voice server ", Toast.LENGTH_LONG);

                try{

                    // transport type always udp

                    AssistantActivity assist = new AssistantActivity();
                    transport = LinphoneAddress.TransportType.LinphoneTransportUdp;
                    android.util.Log.e("","");
                    assist.genericLogIn("7002", "1234", null, "206.189.16.110", transport);

                }catch(Exception ex){
                    ex.printStackTrace();
                }

                break;
            }
            case 4: {
                displayCustomToast("disabled at the moment ", Toast.LENGTH_LONG);
                mPrefs = LinphonePreferences.instance();
                for (int x = 0; x <= 5; x++){
                    try{
                        mPrefs.deleteAccount(x);
                        Thread.sleep(500);

                    }catch(Exception ex){
                        ex.printStackTrace();
                    }

                }
                break;
            }
            case 5: {

                break;
            }
            default: {
                return;
            }
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }

    public AppBarLayout getAppBar() {
        return appbar;
    }
}


interface ContactPicked_Drawer {
    void setAddresGoToDialerAndCall(String number, String name, Uri photo);
}


