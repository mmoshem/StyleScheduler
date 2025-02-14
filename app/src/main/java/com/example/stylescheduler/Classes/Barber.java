package com.example.stylescheduler.Classes;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class Barber extends User {
    private String shopName;
    private String shopAddress;
    private List<String> workingDays; // Stores the days the barber is available
    private List<String> services; // List of services the barber offers
    private List<LocalDateTime> sickDays; // Stores dates when the barber is unavailable due to illness

    private DatabaseReference databaseRef; // Firebase reference

    public Barber(String name, String email, String password, String shopName, String shopAddress) {
        super(name, email, password, "barber"); // Call parent constructor with user role "barber"
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.workingDays = new ArrayList<>();
        this.services = new ArrayList<>();
        this.sickDays = new ArrayList<>();
        this.databaseRef = FirebaseDatabase.getInstance().getReference("barbers");
    }

    // Implements abstract method from User class
    @Override
    public void register() {
        // Store barber information in Firebase database
        databaseRef.child(email.replace("@", "_"))
                .setValue(this);
    }

    @Override
    public boolean login() {
        // Firebase authentication logic can be placed here
        return true; // Placeholder for actual implementation
    }

    // Sets the working days for the barber
    public void setWorkingDays(List<String> workingDays) {
        this.workingDays = workingDays;
    }

    // Adds a new service to the barber's list of offerings
    public void addService(String service) {
        this.services.add(service);
    }

    // Marks a specific day as a sick day and cancels all appointments for that day
    public void markSickDay(LocalDateTime sickDay, AppointmentSystem appointmentSystem) {
        if (appointmentSystem != null) {
            this.sickDays.add(sickDay);
            appointmentSystem.cancelAppointmentsForSickDay(this, sickDay);
        }
    }

    // Checks if the barber is available on a given date
    public boolean isAvailable(LocalDateTime time) {
        return !sickDays.contains(time);
    }

    // Getters for shop details
    public String getShopName() {
        return shopName;
    }

    public String getShopAddress() {
        return shopAddress;
    }
}
