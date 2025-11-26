package com.comp2042.general_utility;

import com.comp2042.controller.StartController;
import com.comp2042.main.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

public class NavigationHelper {

    //Constants
    private static final int START_SCENE_WIDTH = 600;
    private static final int START_SCENE_HEIGHT = 600;
    private static final String START_LAYOUT_FXML = "startLayout.fxml";

    //Defining Logger
    private static final Logger logger = Logger.getLogger(NavigationHelper.class.getName());

    private NavigationHelper(){ //Prevents instantiation of NavigationHelper in other classes
        throw new IllegalStateException("Utility class");
    }

    // Navigates to the start menu screen
    public static void navigateToStartMenu(Stage stage) throws IOException {
        URL location = NavigationHelper.class.getClassLoader().getResource(START_LAYOUT_FXML);
        if (location == null) {
            logger.severe("Error: Could not find startLayout.fxml resource");
            return;
        }
        
        FXMLLoader loader = new FXMLLoader(location);
        Parent root = loader.load();
        StartController startController = loader.getController();
        startController.init(stage);
        
        Scene scene = new Scene(root, START_SCENE_WIDTH, START_SCENE_HEIGHT);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        Main.playStartMusic();
    }
}
