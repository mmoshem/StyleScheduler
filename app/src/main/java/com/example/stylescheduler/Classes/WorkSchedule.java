package com.example.stylescheduler.Classes;

import java.util.*;

public class WorkSchedule {
    private Map<Integer, List<Integer>> workingHours; // שעות עבודה לכל יום (Sunday = 1, Monday = 2, ...)
    private Set<Date> vacationDays; // ימים חופשיים
    private List<Appointment> bookedAppointments; // רשימת תורים קיימים

    public WorkSchedule() {
        this.workingHours = new HashMap<>();
        this.vacationDays = new HashSet<>();
        this.bookedAppointments = new ArrayList<>();
    }

    // 📌 **הגדרת שעות עבודה (משתמש ב-Calendar במקום LocalDate)**
    public void setWorkingHours(int day, List<Integer> hours) {
        workingHours.put(day, hours);
    }

    // 📌 **איפוס שעות עבודה (לא מוחק תורים)**
    public void clearWorkingHours() {
        workingHours.clear();
    }

    // 📌 **הוספת יום חופש**
    public void addVacationDay(Date date) {
        vacationDays.add(date);
    }

    // 📌 **הוספת תור חדש למערכת**
    public void addAppointment(Appointment appointment) {
        bookedAppointments.add(appointment);
    }

    // 📌 **מחיקת תור (למשל אם ספר חולה)**
    public void cancelAppointment(Appointment appointment) {
        bookedAppointments.remove(appointment);
    }

    // 📌 **קבלת רשימת תורים קיימים**
    public List<Appointment> getBookedAppointments() {
        return new ArrayList<>(bookedAppointments);
    }

    // 📌 **פונקציה חדשה לקבלת היום בשבוע מ-Date באמצעות Calendar**
    private int getDayOfWeekFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK); // 1 = Sunday, 2 = Monday, ..., 7 = Saturday
    }

    // 📌 **קבלת רשימת זמנים פנויים לשבועיים הקרובים**
    public List<Date> getAvailableTimeSlots(Date startDate, int daysRange) {
        List<Date> availableSlots = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        for (int i = 0; i < daysRange; i++) {
            if (!vacationDays.contains(calendar.getTime())) { // בדיקה שהיום לא יום חופש
                int dayOfWeek = getDayOfWeekFromDate(calendar.getTime()); // שימוש ב-Calendar
                if (workingHours.containsKey(dayOfWeek)) {
                    for (int hour : workingHours.get(dayOfWeek)) {
                        Calendar slotCalendar = (Calendar) calendar.clone();
                        slotCalendar.set(Calendar.HOUR_OF_DAY, hour);
                        availableSlots.add(slotCalendar.getTime());
                    }
                }
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1); // במקום plusDays()
        }
        return availableSlots;
    }
}
