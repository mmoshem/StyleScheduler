package com.example.stylescheduler.Classes;

import java.util.*;

public class WorkSchedule {
    private Set<Integer> workingDays; // Store working days as a set of integers (Sunday = 1, Monday = 2, etc.)
    private String startHour;
    private String endHour;
    private Set<Date> vacationDays; // Days off
    private ArrayList<Appointment> bookedAppointments; // Existing appointments

    public WorkSchedule() {
        this.workingDays = new HashSet<>();
        this.startHour = "Not Set";
        this.endHour = "Not Set";
        this.vacationDays = new HashSet<>();
        this.bookedAppointments = new ArrayList<>();
    }

    // 📌 **Set working days**
    public void setWorkingDays(Set<Integer> days) {
        this.workingDays = days;
    }

    // 📌 **Set working hours for all selected days**
    public void setWorkingHours(String startHour, String endHour) {
        this.startHour = startHour;
        this.endHour = endHour;
    }

    // 📌 **Clear work schedule**
    public void clearSchedule() {
        workingDays.clear();
        startHour = "Not Set";
        endHour = "Not Set";
    }

    // 📌 **Retrieve working days as a formatted string**
    public String getWorkingDays() {
        if (workingDays.isEmpty()) return "Not Set";
        StringBuilder days = new StringBuilder();
        for (Integer day : workingDays) {
            days.append(getDayName(day)).append(", ");
        }
        return days.substring(0, days.length() - 2);
    }

    // 📌 **Retrieve working hours as a formatted string**
    public String getWorkingHours() {
        if (startHour.equals("Not Set") || endHour.equals("Not Set")) return "Not Set";
        return startHour + " - " + endHour;
    }

    // 📌 **Convert numeric day to text**
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
}
