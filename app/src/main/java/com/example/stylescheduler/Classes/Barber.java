package com.example.stylescheduler.Classes;

import java.util.*;

public class Barber extends User {
    private String shopAddress;
    private WorkSchedule workSchedule;

    public WorkSchedule getWorkSchedule() {
        return workSchedule;
    }
    public Barber(String userID, String name, String email, String password,String shopAddress,String phoneNumber) {
        super(userID, name, email, password, "barber",phoneNumber);
        this.shopAddress = shopAddress;
        this.workSchedule = new WorkSchedule();
    }
    public Barber() {
        super();
        this.shopAddress = "";
        this.workSchedule = new WorkSchedule();
    }

    public String getName() { return this.name; }
    public String getPhoneNumber() { return this.phoneNumber; }
    public String getShopAddress() { return this.shopAddress; }

    public String getWorkingDays() { return workSchedule.getWorkingDaysString(); }
    public List<String> getWorkingHours() { return workSchedule.getWorkingHoursAsList(); }
    public String getAStringOfWorkingHours() {
        List<String> s = getWorkingHours();
        return s.get(0) + " - " + s.get(1);

    }
    public void setWorkSchedule(WorkSchedule workSchedule) {
        this.workSchedule = workSchedule;
    }
//    public String getRole(){
//        return this.role;
//    }
    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }
}