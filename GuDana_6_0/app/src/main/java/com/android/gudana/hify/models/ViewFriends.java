package com.android.gudana.hify.models;

/**
 * Created by amsavarthan on 22/2/18.
 */

public class ViewFriends {

    private String id;
    private String username;
    private String name;
    private String image;
    private String email;
    private String token_id;
    private String phone;

    private String last_message = "";

    // second constructto r   ..
    private String room_uid,type_id;
    private int room_id;
    private int number_of_unread_message = 0;
    private String time_lastmessage= "";



    public ViewFriends() {
    }

    public ViewFriends(String id, String username, String name, String image,
                       String email, String token_id , String Phone , String room_uid , String type_id, int room_id) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.image = image;
        this.email = email;
        this.token_id = token_id;
        this.phone = Phone;


        this.room_uid = room_uid;
        this.type_id = type_id;
        this.room_id = room_id;
    }

    public String getTime_lastmessage() {
        return time_lastmessage;
    }

    public void setTime_lastmessage(String time_lastmessage) {
        this.time_lastmessage = time_lastmessage;
    }

    public int getNumber_of_unread_message() {
        return number_of_unread_message;
    }

    public void setNumber_of_unread_message(int number_of_unread_message) {
        this.number_of_unread_message = number_of_unread_message;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getRoom_uid() {
        return room_uid;
    }

    public void setRoom_uid(String room_uid) {
        this.room_uid = room_uid;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

}
