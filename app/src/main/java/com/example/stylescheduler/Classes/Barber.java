package com.example.stylescheduler.Classes;

import java.util.*;

public class Barber extends User {
    private String shopName;
    private String shopAddress;
    private WorkSchedule workSchedule;
    private String phoneNumber;


    public Barber(String userID, String name, String email, String password, String shopName, String shopAddress) {
        super(userID, name, email, password, "barber");
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.workSchedule = new WorkSchedule();
    }

    // 📌 **הוספת חופשה - שימוש ב-Date במקום LocalDate**
    public void addVacation(Date date) {
        workSchedule.addVacationDay(date);
    }
    public String getName(){
        return this.name;
    }
    public String getShopName() {
        return this.shopName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getShopAddress() {
        return this.shopAddress;
    }

    public String getWorkingDays() {
        return workSchedule.getWorkingDays(); // נקבל את הימים שהספר עובד
    }

    public String getWorkingHours() {
        return workSchedule.getWorkingHours(); // נקבל את שעות העבודה
    }

    // 📌 **הוספת תור חדש**
    public void bookAppointment(Appointment appointment) {
        workSchedule.addAppointment(appointment);
    }
    // 📌 **קבלת זמני עבודה פנויים**
    public Date getNextAvailableAppointment() {
        ArrayList<Date> availableAppointments = workSchedule.getAvailableTimeSlots(new Date(), 14);
        if (!availableAppointments.isEmpty()) {
            return availableAppointments.get(0); // מחזיר את התור הקרוב ביותר
        }
        return null; // אם אין תורים פנויים
    }

    // 📌 **ביטול תור (למשל, אם הספר חולה)**
    public void cancelAppointment(Appointment appointment) {
        workSchedule.cancelAppointment(appointment);
    }

    // 📌 **עדכון ימי ושעות עבודה מבלי לפגוע בתורים קיימים (תיקון ל-API 24)**
    public void updateWorkingDaysAndHours(ArrayList<Integer> newWorkingDays, ArrayList<Integer> workHours) {
        // קבלת כל התורים הקיימים כדי לוודא שהם לא נמחקים
        Set<Integer> daysWithAppointments = new HashSet<>();

        for (Appointment appointment : workSchedule.getBookedAppointments()) {
            Date appointmentTime = appointment.getAppointmentDate();
            if (appointmentTime != null) {
                int dayOfWeek = getDayOfWeekFromDate(appointmentTime); // שימוש בפונקציה חדשה עם Calendar
                daysWithAppointments.add(dayOfWeek);
            } else {
                System.out.println("⚠️ Warning: Found an appointment with null time!");
            }
        }

        // מחיקת שעות ישנות והוספת שעות עבודה חדשות
        workSchedule.clearWorkingHours();
        for (int day : newWorkingDays) {
            if (daysWithAppointments.contains(day)) {
                System.out.println("⚠️ Warning: You have existing appointments on day " + day + ". Keeping old schedule for this day.");
                continue; // לא מעדכן ימים שיש בהם תורים
            }
            workSchedule.setWorkingHours(day, workHours);
        }
    }

    // 📌 **פונקציה חדשה לקבלת היום בשבוע מ-Date באמצעות Calendar**
    public int getDayOfWeekFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK); // 1 = Sunday, 2 = Monday, ..., 7 = Saturday
    }

    // 📌 **צפייה בכל התורים שנקבעו לספר**
    public void viewScheduledAppointments() {
        ArrayList<Appointment> bookedAppointments = workSchedule.getBookedAppointments();
        System.out.println("Appointments for " + this.name + ":");
        for (Appointment appointment : bookedAppointments) {
            System.out.println("📅 " + appointment.getAppointmentDate() + " - " + appointment.getCustomer().name);
        }
    }
}