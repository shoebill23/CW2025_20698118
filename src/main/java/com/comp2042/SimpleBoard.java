package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;

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
        return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(), brickGenerator.getNextBrick().getShapeMatrix().get(0));
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


    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        holdBrick = null;
        canHold = true;
        createNewBrick();
    }
    
    @Override
    public ViewData holdBrick() {
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
    
    public void resetCanHold() {
        canHold = true;
    }
    
    @Override
    public int hardDrop() {
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