package com.comp2042;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class PauseMenuController {

    //Constants
    private static final double FONT_SIZE_PAUSE_TITLE = 28;
    private static final double FONT_SIZE_MENU_BUTTON = 16;

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
        FontLoader.loadFont();

        FontHelper.applyFont(FONT_SIZE_PAUSE_TITLE, pausedLabel);
        FontHelper.applyFont(FONT_SIZE_MENU_BUTTON, restartButton, controlsButton, quitButton, resumeButton);
    }

    @FXML
    private void onRestartClicked() {
        if (guiController != null) {
            guiController.newGame();
        }
    }

    @FXML
    private void onControlsClicked() {
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
    private void onResumeClicked() {
        if (guiController != null) {
            guiController.resumeGame();
        }
    }
}