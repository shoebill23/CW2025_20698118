package com.comp2042.input;

import com.comp2042.model.data.MoveEvent;
import com.comp2042.model.data.ViewData;

/**
 * Minimal controller interface that the input handler calls to manipulate the game view/state.
 */
public interface GameInputReceiver {
    /**
     * Soft drop one row (user event).
     * @param event move event source
     */
    void moveDown(MoveEvent event);
    /** Execute a hard drop to the bottom. */
    void hardDrop();
    /** Toggle pause/resume state and overlays. */
    void togglePause();
    /**
     * Apply latest ViewData to the UI.
     * @param brick updated view for active brick and next preview
     */
    void refreshBrick(ViewData brick);
    /**
     * Whether the game is currently paused.
     * @return true when paused
     */
    boolean isPaused();
    /**
     * Whether the game is currently over.
     * @return true when game over
     */
    boolean isGameOver();
}