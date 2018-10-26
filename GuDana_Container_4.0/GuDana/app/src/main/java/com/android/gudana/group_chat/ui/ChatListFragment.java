package com.android.gudana.group_chat.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.R;
import com.android.gudana.group_chat.model.Chat;
import com.android.gudana.group_chat.model.Message;
import com.android.gudana.group_chat.model.User;
import com.android.gudana.group_chat.utils.Constants;
import com.android.gudana.group_chat.utils.EmailEncoding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

public class ChatListFragment extends Fragment {

    private static final String TAG = "MainActivity";
    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mChatDatabaseReference;
    private ChildEventListener mChildEventListener;

    private ListView mChatListView;
    private FirebaseListAdapter mChatAdapter;
    private String mUsername;
    private ValueEventListener mValueEventListener;
    private DatabaseReference mUserDatabaseReference;
    private ImageView addConversationButton;
    public static DatabaseReference userDB_current;

    Context mContext;
    View view ;



    public ChatListFragment(){

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.p3_activity_main, container, false);

        mContext = getContext();


        try{

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();


        addConversationButton = (ImageView) view.findViewById(R.id.add_conversation);
        addConversationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewChat();
            }
        });



            FirebaseUser user = mFirebaseAuth.getCurrentUser();
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
                                        .setAvailableProviders(Arrays.asList(
                                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                                new AuthUI.IdpConfig.GoogleBuilder().build()))
                                        .build(),
                                RC_SIGN_IN);

                    }
                }
            };

        }catch (Exception ex ){
            ex.printStackTrace();
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        try{
            mChatAdapter.startListening();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try{
            mChatAdapter.stopListening();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void createNewChat(){
        Intent intent = new Intent(mContext, CreateGroupChatActivity.class);
        startActivity(intent);
    }

    private void hideShowAddChatButton(FirebaseUser user) {
        addConversationButton = (ImageView) view.findViewById(R.id.add_conversation);
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
        mUsername = user.getDisplayName();
        mChatDatabaseReference = mFirebaseDatabase.getReference()
                .child(Constants.USERS_LOCATION
                        + "/" + EmailEncoding.commaEncodePeriod(user.getEmail()) + "/"
                        + Constants.CHAT_LOCATION );
        mUserDatabaseReference = mFirebaseDatabase.getReference()
                .child(Constants.USERS_LOCATION);

         hideShowAddChatButton(user);

        //Initialize screen variables
        mChatListView = (ListView) view.findViewById(R.id.chatListView);

        FirebaseListOptions<Chat> options = new FirebaseListOptions.Builder<Chat>()
                .setQuery(mChatDatabaseReference, Chat.class)
                .setLayout(R.layout.p3_chat_item)
                .build();

        mChatAdapter = new FirebaseListAdapter<Chat>(options) {
            @Override
            protected void populateView(final View view, final Chat chat, final int position) {
                //Log.e("TAG", "");
                //final Friend addFriend = new Friend(chat);
                ((TextView) view.findViewById(R.id.messageTextView)).setText(chat.getChatName());

                //Fetch last message from chat
                final DatabaseReference messageRef =
                        mFirebaseDatabase.getReference(Constants.MESSAGE_LOCATION
                                + "/" + chat.getUid());

                final TextView latestMessage = (TextView)view.findViewById(R.id.nameTextView);
                final CircleImageView senderPic = (CircleImageView)view.findViewById(R.id.photoImageView);

                messageRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                        Message newMsg = dataSnapshot.getValue(Message.class);
                        latestMessage.setText(EmailEncoding.commaDecodePeriod(newMsg.getSender()) + ": " + newMsg.getMessage());

                        mUserDatabaseReference.child(newMsg.getSender())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User msgSender = dataSnapshot.getValue(User.class);
                                        if(msgSender != null && msgSender.getProfilePicLocation() != null){
                                            // StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(msgSender.getProfilePicLocation());

                                            //String tesurl = ""
                                            try{
                                                Glide.with(view.getContext())
                                                        .setDefaultRequestOptions(new RequestOptions()
                                                                .circleCrop()
                                                                .centerCrop()
                                                                .fitCenter())
                                                        .load(chat.getProfilePicture())
                                                        .into(senderPic);

                                            }catch(Exception ex){
                                                ex.printStackTrace();
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                //Replace this with the most recent message from the chat

            }
        };

        mChatListView.setAdapter(mChatAdapter);
        try{
            mChatAdapter.startListening();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        //Add on click listener to line items
        mChatListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String messageLocation = mChatAdapter.getRef(position).toString();

                if(messageLocation != null){
                    Intent intent = new Intent(view.getContext(), ChatMessagesActivity.class);
                    String messageKey = mChatAdapter.getRef(position).getKey();
                    intent.putExtra(Constants.MESSAGE_ID, messageKey);
                    Chat chatItem = (Chat)mChatAdapter.getItem(position);
                    intent.putExtra(Constants.CHAT_NAME, chatItem.getChatName());
                    intent.putExtra(Constants.CHAT_UID, chatItem.getUid());
                    intent.putExtra(Constants.picture_chat_url, chatItem.getProfilePicture());

                    //intent.putExtra(Constants.CHAT_NAME, chatItem.getChatName());

                    startActivity(intent);
                }

                //Log.e("TAG", mChatAdapter.getRef(position).toString());
            }
        });

        mValueEventListener = mChatDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                //Check if any chats exists
                if (chat == null) {
                    //finish();
                    return;
                }
                mChatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

        } else if (resultCode == RESULT_OK) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sign_out) {
            AuthUI.getInstance()
                    .signOut(mContext);
        }
        if (id == R.id.listFriends) {
            //Open up activity where a user can add and view friends
            Intent intent = new Intent(mContext, FriendsListActivity.class);
            startActivity(intent);
        }


        return true;
    }

    private void createUser(final FirebaseUser user) {
        final DatabaseReference usersRef = mFirebaseDatabase.getReference(Constants.USERS_LOCATION);
        final String encodedEmail = EmailEncoding.commaEncodePeriod(user.getEmail());
        final DatabaseReference userRef = usersRef.child(encodedEmail);

        // get Information  user  ...
        userDB_current = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        // Set the  Driver Response to true ...
        //HashMap map = new HashMap();
        //map.put("Authentified" , "await");
        //userDB.updateChildren(map);
        userDB_current.keepSynced(true);
        userDB_current.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    try{
                        Map<String, Object> user_data = (Map<String, Object>) dataSnapshot.getValue();

                        try {


                            final String username = user_data.get("name").toString();
                            final String UserId = user.getUid();
                            final String token_id = FirebaseInstanceId.getInstance().getToken();
                            final String profile_pic = user_data.get("image").toString();

                            // update profile

                            //HashMap map = new HashMap();
                            //User newUser = new User(UserId, username, encodedEmail, token_id, profile_pic);
                            //userRef.updateChildren(newUser);

                            //if all exist than update the user profile  ...
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // if (dataSnapshot.getValue() == null) {
                                        User newUser = new User(UserId, username, encodedEmail, token_id, profile_pic);
                                        Map user_map = new HashMap();

                                         Map<String, Object> updates = new HashMap<String,Object>();
                                         updates.put("email", encodedEmail);
                                         updates.put("token_id", token_id);
                                         updates.put("userId", UserId);
                                         updates.put("username", username);
                                         updates.put("profilePicLocation", profile_pic);
                                         userRef.updateChildren(updates);

                                        //user_map.put(encodedEmail+"//" , newUser);
                                        //userRef.updateChildren(user_map);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }catch(Exception ex){

                            ex.printStackTrace();
                        }

                    }catch(Exception ex){
                        ex.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toasty.error(getContext(),databaseError.toString(), Toast.LENGTH_LONG).show();

            }
        });




    }

}
