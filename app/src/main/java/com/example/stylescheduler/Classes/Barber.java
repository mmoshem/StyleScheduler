package com.example.stylescheduler.Classes;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Barber extends User {
    private String shopName;
    private String shopAddress;
    private List<String> workingDays; // Stores the days the barber is available
    private List<LocalDateTime> sickDays; // Stores dates when the barber is unavailable due to illness
    private DatabaseReference databaseRef; // Firebase reference


    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public List<String> getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(List<String> workingDays) {
        this.workingDays = workingDays;
    }

    public List<LocalDateTime> getSickDays() {
        return sickDays;
    }

    public void setSickDays(List<LocalDateTime> sickDays) {
        this.sickDays = sickDays;
    }

    public DatabaseReference getDatabaseRef() {
        return databaseRef;
    }

    public void setDatabaseRef(DatabaseReference databaseRef) {
        this.databaseRef = databaseRef;
    }

    public Barber(String id, String name, String email, String password, String shopName, String shopAddress) {
        super(id, name, email, password, "barber");  // Call parent constructor
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.workingDays = new ArrayList<>();
        this.sickDays = new ArrayList<>();
        this.databaseRef = FirebaseDatabase.getInstance().getReference("barbers");
    }
    public String getId(){
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void register() {
        databaseRef.child(id).setValue(this);
    }

    @Override
    public boolean login() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null && user.getEmail().equals(email);
    }

    public String getShopName() {
        return shopName;
    }

    public String getShopAddress() {
        return shopAddress;
    }
}
