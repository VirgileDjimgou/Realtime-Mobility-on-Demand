package com.android.gudana.chat.fragments;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.android.gudana.R;
import com.android.gudana.chat.activities.ChatActivity;
import com.android.gudana.chat.adapters.RoomAdapter;
import com.android.gudana.chat.layouts.StatusLayout;



import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;


import com.android.gudana.chat.recyclerviewsearch.ContactsAdapter;
import com.android.gudana.chat.recyclerviewsearch.MyDividerItemDecoration;
import com.android.gudana.hify.models.ViewFriends;
//import com.android.gudana.hify.ui.activities.friends.MessagesView;
import com.android.gudana.hify.ui.activities.MainActivity_GuDDana;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static com.android.gudana.hify.ui.activities.MainActivity_GuDDana.bottomNavigation;
import static com.android.gudana.hify.ui.activities.MainActivity_GuDDana.unreadChat;

public class ChatFragment extends Fragment implements ContactsAdapter.ContactsAdapterListener {

    public ListView listRooms;
    public SwipeRefreshLayout swipeContainer;
    public static String username, session;
    public static int user_id;

    public StatusLayout statusLayout;

    RoomAdapter adapter;

    // added
    private static final String TAG = ChatFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<ViewFriends> contactList;
    private ContactsAdapter mAdapter;
    private SearchView searchView;
    LinearLayout  new_contact;
    LinearLayout new_room;
    public static  String member_user_id =  "fakemenber_uid_hkhjjhk_jhgjhgj6546546fhfg676546utuh65764";
    public  static  int global_number_unread_message = 0;
    private Timer myTimer;
    private View view ;


    View mView;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    // url to fetch contacts json


    @Override
    public void setArguments(Bundle arguments) {
        this.user_id = arguments.getInt("user_id");
        this.username = arguments.getString("username");
        this.session = arguments.getString("session");
    }

