package com.android.gudana.chat.recyclerviewsearch;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.android.gudana.R;
import com.android.gudana.chat.activities.ChatActivity;
import com.android.gudana.chat.fragments.RoomsFragment;
import com.android.gudana.chat.network.CreateRoomAsyncTask;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;


import com.android.gudana.hify.models.ViewFriends;
//import com.android.gudana.hify.ui.activities.friends.MessagesView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ContactListActivity extends AppCompatActivity implements ContactsAdapter.ContactsAdapterListener {
    private static final String TAG = ContactListActivity.class.getSimpleName();
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

    public static void startActivity(Context context){
        Intent intent = new Intent(context,ContactListActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list_activity_main);

        /*
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        */

        // Init firestore to fetch Users   ...

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title);

        recyclerView = findViewById(R.id.recycler_view);
        contactList = new ArrayList<>();
        mAdapter = new ContactsAdapter(this, contactList, this);

        // white background notification bar
        whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);

        /*
        new_contact = (LinearLayout) findViewById(R.id.new_contact);
        new_room = (LinearLayout) findViewById(R.id.new_room);
        new_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // disaable create groupe  and new contact  layout
                new_room.setVisibility(View.GONE);
                new_contact.setVisibility(View.GONE);
                showCreateRoomDialog();

            }
        });

        new_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toasty.info(ContactListActivity.this, "not implemeted yet ... ", Toast.LENGTH_SHORT).show();
            }
        });
        */

        runOnUiThread(new Runnable() {
            public void run() {
                getContactList();
                // Do stuff…
            }
        });
    }

    public void showCreateRoomDialog() {
        AlertDialog.Builder createRoomDialog = new AlertDialog.Builder(ContactListActivity.this);
        createRoomDialog.setTitle("Create room");

        final EditText input = new EditText(ContactListActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        input.setLayoutParams(lp);
        input.setHint("Must be between 1-20 characters");

        createRoomDialog.setView(input);

        createRoomDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String room = input.getText().toString().trim();
                String fake_url_pic =  "https://image.flaticon.com/sprites/new_packs/145841-avatar-set.png";
                if(!room.isEmpty() && room.length() <= 20) {
                    // (new CreateRoomAsyncTask(ContactListActivity.this, RoomsFragment.user_id, RoomsFragment.username, RoomsFragment.session, room , fake_url_pic)).execute();
                    //     private String username, session, room_name , url_profile_pic , type_id , room_uid , initiator_user_id , member_user_id;
                    /*
                 public CreateRoomAsyncTask(Activity activity, String username,
                               String session, String room_name,
                               String url_profile_pic, String type_id,
                               String room_uid, String initiator_user_id,
                               String member_user_id, int user_id)
                     */

                    // generate key firebase  uid for room
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    String key = database.getReference("room_uid_key").push().getKey();

                    String username = RoomsFragment.username ;
                    String session = RoomsFragment.session ;
                    String url_profile_pic = fake_url_pic;
                    String type_id = "simple";
                    String room_uid = key;
                    String initiator_user_id = FirebaseAuth.getInstance().getUid();

                    int User_id = RoomsFragment.user_id;
                    (new CreateRoomAsyncTask(ContactListActivity.this, username ,
                            session, room ,
                            url_profile_pic , type_id ,
                            room_uid ,  initiator_user_id ,
                            member_user_id , User_id)).execute();


                    new_room.setVisibility(View.VISIBLE);
                    new_contact.setVisibility(View.VISIBLE);
                    ContactListActivity.this.finish();
                } else {
                    Toasty.error(ContactListActivity.this, "Room name must be between 1-20 characters long.", Toast.LENGTH_LONG).show();
                }
            }
        });

        createRoomDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        createRoomDialog.show();
        new_room.setVisibility(View.VISIBLE);
        new_contact.setVisibility(View.VISIBLE);

    }


    // fetch remote friend on firestore ...


    @Override
    protected void onStart() {
        super.onStart();
        //fetchAllContact();
    }

    private void getContactList() {
        ContentResolver cr = getContentResolver();
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
                                    ViewFriends users_firestore = doc.getDocument().toObject(ViewFriends.class);
                                    String friend_name = doc.getDocument().getString("name");
                                    String room_uid = doc.getDocument().getString("room_uid");
                                    int room_id = (int) doc.getDocument().getLong("room_id").intValue();

                                    // int money = snapshot.getLong("money").intValue();
                                    users_firestore.setRoom_id(room_id);
                                    users_firestore.setRoom_uid(room_uid);
                                    //usersList.add(users);
                                    contactList.add(users_firestore);

                                    // usersAdapter.notifyDataSetChanged();
                                }
                            }
                        }else{
                            Toasty.info(ContactListActivity.this, "No friends found.", Toast.LENGTH_SHORT).show();
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
                ViewFriends Contact = new ViewFriends("--id--",
                        "--usn---",
                        name,
                        "https://cdn2.iconfinder.com/data/icons/perfect-flat-icons-2/512/User_man_male_profile_account_person_people.png"
                       ,"----email-----"
                        ,"----token_id"
                        , phoneNo.toString()
                        ,"romm uid"
                        ,"room_id"
                         , -1);

                contactList.add(Contact);
            }
        }
        if(cur!=null){
            cur.close();
        }


        mAdapter.notifyDataSetChanged();
    }

    /**
     * fetches json by making http calls
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
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
        return true;
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
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public void onContactSelected(ViewFriends contact) {

        if(contact.getRoom_id()!= -1){

            Intent intent = new Intent(ContactListActivity.this, ChatActivity.class);
            intent.putExtra("room_id", contact.getRoom_id());
            intent.putExtra("room_name", contact.getName());
            intent.putExtra("type", ChatActivity.ROOM);
            startActivity(intent);
        }else{

             Toasty.info(getApplicationContext(), "Invite this Contact ? ", Toast.LENGTH_LONG).show();
        }
    }
}
