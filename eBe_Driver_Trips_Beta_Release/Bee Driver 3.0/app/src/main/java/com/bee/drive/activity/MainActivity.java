package com.bee.drive.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bee.drive.DriverSettingsActivity;
import com.bee.drive.MainChatActivity;
import com.bee.drive.Utility.UiUtils;
import com.bee.drive.promotions_swipe.PromotionsActivity;
import com.bee.drive.share_invitations.ShareActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bee.drive.Utility.ImageUtils;
import com.bee.drive.data.FriendDB;
import com.bee.drive.data.GroupDB;
import com.bee.drive.fragment.ShareFragment;
import com.bee.drive.fragment.UserProfileFragment;
import com.bee.drive.model.Configuration;
import com.bee.drive.model.User;
import com.bee.drive.other.CircleTransform;
import com.bee.drive.service.ServiceUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.bee.drive.R;

import com.bee.drive.data.SharedPreferenceHelper;
import com.bee.drive.data.StaticConfig;
import com.bee.drive.fragment.HomeFragment;
import com.bee.drive.ui.FriendsFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg;
    private TextView txtName, infos_id;
    private Toolbar toolbar;
    private ImageView mProfileImage;
    private String mProfileImageUrl;
    private Context mContext;


    // urls to load navigation header background image
    // and profile image
    private static final String urlProfileImg = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PROFIL = "profil";
    private static final String TAG_SHARE = "share";
    private static final String TAG_CHAT = "chat";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    private DatabaseReference userDB;
    private FirebaseAuth mAuth;
    private FirebaseUser user;


    private User myAccount;
    private Context context;
    private List<Configuration> listConfig = new ArrayList<>();
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Declare  differents Fragements
    // private HomeFragment homeFragment = new HomeFragment();
    private HomeFragment homeFragment = new HomeFragment();
    private UserProfileFragment profilFragment = new UserProfileFragment();
    private FriendsFragment FriendChatFragment = new FriendsFragment();


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        mHandler = new Handler();

        mContext = this;
        // Init Firebase
        initFirebase();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        infos_id = (TextView) navHeader.findViewById(R.id.id_or_phone);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
         mProfileImage = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);


        // load nav menu header data
        // loadNavHeader();
        // setImageAvatar(context, myAccount.avata);
        txtName.setText(myAccount.name);
        infos_id.setText(myAccount.email);

        Toast.makeText(this, myAccount.name +"  Email : " + myAccount.email, 8000).show();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

        getUserInfo();
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */




    private void initFirebase(){


        // get Data of Aktual USer
        // FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child("Driver/"+ user.getUid());

        userDB = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(StaticConfig.UID);
        userDB.addListenerForSingleValueEvent(userListener);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    StaticConfig.UID = user.getUid();
                } else {
                    // User is signed inT

                    Toast.makeText(getApplicationContext(), "not a valid User !!!" , 5000).show();

                }
                // ...
            }
        };

        SharedPreferenceHelper prefHelper = SharedPreferenceHelper.getInstance(this);
        myAccount = prefHelper.getUserInfo();


    }

    private ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //get Config actuel User ...
            listConfig.clear();
            myAccount = dataSnapshot.getValue(User.class);

            try{


                if(txtName != null){
                    txtName.setText(myAccount.name);
                }

                SharedPreferenceHelper preferenceHelper = SharedPreferenceHelper.getInstance(context);
                preferenceHelper.saveUserInfo(myAccount);

            }catch (Exception ex ){
                ex.printStackTrace();

            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //Remote Database Error
            Log.e(UserProfileFragment.class.getName(), "loadPost:onCancelled", databaseError.toException());
        }
    };



    private void getUserInfo(){
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){

                    try{

                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if(map.get("name")!=null){
                            txtName.setText(map.get("name").toString());
                        }
                        if(map.get("phone")!=null){
                            infos_id.setText(map.get("phone").toString());
                        }

                        if(map.get("profileImageUrl")!=null){

                            try{

                                mProfileImageUrl = map.get("profileImageUrl").toString();

                                // Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);

                                // Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);
                                // Loading profile image

                                Glide.with(getApplicationContext()).load(mProfileImageUrl)
                                        .crossFade()
                                        .thumbnail(0.5f)
                                        .bitmapTransform(new CircleTransform(getApplicationContext()))
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(mProfileImage);



                                // showing dot next to notifications label
                                navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);


                            }catch (Exception ex ){
                                ex.printStackTrace();
                            }
                        }

                    }catch(Exception ex){
                        Toast.makeText(MainActivity.this, ex.toString() , Toast.LENGTH_LONG).show();
                        ex.printStackTrace();

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(MainActivity.this, databaseError.toString() , Toast.LENGTH_LONG).show();
            }
        });
    }



    private void loadNavHeader() {
        // name, website
        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mProfileImage);

        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer


        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }


        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                // HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // home
                // UserProfileFragment profilFragment = new UserProfileFragment();
                return profilFragment;


            case 2:
                // Friend Fragments ...
                return FriendChatFragment;

            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_profil:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PROFIL;
                        break;

                    case R.id.nav_chat:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_CHAT;
                        break;

                    case R.id.nav_notifications:  // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, PromotionsActivity.class));
                        drawer.closeDrawers();
                        return true;


                    case R.id.nav_settings:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, DriverSettingsActivity.class));
                        drawer.closeDrawers();
                        return true;

                    case R.id.nav_share:
                        startActivity(new Intent(MainActivity.this, ShareActivity.class));
                        drawer.closeDrawers();
                        return true;

                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        // startActivity(new Intent(MainActivity.this, Template_Return.class));
                        // drawer.closeDrawers();


                        if (mContext != null) {
                            try{

                                UiUtils.showMaterialAboutDialog(mContext, getResources().getString(R.string.action_about));

                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                        return true;
                    case R.id.nav_rate_app:
                        // launch new intent instead of loading fragment
                        // UiUtils.rateApp(mContext, true);

                        startActivity(new Intent(MainActivity.this, MainChatActivity.class));

                        return true;

                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            // Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Sign Out Dialog")
                    .setMessage("Are you sure you want to sign out and close the app ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            FriendDB.getInstance(getApplicationContext()).dropDB();
                            GroupDB.getInstance(getApplicationContext()).dropDB();
                            ServiceUtils.stopServiceFriendChat(getApplicationContext(), true);
                            finish();
                            quit();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();

            return true;
        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }


    public void quit() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startMain);
    }



}
