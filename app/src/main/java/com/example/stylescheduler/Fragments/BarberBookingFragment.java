package com.example.stylescheduler.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stylescheduler.Classes.AvailableAppointmentsAdapter;
import com.example.stylescheduler.Classes.Barber;
import com.example.stylescheduler.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class BarberBookingFragment extends Fragment {

    private DatabaseReference barberRef;
    private FirebaseUser currentUser;
    private RecyclerView recyclerViewAvailableAppointments;
    private AvailableAppointmentsAdapter adapter;
    private List<String> availableAppointments = new ArrayList<>();
    private String barberEmail;
    private String selectedDate;
    private List<Integer> workingDays = new ArrayList<>();

    public BarberBookingFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barber_booking, container, false);

        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setMinDate(System.currentTimeMillis()); // Disable past dates
        calendarView.setMaxDate(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 14));

        TextView tvName = view.findViewById(R.id.textViewBarberName);
        TextView tvAddress = view.findViewById(R.id.textViewBarberAddress);
        recyclerViewAvailableAppointments = view.findViewById(R.id.recyclerViewClientAppointments);
        recyclerViewAvailableAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AvailableAppointmentsAdapter(availableAppointments, this::confirmAppointment);
        recyclerViewAvailableAppointments.setAdapter(adapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (getArguments() != null) {
            barberEmail = getArguments().getString("barberEmail");

            if (barberEmail != null) {
                String safeEmail = barberEmail.replace(".", "_");
                barberRef = FirebaseDatabase.getInstance().getReference("barbers").child(safeEmail);

                barberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
//                            Log.e("BarberBookingFragment", "No barber found for email: " + safeEmail);
                            return;
                        }
                        Barber barber = snapshot.getValue(Barber.class);
                        if (barber != null) {
                            tvName.setText(barber.getName());
                            tvAddress.setText(barber.getShopAddress());

                            // טוענים את ימי העבודה של הספר
                            Object workingDaysObj = snapshot.child("workingDays").getValue();
                            workingDays.clear(); // חשוב לנקות לפני הטענה חדשה

                            if (workingDaysObj instanceof List) {
                                List<?> daysList = (List<?>) workingDaysObj;
                                for (Object item : daysList) {
                                    if (item instanceof Long) {
                                        workingDays.add(((Long) item).intValue());
                                    } else if (item instanceof String) {
                                        int dayNum = convertDayNameToNumber(item.toString().trim());
                                        if (dayNum != -1) {
                                            workingDays.add(dayNum);
                                        }
                                    }
                                }
                                Log.d("BarberBookingFragment", "📅 Barber's working days: " + workingDays); // נוסיף הדפסה
                            } else {
                                Log.e("BarberBookingFragment", "⚠️ workingDays is not a valid list");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("BarberBookingFragment", "Error reading barber data", error.toException());
                    }
                });
                calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.set(year, month, dayOfMonth);
                    int selectedDayOfWeek = selectedCal.get(Calendar.DAY_OF_WEEK) - 1; // 0=Sunday, 1=Monday...

                    selectedDate = dayOfMonth + "-" + (month + 1) + "-" + year;
                    Log.d("BarberBookingFragment", "Selected date: " + selectedDate);
                if (!workingDays.contains(selectedDayOfWeek)) {
                    Log.w("BarberBookingFragment", " Barber does not work on this day!");
                    Toast.makeText(getContext(), "⛔ Barber does not work on this day!", Toast.LENGTH_SHORT).show();
                    recyclerViewAvailableAppointments.setVisibility(View.GONE);
                } else {
                    Log.i("BarberBookingFragment", "✅ הספר עובד ביום הזה!");
                    recyclerViewAvailableAppointments.setVisibility(View.VISIBLE);
                    loadAvailableTimeSlots(barberEmail, selectedDate);
                }
            });
            } else {
                Log.e("BarberBookingFragment", "barberEmail is null in Bundle");
            }
        } else {
            Log.e("BarberBookingFragment", "getArguments() returned null");
        }
        return view;
    }
    private int convertDayNameToNumber(String dayName) {
        switch (dayName.toLowerCase()) {
            case "sunday": return 0;
            case "monday": return 1;
            case "tuesday": return 2;
            case "wednesday": return 3;
            case "thursday": return 4;
            case "friday": return 5;
            case "saturday": return 6;
            default: return -1;
        }
    }


    private void loadAvailableTimeSlots(String barberEmail, String selectedDate) {
        String safeEmail = barberEmail.replace(".", "_");
        DatabaseReference barberRef = FirebaseDatabase.getInstance().getReference("barbers").child(safeEmail);
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments").child(safeEmail).child(selectedDate);

        barberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.e("BarberBookingFragment", "❌ Barber not found.");
                    return;
                }

                String startHour = snapshot.child("startHour").getValue(String.class);
                String endHour = snapshot.child("endHour").getValue(String.class);

                if (startHour == null || endHour == null) {
                    Log.e("BarberBookingFragment", "❌ Working hours missing.");
                    return;
                }

                List<String> availableTimeSlots = generateTimeSlots(startHour, endHour);

                appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot appointmentsSnapshot) {
                        List<String> bookedAppointments = new ArrayList<>();
                        for (DataSnapshot appointmentSnapshot : appointmentsSnapshot.getChildren()) {
                            String time = appointmentSnapshot.child("appointmentTime").getValue(String.class);
                            if (time != null) {
                                bookedAppointments.add(time);
                            }
                        }
                        if (bookedAppointments.size() >= availableTimeSlots.size()) {
                            Toast.makeText(getContext(), "there is no empty appointments left", Toast.LENGTH_SHORT).show();
                        }
//                        else{
//                            Toast.makeText(getContext(), "horray there steel an empty appointment ", Toast.LENGTH_SHORT).show();
//                        }
                        availableTimeSlots.removeAll(bookedAppointments);
                        updateRecyclerView(availableTimeSlots);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("BarberBookingFragment", "❌ Failed to load appointments.");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BarberBookingFragment", "❌ Failed to load barber info.");
            }
        });
    }

    private List<String> generateTimeSlots(String startHour, String endHour) {
        List<String> timeSlots = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        try {
            Date startTime = sdf.parse(startHour);
            Date endTime = sdf.parse(endHour);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startTime);

            while (calendar.getTime().before(endTime)) {
                timeSlots.add(sdf.format(calendar.getTime()));
                calendar.add(Calendar.HOUR, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeSlots;
    }

    private void updateRecyclerView(List<String> availableTimeSlots) {
        availableAppointments.clear();
        availableAppointments.addAll(availableTimeSlots);
        adapter.notifyDataSetChanged();
        recyclerViewAvailableAppointments.setVisibility(availableTimeSlots.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void confirmAppointment(String selectedTimeSlot) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Appointment")
                .setMessage("Book an appointment at " + selectedTimeSlot + "?")
                .setPositiveButton("Yes", (dialog, which) -> bookAppointment(selectedTimeSlot))
                .setNegativeButton("No", null)
                .show();
    }
//    private void cancelAppointment(String selectedTimeSlot) {
//        if (currentUser == null || barberEmail == null || selectedDate == null) {
//            Log.e("BarberBookingFragment", "❌ Missing user or barber info.");
//            return;
//        }
//
//        String customerEmail = currentUser.getEmail().replace(".", "_");
//        String barberSafeEmail = barberEmail.replace(".", "_");
//
//        DatabaseReference appointmentRef = FirebaseDatabase.getInstance().getReference("appointments")
//                .child(barberSafeEmail).child(selectedDate).child(selectedTimeSlot);
//        DatabaseReference clientAppointmentRef = FirebaseDatabase.getInstance().getReference("appointmentsByClient")
//                .child(customerEmail).child(selectedDate).child(selectedTimeSlot);
//
//        clientAppointmentRef.removeValue().addOnSuccessListener(aVoid -> {
//            Log.d("CancelAppointment", "✅ התור נמחק אצל הלקוח");
//            appointmentRef.removeValue().addOnSuccessListener(aVoid1 -> {
//                Log.d("CancelAppointment", "✅ התור נמחק אצל הספר");
//                returnTimeSlotToAvailability(barberSafeEmail, selectedDate, selectedTimeSlot);
//            }).addOnFailureListener(e -> Log.e("CancelAppointment", "❌ שגיאה במחיקת התור אצל הספר", e));
//        }).addOnFailureListener(e -> Log.e("CancelAppointment", "❌ שגיאה במחיקת התור אצל הלקוח", e));
//    }
//
//    private void returnTimeSlotToAvailability(String barberEmail, String selectedDate, String timeSlot) {
//        DatabaseReference availableSlotsRef = FirebaseDatabase.getInstance()
//                .getReference("appointments").child(barberEmail).child(selectedDate);
//        availableSlotsRef.child(timeSlot).setValue("available")
//                .addOnSuccessListener(aVoid -> {
//                    Log.d("CancelAppointment", "✅ השעה נוספה מחדש לרשימת הזמינות של הספר");
//                    availableAppointments.add(timeSlot);
//                    adapter.notifyDataSetChanged();
//                })
//                .addOnFailureListener(e -> Log.e("CancelAppointment", "❌ שגיאה בהוספת השעה לרשימה הזמינות", e));
//    }

    private void bookAppointment(String selectedTimeSlot) {
        if (currentUser == null || barberEmail == null || selectedDate == null) {
            Log.e("BarberBookingFragment", "❌ Missing user or barber info.");
            Toast.makeText(getContext(), "❌ Unable to book appointment. Try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String customerEmail = currentUser.getEmail().replace(".", "_");
        String barberSafeEmail = barberEmail.replace(".", "_");

        DatabaseReference appointmentRef = FirebaseDatabase.getInstance().getReference("appointments")
                .child(barberSafeEmail).child(selectedDate).child(selectedTimeSlot);

        DatabaseReference clientAppointmentRef = FirebaseDatabase.getInstance().getReference("appointmentsByClient")
                .child(customerEmail).child(selectedDate).child(selectedTimeSlot);

        Map<String, Object> appointmentData = new HashMap<>();
        appointmentData.put("appointmentTime", selectedTimeSlot);
        appointmentData.put("barberEmail", barberEmail);
        appointmentData.put("customerEmail", customerEmail);
        appointmentData.put("status", "booked");

        // שמירה גם תחת `appointments` וגם תחת `appointmentsByClient`
        appointmentRef.setValue(appointmentData);
        clientAppointmentRef.setValue(appointmentData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "✅ Appointment booked at " + selectedTimeSlot, Toast.LENGTH_SHORT).show();
                    Log.d("BarberBookingFragment", "✅ Appointment booked successfully.");

                    // עדכון הרשימה כך שהתור שנבחר ייעלם
                    availableAppointments.remove(selectedTimeSlot);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("BarberBookingFragment", "❌ Failed to book appointment.", e);
                    Toast.makeText(getContext(), "❌ Failed to book appointment. Try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
