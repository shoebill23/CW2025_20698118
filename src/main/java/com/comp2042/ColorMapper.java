package com.comp2042;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class ColorMapper {
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