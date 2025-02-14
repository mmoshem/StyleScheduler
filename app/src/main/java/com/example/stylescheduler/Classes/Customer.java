package com.example.stylescheduler.Classes;

import com.example.stylescheduler.Classes.Appointment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private String phoneNumber;
    private List<Appointment> appointments;

    public Customer( String name, String email, String password, String phoneNumber) {
        super(name, email, password, "customer");
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
      //ToDO : check if already have existing appointment in the same time if no then add if exists , don't allow to add

        Appointment newAppointment = new Appointment(appointmentSystem.generateAppointmentID(), barber, this, serviceType, time);
        appointmentSystem.addAppointment(newAppointment);  // Add to central appointment system
        this.appointments.add(newAppointment);  // Add to the customer's list of appointments
    }

    public List<Appointment> viewMyAppointments() {
        return appointments;
    }
}
