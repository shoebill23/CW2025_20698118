package com.comp2042.logic.bricks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RandomBrickGeneratorTest {

    private RandomBrickGenerator generator;

    @BeforeEach
    void setUp() {
        // This method runs before each @Test method.
        // It's a good place to initialize objects.
        generator = new RandomBrickGenerator();
    }

    @Test
    void getBrick_shouldNotBeNull() {
        // 1. Call the method we are testing
        Brick brick = generator.getBrick();

        // 2. Assert the result is what we expect
        assertNotNull(brick, "The generated brick should not be null.");
    }

    @Test
    void getBrick_sevenBagSystem_shouldContainAllSevenUniqueBricks() {
        Set<Class<? extends Brick>> brickTypes = new HashSet<>();
        // Pull 7 bricks, which should be one full "bag"
        for (int i = 0; i < 7; i++) {
            Brick brick = generator.getBrick();
            assertNotNull(brick);
            brickTypes.add(brick.getClass());
        }

        // Assert that we received 7 unique brick types
        assertEquals(7, brickTypes.size(), "A full bag of 7 bricks should contain 7 unique brick types.");
    }

    @Test
    void getNextBrick_shouldBeSameAsGetBrick() {
        Brick nextBrickPeek = generator.getNextBrick();
        Brick nextBrickPolled = generator.getBrick();

        // Assert that peeked brick is the same instance as the polled brick
        assertSame(nextBrickPeek, nextBrickPolled, "getNextBrick() should return the same brick that getBrick() will return next.");
    }
}