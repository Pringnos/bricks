// com.example.firebase.utils.HighScoreManager.java
package com.example.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.firebase.models.UserScore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HighScoreManager {
    public interface HighScoreCallback {
        void onHighScoreFetched(int score);
    }
    public static void reportScore(int newScore) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        String name = user.getDisplayName();
        if (name == null || name.isEmpty()) {
            name = "Anonymous";
        }

        String finalName = name;
        FirebaseDatabase.getInstance()
                .getReference("high_scores")
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserScore existingScore = snapshot.getValue(UserScore.class);
                        int savedScore = existingScore != null ? existingScore.getScore() : 0;

                        if (newScore > savedScore) {
                            UserScore userScore = new UserScore(finalName, newScore);
                            snapshot.getRef().setValue(userScore)
                                    .addOnSuccessListener(aVoid -> Log.d("HighScoreManager", "High score updated"))
                                    .addOnFailureListener(e -> Log.e("HighScoreManager", "Score update failed", e));
                        } else {
                            Log.d("HighScoreManager", "Score not higher. No update.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("HighScoreManager", "Failed to check existing high score", error.toException());
                    }
                });
    }

    public static void getHighScore(HighScoreCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        FirebaseDatabase.getInstance()
                .getReference("high_scores")
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserScore userScore = snapshot.getValue(UserScore.class);
                        if (userScore != null) {
                            callback.onHighScoreFetched(userScore.getScore());
                        } else {
                            callback.onHighScoreFetched(0); // Default score if none exists
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("HighScoreManager", "Failed to fetch high score", error.toException());
                        callback.onHighScoreFetched(0);
                    }
                });
    }
}