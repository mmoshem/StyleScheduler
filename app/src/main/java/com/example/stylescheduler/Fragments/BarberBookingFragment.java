package com.example.stylescheduler.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stylescheduler.Classes.Barber;
import com.example.stylescheduler.Classes.WorkSchedule;
import com.example.stylescheduler.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BarberBookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BarberBookingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private DatabaseReference barberRef;
    private FirebaseUser currentUser;

    public BarberBookingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BarberBookingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BarberBookingFragment newInstance(String param1, String param2) {
        BarberBookingFragment fragment = new BarberBookingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barber_booking, container, false);
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setMinDate(System.currentTimeMillis()); // Disable past dates
        calendarView.setMaxDate(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 14));//count of milliseconds in 14 days

        TextView tvName = view.findViewById(R.id.textViewBarberName);
        TextView tvAddress = view.findViewById(R.id.textViewBarberAddress);

        if (getArguments() != null) {
            String barberEmail = getArguments().getString("barberEmail");
            Log.d("BarberBookingFragment", "Received barberEmail: " + barberEmail);

            if (barberEmail != null) {
                String safeEmail = barberEmail.replace(".", "_");
                Log.d("BarberBookingFragment", "Safe email for Firebase: " + safeEmail);

                barberRef = FirebaseDatabase.getInstance().getReference("barbers").child(safeEmail);
                barberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("BarberBookingFragment", "Snapshot received: " + snapshot.getValue());
                        if (!snapshot.exists()) {
                            Log.e("BarberBookingFragment", "No barber found for email: " + safeEmail);
                            return;
                        }
                        Map<String, Object> barberData = (Map<String, Object>) snapshot.getValue();
                        Barber barber = new Barber();
                        List<Integer> convertedDays = new ArrayList<>();
                        if (barberData != null) {
                            barber.setName((String) barberData.get("name"));
                            barber.setShopAddress((String) barberData.get("shopAddress"));
                            Object workingDaysObj = barberData.get("workingDays");


                            if (workingDaysObj instanceof List) {
                                // Convert List<String> to List<Integer>
                                for (Object day : (List<?>) workingDaysObj) {
                                    if (day instanceof String) {
                                        convertedDays.add(barber.getDayNumber(day.toString()));
                                    }
                                }
                                Log.d("BarberListFragment", "Converted working days: " + convertedDays);
//                            List<String> s =barber.getWorkSchedule().getWorkingDays(convertedDays);

//                            Log.d(s.toString(), "onDataChange: ");
                            }
                            if (barber.getWorkSchedule() == null) {
                                barber.setWorkSchedule(new WorkSchedule()); // Initialize if null
                            }
                            tvName.setText(barber.getName());
                            tvAddress.setText(barber.getShopAddress());

                        }
                            Log.d("BarberBookingFragment", "Barber Name Retrieved: " + barber.getName());


                            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                                @Override
                                public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                                    Calendar calendar = Calendar.getInstance();
                                    long today = calendar.getTimeInMillis();
                                    Calendar selectedDate = Calendar.getInstance();
                                    selectedDate.set(year, month, dayOfMonth);
                                    int selectedDayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK); // Get the day number (1=Sunday, 2=Monday, etc.)
                                    Log.d("BarberBookingFragment", "Selected day of week: " + barber.getDayName(selectedDayOfWeek));
                                    Log.d("BarberBookingFragment", "barber days: " + barber.getWorkingDays());

                                    if (convertedDays.contains(selectedDayOfWeek)) {
                                        // ✅ Allow selection
                                        Toast.makeText(getContext(), "✅ Barber works on this day!", Toast.LENGTH_SHORT).show();


                                    }
                                    else {
                                        // ❌ Prevent selection by resetting to the previous valid date
                                        calendarView.setDate(today, true, true);
                                        Toast.makeText(getContext(), "❌ Barber does not work on this day!", Toast.LENGTH_SHORT).show();
                                    }

                                    // Month is 0-based (January = 0, February = 1, ...)
//                                    String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

                                    // Show the selected date in a Toast message
//                                    Toast.makeText(getApplicationContext(), "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();
                                }
                            });

                        }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("BarberBookingFragment", "Error reading barber data", error.toException());
                    }
                });
            } else {
                Log.e("BarberBookingFragment", "barberEmail is null in Bundle");
            }
        } else {
            Log.e("BarberBookingFragment", "getArguments() returned null");
        }
        return view;
    }
}