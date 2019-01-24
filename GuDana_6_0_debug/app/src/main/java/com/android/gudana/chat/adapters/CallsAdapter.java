package com.android.gudana.chat.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.R;
import com.android.gudana.chat.activities.ChatActivity;
import com.android.gudana.chat.fragments.Calls_Fragment;
import com.android.gudana.chat.model.Call;
import com.android.gudana.hify.models.ViewFriends;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

/**
 * Created by ravi on 16/11/17.
 */

public class CallsAdapter extends RecyclerView.Adapter<CallsAdapter.MyViewHolder> {
    private Context context;
    private List<Call> contactList;



    public CallsAdapter(Context context, List<Call> contactList) {
        this.context = context;
        this.contactList = contactList;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView user_name , date_time_call;
        public ImageView type_call_small;
        public CircleImageView user_image;
        public ImageView call_type_big_image;


        public MyViewHolder(View view) {
            super(view);
            user_name = view.findViewById(R.id.user_name);
            date_time_call = view.findViewById(R.id.date_time_call);
            type_call_small = view.findViewById(R.id.type_call_small);
            call_type_big_image = view.findViewById(R.id.call_type_big_image);
            user_image = view.findViewById(R.id.user_image);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback

                }
            });
        }
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Call Calls = contactList.get(position);
        holder.user_name.setText(Calls.getName());
        holder.date_time_call.setText(Calls.getTimestamp_call());
        // set  user image
        Glide.with(context)
                .load(Calls.getImage())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.user_image);

        // set call Attribut  ... outgoing call .... missed call ..... incomming call ...

        if(Calls.getCallAtribut() == 0){ // incomming call

            Glide.with(context)
                    .load(R.mipmap.incoming_call)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.type_call_small);


        }else if (Calls.getCallAtribut() == 1){ // outgoing call

            Glide.with(context)
                    .load(R.mipmap.outgoing_call)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.type_call_small);

        }else{ /// if Attribut == 0 4 or what ever  ...tha missed call

            Glide.with(context)
                    .load(R.mipmap.missed_call)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.type_call_small);

        }


        /*
        if(contact.getRoom_id()!= -1){

            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("room_id", contact.getRoom_id());
            intent.putExtra("room_name", contact.getName());
            intent.putExtra("type", ChatActivity.ROOM);
            startActivity(intent);
        }else{

            Toasty.info(context, "Invite this Contact ? ", Toast.LENGTH_LONG).show();
        }

        */

        // video or audio call

        if(Calls.getTy_video_or_audio().equalsIgnoreCase("video")){ // video

            Glide.with(context)
                    .load(R.mipmap.ic_video)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.call_type_big_image);

        }else { // audio

            Glide.with(context)
                    .load(R.mipmap.ic_call_history)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.call_type_big_image);

        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.call_user, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return this.contactList.size();
    }

}
