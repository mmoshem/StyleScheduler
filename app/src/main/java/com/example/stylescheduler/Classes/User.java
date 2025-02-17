package com.example.stylescheduler.Classes;

import java.util.SplittableRandom;

abstract class User {
    protected String userID;
    protected String name;
    protected String email;
    protected String password;
    protected String role;  // "barber" or "customer"
    protected String phoneNumber;

    public User(String userID, String name, String email, String password, String role, String phoneNumber) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }
    

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    // Methods for registration and login
   // public abstract void register();
  //  public abstract boolean login();
}