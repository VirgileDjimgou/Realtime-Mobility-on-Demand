package com.android.gudana.chat.fragments;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.android.gudana.chat.adapters.CallsAdapter;
import com.android.gudana.chat.adapters.RoomAdapter;
import com.android.gudana.chat.layouts.StatusLayout;
import com.android.gudana.chat.model.Call;
import com.android.gudana.chat.recyclerviewsearch.ContactsAdapter;
import com.android.gudana.chat.recyclerviewsearch.MyDividerItemDecoration;
import com.android.gudana.hify.models.ViewFriends;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import es.dmoral.toasty.Toasty;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

//import com.android.gudana.hify.ui.activities.friends.MessagesView;

public class Calls_Fragment extends Fragment{

    public ListView listRooms;
    public SwipeRefreshLayout swipeContainer;
    public static String username, session;
    public static int user_id;

    public StatusLayout statusLayout;
    public static  FragmentActivity activity;

    RoomAdapter adapter;

    // added
    private static final String TAG = Calls_Fragment.class.getSimpleName();
    private RecyclerView recyclerView;
    public static List<Call> CallsList;
    public static CallsAdapter mAdapter;
    public static  String member_user_id =  "fakemenber_uid_hkhjjhk_jhgjhgj6546546fhfg676546utuh65764";


    private FloatingActionButton test_action;
    View mView;

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
        View view = inflater.inflate(R.layout.calls_fragment, container, false);

        setHasOptionsMenu(true);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        /*
        // test baction button ...
        test_action = (FloatingActionButton) view.findViewById(R.id.test_action);
        test_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add  new call item ...
                newCallItem();
            }
        });

        */

        recyclerView = view.findViewById(R.id.recycler_view_call);
        CallsList = new ArrayList<>();
        mAdapter = new CallsAdapter(getActivity(), CallsList);

        // white background notification bar
        //whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);

        activity = (FragmentActivity)view.getContext();


        return view;
    }

    public static void newCallItem(final Call newCall_param){


        activity.runOnUiThread(new Runnable() {
            public void run() {

                CallsList.add(newCall_param);
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }



    private void getCallsList() {
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        CallsList.clear();

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.chat_menu_main, menu);

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getCallsList();
    }
}