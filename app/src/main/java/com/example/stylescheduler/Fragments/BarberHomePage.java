package com.example.stylescheduler.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stylescheduler.Adapters.ClientAppointmentsAdapter;
import com.example.stylescheduler.Classes.Appointment;
import com.example.stylescheduler.Classes.Appointment;
import com.example.stylescheduler.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class BarberHomePage extends Fragment {

    private RecyclerView recyclerViewAppointments;
    private ClientAppointmentsAdapter adapter;
    private List<Appointment> appointmentList;
    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;

    public BarberHomePage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barber_home_page, container, false);

        recyclerViewAppointments = view.findViewById(R.id.recyclerViewAppointments);
        recyclerViewAppointments.setLayoutManager(new LinearLayoutManager(getContext()));

        Button btnEditShop = view.findViewById(R.id.btn_edit_shop);
        btnEditShop.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_barberHomePage_to_editShopFragment));

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid()).child("appointments");

        appointmentList = new ArrayList<>();
        adapter = new ClientAppointmentsAdapter(appointmentList);
        recyclerViewAppointments.setAdapter(adapter);

        loadAppointments();

        return view;
    }

    private void loadAppointments() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Appointment appointment = data.getValue(Appointment.class);
                    appointmentList.add(appointment);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading appointments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBarberAppointments() {
        FirebaseDatabase.getInstance().getReference("appointments")
                .orderByChild("barberId").equalTo(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        appointmentList.clear();
                        for (DataSnapshot appointmentSnapshot : snapshot.getChildren()) {
                            Appointment appointment = appointmentSnapshot.getValue(Appointment.class);
                            appointmentList.add(appointment);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("BarberHomePage", "Failed to load appointments: " + error.getMessage());
                    }
                });
    }

}
