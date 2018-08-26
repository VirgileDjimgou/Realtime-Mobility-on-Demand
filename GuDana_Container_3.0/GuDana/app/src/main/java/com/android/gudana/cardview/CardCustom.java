package com.android.gudana.cardview;


public class CardCustom {
    private String name;
    private int thumbnail;

    public CardCustom() {
    }

    public CardCustom(String name,  int thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
