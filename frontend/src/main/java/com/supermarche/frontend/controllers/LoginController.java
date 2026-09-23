package com.supermarche.frontend.controllers;

import com.supermarche.frontend.models.LoginResponse;
import com.supermarche.frontend.services.AuthService;
import com.supermarche.frontend.utils.SceneManager; // Assurez-vous que le package correspond
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    private Button btnlogin;

    @FXML
    private Button btnclose;

    @FXML
    private Button btnoblien;

    @FXML
    private Label ereur;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private void initialize() {
        ereur.setText("");

        // Déplacer la fenêtre
        rootPane.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        rootPane.setOnMouseDragged(event -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        // Connexion avec la touche Entrée
        password.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });
    }

    @FXML
    private void handleLogin() {
        String userEmail = email.getText().trim();
        String pass = password.getText().trim();

        if (userEmail.isEmpty() || pass.isEmpty()) {
            ereur.setText("Veuillez remplir tous les champs");
            return;
        }

        ereur.setText("");
        System.out.println("Tentative de connexion : " + userEmail);

        // Appel à AuthService
        LoginResponse response = AuthService.login(userEmail, pass);

        if (response == null) {
            ereur.setText("Identifiant ou mot de passe incorrect");
            return;
        }

        // --- REDIRECTION SELON LE RÔLE ---
        String role = response.getRole();
        System.out.println("Rôle récupéré : " + role);

        if ("admin".equals(role)) {
            // Redirection vers l'interface Admin
            SceneManager.switchScene(rootPane, "/views/admin.fxml", "Super Marché - Admin");
        } else if ("employe".equals(role)) {
            // Redirection vers l'interface Employé
            SceneManager.switchScene(rootPane, "/views/employe.fxml", "Super Marché - Employé");
        } else {
            ereur.setText("Rôle inconnu : " + role);
        }
    }

    @FXML
    private void handleMotDePasseOublie() {
        System.out.println("Mot de passe oublié cliqué");
    }

    @FXML
    private void closeApp() {
        System.exit(0);
    }
}