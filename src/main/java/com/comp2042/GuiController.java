package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
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
    private Group gridLines;

    @FXML
    private GridPane nextBrickPanel;

    @FXML
    private GridPane holdBrickPanel;

    @FXML
    private Group groupPause;
    
    @FXML
    private Group groupGameOver;
    
    @FXML
    private Group groupControls;
    
    private GameOverMenuController gameOverMenuController;
    
    private ControlsMenuController controlsMenuController;

    @FXML
    private Label scoreLabel;

    
    @FXML
    private Label highScoreTitleLabel;
    
    @FXML
    private Label highScoreLabel;

    private PauseMenuController pauseMenuController;

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
                if (!isPause.get() && !isGameOver.get()) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventSource.USER));
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
                // ESC key works both when paused and unpaused
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    togglePause();
                    keyEvent.consume();
                }
            }
        });
        if (groupGameOver != null) {
            groupGameOver.setVisible(false);
        }
        if (groupPause != null) {
            groupPause.setVisible(false);
        }
        if (groupControls != null) {
            groupControls.setVisible(false);
        }
        
        // Load pause menu manually to avoid fx:include issues
        loadPauseMenu();
        
        // Load game over menu manually
        loadGameOverMenu();
        
        // Load controls menu manually
        loadControlsMenu();
        
        // Initialize high score display
        updateHighScoreDisplay();
        
        // Apply fonts to high score labels
        String fontFamily = FontLoader.loadFont();
        if (fontFamily != null) {
            if (highScoreTitleLabel != null) {
                highScoreTitleLabel.setFont(FontLoader.getFont(20));
            }
            if (highScoreLabel != null) {
                highScoreLabel.setFont(FontLoader.getFont(24));
            }
        }
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
        
        // Create white grid lines
        createGridLines(boardMatrix[0].length, boardMatrix.length - 2);

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
                ae -> moveDown(new MoveEvent(EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

   
    private void createGridLines(int columns, int visibleRows) {
        if (gridLines == null) {
            return;
        }
        
        gridLines.getChildren().clear();
        
        
        final double HGAP = 1.0;
        final double VGAP = 1.0;
        
        
        double cellSpacingX = BRICK_SIZE + HGAP;
        double cellSpacingY = BRICK_SIZE + VGAP;
        
        double totalWidth = columns * BRICK_SIZE + (columns - 1) * HGAP;
        double totalHeight = visibleRows * BRICK_SIZE + (visibleRows - 1) * VGAP;
        
        
        final double STROKE_EXTENSION = 0.5;
        

        for (int i = 0; i <= columns; i++) {
            double x = i * cellSpacingX;
            Line verticalLine = new Line(x, -STROKE_EXTENSION, x, totalHeight + STROKE_EXTENSION);
            verticalLine.setStroke(Color.WHITE);
            verticalLine.setStrokeWidth(0.5);
            gridLines.getChildren().add(verticalLine);
        }
        

        for (int i = 0; i <= visibleRows; i++) {
            double y = i * cellSpacingY;
            Line horizontalLine = new Line(-STROKE_EXTENSION, y, totalWidth + STROKE_EXTENSION, y);
            horizontalLine.setStroke(Color.WHITE);
            horizontalLine.setStrokeWidth(0.5);
            gridLines.getChildren().add(horizontalLine);
        }
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
            showScoreNotification(downData.getClearRow());
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    private void hardDrop() {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onHardDropEvent();
            showScoreNotification(downData.getClearRow());
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }
    
    /**
     * Shows a score notification when rows are cleared.
     * 
     * @param clearRow The ClearRow object containing score bonus information
     */
    private void showScoreNotification(ClearRow clearRow) {
        if (clearRow != null && clearRow.getLinesRemoved() > 0) {
            NotificationPanel notificationPanel = new NotificationPanel("+" + clearRow.getScoreBonus());
            groupNotification.getChildren().add(notificationPanel);
            notificationPanel.showScore(groupNotification.getChildren());
        }
    }
    
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    private IntegerProperty scoreProperty;
    
    public void bindScore(IntegerProperty integerProperty) {
        this.scoreProperty = integerProperty;
        scoreLabel.textProperty().bind(integerProperty.asString());
    }
    

    private void updateHighScoreDisplay() {
        int highScore = HighScoreManager.getHighScore();
        if (highScoreLabel != null) {
            highScoreLabel.setText(String.valueOf(highScore));
        }
    }

    public void gameOver() {
        timeLine.stop();
        System.out.println("gameOver() called");
        

        if (scoreProperty != null) {
            int currentScore = scoreProperty.get();
            HighScoreManager.saveScore(currentScore);
            System.out.println("Score saved: " + currentScore);


            updateHighScoreDisplay();
        }
        
        if (groupGameOver != null) {
            groupGameOver.setVisible(true);
            groupGameOver.toFront();
            System.out.println("groupGameOver.setVisible(true) - visible: " + groupGameOver.isVisible());
        } else {
            System.err.println("ERROR: groupGameOver is null!");
        }
        
        isGameOver.setValue(Boolean.TRUE);
    }

    public void resumeGame() {
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        if (groupPause != null) {
            groupPause.setVisible(false);
        }
        gamePanel.requestFocus();
    }

    public void newGame() {
        timeLine.stop();
        if (groupGameOver != null) {
            groupGameOver.setVisible(false);
        }
        if (groupPause != null) {
            groupPause.setVisible(false);
        }
        eventListener.createNewGame();


        updateHighScoreDisplay();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }


    private void loadPauseMenu() {
        if (groupPause == null) {
            System.err.println("ERROR: groupPause is null! Cannot load pause menu.");
            return;
        }
        
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
                groupPause.getChildren().clear(); // Clear previous content if any
                groupPause.getChildren().add(pauseMenuRoot);
                System.out.println("Pause menu loaded successfully into groupPause");
            } else {
                System.err.println("Warning: Pause menu controller is null after loading");
            }
        } catch (Exception e) {
            System.err.println("Error loading pause menu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadGameOverMenu() {
        if (groupGameOver == null) {
            System.err.println("ERROR: groupGameOver is null! Cannot load game over menu.");
            return;
        }
        
        try {
            URL gameOverMenuLocation = getClass().getClassLoader().getResource("gameOverMenu.fxml");
            if (gameOverMenuLocation == null) {
                System.err.println("Error: Could not find gameOverMenu.fxml resource");
                return;
            }
            
            javafx.fxml.FXMLLoader gameOverMenuLoader = new javafx.fxml.FXMLLoader(gameOverMenuLocation);
            javafx.scene.Parent gameOverMenuRoot = gameOverMenuLoader.load();
            gameOverMenuController = gameOverMenuLoader.getController();
            
            if (gameOverMenuController != null) {
                gameOverMenuController.setGuiController(this);
                groupGameOver.getChildren().clear(); 
                groupGameOver.getChildren().add(gameOverMenuRoot);
                System.out.println("Game over menu loaded successfully into groupGameOver");
            } else {
                System.err.println("Warning: Game over menu controller is null after loading");
            }
        } catch (Exception e) {
            System.err.println("Error loading game over menu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadControlsMenu() {
        if (groupControls == null) {
            System.err.println("ERROR: groupControls is null! Cannot load controls menu.");
            return;
        }
        
        try {
            URL controlsMenuLocation = getClass().getClassLoader().getResource("controlsMenu.fxml");
            if (controlsMenuLocation == null) {
                System.err.println("Error: Could not find controlsMenu.fxml resource");
                return;
            }
            
            javafx.fxml.FXMLLoader controlsMenuLoader = new javafx.fxml.FXMLLoader(controlsMenuLocation);
            javafx.scene.Parent controlsMenuRoot = controlsMenuLoader.load();
            controlsMenuController = controlsMenuLoader.getController();
            
            if (controlsMenuController != null) {
                controlsMenuController.setGuiController(this);
                groupControls.getChildren().clear(); 
                groupControls.getChildren().add(controlsMenuRoot);
                System.out.println("Controls menu loaded successfully into groupControls");
            } else {
                System.err.println("Warning: Controls menu controller is null after loading");
            }
        } catch (Exception e) {
            System.err.println("Error loading controls menu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void togglePause() {
        if (isGameOver.getValue() == Boolean.TRUE) {
            return; 
        }
        
        if (isPause.getValue() == Boolean.FALSE) {
            timeLine.pause();
            isPause.setValue(Boolean.TRUE);
            if (groupPause != null) {
                // Ensure menu is loaded
                if (groupPause.getChildren().isEmpty()) {
                    System.out.println("Warning: Pause menu is empty, reloading...");
                    loadPauseMenu();
                }
                groupPause.setVisible(true);
                groupPause.toFront();
                // Also ensure the parent brings it to front by removing and re-adding
                javafx.scene.Parent parent = groupPause.getParent();
                if (parent instanceof javafx.scene.layout.Pane) {
                    javafx.scene.layout.Pane pane = (javafx.scene.layout.Pane) parent;
                    pane.getChildren().remove(groupPause);
                    pane.getChildren().add(groupPause);
                }
                System.out.println("Pause menu set to visible - visible: " + groupPause.isVisible() + 
                                 ", children count: " + groupPause.getChildren().size());
            } else {
                System.err.println("ERROR: groupPause is null in togglePause()!");
            }
        } else {
            timeLine.play();
            isPause.setValue(Boolean.FALSE);
            if (groupPause != null) {
                groupPause.setVisible(false);
                System.out.println("Pause menu set to invisible");
            }
        }
        gamePanel.requestFocus();
    }
    
    public void showControlsMenu() {
        if (groupControls != null) {
            groupControls.setVisible(true);
            groupControls.toFront();
            if (groupPause != null) {
                groupPause.setVisible(false);
            }
            System.out.println("Controls menu set to visible");
        } else {
            System.err.println("ERROR: groupControls is null in showControlsMenu()!");
        }
    }
    
    public void showPauseMenu() {
        if (groupPause != null) {
            groupPause.setVisible(true);
            groupPause.toFront();
            if (groupControls != null) {
                groupControls.setVisible(false);
            }
            System.out.println("Pause menu set to visible");
        } else {
            System.err.println("ERROR: groupPause is null in showPauseMenu()!");
        }
    }

    private void updateNextBrick(int[][] nextBrickData) {
        if (nextBrickData == null || nextBrickPanel == null) {
            return;
        }

        if (nextBrickRectangles == null) {
            nextBrickPanel.getChildren().clear();
            nextBrickRectangles = new Rectangle[PREVIEW_GRID_SIZE][PREVIEW_GRID_SIZE];
            for (int i = 0; i < PREVIEW_GRID_SIZE; i++) {
                for (int j = 0; j < PREVIEW_GRID_SIZE; j++) {
                    Rectangle rectangle = new Rectangle(PREVIEW_BRICK_SIZE, PREVIEW_BRICK_SIZE);
                    rectangle.setFill(Color.TRANSPARENT);
                    setRectangleData(0, rectangle); 
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
    
    // Initializes the hold brick panel grid structure.
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
