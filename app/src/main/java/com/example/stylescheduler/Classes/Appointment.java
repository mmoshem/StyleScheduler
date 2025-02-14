package com.example.stylescheduler.Classes;
import java.time.LocalDateTime;

public class Appointment {
    private int appointmentID;
    private Customer customer;
    private Barber barber;
    private String serviceType;  // e.g., "haircut", "coloring"
    private LocalDateTime appointmentTime;
    private String status;  // "booked", "cancelled", "completed"

    public Appointment(int appointmentID, Customer customer, Barber barber, String serviceType, LocalDateTime appointmentTime) {
        this.appointmentID = appointmentID;
        this.customer = customer;
        this.barber = barber;
        this.serviceType = serviceType;
        this.appointmentTime = appointmentTime;
        this.status = "booked";
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

    // Getters
    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Barber getBarber() {
        return barber;
    }

    public String getServiceType() {
        return serviceType;
    }
}

