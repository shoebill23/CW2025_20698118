package com.comp2042;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class PauseMenuController {

    private GuiController guiController;
    
    @FXML
    private Label pausedLabel;
    
    @FXML
    private Button restartButton;
    
    @FXML
    private Button controlsButton;
    
    @FXML
    private Button quitButton;

    @FXML
    private Button resumeButton;

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
            if (controlsButton != null) {
                controlsButton.setFont(FontLoader.getFont(16));
            }
            if (quitButton != null) {
                quitButton.setFont(FontLoader.getFont(16));
            }
            if (resumeButton != null) {
                resumeButton.setFont(FontLoader.getFont(16));
            }
        }
    }

    @FXML
    private void onRestartClicked(ActionEvent event) {
        if (guiController != null) {
            guiController.newGame();
        }
    }

    @FXML
    private void onControlsClicked(ActionEvent event) {
        if (guiController != null) {
            guiController.showControlsMenu();
        }
    }

    @FXML
    private void onQuitClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        NavigationHelper.navigateToStartMenu(stage);
    }

    @FXML
    private void onResumeClicked(ActionEvent event) {
        if (guiController != null) {
            guiController.resumeGame();
        }
    }
}
