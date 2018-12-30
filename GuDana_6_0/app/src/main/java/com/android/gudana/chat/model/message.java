package com.android.gudana.chat.model;

public class message {

    public  String ROOM_ID ;
    public  String ROOM_UID ;
    public  String USER_ID ;
    public  String USER_UID ;
    public  String CONTENT;
    public  String DataTime;
    private int id;

    public message(String ROOM_ID, String ROOM_UID,
                   String USER_ID, String USER_UID, String CONTENT, String dataTime) {
        this.ROOM_ID = ROOM_ID;
        this.ROOM_UID = ROOM_UID;
        this.USER_ID = USER_ID;
        this.USER_UID = USER_UID;
        this.CONTENT = CONTENT;
        DataTime = dataTime;
    }


    public String getROOM_ID() {
        return ROOM_ID;
    }

    public void setROOM_ID(String ROOM_ID) {
        this.ROOM_ID = ROOM_ID;
    }

    public String getROOM_UID() {
        return ROOM_UID;
    }

    public void setROOM_UID(String ROOM_UID) {
        this.ROOM_UID = ROOM_UID;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getUSER_UID() {
        return USER_UID;
    }

    public void setUSER_UID(String USER_UID) {
        this.USER_UID = USER_UID;
    }

    public String getCONTENT() {
        return CONTENT;
    }

    public void setCONTENT(String CONTENT) {
        this.CONTENT = CONTENT;
    }

    public String getDataTime() {
        return DataTime;
    }

    public void setDataTime(String dataTime) {
        DataTime = dataTime;
    }
}
