package com.android.rivchat;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.rivchat.BootNavigation.MainActivityFragment;
import com.android.rivchat.cardview.Card_Home_fragment;
import com.android.rivchat.linphone.LinphoneActivity;
import com.android.rivchat.linphone.LinphoneLauncherActivity;
import com.android.rivchat.project_3.ChatFragment_init;
import com.android.rivchat.project_3.ProfileFragment;
import com.android.rivchat.ui.UserProfileFragment;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Security;

import okhttp3.Response;

// import rehanced.com.simpleetherwallet.activities.AnalyticsApplication;

public class MainActivity_with_Drawer extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public Fragment[] fragments;
    private TabLayout tabLayout;
    private CoordinatorLayout coord;
    private SharedPreferences preferences;
    private AppBarLayout appbar;
    private int generateRefreshCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // ------------------------- Material Drawer ---------------------------------

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.colorPrimaryDark)
                .addProfiles(
                        new ProfileDrawerItem().
                                withName("Gudana User").
                                withEmail("gudana_user@gud.com").
                                withIcon(getResources().getDrawable(R.mipmap.ic_person_gud_round))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();



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

        coord = (CoordinatorLayout) findViewById(R.id.main_content);
        appbar = (AppBarLayout) findViewById(R.id.appbar);

        fragments = new Fragment[3];
        fragments[0] = new Card_Home_fragment();
        fragments[1] = new UserProfileFragment();
        fragments[2] = new ProfileFragment();


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_signup);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_tab_group);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_play_icon);
        mViewPager.setOffscreenPageLimit(3);

    }

    public void setSelectedPage(int i){
        if(mViewPager != null)
            mViewPager.setCurrentItem(i, true);
    }



    public SharedPreferences getPreferences() {
        return preferences;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
       /* if(preferences == null)
            preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("APP_PAUSED", true);
        editor.apply();*/
    }

    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    public void snackError(String s, int length) {
        if (coord == null) return;
        Snackbar mySnackbar = Snackbar.make(coord, s, length);
        mySnackbar.show();
    }

    public void snackError(String s) {
        snackError(s, Snackbar.LENGTH_SHORT);
    }

    private void selectItem(int position) {
        switch (position) {
            case 1: {

                break;
            }
            case 2: {
                Intent phoneActivity = new Intent(this, LinphoneLauncherActivity.class);
                startActivity(phoneActivity);
                break;
            }
            case 3: {
                new AlertDialog.Builder(this)
                        .setTitle("About GuDana")
                        .setMessage("GuDana is published under GPL3\n" +
                                "Developed by Manuel S. C. for Rehanced, 2017\n"
                                + "www.rehanced.com\n" +
                                getString(R.string.app_name) + "\n" +
                                "\nCredits:\n" +
                                " is published under GPL3\n" +
                                "This app is not independend wallet app.")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
                break;
            }
            case 4: {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.reddit.com/r/gudana"));
                startActivity(i);
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
