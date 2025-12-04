package com.comp2042.model;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * Manages a 60-second Time Attack countdown and invokes a callback when time is up.
 * Provides start/stop/pause/resume and updates a label with remaining seconds.
 */
public class TimeAttackManager {
    private final Label timeLabel;
    private final Runnable onTimeUp;
    private Timeline timeline;
    private int remainingSeconds;
    private boolean isEnabled;

    /**
     * Create a manager bound to a UI label and an on-time-up callback.
     * @param timeLabel label to show remaining seconds
     * @param onTimeUp callback when timer reaches zero
     */
    public TimeAttackManager(Label timeLabel, Runnable onTimeUp) {
        this.timeLabel = timeLabel;
        this.onTimeUp = onTimeUp;
    }

    /** Begin counting down if enabled; updates the label and triggers callback at 0. */
    public void start() {
        if (!isEnabled) return;
        remainingSeconds = 60;
        updateLabel();

        if (timeline != null) timeline.stop();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            remainingSeconds--;
            updateLabel();
            if (remainingSeconds <= 0) {
                stop();
                onTimeUp.run();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /** Stop the countdown timer. */
    public void stop() {
        if (timeline != null) timeline.stop();
    }

    /** Pause the countdown timer. */
    public void pause() {
        if (timeline != null) timeline.pause();
    }

    /** Resume the countdown if enabled. */
    public void resume() {
        if (timeline != null && isEnabled) timeline.play();
    }

    /**
     * Enable/disable the Time Attack mode.
     * @param enabled true to enable, false to disable
     */
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    /**
     * Whether Time Attack mode is currently enabled.
     * @return true if enabled
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    private void updateLabel() {
        if (timeLabel != null) {
            timeLabel.setText(String.valueOf(Math.max(remainingSeconds, 0)));
        }
    }
}