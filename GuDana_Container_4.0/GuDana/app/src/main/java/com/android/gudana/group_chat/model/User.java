package com.android.gudana.group_chat.model;

public class User {

    private String username;
    private String email;
    private String profilePicLocation;
    private String UserId;
    private String Token_id;

    public User(){

    }

    public User(String UserId, String name, String email, String token_id , String profilePicLocation){
        this.username = name;
        this.email = email;
        this.UserId = UserId;
        this.Token_id = token_id;
        this.profilePicLocation = profilePicLocation;
    }

    public String getToken_id() {
        return Token_id;
    }

    public void setToken_id(String token_id) {
        Token_id = token_id;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePicLocation() {
        return profilePicLocation;
    }

    // new added


    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfilePicLocation(String profilePicLocation) {
        this.profilePicLocation = profilePicLocation;
    }
}
