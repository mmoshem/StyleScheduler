package com.example.stylescheduler.Classes;

public abstract class User {
    protected String id;      // Unique ID for each user
    protected String name;
    protected String email;
    protected String password;
    protected String role;  // "barber" or "customer"

    public User(String id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters for ID
    public String getId() {
        return id;
    }

    // Methods for registration and login
    public abstract void register();
    public abstract boolean login();
}
