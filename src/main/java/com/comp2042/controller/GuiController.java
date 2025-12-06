package com.comp2042.controller;

import com.comp2042.general_utility.UIConstants;
import com.comp2042.input.GameInputHandler;
import com.comp2042.input.GameInputReceiver;
import com.comp2042.input.InputEventListener;
import com.comp2042.main.Main;
import com.comp2042.model.GameLoop;
import com.comp2042.model.HighScoreManager;
import com.comp2042.model.TimeAttackManager;
import com.comp2042.model.data.*;
import com.comp2042.render.*;
import com.comp2042.render.utility.FontLoader;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * JavaFX GUI controller implementing the game's view and input receiver.
 * Coordinates renderers, overlays, timers, and forwards user input to the logic.
 */
public class GuiController implements Initializable, GameView, GameInputReceiver {

    @FXML private GridPane gamePanel;
    @FXML private GridPane brickPanel;
    @FXML private GridPane nextBrickPanel;
    @FXML private GridPane holdBrickPanel;
    @FXML private Group gridLines;
    @FXML private Group groupNotification;
    @FXML private Group groupPause;
    @FXML private Group groupGameOver;
    @FXML private Group groupControls;
    @FXML private Label levelLabel;
    @FXML private Label scoreLabel;
    @FXML private Label highScoreTitleLabel;
    @FXML private Label highScoreLabel;
    @FXML private Label timeTitleLabel;
    @FXML private Label timeLabel;

    private final OverlayManager overlayManager = new OverlayManager();
    private GameBoardRenderer boardRenderer;
    private PreviewRenderer nextBrickRenderer;
    private PreviewRenderer holdBrickRenderer;
    private GameLoop gameLoop;
    private TimeAttackManager timeAttackManager;

    private InputEventListener eventListener;
    private IntegerProperty scoreProperty;

    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    /**
     * Initialize UI components, renderers, game loop, time attack manager, and overlay menus.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FontLoader.loadFont();
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        setBoardBackgroundOpacity(UIConstants.BOARD_BACKGROUND_OPACITY);

        boardRenderer = new GameBoardRenderer(gamePanel, brickPanel, gridLines);
        nextBrickRenderer = new PreviewRenderer(nextBrickPanel, UIConstants.PREVIEW_GRID_SIZE, UIConstants.PREVIEW_GRID_SIZE, UIConstants.PREVIEW_BRICK_SIZE);
        holdBrickRenderer = new PreviewRenderer(holdBrickPanel, UIConstants.PREVIEW_GRID_SIZE, UIConstants.PREVIEW_GRID_SIZE, UIConstants.PREVIEW_BRICK_SIZE);

        gameLoop = new GameLoop(this::onGameTick);
        timeAttackManager = new TimeAttackManager(timeLabel, this::gameOver);

        overlayManager.loadMenu(groupPause, UIConstants.PAUSE_MENU_FXML, this);
        overlayManager.loadMenu(groupGameOver, UIConstants.GAME_OVER_MENU_FXML, this);
        overlayManager.loadMenu(groupControls, UIConstants.CONTROLS_MENU_FXML, this);

        hideAllOverlays();
        updateHighScoreDisplay();
        setupFonts();
    }

    /**
     * Attach the logic listener and register the keyboard handler on the game panel.
     * @param eventListener game logic listener
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
        gamePanel.setOnKeyPressed(new GameInputHandler(this, eventListener));
    }

    /**
     * Build the board and initialize active/preview bricks, then start timers.
     * @param boardMatrix initial background matrix
     * @param brick initial active brick view
     */
    @Override
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        boardRenderer.initBoard(boardMatrix, boardMatrix[0].length, boardMatrix.length - UIConstants.BOARD_OFFSET_ROW);
        boardRenderer.initActiveBrick(brick);

        nextBrickRenderer.render(brick.getNextBrickData());
        holdBrickRenderer.render(null);

        gameLoop.reset();
        gameLoop.start();
        updateLevelLabel();

