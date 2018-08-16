package com.android.gudana.project_3.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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

import com.android.gudana.R;
import com.android.gudana.project_3.model.User;
import com.android.gudana.project_3.utils.Constants;
import com.android.gudana.project_3.utils.EmailEncoding;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.android.gudana.R2.drawable.user;

public class FriendsListActivity extends AppCompatActivity {

    private String TAG = "Friends List Activity";

    private ListView mListView;
    private Toolbar mToolBar;

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

        mToolBar.setTitle("Find new friends");

        showUserList();
    }

    private void showUserList(){

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
        mToolBar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
