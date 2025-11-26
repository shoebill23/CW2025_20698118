package com.comp2042.main;

import com.comp2042.render.utility.FontLoader;
import com.comp2042.controller.StartController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


import java.net.URL;
import java.util.logging.Logger;

public class Main extends Application {

    //Constants
    private static final String WINDOW_TITLE = "Tetris";
    private static final int START_SCENE_WIDTH = 600;
    private static final int START_SCENE_HEIGHT = 600;
    private static final String START_LAYOUT_FXML = "startLayout.fxml";
    private static final String START_MENU_MUSIC = "Pokemon_Center.mp3";
    private static final String CLASSIC_MUSIC = "Littleroot_Town.mp3";
    private static final String TIME_ATTACK_MUSIC = "Battle!_Trainer.mp3";
    private static final double BASE_VOLUME = 0.3;

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static MediaPlayer bgPlayer;

    @Override
    public void init() throws Exception {
        // Load the font early, before any FXML is loaded
        // This makes it available throughout the application
        String fontFamily = FontLoader.loadFont();
        if (fontFamily != null) {
            logger.info("Font ready for use. Family name: '" + fontFamily + "'");
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL location = Main.class.getClassLoader().getResource(START_LAYOUT_FXML);
        if (location == null) {
            logger.severe("Error: Could not find startLayout.fxml resource");
            throw new IllegalStateException("startLayout.fxml not found on classpath");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = fxmlLoader.load();
        StartController startController = fxmlLoader.getController();

        primaryStage.setTitle(WINDOW_TITLE);
        Scene scene = new Scene(root, START_SCENE_WIDTH, START_SCENE_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        startController.init(primaryStage);
        playStartMusic();
    }

    private static void playMusic(String resource) {
        try {
            if (bgPlayer != null) {
                bgPlayer.stop();
                bgPlayer.dispose();
                bgPlayer = null;
            }
            URL url = Main.class.getClassLoader().getResource(resource);
            if (url != null) {
                Media media = new Media(url.toExternalForm());
                bgPlayer = new MediaPlayer(media);
                bgPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                bgPlayer.setVolume(BASE_VOLUME);
                bgPlayer.play();
            } else {
                logger.warning("Music resource not found: " + resource);
            }
        } catch (Exception e) {
            logger.severe("Failed to play music: " + e.getMessage());
        }
    }

    public static void playStartMusic() {
        playMusic(START_MENU_MUSIC);
    }

    public static void playClassicMusic() {
        playMusic(CLASSIC_MUSIC);
    }

    public static void playTimeAttackMusic() {
        playMusic(TIME_ATTACK_MUSIC);
    }

    public static void setPaused(boolean paused) {
        if (bgPlayer != null) {
            bgPlayer.setVolume(paused ? BASE_VOLUME * 0.5 : BASE_VOLUME);
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (bgPlayer != null) {
            bgPlayer.stop();
            bgPlayer.dispose();
        }
    }
}