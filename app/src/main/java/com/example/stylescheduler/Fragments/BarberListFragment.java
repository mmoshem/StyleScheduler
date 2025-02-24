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
import com.example.stylescheduler.Classes.BarberAdapter;
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

        // שליפת רשימת הספרים מפיירבייס
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
                        if (barber.getWorkSchedule() == null) {
                            barber.setWorkSchedule(new WorkSchedule()); // Initialize if null
                        }


                        barber.getWorkSchedule().setStartHour((String) barberData.get("startHour"));
                        barber.getWorkSchedule().setEndHour((String) barberData.get("endHour"));

                        Object workingDaysObj = barberData.get("workingDays");
                        List<Integer> convertedDays = new ArrayList<>();

                        if (workingDaysObj instanceof List) {
                            // Convert List<String> to List<Integer>
                            for (Object day : (List<?>) workingDaysObj) {
                                if (day instanceof String) {
                                    convertedDays.add(barber.getWorkSchedule().getDayNumber(day.toString()));
                                }
                            }
                            Log.d("BarberListFragment", "Converted working days: " + convertedDays);
//                            List<String> s =barber.getWorkSchedule().getWorkingDays(convertedDays);

//                            Log.d(s.toString(), "onDataChange: ");
                        }

                        barber.getWorkSchedule().setWorkingDays(new ArrayList<>(convertedDays)); // Convert List to ArrayList
                        Log.d("BarberListFragment", "Barber schedule set: " + barber.getWorkSchedule().getWorkingDays());
                        Log.d("Barberemail", "email:" + barber.getEmail());

                        barberList.add(barber);
                    }
                }
                barberAdapter.notifyDataSetChanged(); // עדכון הנתונים ברשימה
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        return view;
    }
}


//package com.example.stylescheduler.Fragments;
//
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.example.stylescheduler.R;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link BarberListFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class BarberListFragment extends Fragment {
//
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public BarberListFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment BarberListFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static BarberListFragment newInstance(String param1, String param2) {
//        BarberListFragment fragment = new BarberListFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_barber_list, container, false);
//    }
//}