package com.example.stylescheduler.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.stylescheduler.Classes.VacationDays;
import com.example.stylescheduler.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;


public class VacationDaysDialog extends DialogFragment {

    private VacationDays vacationDates;

    public VacationDaysDialog(VacationDays vacationDates) {
        if(vacationDates == null)
            this.vacationDates = new VacationDays();
        else
            this.vacationDates = vacationDates;
    }


    private Dialog d;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vacation_days_dialog, container, false);
    }

    private ListView lv;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lv = view.findViewById(R.id.vacationDaysLv);
        lv.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.vacation_spinner, vacationDates.getVacationDates()));

        lv.setOnItemLongClickListener((parent, view1, position, id) -> {
            // remove the item from the list in the db..?
            vacationDates.remove(position);
            Toast.makeText(requireContext(), "Date removed", Toast.LENGTH_SHORT).show();
            ((ArrayAdapter) lv.getAdapter()).notifyDataSetChanged();
            return true;
        });

        Button calendarBtn = view.findViewById(R.id.cv);
        Calendar c = Calendar.getInstance();
        calendarBtn.setOnClickListener(v -> {
            d = new DatePickerDialog(requireContext(), (view12, year, month, dayOfMonth) -> {
                String date = dayOfMonth + "-" + (month + 1) + "-" + year;

                // cancel all appointments for this date
                deleteAllAppointmentsForDay(date);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
            d.show();
        });
    }

    private void deleteAllAppointmentsForDay(String date) {

        DatabaseReference barberAppointmentsRef = FirebaseDatabase.getInstance()
                .getReference("appointments")
                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "_"))
                .child(date);

        DatabaseReference customerAppointmentsRef = FirebaseDatabase.getInstance()
                .getReference("appointmentsByClient");

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete all appointments")
                .setMessage("Are you sure you want to delete all appointments for this day?")
                .setPositiveButton("YES", (dialog, which) -> {
                    vacationDates.add(date);
                    ((ArrayAdapter) lv.getAdapter()).notifyDataSetChanged();
                    d.dismiss();
                    barberAppointmentsRef.get().addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot appointment : dataSnapshot.getChildren()) {
                                String time = appointment.getKey();
                                String customerEmail = appointment.child("customerEmail").getValue(String.class);

                                if (customerEmail != null) {
                                    String safeCustomerEmail = customerEmail.replace(".", "_");

                                    DatabaseReference customerAppointment = customerAppointmentsRef
                                            .child(safeCustomerEmail)
                                            .child(date)
                                            .child(time);
                                    customerAppointment.removeValue();
                                }
                            }

                            barberAppointmentsRef.removeValue().addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "כל התורים נמחקו בהצלחה!", Toast.LENGTH_SHORT).show();


                            }).addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error deleting appointments!", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Toast.makeText(getContext(), "No appointments for this day!", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
