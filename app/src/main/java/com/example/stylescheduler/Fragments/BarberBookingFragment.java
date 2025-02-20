package com.example.stylescheduler.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.stylescheduler.Classes.Barber;
import com.example.stylescheduler.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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

                        Barber barber = snapshot.getValue(Barber.class);
                        if (barber == null) {
                            Log.e("BarberBookingFragment", "Barber object is null");
                        } else {
                            Log.d("BarberBookingFragment", "Barber Name Retrieved: " + barber.getName());
                            tvName.setText(barber.getName());
                            tvAddress.setText(barber.getShopAddress());

                        }
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