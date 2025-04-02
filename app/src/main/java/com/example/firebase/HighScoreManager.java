// com.example.firebase.utils.HighScoreManager.java
package com.example.firebase;

import android.util.Log;

import com.example.firebase.models.UserScore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class HighScoreManager {

    public static void reportScore(int score) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        String name = user.getDisplayName();
        if (name == null || name.isEmpty()) {
            name = "Anonymous";
        }

        // Create a userScore object
        UserScore userScore = new UserScore(name, score);

        FirebaseDatabase.getInstance()
                .getReference("high_scores")
                .child(uid)
                .setValue(userScore)
                .addOnSuccessListener(aVoid -> Log.d("HighScoreManager", "Score updated"))
                .addOnFailureListener(e -> Log.e("HighScoreManager", "Score update failed", e));
    }
}