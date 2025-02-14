package com.example.stylescheduler.Classes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class AppointmentSystem {
    private List<Appointment> appointments;
    private DatabaseReference databaseRef;

    public AppointmentSystem() {
        this.appointments = new ArrayList<>();
        this.databaseRef = FirebaseDatabase.getInstance().getReference("appointments");
    }

    public void cancelAppointmentsForSickDay(Barber barber, LocalDateTime sickDay) {
        List<Appointment> toCancel = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getBarber().equals(barber) && appointment.getAppointmentTime().toLocalDate().equals(sickDay.toLocalDate())) {
                appointment.cancel();
                toCancel.add(appointment);
                sendCancellationNotification(appointment.getCustomer(), barber);
            }
        }
        appointments.removeAll(toCancel);
    }

    private void sendCancellationNotification(Customer customer, Barber barber) {
        String message = "Your appointment with " + barber.getShopName() + " has been canceled due to illness.";
        FirebaseMessaging.getInstance().send(
                new com.google.firebase.messaging.Message.Builder()
                        .putData("title", "Appointment Canceled")
                        .putData("body", message)
                        .setTopic(customer.getEmail().replace("@", "_"))
                        .build()
        );
    }
}