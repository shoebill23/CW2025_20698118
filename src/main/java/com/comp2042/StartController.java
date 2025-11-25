package com.comp2042;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import java.util.logging.Level;
import java.util.logging.Logger;

public class StartController {

    // Constants
    private static final double FONT_SIZE_TITLE = 32;
    private static final double FONT_SIZE_PLAY_BUTTON = 20;
    private static final int GAME_SCENE_WIDTH = 600;
    private static final int GAME_SCENE_HEIGHT = 600;
    private static final String GAME_LAYOUT_FXML = "gameLayout.fxml";

    //Defining Logger
    private static final Logger logger = Logger.getLogger(StartController.class.getName());

    @FXML
    private Text titleText;

    @FXML
    private Button playButton;

    private Stage primaryStage;

    public void init(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    private void initialize() {
        FontLoader.loadFont();

        FontHelper.applyFont(FONT_SIZE_TITLE, titleText);
        FontHelper.applyFont(FONT_SIZE_PLAY_BUTTON, playButton);
    }

    @FXML

    private void onPlayButtonClicked() throws IOException {
        logger.info("Play button clicked!");

        try {
            if (primaryStage == null) {
                logger.severe("Error: primaryStage is null!");
                return;
            }

            URL location = getClass().getClassLoader().getResource(GAME_LAYOUT_FXML);
            if (location == null) {
                logger.severe("Error: Could not find gameLayout.fxml resource");
                return;
            }

            logger.log(Level.INFO, "Loading gameLayout.fxml from: {0}", location);

            FXMLLoader loader = new FXMLLoader(location);
            Parent gameRoot = loader.load();
            logger.info("FXML loaded successfully");

            GuiController guiController = loader.getController();
            if (guiController == null) {
                logger.severe("Error: GuiController is null after loading FXML");
                return;
            }

            logger.log(Level.INFO, "GuiController obtained: {0}", guiController);

            Scene gameScene = new Scene(gameRoot, GAME_SCENE_WIDTH, GAME_SCENE_HEIGHT);
            primaryStage.setScene(gameScene);
            primaryStage.setResizable(false);
            primaryStage.show();
            logger.info("Scene set and shown");

            new GameController(guiController);
            logger.info("GameController created successfully");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading game", e);
        }
    }
}