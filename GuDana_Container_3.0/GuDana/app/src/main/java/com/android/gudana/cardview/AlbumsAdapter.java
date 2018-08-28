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
    private List<CardCustom> CardList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;
        public TextView  count;
        public ImageView overflow;


        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);

        }
    }


    public AlbumsAdapter(Context mContext, List<CardCustom> albumList) {
        this.mContext = mContext;
        this.CardList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        CardCustom album = CardList.get(position);
        holder.title.setText(album.getName());

        holder.count.setText(album.getNumOfSongs() + " views");

        // loading album cover using Glide library
        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });


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

                    // GuD Transfert  ...
                    // Intent phoneActivity = new Intent(mContext, LinphoneLauncherActivity.class);
                    // mContext.startActivity(phoneActivity);

                }


            }
        });


        // loading album cover using Glide library
        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);

    }


    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }


    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }


    @Override
    public int getItemCount() {
        return CardList.size();
    }
}
