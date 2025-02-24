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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ClientHomePage extends Fragment {

    private TabLayout tabLayout;

    public ClientHomePage() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_home_page, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);

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
}



