package com.comp2042;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ControlsMenuController {

    // --- Font Sizes ---
    private static final double FONT_SIZE_CONTROLS_TITLE = 28;
    private static final double FONT_SIZE_CONTROL_LABEL = 14;
    private static final double FONT_SIZE_MENU_BUTTON = 16;

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
                controlsLabel.setFont(FontLoader.getFont(FONT_SIZE_CONTROLS_TITLE));
            }
            if (escLabel != null) {
                escLabel.setFont(FontLoader.getFont(FONT_SIZE_CONTROL_LABEL));
            }
            if (spaceLabel != null) {
                spaceLabel.setFont(FontLoader.getFont(FONT_SIZE_CONTROL_LABEL));
            }
            if (downLabel != null) {
                downLabel.setFont(FontLoader.getFont(FONT_SIZE_CONTROL_LABEL));
            }
            if (leftLabel != null) {
                leftLabel.setFont(FontLoader.getFont(FONT_SIZE_CONTROL_LABEL));
            }
            if (rightLabel != null) {
                rightLabel.setFont(FontLoader.getFont(FONT_SIZE_CONTROL_LABEL));
            }
            if (upLabel != null) {
                upLabel.setFont(FontLoader.getFont(FONT_SIZE_CONTROL_LABEL));
            }
            if (backButton != null) {
                backButton.setFont(FontLoader.getFont(FONT_SIZE_MENU_BUTTON));
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

