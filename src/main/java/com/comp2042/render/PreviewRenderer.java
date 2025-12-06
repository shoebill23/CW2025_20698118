package com.comp2042.render;

import com.comp2042.general_utility.UIConstants;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

/**
 * Renders a small grid preview (Next/Hold) using a fixed-size matrix.
 */
public class PreviewRenderer {
    private final GridPane targetPane;
    private final int rows;
    private final int cols;
    private final int brickSize;
    private Rectangle[][] gridMatrix;

    /**
     * Create a preview renderer with specified grid dimensions and cell size.
     * @param targetPane grid pane to render into
     * @param rows number of rows
     * @param cols number of columns
     * @param brickSize cell size in pixels
     */
    public PreviewRenderer(GridPane targetPane, int rows, int cols, int brickSize) {
        this.targetPane = targetPane;
        this.rows = rows;
        this.cols = cols;
        this.brickSize = brickSize;
        initializeGrid();
    }

    private void initializeGrid() {
        targetPane.getChildren().clear();
        gridMatrix = new Rectangle[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Rectangle rect = new Rectangle(brickSize, brickSize);
                rect.setFill(ColorMapper.getColor(0));
                rect.setArcHeight(UIConstants.RECTANGLE_ARC_SIZE);
                rect.setArcWidth(UIConstants.RECTANGLE_ARC_SIZE);
                gridMatrix[i][j] = rect;
                targetPane.add(rect, j, i);
            }
        }
    }

    /**
     * Render the given matrix into the preview grid.
     * Clears grid when data is null.
     * @param data preview matrix (may be null)
     */
    public void render(int[][] data) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                gridMatrix[i][j].setFill(ColorMapper.getColor(0));
            }
        }
        if (data == null) return;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (i < rows && j < cols) {
                    gridMatrix[i][j].setFill(ColorMapper.getColor(data[i][j]));
                }
            }
        }
    }
}