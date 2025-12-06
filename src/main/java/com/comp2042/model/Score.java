package com.comp2042.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Observable score holder with add/reset and a bindable IntegerProperty.
 */
public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);

    /** Property to bind UI labels for live score updates. */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /** Increase score by the specified amount. */
    public void add(int i){
        score.setValue(score.getValue() + i);
    }

    /** Reset score back to zero. */
    public void reset() {
        score.setValue(0);
    }
}