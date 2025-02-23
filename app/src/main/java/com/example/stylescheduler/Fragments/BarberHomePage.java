package com.example.stylescheduler.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stylescheduler.Classes.AvailableAppointmentsAdapter;
import com.example.stylescheduler.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class BarberHomePage extends Fragment {

    private TextView textViewName;
    private CalendarView calendarView;
    private RecyclerView recyclerViewAvailableAppointments;
    private DatabaseReference barberRef;
    private FirebaseUser currentUser;
    private AvailableAppointmentsAdapter adapter;
    private List<String> availableAppointments = new ArrayList<>();
    private List<Integer> workingDays = new ArrayList<>();
    private String selectedDate;  // תאריך שנבחר מהלוח שנה

    public BarberHomePage() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barber_home_page, container, false);

        textViewName = view.findViewById(R.id.textViewname);
        calendarView = view.findViewById(R.id.calendarView);
        recyclerViewAvailableAppointments = view.findViewById(R.id.recyclerViewAppointments);

        Button button = view.findViewById(R.id.btn_update_info);
        button.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_barberHomePage_to_barberUpdateInfoFragment));

        // הגדרת ה-RecyclerView
        recyclerViewAvailableAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AvailableAppointmentsAdapter(availableAppointments, timeSlot -> {
            Log.d("RecyclerView", "🕒 Clicked time slot: " + timeSlot);
        });
        recyclerViewAvailableAppointments.setAdapter(adapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            loadBarberInfo();
            loadWorkingDays();
        }

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);

            int selectedDayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK) - 1; // Android מחזיר 1 = Sunday, 2 = Monday וכו'

            Log.d("Calendar", "📅 Selected date: " + dayOfMonth + "/" + (month + 1) + "/" + year + " (Day: " + selectedDayOfWeek + ")");
            Log.d("Calendar", "📆 Barber's working days: " + workingDays);

            if (!workingDays.contains(selectedDayOfWeek)) {
                Log.w("Calendar", "⛔ הספר לא עובד ביום הזה!"); // 🛑 הודעה ב-WARNING כדי להדגיש
                Toast.makeText(getContext(), "📅 הספר לא עובד ביום הזה!", Toast.LENGTH_SHORT).show();
                recyclerViewAvailableAppointments.setVisibility(View.GONE);

            } else {
                Log.i("Calendar", "✅ הספר עובד ביום הזה!"); // ✅ הודעה כדי לראות שהיום נמצא ברשימה
                Toast.makeText(getContext(), "✅ הספר עובד ביום הזה!", Toast.LENGTH_SHORT).show();
                recyclerViewAvailableAppointments.setVisibility(View.VISIBLE);
                loadBarberWorkingHours();
            }
        });

        return view;

    }

    private void loadBarberInfo() {
        String safeEmail = currentUser.getEmail().replace(".", "_");
        barberRef = FirebaseDatabase.getInstance().getReference("barbers").child(safeEmail);

        barberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String barberName = snapshot.child("name").getValue(String.class);
                    textViewName.setText("Hello, " + barberName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "❌ Failed to load barber info: " + error.getMessage());
            }
        });
    }
    private void loadWorkingDays() {
        if (currentUser == null) return;

        String safeEmail = currentUser.getEmail().replace(".", "_");
        barberRef = FirebaseDatabase.getInstance().getReference("barbers").child(safeEmail);

        barberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.d("Firebase", "⚠️ No barber found.");
                    return;
                }

                workingDays.clear(); // ננקה את הרשימה

                Object data = snapshot.child("workingDays").getValue();
                Log.d("Firebase", "📂 Data from Firebase: " + data); // 🟢 לוג לבדיקה

                if (data instanceof List) {
                    List<?> daysList = (List<?>) data;
                    for (Object item : daysList) {
                        Log.d("Firebase", "📆 Raw item: " + item); // 🟢 לוג לבדיקה

                        if (item instanceof Long) {
                            int dayNumber = ((Long) item).intValue();
                            workingDays.add(dayNumber);
                            Log.d("Firebase", "✅ Added numeric day: " + dayNumber);
                        } else if (item instanceof String) {
                            int dayNum = convertDayNameToNumber(item.toString().trim());
                            if (dayNum != -1) {
                                workingDays.add(dayNum);
                                Log.d("Firebase", "✅ Converted and added day: " + dayNum);
                            } else {
                                Log.e("Firebase", "⚠️ Invalid day format: " + item);
                            }
                        }
                    }
                    Log.d("Firebase", "📅 Barber's working days (Processed): " + workingDays);
                } else {
                    Log.d("Firebase", "⚠️ No valid working days format found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "❌ Failed to load working days: " + error.getMessage());
            }
        });
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
            default: return -1; // ערך לא תקין
        }
    }


    private void loadBarberWorkingHours() {
        if (currentUser == null) return;

        String safeEmail = currentUser.getEmail().replace(".", "_");
        barberRef = FirebaseDatabase.getInstance().getReference("barbers").child(safeEmail);

        barberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.d("Firebase", "⚠️ No barber found in database.");
                    return;
                }

                // שליפת שעות העבודה
                String startHour = snapshot.child("startHour").getValue(String.class);
                String endHour = snapshot.child("endHour").getValue(String.class);

                if (startHour != null && endHour != null) {
                    List<String> timeSlots = generateTimeSlots(startHour, endHour);
                    availableAppointments.clear();
                    availableAppointments.addAll(timeSlots);
                    adapter.notifyDataSetChanged();

                    Log.d("Firebase", "✅ Loaded available appointments: " + availableAppointments);
                } else {
                    Log.d("Firebase", "⚠️ startHour or endHour is missing.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "❌ Failed to load barber details: " + error.getMessage());
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
                calendar.add(Calendar.HOUR, 1);  // מחלק את שעות העבודה לתורים של שעה
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeSlots;
    }

    private int getDayOfWeek(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // התאמת ימי השבוע (באנדרואיד: ראשון = 1, שבת = 7)
        return dayOfWeek - 1; // כך שהשבוע יתחיל מ-0 = ראשון
    }
}
