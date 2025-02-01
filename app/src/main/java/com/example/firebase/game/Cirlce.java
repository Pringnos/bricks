package com.example.firebase.game;

public class Cirlce extends Objects{
    private float r;
    private float mx;
    private float my;

    public Cirlce(float x, float y, float r) {
        super(x, y);
        this.r = r;
        this.w = r*2;
        this.h = r*2;
        mx=4;
        my=4;
    }

    public float getR() {
        return r;
    }

    public float getMx() {
        return mx;
    }

    public float getMy() {
        return my;
    }

    public Cirlce setMy(float my) {
        this.my = my;
        return this;
    }

    public Cirlce setMx(float mx) {
        this.mx = mx;
        return this;
    }

    public Cirlce setR(float r) {
        this.r = r;
        return this;
    }
    public Cirlce setCMy() {
        this.my = my*-1;
        return this;
    }
    public Cirlce setCMx() {
        this.mx = mx*-1;
        return this;
    }
    public Cirlce setMMx() {
        setX(getX()+this.mx);
        return this;
    }
    public Cirlce setMMy() {
        setY(getY()+this.my);
        return this;
    }

}
