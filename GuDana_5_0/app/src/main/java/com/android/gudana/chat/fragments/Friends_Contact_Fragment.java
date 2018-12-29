package com.android.gudana.chat.fragments;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.gudana.R;
import com.android.gudana.chat.activities.ChatActivity;
import com.android.gudana.chat.adapters.RoomAdapter;
import com.android.gudana.chat.layouts.StatusLayout;
import com.android.gudana.chat.recyclerviewsearch.ContactsAdapter;
import com.android.gudana.chat.recyclerviewsearch.MyDividerItemDecoration;
import com.android.gudana.hify.models.ViewFriends;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

//import com.android.gudana.hify.ui.activities.friends.MessagesView;

public class Friends_Contact_Fragment extends Fragment implements ContactsAdapter.ContactsAdapterListener {

    public ListView listRooms;
    public SwipeRefreshLayout swipeContainer;
    public static String username, session;
    public static int user_id;

    public StatusLayout statusLayout;

    RoomAdapter adapter;

    // added
    private static final String TAG = Friends_Contact_Fragment.class.getSimpleName();
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
        View view = inflater.inflate(R.layout.friends_contacts_activity_main, container, false);

        setHasOptionsMenu(true);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        // Init firestore to fetch Users   ...


        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        // toolbar fancy stuff
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.toolbar_title);



        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        recyclerView = view.findViewById(R.id.recycler_view);
        contactList = new ArrayList<>();
        mAdapter = new ContactsAdapter(getActivity(), contactList, Friends_Contact_Fragment.this);

        // white background notification bar
        //whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);

        new_contact = (LinearLayout) view.findViewById(R.id.new_contact);
        new_room = (LinearLayout) view.findViewById(R.id.new_room);
        new_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // disaable create groupe  and new contact  layout
                new_room.setVisibility(View.GONE);
                new_contact.setVisibility(View.GONE);
                // showCreateRoomDialog();

            }
        });

        new_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toasty.info(getActivity(), "not implemeted yet ... ", Toast.LENGTH_SHORT).show();
            }
        });


        //fetchAllContact();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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

                                    // int money = snapshot.getLong("money").intValue();
                                    users_firestore.setRoom_id(room_id);
                                    users_firestore.setRoom_uid(room_uid);
                                    //usersList.add(users);
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

        // get local contact  ...
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                String phoneNo = "------";

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //Log.i(TAG, "Name: " + name);
                        //Log.i(TAG, "Phone Number: " + phoneNo);
                    }

                    pCur.close();
                }

                // (String id, String username, String name, String image, String email, String token_id , String Phone)
                final ViewFriends Contact = new ViewFriends("--id--",
                        "--usn---",
                        name,
                        "https://cdn2.iconfinder.com/data/icons/perfect-flat-icons-2/512/User_man_male_profile_account_person_people.png"
                        ,"----email-----"
                        ,"----token_id"
                        , phoneNo.toString()
                        ,"romm uid"
                        ,"room_id"
                        , -1);

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {


                        mAdapter.notifyDataSetChanged();
                        contactList.add(Contact);
                        // Do stuff…
                    }
                });

            }
        }
        if(cur!=null){
            cur.close();
        }

        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.chat_menu_main, menu);

        getActivity().getMenuInflater().inflate(R.menu.chat_menu_main, menu);

        // Associate chat_searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        //return true;


      super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onContactSelected(ViewFriends contact) {

        if(contact.getRoom_id()!= -1){

            Intent intent = new Intent(Friends_Contact_Fragment.this.getContext(), ChatActivity.class);
            intent.putExtra("room_id", contact.getRoom_id());
            intent.putExtra("room_name", contact.getName());
            intent.putExtra("type", ChatActivity.ROOM);
            startActivity(intent);
        }else{

            Toasty.info(Friends_Contact_Fragment.this.getContext(), "Invite this Contact ? ", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchAllContact();
    }
}