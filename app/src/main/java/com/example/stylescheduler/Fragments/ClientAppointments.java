package com.example.stylescheduler.Fragments;

import android.os.Bundle;
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
        loadAppointments();

        return view;
    }

    private void loadAppointments() {
        // Creating dummy barber and customer
        Barber barber1 = new Barber("John Doe", "john@example.com", "password", "John's Barber Shop", "123 Main St");
        Customer customer1 = new Customer("Alice", "alice@example.com", "password", "1234567890");

        Barber barber2 = new Barber("Jane Smith", "jane@example.com", "password", "Jane's Cuts", "456 Elm St");
        Customer customer2 = new Customer("Bob", "bob@example.com", "password", "9876543210");

        // Creating appointment objects with proper parameters
        // Creating appointment objects with proper parameter order (Barber first, then Customer)
        // Creating appointment objects with proper parameter order (Barber first, then Customer)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            appointmentList.add(new Appointment(1, barber1, customer1, "Haircut",
                    LocalDateTime.of(2024, 3, 12, 15, 0)));
            appointmentList.add(new Appointment(2, barber2, customer2, "Beard Trim",
                    LocalDateTime.of(2024, 3, 14, 10, 0)));
        } else {
            // Handle older Android versions (Convert to another format, e.g., using Strings or Date)
            // Example: Save as String or use Calendar API
        }


        adapter.notifyDataSetChanged();
    }

}
