package com.example.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.widget.TextView;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // UI Initialization
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        ImageButton backButton = findViewById(R.id.backButton);

        TextView registerLink = findViewById(R.id.registerLink);
        registerLink.setOnClickListener(v -> {
            // Navigate to the Register activity
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Set up UI Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == loginButton) {
            handleLogin();
        }
    }

    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Input validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(Login.this, getString(R.string.missing_fields_error), Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);

        // Firebase authentication
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Hide loading indicator
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Exception exception = task.getException();
                        String errorMessage = "Authentication failed.";

                        if (exception != null) {
                            Log.e("LoginError", "Login failed", exception);

                            String exceptionMessage = exception.getMessage();
                            if (exceptionMessage != null) {
                                if (exceptionMessage.contains("INVALID_EMAIL")) {
                                    errorMessage = "Invalid email format. Please check your email.";
                                } else if (exceptionMessage.contains("USER_NOT_FOUND")) {
                                    errorMessage = "No account found with this email.";
                                } else if (exceptionMessage.contains("WRONG_PASSWORD")) {
                                    errorMessage = "Incorrect password. Please try again.";
                                } else if (exceptionMessage.contains("USER_DISABLED")) {
                                    errorMessage = "This account has been disabled.";
                                } else if (exceptionMessage.contains("TOO_MANY_REQUESTS")) {
                                    errorMessage = "Too many failed attempts. Try again later.";
                                } else if (exceptionMessage.contains("NETWORK_ERROR")) {
                                    errorMessage = "Network error. Please check your internet connection.";
                                } else {
                                    errorMessage = exceptionMessage; // Show Firebase's actual error message
                                }
                            }
                        }

                        Toast.makeText(Login.this, errorMessage, Toast.LENGTH_LONG).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(Login.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        }
    }
}