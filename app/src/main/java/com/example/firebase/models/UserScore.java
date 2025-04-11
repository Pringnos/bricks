package com.example.firebase.models;

public class UserScore {
    public String name;
    public int score;

    public UserScore() {
        // Default constructor required for calls to DataSnapshot.getValue(UserScore.class)
    }
    public UserScore(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public int getScore() {
        return this.score;
    }
}