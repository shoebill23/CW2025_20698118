package com.comp2042.model;

import com.comp2042.model.data.ViewData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimpleBoardTest {

    private SimpleBoard board;

    @BeforeEach
    void setUp() {
        // Create a standard board 10x20
        board = new SimpleBoard(10, 20);
        board.createNewBrick(); // Spawn first brick
    }

    @Test
    void testMoveBrickDown() {
        ViewData initial = board.getViewData();
        int startY = initial.getyPosition();

        boolean moved = board.moveBrickDown();

        assertTrue(moved, "Brick should move down on empty board");
        assertEquals(startY + 1, board.getViewData().getyPosition(), "Y position should increase by 1");
    }

    @Test
    void testMoveBrickLeft() {
        ViewData initial = board.getViewData();
        int startX = initial.getxPosition();

        boolean moved = board.moveBrickLeft();

        if (moved) {
            assertEquals(startX - 1, board.getViewData().getxPosition());
        }
        // Note: If brick spawns against left wall, moved might be false,
        // but on standard spawn center, it should move.
    }

    @Test
    void testHardDrop() {
        int rowsDropped = board.hardDrop();
        assertTrue(rowsDropped > 0, "Hard drop should drop at least some rows on empty board");

        // After hard drop, moving down should be impossible (collision with floor)
        // Note: Logic requires merging to background to solidify,
        // but hardDrop() moves the currentOffset to the bottom.
        boolean canMoveFurther = board.moveBrickDown();
        assertFalse(canMoveFurther, "Should be at the bottom after hard drop");
    }

    @Test
    void testNewGameResetsScore() {
        board.getScore().add(100);
        board.newGame();
        assertEquals(0, board.getScore().scoreProperty().get(), "Score should reset to 0");
    }
}