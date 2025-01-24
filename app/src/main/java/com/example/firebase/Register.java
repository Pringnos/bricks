package com.example.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    EditText FT, LT , PT , ET;
    Button SB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SB = (Button) findViewById(R.id.SB);
        FT = (EditText) findViewById(R.id.FT);
        LT = (EditText) findViewById(R.id.LT);
        PT = (EditText) findViewById(R.id.PT);
        ET = (EditText) findViewById(R.id.ET);
        SB.setOnClickListener(this);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == SB) {
            if (ET.getText().toString().isBlank() ||FT.getText().toString().isBlank() ||LT.getText().toString().isBlank() ||PT.getText().toString().isBlank()) {
                Toast.makeText(Register.this, "Missing properties.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            String email = ET.getText().toString();
            String password = PT.getText().toString();
            String firstName = FT.getText().toString();
            String lastName = LT.getText().toString();


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    // Update the user's profile
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(firstName + " " + lastName) // Set display name
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("Register", "User profile updated.");
                                                        updateUI(user); // Redirect the user
                                                    } else {
                                                        Log.e("Register", "Failed to update profile: " + task.getException());
                                                        Toast.makeText(Register.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            } else {
                                // If sign-in fails, display a message to the user.
                                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                Log.e("Register", "Authentication failed: " + errorMessage);
                                Toast.makeText(Register.this, "Authentication failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(Register.this, "Authentication succeeded.",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Register.this, MainActivity.class);
            startActivityForResult(intent,1);
        }
    }
}