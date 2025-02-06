package com.example.firebase.game;

import android.graphics.Paint;
import java.util.Random;

public class Block extends Objects {
    private Paint paint;
    private int durability; // Rename 'U' to a clearer name
    private int powerUpChance; // Rename 'P' for clarity

    public Block(int x, int y, int width, int height, int durability, Paint paint) {
        super(x, y, width, height);
        this.paint = paint;
        this.durability = durability;
        this.powerUpChance = new Random().nextInt(16); // Generates a random number from 0-15
    }

    /** Reduces durability, ensuring it doesn't go below zero */
    public int hitBlock() {
        if (durability > 0) {
            durability--;
        }
        return durability;
    }

    public int getPowerUpChance() {
        return powerUpChance;
    }

    public int getDurability() {
        return durability;
    }

    public Paint getPaint() {
        return paint;
    }

    public Block setPaint(Paint paint) {
        this.paint = paint;
        return this;
    }

    /** Checks if the block is broken */
    public boolean isDestroyed() {
        return durability <= 0;
    }
}