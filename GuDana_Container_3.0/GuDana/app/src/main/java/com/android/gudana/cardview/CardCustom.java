package com.android.gudana.cardview;


public class CardCustom {
    private String name;
    private int numOfLike;
    private int thumbnail;

    public CardCustom() {
    }

    public CardCustom(String name, int numbofLike, int thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
        this.numOfLike = numbofLike;


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfSongs() {
        return numOfLike;
    }

    public void setNumOfSongs(int numOfLike) {
        this.numOfLike = numOfLike;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
