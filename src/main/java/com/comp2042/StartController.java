package com.comp2042;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class StartController {

    @FXML
    private Button playButton;

    private Stage primaryStage;

    public void init(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    private void initialize() {
        URL fontUrl = getClass().getClassLoader().getResource("digital.ttf");
        if (fontUrl != null) {
            Font.loadFont(fontUrl.toExternalForm(), 38);
        }
    }

    @FXML
    private void onPlayButtonClicked(ActionEvent event) throws IOException {
        URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        Parent gameRoot = loader.load();
        GuiController guiController = loader.getController();
        Scene gameScene = new Scene(gameRoot, 300, 510);
        primaryStage.setScene(gameScene);
        primaryStage.show();
        new GameController(guiController);
    }
}

