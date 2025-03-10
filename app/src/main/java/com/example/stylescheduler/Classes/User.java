package com.example.stylescheduler.Classes;

import java.util.SplittableRandom;

abstract class User {
    protected String userID;
    protected String name;

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    protected String email;
    protected String password;

    public void setName(String name) {
        this.name = name;
    }

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
    public User(){}
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}