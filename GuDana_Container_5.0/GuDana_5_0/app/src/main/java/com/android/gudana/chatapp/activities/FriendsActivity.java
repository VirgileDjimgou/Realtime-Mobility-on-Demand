package com.android.gudana.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.gudana.R;
import com.android.gudana.tindroid.MessageActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.gudana.chatapp.holders.FriendHolder;
import com.android.gudana.chatapp.models.Friend;

/**
 * This is a part of ChatApp Project (https://github.com/h01d/ChatApp)
 * Licensed under Apache License 2.0
 *
 * @author  Raf (https://github.com/h01d)
 * @version 1.1
 * @since   27/02/2018
 */

public class FriendsActivity extends AppCompatActivity
{
    private final String TAG = "CA/UsersActivity";
    private FirebaseRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ca_fragment_friends);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle(R.string.see_all_users);


        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initializing Friends database

        DatabaseReference friendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUserId);
        friendsDatabase.keepSynced(true); // For offline use

        // RecyclerView related

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FriendsActivity.this.getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        final RecyclerView recyclerView = findViewById(R.id.friends_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        // Initializing adapter

        FirebaseRecyclerOptions<Friend> options = new FirebaseRecyclerOptions.Builder<Friend>().setQuery(friendsDatabase.orderByChild("date"), Friend.class).build();

        adapter = new FirebaseRecyclerAdapter<Friend, FriendHolder>(options)
        {
            @Override
            protected void onBindViewHolder(final FriendHolder holder, int position, final Friend model)
            {
                final String userid = getRef(position).getKey();

                holder.setHolder(userid, model.getDate());
                holder.getView().setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        PopupMenu popup = new PopupMenu(FriendsActivity.this.getApplicationContext(), view);

                        popup.getMenu().add(Menu.NONE, 1, 1, "View Profile");
                        popup.getMenu().add(Menu.NONE, 2, 2, "Send Message");

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                        {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem)
                            {
                                switch(menuItem.getItemId())
                                {
                                    case 1:
                                        Intent userProfileIntent = new Intent(FriendsActivity.this.getApplicationContext(), ProfileActivity.class);
                                        userProfileIntent.putExtra("userid", userid);
                                        startActivity(userProfileIntent);
                                        return true;
                                    case 2:
                                        Intent sendMessageIntent = new Intent(FriendsActivity.this, MessageActivity.class);
                                        sendMessageIntent.putExtra("userid", userid);
                                        startActivity(sendMessageIntent);
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        popup.show();
                    }
                });
            }

            @Override
            public FriendHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ca_user, parent, false);

                return new FriendHolder(FriendsActivity.this, view, FriendsActivity.this.getApplicationContext());
            }

            @Override
            public void onDataChanged()
            {
                super.onDataChanged();

                TextView text = findViewById(R.id.f_friends_text);

                if(adapter.getItemCount() == 0)
                {
                    text.setVisibility(View.VISIBLE);
                }
                else
                {
                    text.setVisibility(View.GONE);
                }

                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        };

        recyclerView.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_all_users, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.action_call_map_users:
                Toast.makeText(getApplicationContext(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_search_user:
                Toast.makeText(getApplicationContext(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        adapter.startListening();
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume()
    {
        super.onResume();

    }

    @Override
    protected void onPause()
    {
        super.onPause();

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        adapter.stopListening();


    }

    @Override
    public void onBackPressed()
    {
        NavUtils.navigateUpFromSameTask(this);
    }


}
