package com.supermarche.frontend.controllers;

import com.supermarche.frontend.models.Produit;
import com.supermarche.frontend.services.ProduitService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;

public class ProduitController {

    // ============ NAVIGATION ============
    @FXML private VBox vueListe;
    @FXML private VBox vueAjout;
    @FXML private VBox vueNotifications;
    @FXML private VBox vueAlertes;
    @FXML private Button btnListe;
    @FXML private Button btnAjouter;
    @FXML private Button btnNotifications;
    @FXML private Button btnAlertes;

    // ============ RECHERCHE ============
    @FXML private TextField txtRecherche;
    @FXML private Label lblNbProduits;
    @FXML private Label lblTotalProduits;
    @FXML private Label lblAlerteStock;

    // ============ TABLEAU PRINCIPAL ============
    @FXML private TableView<Produit> tableProduits;
    @FXML private TableColumn<Produit, Integer> colId;
    @FXML private TableColumn<Produit, String>  colNom;
    @FXML private TableColumn<Produit, String>  colCodeBarre;
    @FXML private TableColumn<Produit, BigDecimal> colPrixAchat;
    @FXML private TableColumn<Produit, BigDecimal> colPrixVente;
    @FXML private TableColumn<Produit, Integer> colQuantite;
    @FXML private TableColumn<Produit, String>  colStatut;
    @FXML private Label lblMessage;

    // ============ FORMULAIRE AJOUT ============
    @FXML private TextField txtNom;
    @FXML private TextField txtCodeBarre;
    @FXML private TextField txtPrixAchat;
    @FXML private TextField txtPrixVente;
    @FXML private TextField txtQuantite;
    @FXML private TextField txtSeuilAlerte;
    @FXML private ComboBox<String> comboCategorie;
    @FXML private ComboBox<String> comboFournisseur;
    @FXML private ComboBox<String> comboUnite;
    @FXML private DatePicker dpExpiration;
    @FXML private Label lblMessageAjout;

    // ============ VUE NOTIFICATIONS ============
    @FXML private Label lblMessageNotif;

    // ============ VUE ALERTES ============
    @FXML private TableView<Produit> tableAlertes;
    @FXML private TableColumn<Produit, Integer> colAlerteId;
    @FXML private TableColumn<Produit, String>  colAlerteNom;
    @FXML private TableColumn<Produit, Integer> colAlerteStock;
    @FXML private TableColumn<Produit, Integer> colAlerteSeuil;
    @FXML private TableColumn<Produit, String>  colAlerteNiveau;

    // ============ DONNÉES ============
    private final ObservableList<Produit> listeProduits = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // --- Colonnes du tableau principal ---
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colCodeBarre.setCellValueFactory(new PropertyValueFactory<>("codeProduit"));
        colPrixAchat.setCellValueFactory(new PropertyValueFactory<>("prixAchat"));
        colPrixVente.setCellValueFactory(new PropertyValueFactory<>("prixDetail"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("qteInitiale"));
        colStatut.setCellValueFactory(cellData -> {
            Integer stock = cellData.getValue().getQteInitiale();
            if (stock == null) stock = 0;
            String s = stock == 0 ? "❌ Rupture"
                    : stock < 10 ? "⚠️ Faible"
                    : "✅ Disponible";
            return new SimpleStringProperty(s);
        });

        tableProduits.setItems(listeProduits);

        // --- Colonnes Alertes ---
        colAlerteId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAlerteNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colAlerteStock.setCellValueFactory(new PropertyValueFactory<>("qteInitiale"));
        colAlerteSeuil.setCellValueFactory(new PropertyValueFactory<>("qteAlerte"));
        colAlerteNiveau.setCellValueFactory(cellData -> {
            Integer stock = cellData.getValue().getQteInitiale();
            if (stock == null) stock = 0;
            String niveau = stock == 0 ? "🔴 Critique"
                    : stock < 5 ? "🟠 Très faible"
                    : "🟡 Faible";
            return new SimpleStringProperty(niveau);
        });

        // --- ComboBox ---
        if (comboCategorie != null) comboCategorie.setItems(FXCollections.observableArrayList(
                "Alimentaire", "Boisson", "Hygiène", "Ménage", "Autre"));
        if (comboFournisseur != null) comboFournisseur.setItems(FXCollections.observableArrayList(
                "Fournisseur A", "Fournisseur B"));
        if (comboUnite != null) comboUnite.setItems(FXCollections.observableArrayList(
                "pièce", "kg", "L", "carton"));

        // --- Charger depuis l'API ---
        chargerProduits();
    }

