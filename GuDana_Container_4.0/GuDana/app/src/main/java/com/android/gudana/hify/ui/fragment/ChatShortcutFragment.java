package com.android.gudana.hify.ui.fragment;

import android.app.Dialog;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.android.gudana.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.android.gudana.chatapp.activities.ChatActivity;
import com.android.gudana.chatapp.holders.ChatHolder;
import com.android.gudana.chatapp.models.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;




/**
 * Created by krupenghetiya on 23/06/17.
 */

public class ChatShortcutFragment extends AAH_FabulousFragment {

    Button btn_close;

    private FirebaseRecyclerAdapter adapter;
    FloatingActionButton action_chat ;
    public static  String WindowsTitel = "Titel";


    public static ChatShortcutFragment newInstance() {
        ChatShortcutFragment f = new ChatShortcutFragment();
        return f;
    }


    @Override

    public void setupDialog(Dialog dialog, int style) {
        final View contentView = View.inflate(getContext(), R.layout.filter_sample_view, null);

        RelativeLayout rl_content = (RelativeLayout) contentView.findViewById(R.id.rl_content);
        //RecyclerView rl_content = (RecyclerView) contentView.findViewById(R.id.chat_recycler);
        LinearLayout ll_buttons = (LinearLayout) contentView.findViewById(R.id.ll_buttons);
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        TextView TitelWindows =  (TextView) contentView.findViewById(R.id.titel_windows);
        TitelWindows.setText(WindowsTitel);

        // Initialize Chat Database
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        DatabaseReference chatDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId);
        chatDatabase.keepSynced(true); // For offline use

        // RecyclerView related
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        RecyclerView recyclerView = contentView.findViewById(R.id.chat_recycler);
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

                TextView text = contentView.findViewById(R.id.f_chat_text);

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

        contentView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFilter("closed");
            }
        });

        try{

            //params to set
            setAnimationDuration(500); //optional; default 500ms
            setPeekHeight(300); // optional; default 400dp
            //setCallbacks((Callbacks) getActivity()); //optional; to get back result
            setViewgroupStatic(ll_buttons); // optional; layout to stick at bottom on slide
//        setViewPager(vp_types); //optional; if you use viewpager that has scrollview
            setViewMain(rl_content); //necessary; main bottomsheet view
            setMainContentView(contentView); // necessary; call at end before super
            super.setupDialog(dialog, style); //call super at last

        }catch (Exception ex){
            ex.printStackTrace();
        }
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
