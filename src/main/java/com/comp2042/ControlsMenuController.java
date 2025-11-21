package com.comp2042;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ControlsMenuController {

    private GuiController guiController;
    
    @FXML
    private Label controlsLabel;
    
    @FXML
    private Label escLabel;
    
    @FXML
    private Label spaceLabel;
    
    @FXML
    private Label downLabel;
    
    @FXML
    private Label leftLabel;
    
    @FXML
    private Label rightLabel;
    
    @FXML
    private Label upLabel;
    
    @FXML
    private Button backButton;

    public void setGuiController(GuiController guiController) {
        this.guiController = guiController;
    }
    
    @FXML
    private void initialize() {
        // Applying custom font to controls menu elements
        String fontFamily = FontLoader.loadFont();
        if (fontFamily != null) {
            if (controlsLabel != null) {
                controlsLabel.setFont(FontLoader.getFont(28));
            }
            if (escLabel != null) {
                escLabel.setFont(FontLoader.getFont(14));
            }
            if (spaceLabel != null) {
                spaceLabel.setFont(FontLoader.getFont(14));
            }
            if (downLabel != null) {
                downLabel.setFont(FontLoader.getFont(14));
            }
            if (leftLabel != null) {
                leftLabel.setFont(FontLoader.getFont(14));
            }
            if (rightLabel != null) {
                rightLabel.setFont(FontLoader.getFont(14));
            }
            if (upLabel != null) {
                upLabel.setFont(FontLoader.getFont(14));
            }
            if (backButton != null) {
                backButton.setFont(FontLoader.getFont(16));
            }
        }
    }

    @FXML
    private void onBackClicked(ActionEvent event) {
        if (guiController != null) {
            guiController.showPauseMenu();
        }
    }
}

