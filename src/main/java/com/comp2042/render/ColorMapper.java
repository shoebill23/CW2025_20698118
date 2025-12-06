package com.comp2042.render;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Maps brick index values to JavaFX colors.
 */
public class ColorMapper {
    /**
     * Convert a brick index to a color.
     * @param index brick value (0 = empty)
     * @return paint color for the cell
     */
    public static Paint getColor(int index) {
        return switch (index) {
            case 0 -> Color.TRANSPARENT;
            case 1 -> Color.AQUA;
            case 2 -> Color.BLUEVIOLET;
            case 3 -> Color.DARKGREEN;
            case 4 -> Color.YELLOW;
            case 5 -> Color.RED;
            case 6 -> Color.BEIGE;
            case 7 -> Color.BURLYWOOD;
            default -> Color.WHITE;
        };
    }
}