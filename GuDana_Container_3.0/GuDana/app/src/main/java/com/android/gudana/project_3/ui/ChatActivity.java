package com.android.gudana.project_3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.HashMap;
import java.util.Map;

import com.android.gudana.R;
import com.android.gudana.project_3.model.Chat;
import com.android.gudana.project_3.model.Friend;
import com.android.gudana.project_3.model.Message;
import com.android.gudana.project_3.model.User;
import com.android.gudana.project_3.utils.Constants;
import com.android.gudana.project_3.utils.EmailEncoding;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/*
    This view will show a list of the users friends,
    the user can select the friends they want to start
    a new conversation with.
 */
public class ChatActivity extends AppCompatActivity {
    private String TAG = "New Conversation";

    private ListView mListView;
    private Toolbar mToolBar;

    private FirebaseListAdapter mFriendListAdapter;
    private ValueEventListener mValueEventListener;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mFriendsLocationDatabaseReference;
    private DatabaseReference mCurrentUserDatabaseReference;
    private DatabaseReference mFriendDatabaseReference;
    private TextView mFriendsInChat;
    private EditText mChatName;

    //Objects for Chat
    private Chat mChat;
    private DatabaseReference mUserDatabaseRef;
    private ImageButton mCreateButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p3_add_chat);
        initializeScreen();
        // showFriendsList();
        addListeners();
    }

    private void addListeners(){
        mChatName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mCreateButton.setEnabled(true);
                } else {
                    mCreateButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    //TODO: Add create new Chat function
    public void createChat(View view){
        //final String userLoggedIn = mFirebaseAuth.getCurrentUser().getEmail();
        //Log.e(TAG, "User logged in is: " + userLoggedIn);
        // final String newFriendEncodedEmail = EmailEncoding.commaEncodePeriod(newFriendEmail);
        final DatabaseReference chatRef = mFirebaseDatabase.getReference(Constants.CHAT_LOCATION);
        final DatabaseReference messageRef = mFirebaseDatabase.getReference(Constants.MESSAGE_LOCATION);
        final DatabaseReference pushRef = chatRef.push();
        final String pushKey = pushRef.getKey();
        mChat.setUid(pushKey);
        mChat.setChatName(mChatName.getText().toString());
        Log.e(TAG, "Push key is: " + pushKey);

        //Create HashMap for Pushing Conv
        HashMap<String, Object> chatItemMap = new HashMap<String, Object>();
        HashMap<String,Object> chatObj = (HashMap<String, Object>) new ObjectMapper()
                .convertValue(mChat, Map.class);
        chatItemMap.put("/" + pushKey, chatObj);
        chatRef.updateChildren(chatItemMap);

        //Create corresponding message location for this chat
        String initialMessage = mFriendsInChat.getText().toString();
        Message initialMessages =
                new Message("System", initialMessage, "");
        final DatabaseReference initMsgRef =
                mFirebaseDatabase.getReference(Constants.MESSAGE_LOCATION + "/" + pushKey);
        final DatabaseReference msgPush = initMsgRef.push();
        final String msgPushKey = msgPush.getKey();
        initMsgRef.child(msgPushKey).setValue(initialMessages);

        //Must add chat reference under every user object. Chat/User/Chats[chat1, chat2 ..]
        //Add to current users chat object
        //TODO: OPTIMIZATION!! decide how we will solve data replication issue, we could just send chat id
        // but this would require more complex queries on other pages
        chatItemMap = new HashMap<String, Object>();
        chatItemMap.put("/chats/" + pushKey, chatObj); //repushes chat obj -- Not space efficient
        mCurrentUserDatabaseReference.updateChildren(chatItemMap); //Adds Chatkey to users chats

        //Push chat to all friends
        for(Friend f: mChat.getFriends()){
            mFriendDatabaseReference = mFirebaseDatabase.getReference().child(Constants.USERS_LOCATION
                    + "/" + EmailEncoding.commaEncodePeriod(f.getEmail()));
            chatItemMap = new HashMap<String, Object>();
            chatItemMap.put("/chats/" + pushKey, chatObj);
            mFriendDatabaseReference.updateChildren(chatItemMap);
            mFriendDatabaseReference = null;
        }

        Intent intent = new Intent(view.getContext(), ChatMessagesActivity.class);
        String messageKey = pushKey;
        intent.putExtra(Constants.MESSAGE_ID, messageKey);
        intent.putExtra(Constants.CHAT_NAME, mChat.getChatName());
        startActivity(intent);
    }

    private void initializeScreen() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUserDatabaseRef = mFirebaseDatabase.getReference().child(Constants.USERS_LOCATION);
        mCurrentUserDatabaseReference = mFirebaseDatabase.getReference().child(Constants.USERS_LOCATION
                + "/" + EmailEncoding.commaEncodePeriod(mFirebaseAuth.getCurrentUser().getEmail()));
        //Eventually this list will filter out users that are already your friend
        mFriendsLocationDatabaseReference = mFirebaseDatabase.getReference().child(Constants.FRIENDS_LOCATION
            + "/" + EmailEncoding.commaEncodePeriod(mFirebaseAuth.getCurrentUser().getEmail()));



        mListView = (ListView) findViewById(R.id.conversationListView);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setTitle("Create new chat");

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mCreateButton = (ImageButton) findViewById(R.id.createButton);
        mFriendsInChat = (TextView) findViewById(R.id.friendsInChat);
        mChatName = (EditText) findViewById(R.id.chat_name);
        mChat = new Chat("","");
    }
}