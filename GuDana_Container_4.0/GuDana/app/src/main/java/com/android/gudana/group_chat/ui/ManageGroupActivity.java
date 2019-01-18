package com.android.gudana.group_chat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.R;
import com.android.gudana.group_chat.model.Chat;
import com.android.gudana.group_chat.model.Friend;
import com.android.gudana.group_chat.model.Message;
import com.android.gudana.group_chat.model.User;
import com.android.gudana.group_chat.utils.Constants;
import com.android.gudana.group_chat.utils.EmailEncoding;
import com.android.gudana.hify.adapters.viewFriends.ViewFriendAdapter;
import com.android.gudana.hify.models.ViewFriends;
import com.android.gudana.hify.utils.AnimationUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tylersuehr.esr.EmptyStateRecyclerView;
import com.yalantis.ucrop.UCrop;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class ManageGroupActivity extends AppCompatActivity {


    private String TAG = "New Conversation";

    private ListView mListView;

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
    private Button mCreateButton;
    private List<ViewFriends> usersList;
    private ViewFriendAdapter usersAdapter;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private EmptyStateRecyclerView mRecyclerView;
    private Button creat_group;
    private EditText GroupName;
    private CircleImageView profile_image;
    private List<String> usersList_group = new ArrayList<>();
    ;
    public ProgressDialog mDialog;
    public static String pushId_group_room = "";
    public StorageReference storageReference;
    private static final int PICK_IMAGE = 100;
    List<Friend> Friendlist ;


    public Uri imageUri = null;
    private String chatName;
    private String Chat_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_group);
        mDialog = new ProgressDialog(this);


        initializeScreen();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("Manage Group");

        mCreateButton = (Button) findViewById(R.id.createButton);
        mFriendsInChat = (TextView) findViewById(R.id.friendsInChat);
        mChatName = (EditText) findViewById(R.id.chat_name);
        mChat = new Chat("", "", "");


        Intent intent = this.getIntent();
        //MessageID is the location of the messages for this specific chat
        chatName = intent.getStringExtra(Constants.CHAT_NAME);
        Chat_uid = intent.getStringExtra(Constants.CHAT_UID);
        actionBar.setTitle(chatName);

        if(Chat_uid == null){
            finish(); // replace this.. nav user back to home
            return;
        }
        GetInformation_from_Users(Chat_uid);

        mChatName.setText(chatName);


        profile_image = findViewById(R.id.profile_image);
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProfilepic();
            }
        });
        creat_group = (Button) findViewById(R.id.createButton);
        creat_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Friend> Friendlist = mChat.getFriends();
                // chech  if the User  haat enter a group name   and the  if the are selected  more than one users ...
                if (Friendlist.size() > 0 && GroupName.getText().length() > 0 && imageUri != null) {

                    // Toasty.info(FriendlistScrollingActivity.this, "your new group is being registered", Toast.LENGTH_SHORT).show();
                    //createChat();
                    uploadTeam_();

                    creat_group.setEnabled(false);

                    // Start the registration process ...

                } else {

                    if (imageUri == null) {
                        AnimationUtil.shakeView(profile_image, ManageGroupActivity.this);
                        //Toast.makeText(RegisterActivity.this, "We recommend you to set a profile picture", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();

                        new LovelyInfoDialog(ManageGroupActivity.this)
                                .setTopColorRes(R.color.error)
                                .setIcon(R.mipmap.ic_infos)
                                .setTitle("Infos ")
                                .setMessage("We recommend you to set a profile picture")
                                .show();
                    } else {

                        new LovelyInfoDialog(ManageGroupActivity.this)
                                .setTopColorRes(R.color.error)
                                .setIcon(R.mipmap.ic_infos)
                                .setTitle("Infos ")
                                .setMessage("please  select contacts from the list mentioned or  enter a valid group name")
                                .show();
                    }
                    //
                }
            }
        });

        GroupName = (EditText) findViewById(R.id.chat_name);
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        // mRecyclerView =  findViewById(R.id.recyclerView);


        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setIndeterminate(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);

        // to create a new chat   ...
        // showFriendsList();
        // addListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:

                //MainActivity_with_Drawer.tabLayout.getTabAt(3);
                //MainActivity_with_Drawer.mViewPager.setCurrentItem(3);
                //play_sound();
                ManageGroupActivity.this.finish();
                // NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void setProfilepic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                imageUri = data.getData();
                // start crop activity
                UCrop.Options options = new UCrop.Options();
                options.setCompressionFormat(Bitmap.CompressFormat.PNG);
                options.setCompressionQuality(100);
                options.setShowCropGrid(true);

                UCrop.of(imageUri, Uri.fromFile(new File(getCacheDir(), "hify_user_profile_picture.png")))
                        .withAspectRatio(1, 1)
                        .withOptions(options)
                        .start(this);

            }
        }
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                imageUri = UCrop.getOutput(data);
                profile_image.setImageURI(imageUri);
            } else if (resultCode == UCrop.RESULT_ERROR) {
                Log.e("Error", "Crop error:" + UCrop.getError(data).getMessage());
            }
        }


    }


    @Override
    protected void onStart() {
        super.onStart();
        try {
            mFriendListAdapter.startListening();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            mFriendListAdapter.stopListening();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addListeners() {
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

    private void showFriendsList() {

        // we should intialise the  database  we  node  Friends ...
        // mFriendsLocationDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");


        final DatabaseReference chatRef = mFirebaseDatabase.getReference(Constants.CHAT_LOCATION).child(Chat_uid).child("friends");

        FirebaseListOptions<String> options = new FirebaseListOptions.Builder<String>()
                .setQuery(chatRef, String.class)
                .setLayout(R.layout.p3_friend_item_manage)
                .build();

        //TODO: This list should not show your own userid..
        mFriendListAdapter = new FirebaseListAdapter<String>(options) {
            @Override
            protected void populateView(final View view, final String friend_email, final int position) {
                Log.e("TAG", friend_email);
                final Friend addFriend = new Friend(friend_email);
                try {

                    ((TextView) view.findViewById(R.id.nameTextView)).setText(EmailEncoding.commaDecodePeriod(friend_email));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


                mUserDatabaseRef.child(friend_email).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User fUser = dataSnapshot.getValue(User.class);
                        if (fUser != null) {
                            try {

                                Log.d("text", "test");

                                ((TextView) view.findViewById(R.id.messageTextView))
                                        .setText(EmailEncoding.commaDecodePeriod(fUser.getUsername()));

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            if (fUser.getProfilePicLocation() != null && fUser.getProfilePicLocation().length() > 0) {
                                try {
                                    StorageReference storageRef = FirebaseStorage.getInstance()
                                            .getReference().child(fUser.getProfilePicLocation());
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
                                            .into((CircleImageView) view.findViewById(R.id.photoImageView));


                                } catch (Exception e) {
                                    Log.e("Err", e.toString());
                                }
                            }
                        } else {
                            ((TextView) view.findViewById(R.id.messageTextView))
                                    .setText("A girl has no name");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //Hide remove button by default, we have to do this because we reuse the view
                if (mChat.getFriends().isEmpty()) {
                    view.findViewById(R.id.removeFriend).setVisibility(View.GONE);
                }
                //view.findViewById(R.id.removeFriend).setVisibility(View.GONE);
                (view.findViewById(R.id.addFriend)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(TAG, "Clicking row: " + position);
                        Log.e(TAG, "Clicking user: " + friend_email);

                        //TODO: Complete the creating of Chat object, then add to firebase
                        //Add friend to chat
                        if (mChat.appendFriend(addFriend)) {
                            String friendsString = "";
                            for (Friend f : mChat.getFriends()) {
                                friendsString += EmailEncoding.commaDecodePeriod(f.getEmail()) + ", ";
                            }
                            friendsString = friendsString.substring(0, friendsString.length() - 2);
                            mFriendsInChat.setText("Users added to chat: " + friendsString);
                        }
                        view.findViewById(R.id.removeFriend).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.addFriend).setVisibility(View.GONE);
                        Log.e(TAG, "Adding to chat: " + friend_email);
                    }
                });
                (view.findViewById(R.id.removeFriend)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(TAG, "Clicking row: " + position);
                        Log.e(TAG, "Clicking user: " + friend_email);
                        //TODO: Add remove methods
                        mChat.removeFriend(addFriend); //the name add Friend here is not appropriate
                        String friendsString = "";
                        for (Friend f : mChat.getFriends()) {
                            friendsString += f.getEmail() + ", ";
                        }
                        if (friendsString.length() > 1) {
                            friendsString = friendsString.substring(0, friendsString.length() - 2);

                            mFriendsInChat.setText("Users added to chat: " + EmailEncoding.commaDecodePeriod(friendsString));
                        } else {
                            mFriendsInChat.setText("Users added to chat: ");
                        }
                        view.findViewById(R.id.addFriend).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.removeFriend).setVisibility(View.GONE);
                        Log.e(TAG, "Removing from chat: " + friend_email);
                    }
                });
            }
        };
        mListView.setAdapter(mFriendListAdapter);

        mValueEventListener = mFriendsLocationDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
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

    public void uploadTeam_() {

        StorageReference mStorage = FirebaseStorage.getInstance().getReference(); //make global
        final String imageLocation = "Photos" + "/" + "TeamProfilPicture";
        final String uniqueId = UUID.randomUUID().toString();
        final StorageReference filepath = mStorage.child(imageLocation).child(uniqueId + "/image_message");
        UploadTask uploadTask = filepath.putFile(imageUri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return filepath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    createChat(downloadUri.toString());

                } else {
                    Toasty.error(ManageGroupActivity.this, "Error communication with the Backend", Toast.LENGTH_SHORT).show();
                    // Handle failures
                    // ...
                }
            }
        });
    }

    //TODO: Add create new Chat function
    public void createChat(String Profil_team) {
        //final String userLoggedIn = mFirebaseAuth.getCurrentUser().getEmail();
        //Log.e(TAG, "User logged in is: " + userLoggedIn);
        // final String newFriendEncodedEmail = EmailEncoding.commaEncodePeriod(newFriendEmail);
        final DatabaseReference chatRef = mFirebaseDatabase.getReference(Constants.CHAT_LOCATION);
        final DatabaseReference messageRef = mFirebaseDatabase.getReference(Constants.MESSAGE_LOCATION);
        final DatabaseReference pushRef = chatRef.push();
        final String pushKey = pushRef.getKey();
        mChat.setUid(pushKey);
        mChat.setChatName(mChatName.getText().toString());
        mChat.setProfilePicture(Profil_team);
        Log.e(TAG, "Push key is: " + pushKey);

        //Create HashMap for Pushing Conv
        HashMap<String, Object> chatItemMap = new HashMap<String, Object>();
        HashMap<String, Object> chatObj = (HashMap<String, Object>) new ObjectMapper()
                .convertValue(mChat, Map.class);
        chatItemMap.put("/" + pushKey, chatObj);
        chatRef.updateChildren(chatItemMap);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String timestamp = dateFormat.format(date);
        //Create corresponding message location for this chat
        String initialMessage = mFriendsInChat.getText().toString();

        Message initialMessages = new Message("System", initialMessage, timestamp);
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
        chatItemMap.put("/chats_groups/" + pushKey, chatObj); //repushes chat obj -- Not space efficient
        mCurrentUserDatabaseReference.updateChildren(chatItemMap); //Adds Chatkey to users chats

        //Push chat to all friends
        for (Friend f : mChat.getFriends()) {
            mFriendDatabaseReference = mFirebaseDatabase.getReference().child(Constants.USERS_LOCATION
                    + "/" + EmailEncoding.commaEncodePeriod(f.getEmail()));
            chatItemMap = new HashMap<String, Object>();
            chatItemMap.put("/chats_groups/" + pushKey, chatObj);
            mFriendDatabaseReference.updateChildren(chatItemMap);
            mFriendDatabaseReference = null;
        }

        Intent intent = new Intent(this, ChatMessagesActivity.class);
        String messageKey = pushKey;
        intent.putExtra(Constants.MESSAGE_ID, messageKey);
        intent.putExtra(Constants.CHAT_NAME, mChat.getChatName());
        startActivity(intent);
        creat_group.setEnabled(true);

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
        //mToolBar = (Toolbar) findViewById(R.id.toolbar);

        mListView = (ListView) findViewById(R.id.conversationListView);

    }

    private void GetInformation_from_Users(String ChatId){

        final DatabaseReference chatRef = mFirebaseDatabase.getReference(Constants.CHAT_LOCATION).child(ChatId);
        // Set the  Driver Response to true ...
        //HashMap map = new HashMap();
        //map.put("Authentified" , "await");
        //userDB.updateChildren(map);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public Object FriendListe;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    try{
                        Map<String, Object> user_data = (Map<String, Object>) dataSnapshot.getValue();

                        // test if the recors Phone already exist  ...if not than
                        // than you are a new user   ...
                        if (user_data.get("profilePicture") != null) {

                            final String url_profil =  user_data.get("profilePicture").toString();

                            Glide.with(ManageGroupActivity.this)
                                    .setDefaultRequestOptions(new RequestOptions()
                                            .circleCrop()
                                            .centerCrop()
                                            .fitCenter())
                                    .load(url_profil)
                                    .into(profile_image);
                        }


                        if (user_data.get("friends") != null) {


                            FriendListe = user_data.get("friends");
                            Log.d("friend infos", "Friendliste");
                        }


                    }catch(Exception ex){
                        Toasty.error(getApplicationContext(), ex.toString() , Toast.LENGTH_LONG).show();
                        ex.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toasty.error(getApplicationContext(),databaseError.toString(), Toast.LENGTH_LONG).show();

            }
        });
    }

}
