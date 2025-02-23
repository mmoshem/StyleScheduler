package com.example.stylescheduler.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stylescheduler.Classes.AppointmentAdapter;
import com.example.stylescheduler.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientAppointmentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvNoAppointments;
    private AppointmentAdapter adapter;
    private List<Map<String, String>> appointmentList = new ArrayList<>();
    private DatabaseReference appointmentsRef;
    private FirebaseUser currentUser;

    public ClientAppointmentsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_appointments, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewAppointmentsOfClient);
        tvNoAppointments = view.findViewById(R.id.tvNoAppointments);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AppointmentAdapter(appointmentList);
        recyclerView.setAdapter(adapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            loadClientAppointments();
        }

        return view;
    }

    private void loadClientAppointments() {
        String customerEmail = currentUser.getEmail().replace(".", "_");
        appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");

        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentList.clear();
                for (DataSnapshot barberSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot dateSnapshot : barberSnapshot.getChildren()) {
                        for (DataSnapshot timeSnapshot : dateSnapshot.getChildren()) {
                            String customer = timeSnapshot.child("customerEmail").getValue(String.class);
                            if (customer != null && customer.equals(customerEmail)) {
                                Map<String, String> appointmentData = (Map<String, String>) timeSnapshot.getValue();
                                appointmentList.add(appointmentData);
                            }
                        }
                    }
                }

                if (appointmentList.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    tvNoAppointments.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoAppointments.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ClientAppointments", "❌ Failed to load appointments", error.toException());
            }
        });
    }
}
