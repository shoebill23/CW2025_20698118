package com.comp2042;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HighScoreManager {
    
    //Constants
    private static final String HIGH_SCORE_FILE = "highscores.csv";
    private static final String CSV_HEADER = "Score\n";
    private static final int DEFAULT_HIGH_SCORE = 0;
    

    public static void saveScore(int score) {
        try {
            Path filePath = Paths.get(HIGH_SCORE_FILE);
            boolean fileExists = Files.exists(filePath);
            
            // Append score to CSV file
            try (FileWriter writer = new FileWriter(HIGH_SCORE_FILE, true)) {
                if (!fileExists) {
                    // Write header if file doesn't exist
                    writer.append(CSV_HEADER);
                }
                writer.append(String.valueOf(score)).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error saving score to CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    public static int getHighScore() {
        try {
            Path filePath = Paths.get(HIGH_SCORE_FILE);
            if (!Files.exists(filePath)) {
                return DEFAULT_HIGH_SCORE; // No scores yet
            }
            
            List<Integer> scores = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                String line;
                boolean isFirstLine = true;
                while ((line = reader.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue; // Skip header
                    }
                    try {
                        int score = Integer.parseInt(line.trim());
                        scores.add(score);
                    } catch (NumberFormatException e) {
                        // Skip invalid lines
                        System.err.println("Invalid score line: " + line);
                    }
                }
            }
            
            // Return the highest score, or default if no scores
            return scores.stream().mapToInt(Integer::intValue).max().orElse(DEFAULT_HIGH_SCORE);
        } catch (IOException e) {
            System.err.println("Error reading high score from CSV: " + e.getMessage());
            e.printStackTrace();
            return DEFAULT_HIGH_SCORE;
        }
    }
}

