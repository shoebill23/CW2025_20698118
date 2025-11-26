package com.comp2042;

public interface GameInputReceiver {
    void moveDown(MoveEvent event);
    void hardDrop();
    void togglePause();
    void refreshBrick(ViewData brick);
    boolean isPaused();
    boolean isGameOver();
}