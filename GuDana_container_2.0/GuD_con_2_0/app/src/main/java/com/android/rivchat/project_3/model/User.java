package com.android.rivchat.project_3.model;

public class User {

    private String username;
    private String email;
    private String profilePicLocation;

    public User(){

    }

    public User(String name, String email){
        this.username = name;
        this.email = email;
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

}
