package com.comp2042;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class GameOverMenuController {

    //Constants
    private static final double FONT_SIZE_GAME_OVER_TITLE = 28;
    private static final double FONT_SIZE_MENU_BUTTON = 16;

    private GuiController guiController;
    
    @FXML
    private Label gameOverLabel;
    
    @FXML
    private Button retryButton;
    
    @FXML
    private Button quitButton;

    public void setGuiController(GuiController guiController) {
        this.guiController = guiController;
    }
    
    @FXML
    private void initialize() {
        FontLoader.loadFont();

        FontHelper.applyFont(FONT_SIZE_GAME_OVER_TITLE, gameOverLabel);
        FontHelper.applyFont(FONT_SIZE_MENU_BUTTON, retryButton, quitButton);
    }

    @FXML
    void onRetry() {
        if (guiController != null) {
            guiController.newGame();
        }
    }

    @FXML
    void onQuit(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        NavigationHelper.navigateToStartMenu(stage);
    }
}