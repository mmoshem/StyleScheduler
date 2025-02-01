package com.example.stylescheduler.Classes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class Customer extends User {
    private String phoneNumber;
    private List<Appointment> appointments;

    public Customer(int userID, String name, String email, String password, String phoneNumber) {
        super(userID, name, email, password, "customer");
        this.phoneNumber = phoneNumber;
        this.appointments = new ArrayList<>();  // Initialize the list
    }

    @Override
    public void register() {

    }

    @Override
    public boolean login() {
        return false;
    }

    // View list of barbers
    public List<Barber> viewBarbersList(List<Barber> barbers) {
        return barbers;
    }

    // Book an appointment
    public void bookAppointment(Barber barber, String serviceType, LocalDateTime time, AppointmentSystem appointmentSystem) {
        Appointment newAppointment = new Appointment(appointmentSystem.generateAppointmentID(), this, barber, serviceType, time);
        appointmentSystem.addAppointment(newAppointment);  // Add to central appointment system
        this.appointments.add(newAppointment);  // Add to the customer's list of appointments
    }

    public List<Appointment> viewAppointments() {
        return appointments;
    }
}
