package com.comp2042;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class GameInputHandler implements EventHandler<KeyEvent> {
    private final GuiController controller;
    private final InputEventListener gameLogic;

    public GameInputHandler(GuiController controller, InputEventListener gameLogic) {
        this.controller = controller;
        this.gameLogic = gameLogic;
    }

    @Override
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
                controller.hardDrop();
                event.consume();
            }
            default -> {
                //Ignores unspecified key presses
            }

        }
    }
}