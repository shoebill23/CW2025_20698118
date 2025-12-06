package com.comp2042.model;

import com.comp2042.logic.bricks.JBrick; // Import a specific brick
import com.comp2042.logic.bricks.Brick;
import com.comp2042.model.data.NextShapeInfo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BrickRotatorTest {

    @Test
    void testRotationCycle() {
        BrickRotator rotator = new BrickRotator();

        // FIX: Do not use RandomBrickGenerator.
        // Use a specific brick with 4 rotations (like J, L, T, etc.)
        Brick brick = new JBrick();

        rotator.setBrick(brick);

        // Current should be 0
        // Get Next (should be 1)
        NextShapeInfo next = rotator.getNextShape();
        assertEquals(1, next.getPosition());

        // Apply it
        rotator.setCurrentShape(next.getPosition());

        // Get Next (should be 2)
        next = rotator.getNextShape();
        assertEquals(2, next.getPosition());
    }
}