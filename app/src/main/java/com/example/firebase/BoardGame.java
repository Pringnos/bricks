package com.example.firebase;

import static android.app.ProgressDialog.show;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class BoardGame extends View {

    Context context;
    float x = 10, y = 10, mx = 1, my = 1 ;
    int t = 0;
    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.img);
    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
    Paint paint = new Paint();
    public BoardGame(Context context)
    {
        super(context);
        this.context = context;
    }
@Override
    protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    canvas.drawBitmap(scaledBitmap,x,y,null);
    x+=mx;
    y+=my;
    if(x > canvas.getWidth()-100 || x < 0)
        mx=-1*mx ;
    if(y > canvas.getHeight()-100 || y < 0)
        my=-1*my;
    paint.setTextSize(50);
    paint.setColor(Color.BLUE);
    canvas.drawText(String.valueOf(t),(this.getWidth()/2)-50,this.getHeight()/2,paint);
    if(t == 10) {
        paint.setTextSize(100);
        canvas.drawText("YOU WIN",(this.getWidth()/2)-200,this.getHeight()/2-50,paint);
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("score", 10);
        context.startActivity(intent);

    }
        else if (t == -10) {
        paint.setTextSize(100);
        canvas.drawText("YOU LOSE",(this.getWidth()/2)-200,this.getHeight()/2-50,paint);
        Intent intent1 = new Intent(context, MainActivity.class);
        intent1.putExtra("score", -10);
        context.startActivity(intent1);
    }
    invalidate();


}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getX() > x && event.getX() < x + 100 && event.getY() > y && event.getY() < y + 100)
        {
        t++;
        }
        else
            t--;
        return super.onTouchEvent(event);
    }
}
