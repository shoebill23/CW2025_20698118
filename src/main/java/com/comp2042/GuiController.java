package com.comp2042;

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

public class GuiController implements Initializable, GameView, GameInputReceiver { //Main GUI controller for the game

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

    @Override
    public void initialize(URL location, ResourceBundle resources) { //Sets up UI, renderers, loop, and menus
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

    public void setEventListener(InputEventListener eventListener) { //Attaches keyboard handler to gamePanel
        this.eventListener = eventListener;
        gamePanel.setOnKeyPressed(new GameInputHandler(this, eventListener));
    }

    @Override
    public void initGameView(int[][] boardMatrix, ViewData brick) { //Builds board and initializes active/preview bricks
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


    private void onGameTick() { //Tick callback used by GameLoop
        if (!isPause.get() && !isGameOver.get()) {
            moveDown(new MoveEvent(EventSource.THREAD));
        }
    }

    @Override
    public void moveDown(MoveEvent event) { //Soft drop and apply scoring effects
        if (!isPause.get()) {
            DownData downData = eventListener.onDownEvent(event);
            handleClearRow(downData.getClearRow());
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    @Override
    public void hardDrop() { //Instant drop to bottom
        if (!isPause.get()) {
            DownData downData = eventListener.onHardDropEvent();
            handleClearRow(downData.getClearRow());
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    @Override
    public void refreshBrick(ViewData brick) { //Updates active brick visuals and next preview
        if (!isPause.get()) {
            boardRenderer.refreshBrick(brick);
        }
        nextBrickRenderer.render(brick.getNextBrickData());
    }

    @Override
    public void refreshGameBackground(int[][] board) { //Redraws background cells from board matrix
        boardRenderer.refreshBackground(board);
    }

    private void handleClearRow(ClearRow clearRow) { //Shows bonus popup and updates level
        if (clearRow != null && clearRow.getLinesRemoved() > 0) {
            NotificationPanel notificationPanel = new NotificationPanel("+" + clearRow.getScoreBonus());
            groupNotification.getChildren().add(notificationPanel);
            notificationPanel.showScore(groupNotification.getChildren());

            gameLoop.onLinesCleared(clearRow.getLinesRemoved());
            updateLevelLabel();
        }
    }


    @Override
    public void togglePause() { //Toggles pause state, timers, music, and overlays
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

    @Override
    public void gameOver() { //Stops timers, saves score, and shows Game Over
        gameLoop.stop();
        timeAttackManager.stop();

        if (scoreProperty != null) {
            HighScoreManager.saveScore(scoreProperty.get(), timeAttackManager.isEnabled());
            updateHighScoreDisplay();
        }
        overlayManager.show(groupGameOver);
        isGameOver.set(true);
    }

    public void newGame() { //Resets state and starts a fresh game
        gameLoop.stop();
        timeAttackManager.stop();
        hideAllOverlays();

        eventListener.createNewGame(); // Will trigger initGameView

        updateHighScoreDisplay();
        gamePanel.requestFocus();
        isPause.set(false);
        isGameOver.set(false);
    }

    public void setTimeAttackMode(boolean timeAttackMode) { //Enables/disables Time Attack mode
        timeAttackManager.setEnabled(timeAttackMode);
        updateHighScoreDisplay();
    }


    public void resumeGame() { //Resumes game if paused
        if (isPause.get()) togglePause();
    }

    public void showControlsMenu() { //Displays controls overlay
        overlayManager.show(groupControls);
        overlayManager.hide(groupPause);
    }

    public void showPauseMenu() { //Displays pause overlay
        overlayManager.show(groupPause);
        overlayManager.hide(groupControls);
    }

    @Override
    public boolean isPaused() { return isPause.get(); } //Exposes paused state to handlers

    @Override
    public boolean isGameOver() { return isGameOver.get(); } //Exposes game over state

    public void setBoardBackgroundOpacity(double opacity) { //Adjusts black backdrop behind gameboard
        double clamped = Math.max(0.0, Math.min(1.0, opacity));
        gamePanel.setStyle("-fx-background-color: rgba(0,0,0," + clamped + ");");
    }

    @Override
    public void bindScore(IntegerProperty integerProperty) { //Binds score label to property
        this.scoreProperty = integerProperty;
        scoreLabel.textProperty().bind(integerProperty.asString());
    }

    @Override
    public void updateHoldBrick(int[][] holdBrickData) { //Updates Hold preview grid
        holdBrickRenderer.render(holdBrickData);
    }

    private void updateLevelLabel() { //Refreshes level label using GameLoop
        if (levelLabel != null) {
            levelLabel.setText(String.valueOf(gameLoop.getLevel()));
        }
    }

    private void updateHighScoreDisplay() { //Refreshes mode-specific high score label
        if (highScoreLabel != null) {
            highScoreLabel.setText(String.valueOf(HighScoreManager.getHighScore(timeAttackManager.isEnabled())));
        }
    }

    private void hideAllOverlays() { //Hides all overlay groups
        overlayManager.hide(groupPause);
        overlayManager.hide(groupGameOver);
        overlayManager.hide(groupControls);
    }

    private void setupFonts() { //Applies custom font to labels
        String fontFamily = FontLoader.getFontFamily();
        if (fontFamily != null && highScoreTitleLabel != null && highScoreLabel != null) {
            highScoreTitleLabel.setFont(FontLoader.getFont(UIConstants.FONT_SIZE_HIGH_SCORE_TITLE));
            highScoreLabel.setFont(FontLoader.getFont(UIConstants.FONT_SIZE_HIGH_SCORE_VALUE));
        }
    }
}