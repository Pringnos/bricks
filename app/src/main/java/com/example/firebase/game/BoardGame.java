package com.example.firebase.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

import com.example.firebase.MainActivity;
import com.example.firebase.Register;

import java.util.logging.LogRecord;

public class BoardGame extends View {
    Context context;
    private int levelNumber;
    private List<Block> blocks;
    private float touchX = 300;
    private int screenHeight;
    private int gameAreaHeight; // New height limit for the game area
    GameThread gameThread;
    Handler gameHandler;
    static final long fps=10;
    private boolean isOver = false;

    // Paint objects
    private Paint ballPaint, paddlePaint, blockPaint1,blockPaint2,blockPaint3, textPaint,winPaint,losePaint;

    // Game objects
    private Objects paddle;
    private Cirlce ball;

    public BoardGame(Context context, int levelNumber) {
        super(context);
        this.context = context;
        this.levelNumber = levelNumber;
        gameThread = new GameThread();
        gameThread.start();
        init(); // Initialize objects

        gameHandler= new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg)
            {
                invalidate();
                return true;
            }
        }) ;


    }

    private void init() {
        // Initialize paint objects
        ballPaint = new Paint();
        ballPaint.setColor(Color.BLUE);

        paddlePaint = new Paint();
        paddlePaint.setColor(Color.BLACK);

        blockPaint1 = new Paint();
        blockPaint1.setColor(Color.RED);
        blockPaint2 = new Paint();
        blockPaint2.setColor(Color.RED);
        blockPaint3 = new Paint();
        blockPaint3.setColor(Color.RED);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50);

        winPaint = new Paint();
        winPaint.setColor(Color.BLUE);
        winPaint.setTextSize(80);

        losePaint = new Paint();
        losePaint.setColor(Color.GRAY);
        losePaint.setTextSize(50);

        // Initialize game objects
        paddle = new Objects(180, gameAreaHeight - 50, 200, 50);
        ball = new Cirlce(500, 700, 30); // Move ball up

        initializeLevel();
    }

    private void initializeLevel() {
        blocks = new ArrayList<>();

        int blockWidth = 150;
        int blockHeight = 50;
        int spacing = 20; // Horizontal spacing between blocks
        int startX = 50;

        if (levelNumber == 1) {
            blocks.add(new Block(startX, 100, blockWidth, blockHeight, 1));
            blocks.add(new Block(startX + (blockWidth / 2) + blockWidth + spacing, 170, blockWidth, blockHeight, 1));
            blocks.add(new Block(startX + (blockWidth / 2) + 2 * (blockWidth + spacing), 170, blockWidth, blockHeight, 2));

            blocks.add(new Block(startX + 2 * (blockWidth / 2), 240, blockWidth, blockHeight, 3));

        }
        if (levelNumber == 2) {

            blocks.add(new Block(startX + 2 * (blockWidth + spacing), 100, blockWidth, blockHeight, 1));
            blocks.add(new Block(startX + 3 * (blockWidth + spacing), 100, blockWidth, blockHeight, 3));

            blocks.add(new Block(startX + (blockWidth / 2), 170, blockWidth, blockHeight, 2));
            blocks.add(new Block(startX + (blockWidth / 2) + blockWidth + spacing, 170, blockWidth, blockHeight, 1));
            blocks.add(new Block(startX + (blockWidth / 2) + 2 * (blockWidth + spacing), 170, blockWidth, blockHeight, 2));

            blocks.add(new Block(startX + 2 * (blockWidth / 2), 240, blockWidth, blockHeight, 3));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenHeight = h;
        gameAreaHeight = (int) (screenHeight * 0.93); // 80% for game, 20% for UI

        paddle = new Objects(w / 2 - 50, gameAreaHeight - 50, 200, 40);
        ball = new Cirlce(w / 2, gameAreaHeight - 80, 30);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // ********** Draw the Game Area **********
        canvas.save(); // Save current canvas state
        canvas.clipRect(0, 0, getWidth(), gameAreaHeight); // Clip only the game area
        canvas.drawColor(Color.DKGRAY); // Background color for the game area

        // Temporary list to store blocks to remove
        List<Block> toRemove = new ArrayList<>();




        // Draw blocks and check collisions
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            canvas.drawRect(block.getX(), block.getY(), block.getRightEdge(), block.getBottomEdge(), block.getPaint());

            if (Collision(block, ball)&&block.getDurability()<=0) {

                toRemove.add(block); // Mark for removal
            }
        }

        // Remove blocks AFTER iteration
        if (!toRemove.isEmpty()) {
            blocks.removeAll(toRemove);
        }

        // Draw ball and paddle
        canvas.drawCircle(ball.getX(), ball.getY(), ball.getR(), ballPaint);
        canvas.drawRect(paddle.getX(), paddle.getY(), paddle.getRightEdge(), paddle.getBottomEdge(), paddlePaint);

        // Handle movement and collision
        Movement(ball);
        Collision(paddle, ball);

        if (paddle.getCenterX() < touchX) paddle.moveHorizontally(5);
        if (paddle.getCenterX() > touchX) paddle.moveHorizontally(-5);

        canvas.restore();

        // ********** Bottom UI Section **********
        canvas.save();
        canvas.clipRect(0, gameAreaHeight, getWidth(), screenHeight); // Clip only the UI section
        canvas.drawColor(Color.LTGRAY);

        // Draw UI text
        textPaint.setColor(Color.BLACK);
        canvas.drawText("Level: " + levelNumber, 50, gameAreaHeight + 100, textPaint);

        canvas.restore();

        if (blocks.isEmpty())
        {
            ball.setDy(0);
            ball.setDx(0);
            canvas.drawText("YOU WIN", (float) getWidth() /2 - 130, (float) gameAreaHeight /2,winPaint);
            isOver = true;
        }

        if (ball.getY() > gameAreaHeight - ball.getR()) {
            ball.setDy(0);
            ball.setDx(0);
            canvas.drawText("YOU LOSE", (float) getWidth() /2 -130, (float) gameAreaHeight /2,losePaint);
            isOver = true;

        }
        //invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isOver) {
            if (event.getY() < gameAreaHeight) { // Only move paddle inside game area
                touchX = event.getX();
            }

        }
        if (isOver)
        {
            if (ball.getY() >= gameAreaHeight - ball.getR()) {
                Intent intent = new Intent(BoardGame.this.getContext(), MainActivity.class);
                intent.putExtra("levelNumber",levelNumber);

            }
            if (blocks.isEmpty()){
                levelNumber++;
                isOver = false;
                init();
                invalidate();
            }
        }
        return super.onTouchEvent(event);
    }
    public boolean Collision(Objects a, Cirlce p) {
        float ballLeft = p.getX() - p.getR();
        float ballRight = p.getX() + p.getR();
        float ballTop = p.getY() - p.getR();
        float ballBottom = p.getY() + p.getR();

        float blockLeft = a.getX();
        float blockRight = a.getX() + a.getWidth();
        float blockTop = a.getY();
        float blockBottom = a.getY() + a.getHeight();

        boolean collisionDetected = false;

        if (ballRight >= blockLeft && ballLeft <= blockRight && ballBottom >= blockTop && ballTop <= blockBottom) {
            // Determine if it's a vertical or horizontal hit
            float overlapLeft = ballRight - blockLeft;
            float overlapRight = blockRight - ballLeft;
            float overlapTop = ballBottom - blockTop;
            float overlapBottom = blockBottom - ballTop;

            if (Math.min(overlapTop, overlapBottom) < Math.min(overlapLeft, overlapRight)) {
                p.setCMy(); // Reverse Y direction
            } else {
                p.setCMx(); // Reverse X direction
            }

            collisionDetected = true;
        }

        if (collisionDetected && a instanceof Block) {
            Block block = (Block) a;
            block.hitBlock();
            if (block.getDurability() <= 0) {
                blocks.remove(block); // Remove if durability is 0
            }
        }
        return collisionDetected;
    }
    public void Movement(Cirlce p) {
        p.setMMy();
        p.setMMx();

        // Bounce off left & right walls
        if (p.getX() > getWidth() - p.getR() || p.getX() < p.getR()) {
            p.setCMx(); // Reverse X direction
        }

        // Bounce off top wall
        if (p.getY() < p.getR()) {
            p.setCMy(); // Reverse Y direction
        }

        // Ball hits the bottom (Game Over condition)
        if (p.getY() > gameAreaHeight - p.getR()) {
            p.setCMy(); // Reverse Y direction
        }
    }

    public class GameThread extends Thread
    {
        long steppersecond = 100/fps;
        long starttime;
        long sleeptime;
        @Override
        public void run() {
            super.run();
            while (true)
            {
                starttime = System.currentTimeMillis();
                sleeptime = steppersecond - (System.currentTimeMillis()-starttime);
                if(sleeptime>0) {
                    try {

                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                gameHandler.sendEmptyMessage(0);
            }

        }
    }
}


