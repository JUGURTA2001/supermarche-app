package com.supermarche.frontend.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;

public class SceneManager {

    // Méthode pour changer de scène
    public static void switchScene(Node currentNode, String fxmlPath, String title) {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            // Récupérer le Stage actuel
            Stage stage = (Stage) currentNode.getScene().getWindow();

            // Créer une nouvelle scène et l'afficher
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue : " + fxmlPath);
            e.printStackTrace();
        }
    }
}