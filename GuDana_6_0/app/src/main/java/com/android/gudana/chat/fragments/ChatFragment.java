package com.android.gudana.chat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;


import com.android.gudana.chat.recyclerviewsearch.ContactListActivity;
import com.android.gudana.chat.recyclerviewsearch.ContactsAdapter;
import com.android.gudana.chat.recyclerviewsearch.MyDividerItemDecoration;
import com.android.gudana.hify.models.ViewFriends;
//import com.android.gudana.hify.ui.activities.friends.MessagesView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

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
        View view = inflater.inflate(R.layout.chat_activity_main, container, false);

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
                Intent ContactListActivity = new Intent(getActivity(), com.android.gudana.chat.recyclerviewsearch.ContactListActivity.class);
                startActivity(ContactListActivity);
            }
        });

        //fetchAllContact();
        return view;
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

    private void getContactList() {
        ContentResolver cr = getActivity().getContentResolver();
         Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

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
    }


    @Override
    public void onContactSelected(ViewFriends contact) {

        if(contact.getRoom_id()!= -1){

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchAllContact();
    }
}