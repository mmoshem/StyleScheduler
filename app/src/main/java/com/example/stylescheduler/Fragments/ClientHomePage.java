package com.example.stylescheduler.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.stylescheduler.R;
import com.google.android.material.tabs.TabLayout;

public class ClientHomePage extends Fragment {

    private TabLayout tabLayout;

    public ClientHomePage() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_home_page, container, false);
        tabLayout = view.findViewById(R.id.tabLayout);

        // Load BarberListFragment by default
        if (savedInstanceState == null) {
            loadFragment(new BarberListFragment());
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                switch (tab.getPosition()) {
                    case 0:
                        loadFragment(new BarberListFragment());
                        break;
                    case 1:
                        loadFragment(new ClientAppointments());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .commit();
    }
}
