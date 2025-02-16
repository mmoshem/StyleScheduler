package com.example.stylescheduler.Classes;

import java.time.LocalDateTime;

public class Appointment {
    private String appointmentID;  // Firebase-generated ID
    private String barberId;       // Stores barber's Firebase UID
    private String customerId;     // Stores customer's Firebase UID
    private String serviceType;
    private LocalDateTime appointmentTime;
    private String status;  // "booked", "cancelled", "completed"

    // Constructor
    public Appointment(String appointmentId, String barberId, String customerId, String serviceType, LocalDateTime appointmentTime) {
        this.appointmentID = appointmentID;
        this.barberId = barberId;
        this.customerId = customerId;
        this.serviceType = serviceType;
        this.appointmentTime = appointmentTime;
        this.status = "booked"; // Default status
    }





    // Default constructor (needed for Firebase)
    public Appointment() {}

    // Getters
    public String getAppointmentID() {
        return appointmentID;
    }

    public String getBarberId() {
        return barberId;
    }

    public String getCustomerId() {
        return customerId;
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
    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    public void setBarberId(String barberId) {
        this.barberId = barberId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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
