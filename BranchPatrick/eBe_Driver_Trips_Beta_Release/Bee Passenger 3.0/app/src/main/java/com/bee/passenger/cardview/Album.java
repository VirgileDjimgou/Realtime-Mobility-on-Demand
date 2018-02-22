package com.bee.passenger.cardview;

public class Album {
    private String name;
    private String infos;
    private int thumbnail;

    public Album() {
    }

    public Album(String name, String infos, int thumbnail) {
        this.name = name;
        this.infos = infos;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumOfSongs() {
        return infos;
    }

    public void setNumOfSongs(String infos) {
        this.infos = infos;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
