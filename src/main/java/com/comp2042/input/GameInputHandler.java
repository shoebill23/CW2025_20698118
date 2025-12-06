package com.comp2042.input;

import com.comp2042.controller.GuiController;
import com.comp2042.model.data.EventSource;
import com.comp2042.model.data.MoveEvent;
import com.comp2042.model.data.ViewData;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Translates keyboard events into game actions via {@link GameInputReceiver} and {@link InputEventListener}.
 */
public class GameInputHandler implements EventHandler<KeyEvent> { //Handle user input events
    private final GameInputReceiver controller;
    private final InputEventListener gameLogic;
    private long lastHardDropTime = 0;
    private static final long HARD_DROP_COOLDOWN = 500;

    /**
     * Bind handler to the controller and game logic.
     * @param controller game view controller to apply actions
     * @param gameLogic logic API to execute moves/rotations/hold/drop
     */
    public GameInputHandler(GuiController controller, InputEventListener gameLogic) {
        this.controller = controller;
        this.gameLogic = gameLogic;
    }

    @Override
    /**
     * Map keys (arrows/WASD/SPACE/C/ESC) to movement, rotation, hold, drop, and pause.
     * @param event key event
     */
    public void handle(KeyEvent event) {
        // Always allow ESC to toggle pause
        if (event.getCode() == KeyCode.ESCAPE) {
            controller.togglePause();
            event.consume();
            return;
        }

        // Block other inputs if paused or game over
        if (controller.isPaused() || controller.isGameOver()) {
            return;
        }

        switch (event.getCode()) {
            case LEFT, A -> {
                controller.refreshBrick(gameLogic.onLeftEvent(new MoveEvent(EventSource.USER)));
                event.consume();
            }
            case RIGHT, D -> {
                controller.refreshBrick(gameLogic.onRightEvent(new MoveEvent(EventSource.USER)));
                event.consume();
            }
            case UP, W -> {
                controller.refreshBrick(gameLogic.onRotateEvent(new MoveEvent(EventSource.USER)));
                event.consume();
            }
            case DOWN, S -> {
                controller.moveDown(new MoveEvent(EventSource.USER));
                event.consume();
            }
            case C -> {
                ViewData viewData = gameLogic.onHoldEvent();
                controller.refreshBrick(viewData);
                event.consume();
            }
            case SPACE -> {
                long currentTime = System.currentTimeMillis();
                //Check if enough time has passed since the last drop
                if (currentTime - lastHardDropTime > HARD_DROP_COOLDOWN) {
                    controller.hardDrop();
                    lastHardDropTime = currentTime; // Reset the timer
                }

                event.consume();
            }
            default -> {
                //Ignores unspecified key presses
            }

        }
    }
}