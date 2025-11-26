package com.comp2042;

public final class UIConstants {

    private UIConstants() {
        // Private constructor to prevent instantiation
    }

    // --- GUI Constants ---
    public static final int BRICK_SIZE = 20;
    public static final int BOARD_OFFSET_ROW = 2;
    public static final int BRICK_PANEL_Y_OFFSET = -42;
    public static final double GRID_HGAP = 1.0;
    public static final double GRID_VGAP = 1.0;
    public static final double GRID_STROKE_EXTENSION = 0.5;
    public static final double GRID_LINE_WIDTH = 1.0;
    public static final double BOARD_BACKGROUND_OPACITY = 0.75;
    public static final double RECTANGLE_ARC_SIZE = 9;
    public static final double FONT_SIZE_HIGH_SCORE_TITLE = 20;
    public static final double FONT_SIZE_HIGH_SCORE_VALUE = 24;
    public static final int GAME_TICK_MS = 400;

    // --- FXML Files ---
    public static final String PAUSE_MENU_FXML = "pauseMenu.fxml";
    public static final String GAME_OVER_MENU_FXML = "gameOverMenu.fxml";
    public static final String CONTROLS_MENU_FXML = "controlsMenu.fxml";

    // --- Preview Panel ---
    public static final int PREVIEW_GRID_SIZE = 4;
    public static final int PREVIEW_BRICK_SIZE = 15;
}
