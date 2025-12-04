package com.comp2042.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Persists and retrieves high scores for Classic and Time Attack modes using simple text files.
 * Overwrites the stored score only when a new score exceeds the current value.
 */
public class HighScoreManager {
    
    private static final String CLASSIC_FILE = "highscore_classic.txt";
    private static final String TIME_ATTACK_FILE = "highscore_time_attack.txt";
    private static final int DEFAULT_HIGH_SCORE = 0;

    private static final Logger logger = Logger.getLogger(HighScoreManager.class.getName());

    /**
     * Save a Classic-mode high score if it beats the stored value.
     * @param score score to persist
     */
    public static void saveScore(int score) {
        saveScore(score, false);
    }

    /**
     * Save a high score for the selected mode if it beats the stored value.
     * @param score score to persist
     * @param timeAttack true for Time Attack file, false for Classic
     */
    public static void saveScore(int score, boolean timeAttack) {
        try {
            Path path = getFile(timeAttack);
            int current = readHighScore(path);
            if (score > current) {
                writeHighScore(path, score);
            }
        } catch (Exception e) {
            logger.severe("Error saving high score: " + e.getMessage());
        }
    }

    /**
     * Read the Classic-mode high score, or default when missing.
     * @return high score value
     */
    public static int getHighScore() {
        return getHighScore(false);
    }

    /**
     * Read the high score for the selected mode, or default when missing.
     * @param timeAttack true for Time Attack file, false for Classic
     * @return high score value
     */
    public static int getHighScore(boolean timeAttack) {
        try {
            Path path = getFile(timeAttack);
            return readHighScore(path);
        } catch (Exception e) {
            logger.severe("Error reading high score: " + e.getMessage());
            return DEFAULT_HIGH_SCORE;
        }
    }

    private static Path getFile(boolean timeAttack) {
        return Paths.get(timeAttack ? TIME_ATTACK_FILE : CLASSIC_FILE);
    }

    private static int readHighScore(Path path) {
        try {
            if (!Files.exists(path)) {
                return DEFAULT_HIGH_SCORE;
            }
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String line = reader.readLine();
                if (line == null) return DEFAULT_HIGH_SCORE;
                try {
                    return Integer.parseInt(line.trim());
                } catch (NumberFormatException _) {
                    return DEFAULT_HIGH_SCORE;
                }
            }
        } catch (IOException e) {
            logger.severe("IO error reading high score: " + e.getMessage());
            return DEFAULT_HIGH_SCORE;
        }
    }

    private static void writeHighScore(Path path, int score) {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            logger.severe("IO error writing high score: " + e.getMessage());
        }
    }
}