package com.example.stylescheduler.Classes;

import java.util.Date;

class Appointment {
    private int appointmentID;
    private Customer customer;
    private Barber barber;
    private String serviceType;  // e.g., "haircut", "coloring"
    private Date appointmentTime;
    private String status;  // "booked", "cancelled", "completed"

    public Appointment(int appointmentID, Customer customer, Barber barber, String serviceType, Date appointmentTime) {
        this.appointmentID = appointmentID;
        this.customer = customer;
        this.barber = barber;
        this.serviceType = serviceType;
        this.appointmentTime = appointmentTime;
        this.status = "booked";
    }

    // 📌 **ביטול תור**
    public void cancel() {
        this.status = "cancelled";
    }

    // 📌 **אישור תור**
    public void confirm() {
        this.status = "completed";
    }

    // 📌 **שינוי זמן התור - שימוש ב-Date במקום LocalDateTime**
    public void reschedule(Date newTime) {
        this.appointmentTime = newTime;
    }

    // 📌 **מתודה לקבלת זמן התור**
    public Date getAppointmentDate() {
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
