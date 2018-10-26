package com.android.gudana.hify.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.android.gudana.R;
import com.android.gudana.apprtc.CallFragment;
import com.android.gudana.chatapp.activities.ChatActivity;
import com.android.gudana.hify.adapters.PostsAdapter;
import com.android.gudana.hify.models.Post;
import com.android.gudana.hify.ui.fragment.ChatShortcutFragment;
import com.android.gudana.hify.ui.fragment.FriendUpdateShortcutFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nightonke.boommenu.Animation.BoomEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.tylersuehr.esr.EmptyStateRecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

import static com.android.gudana.hify.ui.activities.MainActivity.loadpost_firstime;
import static com.android.gudana.hify.ui.activities.MainActivity.mode_public;

/**
 * Created by amsavarthan on 29/3/18.
 */

public class FriendsUpdates extends AppCompatActivity{

    private String TAG = FriendsUpdates.class.getName();


    List<Post> mPostsList;
    Query mQuery;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    EmptyStateRecyclerView mPostsRecyclerView;
    PostsAdapter mAdapter;
    View view_layout;
    private List<String> mFriendIdList=new ArrayList<>();
    ChatShortcutFragment dialogFrag ;
    FriendUpdateShortcutFragment dialog_friends;
    private  String SelectedUserId="";



