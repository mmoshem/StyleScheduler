package com.example.stylescheduler.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.stylescheduler.Classes.Appointment;
import com.example.stylescheduler.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import java.time.LocalDateTime;
import java.util.Calendar;

public class BookingFragment extends Fragment {

    private Button btnSelectDate, btnSelectTime, btnConfirm;
    private Spinner spinnerServiceType;
    private String barberId, customerId, selectedService;
    private LocalDateTime selectedDateTime = null;

    public BookingFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        btnSelectDate = view.findViewById(R.id.btnSelectDate);
        btnSelectTime = view.findViewById(R.id.btnSelectTime);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        spinnerServiceType = view.findViewById(R.id.spinnerServiceType);

        // Retrieve Barber ID from arguments
        if (getArguments() != null) {
            barberId = getArguments().getString("barberId");
        } else {
            Toast.makeText(getContext(), "Error: No Barber ID", Toast.LENGTH_SHORT).show();
            return view;  // Prevent further execution
        }

        // Retrieve logged-in customer ID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            customerId = user.getUid();
        } else {
            Toast.makeText(getContext(), "Error: Please log in again", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Capture selected service type
        spinnerServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedService = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedService = "Haircut"; // Default service
            }
        });

        btnSelectDate.setOnClickListener(v -> openDatePicker());
        btnSelectTime.setOnClickListener(v -> openTimePicker());
        btnConfirm.setOnClickListener(v -> bookAppointment());

        return view;
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    if (selectedDateTime == null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            selectedDateTime = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0);
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            selectedDateTime = selectedDateTime.withYear(year).withMonth(month + 1).withDayOfMonth(dayOfMonth);
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        btnSelectDate.setText(selectedDateTime.toLocalDate().toString());
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }

    private void openTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePicker = new TimePickerDialog(getContext(),
                (view, hourOfDay, minute) -> {
                    if (selectedDateTime == null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            selectedDateTime = LocalDateTime.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            selectedDateTime = selectedDateTime.withHour(hourOfDay).withMinute(minute);
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        btnSelectTime.setText(selectedDateTime.toLocalTime().toString());
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePicker.show();
    }

    private void bookAppointment() {
        if (selectedDateTime == null || selectedService == null) {
            Toast.makeText(getContext(), "Select a Service, Date & Time", Toast.LENGTH_SHORT).show();
            return;
        }

        String appointmentId = FirebaseDatabase.getInstance().getReference().push().getKey();
        if (appointmentId == null) {
            Toast.makeText(getContext(), "Error: Failed to generate appointment ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert LocalDateTime to String before saving
        String formattedDateTime = selectedDateTime.toString();

        // Create appointment object
        new Appointment(appointmentId, barberId, customerId, selectedService, selectedDateTime);

        if (selectedDateTime != null && selectedService != null) {
            String appointmentId = FirebaseDatabase.getInstance().getReference().push().getKey();

            // Create appointment object with correct data types
            Appointment appointment  = new Appointment(appointmentId, barberId, customerId, selectedService, selectedDateTime);

            FirebaseDatabase.getInstance().getReference("appointments")
                    .child(appointmentId).setValue(appointment)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Appointment Booked!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error: Could not book appointment", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Select a Service, Date & Time", Toast.LENGTH_SHORT).show();
        }

    }

}
