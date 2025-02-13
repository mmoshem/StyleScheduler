package com.example.stylescheduler.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.stylescheduler.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegFragment newInstance(String param1, String param2) {
        RegFragment fragment = new RegFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reg, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find the RadioGroup and EditText views
        RadioGroup rgAccountType = view.findViewById(R.id.rg_account_type);
        final EditText etWorkAddress = view.findViewById(R.id.et_work_address);

        // Set the listener to toggle the visibility of the workplace address field
        rgAccountType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // If the worker radio button is selected, show the address field; otherwise hide it
                if (checkedId == R.id.rb_worker) {
                    etWorkAddress.setVisibility(View.VISIBLE);
                } else {
                    etWorkAddress.setVisibility(View.GONE);
                }
            }
        });
    }
}