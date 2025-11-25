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
    private Button classicButton;

    @FXML
    private Button timeAttackButton;

    private Stage primaryStage;

    public void init(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    private void initialize() {
        FontLoader.loadFont();

        FontHelper.applyFont(FONT_SIZE_TITLE, titleText);
        FontHelper.applyFont(FONT_SIZE_PLAY_BUTTON, classicButton, timeAttackButton);
    }

    @FXML

    private void startGame(boolean timeAttack) throws IOException {
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

            FXMLLoader loader = new FXMLLoader(location);
            Parent gameRoot = loader.load();

            GuiController guiController = loader.getController();
            if (guiController == null) {
                logger.severe("Error: GuiController is null after loading FXML");
                return;
            }

            guiController.setTimeAttackMode(timeAttack);

            Scene gameScene = new Scene(gameRoot, GAME_SCENE_WIDTH, GAME_SCENE_HEIGHT);
            primaryStage.setScene(gameScene);
            primaryStage.setResizable(false);
            primaryStage.show();

            Main.playGameMusic();

            new GameController(guiController);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading game", e);
        }
    }

    @FXML
    private void onClassicClicked() throws IOException {
        startGame(false);
    }

    @FXML
    private void onTimeAttackClicked() throws IOException {
        startGame(true);
    }
}