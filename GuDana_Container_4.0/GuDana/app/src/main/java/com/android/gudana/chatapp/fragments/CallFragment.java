package com.android.gudana.chatapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.gudana.R;
import com.android.gudana.chatapp.activities.ChatActivity;
import com.android.gudana.chatapp.holders.CallHolder;
import com.android.gudana.chatapp.holders.ChatHolder;
import com.android.gudana.chatapp.models.Call;
import com.android.gudana.chatapp.models.Chat;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class CallFragment extends Fragment
{
    private FirebaseRecyclerAdapter adapter;
    FloatingActionButton action_chat ;

    public CallFragment()
    {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.ca_fragment_call, container, false);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize Chat Database

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        // DatabaseReference chatDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        DatabaseReference callDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("call_History").child("Call_room");
        callDatabase.keepSynced(true); // For offline use

        // RecyclerView related

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        RecyclerView recyclerView = view.findViewById(R.id.call_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        // Initializing adapter

        FirebaseRecyclerOptions<Call> options = new FirebaseRecyclerOptions.Builder<Call>().setQuery(callDatabase.orderByChild("timestamp"), Call.class).build();

        adapter = new FirebaseRecyclerAdapter<Call, CallHolder>(options)
        {
            @Override
            public CallHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ca_call_user, parent, false);

                return new CallHolder(getActivity(), view, getContext());
            }

            @Override
            protected void onBindViewHolder(final CallHolder holder, int position, final Call model)
            {

                // test  firebase function

                final String UserID = FirebaseAuth.getInstance().getUid();
                final String Call_id_node = getRef(position).getKey();
                try{
                    holder.setHolder(UserID , Call_id_node, model.getMessage(), model.getTimestamp(), model.getSeen());
                    holder.getView().setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                            chatIntent.putExtra("userid", Call_id_node);
                            startActivity(chatIntent);
                        }
                    });

                }catch(Exception ex){
                    ex.printStackTrace();
                }

            }

            @Override
            public void onDataChanged()
            {
                super.onDataChanged();
                TextView text = view.findViewById(R.id.f_call_text);
                try{
                    if(adapter.getItemCount() == 0)
                    {
                        text.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        text.setVisibility(View.GONE);
                    }

                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        };

        recyclerView.setAdapter(adapter);
        // Test  this data base
        // CallHolder.TestFirebase__infos(FirebaseAuth.getInstance().getUid());
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
}
