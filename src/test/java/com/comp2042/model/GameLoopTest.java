package com.comp2042.model;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameLoopTest {

    @BeforeAll
    static void initJfxRuntime() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit already initialized, ignore
        }
    }

    @Test
    void testLevelUpLogic() {
        GameLoop loop = new GameLoop(() -> {});

        assertEquals(1, loop.getLevel(), "Start level should be 1");

        loop.onLinesCleared(5);
        assertEquals(1, loop.getLevel());

        loop.onLinesCleared(5);
        assertEquals(2, loop.getLevel());

        loop.onLinesCleared(10);
        assertEquals(3, loop.getLevel());
    }

    @Test
    void testReset() {
        GameLoop loop = new GameLoop(() -> {});

        loop.onLinesCleared(20);
        assertEquals(3, loop.getLevel());

        loop.reset();

        assertEquals(1, loop.getLevel(), "Should reset to level 1");
    }
}