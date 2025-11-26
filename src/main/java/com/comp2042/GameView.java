package com.comp2042;

import javafx.beans.property.IntegerProperty;

public interface GameView {
    void initGameView(int[][] boardMatrix, ViewData brick);
    void refreshGameBackground(int[][] board);
    void refreshBrick(ViewData brick);
    void updateHoldBrick(int[][] holdBrickData);
    void bindScore(IntegerProperty scoreProperty);
    void gameOver();
    void setEventListener(InputEventListener listener);
}