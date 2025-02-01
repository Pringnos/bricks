package com.example.firebase.game;

public class Objects {
    private float x;
    private float y;
    protected float h;
    protected float w;




    public Objects(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.h = height;
        this.w = width;
    }
    public Objects(float x,float y) {
        this.x = x;
        this.y = y;
    }

    public float getH() {
        return h;
    }
    public float getW() {
        return w;
    }
    public float getWo() {
        return x+w;
    }

    public float getHo() {

        return y+h;
    }
    public float getX() {

        return x;
    }
    public float getY() {

        return y;
    }


    public Objects MoveH(float p) {

        this.y = p-(getH()/2);
        return this;
    }
    public Objects MoveW(float p) {
        this.x = p-(getW()/2);
        return this;
    }
    public float GetMidY(){
        return this.y+(getH()/2);
    }

    public Objects MoveWe(int q) {
        this.x=this.x + q;
        return this;
    }

    public Objects setY(float y) {
        this.y = y;
        return this;
    }

    public Objects setX(float x) {
        this.x = x;
        return this;
    }

    public float GetMidX(){
        return x+(getW()/2);
    }

}
