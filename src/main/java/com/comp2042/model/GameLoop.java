package com.comp2042.model;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class GameLoop { //Manage the game loop and tick events
    private Timeline timeline;
    private final Runnable tickAction;
    private int level = 1;
    private int totalLinesCleared = 0;

    public GameLoop(Runnable tickAction) { //Initializes the gam loop
        this.tickAction = tickAction;
    }

    public void start() { //Starts the game loop    
        if (timeline != null) timeline.stop();
        timeline = new Timeline(new KeyFrame(getCurrentDropDuration(), e -> tickAction.run()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void stop() {
        if (timeline != null) timeline.stop();
    }

    public void pause() {
        if (timeline != null) timeline.pause();
    }

    public void resume() {
        if (timeline != null) timeline.play();
    }

    public void onLinesCleared(int lines) {
        totalLinesCleared += lines;
        int newLevel = (totalLinesCleared / 10) + 1;
        if (newLevel != level) { //Check if the level has changed
            level = newLevel;
            start(); // Restart with new speed
        }
    }

    public void reset() {
        level = 1;
        totalLinesCleared = 0;
        stop();
    }

    public int getLevel() {
        return level;
    }

    private Duration getCurrentDropDuration() { //Calculates the current drop duration based on the level
        double seconds = 0.75 * Math.pow(0.8 - ((level - 1) * 0.010), (level - 1));
        return Duration.seconds(seconds);
    }
}