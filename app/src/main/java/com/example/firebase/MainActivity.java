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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
Button RB,LB,PB;
TextView ST, userNameTextView;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        int score = intent.hasExtra("score") ? intent.getIntExtra("score", 0) : 0;
        ST = (TextView) findViewById(R.id.ST);
        userNameTextView = findViewById(R.id.userNameTextView);
        
        ST.setText("Score:" + score);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        PB = (Button) findViewById(R.id.PB);
        RB = (Button) findViewById(R.id.RB);
        LB = (Button) findViewById(R.id.LB);

        RB.setOnClickListener(this);
        LB.setOnClickListener(this);
        PB.setOnClickListener(this);

        displayUserName();
    }

    private void displayUserName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                userNameTextView.setText("Welcome, " + displayName + "!");
            } else {
                userNameTextView.setText("Welcome!");
            }
        } else {
            userNameTextView.setText("Welcome, Guest!");
        }
    }

    @Override
    public void onClick(View v) {
        if (v == RB)
        {
            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivityForResult(intent,1);
        }
        else if (v == LB)
        {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivityForResult(intent,2);
        }
        else if (v == PB) {
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(MainActivity.this, "User isn't logged in", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, Game.class);
                startActivityForResult(intent, 3);
            }
        }
    }
}