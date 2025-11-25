package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

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

    // Helpers
    private final OverlayManager overlayManager = new OverlayManager();
    private GridRenderer nextBrickRenderer;
    private GridRenderer holdBrickRenderer;

    private InputEventListener eventListener;
    private Rectangle[][] displayMatrix;
    private Rectangle[][] activeBrickRects;
    private Timeline timeLine;
    private IntegerProperty scoreProperty;
    private int level = 1;
    private int totalLinesCleared = 0;
    private boolean timeAttackMode = false;
    private Timeline timeAttackTimeline;
    private int remainingSeconds;

    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FontLoader.loadFont();
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        // 1. Initialize Renderers
        nextBrickRenderer = new GridRenderer(nextBrickPanel, UIConstants.PREVIEW_GRID_SIZE, UIConstants.PREVIEW_GRID_SIZE, UIConstants.PREVIEW_BRICK_SIZE);
        holdBrickRenderer = new GridRenderer(holdBrickPanel, UIConstants.PREVIEW_GRID_SIZE, UIConstants.PREVIEW_GRID_SIZE, UIConstants.PREVIEW_BRICK_SIZE);

        // 2. Load Menus
        overlayManager.loadMenu(groupPause, UIConstants.PAUSE_MENU_FXML, this);
        overlayManager.loadMenu(groupGameOver, UIConstants.GAME_OVER_MENU_FXML, this);
        overlayManager.loadMenu(groupControls, UIConstants.CONTROLS_MENU_FXML, this);

        hideAllOverlays();
        updateHighScoreDisplay();
        setupFonts();
    }

    // Called by Main or StartController to start the game logic
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
        // Attach input handler now that we have the listener
        gamePanel.setOnKeyPressed(new GameInputHandler(this, eventListener));
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        // Initialize Board Matrix
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = UIConstants.BOARD_OFFSET_ROW; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rect = new Rectangle(UIConstants.BRICK_SIZE, UIConstants.BRICK_SIZE);
                rect.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rect;
                gamePanel.add(rect, j, i - UIConstants.BOARD_OFFSET_ROW);
            }
        }

        createGridLines(boardMatrix[0].length, boardMatrix.length - UIConstants.BOARD_OFFSET_ROW);

        // Initialize Active Brick Rects
        activeBrickRects = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rect = new Rectangle(UIConstants.BRICK_SIZE, UIConstants.BRICK_SIZE);
                rect.setFill(ColorMapper.getColor(brick.getBrickData()[i][j]));
                activeBrickRects[i][j] = rect;
                brickPanel.add(rect, j, i);
            }
        }

        updateBrickPosition(brick);
        nextBrickRenderer.render(brick.getNextBrickData());
        holdBrickRenderer.render(null); // Clear hold

        // Start Loop
        level = 1;
        totalLinesCleared = 0;
        rebuildTimeline();

        if (timeTitleLabel != null) {
            timeTitleLabel.setVisible(timeAttackMode);
        }
        if (timeLabel != null) {
            timeLabel.setVisible(timeAttackMode);
        }
        if (timeAttackMode) {
            startTimeAttack();
        }
    }

    void refreshBrick(ViewData brick) {
        if (!isPause.get()) {
            updateBrickPosition(brick);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    activeBrickRects[i][j].setFill(ColorMapper.getColor(brick.getBrickData()[i][j]));
                    activeBrickRects[i][j].setArcWidth(UIConstants.RECTANGLE_ARC_SIZE);
                    activeBrickRects[i][j].setArcHeight(UIConstants.RECTANGLE_ARC_SIZE);
                }
            }
        }
        nextBrickRenderer.render(brick.getNextBrickData());
    }

    void moveDown(MoveEvent event) {
        if (!isPause.get()) {
            DownData downData = eventListener.onDownEvent(event);
            showScoreNotification(downData.getClearRow());
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    void hardDrop() {
        if (!isPause.get()) {
            DownData downData = eventListener.onHardDropEvent();
            showScoreNotification(downData.getClearRow());
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    void togglePause() {
        if (isGameOver.get()) return;

        if (isPause.get()) {
            timeLine.play();
            if (timeAttackTimeline != null) timeAttackTimeline.play();
            Main.setPaused(false);
            isPause.set(false);
            overlayManager.hide(groupPause);
        } else {
            timeLine.pause();
            if (timeAttackTimeline != null) timeAttackTimeline.pause();
            Main.setPaused(true);
            isPause.set(true);
            overlayManager.show(groupPause);
        }
        gamePanel.requestFocus();
    }

    // --- Logic Helpers ---

    private void updateBrickPosition(ViewData brick) {
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * UIConstants.BRICK_SIZE);
        brickPanel.setLayoutY(UIConstants.BRICK_PANEL_Y_OFFSET + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * UIConstants.BRICK_SIZE);
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = UIConstants.BOARD_OFFSET_ROW; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                displayMatrix[i][j].setFill(ColorMapper.getColor(board[i][j]));
                displayMatrix[i][j].setArcWidth(UIConstants.RECTANGLE_ARC_SIZE);
                displayMatrix[i][j].setArcHeight(UIConstants.RECTANGLE_ARC_SIZE);
            }
        }
    }

    private void showScoreNotification(ClearRow clearRow) {
        if (clearRow != null && clearRow.getLinesRemoved() > 0) {
            NotificationPanel notificationPanel = new NotificationPanel("+" + clearRow.getScoreBonus());
            groupNotification.getChildren().add(notificationPanel);
            notificationPanel.showScore(groupNotification.getChildren());
            onLinesCleared(clearRow.getLinesRemoved());
        }
    }

    private void onLinesCleared(int lines) {
        totalLinesCleared += lines;
        int newLevel = (totalLinesCleared / 10) + 1;
        if (newLevel != level) {
            level = newLevel;
            rebuildTimeline();
        }
    }

    private Duration getCurrentDropDuration() {
        double seconds = 0.75 * Math.pow(0.8 - ((level - 1) * 0.010), (level - 1));
        return Duration.seconds(seconds);
    }

    private void rebuildTimeline() {
        boolean shouldPlay = !isPause.get() && !isGameOver.get();
        if (timeLine != null) {
            timeLine.stop();
        }
        timeLine = new Timeline(new KeyFrame(getCurrentDropDuration(),
                ae -> moveDown(new MoveEvent(EventSource.THREAD))));
        timeLine.setCycleCount(Animation.INDEFINITE);
        if (levelLabel != null) {
            levelLabel.setText(String.valueOf(level));
        }
        if (shouldPlay) {
            timeLine.play();
        }
    }

    public void setTimeAttackMode(boolean timeAttackMode) {
        this.timeAttackMode = timeAttackMode;
        updateHighScoreDisplay();
    }

    private void startTimeAttack() {
        remainingSeconds = 60;
        if (timeLabel != null) {
            timeLabel.setText(String.valueOf(remainingSeconds));
        }
        timeAttackTimeline = new Timeline(new KeyFrame(Duration.seconds(1), ae -> {
            if (!isPause.get() && !isGameOver.get()) {
                remainingSeconds--;
                if (timeLabel != null) {
                    timeLabel.setText(String.valueOf(Math.max(remainingSeconds, 0)));
                }
                if (remainingSeconds <= 0) {
                    gameOver();
                }
            }
        }));
        timeAttackTimeline.setCycleCount(Animation.INDEFINITE);
        timeAttackTimeline.play();
    }

    // --- Menu Actions ---

    public void gameOver() {
        timeLine.stop();
        if (timeAttackTimeline != null) {
            timeAttackTimeline.stop();
        }
        if (scoreProperty != null) {
            HighScoreManager.saveScore(scoreProperty.get(), timeAttackMode);
            updateHighScoreDisplay();
        }
        overlayManager.show(groupGameOver);
        isGameOver.set(true);
    }

    public void newGame() {
        if (timeLine != null) {
            timeLine.stop();
        }
        if (timeAttackTimeline != null) {
            timeAttackTimeline.stop();
        }
        hideAllOverlays();
        eventListener.createNewGame();
        updateHighScoreDisplay();
        gamePanel.requestFocus();
        level = 1;
        totalLinesCleared = 0;
        isPause.set(false);
        isGameOver.set(false);
        rebuildTimeline();
        if (timeTitleLabel != null) {
            timeTitleLabel.setVisible(timeAttackMode);
        }
        if (timeLabel != null) {
            timeLabel.setVisible(timeAttackMode);
        }
        if (timeAttackMode) {
            startTimeAttack();
        }
    }

    public void resumeGame() {
        if (isPause.get()) togglePause();
    }

    public void showControlsMenu() {
        overlayManager.show(groupControls);
        overlayManager.hide(groupPause);
    }

    public void showPauseMenu() {
        overlayManager.show(groupPause);
        overlayManager.hide(groupControls);
    }

    // --- Getters/Setters/Init Helpers ---

    public boolean isPaused() { return isPause.get(); }
    public boolean isGameOver() { return isGameOver.get(); }

    public void bindScore(IntegerProperty integerProperty) {
        this.scoreProperty = integerProperty;
        scoreLabel.textProperty().bind(integerProperty.asString());
    }

    private void updateHighScoreDisplay() {
        if (highScoreLabel != null) {
            highScoreLabel.setText(String.valueOf(HighScoreManager.getHighScore(timeAttackMode)));
        }
    }

    public void updateHoldBrick(int[][] holdBrickData) {
        holdBrickRenderer.render(holdBrickData);
    }

    private void hideAllOverlays() {
        overlayManager.hide(groupPause);
        overlayManager.hide(groupGameOver);
        overlayManager.hide(groupControls);
    }

    private void setupFonts() {
        String fontFamily = FontLoader.getFontFamily();
        if (fontFamily != null && highScoreTitleLabel != null && highScoreLabel != null) {
            highScoreTitleLabel.setFont(FontLoader.getFont(UIConstants.FONT_SIZE_HIGH_SCORE_TITLE));
            highScoreLabel.setFont(FontLoader.getFont(UIConstants.FONT_SIZE_HIGH_SCORE_VALUE));
        }
    }

    private void createGridLines(int cols, int visibleRows) {
        if (gridLines == null) return;
        gridLines.getChildren().clear();

        double w = cols * UIConstants.BRICK_SIZE + (cols - 1) * UIConstants.GRID_HGAP;
        double h = visibleRows * UIConstants.BRICK_SIZE + (visibleRows - 1) * UIConstants.GRID_VGAP;

        for (int i = 0; i <= cols; i++) {
            double x = i * (UIConstants.BRICK_SIZE + UIConstants.GRID_HGAP);
            Line line = new Line(x, -UIConstants.GRID_STROKE_EXTENSION, x, h + UIConstants.GRID_STROKE_EXTENSION);
            line.setStroke(Color.WHITE);
            line.setStrokeWidth(UIConstants.GRID_LINE_WIDTH);
            gridLines.getChildren().add(line);
        }
        for (int i = 0; i <= visibleRows; i++) {
            double y = i * (UIConstants.BRICK_SIZE + UIConstants.GRID_VGAP);
            Line line = new Line(-UIConstants.GRID_STROKE_EXTENSION, y, w + UIConstants.GRID_STROKE_EXTENSION, y);
            line.setStroke(Color.WHITE);
            line.setStrokeWidth(UIConstants.GRID_LINE_WIDTH);
            gridLines.getChildren().add(line);
        }
    }
}