package com.example.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private EditText emailEditText, passwordEditText, firstNameEditText, lastNameEditText;
    private Button registerButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        emailEditText = findViewById(R.id.ET);
        passwordEditText = findViewById(R.id.PT);
        firstNameEditText = findViewById(R.id.FT);
        lastNameEditText = findViewById(R.id.LT);
        registerButton = findViewById(R.id.SB);
        progressBar = findViewById(R.id.progressBar);
        TextView loginLink = findViewById(R.id.loginLink);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registerButton.setOnClickListener(this);
        loginLink.setOnClickListener(v -> navigateToLogin());
    }

    @Override
    public void onClick(View v) {
        if (v == registerButton) {
            handleRegistration();
        }
    }

    private void handleRegistration() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();

        // Input validation
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            return;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            return;
        }
        if (firstName.isEmpty()) {
            firstNameEditText.setError("First name is required");
            return;
        }
        if (lastName.isEmpty()) {
            lastNameEditText.setError("Last name is required");
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Firebase registration
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Hide progress bar
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            updateUserProfile(user, firstName, lastName);
                        }
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(Register.this, "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e("Register", "Error: " + errorMessage);
                    }
                });
    }

    private void updateUserProfile(FirebaseUser user, String firstName, String lastName) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(firstName + " " + lastName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Register", "User profile updated.");
                        navigateToMainActivity();
                    } else {
                        Log.e("Register", "Failed to update profile: " + task.getException());
                        Toast.makeText(Register.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(Register.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}