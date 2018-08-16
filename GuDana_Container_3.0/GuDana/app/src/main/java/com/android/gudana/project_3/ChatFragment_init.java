package com.android.gudana.project_3;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
//import android.support.v4.app.Fragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.project_3.ui.ChatActivity;
import com.android.gudana.project_3.ui.ChatMessagesActivity;
import com.android.gudana.project_3.ui.FriendsListActivity;
import com.android.gudana.project_3.ui.ProfileActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.android.gudana.R;
import com.android.gudana.project_3.model.Chat;
import com.android.gudana.project_3.model.Message;
import com.android.gudana.project_3.model.User;
import com.android.gudana.project_3.utils.Constants;
import com.android.gudana.project_3.utils.EmailEncoding;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import com.android.gudana.R;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class ChatFragment_init extends Fragment {

    private RecyclerView recyclerView;

    private static final String USERNAME_LABEL = "Username";
    private static final String EMAIL_LABEL = "Email";
    private static final String SIGNOUT_LABEL = "Sign out";
    private static final String RESETPASS_LABEL = "Change Password";

    private static final int PICK_IMAGE = 1994;
    private Context context;

    private static final String TAG = "MainActivity";
    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mChatDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FloatingActionButton addUser;

    private ListView mChatListView;
    private FirebaseListAdapter mChatAdapter;
    private String mUsername;
    private ValueEventListener mValueEventListener;
    private DatabaseReference mUserDatabaseReference;
    private ImageView addConversationButton;



    public ChatFragment_init() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.p3_activity_main, container, false);

        //Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        context = view.getContext();

        //Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //Nav to ChatListActivity
                    createUser(user);
                    onSignedInInitialize(user);
                } else {
                    // User is signed out
                    //onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    // .setProviders(AuthUI.EMAIL_PROVIDER, AuthUI.GOOGLE_PROVIDER)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };


        addUser = (FloatingActionButton) view.findViewById(R.id.add_conversation);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ChatActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }


    /*

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.p3_menu_main, menu);
        return true;
    }

*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_calls_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sign_out) {
            AuthUI.getInstance()
                    .signOut(getActivity());
        }
        if (id == R.id.listFriends) {
            //Open up activity where a user can add and view friends
            Intent intent = new Intent(getActivity(), FriendsListActivity.class);
            startActivity(intent);
        }

        if (id == R.id.profilePage) {
            //Open up activity where a user can add and view friends
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        }

        return true;
    }

    private void hideShowAddChatButton(FirebaseUser user) {
        addConversationButton = (ImageView) getView().findViewById(R.id.add_conversation);
        final String userLoggedIn = user.getEmail();
        final DatabaseReference friendsCheckRef = mFirebaseDatabase.getReference(Constants.FRIENDS_LOCATION
                + "/" + EmailEncoding.commaEncodePeriod(userLoggedIn));
        friendsCheckRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long size = dataSnapshot.getChildrenCount();
                String strLong = Long.toString(size);
                if (size > 0) {
                    addConversationButton.setVisibility(View.VISIBLE);
                } else {
                    addConversationButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void onSignedInInitialize(FirebaseUser user) {

    }


    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getActivity(), "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Sign in canceled", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        } else if (resultCode == RESULT_OK) {

        }

    }




    private void createUser(FirebaseUser user) {
        final DatabaseReference usersRef = mFirebaseDatabase.getReference(Constants.USERS_LOCATION);
        final String encodedEmail = EmailEncoding.commaEncodePeriod(user.getEmail());
        final DatabaseReference userRef = usersRef.child(encodedEmail);
        final String username = user.getDisplayName();

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    User newUser = new User(username, encodedEmail);
                    userRef.setValue(newUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



}
