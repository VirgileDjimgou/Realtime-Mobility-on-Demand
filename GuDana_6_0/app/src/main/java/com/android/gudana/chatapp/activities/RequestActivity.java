package com.android.gudana.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.R;
import com.android.gudana.chatapp.holders.RequestHolder;
import com.android.gudana.chatapp.models.Request;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;


public class RequestActivity extends AppCompatActivity
{
    private final String TAG = "CA/UsersActivity";

    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ca_fragment_request);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle(R.string.see_all_users);



        final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initializing Request database

        DatabaseReference requestsDatabase = FirebaseDatabase.getInstance().getReference().child("Requests").child(currentUserId);
        requestsDatabase.keepSynced(true); // For offline use

        // RecyclerView related

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RequestActivity.this.getApplicationContext());

        RecyclerView recyclerView =  findViewById(R.id.f_request_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        // Initializing adapter

        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>().setQuery(requestsDatabase.orderByChild("type"), Request.class).build();

        adapter = new FirebaseRecyclerAdapter<Request, RequestHolder>(options)
        {
            @Override
            protected void onBindViewHolder(final RequestHolder holder, int position, final Request model)
            {
                if(model.getType().equals("sent"))
                {
                    holder.getView().setVisibility(View.GONE);
                }
                else
                {
                    final String userid = getRef(position).getKey();

                    holder.setHolder(userid);
                    holder.getView().setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            PopupMenu popup = new PopupMenu(RequestActivity.this.getApplicationContext(), view);

                            popup.getMenu().add(Menu.NONE, 1, 1, "View Profile");
                            popup.getMenu().add(Menu.NONE, 2, 2, "Accept Request");

                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                            {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem)
                                {
                                    switch(menuItem.getItemId())
                                    {
                                        case 1:
                                            Intent userProfileIntent = new Intent(RequestActivity.this.getApplicationContext(), ProfileActivity.class);
                                            userProfileIntent.putExtra("userid", userid);
                                            startActivity(userProfileIntent);
                                            return true;
                                        case 2:
                                            // Pushing notification to get keyId

                                            DatabaseReference acceptNotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(userid).push();
                                            String acceptNotificationId = acceptNotificationRef.getKey();

                                            // "Packing" request

                                            HashMap<String, String> acceptNotificationData = new HashMap<>();
                                            acceptNotificationData.put("from", currentUserId);
                                            acceptNotificationData.put("type", "accept");

                                            // "Packing" data

                                            Map map = new HashMap<>();
                                            map.put("Friends/" + userid + "/" + currentUserId + "/date", ServerValue.TIMESTAMP);
                                            map.put("Friends/" + currentUserId + "/" + userid + "/date", ServerValue.TIMESTAMP);

                                            map.put("Requests/" + userid + "/" + currentUserId, null);
                                            map.put("Requests/" + currentUserId + "/" + userid, null);

                                            map.put("Notifications/" + userid + "/" + acceptNotificationId, acceptNotificationData);

                                            // Updating data

                                            FirebaseDatabase.getInstance().getReference().updateChildren(map, new DatabaseReference.CompletionListener()
                                            {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                                                {
                                                    if(databaseError == null)
                                                    {
                                                        Toast.makeText(RequestActivity.this.getApplicationContext(), "You are now friends!", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else
                                                    {
                                                        Log.d(TAG, "acceptRequest failed: " + databaseError.getMessage());
                                                    }
                                                }
                                            });
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
            }

            @Override
            public RequestHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ca_user, parent, false);

                return new RequestHolder(RequestActivity.this, view, RequestActivity.this.getApplicationContext());
            }

            @Override
            public void onDataChanged()
            {
                super.onDataChanged();

                int counter = 0;

                for(int i = 0; i < adapter.getItemCount(); i++)
                {
                    Request tmp = (Request) adapter.getItem(i);

                    if(tmp != null && tmp.getType().equals("received"))
                    {
                        counter++;
                    }
                }

                TextView text = findViewById(R.id.f_request_text);

                if(counter == 0)
                {
                    text.setVisibility(View.VISIBLE);
                }
                else
                {
                    text.setVisibility(View.GONE);
                }
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
