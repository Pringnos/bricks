package com.example.firebase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.firebase.game.HighScore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.firebase.game.Game;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityResultLauncher<Intent> registerActivityLauncher;
    private ActivityResultLauncher<Intent> loginActivityLauncher;
    private ActivityResultLauncher<Intent> playActivityLauncher;
    private Button registerButton, loginButton, playButton, playAsGuestButton, logoutButton, highScoresButton;
    private TextView scoreTextView, welcomeTextView, highScoreTextView;
    private FirebaseAuth firebaseAuth;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        loginActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        displayUserNameAndUpdateButtons(); // Refresh UI after login
                    }
                }
        );
        playActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        int score = result.getData().getIntExtra("score", 0);
                        scoreTextView.setText("Score: " + score);
                        scoreTextView.setVisibility(View.VISIBLE);
                    }
                }
        );
        registerActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> { }
        );

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
        highScoresButton = findViewById(R.id.highScoresButton);
        highScoreTextView = findViewById(R.id.highScoreTextView);

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
        highScoresButton.setOnClickListener(this);

        // Display user name & update button visibility
        displayUserNameAndUpdateButtons();
    }

    private void displayUserNameAndUpdateButtons() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                welcomeTextView.setText(getString(R.string.welcome, displayName));
            } else {
                welcomeTextView.setText(getString(R.string.welcome));
            }
            scoreTextView.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.VISIBLE);
            playAsGuestButton.setVisibility(View.GONE); // Hide guest play button
            loginButton.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);

            loadHighScore();
        } else {
            welcomeTextView.setText(getString(R.string.welcome_guest));
            scoreTextView.setVisibility(View.GONE);
            playButton.setVisibility(View.GONE);
            playAsGuestButton.setVisibility(View.VISIBLE); // Show guest play button
            loginButton.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);

            highScoreTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == registerButton) {
            Intent intent = new Intent(MainActivity.this, Register.class);
            registerActivityLauncher.launch(intent);
        } else if (view == loginButton) {
            Intent intent = new Intent(MainActivity.this, Login.class);
            loginActivityLauncher.launch(intent);
        } else if (view == playButton) {
            if (firebaseAuth.getCurrentUser() == null) {
                Toast.makeText(MainActivity.this, "User isn't logged in", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, Game.class);
                intent.putExtra("LEVEL_NUMBER", 1);
                playActivityLauncher.launch(intent);
            }
        } else if (view == playAsGuestButton) {
            Intent intent = new Intent(MainActivity.this, Game.class);
            intent.putExtra("LEVEL_NUMBER", 1);
            playActivityLauncher.launch(intent);
        } else if (view == logoutButton) {
            firebaseAuth.signOut();
            Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            displayUserNameAndUpdateButtons(); // Refresh UI after logout
        } else if (view == highScoresButton) {
            Intent intent = new Intent(MainActivity.this, HighScore.class);
            startActivity(intent);
        }
    }
    private void loadHighScore() {
        HighScoreManager.getHighScore(highScore -> {
            highScoreTextView.setText("High Score: " + highScore);
            highScoreTextView.setVisibility(View.VISIBLE);
        });
    }
}