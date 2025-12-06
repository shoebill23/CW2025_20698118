package com.comp2042.model;

import com.comp2042.model.data.ClearRow;
import com.comp2042.model.data.NextShapeInfo;
import com.comp2042.model.data.ViewData;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;

/**
 * Board implementation managing active brick state, movement/rotation with wall-kicks,
 * merging/clearing, hold logic, scoring, and game lifecycle.
 */
public class SimpleBoard implements Board {

    //Constants
    private static final int BRICK_START_X = 4;
    private static final int BRICK_START_Y = 0;
    private static final int GAME_OVER_ROW = 5;
    private static final int MOVE_DOWN_DELTA = 1;
    private static final int MOVE_LEFT_DELTA = -1;
    private static final int MOVE_RIGHT_DELTA = 1;

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private Brick holdBrick;
    private boolean canHold = true; // Can only hold once per brick placement

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, MOVE_DOWN_DELTA);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }


    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(MOVE_LEFT_DELTA, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(MOVE_RIGHT_DELTA, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    /**
     * Rotate the active brick; if blocked, attempts small horizontal wall-kicks
     * so rotation can succeed near boundaries.
     */
    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        int cx = (int) currentOffset.getX();
        int cy = (int) currentOffset.getY();

        if (!MatrixOperations.intersect(currentMatrix, nextShape.getShape(), cx, cy)) {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }

        int[] kicks = new int[]{-1, 1, -2, 2};
        for (int dx : kicks) {
            int nx = cx + dx;
            if (!MatrixOperations.intersect(currentMatrix, nextShape.getShape(), nx, cy)) {
                brickRotator.setCurrentShape(nextShape.getPosition());
                currentOffset = new java.awt.Point(nx, cy);
                return true;
            }
        }
        return false;
    }

    /** Spawn a new brick at the start position; returns true on immediate collision. */
    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(BRICK_START_X, BRICK_START_Y);
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }
    
    @Override
    public boolean isGameOver() {
        // Check if the game over row has any blocks
        int gameOverRow = GAME_OVER_ROW;
        if (gameOverRow >= currentGameMatrix.length) {
            return false;
        }
        for (int x = 0; x < currentGameMatrix[gameOverRow].length; x++) {
            if (currentGameMatrix[gameOverRow][x] != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        Brick next = brickGenerator.getNextBrick();
        int[][] nextMatrix = (next != null && next.getShapeMatrix() != null && !next.getShapeMatrix().isEmpty())
                ? next.getShapeMatrix().get(0)
                : new int[0][0];
        return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(), nextMatrix);
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }

    @Override
    public Score getScore() {
        return score;
    }


    /** Reset background, score, hold, and spawn the first brick; resets generator bag. */
    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        holdBrick = null;
        canHold = true;
        brickGenerator.reset();
        createNewBrick();
    }
    
    /**
     * Hold the current brick once per placement: first hold takes next; subsequent holds swap.
     * Returns updated view data.
     */
    @Override
    public ViewData holdBrick() { //Source: https://harddrop.com/wiki/Hold_piece
        if (!canHold) {
            return getViewData(); // Can't hold if already held this turn
        }
        
        Brick currentBrick = brickRotator.getBrick();
        
        if (holdBrick == null) {
            // First time holding - store current brick and get next brick
            holdBrick = currentBrick;
            Brick nextBrick = brickGenerator.getBrick();
            brickRotator.setBrick(nextBrick);
            currentOffset = new Point(BRICK_START_X, BRICK_START_Y);
            canHold = false;
        } else {
            // Swap current brick with hold brick
            Brick temp = holdBrick;
            holdBrick = currentBrick;
            brickRotator.setBrick(temp);
            currentOffset = new Point(BRICK_START_X, BRICK_START_Y);
            canHold = false;
        }
        
        return getViewData();
    }
    
    @Override
    public int[][] getHoldBrickData() {
        if (holdBrick == null) {
            return new int[0][0];
        }
        return holdBrick.getShapeMatrix().get(0); // Return first rotation
    }
    
    /** Allow holding again after a new brick spawns. */
    public void resetCanHold() {
        canHold = true;
    }
    
    /**
     * Move to the lowest valid Y by simulating downward steps; returns rows dropped for scoring.
     */
    @Override
    public int hardDrop() { //Source: https://stackoverflow.com/questions/16592898/tetris-hard-drop-logic
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        int[][] currentShape = brickRotator.getCurrentShape();
        
        // Store the starting Y position
        int startY = (int) currentOffset.getY();
        
        // Find the lowest valid Y position
        Point p = new Point(currentOffset);
        while (true) {
            Point nextP = new Point(p);
            nextP.translate(0, MOVE_DOWN_DELTA);
            boolean conflict = MatrixOperations.intersect(currentMatrix, currentShape, (int) nextP.getX(), (int) nextP.getY());
            if (conflict) {
                break; // The next position is invalid, so the current 'p' is the lowest valid spot.
            }
            p = nextP; // The next position is valid, so we continue from there.
        }
        // Move brick to the lowest valid position
        currentOffset = p;
        
        // Calculate and return the number of rows dropped
        int endY = (int) currentOffset.getY();
        return endY - startY;
    }
}