        boolean isTimeAttack = timeAttackManager.isEnabled();
        if (timeTitleLabel != null) timeTitleLabel.setVisible(isTimeAttack);
        if (timeLabel != null) timeLabel.setVisible(isTimeAttack);
        timeAttackManager.start();
    }


    /**
     * Callback for the game loop tick; performs a soft drop when active.
     */
    private void onGameTick() {
        if (!isPause.get() && !isGameOver.get()) {
            moveDown(new MoveEvent(EventSource.THREAD));
        }
    }

    /**
     * Perform a soft drop and apply scoring/clear row updates.
     * @param event move event source
     */
    @Override
    public void moveDown(MoveEvent event) {
        if (!isPause.get()) {
            DownData downData = eventListener.onDownEvent(event);
            handleClearRow(downData.getClearRow());
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    /**
     * Perform a hard drop to the bottom and refresh the view.
     */
    @Override
    public void hardDrop() {
        if (!isPause.get()) {
            DownData downData = eventListener.onHardDropEvent();
            handleClearRow(downData.getClearRow());
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    /**
     * Update active brick visuals and the Next preview.
     * @param brick updated brick view
     */
    @Override
    public void refreshBrick(ViewData brick) {
        if (!isPause.get()) {
            boardRenderer.refreshBrick(brick);
        }
        nextBrickRenderer.render(brick.getNextBrickData());
    }

    /**
     * Redraw the background cells from the board matrix.
     * @param board latest background matrix
     */
    @Override
    public void refreshGameBackground(int[][] board) {
        boardRenderer.refreshBackground(board);
    }

    /**
     * Show bonus notification and update level on cleared rows.
     * @param clearRow clear-row result
     */
    private void handleClearRow(ClearRow clearRow) {
        if (clearRow != null && clearRow.getLinesRemoved() > 0) {
            NotificationPanel notificationPanel = new NotificationPanel("+" + clearRow.getScoreBonus());
            groupNotification.getChildren().add(notificationPanel);
            notificationPanel.showScore(groupNotification.getChildren());

            gameLoop.onLinesCleared(clearRow.getLinesRemoved());
            updateLevelLabel();
        }
    }


    /**
     * Toggle pause state, timers, music volume, and overlays.
     */
    @Override
    public void togglePause() {
        if (isGameOver.get()) return;

        if (isPause.get()) {
            Main.setPaused(false);
            isPause.set(false);
            gameLoop.resume();
            timeAttackManager.resume();
            overlayManager.hide(groupPause);
        } else {
            Main.setPaused(true);
            isPause.set(true);
            gameLoop.pause();
            timeAttackManager.pause();
            overlayManager.show(groupPause);
        }
        gamePanel.requestFocus();
    }

    /**
     * Stop timers, save score, and show the Game Over overlay.
     */
    @Override
    public void gameOver() {
        gameLoop.stop();
        timeAttackManager.stop();

        if (scoreProperty != null) {
            HighScoreManager.saveScore(scoreProperty.get(), timeAttackManager.isEnabled());
            updateHighScoreDisplay();
        }
        overlayManager.show(groupGameOver);
        isGameOver.set(true);
    }

    /**
     * Reset state and start a fresh game.
     */
    public void newGame() {
        gameLoop.stop();
        timeAttackManager.stop();
        hideAllOverlays();

        eventListener.createNewGame(); // Will trigger initGameView

        updateHighScoreDisplay();
        gamePanel.requestFocus();
        isPause.set(false);
        isGameOver.set(false);
    }

    /**
     * Enable or disable Time Attack mode.
     * @param timeAttackMode whether Time Attack is enabled
     */
    public void setTimeAttackMode(boolean timeAttackMode) {
        timeAttackManager.setEnabled(timeAttackMode);
        updateHighScoreDisplay();
    }


    /**
     * Resume the game if currently paused.
     */
    public void resumeGame() {
        if (isPause.get()) togglePause();
    }

    /**
     * Display the controls overlay.
     */
    public void showControlsMenu() {
        overlayManager.show(groupControls);
        overlayManager.hide(groupPause);
    }

    /**
     * Display the pause overlay.
     */
    public void showPauseMenu() {
        overlayManager.show(groupPause);
        overlayManager.hide(groupControls);
    }

    /**
     * Whether the game is paused.
     * @return true if paused
     */
    @Override
    public boolean isPaused() { return isPause.get(); }

    /**
     * Whether the game is over.
     * @return true if game over
     */
    @Override
    public boolean isGameOver() { return isGameOver.get(); }

    /**
     * Adjust the dark backdrop opacity behind the game board.
     * @param opacity value between 0.0 and 1.0
     */
    public void setBoardBackgroundOpacity(double opacity) {
        double clamped = Math.max(0.0, Math.min(1.0, opacity));
        gamePanel.setStyle("-fx-background-color: rgba(0,0,0," + clamped + ");");
    }

    /**
     * Bind the score label to the provided score property.
     * @param integerProperty score observable
     */
    @Override
    public void bindScore(IntegerProperty integerProperty) {
        this.scoreProperty = integerProperty;
        scoreLabel.textProperty().bind(integerProperty.asString());
    }

    /**
     * Update the Hold preview grid.
     * @param holdBrickData matrix for held brick preview
     */
    @Override
    public void updateHoldBrick(int[][] holdBrickData) {
        holdBrickRenderer.render(holdBrickData);
    }

    /**
     * Refresh the level label using the game loop level.
     */
    private void updateLevelLabel() {
        if (levelLabel != null) {
            levelLabel.setText(String.valueOf(gameLoop.getLevel()));
        }
    }

    /**
     * Refresh the high score label for the current mode.
     */
    private void updateHighScoreDisplay() {
        if (highScoreLabel != null) {
            highScoreLabel.setText(String.valueOf(HighScoreManager.getHighScore(timeAttackManager.isEnabled())));
        }
    }

    /**
     * Hide all overlay groups.
     */
    private void hideAllOverlays() {
        overlayManager.hide(groupPause);
        overlayManager.hide(groupGameOver);
        overlayManager.hide(groupControls);
    }

    /**
     * Apply custom font to labels if available.
     */
    private void setupFonts() {
        String fontFamily = FontLoader.getFontFamily();
        if (fontFamily != null && highScoreTitleLabel != null && highScoreLabel != null) {
            highScoreTitleLabel.setFont(FontLoader.getFont(UIConstants.FONT_SIZE_HIGH_SCORE_TITLE));
            highScoreLabel.setFont(FontLoader.getFont(UIConstants.FONT_SIZE_HIGH_SCORE_VALUE));
        }
    }
}