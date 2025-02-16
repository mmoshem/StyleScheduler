package com.example.stylescheduler.Classes;

import com.example.stylescheduler.Classes.Appointment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private String phoneNumber;
    private List<Appointment> appointments;

    public Customer(String name, String email, String password, String phoneNumber) {
        super(name, email, password, "customer");
        this.phoneNumber = phoneNumber;
        this.appointments = new ArrayList<>();  // Initialize the list
    }

    @Override
    public void register() {}

    @Override
    public boolean login() {
        return false;
    }

    public List<Barber> viewBarbersList(List<Barber> barbers) {
        return barbers;
    }

    public void bookAppointment(Barber barber, String serviceType, LocalDateTime time, AppointmentSystem appointmentSystem) {
        if (appointments == null) {
            appointments = new ArrayList<>();
        }

        Appointment newAppointment = new Appointment(
                appointmentSystem.generateAppointmentID(),
                barber,
                this,
                serviceType,
                time
        );
        appointmentSystem.addAppointment(newAppointment);
        this.appointments.add(newAppointment);
    }

    public List<Appointment> viewMyAppointments() {
        return appointments != null ? appointments : new ArrayList<>();
    }
}
