package com.android.gudana.chat.model;



public class Call
{

    private String timestamp_call;
    private String ty_video_or_audio;
    private long Date_call;
    private String Call_duration;
    private Integer CallAtribut ; // 0 incomming  Call ,  1 Outgoing Call  , 2 missed Call

    private String id;
    private String username;
    private String name;
    private String image;


    public Call(String timestamp_call, String ty_video_or_audio , long date_call, String call_duration, Integer callAtribut, String id, String username, String name, String image) {
        this.timestamp_call = timestamp_call;
        Date_call = date_call;
        Call_duration = call_duration;
        CallAtribut = callAtribut;
        this.id = id;
        this.username = username;
        this.name = name;
        this.image = image;
        this.ty_video_or_audio = ty_video_or_audio;
    }

    public String getTy_video_or_audio() {
        return ty_video_or_audio;
    }

    public void setTy_video_or_audio(String ty_video_or_audio) {
        this.ty_video_or_audio = ty_video_or_audio;
    }

    public String getTimestamp_call() {
        return timestamp_call;
    }

    public void setTimestamp_call(String timestamp_call) {
        this.timestamp_call = timestamp_call;
    }

    public long getDate_call() {
        return Date_call;
    }

    public void setDate_call(long date_call) {
        Date_call = date_call;
    }

    public String getCall_duration() {
        return Call_duration;
    }

    public void setCall_duration(String call_duration) {
        Call_duration = call_duration;
    }

    public Integer getCallAtribut() {
        return CallAtribut;
    }

    public void setCallAtribut(Integer callAtribut) {
        CallAtribut = callAtribut;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


}
