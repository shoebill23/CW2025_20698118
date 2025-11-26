package com.comp2042.model;

import com.comp2042.model.data.ClearRow;
import com.comp2042.model.data.ViewData;

public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateLeftBrick();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getScore();

    void newGame();
    
    boolean isGameOver();
    
    ViewData holdBrick();
    
    int[][] getHoldBrickData();
    
    int hardDrop();
}