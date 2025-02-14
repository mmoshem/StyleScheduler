package com.example.stylescheduler.Classes;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    @Override
    public void register() {
        databaseRef.child(email.replace("@", "_"))
                .setValue(this);
    }

    @Override
    public boolean login() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null && user.getEmail().equals(email);
    }

    public void setWorkingDays(List<String> workingDays) {
        this.workingDays = workingDays;
    }

    public void addService(String service) {
        this.services.add(service);
    }

    public void markSickDay(LocalDateTime sickDay, AppointmentSystem appointmentSystem) {
        if (appointmentSystem != null) {
            this.sickDays.add(sickDay);
            appointmentSystem.cancelAppointmentsForSickDay(this, sickDay);
        }
    }
    public String getWorkingDays() {
        return String.join(", ", workingDays); // Converts List<String> to "Monday, Tuesday, Wednesday"
    }

    public boolean isAvailable(LocalDateTime time) {
        return !sickDays.contains(time);
    }

    public String getShopName() {
        return shopName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public String getName() {
        return name;
    }
}
