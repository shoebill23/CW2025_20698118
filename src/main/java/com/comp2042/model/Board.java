package com.comp2042.model;

import com.comp2042.model.data.ClearRow;
import com.comp2042.model.data.ViewData;

/**
 * Core board operations for Tetris gameplay.
 * Implementations manage movement, rotation, merging, clearing, scoring, hold, and game lifecycle.
 */
public interface Board {

    /**
     * Attempt to move the active brick down by one row.
     * @return true if the move succeeded; false if blocked
     */
    boolean moveBrickDown();

    /**
     * Attempt to move the active brick left by one column.
     * @return true if the move succeeded; false if blocked
     */
    boolean moveBrickLeft();

    /**
     * Attempt to move the active brick right by one column.
     * @return true if the move succeeded; false if blocked
     */
    boolean moveBrickRight();

    /**
     * Rotate the active brick with boundary collision checks.
     * @return true if rotation (with kicks) succeeded; false otherwise
     */
    boolean rotateLeftBrick();

    /**
     * Spawn a new brick at the start position.
     * @return true if immediate collision occurs (game over condition)
     */
    boolean createNewBrick();

    /**
     * Current background matrix (placed bricks).
     * @return board matrix
     */
    int[][] getBoardMatrix();

    /**
     * Current view data including active brick and next-preview matrix.
     * @return view snapshot
     */
    ViewData getViewData();

    /** Merge the active brick into the background matrix at its current offset. */
    void mergeBrickToBackground();

    /**
     * Check for and clear full rows.
     * @return result with lines removed, new matrix, and score bonus
     */
    ClearRow clearRows();

    /**
     * Access the score tracker.
     * @return score instance
     */
    Score getScore();

    /** Reset board state, score, hold, and spawn a new brick. */
    void newGame();
    
    /**
     * Whether blocks occupy the game-over sentinel row.
     * @return true if game-over row contains blocks
     */
    boolean isGameOver();
    
    /**
     * Hold/swap the current brick respecting once-per-brick rule.
     * @return updated view snapshot
     */
    ViewData holdBrick();
    
    /**
     * Matrix of the held brick's first rotation, or empty if none.
     * @return held brick matrix
     */
    int[][] getHoldBrickData();
    
    /**
     * Move to the lowest valid position.
     * @return number of rows dropped (for scoring)
     */
    int hardDrop();
}