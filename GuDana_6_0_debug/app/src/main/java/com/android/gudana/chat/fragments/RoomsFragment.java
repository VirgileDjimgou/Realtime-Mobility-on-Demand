package com.android.gudana.chat.fragments;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.gudana.R;
import com.android.gudana.chat.activities.ChatActivity;
import com.android.gudana.chat.adapters.RoomAdapter;
import com.android.gudana.chat.layouts.StatusLayout;
import com.android.gudana.chat.network.FetchRoomsAsyncTask;

import static com.android.gudana.chat.fragments.ChatFragment.member_user_id;


public class RoomsFragment extends Fragment {
    private static final String TAG = "fragments/RoomsFragment";

    public ListView listRooms;
    public SwipeRefreshLayout swipeContainer;
    public static String username, session;
    public static int user_id;

    public StatusLayout statusLayout;

    RoomAdapter adapter;

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
        View view = inflater.inflate(R.layout.chat_fragment_rooms, container, false);
        listRooms = (ListView) view.findViewById(R.id.list_rooms);
        statusLayout = (StatusLayout) view.findViewById(R.id.layout_status);

        listRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("room_id", ((RoomAdapter.RoomItem) adapter.getItem(position)).getRoomID());
                intent.putExtra("room_name", ((RoomAdapter.RoomItem) adapter.getItem(position)).getRoomName());
                intent.putExtra("type", ChatActivity.ROOM);
                getActivity().startActivity(intent);
            }
        });

        adapter = new RoomAdapter(getActivity());
        listRooms.setAdapter(adapter);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //(new FetchRoomsAsyncTask(RoomsFragment.this, username, user_id, session, member_user_id)).execute();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab2);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //ContactListActivity.startActivity(getActivity());
                //Intent ContactList = new Intent(RoomsFragment.this.getActivity(), ContactListActivity.class);
                //startActivity(ContactList);
            }
        });

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        (new FetchRoomsAsyncTask(this, username, user_id, session, member_user_id)).execute();
        swipeContainer.setVisibility(View.GONE);
        statusLayout.setLoading();
        statusLayout.setActionButton("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new FetchRoomsAsyncTask(RoomsFragment.this, username, user_id, session,member_user_id)).execute();
            }
        });
    }
}