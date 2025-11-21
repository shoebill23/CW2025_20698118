package com.comp2042;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class NavigationHelper {
    
    private static final int START_SCENE_WIDTH = 300;
    private static final int START_SCENE_HEIGHT = 510;
    
    // Navigates to the start menu screen
    public static void navigateToStartMenu(Stage stage) throws IOException {
        URL location = NavigationHelper.class.getClassLoader().getResource("startLayout.fxml");
        if (location == null) {
            System.err.println("Error: Could not find startLayout.fxml resource");
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
    }
}

