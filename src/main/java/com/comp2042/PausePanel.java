package com.comp2042;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;


public class PausePanel extends BorderPane {

    public PausePanel() {
        final Label pauseLabel = new Label("PAUSED");
        pauseLabel.getStyleClass().add("gameOverStyle");
        setCenter(pauseLabel);
    }

}

