package com.example.stylescheduler.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylescheduler.Classes.Barber;
import com.example.stylescheduler.Classes.Customer;
import com.example.stylescheduler.R;
import com.example.stylescheduler.Adapters.ClientAppointmentsAdapter;
import com.example.stylescheduler.Classes.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClientAppointments extends Fragment {

    private RecyclerView recyclerView;
    private ClientAppointmentsAdapter adapter;
    private List<Appointment> appointmentList;

    public ClientAppointments() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_appointments, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewAppointments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize list and adapter
        appointmentList = new ArrayList<>();
        adapter = new ClientAppointmentsAdapter(appointmentList);
        recyclerView.setAdapter(adapter);

        // Load dummy data (Replace with Firebase data fetching logic)

        return view;
    }

    private void loadAppointments() {
        FirebaseDatabase.getInstance().getReference("appointments")
                .orderByChild("customerId").equalTo(FirebaseAuth.getInstance().getUid())
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
                        Log.e("ClientAppointments", "Failed to load appointments: " + error.getMessage());
                    }
                });
    }




}
