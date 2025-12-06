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
 * Controller for the Game Over overlay: retry and quit actions.
 */
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

        FontHelper.applyFont(FONT_SIZE_GAME_OVER_TITLE, gameOverLabel);
        FontHelper.applyFont(FONT_SIZE_MENU_BUTTON, retryButton, quitButton);
    }

    @FXML
    /**
     * Retry the game by starting a new game.
     */
    void onRetry() {
        if (guiController != null) {
            guiController.newGame();
        }
    }

    @FXML
    /**
     * Quit to the start menu.
     * @param event action event
     * @throws IOException when navigation fails
     */
    void onQuit(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        NavigationHelper.navigateToStartMenu(stage);
    }
}