    public RoomAdapter getAdapter() {
        return adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {

            view = inflater.inflate(R.layout.chat_activity_main, container, false);

            CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                    .setDefaultFontPath("fonts/regular.ttf")
                    .setFontAttrId(R.attr.fontPath)
                    .build()
            );

            // Init firestore to fetch Users   ...

            firestore = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();


            recyclerView = view.findViewById(R.id.recycler_view);
            contactList = new ArrayList<>();
            mAdapter = new ContactsAdapter(getActivity(), contactList, this);

            // white background notification bar
            whiteNotificationBar(recyclerView);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new MyDividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL, 36));
            recyclerView.setAdapter(mAdapter);

            Button menu_chat = view.findViewById(R.id.menu_chat);
            menu_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Increment_unread_message("0000", "456", "tets message");
                    Intent ContactListActivity = new Intent(getActivity(), com.android.gudana.chat.recyclerviewsearch.ContactListActivity.class);
                    startActivity(ContactListActivity);
                }
            });


            // get your contact on firestore ...
            fetchAllContact();

        }else {
            // do nothing with view  ...
           // return  view;
        }

        //fetchAllContact();
        return view;
    }

    public void TimerMethod(Map<String, String> data)
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        getActivity().runOnUiThread(update_Chat_read_message);
    }


    public Runnable update_Chat_read_message = new Runnable() {
        public void run() {
            //Increment_unread_message("0000", "456", "tets message");
            //This method runs in the same thread as the UI.

            //Do something to the UI thread here

        }
    };


    public  void Update_ui_Chat(final Map<String, String> data){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {

                String FCM_ICON_SENDER = "picture";
                String FCM_name_Sender = "SenderName";
                String FCM_message_sender = "body";
                String fcm_msg = "msg";
                String CHANNEL_NAME = "FCM";
                String CHANNEL_DESC = "Xshaka Cloud Messaging";
                String senderName = "";
                String  MessageSended = "";
                String msg = "";
                String TimeSend = "";
                String senderID = "";

                String picture_url = data.get(FCM_ICON_SENDER);
                senderName = data.get(FCM_name_Sender);
                MessageSended = data.get(FCM_message_sender);
                msg = data.get(fcm_msg);
                senderID = data.get("SenderID");
                TimeSend = data.get("TimeSend");
                String Room_id_received = data.get("Room_id");

                //jData.put("Room_id", Room_Call_ID);

                for (ViewFriends row : contactList) {
                    if(Integer.toString(row.getRoom_id()).equalsIgnoreCase(Room_id_received) ){
                        final String[] type_of_message = MessageSended.split(ChatActivity.splitter_pattern_message);
                        if(type_of_message !=null && type_of_message[0] != null && type_of_message.length >1 ) {

                            if (type_of_message[0].equalsIgnoreCase(ChatActivity.Type_Text)) {
                                row.setLast_message(type_of_message[1]);
                            }else if(type_of_message[0].equalsIgnoreCase(ChatActivity.Type_image)){
                                row.setLast_message(ChatActivity.Type_image);
                            }
                            else if(type_of_message[0].equalsIgnoreCase(ChatActivity.Type_Doc)){
                                row.setLast_message(ChatActivity.Type_Doc);
                            }
                            else if(type_of_message[0].equalsIgnoreCase(ChatActivity.Type_map)){
                                row.setLast_message(ChatActivity.Type_map);
                            }
                            else if(type_of_message[0].equalsIgnoreCase(ChatActivity.Type_voice)){
                                row.setLast_message(ChatActivity.Type_voice);
                            }
                            else if(type_of_message[0].equalsIgnoreCase(ChatActivity.Type_live_location)){
                                row.setLast_message(ChatActivity.Type_live_location);
                            }
                        }
                        row.setTime_lastmessage(TimeSend);
                        row.setNumber_of_unread_message(row.getNumber_of_unread_message() + 1);
                        mAdapter.notifyDataSetChanged();
                    }

                    //row.getRoom_id();

                }

            }
        });



        // increment  global notification  ...
        unreadChat =unreadChat +1;
        final AHNotification notification = new AHNotification.Builder()
                .setText(Integer.toString(unreadChat))
                .setBackgroundColor(Color.BLUE)
                .setTextColor(Color.WHITE)
                .build();
        // Adding notification to Chat item.
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Stuff that updates the UI
                bottomNavigation.setNotification(notification, 1);


            }
        });

        if(unreadChat >0){
            MainActivity_GuDDana.notificationVisible = true;

        }else {
            MainActivity_GuDDana.notificationVisible = false;
        }

    }



    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getActivity().getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    private void fetchAllContact(){
        //getContact();
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                getContactList();
                // Do stuff…
            }
        });
    }
    private void askPermission(){

        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS,


                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.CALL_PHONE


                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.isAnyPermissionPermanentlyDenied()){
                            Toast.makeText(getActivity(), "You have denied some permissions permanently, if the app force close try granting permission from settings.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();

    }

    private void getContactList() {
        ContentResolver cr = getActivity().getContentResolver();
        try{

            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);


        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
               askPermission();
        }
        contactList.clear();

        // get firestore contact  ...
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
                                    final ViewFriends users_firestore = doc.getDocument().toObject(ViewFriends.class);
                                    String friend_name = doc.getDocument().getString("name");
                                    String room_uid = doc.getDocument().getString("room_uid");
                                    int room_id = (int) doc.getDocument().getLong("room_id").intValue();
                                    String id = doc.getDocument().getString("id");

                                    // int money = snapshot.getLong("money").intValue();
                                    users_firestore.setRoom_id(room_id);
                                    users_firestore.setRoom_uid(room_uid);
                                    users_firestore.setId(id);
                                    users_firestore.setLast_message("blalala");
                                    //usersList.add(users);
                                    // usersAdapter.notifyDataSetChanged();
                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {

                                            contactList.add(users_firestore);
                                            mAdapter.notifyDataSetChanged();                                            // Do stuff…
                                        }
                                    });
                                }
                            }
                        }else{
                            Toasty.info(getActivity(), "No friends found.", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.w("Error", "listen:error", e);

                    }
                });


        mAdapter.notifyDataSetChanged();
        // start timer

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //TimerMethod();
                // get all  the unread message  ...
            }

        }, 1000, 1000);
    }


    // increment unread message ...
    public void  Increment_unread_message(String room_id , String room_uid , String last_message){

        //final ViewFriends users_firestore = contactList.get(1);
        //users_firestore.setLast_message(last_message);
        contactList.get(1).setLast_message(last_message);
        contactList.get(1).setNumber_of_unread_message(contactList.get(1).getNumber_of_unread_message() + global_number_unread_message );
        mAdapter.notifyDataSetChanged();
        //mAdapter.notifyDataSetChanged();

        //Toast.makeText(ChatFragment.this.getContext(), "get last message "+users_firestore.getLast_message(), Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onContactSelected(ViewFriends contact) {

        if(contact.getRoom_id()!= -1){

            unreadChat =unreadChat - contact.getNumber_of_unread_message();
            final AHNotification notification = new AHNotification.Builder()
                    .setText(Integer.toString(unreadChat))
                    .setBackgroundColor(Color.BLUE)
                    .setTextColor(Color.WHITE)
                    .build();
            // Adding notification to Chat item.
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    // Stuff that updates the UI
                    bottomNavigation.setNotification(notification, 1);


                }
            });

            if(unreadChat >0){
                MainActivity_GuDDana.notificationVisible = true;

            }else {
                MainActivity_GuDDana.notificationVisible = false;
            }


            // reset  unreda message this Item
            contact.setNumber_of_unread_message(0);
            // get number of notification and decrement on global notification ...
            mAdapter.notifyDataSetChanged();

            Intent intent = new Intent(ChatFragment.this.getContext(), ChatActivity.class);
            intent.putExtra("room_id", contact.getRoom_id());
            intent.putExtra("room_name", contact.getName());
            intent.putExtra("type", ChatActivity.ROOM);
            intent.putExtra("room_uid", contact.getRoom_uid());
            intent.putExtra("token_id", contact.getToken_id());
            intent.putExtra("image_url", contact.getImage());
            intent.putExtra("userid", contact.getId());

            startActivity(intent);
        }else{

            Toasty.info(ChatFragment.this.getContext(), "Invite this Contact ? ", Toast.LENGTH_LONG).show();
        }
    }

}