package com.example.stylescheduler.Classes;

import com.example.stylescheduler.Classes.Barber;
import com.example.stylescheduler.Classes.Customer;

import java.time.LocalDateTime;

public class Appointment {
    private int appointmentID;
    private Barber barber;  // Store Barber object instead of just name
    private Customer customer;  // Store Customer object
    private String serviceType;
    private LocalDateTime appointmentTime;  // Use LocalDateTime for time
    private String status;  // "booked", "cancelled", "completed"

    public Appointment(int appointmentID, Barber barber, Customer customer, String serviceType, LocalDateTime appointmentTime) {
        this.appointmentID = appointmentID;
        this.barber = barber;
        this.customer = customer;
        this.serviceType = serviceType;
        this.appointmentTime = appointmentTime;
        this.status = "booked";
    }

    // Getters
    public int getAppointmentID() {
        return appointmentID;
    }

    public Barber getBarber() {
        return barber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getServiceType() {
        return serviceType;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    public void setBarber(Barber barber) {
        this.barber = barber;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Methods to manage appointments
    public void cancel() {
        this.status = "cancelled";
    }

    public void confirm() {
        this.status = "completed";
    }

    public void reschedule(LocalDateTime newTime) {
        this.appointmentTime = newTime;
    }
}
