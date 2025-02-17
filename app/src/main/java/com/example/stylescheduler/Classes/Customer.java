package com.example.stylescheduler.Classes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class Customer extends User {
    private String phoneNumber;
    private ArrayList<Appointment> appointments;

    public Customer(int userID, String name, String email, String password, String phoneNumber) {
        super(userID, name, email, password, "customer");
        this.phoneNumber = phoneNumber;
        this.appointments = new ArrayList<>();  // Initialize the list
    }

    // View list of barbers
    public ArrayList<Barber> viewBarbersList(ArrayList<Barber> barbers) {
        return barbers;
    }

    // Book an appointment
    public void bookAppointment(Barber barber, String serviceType, Date time, AppointmentSystem appointmentSystem) {
        // בדיקה האם השעה כבר תפוסה
        if (!appointmentSystem.isSlotAvailable(barber, time)) {
            System.out.println("This slot is already booked!");
            return;
        }

        // יצירת התור החדש עם `Date` במקום `LocalDateTime`
        Appointment newAppointment = new Appointment(
                appointmentSystem.generateAppointmentID(), this, barber, serviceType, time
        );

        // הוספת התור למערכת התורים המרכזית ולרשימת התורים של הלקוח
        appointmentSystem.addAppointment(newAppointment);
        this.appointments.add(newAppointment);
    }


    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    public void cancelAppointment(Appointment appointment) {
        appointments.remove(appointment);
    }

    public ArrayList<Appointment> getUpcomingAppointments() {
        return appointments;
    }
    public ArrayList<Appointment> viewMyAppointments() {
        return appointments;
    }
}
