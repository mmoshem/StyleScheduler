package com.example.stylescheduler.Classes;

import java.util.*;

public class Barber extends User {
    private String shopAddress;
    private WorkSchedule workSchedule;

    public WorkSchedule getWorkSchedule() {
        return workSchedule;
    }
    public Barber(String userID, String name, String email, String password,String shopAddress,String phoneNumber) {
        super(userID, name, email, password, "barber",phoneNumber);
        this.shopAddress = shopAddress;
        this.workSchedule = new WorkSchedule();
    }


    public Barber() {
        super();
        this.workSchedule = new WorkSchedule();
    }
    // 📌 **הוספת חופשה - שימוש ב-Date במקום LocalDate**
    public String getName() { return this.name; }
    public String getPhoneNumber() { return this.phoneNumber; }
    public String getShopAddress() { return this.shopAddress; }

    public String getWorkingDays() { return workSchedule.getWorkingDaysString(); }
    public String getWorkingHours() { return workSchedule.getWorkingHours(); }

    public List<String> getWorkingHours() { return workSchedule.getWorkingHoursAsList(); }
    public String getAStringOfWorkingHours() {
        List<String> s = getWorkingHours();
        return s.get(0) + " - " + s.get(1);
    }


    }
    public void setWorkSchedule(WorkSchedule workSchedule) {
        this.workSchedule = workSchedule;
    }

    public void updateWorkSchedule(List<Integer> days, String startHour, String endHour) {
        workSchedule.setWorkingDays(new ArrayList<>(days));
        workSchedule.setWorkingHours(startHour, endHour);
    }
    public int getDayNumber(String day) {
        return workSchedule.getDayNumber(day);
    }
    public String getRole(){
        return this.role;
    }
    public int getDayOfWeekFromDate(Date date){
        return workSchedule.getDayOfWeekFromDate(date);
    }
   public String getDayName(int day){
        return workSchedule.getDayName(day);
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

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public void updateWorkingDaysAndHours(List<Integer> newWorkingDays, String startHour, String endHour) {
        List<Integer> daysWithAppointments = new ArrayList<>();

        for (Appointment appointment : workSchedule.getBookedAppointments()) {
            int dayOfWeek = getDayOfWeekFromDate(appointment.getAppointmentDate());
            daysWithAppointments.add(dayOfWeek);
        }

        // Clear the previous work schedule
        workSchedule.clearSchedule();

        // Keep old schedule for days with existing appointments
        List<Integer> updatedDays = new ArrayList<>();
        for (int day : newWorkingDays) {
            if (daysWithAppointments.contains(day)) {
                updatedDays.add(day);
                System.out.println("⚠️ Warning: Existing appointments on day " + day + ". Keeping previous schedule.");
            } else {
                updatedDays.add(day);
            }
        }

        workSchedule.setWorkingDays(new ArrayList<>(updatedDays)); // Now using List<Integer>
        workSchedule.setWorkingHours(startHour, endHour);
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