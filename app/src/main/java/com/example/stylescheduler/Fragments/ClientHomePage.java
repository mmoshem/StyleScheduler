package com.example.stylescheduler.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.stylescheduler.R;
import com.google.android.material.tabs.TabLayout;

public class ClientHomePage extends Fragment {

    private TabLayout tabLayout;

    public ClientHomePage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_home_page, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                switch (tab.getPosition()) {
                    case 0:
                        transaction.replace(R.id.fragmentContainerView, new BarberListFragment()).commit();
                        break;
                    case 1:
                        transaction.replace(R.id.fragmentContainerView, new ClientAppointments()).commit();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Load default fragment
        if (savedInstanceState == null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, new BarberListFragment())
                    .commit();
        }

        return view;
    }
}
