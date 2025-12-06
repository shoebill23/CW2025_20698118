package com.comp2042.controller;

import com.comp2042.render.utility.FontHelper;
import com.comp2042.render.utility.FontLoader;
import com.comp2042.general_utility.NavigationHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the Pause overlay: restart, controls, quit, and resume.
 */
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

        FontHelper.applyFont(FONT_SIZE_PAUSE_TITLE, pausedLabel);
        FontHelper.applyFont(FONT_SIZE_MENU_BUTTON, restartButton, controlsButton, quitButton, resumeButton);
    }

    @FXML
    /**
     * Restart the game.
     */
    private void onRestartClicked() {
        if (guiController != null) {
            guiController.newGame();
        }
    }

    @FXML
    /**
     * Show the controls overlay.
     */
    private void onControlsClicked() {
        if (guiController != null) {
            guiController.showControlsMenu();
        }
    }

    @FXML
    /**
     * Quit to the start menu.
     * @param event action event
     * @throws IOException when navigation fails
     */
    private void onQuitClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        NavigationHelper.navigateToStartMenu(stage);
    }

    @FXML
    /**
     * Resume the game.
     */
    private void onResumeClicked() {
        if (guiController != null) {
            guiController.resumeGame();
        }
    }
}