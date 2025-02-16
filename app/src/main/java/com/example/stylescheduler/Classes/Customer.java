package com.example.stylescheduler.Classes;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class Customer extends User {
    private String phoneNumber;
    private List<Appointment> appointments;

    public Customer(String id, String name, String email, String password, String phoneNumber) {
        super(id, name, email, password, "customer");
        this.phoneNumber = phoneNumber;
        this.appointments = new ArrayList<>();
    }

    @Override
    public void register() {
        // Store customer in Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("customers");
        databaseRef.child(id).setValue(this);
    }

    @Override
    public boolean login() {
        return false;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
