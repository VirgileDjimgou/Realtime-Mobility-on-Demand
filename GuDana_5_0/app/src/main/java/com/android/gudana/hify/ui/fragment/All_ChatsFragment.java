package com.android.gudana.hify.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.gudana.R;
import com.android.gudana.chatapp.fragments.ChatFragment;
import com.android.gudana.hify.ui.activities.friends.SearchUsersActivity;

/**
 * Created by amsavarthan on 29/3/18.
 */

public class All_ChatsFragment extends Fragment {

    View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.all_chats_fragment, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


       // floating action button ....  -----  ....
        loadFragment(new ChatFragment()); /// Friends Fragment may cause a bug  with parse of firebase data
        BottomNavigationView bottomNavigationView=mView.findViewById(R.id.bottom_nav_allchat);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_single_chat:
                        loadFragment(new ChatFragment()); // for single Chats
                        break;
                    case R.id.action_group_chat:
                        loadFragment(new ChatFragment()); // for group Chats
                        break;
                    default:

                }
                return true;
            }
        });


    }

    private void loadFragment(Fragment fragment) {

        try{

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_all_chats, fragment)
                    .commit();

        }catch(Exception ex){
            ex.printStackTrace();

        }
    }



}
