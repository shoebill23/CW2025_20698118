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
import java.util.logging.Logger;

public class GuiController implements Initializable {

    private static final Logger logger = Logger.getLogger(GuiController.class.getName());

    @FXML private GridPane gamePanel;
    @FXML private GridPane brickPanel;
    @FXML private GridPane nextBrickPanel;
    @FXML private GridPane holdBrickPanel;
    @FXML private Group gridLines;
    @FXML private Group groupNotification;
    @FXML private Group groupPause;
    @FXML private Group groupGameOver;
    @FXML private Group groupControls;
    @FXML private Label scoreLabel;
    @FXML private Label highScoreTitleLabel;
    @FXML private Label highScoreLabel;

    // Helpers
    private final OverlayManager overlayManager = new OverlayManager();
    private GridRenderer nextBrickRenderer;
    private GridRenderer holdBrickRenderer;

    private InputEventListener eventListener;
    private Rectangle[][] displayMatrix;
    private Rectangle[][] activeBrickRects;
    private Timeline timeLine;
    private IntegerProperty scoreProperty;

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
        timeLine = new Timeline(new KeyFrame(Duration.millis(UIConstants.GAME_TICK_MS),
                ae -> moveDown(new MoveEvent(EventSource.THREAD))));
        timeLine.setCycleCount(Animation.INDEFINITE);
        timeLine.play();
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
            isPause.set(false);
            overlayManager.hide(groupPause);
        } else {
            timeLine.pause();
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
        }
    }

    // --- Menu Actions ---

    public void gameOver() {
        timeLine.stop();
        if (scoreProperty != null) {
            HighScoreManager.saveScore(scoreProperty.get());
            updateHighScoreDisplay();
        }
        overlayManager.show(groupGameOver);
        isGameOver.set(true);
    }

    public void newGame() {
        timeLine.stop();
        hideAllOverlays();
        eventListener.createNewGame();
        updateHighScoreDisplay();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.set(false);
        isGameOver.set(false);
    }

    public void resumeGame() {
        // Just reusing togglePause logic to ensure consistent state
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
            highScoreLabel.setText(String.valueOf(HighScoreManager.getHighScore()));
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