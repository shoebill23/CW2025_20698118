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
            guiController.newGame(event);
        }
    }

    @FXML
    void onQuit(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        
        
        URL location = getClass().getClassLoader().getResource("startLayout.fxml");
        if (location == null) {
            System.err.println("Error: Could not find startLayout.fxml resource");
            return;
        }
        
        FXMLLoader loader = new FXMLLoader(location);
        Parent root = loader.load();
        StartController startController = loader.getController();
        startController.init(stage);
        
        
        Scene scene = new Scene(root, 300, 510);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}