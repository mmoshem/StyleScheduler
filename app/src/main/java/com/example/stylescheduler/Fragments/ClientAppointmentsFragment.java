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

import com.example.stylescheduler.Classes.Appointment;
import com.example.stylescheduler.Classes.AppointmentAdapter;
import com.example.stylescheduler.Classes.Barber;
import com.example.stylescheduler.R;
import com.google.android.gms.tasks.OnSuccessListener;
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

    public ClientAppointmentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_appointments, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewAppointmentsOfClient);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tvNoAppointments = view.findViewById(R.id.tvNoAppointments);
        appointmentList = new ArrayList<>();
        adapter = new AppointmentAdapter(appointmentList, this);
        recyclerView.setAdapter(adapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            loadAppointments();
        } else {
            Log.e("ClientAppointments", "User not logged in.");
        }

        return view;
    }

    private HashMap<String, Barber> barbers = new HashMap<>();
    private void loadAppointments() {
        String clientEmail = currentUser.getEmail().replace(".", "_");
        DatabaseReference clientAppointmentsRef = FirebaseDatabase.getInstance()
                .getReference("appointmentsByClient")
                .child(clientEmail);



        DatabaseReference barberRef = FirebaseDatabase.getInstance()
                .getReference("barbers");


        barberRef.get()
            .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Barber barber = new Barber();
                        String bName = snapshot.child("name").getValue(String.class);
                        String bEmail = snapshot.child("email").getValue(String.class);
                        barber.setEmail(bEmail);
                        String shopAddress = snapshot.child("shopAddress").getValue(String.class);
                        barber.setName(bName);
                        barber.setShopAddress(shopAddress);
                        barbers.put(barber.getEmail().replace(".", "_"), barber);
                    }

                    // now we can show the appointments

                    clientAppointmentsRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            appointmentList.clear();

                            for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                                String dateKey = dateSnapshot.getKey();

                                for (DataSnapshot appointmentSnapshot : dateSnapshot.getChildren()) {
                                    // Create a new map for EACH appointment
                                    Map<String, String> appointment = new HashMap<>();

                                    //Appointment ap = appointmentSnapshot.getValue(Appointment.class);

                                    // Basic info
                                    appointment.put("date", dateKey);
                                    appointment.put("appointmentTime", appointmentSnapshot.getKey());

                                    String barberEmail = appointmentSnapshot.child("barberEmail").getValue(String.class);
                                    if (barberEmail == null) barberEmail = "Unknown Email";
                                    appointment.put("barberEmail", barberEmail);

                                    // Add the appointment to the list right away
                                    appointmentList.add(appointment);

                                    Barber b = barbers.get(barberEmail.replace(".", "_"));

                                    // Update the map that is already in appointmentList
                                    appointment.put("name",b != null ? b.getName() : "Unknown Name");
                                    appointment.put("barberAddress", b != null ? b.getShopAddress() : "Unknown Address");

                                    // Notify that data changed
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            // Show/hide "No Appointments" message
                            if (appointmentList.isEmpty()) {
                                tvNoAppointments.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                tvNoAppointments.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }

                            // Initial notify so the list shows placeholders (date/time/email)
                            // Detailed barber info will come in asynchronously.
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("ClientAppointments", "Failed to load appointments", error.toException());
                        }
                    });
                }
            });


    }

    @Override
    public void onCancelClick(Map<String, String> appointment, int position) {
        if (appointment == null
                || !appointment.containsKey("barberEmail")
                || !appointment.containsKey("date")
                || !appointment.containsKey("appointmentTime")) {
            Toast.makeText(getContext(), "Error: Missing appointment details", Toast.LENGTH_SHORT).show();
            return;
        }

        String barberEmail = appointment.get("barberEmail").replace(".", "_");
        String clientEmail = currentUser.getEmail().replace(".", "_");
        String appointmentDate = appointment.get("date");
        String appointmentTime = appointment.get("appointmentTime");

        new AlertDialog.Builder(requireContext())
                .setTitle("Cancel Appointment")
                .setMessage("Are you sure you want to cancel this appointment?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    DatabaseReference barberAppointmentsRef = FirebaseDatabase.getInstance()
                            .getReference("appointments")
                            .child(barberEmail)
                            .child(appointmentDate)
                            .child(appointmentTime);

                    DatabaseReference clientAppointmentsRef = FirebaseDatabase.getInstance()
                            .getReference("appointmentsByClient")
                            .child(clientEmail)
                            .child(appointmentDate)
                            .child(appointmentTime);

                    barberAppointmentsRef.removeValue().addOnSuccessListener(aVoid -> {
                        clientAppointmentsRef.removeValue().addOnSuccessListener(aVoid1 -> {

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
