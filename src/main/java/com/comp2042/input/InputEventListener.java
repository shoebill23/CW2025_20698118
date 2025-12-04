package com.comp2042.input;

import com.comp2042.model.data.DownData;
import com.comp2042.model.data.MoveEvent;
import com.comp2042.model.data.ViewData;

/**
 * Game logic API consumed by the view/input layer.
 * Methods perform movement, rotation, hold, dropping, and lifecycle actions.
 */
public interface InputEventListener {

    /**
     * Soft drop one row; awards soft-drop points if user-initiated.
     * @param event move event source (user or thread)
     * @return data including cleared rows (if any) and updated view
     */
    DownData onDownEvent(MoveEvent event);

    /**
     * Move active brick left if possible.
     * @param event move event source (user or thread)
     * @return updated view data
     */
    ViewData onLeftEvent(MoveEvent event);

    /**
     * Move active brick right if possible.
     * @param event move event source (user or thread)
     * @return updated view data
     */
    ViewData onRightEvent(MoveEvent event);

    /**
     * Rotate active brick with wall-kicks near edges.
     * @param event move event source (user or thread)
     * @return updated view data
     */
    ViewData onRotateEvent(MoveEvent event);

    /**
     * Hold current brick (swap with stored) respecting once-per-brick rule.
     * @return updated view data
     */
    ViewData onHoldEvent();

    /** Reset board state and start a new game, reinitializing the view. */
    void createNewGame();
    
    /**
     * Hard drop to the lowest valid position, merge, clear, score, and spawn next.
     * @return data including cleared rows (if any) and updated view
     */
    DownData onHardDropEvent();
}