package com.example.firebase.game;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {

    // Moves the ball or power-up, handles wall bounce, and invokes callback if main ball falls below
    public static boolean move(Cirlce p, int width, int gameAreaHeight, boolean isMainBall) {
        p.setMMy();
        p.setMMx();
        if (p.getX() > width - p.getR() || p.getX() < p.getR()) {
            p.setCMx();
            Audio.playSound("bounce", 1.0f); // side wall bounce sound
        }
        if (p.getY() < p.getR()) {
            p.setCMy();
            Audio.playSound("bounce", 1.0f); // top wall bounce sound
        }
        return isMainBall && p.getY() > gameAreaHeight - p.getR();
    }

    // Detects collision between a circle and an object (block or paddle) and handles bounce logic
    public static boolean detectCollision(Objects a, Cirlce p) {
        float ballLeft = p.getX() - p.getR();
        float ballRight = p.getX() + p.getR();
        float ballTop = p.getY() - p.getR();
        float ballBottom = p.getY() + p.getR();

        float blockLeft = a.getX();
        float blockRight = a.getX() + a.getWidth();
        float blockTop = a.getY();
        float blockBottom = a.getY() + a.getHeight();

        boolean collision = ballRight >= blockLeft && ballLeft <= blockRight &&
                ballBottom >= blockTop && ballTop <= blockBottom;

        if (!collision) return false;

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
            ((Block) a).hitBlock();
        }

        return true;
    }

    // Checks if a circle has gone below the game area
    public static boolean isOutOfBounds(Cirlce c, int gameAreaHeight) {
        return c.getY() > gameAreaHeight;
    }

    // Moves the paddle toward a target X position using a specified speed
    public static void moveToward(Objects paddle, float targetX, float speed) {
        float center = paddle.getCenterX();
        float dx = targetX - center;

        if (Math.abs(dx) < speed) return;

        if (dx > 0) paddle.moveHorizontally(speed);
        else paddle.moveHorizontally(-speed);
    }

    // Returns true if all blocks are cleared and the player hasn't already won
    public static boolean didWin(List<Block> blocks, boolean alreadyWon) {
        return !alreadyWon && blocks.isEmpty();
    }

    // Spawns a new ball slightly above a power-up circle
    public static Cirlce spawnBallFromPower(Cirlce power) {
        return new Cirlce(power.getX(), power.getY() - 30, 30);
    }

    // Applies the effect of collecting a power-up (currently just spawns one new ball)
    public static List<Cirlce> applyPowerEffect(Cirlce power, Objects paddle) {
        List<Cirlce> result = new ArrayList<>();
        result.add(spawnBallFromPower(power));
        return result;
    }

    // Draws a block onto the canvas
    public static void drawBlock(android.graphics.Canvas canvas, Block block) {
        canvas.drawRect(block.getX(), block.getY(), block.getRightEdge(), block.getBottomEdge(), block.getPaint());
    }

    // Draws a circle onto the canvas using a given paint
    public static void drawCircle(Canvas canvas, Cirlce circle, Paint paint) {
        canvas.drawCircle(circle.getX(), circle.getY(), circle.getR(), paint);
    }

    // Draws the paddle onto the canvas using a given paint
    public static void drawPaddle(Canvas canvas, Objects paddle, Paint paint) {
        canvas.drawRect(paddle.getX(), paddle.getY(), paddle.getRightEdge(), paddle.getBottomEdge(), paint);
    }

    // Checks if a block should be destroyed and handles pop sound, power-up drop, and removal
    public static int tryDestroyBlock(Block block, List<Cirlce> powerList, List<Block> toRemove) {
        if (block.getDurability() <= 0) {
            Audio.playSound("pop", 1.0f);
            if (block.getPowerUpChance() == 1 && powerList != null) {
                powerList.add(block.getPowerUp());
            }
            toRemove.add(block);
            return 10;
        }
        return 0;
    }
}
