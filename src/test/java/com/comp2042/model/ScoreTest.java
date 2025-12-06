package com.comp2042.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ScoreTest {

    @Test
    void testAddScore() {
        Score score = new Score();
        assertEquals(0, score.scoreProperty().get());

        score.add(50);
        assertEquals(50, score.scoreProperty().get());

        score.add(50);
        assertEquals(100, score.scoreProperty().get());
    }

    @Test
    void testReset() {
        Score score = new Score();
        score.add(100);
        score.reset();
        assertEquals(0, score.scoreProperty().get());
    }
}