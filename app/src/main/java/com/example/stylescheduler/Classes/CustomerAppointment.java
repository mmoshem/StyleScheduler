package com.example.stylescheduler.Classes;

public class CustomerAppointment {
    private String customerEmail;
    private String time;
    public CustomerAppointment() {}

    public CustomerAppointment(String customerEmail, String time) {
        this.customerEmail = customerEmail;
        this.time = time;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getTime() {
        return time;
    }
}