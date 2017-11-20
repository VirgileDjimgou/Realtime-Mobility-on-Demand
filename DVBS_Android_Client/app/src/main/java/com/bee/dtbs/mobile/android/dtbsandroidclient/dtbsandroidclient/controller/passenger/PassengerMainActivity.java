package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.passenger;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.AboutFragment;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.LoginActivity;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.config.DtbsPreferences;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.service.network.NetworkMonitor;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.SettingsFragment;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.controller.booking.history.BookingHistoryFragment;
import com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model.Account;

public class PassengerMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView txtUsername;
    private TextView txtEmail;

    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.bee.dtbs.mobile.android.dtbsandroidclient.R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, com.bee.dtbs.mobile.android.dtbsandroidclient.R.string.navigation_drawer_open, com.bee.dtbs.mobile.android.dtbsandroidclient.R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(PassengerMainActivity.this);

        getFragmentManager().beginTransaction().add(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.content_frame, new PassengerMapFragment()).commit();

        snackbar = Snackbar
                .make(findViewById(android.R.id.content), "Connectivity Issues Detected!", Snackbar.LENGTH_INDEFINITE);

        View view = snackbar.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.topMargin = 50;
        view.setLayoutParams(params);

        LocalBroadcastManager.getInstance(this).registerReceiver(mNetworkMonitorReceiver,
                new IntentFilter(DtbsPreferences.NETWORK_STATE_EVENT));
    }

    /**
     * Called when the activity has detected the user's press of the back key.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            int count = getFragmentManager().getBackStackEntryCount();

            if (count == 0) {
                super.onBackPressed();
            } else {
                getFragmentManager().popBackStack();
            }
        }
    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     * @param menu menu to initialise.
     * @return true if successful initialisation else false.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.bee.dtbs.mobile.android.dtbsandroidclient.R.menu.main, menu);
        txtUsername = (TextView)findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_username);
        txtUsername.setText(Account.getInstance().getCommonName() + " " + Account.getInstance().getFamilyName());
        txtEmail = (TextView)findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.txt_email_address);
        txtEmail.setText(Account.getInstance().getEmail());
        return true;
    }

    /**
     * Called whenever a navigation item is selected.
     *
     * @param item item pressed.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id){

            case com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.logout:
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                break;
            case com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.taxi_history:
                fragment = new BookingHistoryFragment();
                break;
            case com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.book_taxi:
                fragment = new PassengerMapFragment();
                break;
            case com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.about:
                fragment = new AboutFragment();
                break;
            case com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.settings:
                fragment = new SettingsFragment();
        }

        // replace fragment if event handled.
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.content_frame, fragment).commit();
        }else{
            return false;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(com.bee.dtbs.mobile.android.dtbsandroidclient.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    NetworkMonitor mNetworkMonitorReceiver = new NetworkMonitor() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(isConnected(context)){
                snackbar.dismiss();
            }else{
                snackbar.show();
            }
        }
    };
}
