package com.example.stylescheduler.Classes;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

class AppointmentSystem {
    private List<Appointment> appointments; // List of all appointments
    private DatabaseReference databaseRef; // Reference to Firebase database

    public AppointmentSystem() {
        this.appointments = new ArrayList<>();
        this.databaseRef = FirebaseDatabase.getInstance().getReference("appointments");
    }

    // Cancels all appointments scheduled for a barber on a sick day
    public void cancelAppointmentsForSickDay(Barber barber, LocalDateTime sickDay) {
        List<Appointment> toCancel = new ArrayList<>();
        for (Appointment appointment : appointments) {
            // Check if appointment belongs to this barber and matches the sick day
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                if (appointment.getBarber().equals(barber) &&
                        appointment.getAppointmentTime().toLocalDate().equals(sickDay.toLocalDate())) {
                    appointment.cancel(); // Mark appointment as cancelled
                    toCancel.add(appointment);
                    sendCancellationNotification(appointment.getCustomer(), barber); // Notify customer
                }
            } else {
                // Handle older Android versions if needed (e.g., compare manually)
            }
        }
        appointments.removeAll(toCancel); // Remove cancelled appointments from list
    }

    // Sends a Firebase push notification to a customer when their appointment is canceled
    private void sendCancellationNotification(Customer customer, Barber barber) {
        String message = "Your appointment with " + barber.getShopName() + " has been canceled due to illness.";
        RemoteMessage notification = new RemoteMessage.Builder("Appointment_Cancel")
                .addData("title", "Appointment Canceled")
                .addData("body", message)
                .build();

        FirebaseMessaging.getInstance().send(notification); // Send the notification via Firebase
    }
}
