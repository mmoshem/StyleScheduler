package com.example.stylescheduler.Fragments;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.stylescheduler.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomePageFragment extends Fragment {

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    public HomePageFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progressBar);
        EditText etEmail = view.findViewById(R.id.editTextTextEmailAddress);
        EditText etPassword = view.findViewById(R.id.editTextTextPassword);
        Button btLogin = view.findViewById(R.id.buttonLogIn);
        Button btRegister = view.findViewById(R.id.buttonRegistration);

        progressBar.setVisibility(View.VISIBLE);
        etEmail.setVisibility(View.GONE);
        etPassword.setVisibility(View.GONE);
        btLogin.setVisibility(View.GONE);
        btRegister.setVisibility(View.GONE);

        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                checkUserRoleAndNavigate(view);
            } else {
                progressBar.setVisibility(View.GONE);
                etEmail.setVisibility(View.VISIBLE);
                etPassword.setVisibility(View.VISIBLE);
                btLogin.setVisibility(View.VISIBLE);
                btRegister.setVisibility(View.VISIBLE);
            }
        }, 940);

        btLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (!email.isEmpty() && !password.isEmpty()) {
                loginUser(email, password, view);
            } else {
                Toast.makeText(getContext(), "OOPS! SOMETHING WRONG!", Toast.LENGTH_SHORT).show();
            }
        });

        btRegister.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_regFragment));
    }

    private void loginUser(String email, String password, View view) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), task -> {
            if (task.isSuccessful()) {
                storeFCMToken();
                checkUserRoleAndNavigate(view);
            } else {
                Toast.makeText(getContext(), "Login failed. Check credentials.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserRoleAndNavigate(View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String safeEmail = user.getEmail().replace(".", "_");
            DatabaseReference barbersRef = FirebaseDatabase.getInstance().getReference("barbers").child(safeEmail);
            DatabaseReference customersRef = FirebaseDatabase.getInstance().getReference("customers").child(safeEmail);

            barbersRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_barberHomePage);
                } else {
                    customersRef.get().addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful() && task2.getResult().exists()) {
                            Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_clientHomePage);
                        } else {
                            Toast.makeText(getContext(), "User role not found", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    private void storeFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }
            String token = task.getResult();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                databaseRef.child("fcmToken").setValue(token);
            }
        });
    }
}
