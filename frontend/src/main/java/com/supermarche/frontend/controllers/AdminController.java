package com.supermarche.frontend.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class AdminController {

    @FXML private StackPane contentArea;
    @FXML private Label lblPageTitre;
    @FXML private Label lblUtilisateur;
    @FXML private Label lblRole;

    @FXML private Button btnAccueil;
    @FXML private Button btnProduits;
    @FXML private Button btnVentes;
    @FXML private Button btnStock;
    @FXML private Button btnStatistiques;
    @FXML private Button btnNotifications;
    @FXML private Button btnFournisseurs;
    @FXML private Button btnParametres;

    @FXML
    private void initialize() {
        // TODO: décommenter quand SessionManager sera prêt
        // lblUtilisateur.setText(SessionManager.getNom());
        // lblRole.setText("Rôle : " + SessionManager.getRole());
        lblUtilisateur.setText("Admin");
        lblRole.setText("Rôle : admin");

        // Charger la page d'accueil par défaut
        pageAccueil();
    }

    // ============================================================
    // MÉTHODE UTILITAIRE : change le bouton actif visuellement
    // ============================================================
    private void setActiveButton(Button btnActif) {
        Button[] tousLesBoutons = {
                btnAccueil, btnProduits, btnVentes, btnStock,
                btnStatistiques, btnNotifications, btnFournisseurs, btnParametres
        };

        for (Button b : tousLesBoutons) {
            if (b != null) {
                b.getStyleClass().remove("nav-btn-active");
            }
        }

        if (btnActif != null && !btnActif.getStyleClass().contains("nav-btn-active")) {
            btnActif.getStyleClass().add("nav-btn-active");
        }
    }

    // ============================================================
    // MÉTHODE UTILITAIRE : charge une vue dans contentArea
    // ============================================================
    private void chargerVue(String titre, String fxmlPath) {
        lblPageTitre.setText(titre);
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (Exception e) {
            System.err.println("Impossible de charger la vue : " + fxmlPath);
            e.printStackTrace();
            afficherMessage(titre, "Erreur de chargement de la page.");
        }
    }

    // ============================================================
    // MÉTHODE UTILITAIRE : affiche un simple message dans contentArea
    // ============================================================
    private void afficherMessage(String titre, String message) {
        lblPageTitre.setText(titre);
        contentArea.getChildren().clear();
        Label label = new Label(message);
        label.getStyleClass().add("placeholder-text");
        contentArea.getChildren().add(label);
    }

    // ============================================================
    // ACTIONS DE NAVIGATION (appelées par le FXML)
    // ============================================================

    @FXML
    private void pageAccueil() {
        setActiveButton(btnAccueil);
        chargerVue("Tableau de bord", "/views/accueil.fxml");
    }

    @FXML
    private void pageProduits() {
        setActiveButton(btnProduits);
        chargerVue("Produits", "/views/produits.fxml");
    }

    @FXML
    private void pageVentes() {
        setActiveButton(btnVentes);
        chargerVue("Ventes", "/views/vente.fxml");
    }

    @FXML
    private void pageStock() {
        setActiveButton(btnStock);
        chargerVue("Stock", "/views/stock.fxml");
    }

    @FXML
    private void pageStatistiques() {
        setActiveButton(btnStatistiques);
        chargerVue("Statistiques", "/views/statistique.fxml");
    }

    @FXML
    private void pageNotifications() {
        setActiveButton(btnNotifications);
        chargerVue("Notifications", "/views/notification.fxml");
    }

    @FXML
    private void pageFournisseurs() {
        setActiveButton(btnFournisseurs);
        chargerVue("Fournisseurs", "/views/fournisseurs.fxml");
    }

    @FXML
    private void pageParametres() {
        setActiveButton(btnParametres);
        chargerVue("Paramètres", "/views/parametre.fxml");
    }

    // ============================================================
    // ACTIONS DU MENU
    // ============================================================

    @FXML
    private void menuAccueil() {
        pageAccueil();
    }

    @FXML
    private void menuDeconnexion() {
        System.out.println("Déconnexion...");
        // TODO: SessionManager.clear();
        // SceneManager.switchScene(lblPageTitre, "/views/login.fxml", "Connexion");
    }

    @FXML
    private void menuQuitter() {
        System.exit(0);
    }

    @FXML
    private void menuListeProduits() {
        pageProduits();
    }

    @FXML
    private void menuAjouterProduit() {
        System.out.println("Ajouter un produit (à venir)");
    }

    @FXML
    private void menuCategories() {
        System.out.println("Catégories (à venir)");
    }

    @FXML
    private void menuNouvelleVente() {
        pageVentes();
    }

    @FXML
    private void menuHistoriqueVentes() {
        System.out.println("Historique des ventes (à venir)");
    }

    @FXML
    private void menuEtatStock() {
        pageStock();
    }

    @FXML
    private void menuAlertesStock() {
        System.out.println("Alertes stock (à venir)");
    }

    @FXML
    private void menuNotifications() {
        pageNotifications();
    }

    @FXML
    private void menuMarquerLu() {
        System.out.println("Marquer tout comme lu");
    }

    @FXML
    private void menuUtilisateurs() {
        System.out.println("Gestion des utilisateurs (à venir)");
    }

    @FXML
    private void menuFournisseurs() {
        pageFournisseurs();
    }

    @FXML
    private void menuPreferences() {
        pageParametres();
    }
}