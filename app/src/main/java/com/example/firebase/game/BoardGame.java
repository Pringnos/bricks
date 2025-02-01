package com.example.firebase.game;

import static android.app.ProgressDialog.show;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.ArrayList;

import com.example.firebase.game.Block;
import com.example.firebase.game.Cirlce;
import com.example.firebase.game.Objects;

import java.util.ArrayList;
import java.util.List;

public class BoardGame extends View {
    private float r = 300;
    Context context;
    Paint paint = new Paint();
    Paint paint1 = new Paint();
    Paint lv1 = new Paint();
    Paint lv2 = new Paint();
    Paint lv3 = new Paint();
    Objects a = new Objects(180, 950,100,50);
    Cirlce p = new Cirlce(500,950,30);
    Block q = new Block(100, 100, 75,50,3,lv1);
    Block w = new Block(500, 300, 75,50,3,lv1);
    Block e = new Block(500, 200, 75,50,2,lv1);
    Block t = new Block(500, 300, 75,50,2,lv1);
    Block y = new Block(300, 300, 75,50,1,lv1);



    public BoardGame(Context context) {
        super(context);
        this.context = context;

    }
    @Override
    protected void onDraw(@NonNull Canvas canvas) {

        super.onDraw(canvas);
        paint.setColor(Color.BLUE);
        paint1.setColor(Color.BLACK);
        lv1.setColor(Color.YELLOW);
        lv2.setColor(Color.parseColor("#FF9500"));
        lv3.setColor(Color.RED);
        canvas.drawCircle(p.getX(),p.getY(),p.getR(),paint);
        canvas.drawRect(a.getX(),a.getY(), a.getWo(), a.getHo(), paint1);
        Movement(p);
        Collision(a,p);

        UC(w);
        if (w.getU()>0){
            canvas.drawRect(w.getX(),w.getY(),w.getWo(),w.getHo(),w.getPaint());
            Dissapear(w,p);
        }



        paint.setTextSize(50);
        paint.setColor(Color.BLUE);
        if(a.GetMidX()<r)
            a.MoveWe(5);
        if(a.GetMidX()>r)
            a.MoveWe(-5);
        invalidate();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
         r = event.getX();
        return super.onTouchEvent(event);
    }

    public void Collision(Objects a, Cirlce p){
        if (p.getY() == a.getY()-p.getR() && p.getX()>=a.getX()-p.getR()&&p.getX()<=a.getWo()+p.getR())
            p.setCMy();
        if (p.getY() - p.getH() == a.getHo()-p.getR() && p.getX()>=a.getX()-p.getR()&&p.getX()<=a.getWo()+p.getR())
            p.setCMy();
        if (p.getX() == a.getX()-p.getR() && p.getY()>=a.getY()-p.getR()&&p.getY()<=a.getHo()+p.getR())
            p.setCMx();
        if (p.getX() - p.getW() == a.getWo()-p.getR() && p.getY()>=a.getY()-p.getR()&&p.getY()<=a.getHo()+p.getR())
            p.setCMx();
    }
    public void Movement(Cirlce p){

        p.setMMy();
        if (p.getX() > getWidth() - p.getR() || p.getX() < p.getR())
            p.setCMx();
        if (p.getY() > getHeight() - p.getR() || p.getY() < p.getR())
            p.setCMy();
    }
    public void Dissapear(Block a, Cirlce p) {
        if (p.getY() == a.getY() - p.getR() && p.getX() >= a.getX() - p.getR() && p.getX() <= a.getWo() + p.getR()) {
            p.setCMy();
        a.UD();
        }
        if (p.getY() - p.getH() == a.getHo() - p.getR() && p.getX() >= a.getX() - p.getR() && p.getX() <= a.getWo() + p.getR()) {
            p.setCMy();
            a.UD();
        }
        if (p.getX() == a.getX() - p.getR() && p.getY() >= a.getY() - p.getR() && p.getY() <= a.getHo() + p.getR()) {
            p.setCMx();
            a.UD();
        }
        if (p.getX() - p.getW() == a.getWo() - p.getR() && p.getY() >= a.getY() - p.getR() && p.getY() <= a.getHo() + p.getR()) {
            p.setCMx();
            a.UD();
        }
    }
    public void UC(Block a){
        if (a.getU()==3)
        a.setPaint(lv3);
        else if (a.getU()==2)
        a.setPaint(lv2);
        else if (a.getU()==1)
        a.setPaint(lv1);
        else if (a.getU()>0)
            a.setPaint(paint1);
        else
            a.setPaint(paint);
    }
}


