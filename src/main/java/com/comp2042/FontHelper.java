package com.comp2042;

import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class FontHelper {

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
}