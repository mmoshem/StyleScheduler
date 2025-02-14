package com.example.stylescheduler.Classes;

import android.view.InputQueue;

import java.util.*;

public class Barber extends User {
    private String shopName;
    private String shopAddress;
    private WorkSchedule workSchedule;



    public Barber(int userID, String name, String email, String password, String shopName, String shopAddress, String i_PhoneNumber) {
        super(userID, name, email, password, "barber", i_PhoneNumber);
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.workSchedule = new WorkSchedule();
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getName() {
        return this.name;
    }

    public String getShopAddress() {
        return shopAddress;
    }
    public String getPhoneNumber(){
        return m_PhoneNumber;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public WorkSchedule getWorkSchedule() {
        return workSchedule;
    }

    public void setWorkSchedule(WorkSchedule workSchedule) {
        WorkSchedule w = new WorkSchedule();
        ArrayList<Integer> l= new ArrayList<>();
        l.add(8);
        l.add(9);
        w.setWorkingHours(1,l);
        this.workSchedule = workSchedule;
    }




    // 📌 **קבלת זמני עבודה פנויים - שימוש ב-Date במקום LocalDate**
    public ArrayList<Date> getAvailableAppointments(Date startDate, int daysRange) {
        return workSchedule.getAvailableTimeSlots(startDate, daysRange);
    }

    // 📌 **הוספת חופשה - שימוש ב-Date במקום LocalDate**
    public void addVacation(Date date) {
        workSchedule.addVacationDay(date);
    }

    // 📌 **הוספת תור חדש**
    public void bookAppointment(Appointment appointment) {
        workSchedule.addAppointment(appointment);
    }

    // 📌 **ביטול תור (למשל, אם הספר חולה)**
    public void cancelAppointment(Appointment appointment) {
        workSchedule.cancelAppointment(appointment);
    }

    // 📌 **עדכון ימי ושעות עבודה מבלי לפגוע בתורים קיימים (תיקון ל-API 24)**
    public void updateWorkingDaysAndHours(List<Integer> newWorkingDays, List<Integer> workHours) {
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
    private int getDayOfWeekFromDate(Date date) {
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

    @Override
    public void register() {
        // להוסיף בהמשך אינטגרציה עם Firebase אם צריך
    }

    @Override
    public boolean login() {
        return false; // בהמשך נוסיף לוגיקה לחיבור
    }


}


    /*
    public void setWorkingDays(List<String> workingDays) {
       this.workingDays = workingDays;

    public void setAvailableTimeSlots(List<LocalDateTime> availableTimeSlots) {
        this.availableTimeSlots = availableTimeSlots;
    }

    public void viewScheduledAppointments(List<Appointment> appointments) {
        for (Appointment appointment : appointments) {
            if (appointment.getBarber().equals(this)) {
                System.out.println("Appointment for customer: " + appointment.getCustomer().name + " at " + appointment.getAppointmentTime());
            }
        }
    }

    @Override
    public void register() {

    }

    @Override
    public boolean login() {
        return false;
    }
}
*/
