package com.example.stylescheduler.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.stylescheduler.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EditShopFragment extends Fragment {

    private EditText etShopName, etShopAddress;
    private CheckBox cbMonday, cbTuesday, cbWednesday, cbThursday, cbFriday, cbSaturday, cbSunday;
    private Button btnSave, btnSickDay;
    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private String barberId;

    public EditShopFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_shop, container, false);

        // Initialize UI components
        etShopName = view.findViewById(R.id.et_shop_name);
        etShopAddress = view.findViewById(R.id.et_shop_address);
        cbMonday = view.findViewById(R.id.cb_monday);
        cbTuesday = view.findViewById(R.id.cb_tuesday);
        cbWednesday = view.findViewById(R.id.cb_wednesday);
        cbThursday = view.findViewById(R.id.cb_thursday);
        cbFriday = view.findViewById(R.id.cb_friday);
        cbSaturday = view.findViewById(R.id.cb_saturday);
        cbSunday = view.findViewById(R.id.cb_sunday);
        btnSave = view.findViewById(R.id.btn_save);
        btnSickDay = view.findViewById(R.id.btn_sick_day);

        mAuth = FirebaseAuth.getInstance();
        barberId = mAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference("barbers").child(barberId);

        // Load existing data from Firebase
        loadBarberData();

        // Save changes button
        btnSave.setOnClickListener(v -> saveBarberData());

        // Announce sick day (sets today as a sick day)
        btnSickDay.setOnClickListener(v -> announceSickDay());

        return view;
    }

    private void loadBarberData() {
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Load shop name & address
                    if (snapshot.child("shopName").exists()) {
                        etShopName.setText(snapshot.child("shopName").getValue(String.class));
                    }
                    if (snapshot.child("shopAddress").exists()) {
                        etShopAddress.setText(snapshot.child("shopAddress").getValue(String.class));
                    }

                    // Load working days
                    if (snapshot.child("workingDays").exists()) {
                        List<String> workingDays = (List<String>) snapshot.child("workingDays").getValue();
                        if (workingDays != null) {
                            cbMonday.setChecked(workingDays.contains("Monday"));
                            cbTuesday.setChecked(workingDays.contains("Tuesday"));
                            cbWednesday.setChecked(workingDays.contains("Wednesday"));
                            cbThursday.setChecked(workingDays.contains("Thursday"));
                            cbFriday.setChecked(workingDays.contains("Friday"));
                            cbSaturday.setChecked(workingDays.contains("Saturday"));
                            cbSunday.setChecked(workingDays.contains("Sunday"));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveBarberData() {
        String shopName = etShopName.getText().toString().trim();
        String shopAddress = etShopAddress.getText().toString().trim();

        // Get selected working days
        List<String> workingDays = new ArrayList<>();
        if (cbMonday.isChecked()) workingDays.add("Monday");
        if (cbTuesday.isChecked()) workingDays.add("Tuesday");
        if (cbWednesday.isChecked()) workingDays.add("Wednesday");
        if (cbThursday.isChecked()) workingDays.add("Thursday");
        if (cbFriday.isChecked()) workingDays.add("Friday");
        if (cbSaturday.isChecked()) workingDays.add("Saturday");
        if (cbSunday.isChecked()) workingDays.add("Sunday");

        // Save to Firebase
        databaseRef.child("shopName").setValue(shopName);
        databaseRef.child("shopAddress").setValue(shopAddress);
        databaseRef.child("workingDays").setValue(workingDays);

        Toast.makeText(getContext(), "Shop details updated!", Toast.LENGTH_SHORT).show();
    }

    private void announceSickDay() {
        long currentTimeMillis = System.currentTimeMillis();
        databaseRef.child("sickDays").push().setValue(currentTimeMillis);
        Toast.makeText(getContext(), "Sick day announced!", Toast.LENGTH_SHORT).show();
    }
}
