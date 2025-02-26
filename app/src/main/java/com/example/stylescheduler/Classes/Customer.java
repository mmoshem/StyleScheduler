package com.example.stylescheduler.Classes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Customer extends User {
    public Customer(String userID, String name, String email, String password, String phoneNumber) {
        super(userID, name, email, password, "customer",phoneNumber);
    }
    public Customer() {
        super();
    }
    public String getName(){
        return this.name;
    }

    public String getEmail(){
        return this.email;
    }
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
}
