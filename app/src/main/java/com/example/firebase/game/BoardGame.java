package com.example.firebase.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.KeyEvent;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

public class BoardGame extends View {

    Context context;
    private int levelNumber;
    private List<Block> blocks;
    private List<Cirlce> Power;
    private List<Cirlce> PBalls;
    private float touchX = 300;
    private int screenHeight;
    private int screenWidth;
    private int gameAreaHeight; // New height limit for the game area
    GameThread gameThread;
    Handler gameHandler;
    static final long fps=10;
    private boolean GameLose = false;
    private boolean GameWin = false;
    int PaddleHight = 0;
    private boolean pause = false;
    private boolean Apause = false;
    private boolean sizeInitialized = false;






    // Paint objects
    private Paint ballPaint, paddlePaint, textPaint,winPaint,losePaint, powerPaint;

    // Game objects
    private Objects paddle;
    private Cirlce ball;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenHeight = h;
        screenWidth = w;
        if (!sizeInitialized) {
            gameAreaHeight = (int) (screenHeight * 0.93);
            paddle = new Objects(w / 2 - 50, gameAreaHeight - 50 - PaddleHight, 200, 40);
            ball = new Cirlce(w / 2, gameAreaHeight - 100 - PaddleHight, 30);
            sizeInitialized = true;
            init();
        }
    }

    public BoardGame(Context context, int levelNumber) {
        super(context);
        this.context = context;
        this.levelNumber = levelNumber;
        gameThread = new GameThread();
        gameThread.start();

        gameHandler= new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg)
            {
                if (!pause && !Apause)
                    invalidate();
                return true;
            }
        }) ;
    }

    public void destroy() {
        if (gameThread != null) {
            gameThread.stopThread();
            try {
                gameThread.join(); // Ensures the thread fully stops before continuing
            } catch (InterruptedException e) {
                Log.e("BoardGame", "Game thread interrupted during shutdown", e);
            }
            gameThread = null;  // Only set to null after stopping fully
        }
        Audio.release(); // Release sounds
        Log.d("BoardGame", "Resources released and game thread stopped.");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        destroy();
    }

    private void init() {
        // Stop previous game thread (if any)
        if (gameThread != null && gameThread.isRunning) {
            gameThread.stopThread();
            try {
                gameThread.join(); // Ensures the old thread fully stops
            } catch (InterruptedException e) {
                Log.e("BoardGame", "Game thread interrupted during restart", e);
            }
            gameThread = null;
        }
        // Initialize paint objects
        ballPaint = new Paint();
        ballPaint.setColor(Color.BLUE);

        paddlePaint = new Paint();
        paddlePaint.setColor(Color.BLACK);

        powerPaint = new Paint();
        powerPaint.setColor(Color.rgb(127,0,255));

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50);

        winPaint = new Paint();
        winPaint.setColor(Color.BLUE);
        winPaint.setTextSize(80);

        losePaint = new Paint();
        losePaint.setColor(Color.GRAY);
        losePaint.setTextSize(50);

        initializeLevel();
        // Start new game thread. Ensure only one thread runs.
        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new GameThread();
            gameThread.start();
        }
    }


    private void initializeLevel() {
        blocks = new ArrayList<>();
        Power = new ArrayList<>();
        PBalls = new ArrayList<>();
        int blockWidth = (int) (screenWidth/4.8); //150
        int blockHeight = (int) (screenWidth/14.4); //50
        int spacing = (int) (screenWidth/36); //20
        int startX = (int) (screenWidth/14.4); //50
        int Y1 = (int) (screenHeight/11);
        int Ydiff = (int) (screenHeight/15);


        if (levelNumber != 1){
            paddle = new Objects((float) getWidth() /2, gameAreaHeight - 50 - PaddleHight, 200, 40);
            ball = new Cirlce((float) getWidth() /2, gameAreaHeight - 100 - PaddleHight, 30);
            Intent intent=new Intent(BoardGame.this.getContext(),MyService.class);
            context.startService(intent);

        }

        if (levelNumber == 1) {
            blocks.add(new Block(startX, 100, blockWidth, blockHeight, 1));

            blocks.add(new Block(startX + (blockWidth / 2) + 2 * (blockWidth + spacing), 170, blockWidth, blockHeight, 2));

            blocks.add(new Block(startX + 2 * (blockWidth / 2), 240, blockWidth, blockHeight, 1));
        }
        if (levelNumber == 2) {

            blocks.add(new Block(startX + 2 * (blockWidth + spacing), 100, blockWidth, blockHeight, 1));
            blocks.add(new Block(startX + 3 * (blockWidth + spacing), 100, blockWidth, blockHeight, 3));

            blocks.add(new Block(startX + (blockWidth / 2), 170, blockWidth, blockHeight, 2));
            blocks.add(new Block(startX + (blockWidth / 2) + blockWidth + spacing, 170, blockWidth, blockHeight, 1));
            blocks.add(new Block(startX + (blockWidth / 2) + 2 * (blockWidth + spacing), 170, blockWidth, blockHeight, 2));

            blocks.add(new Block(startX + 2 * (blockWidth / 2), 240, blockWidth, blockHeight, 3));
        }if (levelNumber == 3) {
            ball.setDy(-6);
            ball.setDx(6);
            blocks.add(new Block(startX, 100, blockWidth, blockHeight, 1));
            blocks.add(new Block(startX + 3 * (blockWidth + spacing), 100, blockWidth, blockHeight, 1));
            blocks.add(new Block(startX + (blockWidth / 2), 170 + spacing, blockWidth, blockHeight, 2));
            blocks.add(new Block(startX + (blockWidth / 2) + 2 * (blockWidth + spacing), 170 + spacing, blockWidth, blockHeight, 2));
            blocks.add(new Block(startX + 1 * (blockWidth ), 240 + 2 * spacing, blockWidth, blockHeight, 3));
            blocks.add(new Block(startX + 2 * (blockWidth + spacing), 240 + 2 * spacing, blockWidth, blockHeight, 3));
            blocks.add(new Block(startX + (blockWidth / 2)+ 1 * (blockWidth + spacing), 310 + 3 * spacing, blockWidth, blockHeight, 4));


        }if (levelNumber == 4) {
            ball.setDy(6);
            ball.setDx(6);
            PaddleHight = 20;
            blocks.add(new Block(startX + blockWidth - 2 * spacing , 100, 100, blockHeight, 1));
            blocks.add(new Block(startX + blockWidth - 2 * spacing + 2 * (100 + spacing) + 50 , 100, 100, blockHeight, 1));
            blocks.add(new Block(startX + (blockWidth + spacing) + 10, 170, 100, blockHeight, 2));
            blocks.add(new Block(startX + 40, 170, 100, blockHeight, 2));
            blocks.add(new Block(startX + 2 * (blockWidth + spacing) - 10, 170, 100, blockHeight, 3));
            blocks.add(new Block(startX + 2 * (blockWidth + spacing) + 130, 170, 100, blockHeight, 2));
            blocks.add(new Block(startX + (blockWidth / 2) + (blockWidth + spacing), 240, 120, blockHeight, 2));
            blocks.add(new Block(startX - spacing, 240, 120, blockHeight, 2));
            blocks.add(new Block(startX + 2 * (blockWidth + spacing) + 190, 240, 120, blockHeight, 2));
            blocks.add(new Block(startX + 40, 310, 100, blockHeight, 2));
            blocks.add(new Block(startX + (blockWidth + spacing) + 10, 310, 100, blockHeight, 2));
            blocks.add(new Block(startX + 2 * (blockWidth + spacing) - 10, 310, 100, blockHeight, 3));
            blocks.add(new Block(startX + 2 * (blockWidth + spacing) + 130, 310, 100, blockHeight, 2));
            blocks.add(new Block(startX + blockWidth - 2 * spacing , 380, 100, blockHeight, 1));
            blocks.add(new Block(startX + blockWidth - 2 * spacing + 2 * (100 + spacing) + 50 , 380, 100, blockHeight, 1));
        }if (levelNumber == 5) {
            ball.setDy(6);
            ball.setDx(6);
            PaddleHight = 50;

            blocks.add(new Block(startX + (blockWidth + spacing), 100, blockWidth, blockHeight, 2));
            blocks.add(new Block(startX + 2 * (blockWidth + spacing), 100, blockWidth, blockHeight, 3));
            blocks.add(new Block(startX + 3 * (blockWidth + spacing), 100, blockWidth, blockHeight, 4));

            blocks.add(new Block(startX, 170, blockWidth, blockHeight, 1));
            blocks.add(new Block(startX + 2 * (blockWidth + spacing), 170, blockWidth, blockHeight, 3));
            blocks.add(new Block(startX + 3 * (blockWidth + spacing), 170, blockWidth, blockHeight, 4));

            blocks.add(new Block(startX, 240, blockWidth, blockHeight, 1));
            blocks.add(new Block(startX + (blockWidth + spacing), 240, blockWidth, blockHeight, 2));
            blocks.add(new Block(startX + 3 * (blockWidth + spacing), 240, blockWidth, blockHeight, 4));

            blocks.add(new Block(startX, 310, blockWidth, blockHeight, 1));
            blocks.add(new Block(startX + (blockWidth + spacing), 310, blockWidth, blockHeight, 2));
            blocks.add(new Block(startX + 2 * (blockWidth + spacing), 310, blockWidth, blockHeight, 3));

        }
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
        List<Cirlce> PowerRemove = new ArrayList<>();
        List<Cirlce> BallRemove = new ArrayList<>();



        // Draw blocks and check collisions
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            canvas.drawRect(block.getX(), block.getY(), block.getRightEdge(), block.getBottomEdge(), block.getPaint());

            if (Collision(block, ball)&&block.getDurability()<=0) {
                Audio.playSound("pop", 1.0f);
                blocks.get(i).setDurability(0);
                if(block.getPowerUpChance()==15) {
                    Power.add(block.getPowerUp());
                }
                toRemove.add(block); // Mark for removal
            }

        }


        for (int j = 0; j < Power.size(); j++){
            canvas.drawCircle(Power.get(j).getX(),Power.get(j).getY(),Power.get(j).getR(),powerPaint);
            Movement(Power.get(j));
            if (Collision(paddle, Power.get(j) )||( Power.get(j).getY() > gameAreaHeight - Power.get(j).getR())) {
                PowerRemove.add(Power.get(j));
                if (Collision(paddle, Power.get(j))){
                    PBalls.add(new Cirlce(Power.get(j).getX(), Power.get(j).getY()-30, 30));
                }
            }
        }



        for (int j = 0; j < PBalls.size(); j++){
            canvas.drawCircle(PBalls.get(j).getX(),PBalls.get(j).getY(),PBalls.get(j).getR(),powerPaint);
            Movement(PBalls.get(j));
            for (int i = 0; i < blocks.size(); i++){
                if(Collision(blocks.get(i),PBalls.get(j))&&blocks.get(i).getDurability()<=0){
                        Audio.playSound("pop", 1.0f);
                        toRemove.add(blocks.get(i)); // Mark for removal
                     }
            }

            if ( PBalls.get(j).getY() > gameAreaHeight - PBalls.get(j).getR()) {
                BallRemove.add(PBalls.get(j));
            }
            Collision(paddle, PBalls.get(j));
        }
        if (!toRemove.isEmpty()) {
            blocks.removeAll(toRemove);
        }
        if (!BallRemove.isEmpty()) {
            PBalls.removeAll(BallRemove);
        }
        if (!PowerRemove.isEmpty()) {
            Power.removeAll(PowerRemove);
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

            Intent intent=new Intent(BoardGame.this.getContext(),MyService.class);
            intent.setAction("PAUSE");
            context.startService(intent);

            pause = true;
            if(!(levelNumber == 10))
            {
            if(!GameWin){
                Audio.playSound("Win", 1.0f);
            }
            canvas.drawText("LEVEL WON", (float) getWidth() /2 - 130, (float) gameAreaHeight /2,winPaint);
            GameWin = true;
            }
            if(levelNumber == 10)
            {
                canvas.drawText("YOU WIN", (float) getWidth() /2 - 130, (float) gameAreaHeight /2,winPaint);
            }
        }

        if (ball.getY() > gameAreaHeight - ball.getR()) {
            Intent intent=new Intent(BoardGame.this.getContext(),MyService.class);
            context.stopService(intent);
            pause = true;
            if(!GameLose)
            {
                Audio.playSound("gameover", 1.0f);
            }

            canvas.drawText("YOU LOSE", (float) getWidth() /2 -130, (float) gameAreaHeight /2,losePaint);
            GameLose = true;


        }
        //invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!GameWin && !GameLose) {

            if (event.getY() < gameAreaHeight) { // Only move paddle inside game area
                touchX = event.getX();
            }

        }
        if (GameLose) {
            pause = false;
        }
            if (GameWin) {
                pause = false;
                levelNumber++;
                GameWin = false;
                init();
                invalidate();
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
                Audio.playSound("pop", 1.0f);
            }
            if (block.getDurability() > 0){
                Audio.playSound("bounce", 1.0f);
            }
        }
        if (collisionDetected && !(a instanceof Block)) {
            if(!GameLose)
            {
                Audio.playSound("bounce", 1.0f);
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
            if(!GameLose)
            {
                Audio.playSound("bounce", 1.0f);
            }

        }

        // Bounce off top wall
        if (p.getY() < p.getR()) {
            p.setCMy(); // Reverse Y direction
            if(!GameLose)
            {
                Audio.playSound("bounce", 1.0f);
            }

        }

        // Ball hits the bottom (Game Over condition)
        if (p.getY() > gameAreaHeight - p.getR()) {
            p.setCMy(); // Reverse Y direction
        }

    }
    public class GameThread extends Thread {
        private boolean isRunning = true;  // Controls loop execution
        long stepPerSecond = 100 / fps;
        long startTime;
        long sleepTime;

        @Override
        public void run() {
            super.run();
            while (isRunning) {
                startTime = System.currentTimeMillis();
                sleepTime = stepPerSecond - (System.currentTimeMillis() - startTime);
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                gameHandler.sendEmptyMessage(0);
            }
            Log.d("GameThread", "Thread stopped.");
        }

        public void stopThread() {
            isRunning = false;
            interrupt(); // Interrupts sleep if it's waiting
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_1) {  // Detect "1" key
            if (!Apause)
                Apause = true;
           else if (Apause && !pause)
               Apause = false;
            Log.d("KeyEvent", "Key 1 Pressed - Apause: " + Apause);
            return true;  // Consume event
        }
        return super.onKeyDown(keyCode, event);
    }
//    @Override
//    public void setOnKeyListener(OnKeyListener l) {
//        super.setOnKeyListener(l);
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {  // Detect key press
//            if (keyCode == KeyEvent.KEYCODE_1) { // Check if "1" key is pressed
//                Apause = !Apause;  // Toggle Apause{
//            if (!Apause)
//                Apause = true;
//            else if (Apause && !pause)
//                Apause = false;
//            }
//        }
//    }
}


