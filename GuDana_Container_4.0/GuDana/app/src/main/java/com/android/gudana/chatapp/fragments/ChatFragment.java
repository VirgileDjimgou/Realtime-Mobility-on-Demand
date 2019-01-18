package com.android.gudana.chatapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.apprtc.ConnectActivity;
import com.android.gudana.chatapp.activities.FriendsActivity;
import com.android.gudana.chatapp.activities.ProfileActivity;
import com.android.gudana.hify.ui.activities.FriendlistScrollingActivity;
import com.android.gudana.hify.ui.activities.FriendsUpdates;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.android.gudana.R;
import com.android.gudana.chatapp.activities.ChatActivity;
import com.android.gudana.chatapp.holders.ChatHolder;
import com.android.gudana.chatapp.models.Chat;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;
import jahirfiquitiva.libs.fabsmenu.FABsMenu;
import jahirfiquitiva.libs.fabsmenu.TitleFAB;


public class ChatFragment extends Fragment
{
    private FirebaseRecyclerAdapter adapter;
    FloatingActionButton action_chat ;

    FloatingActionMenu MenuChat;
    com.github.clans.fab.FloatingActionButton Select_Contact;
    Context  mContext;


    public ChatFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.ca_fragment_chat, container, false);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize Chat Database
        // init Context  ...
        mContext = getContext();

        // floating fab  ...
        MenuChat = (FloatingActionMenu) view.findViewById(R.id.chat_action_menu);
        Select_Contact = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.select_contact);
        Select_Contact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                //Intent IntentUpdate =  new Intent(mContext, ChatListActivity.class);
                //mContext.startActivity(IntentUpdate);
            }
        });


        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        DatabaseReference chatDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId);
        chatDatabase.keepSynced(true); // For offline use

        // RecyclerView related

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        RecyclerView recyclerView = view.findViewById(R.id.chat_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        // Initializing adapter

        FirebaseRecyclerOptions<Chat> options = new FirebaseRecyclerOptions.Builder<Chat>().setQuery(chatDatabase.orderByChild("timestamp"), Chat.class).build();

        adapter = new FirebaseRecyclerAdapter<Chat, ChatHolder>(options)
        {
            @Override
            public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ca_user, parent, false);

                return new ChatHolder(getActivity(), view, getContext());
            }

            @Override
            protected void onBindViewHolder(final ChatHolder holder, int position, final Chat model)
            {
                final String userid = getRef(position).getKey();
                holder.setHolder(userid, model.getMessage(), model.getTimestamp(), model.getSeen());
                holder.getView().setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                        chatIntent.putExtra("userid", userid);
                        startActivity(chatIntent);
                    }
                });
            }

            @Override
            public void onDataChanged()
            {
                super.onDataChanged();

                TextView text = view.findViewById(R.id.f_chat_text);

                if(adapter.getItemCount() == 0)
                {
                    text.setVisibility(View.VISIBLE);
                }
                else
                {
                    text.setVisibility(View.GONE);
                }
            }
        };

        // init flaoting action button

        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        adapter.startListening();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop()
    {
        super.onStop();

        adapter.stopListening();
    }

    // init button
}
