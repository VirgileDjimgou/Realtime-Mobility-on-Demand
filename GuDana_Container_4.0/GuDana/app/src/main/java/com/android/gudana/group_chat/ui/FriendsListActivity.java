package com.android.gudana.group_chat.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.android.gudana.R;
import com.android.gudana.group_chat.model.User;
import com.android.gudana.group_chat.utils.Constants;
import com.android.gudana.group_chat.utils.EmailEncoding;
// import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class FriendsListActivity extends AppCompatActivity {

    private String TAG = "Friends List Activity";

    private ListView mListView;

    private FirebaseListAdapter mFriendListAdapter;
    private ValueEventListener mValueEventListener;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mCurrentUsersFriends;
    private FirebaseAuth mFirebaseAuth;

    private final List<String> mUsersFriends = new ArrayList<>();
    private String mCurrentUserEmail;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p3_add_friends_activity);
        initializeScreen();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("Find new friends");


        showUserList();

        // TestDatabas();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFriendListAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFriendListAdapter.stopListening();
    }


    private void showUserList(){


        final DatabaseReference UsersRef =
                FirebaseDatabase.getInstance().getReference().child("users");

        FirebaseListOptions<User> options = new FirebaseListOptions.Builder<User>()
                .setQuery(mUserDatabaseReference, User.class)
                .setLayout(R.layout.p3_friend_item)
                .build();

        mFriendListAdapter = new FirebaseListAdapter<User>(options) {
            @Override
            protected void populateView(final View view, User user, final int position) {
                Log.e("TAG", user.toString());

                final String email = EmailEncoding.commaEncodePeriod(user.getEmail());
                //Check if this user is already your friend
                final DatabaseReference friendRef =
                        mFirebaseDatabase.getReference(Constants.FRIENDS_LOCATION
                                + "/" + mCurrentUserEmail + "/" + EmailEncoding.commaEncodePeriod(email));

                friendRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(email.equals(mCurrentUserEmail)){
                            view.findViewById(R.id.addFriend).setVisibility(View.GONE);
                            view.findViewById(R.id.removeFriend).setVisibility(View.GONE);
                        }else if(dataSnapshot.getValue() != null){
                            Log.w(TAG, "User is friend");
                            view.findViewById(R.id.addFriend).setVisibility(View.GONE);
                            view.findViewById(R.id.removeFriend).setVisibility(View.VISIBLE);
                        }else{
                            Log.w(TAG, "User is not friend");
                            view.findViewById(R.id.removeFriend).setVisibility(View.GONE);
                            view.findViewById(R.id.addFriend).setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                if(user.getProfilePicLocation() != null && user.getProfilePicLocation().length() > 0){
                    StorageReference storageRef = FirebaseStorage.getInstance()
                            .getReference().child(user.getProfilePicLocation());
                    /*
                    Glide.with(view.getContext())
                            .using(new FirebaseImageLoader())
                            .load(storageRef)
                            .bitmapTransform(new CropCircleTransformation(view.getContext()))
                            .into((ImageView)view.findViewById(R.id.photoImageView));

                        */


                    Glide.with(view.getContext())
                            .setDefaultRequestOptions(new RequestOptions()
                                    .circleCrop()
                                    .centerCrop()
                                    .fitCenter())
                            .load(storageRef)
                            .into((ImageView)view.findViewById(R.id.photoImageView));
                }

                ((TextView)view.findViewById(R.id.messageTextView)).setText(user.getUsername());
                ((TextView)view.findViewById(R.id.nameTextView)).setText(EmailEncoding.commaDecodePeriod(email));
                (view.findViewById(R.id.addFriend)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.w(TAG, "Clicking row: " + position);
                        Log.w(TAG, "Clicking user: " + email);
                        //Add this user to your friends list, by email
                        addNewFriend(email);
                    }
                });
                (view.findViewById(R.id.removeFriend)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.w(TAG, "Clicking row: " + position);
                        Log.w(TAG, "Clicking user: " + email);
                        //Add this user to your friends list, by email
                        removeFriend(email);
                    }
                });
              }
        };

        mListView.setAdapter(mFriendListAdapter);

        mValueEventListener = mUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user == null){
                    finish();
                    return;
                }
                mFriendListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void removeFriend(String friendEmail){
        //Get current user logged in by email
        final String userLoggedIn = mFirebaseAuth.getCurrentUser().getEmail();
        Log.e(TAG, "User logged in is: " + userLoggedIn);
        final DatabaseReference friendsRef = mFirebaseDatabase.getReference(Constants.FRIENDS_LOCATION
                + "/" + EmailEncoding.commaEncodePeriod(userLoggedIn));
        friendsRef.child(EmailEncoding.commaEncodePeriod(friendEmail)).removeValue();
    }

    private void addNewFriend(String newFriendEmail){
        //Get current user logged in by email
        final String userLoggedIn = mFirebaseAuth.getCurrentUser().getEmail();
        Log.e(TAG, "User logged in is: " + userLoggedIn);
        //final String newFriendEncodedEmail = EmailEncoding.commaEncodePeriod(newFriendEmail);
        final DatabaseReference friendsRef = mFirebaseDatabase.getReference(Constants.FRIENDS_LOCATION
                + "/" + EmailEncoding.commaEncodePeriod(userLoggedIn));
        //Add friends to current users friends list
        friendsRef.child(newFriendEmail).setValue(newFriendEmail);
    }

    private void initializeScreen(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurrentUserEmail = EmailEncoding.commaEncodePeriod(mFirebaseAuth.getCurrentUser().getEmail().toString());
        //Eventually this list will filter out users that are already your friend
        mUserDatabaseReference = mFirebaseDatabase.getReference().child(Constants.USERS_LOCATION);
        mCurrentUsersFriends = mFirebaseDatabase.getReference().child(Constants.FRIENDS_LOCATION
            + "/" + EmailEncoding.commaEncodePeriod(mFirebaseAuth.getCurrentUser().getEmail()));

        mListView = (ListView) findViewById(R.id.friendsListView);

    }
}
