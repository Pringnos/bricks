package com.example.firebase.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.firebase.HighScoreManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoardGame extends View {
    Context context;
    private int levelNumber;
    private List<Block> blocks;
    private List<Cirlce> Power;
    private List<Cirlce> PBalls;
    private float touchX = 300;
    private int screenHeight;
    private int screenWidth;
    private int gameAreaHeight;
    private boolean GameLose = false;
    private boolean GameWin = false;
    private boolean waitForTapToEnd = false;
    private boolean isGameOverHandled = false;
    private boolean pause = false;
    private boolean Apause = false;
    private boolean sizeInitialized = false;

    private int PaddleHight = 0;
    private int score = 0;

    private Paint ballPaint, paddlePaint, textPaint, winPaint, losePaint, powerPaint;
    private Objects paddle;
    private Cirlce ball;

    private GameThread gameThread;
    private Handler gameHandler;
    static final long fps = 20;

    public BoardGame(Context context, int levelNumber) {
        super(context);
        this.context = context;
        this.levelNumber = levelNumber;

        gameHandler = new Handler(msg -> {
            if (!pause && !Apause) invalidate();
            return true;
        });

        gameThread = new GameThread();
        gameThread.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
        if (!sizeInitialized) {
            gameAreaHeight = (int) (screenHeight * 0.93);
            paddle = new Objects(w / 2 - 50, gameAreaHeight - 50 - PaddleHight, 200, 40);
            ball = new Cirlce(w / 2, gameAreaHeight - 100 - PaddleHight, 30);
            sizeInitialized = true;
            init();
        }
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
        if (gameThread != null && gameThread.isRunning) {
            gameThread.stopThread();
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("BoardGame", "Thread error", e);
            }
        }

        ballPaint = new Paint(); ballPaint.setColor(Color.BLUE);
        paddlePaint = new Paint(); paddlePaint.setColor(Color.BLACK);
        powerPaint = new Paint(); powerPaint.setColor(Color.rgb(127, 0, 255));
        textPaint = new Paint(); textPaint.setColor(Color.WHITE); textPaint.setTextSize(50);
        winPaint = new Paint(); winPaint.setColor(Color.BLUE); winPaint.setTextSize(80);
        losePaint = new Paint(); losePaint.setColor(Color.GRAY); losePaint.setTextSize(50);

        initializeLevel();

        gameThread = new GameThread();
        gameThread.start();
    }

    private void initializeLevel() {
        blocks = new ArrayList<>();
        Power = new ArrayList<>();
        PBalls = new ArrayList<>();

        int blockWidth = (int) (screenWidth / 4.8);       // ~150
        int blockHeight = (int) (screenWidth / 14.4);     // ~50
        int spacing = (int) (screenWidth / 36);           // ~20
        int startX = (int) (screenWidth / 14.4);          // ~50
        int Y1 = (int) (screenHeight / 11);
        int Ydiff = (int) (screenHeight / 15);

        if (levelNumber != 1) {
            paddle = new Objects((float) getWidth() / 2, gameAreaHeight - 50 - PaddleHight, 200, 40);
            ball = new Cirlce((float) getWidth() / 2, gameAreaHeight - 100 - PaddleHight, 30);
            Intent intent = new Intent(BoardGame.this.getContext(), MyService.class);
            context.startService(intent);
        }

        if (levelNumber == 6) {
            blocks.add(new Block(startX, Y1, blockWidth, blockHeight, 1));
            blocks.add(new Block(startX + (blockWidth / 2) + 2 * (blockWidth + spacing), Y1 + Ydiff, blockWidth, blockHeight, 2));
            blocks.add(new Block(startX + blockWidth, Y1 + 2 * Ydiff, blockWidth, blockHeight, 1));
        }

        if (levelNumber == 2) {
            blocks.add(new Block(startX + 2 * (blockWidth + spacing), Y1, blockWidth, blockHeight, 1));
            blocks.add(new Block(startX + 3 * (blockWidth + spacing), Y1, blockWidth, blockHeight, 3));
            blocks.add(new Block(startX + (blockWidth / 2), Y1 + Ydiff, blockWidth, blockHeight, 2));
            blocks.add(new Block(startX + (blockWidth / 2) + blockWidth + spacing, Y1 + Ydiff, blockWidth, blockHeight, 1));
            blocks.add(new Block(startX + (blockWidth / 2) + 2 * (blockWidth + spacing), Y1 + Ydiff, blockWidth, blockHeight, 2));
            blocks.add(new Block(startX + blockWidth, Y1 + 2 * Ydiff, blockWidth, blockHeight, 3));
        }

        if (levelNumber == 3) {
            ball.setDy(-6);
            ball.setDx(6);
            blocks.add(new Block(startX, Y1, blockWidth, blockHeight, 1));
            blocks.add(new Block(startX + 3 * (blockWidth + spacing), Y1, blockWidth, blockHeight, 1));
            blocks.add(new Block(startX + (blockWidth / 2), Y1 + Ydiff + spacing, blockWidth, blockHeight, 2));
            blocks.add(new Block(startX + (blockWidth / 2) + 2 * (blockWidth + spacing), Y1 + Ydiff + spacing, blockWidth, blockHeight, 2));
            blocks.add(new Block(startX + blockWidth, Y1 + 2 * Ydiff + 2 * spacing, blockWidth, blockHeight, 3));
            blocks.add(new Block(startX + 2 * (blockWidth + spacing), Y1 + 2 * Ydiff + 2 * spacing, blockWidth, blockHeight, 3));
            blocks.add(new Block(startX + (blockWidth / 2) + blockWidth + spacing, Y1 + 3 * Ydiff + 3 * spacing, blockWidth, blockHeight, 4));
        }

        if (levelNumber == 4) {
            ball.setDy(6);
            ball.setDx(6);
            PaddleHight = 20;

            int smallWidth = (int) (blockWidth * 0.7);
            int medWidth = (int) (blockWidth * 0.85);

            blocks.add(new Block(startX + blockWidth - 2 * spacing, Y1, smallWidth, blockHeight, 1));
            blocks.add(new Block(startX + blockWidth - 2 * spacing + 2 * (smallWidth + spacing) + spacing, Y1, smallWidth, blockHeight, 1));
            blocks.add(new Block(startX + blockWidth + spacing + spacing / 2, Y1 + Ydiff, smallWidth, blockHeight, 2));
            blocks.add(new Block(startX + spacing * 2, Y1 + Ydiff, smallWidth, blockHeight, 2));
            blocks.add(new Block(startX + 2 * (blockWidth + spacing) - spacing / 2, Y1 + Ydiff, smallWidth, blockHeight, 3));
            blocks.add(new Block(startX + 2 * (blockWidth + spacing) + 130, Y1 + Ydiff, smallWidth, blockHeight, 2));
            blocks.add(new Block(startX + (blockWidth / 2) + (blockWidth + spacing), Y1 + 2 * Ydiff, medWidth, blockHeight, 2));
            blocks.add(new Block(startX - spacing, Y1 + 2 * Ydiff, medWidth, blockHeight, 2));
            blocks.add(new Block(startX + 2 * (blockWidth + spacing) + 190, Y1 + 2 * Ydiff, medWidth, blockHeight, 2));
            blocks.add(new Block(startX + spacing * 2, Y1 + 3 * Ydiff, smallWidth, blockHeight, 2));
            blocks.add(new Block(startX + blockWidth + spacing + spacing / 2, Y1 + 3 * Ydiff, smallWidth, blockHeight, 2));
            blocks.add(new Block(startX + 2 * (blockWidth + spacing) - spacing / 2, Y1 + 3 * Ydiff, smallWidth, blockHeight, 3));
            blocks.add(new Block(startX + 2 * (blockWidth + spacing) + 130, Y1 + 3 * Ydiff, smallWidth, blockHeight, 2));
            blocks.add(new Block(startX + blockWidth - 2 * spacing, Y1 + 4 * Ydiff, smallWidth, blockHeight, 1));
            blocks.add(new Block(startX + blockWidth - 2 * spacing + 2 * (smallWidth + spacing) + spacing, Y1 + 4 * Ydiff, smallWidth, blockHeight, 1));
        }

        if (levelNumber == 5) {
            ball.setDy(6);
            ball.setDx(6);
            PaddleHight = 50;

            blocks.add(new Block(startX + blockWidth + spacing, Y1, blockWidth, blockHeight, 2));
            blocks.add(new Block(startX + 2 * (blockWidth + spacing), Y1, blockWidth, blockHeight, 3));
            blocks.add(new Block(startX + 3 * (blockWidth + spacing), Y1, blockWidth, blockHeight, 4));

            blocks.add(new Block(startX, Y1 + Ydiff, blockWidth, blockHeight, 1));
            blocks.add(new Block(startX + 2 * (blockWidth + spacing), Y1 + Ydiff, blockWidth, blockHeight, 3));
            blocks.add(new Block(startX + 3 * (blockWidth + spacing), Y1 + Ydiff, blockWidth, blockHeight, 4));

            blocks.add(new Block(startX, Y1 + 2 * Ydiff, blockWidth, blockHeight, 1));
            blocks.add(new Block(startX + blockWidth + spacing, Y1 + 2 * Ydiff, blockWidth, blockHeight, 2));
            blocks.add(new Block(startX + 3 * (blockWidth + spacing), Y1 + 2 * Ydiff, blockWidth, blockHeight, 4));

            blocks.add(new Block(startX, Y1 + 3 * Ydiff, blockWidth, blockHeight, 1));
            blocks.add(new Block(startX + blockWidth + spacing, Y1 + 3 * Ydiff, blockWidth, blockHeight, 2));
            blocks.add(new Block(startX + 2 * (blockWidth + spacing), Y1 + 3 * Ydiff, blockWidth, blockHeight, 3));
        }
        if (levelNumber == 6) {
            for (int row = 0; row < 5; row++) {
                int rowY = Y1 + row * Ydiff;
                for (int col = 0; col < 4; col++) {
                    if ((row + col) % 2 == 0) continue; // create "holes"
                    int x = startX + col * (blockWidth + spacing);
                    blocks.add(new Block(x, rowY, blockWidth, blockHeight, (col % 3) + 1));
                }
            }
        }
        if (levelNumber == 1) {
            for (int row = 0; row < 4; row++) {
                int blocksInRow = 4 - row;
                int rowY = Y1 + row * Ydiff;
                int offsetX = startX + (blockWidth + spacing) * row / 2;

                for (int col = 0; col < blocksInRow; col++) {
                    int x = offsetX + col * (blockWidth + spacing);
                    blocks.add(new Block(x, rowY, blockWidth, blockHeight, row + 1));
                }
            }
        }


    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.clipRect(0, 0, getWidth(), gameAreaHeight);
        canvas.drawColor(Color.DKGRAY);

        List<Block> toRemove = new ArrayList<>();
        List<Cirlce> powerRemove = new ArrayList<>();
        List<Cirlce> ballRemove = new ArrayList<>();

        for (Block block : blocks) {
            canvas.drawRect(block.getX(), block.getY(), block.getRightEdge(), block.getBottomEdge(), block.getPaint());
            if (Collision(block, ball) && block.getDurability() <= 0) {
                Audio.playSound("pop", 1.0f);
                if (block.getPowerUpChance() == 15) Power.add(block.getPowerUp());
                toRemove.add(block);
                score += 10;
            }
        }

        for (Cirlce power : Power) {
            canvas.drawCircle(power.getX(), power.getY(), power.getR(), powerPaint);
            Movement(power);
            if (Collision(paddle, power) || power.getY() > gameAreaHeight) {
                powerRemove.add(power);
                if (Collision(paddle, power)) {
                    PBalls.add(new Cirlce(power.getX(), power.getY() - 30, 30));
                }
            }
        }

        for (Cirlce b : PBalls) {
            canvas.drawCircle(b.getX(), b.getY(), b.getR(), powerPaint);
            Movement(b);
            for (Block block : blocks) {
                if (Collision(block, b) && block.getDurability() <= 0) {
                    Audio.playSound("pop", 1.0f);
                    toRemove.add(block);
                    score += 10;
                }
            }
            if (b.getY() > gameAreaHeight) ballRemove.add(b);
            Collision(paddle, b);
        }

        blocks.removeAll(toRemove);
        Power.removeAll(powerRemove);
        PBalls.removeAll(ballRemove);

        canvas.drawCircle(ball.getX(), ball.getY(), ball.getR(), ballPaint);
        canvas.drawRect(paddle.getX(), paddle.getY(), paddle.getRightEdge(), paddle.getBottomEdge(), paddlePaint);

        Movement(ball);
        Collision(paddle, ball);

        if (paddle.getCenterX() < touchX) paddle.moveHorizontally(5);
        if (paddle.getCenterX() > touchX) paddle.moveHorizontally(-5);

        canvas.restore();
        canvas.save();
        canvas.clipRect(0, gameAreaHeight, getWidth(), screenHeight);
        canvas.drawColor(Color.LTGRAY);
        canvas.drawText("Level: " + levelNumber + "   Score: " + score, 50, gameAreaHeight + 100, textPaint);
        canvas.restore();

        if (blocks.isEmpty() && !GameWin) {
            Audio.playSound("Win", 1.0f);
            GameWin = true;
            waitForTapToEnd = true;
            HighScoreManager.reportScore(score);
            canvas.drawText("YOU WIN", getWidth() / 2f - 180, gameAreaHeight / 2f, winPaint);
        }

        if (GameLose && !isGameOverHandled) {
            isGameOverHandled = true;
            Audio.playSound("gameover", 1.0f);
            HighScoreManager.reportScore(score);
            waitForTapToEnd = true;
            canvas.drawText("YOU LOSE", getWidth() / 2f - 180, gameAreaHeight / 2f, losePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!GameWin && !GameLose && event.getY() < gameAreaHeight) {
                touchX = event.getX();
            } else if (waitForTapToEnd && context instanceof android.app.Activity) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("score", score);
                ((android.app.Activity) context).setResult(android.app.Activity.RESULT_OK, resultIntent);
                ((android.app.Activity) context).finish();
            }
        }
        return true;
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

        boolean collisionDetected = ballRight >= blockLeft && ballLeft <= blockRight && ballBottom >= blockTop && ballTop <= blockBottom;
        if (!collisionDetected) return false;

        float overlapLeft = ballRight - blockLeft;
        float overlapRight = blockRight - ballLeft;
        float overlapTop = ballBottom - blockTop;
        float overlapBottom = blockBottom - ballTop;

        if (Math.min(overlapTop, overlapBottom) < Math.min(overlapLeft, overlapRight)) {
            p.setCMy();
        } else {
            p.setCMx();
        }

        if (a instanceof Block) {
            Block block = (Block) a;
            block.hitBlock();
            if (block.getDurability() > 0) Audio.playSound("bounce", 1.0f);
        } else {
            if (!GameLose) Audio.playSound("bounce", 1.0f);
        }

        return true;
    }

    public void Movement(Cirlce p) {
        p.setMMy();
        p.setMMx();
        if (p.getX() > getWidth() - p.getR() || p.getX() < p.getR()) p.setCMx();
        if (p.getY() < p.getR()) p.setCMy();
        if (p.getY() > gameAreaHeight - p.getR() && p == ball) GameLose = true;
    }

    public int getScore() {
        return score;
    }

    public class GameThread extends Thread {
        private boolean isRunning = true;
        long stepPerSecond = 100 / fps;

        @Override
        public void run() {
            while (isRunning) {
                long startTime = System.currentTimeMillis();
                long sleepTime = stepPerSecond - (System.currentTimeMillis() - startTime);
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                gameHandler.sendEmptyMessage(0);
            }
        }

        public void stopThread() {
            isRunning = false;
            interrupt();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_1) {
            Apause = !Apause;
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            paddle.moveHorizontally(5);
            touchX += 5;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            paddle.moveHorizontally(-5);
            touchX -= 5;
        }
        return super.onKeyDown(keyCode, event);
    }
}