package com.android.gudana.chat.adapters;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.R;
import com.android.gudana.chat.utilities.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class RoomAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

    private final Activity activity;
    private final Resources res;
    private final ArrayList<Object> mArrayList = new ArrayList<>();

    public RoomAdapter(Activity activity) {
        this.activity = activity;
        res = activity.getResources();
    }

    public void clear() {
        mArrayList.clear();
        notifyDataSetChanged();
    }

    public void addItem(final Object item) {
        if(item == null) return;
        mArrayList.add(item);
        notifyDataSetChanged();
    }

    public void addItems(final ArrayList<Object> items) {
        if(items == null) return;
        mArrayList.addAll(items);
        notifyDataSetChanged();
    }

    public void prependItems(final ArrayList<Object> items) {
        if(getCount() > 0) {
            mArrayList.addAll(0, items);
        } else {
            addItems(items);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(activity);

        final RoomViewHolder roomViewHolder;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.chat_listview_rooms, parent, false);

            roomViewHolder = new RoomViewHolder();
            roomViewHolder.roomNameText = (TextView) convertView.findViewById(R.id.txt_room_name);
            roomViewHolder.createdByUserText = (TextView) convertView.findViewById(R.id.txt_created_by_user);
            roomViewHolder.CircleImage_profile = (CircleImageView) convertView.findViewById(R.id.profile_image);
            roomViewHolder.MenuButton = (Button) convertView.findViewById(R.id.button_menu);
            convertView.setTag(roomViewHolder);
        } else {
            roomViewHolder = (RoomViewHolder) convertView.getTag();
        }

        roomViewHolder.MenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.warning(activity, "not yet implemented ", Toast.LENGTH_SHORT).show();
            }
        });

        RoomItem roomItem = (RoomItem) getItem(position);
        roomViewHolder.roomNameText.setText(roomItem.getRoomName());

        //roomViewHolder.CircleImage_profile

        Glide.with(activity)
                .setDefaultRequestOptions(new RequestOptions().placeholder(activity.getResources().getDrawable(R.drawable.user)))
                .load(roomItem.getUrl_profile_pic())
                .into(roomViewHolder.CircleImage_profile);

        final Date createdDate = Utility.parseDateAsUTC(roomItem.getDate());
        if(createdDate != null) {
            roomViewHolder.createdByUserText.setText(res.getString(R.string.created_by_user, roomItem.getUsername() + " " + Utility.getTimeAgo(createdDate)));
        }

        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(activity, "Item: " + position, Toast.LENGTH_SHORT).show();
    }

    public static class RoomItem {
        private final String room_name, username, date , url_profile_pic;
        private final String initiator_user_id,member_user_id,room_uid,type_id;
        private final int room_id;


        public RoomItem(int room_id, String room_name,
                        String username, String date ,
                        String url_profile_pic , String initiator_user_id,
                        String member_user_id, String room_uid , String type_id) {
            this.room_id = room_id;
            this.room_name = room_name;
            this.username = username;
            this.date = date;
            this.url_profile_pic = url_profile_pic;
            this.initiator_user_id = initiator_user_id;
            this.member_user_id = member_user_id;
            this.room_uid = room_uid;
            this.type_id = type_id;
        }

        public String getInitiator_user_id() {
            return initiator_user_id;
        }

        public String getMember_user_id() {
            return member_user_id;
        }

        public String getRoom_uid() {
            return room_uid;
        }

        public String getType_id() {
            return type_id;
        }

        public int getRoom_id() {
            return room_id;
        }

        public int getRoomID() {
            return room_id;
        }

        public String getRoomName() {
            return room_name;
        }

        public String getUsername() {
            return username;
        }

        public String getDate() {
            return date;
        }
        public String getUrl_profile_pic(){
            return url_profile_pic;
        }
    }

    public static class RoomViewHolder {
        public TextView roomNameText;
        public TextView createdByUserText;
        public CircleImageView CircleImage_profile;
        public Button MenuButton;
    }
}
