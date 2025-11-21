package com.comp2042;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class GameOverMenuController {

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
        
        String fontFamily = FontLoader.loadFont();
        if (fontFamily != null) {
            if (gameOverLabel != null) {
                gameOverLabel.setFont(FontLoader.getFont(28));
            }
            if (retryButton != null) {
                retryButton.setFont(FontLoader.getFont(16));
            }
            if (quitButton != null) {
                quitButton.setFont(FontLoader.getFont(16));
            }
        }
    }

    @FXML
    void onRetry(ActionEvent event) {
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