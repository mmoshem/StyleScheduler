package com.example.stylescheduler.Classes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDateTime;
import com.example.stylescheduler.Classes.Appointment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class AppointmentSystem {
    private List<Appointment> appointments; // List of all appointments
    private DatabaseReference databaseRef; // Reference to Firebase database
    private AtomicInteger appointmentIDCounter; // For generating unique appointment IDs

    public AppointmentSystem() {
        this.appointments = new ArrayList<>();
        this.databaseRef = FirebaseDatabase.getInstance().getReference("appointments");
        this.appointmentIDCounter = new AtomicInteger(1); // Start IDs from 1
    }

    // Generate a unique appointment ID
    public int generateAppointmentID() {
        return appointmentIDCounter.getAndIncrement();
    }

    // Add an appointment to the system
    public void addAppointment(Appointment appointment) {
        if (appointment != null) {
            appointments.add(appointment);
            databaseRef.child(String.valueOf(appointment.getAppointmentID())).setValue(appointment);
        }
    }

    // Cancel all appointments for a barber's sick day
    public void cancelAppointmentsForSickDay(Barber barber, LocalDateTime sickDay) {
        List<Appointment> toCancel = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                if (appointment.getBarber().equals(barber) &&
                        appointment.getAppointmentTime().toLocalDate().equals(sickDay.toLocalDate())) {
                    appointment.cancel();
                    toCancel.add(appointment);
                    sendCancellationNotification(appointment.getCustomer(), barber);
                }
            }
        }
        appointments.removeAll(toCancel);
    }

    // Send a push notification when an appointment is canceled due to a sick day
    private void sendCancellationNotification(Customer customer, Barber barber) {
        String message = "Your appointment with " + barber.getShopName() + " has been canceled due to illness.";

        RemoteMessage.Builder builder = new RemoteMessage.Builder("Appointment_Cancel")
                .addData("title", "Appointment Canceled")
                .addData("body", message);

        FirebaseMessaging.getInstance().send(builder.build());
    }

    // Get all appointments
    public List<Appointment> getAppointments() {
        return appointments;
    }
}
