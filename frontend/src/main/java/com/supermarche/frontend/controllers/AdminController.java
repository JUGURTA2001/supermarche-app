package com.supermarche.frontend.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class AdminController {

    @FXML private StackPane contentArea;
    @FXML private Label lblUtilisateur;
    @FXML private Label lblRole;

    @FXML private Button btnAccueil;
    @FXML private Button btnProduits;
    @FXML private Button btnVentes;
    @FXML private Button btnStock;
    @FXML private Button btnBon;              // ← AJOUT
    @FXML private Button btnStatistiques;
    @FXML private Button btnNotifications;
    @FXML private Button btnFournisseurs;
    @FXML private Button btnParametres;

    @FXML
    private void initialize() {
        lblUtilisateur.setText("Admin");
        lblRole.setText("Rôle : admin");
        pageAccueil();
    }

    private void setActiveButton(Button btnActif) {
        Button[] tous = { btnAccueil, btnProduits, btnVentes, btnStock, btnBon,
                btnStatistiques, btnNotifications, btnFournisseurs, btnParametres };
        for (Button b : tous) {
            if (b != null) b.getStyleClass().remove("nav-btn-active");
        }
        if (btnActif != null && !btnActif.getStyleClass().contains("nav-btn-active")) {
            btnActif.getStyleClass().add("nav-btn-active");
        }
    }

    private void chargerVue(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (Exception e) {
            System.err.println("Impossible de charger la vue : " + fxmlPath);
            e.printStackTrace();
        }
    }

    @FXML private void pageAccueil()        { setActiveButton(btnAccueil);        chargerVue("/views/accueil.fxml"); }
    @FXML private void pageProduits()       { setActiveButton(btnProduits);       chargerVue("/views/produits.fxml"); }
    @FXML private void pageVentes()         { setActiveButton(btnVentes);         chargerVue("/views/vente.fxml"); }
    @FXML private void pageStock()          { setActiveButton(btnStock);          chargerVue("/views/stock.fxml"); }
    @FXML private void pageBon()            { setActiveButton(btnBon);            chargerVue("/views/bon.fxml"); }
    @FXML private void pageStatistiques()   { setActiveButton(btnStatistiques);   chargerVue("/views/statistique.fxml"); }
    @FXML private void pageNotifications()  { setActiveButton(btnNotifications);  chargerVue("/views/notification.fxml"); }
    @FXML private void pageFournisseurs()   { setActiveButton(btnFournisseurs);   chargerVue("/views/fournisseurs.fxml"); }
    @FXML private void pageParametres()     { setActiveButton(btnParametres);     chargerVue("/views/parametre.fxml"); }

    @FXML
    private void menuDeconnexion() {
        System.out.println("Déconnexion");
    }
}