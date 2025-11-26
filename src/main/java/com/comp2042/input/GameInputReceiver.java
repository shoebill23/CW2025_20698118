package com.comp2042.input;

import com.comp2042.model.data.MoveEvent;
import com.comp2042.model.data.ViewData;

public interface GameInputReceiver {
    void moveDown(MoveEvent event);
    void hardDrop();
    void togglePause();
    void refreshBrick(ViewData brick);
    boolean isPaused();
    boolean isGameOver();
}