package com.example.firebase.game;

public class Cirlce {
    private float x, y, radius;
    private float dx = 5;  // Default horizontal speed
    private float dy = -5; // Default upward movement


    public Cirlce(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }
    public Cirlce(float x, float y) {
        this.x = x;
        this.y = y;
        this.radius = 10;
        dx = 0;
        dy = 3;
    }




    public void setMMy() {
        y += dy;  // Move vertically
    }

    public void setMMx() {
        x += dx;  // Move horizontally
    }

    public void setCMy() {
        dy = -dy;  // Reverse Y direction
    }

    public void setCMx() {
        dx = -dx;  // Reverse X direction
    }

    public float getDy() { return dy; }
    public float getDx() { return dx; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getR() { return radius; }

    public Cirlce setDx(float dx) {
        this.dx = dx;
        return this;
    }
    public Cirlce setDy(float dy) {
        this.dy = dy;
        return this;
    }
}