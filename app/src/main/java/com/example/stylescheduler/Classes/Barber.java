package com.example.stylescheduler.Classes;

import java.util.*;

public class Barber extends User {
    private String shopAddress;

    public Barber(String userID, String name, String email, String password,String shopAddress,String phoneNumber) {
        super(userID, name, email, password, "barber",phoneNumber);
        this.shopAddress = shopAddress;

    }

    public Barber() {
        super();
    }
    // 📌 **הוספת חופשה - שימוש ב-Date במקום LocalDate**

    public String getName(){
        return this.name;
    }
    public String getRole(){
        return this.role;
    }

    public String getEmail(){
        return this.email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getShopAddress() {
        return this.shopAddress;
    }


    // 📌 **קבלת זמני עבודה פנויים**


    // 📌 **פונקציה חדשה לקבלת היום בשבוע מ-Date באמצעות Calendar**
    public int getDayOfWeekFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK); // 1 = Sunday, 2 = Monday, ..., 7 = Saturday
    }

}