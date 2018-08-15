package com.android.gudana.viewpagercards;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gudana.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import static com.android.gudana.cardview.Card_Home_fragment.animHide;
import static com.android.gudana.cardview.Card_Home_fragment.layoutAnimation;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<CardItem> mData;
    private float mBaseElevation;
    private Context mContext;
    public  int  index ;


    public CardPagerAdapter() {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(CardItem item) {
        mViews.add(null);
        mData.add(item);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.adapter_pager_card, container, false);
        container.addView(view);
        mContext = view.getContext();
        bind(mData.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        index = position;

        ImageView Image_card = (ImageView) view.findViewById(R.id.card_image);
        Image_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //container.setVisibility(View.GONE);
                Toast.makeText(mContext, "Card " + index + " is pressed.", Toast.LENGTH_SHORT).show();

                animHide.reset();
                animHide = AnimationUtils.loadAnimation(mContext, R.anim.view_validate);
                layoutAnimation.setAnimation(animHide);
                layoutAnimation.setVisibility(View.GONE);
            }
        });


        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(CardItem item, View view) {
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        ImageView contentTmages = (ImageView) view.findViewById(R.id.card_image);
        titleTextView.setText(item.getTitle());
        //contentTmages.setImageBitmap(item.getImage());

        // loading album cover using Glide library
        Glide.with(mContext).load(item.getImage()).into(contentTmages);    }

}
