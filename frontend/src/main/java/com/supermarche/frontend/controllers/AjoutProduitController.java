package com.supermarche.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class AjoutProduitController {

    @FXML private TextField unite;
    @FXML private TextField nom;
    @FXML private TextField marque;
    @FXML private TextField famille;
    @FXML private TextField designation;
    @FXML private TextField rayonnage;
    @FXML private TextField codeproduit;
    @FXML private TextField codebarre;
    @FXML private TextField qteinitiale;
    @FXML private TextField prixachat;
    @FXML private TextField prixgros;
    @FXML private TextField prixdetail;
    @FXML private TextField tva;
    @FXML private DatePicker dateperemption;
    @FXML private TextField joursalerte;
    @FXML private ListView<String> listecodebarre;
    @FXML private ImageView imageproduit;

    @FXML
    private void enregistrer() {
        // Vérification minimale
        if (nom.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Le nom est obligatoire.");
            alert.showAndWait();
            return;
        }

        System.out.println("📦 Produit à enregistrer :");
        System.out.println("  Nom         : " + nom.getText());
        System.out.println("  Marque      : " + marque.getText());
        System.out.println("  Désignation : " + designation.getText());
        System.out.println("  Code-barre  : " + codebarre.getText());
        System.out.println("  Qté initiale: " + qteinitiale.getText());
        System.out.println("  Prix achat  : " + prixachat.getText());
        System.out.println("  Prix gros   : " + prixgros.getText());
        System.out.println("  Prix détail : " + prixdetail.getText());
        System.out.println("  TVA         : " + tva.getText());
        System.out.println("  Unité       : " + unite.getText());
        System.out.println("  Rayonnage   : " + rayonnage.getText());

        // TODO: appel POST /api/produits (multipart)
        // puis fermer la fenêtre
        fermerFenetre();
    }

    @FXML
    private void annuler() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) nom.getScene().getWindow();
        stage.close();
    }
}