package com.example.stylescheduler.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.stylescheduler.Classes.Barber;
import com.example.stylescheduler.Classes.Customer;
import com.example.stylescheduler.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class RegFragment extends Fragment {

    private EditText etName, etEmail, etPassword, etPhone, etWorkAddress;
    private RadioGroup rgAccountType;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    public RegFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reg, container, false);

        etName = view.findViewById(R.id.et_name);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        etPhone = view.findViewById(R.id.et_phone);
        etWorkAddress = view.findViewById(R.id.et_work_address);
        rgAccountType = view.findViewById(R.id.rg_account_type);
        btnRegister = view.findViewById(R.id.btn_register);

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("users");

        btnRegister.setOnClickListener(v -> registerUser(view));

        return view;
    }

    private void registerUser(View view) {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String workAddress = etWorkAddress.getText().toString().trim();
        boolean isBarber = rgAccountType.getCheckedRadioButtonId() == R.id.rb_worker;

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();  // Get Firebase-generated UID
                    if (isBarber) {
                        Barber barber = new Barber(userId, name, email, password, name, workAddress.isEmpty() ? "Not Set" : workAddress);
                        FirebaseDatabase.getInstance().getReference("users").child(userId).setValue(barber);
                    } else {
                        Customer customer = new Customer(userId, name, email, password, phone);
                        FirebaseDatabase.getInstance().getReference("users").child(userId).setValue(customer);
                    }

                    Toast.makeText(getContext(), "Registration successful! Please log in.", Toast.LENGTH_SHORT).show();
                    navigateToLogin(view);  // Go to login instead of auto-login
                }
            } else {
                Toast.makeText(getContext(), "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void navigateToLogin(View view) {
        Navigation.findNavController(view).navigate(R.id.action_regFragment_to_homePageFragment);
    }
}
