package com.example.stylescheduler.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.stylescheduler.Classes.Appointment;
import com.example.stylescheduler.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;

public class BookingFragment extends Fragment {

    private Button btnSelectDate, btnSelectTime, btnConfirm;
    private String barberId, customerId, selectedDateTime;

    public BookingFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        btnSelectDate = view.findViewById(R.id.btnSelectDate);
        btnSelectTime = view.findViewById(R.id.btnSelectTime);
        btnConfirm = view.findViewById(R.id.btnConfirm);

        if (getArguments() != null) {
            barberId = getArguments().getString("barberId");
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            customerId = user.getUid();
        }

        btnSelectDate.setOnClickListener(v -> openDatePicker());
        btnSelectTime.setOnClickListener(v -> openTimePicker());
        btnConfirm.setOnClickListener(v -> bookAppointment());

        return view;
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime = year + "-" + (month + 1) + "-" + dayOfMonth;
                    btnSelectDate.setText(selectedDateTime);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void openTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePicker = new TimePickerDialog(getContext(),
                (view, hourOfDay, minute) -> {
                    selectedDateTime += " " + hourOfDay + ":" + minute;
                    btnSelectTime.setText(hourOfDay + ":" + minute);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePicker.show();
    }

    private void bookAppointment() {
        if (selectedDateTime != null) {
            String appointmentId = FirebaseDatabase.getInstance().getReference().push().getKey();
            Appointment appointment = new Appointment(appointmentId, barberId, customerId, selectedDateTime);
            FirebaseDatabase.getInstance().getReference("appointments").child(appointmentId).setValue(appointment);

            Toast.makeText(getContext(), "Appointment Booked!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Select Date & Time", Toast.LENGTH_SHORT).show();
        }
    }
}