    // ============================================================
    // CHARGEMENT DEPUIS L'API
    // ============================================================
    private void chargerProduits() {
        try {
            ObservableList<Produit> produits = ProduitService.getAll();
            listeProduits.setAll(produits);

            if (lblNbProduits != null) lblNbProduits.setText(listeProduits.size() + " produit(s)");
            if (lblTotalProduits != null) lblTotalProduits.setText("Total : " + listeProduits.size());

            long nbAlertes = listeProduits.stream()
                    .filter(p -> p.getQteInitiale() != null && p.getQteInitiale() < 10)
                    .count();
            if (lblAlerteStock != null) lblAlerteStock.setText("Alertes : " + nbAlertes);

            System.out.println("✅ " + listeProduits.size() + " produits chargés depuis l'API");
        } catch (Exception e) {
            System.err.println("❌ Erreur de chargement : " + e.getMessage());
            e.printStackTrace();
            if (lblMessage != null) {
                lblMessage.setStyle("-fx-text-fill: #e74c3c;");
                lblMessage.setText("Erreur de connexion au backend");
            }
        }
    }

    // ============================================================
    // NAVIGATION
    // ============================================================
    @FXML private void afficherListe()        { basculerVue(vueListe, btnListe); }
    @FXML private void afficherAjout()        { basculerVue(vueAjout, btnAjouter); }
    @FXML private void afficherNotifications() { basculerVue(vueNotifications, btnNotifications); }
    @FXML private void afficherAlertes()      { basculerVue(vueAlertes, btnAlertes); chargerAlertes(); }

    private void basculerVue(VBox vueActive, Button btnActif) {
        if (vueListe != null)         { vueListe.setVisible(false);         vueListe.setManaged(false); }
        if (vueAjout != null)         { vueAjout.setVisible(false);         vueAjout.setManaged(false); }
        if (vueNotifications != null) { vueNotifications.setVisible(false); vueNotifications.setManaged(false); }
        if (vueAlertes != null)       { vueAlertes.setVisible(false);       vueAlertes.setManaged(false); }

        if (vueActive != null) {
            vueActive.setVisible(true);
            vueActive.setManaged(true);
        }

        if (btnListe != null)         btnListe.getStyleClass().remove("nav-btn-active");
        if (btnAjouter != null)       btnAjouter.getStyleClass().remove("nav-btn-active");
        if (btnNotifications != null) btnNotifications.getStyleClass().remove("nav-btn-active");
        if (btnAlertes != null)       btnAlertes.getStyleClass().remove("nav-btn-active");

        if (btnActif != null && !btnActif.getStyleClass().contains("nav-btn-active")) {
            btnActif.getStyleClass().add("nav-btn-active");
        }
    }

    private void chargerAlertes() {
        ObservableList<Produit> alertes = FXCollections.observableArrayList();
        for (Produit p : listeProduits) {
            if (p.getQteInitiale() != null && p.getQteInitiale() < 10) alertes.add(p);
        }
        if (tableAlertes != null) tableAlertes.setItems(alertes);
    }

    // ============================================================
    // ACTIONS
    // ============================================================
    @FXML
    private void handleAjouter() {
        chargerProduits();
    }

    @FXML
    private void handleModifier() {
        Produit sel = tableProduits.getSelectionModel().getSelectedItem();
        if (sel == null) {
            if (lblMessage != null) {
                lblMessage.setStyle("-fx-text-fill: #e74c3c;");
                lblMessage.setText("Sélectionnez un produit.");
            }
            return;
        }
        if (txtNom != null) txtNom.setText(sel.getNom());
        if (txtCodeBarre != null) txtCodeBarre.setText(sel.getCodeProduit());
        if (txtPrixAchat != null) txtPrixAchat.setText(sel.getPrixAchat() == null ? "" : sel.getPrixAchat().toString());
        if (txtPrixVente != null) txtPrixVente.setText(sel.getPrixDetail() == null ? "" : sel.getPrixDetail().toString());
        if (txtQuantite != null) txtQuantite.setText(sel.getQteInitiale() == null ? "" : sel.getQteInitiale().toString());
        afficherAjout();
    }

    @FXML
    private void handleSupprimer() {
        Produit sel = tableProduits.getSelectionModel().getSelectedItem();
        if (sel == null) {
            if (lblMessage != null) {
                lblMessage.setStyle("-fx-text-fill: #e74c3c;");
                lblMessage.setText("Sélectionnez un produit.");
            }
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer \"" + sel.getNom() + "\" ?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) {
                if (lblMessage != null) {
                    lblMessage.setStyle("-fx-text-fill: #27ae60;");
                    lblMessage.setText("Produit supprimé (à implémenter côté API).");
                }
            }
        });
    }

    @FXML
    private void handleEnregistrer() {
        if (lblMessageAjout != null) {
            lblMessageAjout.setStyle("-fx-text-fill: #27ae60;");
            lblMessageAjout.setText("À implémenter (multipart/form-data)");
        }
    }

    @FXML
    private void handleAnnuler() {
        afficherListe();
    }

    // ============================================================
    // NOTIFICATIONS
    // ============================================================
    @FXML
    private void marquerToutLu() {
        if (lblMessageNotif != null) {
            lblMessageNotif.setStyle("-fx-text-fill: #27ae60;");
            lblMessageNotif.setText("✅ Toutes les notifications sont marquées comme lues.");
        }
    }
}