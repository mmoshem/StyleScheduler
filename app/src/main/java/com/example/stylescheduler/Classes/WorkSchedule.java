package com.example.stylescheduler.Classes;

import android.util.Log;

import java.util.*;

public class WorkSchedule {
    private List<Integer> workingDays;
    private String startHour;
    private String endHour;
   // private List<Date> vacationDays;

    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }
    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public WorkSchedule() {
        this.workingDays = new ArrayList<>();
        this.startHour = "Not Set";
        this.endHour = "Not Set";
      //  this.vacationDays = new ArrayList<>();
    }

    public void setWorkingDays(List<Integer> workingDays) {
        if (workingDays == null) {
            Log.d("WorkSchedule", "Attempted to set working days to null!");
            this.workingDays = new ArrayList<>();
        } else {
            this.workingDays.clear();  // Make sure old data is removed
            this.workingDays.addAll(workingDays);
        }
        Log.d("WorkSchedule", "Stored working days after setting: " + this.workingDays);
    }

    public String getWorkingDaysString() {
        if (workingDays.isEmpty()) return "Not Set";
        StringBuilder days = new StringBuilder();
        for (Integer day : workingDays) {
            days.append(getDayName(day)).append(", ");
        }
        return days.substring(0, days.length() - 2);
    }
    public List<Integer> getWorkingDays() {
        if (this.workingDays == null) {
            Log.d("WorkSchedule", "getWorkingDays() called but list was null, returning empty list.");
            return new ArrayList<>();
        }
        Log.d("WorkSchedule", "Returning working days: " + this.workingDays);
        return new ArrayList<>(this.workingDays);
    }

    public String getDayName(int day) {
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
    public int getDayNumber(String day) {
        switch (day) {
            case "Sunday": return Calendar.SUNDAY;
            case "Monday": return Calendar.MONDAY;
            case "Tuesday": return Calendar.TUESDAY;
            case "Wednesday": return Calendar.WEDNESDAY;
            case "Thursday": return Calendar.THURSDAY;
            case "Friday": return Calendar.FRIDAY;
            case "Saturday": return Calendar.SATURDAY;
            default: return -1; // Invalid day
        }
    }
    public List<String> getWorkingHoursAsList() {
        if (startHour == null || endHour == null || startHour.equals("Not Set") || endHour.equals("Not Set")) {
            return new ArrayList<>(List.of("NotSet","NotSet"));
        }
        return Arrays.asList(startHour, endHour);
    }

}
