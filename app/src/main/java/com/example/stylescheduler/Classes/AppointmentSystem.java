package com.example.stylescheduler.Classes;
import java.util.ArrayList;
import java.util.List;

class AppointmentSystem {
    private List<Appointment> appointments;
    private int appointmentIDCounter;

    public AppointmentSystem() {
        this.appointments = new ArrayList<>();
        this.appointmentIDCounter = 1;  // Start appointment IDs from 1
    }

    public int generateAppointmentID() {
        return appointmentIDCounter++;
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void displayAppointmentsForBarber(Barber barber) {
        for (Appointment appointment : appointments) {
            if (appointment.getBarber().equals(barber)) {
                System.out.println("Appointment for " + appointment.getCustomer().name + " at " + appointment.getAppointmentTime());
            }
        }
    }
}