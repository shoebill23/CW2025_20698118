package com.comp2042;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class PauseMenuController {

    private GuiController guiController;
    
    @FXML
    private Label pausedLabel;
    
    @FXML
    private Button restartButton;
    
    @FXML
    private Button quitButton;

    public void setGuiController(GuiController guiController) {
        this.guiController = guiController;
    }
    
    @FXML
    private void initialize() {
        // Applying custom font to pause menu elements
        String fontFamily = FontLoader.loadFont();
        if (fontFamily != null) {
            if (pausedLabel != null) {
                pausedLabel.setFont(FontLoader.getFont(28));
            }
            if (restartButton != null) {
                restartButton.setFont(FontLoader.getFont(16));
            }
            if (quitButton != null) {
                quitButton.setFont(FontLoader.getFont(16));
            }
        }
    }

    @FXML
    private void onRestartClicked(ActionEvent event) {
        if (guiController != null) {
            guiController.newGame(event);
        }
    }

    @FXML
    private void onQuitClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        
        // Load the start layout (corrected filename)
        URL location = getClass().getClassLoader().getResource("startLayout.fxml");
        if (location == null) {
            System.err.println("Error: Could not find startLayout.fxml resource");
            return;
        }
        
        FXMLLoader loader = new FXMLLoader(location);
        Parent root = loader.load();
        StartController startController = loader.getController();
        startController.init(stage);
        
        // Match the scene size from Main.java
        Scene scene = new Scene(root, 300, 510);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
