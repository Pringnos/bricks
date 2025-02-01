package com.example.firebase.game;

import android.graphics.Paint;

import java.lang.reflect.GenericArrayType;
import java.util.Random;

public class Block extends Objects{
private Paint paint;
private int U;
private int P;


    public Block(int x, int y, int Width, int Height, int U, Paint paint) {
        super(x, y, Width, Height);
        this.paint = paint;
        Random rng = new Random();
        P = rng.nextInt(16);
        this.U = U;
    }
    public int UD(){
        return U--;
    }
    public int getP() {
        return P;
    }
    public int getU() {
        return U;
    }

    public Paint getPaint() {
        return paint;
    }

    public Block setPaint(Paint paint) {
        this.paint = paint;
        return this;
    }
}
