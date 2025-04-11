package com.example.firebase.levels;

import android.content.Context;

import com.example.firebase.game.Block;
import com.example.firebase.game.Cirlce;
import com.example.firebase.game.Objects;

import java.util.ArrayList;
import java.util.List;

public class LevelLoader {

    public static final int MAX_LEVEL = 5;
    public static class LevelData {
        public final List<Block> blocks;
        public final List<Cirlce> powerUps = new ArrayList<>();
        public final List<Cirlce> extraBalls = new ArrayList<>();
        public final int paddleHeight;
        public final int ballDx;
        public final int ballDy;

        public LevelData(List<Block> blocks, int paddleHeight, int ballDx, int ballDy) {
            this.blocks = blocks;
            this.paddleHeight = paddleHeight;
            this.ballDx = ballDx;
            this.ballDy = ballDy;
        }
    }

    public static LevelData loadLevel(Context context, int levelNumber, int screenWidth, int screenHeight) {
        List<Block> blocks = new ArrayList<>();
        int paddleHeight = 0;
        int ballDx = 6;
        int ballDy = 6;

        int blockWidth = (int) (screenWidth / 4.8);
        int blockHeight = (int) (screenWidth / 14.4);
        int spacing = (int) (screenWidth / 36);
        int startX = (int) (screenWidth / 14.4);
        int Y1 = (int) (screenHeight / 11);
        int Ydiff = (int) (screenHeight / 15);

        switch (levelNumber) {
            case 1:
                int smallWidth = (int) (blockWidth * 0.7);
                int medWidth = (int) (blockWidth * 0.85);

                blocks.add(new Block(startX + blockWidth - 2 * spacing, Y1, smallWidth, blockHeight, 1));
                blocks.add(new Block(startX + blockWidth - 2 * spacing + 2 * (smallWidth + spacing) + spacing, Y1, smallWidth, blockHeight, 1));
                blocks.add(new Block(startX + blockWidth + spacing + spacing / 2, Y1 + Ydiff, smallWidth, blockHeight, 2));
                blocks.add(new Block(startX + spacing * 2, Y1 + Ydiff, smallWidth, blockHeight, 2));
                blocks.add(new Block(startX + 2 * (blockWidth + spacing) - spacing / 2, Y1 + Ydiff, smallWidth, blockHeight, 3));
                blocks.add(new Block(startX + 2 * (blockWidth + spacing) + blockWidth - spacing, Y1 + Ydiff, smallWidth, blockHeight, 2));
                blocks.add(new Block(startX + (blockWidth / 2) + (blockWidth + spacing), Y1 + 2 * Ydiff, medWidth, blockHeight, 2));
                blocks.add(new Block(startX - spacing, Y1 + 2 * Ydiff, medWidth, blockHeight, 2));
                blocks.add(new Block(startX + 2 * (blockWidth + spacing) + blockWidth, Y1 + 2 * Ydiff, medWidth, blockHeight, 2));
                blocks.add(new Block(startX + spacing * 2, Y1 + 3 * Ydiff, smallWidth, blockHeight, 2));
                blocks.add(new Block(startX + blockWidth + spacing + spacing / 2, Y1 + 3 * Ydiff, smallWidth, blockHeight, 2));
                blocks.add(new Block(startX + 2 * (blockWidth + spacing) - spacing / 2, Y1 + 3 * Ydiff, smallWidth, blockHeight, 3));
                blocks.add(new Block(startX + 2 * (blockWidth + spacing) + blockWidth - spacing, Y1 + 3 * Ydiff, smallWidth, blockHeight, 2));
                blocks.add(new Block(startX + blockWidth - 2 * spacing, Y1 + 4 * Ydiff, smallWidth, blockHeight, 1));
                blocks.add(new Block(startX + blockWidth - 2 * spacing + 2 * (smallWidth + spacing) + spacing, Y1 + 4 * Ydiff, smallWidth, blockHeight, 1));
                break;

            case 2:
                blocks.add(new Block(startX + 2 * (blockWidth + spacing), Y1, blockWidth, blockHeight, 1));
                blocks.add(new Block(startX + 3 * (blockWidth + spacing), Y1, blockWidth, blockHeight, 3));
                blocks.add(new Block(startX + (blockWidth / 2), Y1 + Ydiff, blockWidth, blockHeight, 2));
                blocks.add(new Block(startX + (blockWidth / 2) + blockWidth + spacing, Y1 + Ydiff, blockWidth, blockHeight, 1));
                blocks.add(new Block(startX + (blockWidth / 2) + 2 * (blockWidth + spacing), Y1 + Ydiff, blockWidth, blockHeight, 2));
                blocks.add(new Block(startX + blockWidth, Y1 + 2 * Ydiff, blockWidth, blockHeight, 3));
                break;

            case 3:
                blocks.add(new Block(startX, Y1, blockWidth, blockHeight, 1));
                blocks.add(new Block(startX + 3 * (blockWidth + spacing), Y1, blockWidth, blockHeight, 1));
                blocks.add(new Block(startX + (blockWidth / 2), Y1 + Ydiff + spacing, blockWidth, blockHeight, 2));
                blocks.add(new Block(startX + (blockWidth / 2) + 2 * (blockWidth + spacing), Y1 + Ydiff + spacing, blockWidth, blockHeight, 2));
                blocks.add(new Block(startX + blockWidth, Y1 + 2 * Ydiff + 2 * spacing, blockWidth, blockHeight, 3));
                blocks.add(new Block(startX + 2 * (blockWidth + spacing), Y1 + 2 * Ydiff + 2 * spacing, blockWidth, blockHeight, 3));
                blocks.add(new Block(startX + (blockWidth / 2) + blockWidth + spacing, Y1 + 3 * Ydiff + 3 * spacing, blockWidth, blockHeight, 4));
                break;

            case 4:
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
                break;

            case 5:
                blocks.add(new Block(startX, Y1, blockWidth, blockHeight, 1));
                blocks.add(new Block(startX + (blockWidth / 2) + 2 * (blockWidth + spacing), Y1 + Ydiff, blockWidth, blockHeight, 2));
                blocks.add(new Block(startX + blockWidth, Y1 + 2 * Ydiff, blockWidth, blockHeight, 1));
                break;
            default:
                break;
        }

        return new LevelData(blocks, paddleHeight, ballDx, ballDy);
    }
}
