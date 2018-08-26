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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.android.gudana.R;
import com.android.gudana.chatapp.holders.UserHolder;
import com.android.gudana.chatapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

/**
 * This is a part of ChatApp Project (https://github.com/h01d/ChatApp)
 * Licensed under Apache License 2.0
 *
 * @author  Raf (https://github.com/h01d)
 * @version 1.1
 * @since   27/02/2018
 */

public class UsersActivity extends AppCompatActivity
{
    private final String TAG = "CA/UsersActivity";

    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ca_activity_users);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle(R.string.see_all_users);

        // RecyclerView related

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        RecyclerView recyclerView = findViewById(R.id.users_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        // Initializing Users database

        DatabaseReference usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        usersDatabase.keepSynced(true); // For offline use

        // Initializing adapter

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(usersDatabase.orderByChild("name"), User.class).build();

        adapter = new FirebaseRecyclerAdapter<User, UserHolder>(options)
        {
            @Override
            protected void onBindViewHolder(final UserHolder holder, int position, User model)
            {
                final String userid = getRef(position).getKey();

                holder.setHolder(userid);
                holder.getView().setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent userProfileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        userProfileIntent.putExtra("userid", userid);
                        startActivity(userProfileIntent);
                    }
                });
            }

            @Override
            public UserHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ca_user, parent, false);

                return new UserHolder(UsersActivity.this, view, getApplicationContext());
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
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue("true");
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);
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
