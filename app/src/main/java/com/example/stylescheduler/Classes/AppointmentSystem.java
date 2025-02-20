package com.example.stylescheduler.Classes;
import java.util.ArrayList;
import java.util.Date;


class AppointmentSystem {
    private ArrayList<Appointment> appointments;
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

    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    // 📌 **בדיקה שהתור פנוי - שימוש ב-Date במקום LocalDateTime**
    public boolean isSlotAvailable(Barber barber, Date time) {
        for (Appointment appointment : appointments) {
            if (appointment.getBarber().equals(barber) && appointment.getAppointmentDate().equals(time)) {
                return false; // כבר קיים תור בשעה זו
            }
        }
        return true;
    }

    public void bookAppointment(Customer customer, Barber barber, String serviceType, Date time) {
        // 📌 Ensure the appointment time is within the barber's working schedule
        int dayOfWeek = barber.getWorkSchedule().getDayOfWeekFromDate(time);
        String dayName = barber.getWorkSchedule().getDayName(dayOfWeek); // Convert to String


        String startHour = barber.getWorkSchedule().getWorkingHours().split(" - ")[0];
        String endHour = barber.getWorkSchedule().getWorkingHours().split(" - ")[1];

        try {
            Date startTime = barber.getWorkSchedule().parseTime(time, startHour);
            Date endTime = barber.getWorkSchedule().parseTime(time, endHour);

            if (time.before(startTime) || time.after(endTime)) {
                System.out.println("❌ Cannot book: Outside of working hours.");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isSlotAvailable(barber, time)) {
            System.out.println("❌ This slot is already booked!");
            return;
        }

        Appointment newAppointment = new Appointment(generateAppointmentID(), customer, barber, serviceType, time);
        appointments.add(newAppointment);
        barber.bookAppointment(newAppointment);
        customer.addAppointment(newAppointment);
    }


    // 📌 **תצוגת תורים של הספר - שימוש ב-Date**
    public void displayAppointmentsForBarber(Barber barber) {
        for (Appointment appointment : appointments) {
            if (appointment.getBarber().equals(barber)) {
                System.out.println("Appointment for " + appointment.getCustomer().name + " at " + appointment.getAppointmentDate());
            }
        }
    }
}
