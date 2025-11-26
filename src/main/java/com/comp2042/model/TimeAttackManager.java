package com.comp2042.model;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class TimeAttackManager {
    private final Label timeLabel;
    private final Runnable onTimeUp;
    private Timeline timeline;
    private int remainingSeconds;
    private boolean isEnabled;

    public TimeAttackManager(Label timeLabel, Runnable onTimeUp) {
        this.timeLabel = timeLabel;
        this.onTimeUp = onTimeUp;
    }

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

    public void stop() {
        if (timeline != null) timeline.stop();
    }

    public void pause() {
        if (timeline != null) timeline.pause();
    }

    public void resume() {
        if (timeline != null && isEnabled) timeline.play();
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    private void updateLabel() {
        if (timeLabel != null) {
            timeLabel.setText(String.valueOf(Math.max(remainingSeconds, 0)));
        }
    }
}