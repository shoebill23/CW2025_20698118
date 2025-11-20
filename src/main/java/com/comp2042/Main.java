package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void init() throws Exception {
        // Load the font early, before any FXML is loaded
        // This makes it available throughout the application
        String fontFamily = FontLoader.loadFont();
        if (fontFamily != null) {
            System.out.println("Font ready for use. Family name: '" + fontFamily + "'");
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL location = getClass().getClassLoader().getResource("startLayout.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent root = fxmlLoader.load();
        StartController startController = fxmlLoader.getController();

        primaryStage.setTitle("Tetris");
        Scene scene = new Scene(root, 300, 510);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        startController.init(primaryStage);
    }
}
