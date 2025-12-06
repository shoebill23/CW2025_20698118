package com.comp2042.render;

import com.comp2042.model.data.ViewData;
import com.comp2042.general_utility.UIConstants;
import javafx.scene.Group;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * Renders the game board background and the active brick on top of it.
 * Manages grid lines and updates visuals based on board/brick state.
 */
public class GameBoardRenderer {

    private final GridPane gamePanel;
    private final GridPane brickPanel;
    private final Group gridLines;

    private Rectangle[][] displayMatrix;
    private Rectangle[][] activeBrickRects;

    /**
     * Create a renderer for the board and active brick.
     * @param gamePanel grid for background cells
     * @param brickPanel grid for the active brick
     * @param gridLines overlay group used to draw grid lines and border
     */
    public GameBoardRenderer(GridPane gamePanel, GridPane brickPanel, Group gridLines) {
        this.gamePanel = gamePanel;
        this.brickPanel = brickPanel;
        this.gridLines = gridLines;
    }

    /**
     * Initialize the background cell rectangles and draw grid lines.
     * @param boardMatrix initial board matrix
     * @param cols number of columns
     * @param visibleRows number of visible rows
     */
    public void initBoard(int[][] boardMatrix, int cols, int visibleRows) {
        gamePanel.getChildren().clear();
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];

        for (int i = UIConstants.BOARD_OFFSET_ROW; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rect = new Rectangle(UIConstants.BRICK_SIZE, UIConstants.BRICK_SIZE);
                rect.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rect;
                gamePanel.add(rect, j, i - UIConstants.BOARD_OFFSET_ROW);
            }
        }
        drawGridLines(cols, visibleRows);
    }

    /**
     * Initialize the active brick rectangles and position them.
     * @param brick active brick view data
     */
    public void initActiveBrick(ViewData brick) {
        brickPanel.getChildren().clear();
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
    }

    /**
     * Update the active brick panel position based on its offsets.
     * @param brick active brick view data
     */
    public void updateBrickPosition(ViewData brick) {
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * UIConstants.BRICK_SIZE);
        brickPanel.setLayoutY(UIConstants.BRICK_PANEL_Y_OFFSET + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * UIConstants.BRICK_SIZE);
    }

    /**
     * Refresh active brick cells and rounded corners.
     * @param brick active brick view data
     */
    public void refreshBrick(ViewData brick) {
        updateBrickPosition(brick);
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                activeBrickRects[i][j].setFill(ColorMapper.getColor(brick.getBrickData()[i][j]));
                activeBrickRects[i][j].setArcWidth(UIConstants.RECTANGLE_ARC_SIZE);
                activeBrickRects[i][j].setArcHeight(UIConstants.RECTANGLE_ARC_SIZE);
            }
        }
    }

    /**
     * Refresh background cells colors and rounded corners.
     * @param board latest board matrix
     */
    public void refreshBackground(int[][] board) {
        for (int i = UIConstants.BOARD_OFFSET_ROW; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                displayMatrix[i][j].setFill(ColorMapper.getColor(board[i][j]));
                displayMatrix[i][j].setArcWidth(UIConstants.RECTANGLE_ARC_SIZE);
                displayMatrix[i][j].setArcHeight(UIConstants.RECTANGLE_ARC_SIZE);
            }
        }
    }

    /**
     * Draw grid lines and border around the board.
     */
    private void drawGridLines(int cols, int visibleRows) {
        if (gridLines == null) return;
        gridLines.getChildren().clear();

        double w = cols * UIConstants.BRICK_SIZE + (cols - 1) * UIConstants.GRID_HGAP;
        double h = visibleRows * UIConstants.BRICK_SIZE + (visibleRows - 1) * UIConstants.GRID_VGAP;

        for (int i = 0; i <= cols; i++) { //Vertical
            double x = i * (UIConstants.BRICK_SIZE + UIConstants.GRID_HGAP);
            Line line = new Line(x, -UIConstants.GRID_STROKE_EXTENSION, x, h + UIConstants.GRID_STROKE_EXTENSION);
            line.setStroke(Color.web("#D9D9D9"));
            line.setStrokeWidth(UIConstants.GRID_LINE_WIDTH);
            gridLines.getChildren().add(line);
        }
        for (int i = 0; i <= visibleRows; i++) { //Horizontal
            double y = i * (UIConstants.BRICK_SIZE + UIConstants.GRID_VGAP);
            Line line = new Line(-UIConstants.GRID_STROKE_EXTENSION, y, w + UIConstants.GRID_STROKE_EXTENSION, y);
            line.setStroke(Color.web("#D9D9D9"));
            line.setStrokeWidth(UIConstants.GRID_LINE_WIDTH);
            gridLines.getChildren().add(line);
        }
        Rectangle border = new Rectangle(-UIConstants.GRID_STROKE_EXTENSION,
                -UIConstants.GRID_STROKE_EXTENSION,
                w + 2 * UIConstants.GRID_STROKE_EXTENSION,
                h + 2 * UIConstants.GRID_STROKE_EXTENSION);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.BLACK);
        border.setStrokeWidth(4);
        gridLines.getChildren().add(border);
    }
}