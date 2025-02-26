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
    List<String> hours = List.of("07:00","08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00","19:00","20:00","21:00");

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, hours);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
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

                        List<String> workingDays = new ArrayList<>();
                        Object workingDaysObj = snapshot.child("workingDays").getValue();
                        if (workingDaysObj instanceof List) {
                            workingDays = (List<String>) workingDaysObj;
                        } else if (workingDaysObj instanceof String) {
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
        spinnerStartHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validateStartHour();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerEndHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validateEndHour();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        saveButton.setOnClickListener(v -> updateBarberInfo());
    }
    private void validateEndHour() {
        String selectedStartHour = spinnerStartHour.getSelectedItem().toString();
        String selectedEndHour = spinnerEndHour.getSelectedItem().toString();

        int startIndex = hours.indexOf(selectedStartHour);
        int endIndex = hours.indexOf(selectedEndHour);

        if (endIndex < startIndex) {
            Toast.makeText(getContext(), "End hour must be later than start hour!", Toast.LENGTH_SHORT).show();
            spinnerEndHour.setSelection(startIndex);
        }
    }
    private void validateStartHour() {
        String selectedStartHour = spinnerStartHour.getSelectedItem().toString();
        String selectedEndHour = spinnerEndHour.getSelectedItem().toString();

        int startIndex = hours.indexOf(selectedStartHour);
        int endIndex = hours.indexOf(selectedEndHour);

        if (startIndex > endIndex) {
            Toast.makeText(getContext(), "Start hour must be earlier than end hour!", Toast.LENGTH_SHORT).show();
            spinnerStartHour.setSelection(endIndex);
        }
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

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", newName);
        updates.put("phoneNumber", newPhone);
        updates.put("shopAddress", newAddress);
        updates.put("startHour", selectedStartHour);
        updates.put("endHour", selectedEndHour);

        Set<Integer> selectedDays = getSelectedDays();
        List<String> workingDaysStringList = new ArrayList<>();
        WorkSchedule workSchedule = new WorkSchedule();
        for (Integer day : selectedDays) {
            workingDaysStringList.add(workSchedule.getDayName(day));
        }
        updates.put("workingDays", workingDaysStringList);

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
