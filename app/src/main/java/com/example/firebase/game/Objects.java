package com.example.firebase.game;

public class Objects {
    private float x;
    private float y;
    protected float w; // Width
    protected float h; // Height
    //new a
    public Objects(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;
    }

    public Objects(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /** Returns height */
    public float getHeight() {
        return h;
    }

    /** Returns width */
    public float getWidth() {
        return w;
    }

    /** Returns right edge of object */
    public float getRightEdge() {
        return x + w;
    }

    /** Returns bottom edge of object */
    public float getBottomEdge() {
        return y + h;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    /** Move object horizontally */
    public Objects moveHorizontally(float delta) {
        this.x += delta;
        return this;
    }

    /** Move object vertically */
    public Objects moveVertically(float delta) {
        this.y += delta;
        return this;
    }

    /** Move object to horizontal center of `p` */
    public Objects MoveH(float p) {
        this.y = p - (h / 2);
        return this;
    }

    /** Move object to vertical center of `p` */
    public Objects MoveW(float p) {
        this.x = p - (w / 2);
        return this;
    }

    /** Get center Y position */
    public float getCenterY() {
        return this.y + (h / 2);
    }

    /** Get center X position */
    public float getCenterX() {
        return this.x + (w / 2);
    }

    /** Set object Y position */
    public Objects setY(float y) {
        this.y = y;
        return this;
    }

    /** Set object X position */
    public Objects setX(float x) {
        this.x = x;
        return this;
    }
}