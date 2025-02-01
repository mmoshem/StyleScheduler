package com.example.stylescheduler.Classes;

import java.time.LocalDateTime;
import java.util.List;

class Barber extends User {
    private String shopName;
    private String shopAddress;
    private List<String> workingDays;
    private List<LocalDateTime> availableTimeSlots;

    public Barber(int userID, String name, String email, String password, String shopName, String shopAddress) {
        super(userID, name, email, password, "barber");
        this.shopName = shopName;
        this.shopAddress = shopAddress;
    }

    public void setWorkingDays(List<String> workingDays) {
        this.workingDays = workingDays;
    }

    public void setAvailableTimeSlots(List<LocalDateTime> availableTimeSlots) {
        this.availableTimeSlots = availableTimeSlots;
    }

    public void viewScheduledAppointments(List<Appointment> appointments) {
        for (Appointment appointment : appointments) {
            if (appointment.getBarber().equals(this)) {
                System.out.println("Appointment for customer: " + appointment.getCustomer().name + " at " + appointment.getAppointmentTime());
            }
        }
    }

    @Override
    public void register() {

    }

    @Override
    public boolean login() {
        return false;
    }
}