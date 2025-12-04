package com.comp2042.render;

import com.comp2042.model.data.ViewData;
import com.comp2042.input.InputEventListener;
import javafx.beans.property.IntegerProperty;

/**
 * UI contract for the game's view layer.
 * Implementations render board state, active/preview bricks, bind score, and react to lifecycle events.
 */
public interface GameView {
    /**
     * Initialize the view with the starting board matrix and the active brick visuals.
     * @param boardMatrix background matrix to render
     * @param brick initial active brick view
     */
    void initGameView(int[][] boardMatrix, ViewData brick);
    /**
     * Redraw the background board cells from the given matrix.
     * @param board latest background matrix
     */
    void refreshGameBackground(int[][] board);
    /**
     * Update the active brick and the Next preview visuals.
     * @param brick updated active brick view
     */
    void refreshBrick(ViewData brick);
    /**
     * Update the Hold preview grid.
     * @param holdBrickData matrix for the held brick preview
     */
    void updateHoldBrick(int[][] holdBrickData);
    /**
     * Bind the score property to the score label for live updates.
     * @param scoreProperty observable score property
     */
    void bindScore(IntegerProperty scoreProperty);
    /** Signal that the game has finished and show the appropriate overlay. */
    void gameOver();
    /**
     * Attach the logic listener so the view can forward user input to the game.
     * @param listener game logic event listener
     */
    void setEventListener(InputEventListener listener);
}