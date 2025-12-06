package com.comp2042.general_utility;

/**
 * Centralized UI constants for layout, styling, and resource paths.
 * Used by renderers and controllers to keep values consistent.
 */
public final class UIConstants {

    private UIConstants() {
        // Private constructor to prevent instantiation
    }

    /** Cell size in pixels for board bricks. */
    public static final int BRICK_SIZE = 20;
    /** Number of hidden top rows in the board matrix. */
    public static final int BOARD_OFFSET_ROW = 2;
    /** Visual Y offset applied to the active brick panel. */
    public static final int BRICK_PANEL_Y_OFFSET = -42;
    /** Horizontal/vertical gaps between grid cells. */
    public static final double GRID_HGAP = 1.0;
    public static final double GRID_VGAP = 1.0;
    /** Extra stroke length beyond bounds for grid lines. */
    public static final double GRID_STROKE_EXTENSION = 0.5;
    /** Grid line stroke width. */
    public static final double GRID_LINE_WIDTH = 1.0;
    /** Opacity for board background overlay. */
    public static final double BOARD_BACKGROUND_OPACITY = 0.75;
    /** Rounded corner radius for cells. */
    public static final double RECTANGLE_ARC_SIZE = 9;
    /** Font sizes for high score labels. */
    public static final double FONT_SIZE_HIGH_SCORE_TITLE = 20;
    public static final double FONT_SIZE_HIGH_SCORE_VALUE = 24;
    /** Default tick duration for game loop in milliseconds. */
    public static final int GAME_TICK_MS = 400;

    /** FXML resource paths for overlay menus. */
    public static final String PAUSE_MENU_FXML = "pauseMenu.fxml";
    public static final String GAME_OVER_MENU_FXML = "gameOverMenu.fxml";
    public static final String CONTROLS_MENU_FXML = "controlsMenu.fxml";

    /** Preview grid dimensions and cell size. */
    public static final int PREVIEW_GRID_SIZE = 4;
    public static final int PREVIEW_BRICK_SIZE = 15;
}