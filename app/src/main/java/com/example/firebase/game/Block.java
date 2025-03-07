package com.example.firebase.game;

import android.graphics.Color;
import android.graphics.Paint;
import java.util.Random;

public class Block extends Objects {
    private Paint paint;
    private int durability;
    private int powerUpChance;

    public Block(int x, int y, int width, int height, int durability) {
        super(x, y, width, height);
        paint = new Paint();
        this.durability = durability;
        this.powerUpChance = 15;
        if (durability==1)
            paint.setColor(Color.YELLOW);
        else if (durability==2)
            paint.setColor(Color.rgb(255, 123, 25));
        else if (durability==3)
            paint.setColor(Color.RED);
        else
            paint.setColor(Color.BLACK);
    }


    public int hitBlock() {
        if (durability > 0) {
            durability--;
            if (durability==1)
                paint.setColor(Color.YELLOW);
            else if (durability==2)
                paint.setColor(Color.rgb(255, 123, 25));
            else if (durability==3)
                paint.setColor(Color.RED);
            else
                paint.setColor(Color.BLACK);
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




    public boolean isDestroyed() {
        return durability <= 0;
    }
}