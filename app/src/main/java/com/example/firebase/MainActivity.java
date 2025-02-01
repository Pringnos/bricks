package com.example.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.firebase.game.Game;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button registerButton, loginButton, playButton, playAsGuestButton, logoutButton;
    private TextView scoreTextView, welcomeTextView;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Get score from intent
        Intent intent = getIntent();
        int score = intent.hasExtra("score") ? intent.getIntExtra("score", 0) : 0;

        // Initialize views
        scoreTextView = findViewById(R.id.ST);
        welcomeTextView = findViewById(R.id.userNameTextView);
        playButton = findViewById(R.id.PB);
        playAsGuestButton = findViewById(R.id.playAsGuestButton); // New button
        registerButton = findViewById(R.id.RB);
        loginButton = findViewById(R.id.LB);
        logoutButton = findViewById(R.id.logoutButton);

        // Set score text
        scoreTextView.setText("Score: " + score);

        // Adjust window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set button click listeners
        registerButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        playAsGuestButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

        // Display user name & update button visibility
        displayUserNameAndUpdateButtons();
    }

    private void displayUserNameAndUpdateButtons() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                welcomeTextView.setText("Welcome, " + displayName + "!");
            } else {
                welcomeTextView.setText("Welcome!");
            }
            scoreTextView.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.VISIBLE);
            playAsGuestButton.setVisibility(View.GONE); // Hide guest play button
            loginButton.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
        } else {
            welcomeTextView.setText("Welcome, Guest!");
            scoreTextView.setVisibility(View.GONE);
            playButton.setVisibility(View.GONE);
            playAsGuestButton.setVisibility(View.VISIBLE); // Show guest play button
            loginButton.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == registerButton) {
            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivityForResult(intent, 1);
        } else if (view == loginButton) {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivityForResult(intent, 2);
        } else if (view == playButton) {
            if (firebaseAuth.getCurrentUser() == null) {
                Toast.makeText(MainActivity.this, "User isn't logged in", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, Game.class);
                startActivityForResult(intent, 3);
            }
        } else if (view == playAsGuestButton) {
            // Start game as a guest
            Intent intent = new Intent(MainActivity.this, Game.class);
            startActivity(intent);
        } else if (view == logoutButton) {
            firebaseAuth.signOut();
            Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            displayUserNameAndUpdateButtons(); // Refresh UI after logout
        }
    }
}