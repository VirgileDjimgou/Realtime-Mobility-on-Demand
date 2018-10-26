package com.android.gudana.group_chat.model;

import java.util.ArrayList;
import java.util.List;

public class Chat {

    private String uid;
    private String chatName;
    private List<Message> messages;
    private List<Friend> friends;
    private String ProfilePicture;

    public Chat(){

    }

    public String getProfilePicture() {
        return ProfilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        ProfilePicture = profilePicture;
    }

    public Chat(String uid, String chatName , String ProfilePicture){
        this.uid = uid;
        this.ProfilePicture = ProfilePicture;
        this.chatName = chatName;
        this.messages = new ArrayList<Message>();
        this.friends = new ArrayList<Friend>();
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getChatName() {
        return chatName;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public boolean appendFriend(Friend friend){
        Boolean contFriend = friends.contains(friend);
        if(!contFriend){
            friends.add(friend);
            return true;
        }
        return false;
    }

    public boolean removeFriend(Friend friend){
        friends.remove(friend);
        return true;
    }

}
