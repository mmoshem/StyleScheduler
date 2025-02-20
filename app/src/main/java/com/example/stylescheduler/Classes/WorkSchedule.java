package com.example.stylescheduler.Classes;

import android.util.Log;

import java.util.*;

public class WorkSchedule {
    private List<Integer> workingDays; // Store working days as a set of integers (Sunday = 1, Monday = 2, etc.)
    private String startHour;
    private String endHour;
    private List<Date> vacationDays; // Days off

    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    private ArrayList<Appointment> bookedAppointments; // Existing appointments

    public WorkSchedule() {
        this.workingDays = new ArrayList<>();
        this.startHour = "Not Set";
        this.endHour = "Not Set";
        this.vacationDays = new ArrayList<>();
        this.bookedAppointments = new ArrayList<>();
    }

    // 📌 **Set working days**

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
//    public List<String> getWorkingDays() {
//        List<String> daysList = new ArrayList<>();
//        for (Integer day : workingDays) {
//            daysList.add(getDayName(day)); // Convert numbers to day names
//        }
//        return daysList;
//    }
    // 📌 **Convert numeric day to text**
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

    public int getDayOfWeekFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK); // 1 = Sunday, ..., 7 = Saturday
    }

    // 📌 **Check if a day has scheduled appointments**
    public boolean hasAppointmentsOnDay(int day) {
        return workingDays.contains(day);
    }
    public Date parseTime(Date baseDate, String time) throws Exception {
        if (time.equals("Not Set")) {
            throw new Exception("Time is not set");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(baseDate);
        String[] parts = time.split(":");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTime();
    }
    public List<String> getWorkingHoursAsList() {
        if (startHour == null || endHour == null || startHour.equals("Not Set") || endHour.equals("Not Set")) {
            return new ArrayList<>();
        }
        return Arrays.asList(startHour, endHour);
    }
    public ArrayList<Date> getAvailableTimeSlots(Date startDate, int daysRange) {
        ArrayList<Date> availableSlots = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        for (int i = 0; i < daysRange; i++) {
            int dayOfWeek = getDayOfWeekFromDate(calendar.getTime());
            if (!vacationDays.contains(calendar.getTime()) && workingDays.contains(dayOfWeek)) {
                try {
                    Date startTime = parseTime(calendar, startHour);
                    Date endTime = parseTime(calendar, endHour);

                    Calendar slotTime = (Calendar) calendar.clone();
                    slotTime.setTime(startTime);

                    while (slotTime.getTime().before(endTime)) {
                        availableSlots.add(slotTime.getTime());
                        slotTime.add(Calendar.MINUTE, 30);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return availableSlots;
    }

    // 📌 **Helper function: Convert string time (HH:mm) to Date**
    private Date parseTime(Calendar calendar, String time) throws Exception {
        if (time.equals("Not Set")) return null;
        String[] parts = time.split(":");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
        return calendar.getTime();
    }

}
