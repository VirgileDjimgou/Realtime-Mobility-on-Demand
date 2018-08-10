package com.android.rivchat.viewpagercards;


import android.graphics.Bitmap;
import android.media.Image;
import android.widget.Button;
import android.widget.ImageView;

public class CardItem {

    private int mTitleResource;
    private int mImageRessource;

    public CardItem(int title, int BitmapImg) {
        mTitleResource = title;
        mImageRessource = BitmapImg;
    }

    public int getImage(){return mImageRessource;}


    public int getTitle() {
        return mTitleResource;
    }
}
