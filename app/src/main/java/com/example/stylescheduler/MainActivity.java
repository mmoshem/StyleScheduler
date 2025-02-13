package com.example.stylescheduler;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

//    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        mAuth = FirebaseAuth.getInstance();
    }


//
//    public void login(View view){
//
//        String email=((EditText)findViewById(R.id.EmailAddressfrag1)).getText().toString();
//        String password=((EditText)findViewById(R.id.TextPassword)).getText().toString();
//
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Navigation.findNavController(view).navigate(R.id.action_fragment1_to_fragment3);
//                            Toast.makeText(MainActivity.this,"login ok",Toast.LENGTH_LONG).show();
//
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Toast.makeText(MainActivity.this,"login fail",Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//
//
//        //Navigation.findNavController(view).navigate(R.id.action_fragment1_to_fragment3);
//
//    }


//
//    public void reg(View view) {
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//
//                            FirebaseUser user = mAuth.getCurrentUser();
//
//                        }
//                        else {
//
//                             }
//                    }
//                });
//    }







}


