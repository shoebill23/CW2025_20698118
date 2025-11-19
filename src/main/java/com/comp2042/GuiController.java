package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GridPane nextBrickPanel;

    @FXML
    private GridPane holdBrickPanel;

    @FXML
    private GameOverPanel gameOverPanel;

    @FXML
    private Group groupPause;

    private PauseMenuController pauseMenuController;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label holdLabel;
    
    @FXML
    private Label scoreTitleLabel;
    
    @FXML
    private Label nextBrickLabel;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;
    private Rectangle[][] nextBrickRectangles;
    private Rectangle[][] holdBrickRectangles;
    
    // Fixed grid size for preview panels (all bricks are 4x4)
    private static final int PREVIEW_GRID_SIZE = 4;
    private static final int PREVIEW_BRICK_SIZE = 15;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load font using FontLoader
        FontLoader.loadFont();

        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.C) {
                        ViewData viewData = eventListener.onHoldEvent();
                        refreshBrick(viewData);
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.SPACE) {
                        hardDrop();
                        keyEvent.consume();
                    }
                }
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }
                // ESC key works both when paused and unpaused
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    togglePause();
                    keyEvent.consume();
                }
            }
        });
        gameOverPanel.setVisible(false);
        groupPause.setVisible(false);
        
        // Load pause menu manually to avoid fx:include issues
        loadPauseMenu();
        

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);

        updateNextBrick(brick.getNextBrickData());
        
        // Initialize hold brick panel grid even if no brick is held
        initializeHoldBrickGrid();

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    private Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                returnPaint = Color.AQUA;
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET;
                break;
            case 3:
                returnPaint = Color.DARKGREEN;
                break;
            case 4:
                returnPaint = Color.YELLOW;
                break;
            case 5:
                returnPaint = Color.RED;
                break;
            case 6:
                returnPaint = Color.BEIGE;
                break;
            case 7:
                returnPaint = Color.BURLYWOOD;
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }


    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
        }
        updateNextBrick(brick.getNextBrickData());
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    private void hardDrop() {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onHardDropEvent();
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }
    
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
        scoreLabel.textProperty().bind(integerProperty.asString());
    }

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        groupPause.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    public void pauseGame(ActionEvent actionEvent) {
        togglePause();
    }

    private void loadPauseMenu() {
        try {
            URL pauseMenuLocation = getClass().getClassLoader().getResource("pauseMenu.fxml");
            if (pauseMenuLocation == null) {
                System.err.println("Error: Could not find pauseMenu.fxml resource");
                return;
            }
            
            javafx.fxml.FXMLLoader pauseMenuLoader = new javafx.fxml.FXMLLoader(pauseMenuLocation);
            javafx.scene.Parent pauseMenuRoot = pauseMenuLoader.load();
            pauseMenuController = pauseMenuLoader.getController();
            
            if (pauseMenuController != null) {
                pauseMenuController.setGuiController(this);
                groupPause.getChildren().add(pauseMenuRoot);
                System.out.println("Pause menu loaded successfully");
            } else {
                System.err.println("Warning: Pause menu controller is null after loading");
            }
        } catch (Exception e) {
            System.err.println("Error loading pause menu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void togglePause() {
        if (isGameOver.getValue() == Boolean.TRUE) {
            return; // Don't allow pausing when game is over
        }
        
        if (isPause.getValue() == Boolean.FALSE) {
            // Pause the game
            timeLine.pause();
            isPause.setValue(Boolean.TRUE);
            groupPause.setVisible(true);
        } else {
            // Resume the game
            timeLine.play();
            isPause.setValue(Boolean.FALSE);
            groupPause.setVisible(false);
        }
        gamePanel.requestFocus();
    }

    private void updateNextBrick(int[][] nextBrickData) {
        if (nextBrickData == null || nextBrickPanel == null) {
            return;
        }

        // Initialize fixed 4x4 grid if not already created
        if (nextBrickRectangles == null) {
            nextBrickPanel.getChildren().clear();
            nextBrickRectangles = new Rectangle[PREVIEW_GRID_SIZE][PREVIEW_GRID_SIZE];
            for (int i = 0; i < PREVIEW_GRID_SIZE; i++) {
                for (int j = 0; j < PREVIEW_GRID_SIZE; j++) {
                    Rectangle rectangle = new Rectangle(PREVIEW_BRICK_SIZE, PREVIEW_BRICK_SIZE);
                    rectangle.setFill(Color.TRANSPARENT);
                    setRectangleData(0, rectangle); // Initialize with transparent
                    nextBrickRectangles[i][j] = rectangle;
                    nextBrickPanel.add(rectangle, j, i);
                }
            }
        }

        // Calculate offset to center the brick within the 4x4 grid
        int[] offset = calculateCenteringOffset(nextBrickData, PREVIEW_GRID_SIZE);

        // Clear all rectangles first
        for (int i = 0; i < PREVIEW_GRID_SIZE; i++) {
            for (int j = 0; j < PREVIEW_GRID_SIZE; j++) {
                setRectangleData(0, nextBrickRectangles[i][j]);
            }
        }

        // Place the brick centered within the grid
        for (int i = 0; i < nextBrickData.length; i++) {
            for (int j = 0; j < nextBrickData[i].length; j++) {
                int targetRow = i + offset[0];
                int targetCol = j + offset[1];
                if (targetRow >= 0 && targetRow < PREVIEW_GRID_SIZE && 
                    targetCol >= 0 && targetCol < PREVIEW_GRID_SIZE) {
                    setRectangleData(nextBrickData[i][j], nextBrickRectangles[targetRow][targetCol]);
                }
            }
        }
    }
    
    /**
     * Initializes the hold brick panel grid structure.
     * This ensures the panel maintains its size even when no brick is held.
     */
    private void initializeHoldBrickGrid() {
        if (holdBrickPanel == null || holdBrickRectangles != null) {
            return; // Already initialized or panel doesn't exist
        }
        
        holdBrickPanel.getChildren().clear();
        holdBrickRectangles = new Rectangle[PREVIEW_GRID_SIZE][PREVIEW_GRID_SIZE];
        for (int i = 0; i < PREVIEW_GRID_SIZE; i++) {
            for (int j = 0; j < PREVIEW_GRID_SIZE; j++) {
                Rectangle rectangle = new Rectangle(PREVIEW_BRICK_SIZE, PREVIEW_BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                setRectangleData(0, rectangle); // Initialize with transparent
                holdBrickRectangles[i][j] = rectangle;
                holdBrickPanel.add(rectangle, j, i);
            }
        }
    }
    
    public void updateHoldBrick(int[][] holdBrickData) {
        if (holdBrickPanel == null) {
            return;
        }

        // Ensure grid is initialized (even if no brick is held)
        if (holdBrickRectangles == null) {
            initializeHoldBrickGrid();
        }

        // Clear all rectangles first
        for (int i = 0; i < PREVIEW_GRID_SIZE; i++) {
            for (int j = 0; j < PREVIEW_GRID_SIZE; j++) {
                setRectangleData(0, holdBrickRectangles[i][j]);
            }
        }

        // If there's a brick to display, place it centered within the grid
        if (holdBrickData != null) {
            // Calculate offset to center the brick within the 4x4 grid
            int[] offset = calculateCenteringOffset(holdBrickData, PREVIEW_GRID_SIZE);

            // Place the brick centered within the grid
            for (int i = 0; i < holdBrickData.length; i++) {
                for (int j = 0; j < holdBrickData[i].length; j++) {
                    int targetRow = i + offset[0];
                    int targetCol = j + offset[1];
                    if (targetRow >= 0 && targetRow < PREVIEW_GRID_SIZE && 
                        targetCol >= 0 && targetCol < PREVIEW_GRID_SIZE) {
                        setRectangleData(holdBrickData[i][j], holdBrickRectangles[targetRow][targetCol]);
                    }
                }
            }
        }
    }
    
    /**
     * Calculates the offset needed to center a brick within a fixed-size grid.
     * Finds the bounding box of the actual brick (non-zero values) and centers it.
     * Returns an array [rowOffset, colOffset] to center the brick.
     */
    private int[] calculateCenteringOffset(int[][] brickData, int gridSize) {
        if (brickData == null || brickData.length == 0 || brickData[0].length == 0) {
            return new int[]{0, 0};
        }
        
        // Find the bounding box of the actual brick (non-zero values)
        int minRow = brickData.length;
        int maxRow = -1;
        int minCol = brickData[0].length;
        int maxCol = -1;
        
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    minRow = Math.min(minRow, i);
                    maxRow = Math.max(maxRow, i);
                    minCol = Math.min(minCol, j);
                    maxCol = Math.max(maxCol, j);
                }
            }
        }
        
        // If no non-zero values found, return no offset
        if (maxRow < minRow || maxCol < minCol) {
            return new int[]{0, 0};
        }
        
        // Calculate the actual dimensions of the brick
        int actualBrickRows = maxRow - minRow + 1;
        int actualBrickCols = maxCol - minCol + 1;
        
        // Calculate offset to center the bounding box within the grid
        int rowOffset = (gridSize - actualBrickRows) / 2 - minRow;
        int colOffset = (gridSize - actualBrickCols) / 2 - minCol;
        
        return new int[]{rowOffset, colOffset};
    }
}
