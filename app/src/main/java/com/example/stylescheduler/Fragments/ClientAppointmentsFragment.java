package com.example.stylescheduler.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stylescheduler.Classes.AppointmentAdapter;
import com.example.stylescheduler.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientAppointmentsFragment extends Fragment implements AppointmentAdapter.OnCancelClickListener {

    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private List<Map<String, String>> appointmentList;
    private FirebaseUser currentUser;
    private TextView tvNoAppointments;

    public ClientAppointmentsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_appointments, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewAppointmentsOfClient);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tvNoAppointments = view.findViewById(R.id.tvNoAppointments);
        appointmentList = new ArrayList<>();
        adapter = new AppointmentAdapter(appointmentList, this); // Pass 'this' as the listener
        recyclerView.setAdapter(adapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            loadAppointments();
        } else {
            Log.e("ClientAppointments", "User not logged in.");
        }

        return view;
    }

    private void loadAppointments() {
        String clientEmail = currentUser.getEmail().replace(".", "_");
        DatabaseReference clientAppointmentsRef = FirebaseDatabase.getInstance()
                .getReference("appointmentsByClient").child(clientEmail);

        clientAppointmentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentList.clear();
                Map<String, String> appointment = new HashMap<>();
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    appointment.clear();
                    appointment.put("date", dateSnapshot.getKey());
                    for (DataSnapshot appointmentSnapshot : dateSnapshot.getChildren()) {
                        appointment.put("appointmentTime", appointmentSnapshot.getKey());
                        appointment.put("barberEmail", appointmentSnapshot.child("barberEmail").getValue(String.class));
                        DatabaseReference barberRef = FirebaseDatabase.getInstance().getReference("barbers")
                                .child(appointment.get("barberEmail").replace(".", "_"));

                        barberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String barberName = snapshot.child("name").getValue(String.class);
                                    String shopAddress = snapshot.child("shopAddress").getValue(String.class);
                                    appointment.put("barberAddress", shopAddress != null ? shopAddress : "Unknown Address");
                                    appointment.put("name", barberName != null ? barberName : "Unknown Name");
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.e("Firebase", "Error fetching barber name", error.toException());
                            }
                        });
                        appointmentList.add(appointment);

                    }

                }

                if (appointmentList.isEmpty()) {
                    tvNoAppointments.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvNoAppointments.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ClientAppointments", "Failed to load appointments", error.toException());
            }
        });
    }

    @Override
    public void onCancelClick(Map<String, String> appointment, int position) {
        if (appointment == null || !appointment.containsKey("barberEmail") || !appointment.containsKey("date") || !appointment.containsKey("appointmentTime")) {
            Toast.makeText(getContext(), "Error: Missing appointment details", Toast.LENGTH_SHORT).show();
            return;
        }

        String barberEmail = appointment.get("barberEmail").replace(".", "_");
        String clientEmail = currentUser.getEmail().replace(".", "_");
        String appointmentDate = appointment.get("date");
        String appointmentTime = appointment.get("appointmentTime");

        new AlertDialog.Builder(getContext())
                .setTitle("Cancel Appointment")
                .setMessage("Are you sure you want to cancel this appointment?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    DatabaseReference barberAppointmentsRef = FirebaseDatabase.getInstance()
                            .getReference("appointments").child(barberEmail).child(appointmentDate).child(appointmentTime);

                    DatabaseReference clientAppointmentsRef = FirebaseDatabase.getInstance()
                            .getReference("appointmentsByClient").child(clientEmail).child(appointmentDate).child(appointmentTime);

                    barberAppointmentsRef.removeValue().addOnSuccessListener(aVoid -> {
                        clientAppointmentsRef.removeValue().addOnSuccessListener(aVoid1 -> {
                            appointmentList.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, appointmentList.size());
                            Toast.makeText(getContext(), "Appointment canceled successfully", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to cancel appointment", Toast.LENGTH_SHORT).show();
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to cancel appointment", Toast.LENGTH_SHORT).show();
                    });
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
