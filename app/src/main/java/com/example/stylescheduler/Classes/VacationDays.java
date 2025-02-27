package com.example.stylescheduler.Classes;

import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class VacationDays {

    private List<String> vacationDates = new ArrayList<>();

    public VacationDays(List<String> vacationDates) {
        this.vacationDates = vacationDates;
    }
    public VacationDays() {

    }


    public List<String> getVacationDates() {
        return vacationDates;
    }

    public void setVacationDates(List<String> vacationDates) {
        this.vacationDates = vacationDates;
    }

    public void remove(int position) {
        vacationDates.remove(position);
        save();
    }

    public void save() {
        FirebaseDatabase.getInstance().getReference("vacationDays")
                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "_"))
                .setValue(this);
    }

    public void add(String date) {
        vacationDates.add(date);
        save();
    }

    public boolean contains(String selectedDate) {
        Log.d("Selected", selectedDate);
        for(String date : vacationDates) {
            Log.d("Date", date);
            if(date.equals(selectedDate))
                return true;
        }
        return vacationDates.contains(selectedDate);
    }
}
