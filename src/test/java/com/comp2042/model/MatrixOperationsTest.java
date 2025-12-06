package com.comp2042.model;

import com.comp2042.model.data.ClearRow;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MatrixOperationsTest {

    @Test
    void testIntersect_NoCollision() {
        int[][] board = new int[10][10];
        int[][] brick = {
                {0, 0, 0, 0},
                {0, 1, 1, 0},
                {0, 1, 1, 0},
                {0, 0, 0, 0}
        };
        // Placing brick in the middle of empty board
        boolean result = MatrixOperations.intersect(board, brick, 4, 4);
        assertFalse(result, "Should not collide on empty board");
    }

    @Test
    void testIntersect_CollisionWithWall() {
        int[][] board = new int[10][10];
        int[][] brick = {
                {1, 1},
                {1, 1}
        };
        // Placing brick out of bounds (x = -1)
        boolean result = MatrixOperations.intersect(board, brick, -1, 0);
        assertTrue(result, "Should collide with left wall");
    }

    @Test
    void testMerge() {
        int[][] board = new int[5][5];
        int[][] brick = {{1}}; // A single 1x1 brick block

        // Merge brick at 2,2
        int[][] newBoard = MatrixOperations.merge(board, brick, 2, 2);

        assertEquals(1, newBoard[2][2], "Board should have the brick merged at the specific coordinate");
        assertEquals(0, newBoard[0][0], "Other parts of board should remain empty");
    }

    @Test
    void testCheckRemoving_FullRow() {
        int[][] board = new int[5][5];
        // Fill the bottom row (index 4) with 1s
        for(int i=0; i<5; i++) board[4][i] = 1;
        // Fill top row with 0s
        for(int i=0; i<5; i++) board[0][i] = 0;

        ClearRow result = MatrixOperations.checkRemoving(board);

        assertEquals(1, result.getLinesRemoved(), "Should clear exactly 1 line");
        assertEquals(0, result.getNewMatrix()[4][0], "Bottom row should now be empty (new line)");
        assertTrue(result.getScoreBonus() > 0, "Score should be awarded");
    }
}