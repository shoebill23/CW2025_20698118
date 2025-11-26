package com.comp2042;

import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.logging.Logger;

public class FontHelper { //Helper class to apply fonts to nodes and diagnose font availability

    private static final Logger logger = Logger.getLogger(FontHelper.class.getName());

    private FontHelper(){ //prevents instantiation of FontHelper in other classes
        throw new IllegalStateException("Utility class");
    }

    public static void applyFont(double size, Node... nodes) {
        Font font = FontLoader.getFont(size);
        if (font == null) return; // If font is not loaded, program exits gracefully

        for (Node node : nodes) {
            if (node instanceof Labeled labeled) { //Checking the type of the nodes (Labels/Text)
                labeled.setFont(font);

            } else if (node instanceof Text text) {
                text.setFont(font);
            }
        }
    }

    public static void diagnoseFontAvailability(String fontFamilyName) { //Checks if the font is available in the system
        if (fontFamilyName == null) {
            logger.warning("Cannot diagnose font availability, family name is null.");
            return;
        }

        boolean isAvailable = javafx.scene.text.Font.getFamilies().contains(fontFamilyName);
        logger.info("Font '" + fontFamilyName + "' is available in system: " + isAvailable);

        if (!isAvailable) {
            logger.severe("Warning: Font loaded but not found in available families!");
            logger.severe("Trying to find similar font names...");
            for (String family : Font.getFamilies()) {
                if (family.toLowerCase().contains("janinos") || family.toLowerCase().contains("juosta")) {
                    logger.info("Found similar font: " + family);
                }
            }
        }
    }
}