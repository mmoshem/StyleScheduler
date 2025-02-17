package com.example.stylescheduler.Fragments;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.stylescheduler.Classes.Barber;
import com.example.stylescheduler.Classes.Customer;
import com.example.stylescheduler.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegFragment extends Fragment {

    private FirebaseAuth mAuth;

    public RegFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reg, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText etName = view.findViewById(R.id.et_name);
        EditText etEmail = view.findViewById(R.id.et_email);
        EditText etPhone = view.findViewById(R.id.et_phone);
        EditText etPassword = view.findViewById(R.id.et_password);
        RadioGroup rgAccountType = view.findViewById(R.id.rg_account_type);
        EditText etWorkAddress = view.findViewById(R.id.et_work_address);
        Button registerButton = view.findViewById(R.id.btn_register);

        rgAccountType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_worker) {
                etWorkAddress.setVisibility(View.VISIBLE);
            } else {
                etWorkAddress.setVisibility(View.GONE);
            }
        });

        registerButton.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String phone = etPhone.getText().toString();
            String password = etPassword.getText().toString();
            String role = (rgAccountType.getCheckedRadioButtonId() == R.id.rb_worker) ? "barber" : "customer";
            String workAddress = (role.equals("barber")) ? etWorkAddress.getText().toString() : "";

            if (isValidInput(name, email, phone, password)) {
                registerToFirebase(view, name, email, phone, password, role, workAddress);
            }
        });
    }

    private boolean isValidInput(String name, String email, String phone, String password) {
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void registerToFirebase(View view, String name, String email, String phone, String password, String role, String workAddress) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();
                    String safeEmail = email.replace(".", "_");
                    FirebaseDatabase database=FirebaseDatabase.getInstance();
                    if (role.equals("barber")) {
                        Barber barber = new Barber(userId, name, email,password, workAddress, phone);
                        database.getReference("barbers").child(safeEmail).setValue(barber);
                    }
                    else {
                        Customer customer = new Customer(userId, name, email,password ,phone);
                        database.getReference("customers").child(safeEmail).setValue(customer);
                    }

                    Toast.makeText(getContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigate(R.id.homePageFragment);
                }
            } else {
                Toast.makeText(getContext(), "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
