package com.comp2042.render;

import com.comp2042.model.data.ViewData;
import com.comp2042.input.InputEventListener;
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