package com.comp2042;

import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OverlayManager {
    private static final Logger logger = Logger.getLogger(OverlayManager.class.getName());

    // Generic loader that assumes the controller has a setGuiController method
    public <T> T loadMenu(Group container, String fxmlPath, GuiController mainController) {
        try {
            URL url = getClass().getClassLoader().getResource(fxmlPath);
            if (url == null) throw new IOException("Resource not found: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            T controller = loader.getController();

            // Reflection or Interface could be used here.
            // Assuming your controllers look like: void setGuiController(GuiController c)
            try {
                controller.getClass().getMethod("setGuiController", GuiController.class)
                        .invoke(controller, mainController);
            } catch (Exception _) {
                logger.warning("Controller for " + fxmlPath + " does not have setGuiController method.");
            }

            container.getChildren().clear();
            container.getChildren().add(root);
            logger.info("Loaded menu: " + fxmlPath);
            return controller;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load menu: " + fxmlPath, e);
            return null;
        }
    }

    public void show(Group group) {
        if (group != null) {
            // Ensure parent brings it to front (Fixes "hidden behind board" bugs)
            if (group.getParent() instanceof Pane parentPane) {
                parentPane.getChildren().remove(group);
                parentPane.getChildren().add(group);
            }
            group.setVisible(true);
            group.toFront();
        }
    }

    public void hide(Group group) {
        if (group != null) group.setVisible(false);
    }
}