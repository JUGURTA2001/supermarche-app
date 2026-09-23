package com.supermarche.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
        Scene scene = new Scene(root);
        scene.setFill(null); // fond transparent, pour que les coins arrondis s'affichent bien

        stage.initStyle(StageStyle.TRANSPARENT); // supprime complètement la barre de titre Windows
        stage.setScene(scene);
        stage.setTitle("Gestion Supermarché");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}