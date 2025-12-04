package com.comp2042.model;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Manages the game loop: schedules drop ticks based on level and provides pause/resume/reset.
 */
public class GameLoop { //Manage the game loop and tick events
    private Timeline timeline;
    private final Runnable tickAction;
    private int level = 1;
    private int totalLinesCleared = 0;

    /** Create a loop with a runnable invoked each tick. */
    public GameLoop(Runnable tickAction) { //Initializes the gam loop
        this.tickAction = tickAction;
    }

    /** Start or restart the timeline with the current drop speed. */
    public void start() { //Starts the game loop    
        if (timeline != null) timeline.stop();
        timeline = new Timeline(new KeyFrame(getCurrentDropDuration(), e -> tickAction.run()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /** Stop the timeline if running. */
    public void stop() {
        if (timeline != null) timeline.stop();
    }

    /** Pause the timeline if running. */
    public void pause() {
        if (timeline != null) timeline.pause();
    }

    /** Resume the timeline if paused. */
    public void resume() {
        if (timeline != null) timeline.play();
    }

    /** Update level based on cleared lines and restart with new speed on level change. */
    public void onLinesCleared(int lines) {
        totalLinesCleared += lines;
        int newLevel = (totalLinesCleared / 10) + 1;
        if (newLevel != level) { //Check if the level has changed
            level = newLevel;
            start(); // Restart with new speed
        }
    }

    /** Reset level and counters; stop timeline. */
    public void reset() {
        level = 1;
        totalLinesCleared = 0;
        stop();
    }

    /**
     * Current level number.
     * @return level (1-based)
     */
    public int getLevel() {
        return level;
    }

    /**
     * Calculate current drop interval using the standard speed curve.
     * @return tick duration based on current level
     */
    private Duration getCurrentDropDuration() { //Calculates the current drop duration based on the level
        double seconds = 0.75 * Math.pow(0.8 - ((level - 1) * 0.010), (level - 1));
        return Duration.seconds(seconds);
    }
}