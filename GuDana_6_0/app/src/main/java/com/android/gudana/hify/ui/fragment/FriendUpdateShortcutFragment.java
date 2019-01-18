package com.android.gudana.hify.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.android.gudana.R;
import com.android.gudana.hify.adapters.viewFriends.RecyclerViewTouchHelper;
import com.android.gudana.hify.adapters.viewFriends.ViewFriendAdapter;
import com.android.gudana.hify.models.ViewFriends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tylersuehr.esr.EmptyStateRecyclerView;
import com.tylersuehr.esr.TextStateDisplay;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator;


/**
 * Created by krupenghetiya on 23/06/17.
 */

public class FriendUpdateShortcutFragment extends AAH_FabulousFragment {

    Button btn_close;


    private List<ViewFriends> usersList;
    private ViewFriendAdapter usersAdapter;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private EmptyStateRecyclerView mRecyclerView;
    public Context mContext;


    private FirebaseRecyclerAdapter adapter;
    FloatingActionButton action_chat ;
    public static  String WindowsTitel = "Titel";

    public static FriendUpdateShortcutFragment newInstance() {
        FriendUpdateShortcutFragment f = new FriendUpdateShortcutFragment();
        return f;
    }

    @Override

    public void setupDialog(Dialog dialog, int style) {
        final View contentView = View.inflate(getContext(), R.layout.view_friends_update, null);

        RelativeLayout rl_content = (RelativeLayout) contentView.findViewById(R.id.rl_content);
        //RecyclerView rl_content = (RecyclerView) contentView.findViewById(R.id.chat_recycler);
        LinearLayout ll_buttons = (LinearLayout) contentView.findViewById(R.id.ll_buttons);
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        TextView TitelWindows =  (TextView) contentView.findViewById(R.id.titel_windows);
        TitelWindows.setText(WindowsTitel);
        mContext = getContext();

        // sset another color on the background to make a difference  with  chat shortcut
        rl_content.setBackgroundColor(getResources().getColor(R.color.bg_gradient_end));


        // Initialize Chat Database
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

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mRecyclerView =  contentView.findViewById(R.id.chat_recycler);

        usersList = new ArrayList<>();
        usersAdapter = new ViewFriendAdapter(usersList, contentView.getContext(), "FriendsLastUpdates");

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerViewTouchHelper(0, ItemTouchHelper.LEFT, new RecyclerViewTouchHelper.RecyclerItemTouchHelperListener() {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
                if (viewHolder instanceof ViewFriendAdapter.ViewHolder) {

                    usersAdapter.removeItem(viewHolder.getAdapterPosition());

                }
            }
        });

        mRecyclerView.setItemAnimator(new FlipInTopXAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(contentView.getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(contentView.getContext(), DividerItemDecoration.VERTICAL));
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(usersAdapter);

        mRecyclerView.setStateDisplay(EmptyStateRecyclerView.STATE_EMPTY,
                new TextStateDisplay(contentView.getContext(),"No friends found","Add some friends to manage them here"));

        mRecyclerView.setStateDisplay(EmptyStateRecyclerView.STATE_LOADING,
                new TextStateDisplay(contentView.getContext(),"We found some of your friends","We are getting information of your friends.."));

        mRecyclerView.setStateDisplay(EmptyStateRecyclerView.STATE_ERROR,
                new TextStateDisplay(contentView.getContext(),"Sorry for inconvenience","Something went wrong :("));

        startListening();


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

        try{
            adapter.startListening();
            adapter.notifyDataSetChanged();

        }catch(Exception ex){

        }
    }

    public void startListening() {
        usersList.clear();
        firestore.collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Friends")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if(!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    ViewFriends users = doc.getDocument().toObject(ViewFriends.class);
                                    usersList.add(users);
                                    usersAdapter.notifyDataSetChanged();
                                }
                            }
                        }else{
                            Toast.makeText(mContext, "No friends found.", Toast.LENGTH_SHORT).show();
                            mRecyclerView.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        mRecyclerView.invokeState(EmptyStateRecyclerView.STATE_ERROR);
                        Log.w("Error", "listen:error", e);

                    }
                });
    }


    @Override
    public void onStop()
    {
        super.onStop();

        try{
            adapter.stopListening();

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

}
