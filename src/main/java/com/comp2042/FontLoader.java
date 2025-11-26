package com.comp2042;

import javafx.scene.text.Font;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FontLoader {

    //Font Resource
    private static final String FONT_FILE_NAME = "janinosjuosta.ttf";

    //Sizes
    private static final double FONT_LOAD_SIZE = 12;

    private static String fontFamilyName = null;
    private static boolean fontLoaded = false;

    private static final Logger logger = Logger.getLogger(FontLoader.class.getName());

    public static String loadFont() { //Loads the font from the resource folder
        if (fontFamilyName != null) {
            return fontFamilyName;
        }

        //Try loading font using InputStream
        try (InputStream fontStream = FontLoader.class.getClassLoader().getResourceAsStream(FONT_FILE_NAME)) {
            if (fontStream != null) {
                Font loadedFont = Font.loadFont(fontStream, FONT_LOAD_SIZE);
                if (loadedFont != null) {
                    fontFamilyName = loadedFont.getFamily();
                    fontLoaded = true;

                    //Log success using placeholders
                    logger.log(Level.INFO, "Font loaded successfully. Family name: ''{0}''", fontFamilyName);

                    //Diagnose font availability
                    FontHelper.diagnoseFontAvailability(fontFamilyName);

                    return fontFamilyName;
                } else {
                    logger.severe("Failed to load font - Font.loadFont returned null");
                }
            } else {
                //Log using placeholders
                logger.log(Level.SEVERE, "Font resource stream is null - ''{0}'' not found", FONT_FILE_NAME);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception loading font from stream", e);
        }

        //Try loading font using URL
        try {
            URL fontUrl = FontLoader.class.getClassLoader().getResource(FONT_FILE_NAME);
            if (fontUrl != null) {
                Font loadedFont = Font.loadFont(fontUrl.toExternalForm(), FONT_LOAD_SIZE);
                if (loadedFont != null) {
                    fontFamilyName = loadedFont.getFamily();
                    fontLoaded = true;
                    logger.log(Level.INFO, "Font loaded successfully via URL. Family name: ''{0}''", fontFamilyName);
                    return fontFamilyName;
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception loading font via URL", e);
        }

        return null;
    }

    public static String getFontFamily() {
        if (fontFamilyName == null) {
            loadFont();
        }
        if (fontFamilyName != null) {
            return fontFamilyName;
        }
        // Try to find the font by checking available families
        for (String family : Font.getFamilies()) {
            if (family.toLowerCase().contains("janinos") || family.toLowerCase().contains("juosta")) {
                fontFamilyName = family;
                logger.log(Level.INFO, "Found font by name matching: {0}", family);
                return family;
            }
        }
        return "System"; // Fallback to system font
    }

    public static Font getFont(double size) {
        String family = getFontFamily();
        logger.log(Level.INFO, "FontLoader.getFont({0}) - Using family: ''{1}''", new Object[]{size, family});

        if (!"System".equals(family)) {
            Font font = Font.font(family, size);
            logger.log(Level.INFO, "  - Created font: {0}", font);
            return font;
        }
        logger.severe("  - WARNING: Using default font (family was null or System)");
        return Font.font(size);
    }

    public static boolean isFontLoaded() {
        return fontLoaded && fontFamilyName != null;
    }
}