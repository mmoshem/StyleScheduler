package com.example.stylescheduler.Classes;

abstract class User {
    protected String userID;
    protected String name;
    protected String email;
    protected String password;
    protected String role;  // "barber" or "customer"

    public User(String userID, String name, String email, String password, String role) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Methods for registration and login
   // public abstract void register();
  //  public abstract boolean login();
}