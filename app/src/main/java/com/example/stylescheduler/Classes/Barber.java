package com.example.stylescheduler.Classes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

class Barber extends User {
    private String shopName;
    private String shopAddress;
    private List<eDays> workingDays;
    private List<String> services;
    private List<LocalDateTime> sickDays;

    public Barber(String name, String email, String password, String shopName, String shopAddress) {
        super(name, email, password, "barber");
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.workingDays = new ArrayList<>();
        this.services = new ArrayList<>();
        this.sickDays = new ArrayList<>();
    }

    public void setWorkingDays(List<eDays> workingDays) {
        this.workingDays = workingDays;
    }

    public void addService(String service) {
        this.services.add(service);
    }

    public void markSickDay(LocalDateTime sickDay, AppointmentSystem appointmentSystem) {
        this.sickDays.add(sickDay);
        appointmentSystem.cancelAppointmentsForSickDay(this, sickDay);
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
}


