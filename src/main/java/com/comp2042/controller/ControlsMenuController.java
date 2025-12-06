package com.comp2042.controller;


import com.comp2042.render.utility.FontHelper;
import com.comp2042.render.utility.FontLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller for the Controls overlay: shows key bindings and allows returning to pause menu.
 */
public class ControlsMenuController {

    //Font Sizes
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

    /**
     * Inject the main GUI controller.
     * @param guiController main controller
     */
    public void setGuiController(GuiController guiController) {
        this.guiController = guiController;
    }
    
    @FXML
    /**
     * Initialize fonts for labels and buttons.
     */
    private void initialize() {

        FontLoader.loadFont();

        FontHelper.applyFont(FONT_SIZE_CONTROLS_TITLE, controlsLabel);
        FontHelper.applyFont(FONT_SIZE_CONTROL_LABEL, escLabel, spaceLabel, downLabel, leftLabel, rightLabel, upLabel);
        FontHelper.applyFont(FONT_SIZE_MENU_BUTTON, backButton);
    }

    @FXML
    /**
     * Go back to the Pause menu.
     */
    private void onBackClicked() {
        if (guiController != null) {
            guiController.showPauseMenu();
        }
    }
}