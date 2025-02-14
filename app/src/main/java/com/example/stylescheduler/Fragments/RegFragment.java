package com.example.stylescheduler.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.stylescheduler.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private FirebaseAuth mAuth;
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
        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_reg, container, false);

        Button authanticateAndRegButton = view.findViewById(R.id.btn_register);

        authanticateAndRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextEmailBox = view.findViewById(R.id.et_email);
                EditText editTextPhoneNum = view.findViewById(R.id.et_phone);
                EditText editTextPassword = view.findViewById(R.id.et_password);
                EditText editTextRePassword = view.findViewById(R.id.et_repassword);
                EditText editTextName = view.findViewById(R.id.et_name);

                if(isInfoGoodToReg(editTextEmailBox,editTextPhoneNum,editTextPassword,editTextRePassword,editTextName)){
                    String passwordStr = editTextPassword.getText().toString();
                    String emailStr = editTextEmailBox.getText().toString();
                    registerToFireBase(view ,emailStr, passwordStr);
                }


            }
        });

        return view;
    }

    public boolean isInfoGoodToReg(EditText emailView, EditText phoneNumView, EditText PasswordView, EditText RePasswordView,EditText name){
        String emailStr = emailView.getText().toString();
        String phoneNumString = phoneNumView.getText().toString();
        String passwordStr = PasswordView.getText().toString();
        String rePasswordString = RePasswordView.getText().toString();
        String namestring = name.getText().toString();



        if(!emailStr.isBlank()&&!passwordStr.isBlank()&&
                !phoneNumString.isBlank()&&!rePasswordString.isBlank()&&!namestring.isBlank()){
            if(passwordStr.equals(rePasswordString)) {
                if(passwordStr.length()>=6) {
                    if(namestring.length()<10) {
                        return true;
                    }
                    else{
                        Toast.makeText(getContext(), "name should be shorter than 10 chars", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getContext(), "password should be longer than 5", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                RePasswordView.setText("");
                Toast.makeText(getContext(), "password should match", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getContext(), "fill all fields", Toast.LENGTH_SHORT).show();
        }
        return false;
    }



    public void registerToFireBase(View view , String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String nameString = ((EditText) view.findViewById(R.id.et_name)).getText().toString();
                    String phoneNumString = ((EditText)view.findViewById(R.id.et_phone)).getText().toString();
                    EditText etWorkAddress = view.findViewById(R.id.et_work_address);

                    addData(nameString,email,phoneNumString);
                    Toast.makeText(getContext(), "registration successful", Toast.LENGTH_SHORT).show();

                    Navigation.findNavController(view).navigate(R.id.action_regFragment_to_homePageFragment);

                } else {
                    Toast.makeText(getContext(), "registration filed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void addData(String name,String Email,String phone) {
        // Write a message to the database
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.child("Email").setValue(Email);
        userRef.child("phone").setValue(phone);
        userRef.child("name").setValue(name);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find the RadioGroup and EditText views
        RadioGroup rgAccountType = view.findViewById(R.id.rg_account_type);
        EditText etWorkAddress = view.findViewById(R.id.et_work_address);

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