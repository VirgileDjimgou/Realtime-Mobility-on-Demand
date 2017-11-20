package com.robertnorthard.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Account singleton class.
 *
 * @author robertnorthard
 */
public class Account implements Serializable{

    private String commonName;
    private String familyName;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String role;
    private String gcmRegId;

    @JsonIgnore
    private boolean active;

    private static Account account;

    public Account(){

    }

    public static Account getInstance(){
        if(account==null){
            //dead locking approach - threads only acquire intrinsic lock if necessary.
            synchronized (Account.class){
                Account.account = new Account();
            }
        }
        return Account.account;
    }

    public synchronized static void setInstance(Account account){
        Account.account = account;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getFamilyName() { return familyName; }

    public void setFamilyName(String familyName) { this.familyName = familyName; }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getUsername() { return this.username; }

    public String getEmail() { return this.email; }

    public String getPassword() { return this.password; }

    public String getRole() {
        return role;
    }

    public void setRole(String role) { this.role = role; }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGcmRegId() {
        return gcmRegId;
    }

    public void setGcmRegId(String gcmRegId) {
        this.gcmRegId = gcmRegId;
    }

    @JsonIgnore
    public boolean isDriver(){
        return this.role.equals("DRIVER");
    }
}
