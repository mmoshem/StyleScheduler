package com.example.stylescheduler.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stylescheduler.Adapters.BarberListAdapter;
import com.example.stylescheduler.Classes.Barber;
import com.example.stylescheduler.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class BarberListFragment extends Fragment {

    private RecyclerView recyclerView;
    private BarberListAdapter adapter;
    private List<Barber> barberList;

    public BarberListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barber_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewBarbers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        barberList = new ArrayList<>();
        adapter = new BarberListAdapter(barberList);
        recyclerView.setAdapter(adapter);

        loadBarbers();

        return view;
    }

    private void loadBarbers() {
        FirebaseDatabase.getInstance().getReference("users")
                .orderByChild("role").equalTo("barber")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        barberList.clear();
                        for (DataSnapshot barberSnapshot : snapshot.getChildren()) {
                            Barber barber = barberSnapshot.getValue(Barber.class);
                            if (barber != null) {
                                barber.setId(barberSnapshot.getKey());
                                barberList.add(barber);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("BarberListFragment", "Failed to load barbers: " + error.getMessage());
                    }
                });
    }
}
