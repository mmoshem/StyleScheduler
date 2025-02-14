package com.example.stylescheduler.Classes;

import java.util.*;

public class WorkSchedule {
    private Map<Integer, ArrayList<Integer>> workingHours; // שעות עבודה לכל יום (Sunday = 1, Monday = 2, ...)
    private Set<Date> vacationDays; // ימים חופשיים
    private ArrayList<Appointment> bookedAppointments; // רשימת תורים קיימים

    public WorkSchedule() {
        this.workingHours = new HashMap<>();
        this.vacationDays = new HashSet<>();
        this.bookedAppointments = new ArrayList<>();
    }

    // 📌 **הגדרת שעות עבודה (משתמש ב-Calendar במקום LocalDate)**
    public void setWorkingHours(int day, ArrayList<Integer> hours) {
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
    public ArrayList<Appointment> getBookedAppointments() {
        return new ArrayList<>(bookedAppointments);
    }

    // 📌 **פונקציה חדשה לקבלת היום בשבוע מ-Date באמצעות Calendar**
    private int getDayOfWeekFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK); // 1 = Sunday, 2 = Monday, ..., 7 = Saturday
    }
    public String getWorkingDays() {
        if (workingHours.isEmpty()) return "Not Set";
        StringBuilder days = new StringBuilder();
        for (Integer day : workingHours.keySet()) {
            days.append(getDayName(day)).append(", ");
        }
        return days.substring(0, days.length() - 2); // להסיר פסיק אחרון
    }

    // 📌 **הוספת פונקציה לקבלת שעות עבודה**
    public String getWorkingHours() {
        if (workingHours.isEmpty()) return "Not Set";
        StringBuilder hours = new StringBuilder();
        for (Integer day : workingHours.keySet()) {
            hours.append(getDayName(day)).append(": ").append(workingHours.get(day)).append("\n");
        }
        return hours.toString();
    }

    // 📌 **המרת מספרי ימים לשמות ימים**
    private String getDayName(int day) {
        switch (day) {
            case Calendar.SUNDAY: return "Sunday";
            case Calendar.MONDAY: return "Monday";
            case Calendar.TUESDAY: return "Tuesday";
            case Calendar.WEDNESDAY: return "Wednesday";
            case Calendar.THURSDAY: return "Thursday";
            case Calendar.FRIDAY: return "Friday";
            case Calendar.SATURDAY: return "Saturday";
            default: return "Unknown";
        }
    }
    // 📌 **קבלת רשימת זמנים פנויים לשבועיים הקרובים**
    public ArrayList<Date> getAvailableTimeSlots(Date startDate, int daysRange) {
        ArrayList<Date> availableSlots = new ArrayList<>();
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
