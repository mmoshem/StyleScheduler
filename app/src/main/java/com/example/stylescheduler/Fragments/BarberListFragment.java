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
import com.example.stylescheduler.R;
import com.example.stylescheduler.Adapters.BarberListAdapter;
import java.util.ArrayList;
import java.util.List;

public class BarberListFragment extends Fragment {

    private RecyclerView recyclerView;
    private BarberListAdapter adapter;
    private List<Barber> barberList;

    public BarberListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barber_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewBarbers); // Ensure this ID exists in XML
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize list and adapter
        barberList = new ArrayList<>();
        adapter = new BarberListAdapter(barberList);
        recyclerView.setAdapter(adapter);

        // Load dummy data (Replace with Firebase data fetching logic)
        loadBarbers();

        return view;
    }

    private void loadBarbers() {
        // Corrected Barber Constructor Usage
        barberList.add(new Barber("John Doe", "johndoe@email.com", "password123", "John's Barber Shop", "123 Main St"));
        barberList.add(new Barber("Jane Smith", "janesmith@email.com", "password456", "Jane's Salon", "456 Elm St"));

        adapter.notifyDataSetChanged();
    }
}
