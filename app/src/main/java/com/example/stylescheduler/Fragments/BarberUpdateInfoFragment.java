package com.example.stylescheduler.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.stylescheduler.Classes.WorkSchedule;
import com.example.stylescheduler.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BarberUpdateInfoFragment extends Fragment {

    private EditText editName, editPhone, editAddress;
    private Spinner spinnerStartHour, spinnerEndHour;
    private CheckBox checkMonday, checkTuesday, checkWednesday, checkThursday, checkFriday, checkSaturday, checkSunday;
    private Button saveButton;
    private DatabaseReference barberRef;
    private FirebaseUser currentUser;

    public BarberUpdateInfoFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_barber_update_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editName = view.findViewById(R.id.editName);
        editPhone = view.findViewById(R.id.editPhone);
        editAddress = view.findViewById(R.id.editAddress);
        spinnerStartHour = view.findViewById(R.id.spinner_time_start);
        spinnerEndHour = view.findViewById(R.id.spinner_time_end);
        saveButton = view.findViewById(R.id.saveButton);

        checkMonday = view.findViewById(R.id.checkMonday);
        checkTuesday = view.findViewById(R.id.checkTuesday);
        checkWednesday = view.findViewById(R.id.checkWednesday);
        checkThursday = view.findViewById(R.id.checkThursday);
        checkFriday = view.findViewById(R.id.checkFriday);
        checkSaturday = view.findViewById(R.id.checkSaturday);
        checkSunday = view.findViewById(R.id.checkSunday);
        //add from 6 to 22 hours
        List<String> hours = List.of("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, hours);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinnerStartHour.setAdapter(adapter);
        spinnerEndHour.setAdapter(adapter);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String safeEmail = currentUser.getEmail().replace(".", "_");
            barberRef = FirebaseDatabase.getInstance().getReference("barbers").child(safeEmail);

            barberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        editName.setText(snapshot.child("name").getValue(String.class));
                        editPhone.setText(snapshot.child("phoneNumber").getValue(String.class));
                        editAddress.setText(snapshot.child("shopAddress").getValue(String.class));

                        String startHour = snapshot.child("startHour").getValue(String.class);
                        String endHour = snapshot.child("endHour").getValue(String.class);
                        if (startHour != null) spinnerStartHour.setSelection(hours.indexOf(startHour));
                        if (endHour != null) spinnerEndHour.setSelection(hours.indexOf(endHour));

                        // טעינת ימי עבודה בצורה בטוחה
                        List<String> workingDays = new ArrayList<>();
                        Object workingDaysObj = snapshot.child("workingDays").getValue();

                        if (workingDaysObj instanceof List) {
//                            // אם זה באמת רשימה, נמיר ישירות
                            workingDays = (List<String>) workingDaysObj;
                        } else if (workingDaysObj instanceof String) {
                            // אם זה מחרוזת, נמיר לרשימה
                            workingDays = new ArrayList<>(List.of(((String) workingDaysObj).split(", ")));
                        }

                        checkMonday.setChecked(workingDays.contains("Monday"));
                        checkTuesday.setChecked(workingDays.contains("Tuesday"));
                        checkWednesday.setChecked(workingDays.contains("Wednesday"));
                        checkThursday.setChecked(workingDays.contains("Thursday"));
                        checkFriday.setChecked(workingDays.contains("Friday"));
                        checkSaturday.setChecked(workingDays.contains("Saturday"));
                        checkSunday.setChecked(workingDays.contains("Sunday"));
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        saveButton.setOnClickListener(v -> updateBarberInfo());
    }

    private void updateBarberInfo() {
        String newName = editName.getText().toString().trim();
        String newPhone = editPhone.getText().toString().trim();
        String newAddress = editAddress.getText().toString().trim();
        String selectedStartHour = spinnerStartHour.getSelectedItem().toString();
        String selectedEndHour = spinnerEndHour.getSelectedItem().toString();

        if (newName.isEmpty() || newPhone.isEmpty() || newAddress.isEmpty()) {
            Toast.makeText(getContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String safeEmail = currentUser.getEmail().replace(".", "_");
        barberRef = FirebaseDatabase.getInstance().getReference("barbers").child(safeEmail);

        // ✅ מחיקת כל הנתונים הישנים כדי למנוע כפילויות
        barberRef.child("workingDays").removeValue();
        barberRef.child("workingHours").removeValue();
        barberRef.child("workSchedule").removeValue();

        // ✅ יצירת רשימה של ימי עבודה
        Set<Integer> selectedDays = getSelectedDays();
        List<String> workingDaysStringList = new ArrayList<>();
        WorkSchedule workSchedule = new WorkSchedule();

        for (Integer day : selectedDays) {
            workingDaysStringList.add(workSchedule.getDayName(day));
        }

        // ✅ יצירת מבנה מסודר לשעות עבודה
        Map<String, Object> workScheduleMap = new HashMap<>();
        workScheduleMap.put("workingDays", workingDaysStringList);
        workScheduleMap.put("workingHours", selectedStartHour + " - " + selectedEndHour);

        // ✅ שמירת כל הנתונים בפיירבייס בצורה מסודרת
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", newName);
        updates.put("phoneNumber", newPhone);
        updates.put("shopAddress", newAddress);
        updates.put("workSchedule", workScheduleMap); // ✅ שמירה תחת `workSchedule` בלבד

        barberRef.updateChildren(updates).addOnSuccessListener(aVoid -> {
            Toast.makeText(getContext(), "Updated successfully!", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).navigate(R.id.action_barberUpdateInfoFragment_to_barberHomePage);
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to update", Toast.LENGTH_SHORT).show();
        });
    }


    private Set<Integer> getSelectedDays() {
        Set<Integer> selectedDays = new HashSet<>();
        if (checkMonday.isChecked()) selectedDays.add(Calendar.MONDAY);
        if (checkTuesday.isChecked()) selectedDays.add(Calendar.TUESDAY);
        if (checkWednesday.isChecked()) selectedDays.add(Calendar.WEDNESDAY);
        if (checkThursday.isChecked()) selectedDays.add(Calendar.THURSDAY);
        if (checkFriday.isChecked()) selectedDays.add(Calendar.FRIDAY);
        if (checkSaturday.isChecked()) selectedDays.add(Calendar.SATURDAY);
        if (checkSunday.isChecked()) selectedDays.add(Calendar.SUNDAY);
        return selectedDays;
    }
}














//package com.example.stylescheduler.Fragments;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.*;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.navigation.Navigation;
//import com.example.stylescheduler.R;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.*;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class BarberUpdateInfoFragment extends Fragment {
//
//    private EditText editName, editPhone, editAddress;
//    private Spinner spinnerStartHour, spinnerEndHour;
//    private Button saveButton;
//    private DatabaseReference barberRef;
//    private FirebaseUser currentUser;
//
//    public BarberUpdateInfoFragment() {}
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_barber_update_info, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        editName = view.findViewById(R.id.editName);
//        editPhone = view.findViewById(R.id.editPhone);
//        editAddress = view.findViewById(R.id.editAddress);
//        spinnerStartHour = view.findViewById(R.id.spinner_time_start);
//        spinnerEndHour = view.findViewById(R.id.spinner_time_end);
//        saveButton = view.findViewById(R.id.saveButton);
//
//        // יצירת רשימת שעות לבחירה
//        List<String> hours = List.of("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00");
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, hours);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerStartHour.setAdapter(adapter);
//        spinnerEndHour.setAdapter(adapter);
//
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String safeEmail = currentUser.getEmail().replace(".", "_");
//            barberRef = FirebaseDatabase.getInstance().getReference("barbers").child(safeEmail);
//
//            // טעינת הנתונים מה-Firebase בעת פתיחת המסך
//            barberRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.exists()) {
//                        if (snapshot.child("name").exists()) {
//                            editName.setText(snapshot.child("name").getValue(String.class));
//                        }
//                        if (snapshot.child("phoneNumber").exists()) {
//                            editPhone.setText(snapshot.child("phoneNumber").getValue(String.class));
//                        }
//                        if (snapshot.child("shopAddress").exists()) {
//                            editAddress.setText(snapshot.child("shopAddress").getValue(String.class));
//                        }
//
//                        // טעינת שעות עבודה
//                        String startHour = snapshot.child("startHour").getValue(String.class);
//                        String endHour = snapshot.child("endHour").getValue(String.class);
//
//                        if (startHour != null && hours.contains(startHour)) {
//                            spinnerStartHour.setSelection(hours.indexOf(startHour));
//                        }
//                        if (endHour != null && hours.contains(endHour)) {
//                            spinnerEndHour.setSelection(hours.indexOf(endHour));
//                        }
//                    } else {
//                        Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//
//        saveButton.setOnClickListener(v -> updateBarberInfo());
//    }
//
//    private void updateBarberInfo() {
//        if (currentUser == null) {
//            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String safeEmail = currentUser.getEmail().replace(".", "_");
//        barberRef = FirebaseDatabase.getInstance().getReference("barbers").child(safeEmail);
//
//        // קבלת הנתונים מהשדות
//        String newName = editName.getText().toString().trim();
//        String newPhone = editPhone.getText().toString().trim();
//        String newAddress = editAddress.getText().toString().trim();
//        String selectedStartHour = spinnerStartHour.getSelectedItem().toString();
//        String selectedEndHour = spinnerEndHour.getSelectedItem().toString();
//
//        if (newName.isEmpty() || newPhone.isEmpty() || newAddress.isEmpty()) {
//            Toast.makeText(getContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // יצירת HashMap עם הנתונים לעדכון
//        Map<String, Object> updates = new HashMap<>();
//        updates.put("name", newName);
//        updates.put("phoneNumber", newPhone);
//        updates.put("shopAddress", newAddress);
//        updates.put("startHour", selectedStartHour);
//        updates.put("endHour", selectedEndHour);
//
//        // עדכון הנתונים בפיירבייס
//        barberRef.updateChildren(updates).addOnSuccessListener(aVoid -> {
//            Toast.makeText(getContext(), "Updated successfully!", Toast.LENGTH_SHORT).show();
//            Navigation.findNavController(requireView()).navigate(R.id.action_barberUpdateInfoFragment_to_barberHomePage);
//        }).addOnFailureListener(e -> {
//            Toast.makeText(getContext(), "Failed to update", Toast.LENGTH_SHORT).show();
//        });
//    }
//}

//package com.example.stylescheduler.Fragments;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.*;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.navigation.Navigation;
//import com.example.stylescheduler.R;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.*;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class BarberUpdateInfoFragment extends Fragment {
//
//    private EditText editName, editPhone, editAddress;
//    private Spinner spinnerStartHour, spinnerEndHour;
//    private CheckBox checkMonday, checkTuesday, checkWednesday, checkThursday, checkFriday, checkSaturday, checkSunday;
//    private Button saveButton;
//    private DatabaseReference barberRef;
//    private FirebaseUser currentUser;
//
//    public BarberUpdateInfoFragment() {}
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_barber_update_info, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        editName = view.findViewById(R.id.editName);
//        editPhone = view.findViewById(R.id.editPhone);
//        editAddress = view.findViewById(R.id.editAddress);
//        spinnerStartHour = view.findViewById(R.id.spinner_time_start);
//        spinnerEndHour = view.findViewById(R.id.spinner_time_end);
//
//        checkMonday = view.findViewById(R.id.checkMonday);
//        checkTuesday = view.findViewById(R.id.checkTuesday);
//        checkWednesday = view.findViewById(R.id.checkWednesday);
//        checkThursday = view.findViewById(R.id.checkThursday);
//        checkFriday = view.findViewById(R.id.checkFriday);
//        checkSaturday = view.findViewById(R.id.checkSaturday);
//        checkSunday = view.findViewById(R.id.checkSunday);
//
//        saveButton = view.findViewById(R.id.saveButton);
//
//        // רשימת שעות קבועות
//        List<String> hours = List.of("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00");
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, hours);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerStartHour.setAdapter(adapter);
//        spinnerEndHour.setAdapter(adapter);
//
//        // טעינת הנתונים מה-Firebase
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String safeEmail = currentUser.getEmail().replace(".", "_");
//            barberRef = FirebaseDatabase.getInstance().getReference("barbers").child(safeEmail);
//
//            barberRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    editName.setText(snapshot.child("name").getValue(String.class));
//                    editPhone.setText(snapshot.child("phoneNumber").getValue(String.class));
//                    editAddress.setText(snapshot.child("shopAddress").getValue(String.class));
//                    spinnerStartHour.setSelection(hours.indexOf(snapshot.child("startHour").getValue(String.class)));
//                    spinnerEndHour.setSelection(hours.indexOf(snapshot.child("endHour").getValue(String.class)));
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {}
//            });
//        }
//
//        saveButton.setOnClickListener(v -> updateBarberInfo());
//    }
//
//    private void updateBarberInfo() {
//        barberRef.child("startHour").setValue(spinnerStartHour.getSelectedItem().toString());
//        barberRef.child("endHour").setValue(spinnerEndHour.getSelectedItem().toString());
//        Toast.makeText(getContext(), "Updated successfully!", Toast.LENGTH_SHORT).show();
//    }
//}
