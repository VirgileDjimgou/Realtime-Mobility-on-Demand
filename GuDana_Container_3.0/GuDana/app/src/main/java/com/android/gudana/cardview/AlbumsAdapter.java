package com.android.gudana.cardview;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.R;
import com.android.gudana.linphone.LinphoneLauncherActivity;
import com.bumptech.glide.Glide;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;

import java.util.List;

import static com.android.gudana.cardview.Card_Home_fragment.Driver_Card;
import static com.android.gudana.cardview.Card_Home_fragment.Paket_Delivery;
import static com.android.gudana.cardview.Card_Home_fragment.Passenger_Card;
import static com.android.gudana.cardview.Card_Home_fragment.animShow;
import static com.android.gudana.cardview.Card_Home_fragment.layoutAnimation;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {

    private Context mContext;
    private List<Album> albumList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail;
        public BoomMenuButton bmb1;


        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

        }
    }


    public AlbumsAdapter(Context mContext, List<Album> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Album album = albumList.get(position);
        holder.title.setText(album.getName());
        holder.count.setText(album.getNumOfSongs() + " songs");


        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "click on  "+ String.valueOf((position) ), Toast.LENGTH_SHORT).show();
                if(position == 0){
                    Driver_Card();
                    layoutAnimation.setVisibility(View.VISIBLE);
                    animShow.reset();
                    animShow = AnimationUtils.loadAnimation(mContext, R.anim.view_show);
                    layoutAnimation.setAnimation(Card_Home_fragment.animShow);

                }

                if(position == 1){
                    Passenger_Card();
                    animShow.reset();
                    layoutAnimation.setVisibility(View.VISIBLE);
                    animShow = AnimationUtils.loadAnimation(mContext, R.anim.view_show);
                    layoutAnimation.setAnimation(Card_Home_fragment.animShow);


                }

                if(position == 2 ){
                    Paket_Delivery();
                    animShow.reset();
                    layoutAnimation.setVisibility(View.VISIBLE);
                    animShow = AnimationUtils.loadAnimation(mContext, R.anim.view_show);
                    layoutAnimation.setAnimation(Card_Home_fragment.animShow);

                }

                if (position == 3){

                    Intent phoneActivity = new Intent(mContext, LinphoneLauncherActivity.class);
                    mContext.startActivity(phoneActivity);

                }


            }
        });


        // loading album cover using Glide library
        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);

    }


    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
