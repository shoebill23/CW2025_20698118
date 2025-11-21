package com.comp2042;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class StartController {

    //Constants
    private static final double FONT_SIZE_TITLE = 32;
    private static final double FONT_SIZE_PLAY_BUTTON = 20;
    private static final int GAME_SCENE_WIDTH = 600;
    private static final int GAME_SCENE_HEIGHT = 600;
    private static final String GAME_LAYOUT_FXML = "gameLayout.fxml";

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
        // Load the font and apply it programmatically
        String fontFamily = FontLoader.loadFont();
        System.out.println("StartController.initialize() - Font family: '" + fontFamily + "'");
        if (fontFamily != null) {
            // Apply font to title text
            if (titleText != null) {
                titleText.setFont(FontLoader.getFont(FONT_SIZE_TITLE));
                System.out.println("  - Applied font to titleText");
            } else {
                System.err.println("  - titleText is null!");
            }
            // Apply font to button
            if (playButton != null) {
                playButton.setFont(FontLoader.getFont(FONT_SIZE_PLAY_BUTTON));
                System.out.println("  - Applied font to playButton");
            } else {
                System.err.println("  - playButton is null!");
            }
            System.out.println("Fonts applied to start menu. Family: " + fontFamily);
        } else {
            System.err.println("ERROR: Could not load font. Using default font.");
        }
    }

    @FXML
    private void onPlayButtonClicked(ActionEvent event) throws IOException {
        System.out.println("Play button clicked!");
        try {
            if (primaryStage == null) {
                System.err.println("Error: primaryStage is null!");
                return;
            }
            
            URL location = getClass().getClassLoader().getResource(GAME_LAYOUT_FXML);
            if (location == null) {
                System.err.println("Error: Could not find gameLayout.fxml resource");
                return;
            }
            
            System.out.println("Loading gameLayout.fxml from: " + location);
            FXMLLoader loader = new FXMLLoader(location);
            Parent gameRoot = loader.load();
            System.out.println("FXML loaded successfully");
            
            GuiController guiController = loader.getController();
            if (guiController == null) {
                System.err.println("Error: GuiController is null after loading FXML");
                return;
            }
            System.out.println("GuiController obtained: " + guiController);
            
            Scene gameScene = new Scene(gameRoot, GAME_SCENE_WIDTH, GAME_SCENE_HEIGHT);
            primaryStage.setScene(gameScene);
            primaryStage.setResizable(false);
            primaryStage.show();
            System.out.println("Scene set and shown");
            
            new GameController(guiController);
            System.out.println("GameController created successfully");
        } catch (Exception e) {
            System.err.println("Error loading game: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to see the full stack trace
        }
    }
}

