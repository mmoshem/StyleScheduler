package com.example.stylescheduler.Fragments;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.stylescheduler.Classes.Barber;
import com.example.stylescheduler.Adapters.BarberAdapter;
import com.example.stylescheduler.Classes.WorkSchedule;
import com.example.stylescheduler.R;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BarberListFragment extends Fragment {

    private RecyclerView recyclerView;
    private BarberAdapter barberAdapter;
    private ArrayList<Barber> barberList;
    private DatabaseReference barbersRef;

    public BarberListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barber_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        barberList = new ArrayList<>();
        barberAdapter = new BarberAdapter(barberList);
        recyclerView.setAdapter(barberAdapter);
        barbersRef = FirebaseDatabase.getInstance().getReference("barbers");
        barbersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                barberList.clear();
                for (DataSnapshot barberSnapshot : snapshot.getChildren()) {
                    Map<String, Object> barberData = (Map<String, Object>) barberSnapshot.getValue();
                    if (barberData != null) {
                        Barber barber = new Barber();
                        barber.setName((String) barberData.get("name"));
                        barber.setPhoneNumber((String) barberData.get("phoneNumber"));
                        barber.setShopAddress((String) barberData.get("shopAddress"));
                        barber.setEmail((String) barberData.get("email"));
//                        if (barber.getWorkSchedule() == null) {
//                            barber.setWorkSchedule(new WorkSchedule());
//                        }

                        barber.setStartHour((String) barberData.get("startHour"));
                        barber.setEndHour((String) barberData.get("endHour"));
                        Object workingDaysObj = barberData.get("workingDays");
                        List<Integer> convertedDays = new ArrayList<>();

                        if (workingDaysObj instanceof List) {
                            for (Object day : (List<?>) workingDaysObj) {
                                if (day instanceof String) {
                                    convertedDays.add(barber.getDayNumber(day.toString()));
                                }
                            }
                            Log.d("BarberListFragment", "Converted working days: " + convertedDays);
                        }

                        barber.setWorkingDays(new ArrayList<>(convertedDays));

                        barberList.add(barber);
                    }
                }
                barberAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        return view;
    }
}
