package com.example.stylescheduler.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.tabs.TabLayout;
import com.example.stylescheduler.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ClientHomePage extends Fragment {

    private TabLayout tabLayout;
    private FirebaseUser currentUser;
    String safeEmail;


    public ClientHomePage() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_home_page, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            safeEmail = currentUser.getEmail().replace(".", "_");
            deleteExpiredAppointments();
        }
        // ברירת מחדל: הצגת רשימת הספרים
//        replaceFragment(new BarberListFragment());

        // האזנה ללחיצות על טאבים
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
                if (tab.getPosition() == 0) {
                    navController.navigate(R.id.barberListFragment); // Navigate properly
                } else {
                    navController.navigate(R.id.clientAppointmentsFragment2); // Navigate properly
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }
    private void deleteExpiredAppointments() {
        if (currentUser == null) return;
        String safeEmail = currentUser.getEmail().replace(".", "_");
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance()
                .getReference("appointmentsByClient")
                .child(safeEmail);

        DatabaseReference barbersRef = FirebaseDatabase.getInstance()
                .getReference("appointments");

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();


        //  SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        appointmentsRef.get().addOnSuccessListener(dataSnapshot -> {
            if (!dataSnapshot.exists()) return;

            for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                String currDate = dateSnapshot.getKey();

                for(DataSnapshot timeSnapshot : dateSnapshot.getChildren()){
                    String timeStr = timeSnapshot.getKey();
                    String currDateTimeStr = currDate + " " + timeStr;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    try {
                        Date targetDateTime = dateFormat.parse(currDateTimeStr);
                        if (now.after(targetDateTime)) {
                            appointmentsRef.child(currDate).child(timeStr).removeValue();
                            barbersRef.get().addOnSuccessListener(barberSnapshot -> {
                                if (barberSnapshot.exists()) {
                                    for (DataSnapshot barber : barberSnapshot.getChildren()) {
                                        DatabaseReference barbersAppointmentRef = barbersRef.child(barber.getKey()).child(currDate).child(timeStr);
                                        barbersAppointmentRef.removeValue();
                                    }
                                }
                            });
                        }

                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}



