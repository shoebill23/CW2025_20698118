package com.comp2042;

import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

public class GridRenderer {
    private final GridPane targetPane;
    private final int rows;
    private final int cols;
    private final int brickSize;
    private Rectangle[][] gridMatrix;

    public GridRenderer(GridPane targetPane, int rows, int cols, int brickSize) {
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
                rect.setFill(ColorMapper.getColor(0)); // Transparent
                rect.setArcHeight(UIConstants.RECTANGLE_ARC_SIZE);
                rect.setArcWidth(UIConstants.RECTANGLE_ARC_SIZE);
                gridMatrix[i][j] = rect;
                targetPane.add(rect, j, i);
            }
        }
    }

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