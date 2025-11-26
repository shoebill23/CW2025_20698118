package com.comp2042;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class NotificationPanel extends BorderPane {

    //Constants
    private static final double NOTIFICATION_MIN_HEIGHT = 200;
    private static final double NOTIFICATION_MIN_WIDTH = 220;
    private static final double NOTIFICATION_FONT_SIZE = 40;
    private static final double NOTIFICATION_GLOW_LEVEL = 0.6;
    private static final int FADE_DURATION_MS = 2000;
    private static final int TRANSLATE_DURATION_MS = 2500;
    private static final double TRANSLATE_Y_OFFSET = -40;
    private static final double FADE_FROM_VALUE = 1.0;
    private static final double FADE_TO_VALUE = 0.0;

    public NotificationPanel(String text) {
        setMinHeight(NOTIFICATION_MIN_HEIGHT);
        setMinWidth(NOTIFICATION_MIN_WIDTH);
        final Label score = new Label(text);
        score.getStyleClass().add("bonusStyle");
        String fontFamily = FontLoader.loadFont();
        if (fontFamily != null) {
            score.setFont(FontLoader.getFont(NOTIFICATION_FONT_SIZE));
        }
        
        final Effect glow = new Glow(NOTIFICATION_GLOW_LEVEL);
        score.setEffect(glow);
        score.setTextFill(Color.WHITE);
        setCenter(score);

    }

    public void showScore(ObservableList<Node> list) {
        FadeTransition ft = new FadeTransition(Duration.millis(FADE_DURATION_MS), this);
        TranslateTransition tt = new TranslateTransition(Duration.millis(TRANSLATE_DURATION_MS), this);
        tt.setToY(this.getLayoutY() + TRANSLATE_Y_OFFSET);
        ft.setFromValue(FADE_FROM_VALUE);
        ft.setToValue(FADE_TO_VALUE);
        ParallelTransition transition = new ParallelTransition(tt, ft);
        transition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                list.remove(NotificationPanel.this);
            }
        });
        transition.play();
    }
}