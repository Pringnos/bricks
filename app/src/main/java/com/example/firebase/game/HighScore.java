package com.example.firebase.game;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebase.R;
import com.example.firebase.adapters.LeaderboardAdapter;
import com.example.firebase.models.UserScore;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScore extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score); // Layout with RecyclerView

        RecyclerView recyclerView = findViewById(R.id.leaderboardRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load top scores from Firebase
        FirebaseDatabase.getInstance()
                .getReference("high_scores")
                .orderByChild("score")
                .limitToLast(10)
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    List<UserScore> topScores = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        UserScore userScore = child.getValue(UserScore.class);
                        if (userScore != null) {
                            topScores.add(userScore);
                        }
                    }

                    // Highest score first (Firebase gives ascending order)
                    Collections.reverse(topScores);

                    LeaderboardAdapter adapter = new LeaderboardAdapter(this, topScores);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Log.e("HighScore", "Failed to fetch top scores", e));

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }
}