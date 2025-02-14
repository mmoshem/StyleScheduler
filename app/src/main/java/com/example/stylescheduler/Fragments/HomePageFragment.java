package com.example.stylescheduler.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.stylescheduler.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomePageFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("users");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        Button btRegister = view.findViewById(R.id.buttonRegistration);
        Button btLogIn = view.findViewById(R.id.buttonLogIn);

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_regFragment);
            }
        });

        btLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserRole(view);
            }
        });

        return view;
    }

    private void checkUserRole(View view) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String role = snapshot.child("role").getValue(String.class);
                        Log.d("HomePageFragment", "User role: " + role);
                        if ("barber".equals(role)) {
                            Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_barberHomePage);
                        } else {
                            Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_clientHomePage);
                        }
                    } else {
                        Log.e("HomePageFragment", "User role not found in database.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("HomePageFragment", "Database error: " + error.getMessage());
                }
            });
        } else {
            Log.e("HomePageFragment", "No user logged in.");
        }
    }
}