    private Context context;
    public static ViewPager mViewPager;
    public LinearLayout SearchAppbar;
    private String id,friend_name, friend_email, friend_image, friend_token;
    private  ActionBar actionBar;




    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_card_main_updates);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("User Name  Last Updates ");

        context = FriendsUpdates.this.getApplicationContext();

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mPostsList = new ArrayList<>();
        mAdapter = new PostsAdapter(mPostsList, context,FriendsUpdates.this);
        mPostsRecyclerView = findViewById(R.id.posts_recyclerview_new);
        mPostsRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mPostsRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        mPostsRecyclerView.setLayoutManager(new LinearLayoutManager(FriendsUpdates.this));
        mPostsRecyclerView.setHasFixedSize(true);
        mPostsRecyclerView.setAdapter(mAdapter);

        /*
        mPostsRecyclerView.setStateDisplay(EmptyStateRecyclerView.STATE_EMPTY,
                new ImageTextStateDisplay(view.getContext(),R.mipmap.no_posts,"No posts found","Add some friends to see their posts."));

         */

        /*
        mPostsRecyclerView.setStateDisplay(EmptyStateRecyclerView.STATE_ERROR,
                new TextStateDisplay(view.getContext(),"Sorry for inconvenience","Something went wrong :("));

         */

        /*

         */

        SelectedUserId = getIntent().getStringExtra("user_id");

        getUserProfile();

        getYourOwnPost();
        // to test the update without refresch  ()
        updateYourPostRealtime();


    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onBackPressed()
    {
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                // NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    /// ########### private Mode when you will see once the post of your friends ....

    private void  updateYourPostRealtime(){
        FirebaseFirestore.getInstance().collection("Posts")
                .whereEqualTo("userId", SelectedUserId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }


                        for(DocumentChange doc:snapshots.getDocumentChanges()){
                            Post post = doc.getDocument().toObject(Post.class).withId(doc.getDocument().getId());
                            mPostsList.add(0,post);
                            mAdapter.notifyDataSetChanged();
                        }

                        // instead of simply using the entire query snapshot
                        // see the actual changes to query results between query snapshots (added, removed, and modified)
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New city: " + dc.getDocument().getData());
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });
    }

    private void getYourOwnPost() {

        FirebaseFirestore.getInstance().collection("Posts")
                .whereEqualTo("userId", SelectedUserId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {

                        if(!querySnapshot.isEmpty()){

                            for(DocumentChange doc:querySnapshot.getDocumentChanges()){
                                Post post = doc.getDocument().toObject(Post.class).withId(doc.getDocument().getId());
                                mPostsList.add(post);
                                mAdapter.notifyDataSetChanged();
                            }


                        }else{
                            mPostsRecyclerView.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error",e.getMessage());
                    }
                });

    }

    private void getUserProfile(){

        // get Users Informations
        mFirestore.collection("Users")
                .document(SelectedUserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        friend_name=documentSnapshot.getString("name");
                        friend_image=documentSnapshot.getString("image");
                        friend_token=documentSnapshot.getString("token_id");

                        if(friend_name!=null){
                            actionBar.setTitle(friend_name+" Last Updates ");
                        }

                        // je ne suisjkghgdjjhgsdfjkjd jgjhsdgjhjhgjjhghfgfgh ghfhgfihhkjlkhkjghjgj

                        /*
                        Glide.with(FriendsListeActivity.this)
                                .setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.default_user_art_g_2))
                                .load(friend_image)
                                .into(profile_pic);

                                */


                    }
                });
    }

    private void  updateFriendsPostRealtime(){

        mFirestore.collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)

                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        if (!snapshots.isEmpty()) {

                            for (final DocumentChange doc : snapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {


                                    mFirestore.collection("Users")
                                            .document(currentUser.getUid())
                                            .collection("Friends")
                                            .get()

                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot querySnapshot) {

                                                    for (DocumentChange documentChange : querySnapshot.getDocumentChanges()) {

                                                        if (documentChange.getType() == DocumentChange.Type.ADDED) {

                                                            if(documentChange.getDocument().getId().equals(doc.getDocument().get("userId"))){

                                                                Post post = doc.getDocument().toObject(Post.class).withId(doc.getDocument().getId());
                                                                mPostsList.add(0,post);
                                                                mAdapter.notifyDataSetChanged();

                                                            }

                                                        }

                                                    }


                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    mPostsRecyclerView.invokeState(EmptyStateRecyclerView.STATE_ERROR);
                                                    Log.w("Error", "listen:error", e);
                                                }
                                            });

                                }

                            }

                        }

                        // instead of simply using the entire query snapshot
                        // see the actual changes to query results between query snapshots (added, removed, and modified)
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New city: " + dc.getDocument().getData());
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });
    }

    public void getPosts() {


        mFirestore.collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            for (final DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {


                                    mFirestore.collection("Users")
                                            .document(currentUser.getUid())
                                            .collection("Friends")
                                            .get()

                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot querySnapshot) {

                                                    for (DocumentChange documentChange : querySnapshot.getDocumentChanges()) {

                                                        if (documentChange.getType() == DocumentChange.Type.ADDED) {

                                                            if(documentChange.getDocument().getId().equals(doc.getDocument().get("userId"))){

                                                                Post post = doc.getDocument().toObject(Post.class).withId(doc.getDocument().getId());
                                                                mPostsList.add(post);
                                                                mAdapter.notifyDataSetChanged();

                                                            }

                                                        }

                                                    }


                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    mPostsRecyclerView.invokeState(EmptyStateRecyclerView.STATE_ERROR);
                                                    Log.w("Error", "listen:error", e);
                                                }
                                            });

                                }

                            }

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mPostsRecyclerView.invokeState(EmptyStateRecyclerView.STATE_ERROR);
                        Log.w("Error", "listen:error", e);
                    }
                });

    }

    //7777 ####public mode when you will see all the  post on the network   ...  ...

    private void getAllPost() {

        FirebaseFirestore.getInstance().collection("Posts")
                //.whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {

                        if(!querySnapshot.isEmpty()){

                            for(DocumentChange doc:querySnapshot.getDocumentChanges()){
                                Post post = doc.getDocument().toObject(Post.class).withId(doc.getDocument().getId());
                                mPostsList.add(post);
                                mAdapter.notifyDataSetChanged();
                            }


                        }else{
                            // mPostsRecyclerView.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error",e.getMessage());
                    }
                });

    }

    private void  updatePostRealtime(){
        FirebaseFirestore.getInstance().collection("Posts")
                //.whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }


                        for(DocumentChange doc:snapshots.getDocumentChanges()){
                            Post post = doc.getDocument().toObject(Post.class).withId(doc.getDocument().getId());
                            mPostsList.add(0,post); // always on the top of the list
                            mAdapter.notifyDataSetChanged();
                        }

                        // instead of simply using the entire query snapshot
                        // see the actual changes to query results between query snapshots (added, removed, and modified)
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New city: " + dc.getDocument().getData());
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });
    }



}
