package com.example.stylescheduler.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stylescheduler.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class BarberHomePage extends Fragment {

    private TextView textViewName;
    private CalendarView calendarView;
    private RecyclerView recyclerViewAppointments;
    private DatabaseReference barberRef;
    private FirebaseUser currentUser;

    public BarberHomePage() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barber_home_page, container, false);

        textViewName = view.findViewById(R.id.textViewname);
        calendarView = view.findViewById(R.id.calendarView);
        recyclerViewAppointments = view.findViewById(R.id.recyclerViewAppointments);

        Button button = view.findViewById(R.id.btn_update_info);
        button.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_barberHomePage_to_barberUpdateInfoFragment));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            loadBarberInfo();
            loadBarberWorkingHours();
        }

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
    private List<Integer> workingDays = new ArrayList<>();

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

                // שליפת ימי העבודה מרשימת מספרים (נניח שהם נשמרו כמחרוזת "1,3,5")
                String workingDaysStr = snapshot.child("workingDays").getValue(String.class);

                if (workingDaysStr != null) {
                    workingDays.clear(); // ניקוי רשימה קודמת
                    for (String day : workingDaysStr.split(",")) {
                        try {
                            workingDays.add(Integer.parseInt(day.trim())); // המרה לרשימה של מספרים
                        } catch (NumberFormatException e) {
                            Log.e("Firebase", "⚠️ Invalid day format: " + day);
                        }
                    }
                    Log.d("Firebase", "📅 Barber's working days: " + workingDays);
                } else {
                    Log.d("Firebase", "⚠️ No working days found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "❌ Failed to load working days: " + error.getMessage());
            }
        });
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

                // שליפת שעות העבודה ישירות מהספר
                String startHour = snapshot.child("startHour").getValue(String.class);
                String endHour = snapshot.child("endHour").getValue(String.class);

                if (startHour != null && endHour != null) {
                    List<String> timeSlots = generateTimeSlots(startHour, endHour);

                    // בדיקה והדפסה של השעות
                    for (String slot : timeSlots) {
                        Log.d("Firebase", "🕒 Time Slot: " + slot);
                    }
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
}
