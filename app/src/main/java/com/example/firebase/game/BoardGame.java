package com.example.firebase.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.firebase.HighScoreManager;
import com.example.firebase.levels.LevelLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collection;
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
    private Texttospeech t;
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
        t = new Texttospeech(BoardGame.this.context);

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
        stopGameThreadSafely();
        Audio.release();
        t.shutdown();
        Log.d("BoardGame", "Resources released and game thread stopped.");


    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        destroy();
    }

    private void init() {
        stopGameThreadSafely();

        ballPaint = new Paint(); ballPaint.setColor(Color.BLUE);
        paddlePaint = new Paint(); paddlePaint.setColor(Color.CYAN);
        powerPaint = new Paint(); powerPaint.setColor(Color.MAGENTA);
        textPaint = new Paint(); textPaint.setColor(Color.BLACK); textPaint.setTextSize(50);
        winPaint = new Paint(); winPaint.setColor(Color.BLUE); winPaint.setTextSize(80);
        losePaint = new Paint(); losePaint.setColor(Color.GRAY); losePaint.setTextSize(50);

        initializeLevel();

        gameThread = new GameThread();
        gameThread.start();
    }

    private void initializeLevel() {
        LevelLoader.LevelData levelData = LevelLoader.loadLevel(context, levelNumber, screenWidth, screenHeight);
        this.blocks = levelData.blocks;
        this.Power = new ArrayList<>();
        this.PBalls = new ArrayList<>();
        this.PaddleHight = levelData.paddleHeight;

        // Reset ball position to center and above the paddle
        float startX = screenWidth / 2f;
        float startY = gameAreaHeight - 100 - PaddleHight;
        ball.setX(startX);
        ball.setY(startY);
        if (levelNumber != 1){
            Intent intent1=new Intent(BoardGame.this.context, MusicService.class);
            context.startService(intent1);
            pause = false;
        }

        // Reset direction: dx from level, dy forced upward
        ball.setDx(levelData.ballDx);
        ball.setDy(-Math.abs(levelData.ballDy)); // Always go up at start
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if ((GameLose || GameWin) && waitForTapToEnd) {
            drawGameOverScreen(canvas);
            return;
        }

        List<Block> toRemove = new ArrayList<>();
        List<Cirlce> powerRemove = new ArrayList<>();
        List<Cirlce> ballRemove = new ArrayList<>();

        for (Block block : blocks) {
        PhysicsEngine.drawBlock(canvas, block);
            if (PhysicsEngine.detectCollision(block, ball,
                    () -> Audio.playSound("bounce", 1.0f),
                    () -> { if (!GameLose) Audio.playSound("bounce", 1.0f); })) {
                score += PhysicsEngine.tryDestroyBlock(block, Power, toRemove);
            }
        }

        for (Cirlce power : Power) {
            canvas.drawCircle(power.getX(), power.getY(), power.getR(), powerPaint);
            PhysicsEngine.move(power, getWidth(), gameAreaHeight, false, () -> {});
            boolean hit = PhysicsEngine.detectCollision(paddle, power,
                    () -> {},
                    () -> { if (!GameLose) Audio.playSound("bounce", 1.0f); });
            if (hit || power.getY() > gameAreaHeight) {
                powerRemove.add(power);
                if (hit) {
                    PBalls.addAll(PhysicsEngine.applyPowerEffect(power, paddle));
                }
            }
        }

        for (Cirlce b : PBalls) {
            canvas.drawCircle(b.getX(), b.getY(), b.getR(), powerPaint);
            PhysicsEngine.move(b, getWidth(), gameAreaHeight, false, () -> {});
            for (Block block : blocks) {
                if (PhysicsEngine.detectCollision(block, b,
                        () -> Audio.playSound("bounce", 1.0f),
                        () -> { if (!GameLose) Audio.playSound("bounce", 1.0f); })) {
                    score += PhysicsEngine.tryDestroyBlock(block, Power, toRemove);
                }
            }
            if (b.getY() > gameAreaHeight) ballRemove.add(b);
            PhysicsEngine.detectCollision(paddle, b,
                    () -> {},
                    () -> { if (!GameLose) Audio.playSound("bounce", 1.0f); });
        }

        blocks.removeAll(toRemove);
        Power.removeAll(powerRemove);
        PBalls.removeAll(ballRemove);

        PhysicsEngine.drawCircle(canvas, ball, ballPaint);
        PhysicsEngine.drawPaddle(canvas, paddle, paddlePaint);

        PhysicsEngine.move(ball, getWidth(), gameAreaHeight, true, () -> GameLose = true);
        PhysicsEngine.detectCollision(paddle, ball,
                () -> {},
                () -> { if (!GameLose) Audio.playSound("bounce", 1.0f); });

        PhysicsEngine.moveToward(paddle, touchX, 5);

        canvas.save();
        canvas.clipRect(0, gameAreaHeight, getWidth(), screenHeight);
        canvas.drawColor(Color.LTGRAY);
        canvas.drawText("Level: " + levelNumber + "   Score: " + score, 50, gameAreaHeight + 100, textPaint);
        canvas.restore();

        if (PhysicsEngine.didWin(blocks, GameWin)) {
            handleGameOver(canvas, true);
        }

        if (GameLose && !isGameOverHandled) {
            handleGameOver(canvas, false);
        }
    }

    private void drawGameOverScreen(Canvas canvas) {
        canvas.save();
        canvas.clipRect(0, 0, getWidth(), gameAreaHeight);
        canvas.drawColor(Color.DKGRAY);

        String message = getGameOverMessage();

        Paint paint = GameWin ? winPaint : losePaint;
        float textWidth = paint.measureText(message);
        canvas.drawText(message, (getWidth() - textWidth) / 2f, gameAreaHeight / 2f, paint);

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!GameWin && !GameLose && event.getY() < gameAreaHeight) {
                touchX = event.getX();
            } else if (waitForTapToEnd && context instanceof android.app.Activity) {
                if (GameWin && levelNumber < LevelLoader.MAX_LEVEL) {
                    // Just completed a level, go to the next one
                    GameLose = false;
                    GameWin = false;
                    waitForTapToEnd = false;
                    isGameOverHandled = false;
                    levelNumber++;
                    initializeLevel();
                    invalidate();
                } else {
                    // Either lost or beat final level → exit
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("score", score);
                    ((android.app.Activity) context).setResult(android.app.Activity.RESULT_OK, resultIntent);
                    ((android.app.Activity) context).finish();
                }
            }
        }
        return true;
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
        if (keyCode == KeyEvent.KEYCODE_1 && !isGameOverHandled) {
            Apause = !Apause;
            Intent intent1=new Intent(BoardGame.this.context, MusicService.class);
            if(Apause){
                intent1.setAction("PAUSE");
                context.startService(intent1);
            }
                else
                    context.startService(intent1);
            return true;
        }
        // arrow right
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            paddle.moveHorizontally(15);
            touchX += 15;
        }
        // arrow left
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            paddle.moveHorizontally(-15);
            touchX -= 15;
        }

        // zero to instantly pass the stage
        if (keyCode == KeyEvent.KEYCODE_0 && !GameWin && !GameLose) {
            blocks.clear(); // instantly empty blocks
            invalidate();   // force redraw to trigger win logic
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void stopGameThreadSafely() {
        if (gameThread != null && gameThread.isRunning) {
            gameThread.stopThread();
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("BoardGame", "Thread error", e);
            }
        }
    }

    private void handleGameOver(Canvas canvas, boolean win) {
        Intent intent1=new Intent(BoardGame.this.context, MusicService.class);

        isGameOverHandled = true;
        GameWin = win;
        waitForTapToEnd = true;

        if(win) {
            intent1.setAction("PAUSE");
            context.startService(intent1);
        } else {
            context.stopService(intent1);
        }

        Audio.playSound(win ? "Win" : "gameover", 1.0f);
        HighScoreManager.reportScore(score);

        String message = getGameOverMessage();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            t.speak(message);
        }, 2000);

    }

    private String getGameOverMessage() {
        boolean isFinalLevel = levelNumber == LevelLoader.MAX_LEVEL;
        return GameWin
                ? (isFinalLevel ? "YOU WIN" : "LEVEL COMPLETED")
                : "YOU LOSE";
    }
}