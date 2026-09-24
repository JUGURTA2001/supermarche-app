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

    // ============ VUE LISTE ============
    @FXML private TableView<Produit> tableProduits;
    @FXML private TableColumn<Produit, Long> colId;
    @FXML private TableColumn<Produit, String> colNom;
    @FXML private TableColumn<Produit, String> colCodeBarre;
    @FXML private TableColumn<Produit, Double> colPrixAchat;
    @FXML private TableColumn<Produit, Double> colPrixVente;
    @FXML private TableColumn<Produit, Integer> colQuantite;
    @FXML private TableColumn<Produit, String> colStatut;
    @FXML private Label lblMessage;

    // ============ VUE AJOUT ============
    @FXML private TextField txtNom;
    @FXML private TextField txtCodeBarre;
    @FXML private TextField txtPrixAchat;
    @FXML private TextField txtPrixVente;
    @FXML private TextField txtQuantite;
    @FXML private TextField txtSeuilAlerte;
    @FXML private ComboBox<String> comboCategorie;
    @FXML private ComboBox<String> comboFournisseur;
    @FXML private DatePicker dpExpiration;
    @FXML private ComboBox<String> comboUnite;
    @FXML private Label lblMessageAjout;

    // ============ VUE NOTIFICATIONS ============
    @FXML private TableView<Notification> tableNotifications;
    @FXML private TableColumn<Notification, Long> colNotifId;
    @FXML private TableColumn<Notification, String> colNotifType;
    @FXML private TableColumn<Notification, String> colNotifMessage;
    @FXML private TableColumn<Notification, String> colNotifDate;
    @FXML private TableColumn<Notification, String> colNotifStatut;
    @FXML private Label lblMessageNotif;

    // ============ VUE ALERTES ============
    @FXML private TableView<Produit> tableAlertes;
    @FXML private TableColumn<Produit, Long> colAlerteId;
    @FXML private TableColumn<Produit, String> colAlerteNom;
    @FXML private TableColumn<Produit, Integer> colAlerteStock;
    @FXML private TableColumn<Produit, Integer> colAlerteSeuil;
    @FXML private TableColumn<Produit, String> colAlerteNiveau;

    // ============ DONNÉES ============
    private ObservableList<Produit> listeProduits = FXCollections.observableArrayList();
    private Produit produitSelectionne = null;

    @FXML
    private void initialize() {
        // --- Colonnes table principale ---
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colCodeBarre.setCellValueFactory(new PropertyValueFactory<>("codeBarre"));
        colPrixAchat.setCellValueFactory(new PropertyValueFactory<>("prixAchat"));
        colPrixVente.setCellValueFactory(new PropertyValueFactory<>("prixVente"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantiteStock"));

        // Colonne "Statut" calculée
        colStatut.setCellValueFactory(cellData -> {
            Produit p = cellData.getValue();
            int stock = p.getQuantiteStock();
            String statut;
            if (stock == 0) statut = "❌ Rupture";
            else if (stock < 10) statut = "⚠️ Faible";
            else statut = "✅ Disponible";
            return new SimpleStringProperty(statut);
        });

        tableProduits.setItems(listeProduits);

        // Sélection d'une ligne
        tableProduits.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> produitSelectionne = newVal);

        // --- Colonnes alertes ---
        colAlerteId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAlerteNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colAlerteStock.setCellValueFactory(new PropertyValueFactory<>("quantiteStock"));
        colAlerteSeuil.setCellValueFactory(new PropertyValueFactory<>("seuilAlerte"));
        colAlerteNiveau.setCellValueFactory(cellData -> {
            Produit p = cellData.getValue();
            int stock = p.getQuantiteStock();
            String niveau;
            if (stock == 0) niveau = "🔴 Critique";
            else if (stock < 5) niveau = "🟠 Très faible";
            else niveau = "🟡 Faible";
            return new SimpleStringProperty(niveau);
        });

        // --- Remplir ComboBox ---
        comboCategorie.setItems(FXCollections.observableArrayList(
                "Alimentaire", "Boisson", "Hygiène", "Ménage", "Autre"));
        comboFournisseur.setItems(FXCollections.observableArrayList(
                "Fournisseur A", "Fournisseur B", "Fournisseur C"));
        comboUnite.setItems(FXCollections.observableArrayList("pièce", "kg", "L", "carton"));

        // --- Charger la liste ---
        chargerProduits();
    }

    // ============================================================
    // NAVIGATION ENTRE LES VUES
    // ============================================================
    @FXML
    private void afficherListe() {
        basculerVue(vueListe, btnListe);
    }

    @FXML
    private void afficherAjout() {
        basculerVue(vueAjout, btnAjouter);
        viderFormulaire();
    }

    @FXML
    private void afficherNotifications() {
        basculerVue(vueNotifications, btnNotifications);
    }

    @FXML
    private void afficherAlertes() {
        basculerVue(vueAlertes, btnAlertes);
        chargerAlertes();
    }

    private void basculerVue(VBox vueActive, Button btnActif) {
        vueListe.setVisible(false);        vueListe.setManaged(false);
        vueAjout.setVisible(false);        vueAjout.setManaged(false);
        vueNotifications.setVisible(false);vueNotifications.setManaged(false);
        vueAlertes.setVisible(false);      vueAlertes.setManaged(false);

        vueActive.setVisible(true);
        vueActive.setManaged(true);

        // Mise à jour du bouton actif
        btnListe.getStyleClass().remove("nav-btn-active");
        btnAjouter.getStyleClass().remove("nav-btn-active");
        btnNotifications.getStyleClass().remove("nav-btn-active");
        btnAlertes.getStyleClass().remove("nav-btn-active");
        if (!btnActif.getStyleClass().contains("nav-btn-active")) {
            btnActif.getStyleClass().add("nav-btn-active");
        }
    }

    // ============================================================
    // CHARGEMENT DES DONNÉES
    // ============================================================
    private void chargerProduits() {
        listeProduits.setAll(ProduitService.getAll());
        lblNbProduits.setText(listeProduits.size() + " produit(s)");
        lblTotalProduits.setText("Total : " + listeProduits.size());
        long nbAlertes = listeProduits.stream().filter(p -> p.getQuantiteStock() < 10).count();
        lblAlerteStock.setText("Alertes : " + nbAlertes);
    }

    private void chargerAlertes() {
        ObservableList<Produit> alertes = FXCollections.observableArrayList();
        for (Produit p : listeProduits) {
            if (p.getQuantiteStock() < 10) alertes.add(p);
        }
        tableAlertes.setItems(alertes);
    }

    // ============================================================
    // AJOUT D'UN PRODUIT
    // ============================================================
    @FXML
    private void handleEnregistrer() {
        if (txtNom.getText().isBlank() || txtPrixVente.getText().isBlank()) {
            lblMessageAjout.setStyle("-fx-text-fill: #e74c3c;");
            lblMessageAjout.setText("Le nom et le prix de vente sont obligatoires.");
            return;
        }

        Produit p = new Produit();
        p.setNom(txtNom.getText());
        p.setCodeBarre(txtCodeBarre.getText());
        p.setPrixAchat(parseDouble(txtPrixAchat.getText()));
        p.setPrixVente(parseDouble(txtPrixVente.getText()));
        p.setQuantiteStock(parseInt(txtQuantite.getText()));

        Produit cree = ProduitService.create(p);
        if (cree != null) {
            lblMessageAjout.setStyle("-fx-text-fill: #27ae60;");
            lblMessageAjout.setText("✅ Produit ajouté avec succès.");
            chargerProduits();
            viderFormulaire();
        } else {
            lblMessageAjout.setStyle("-fx-text-fill: #e74c3c;");
            lblMessageAjout.setText("❌ Erreur lors de l'ajout.");
        }
    }

    @FXML
    private void handleModifier() {
        if (produitSelectionne == null) {
            lblMessage.setStyle("-fx-text-fill: #e74c3c;");
            lblMessage.setText("Sélectionnez d'abord un produit.");
            return;
        }
        // Remplir la vue Ajout avec les données du produit sélectionné
        txtNom.setText(produitSelectionne.getNom());
        txtCodeBarre.setText(produitSelectionne.getCodeBarre());
        txtPrixAchat.setText(String.valueOf(produitSelectionne.getPrixAchat()));
        txtPrixVente.setText(String.valueOf(produitSelectionne.getPrixVente()));
        txtQuantite.setText(String.valueOf(produitSelectionne.getQuantiteStock()));
        afficherAjout();
    }

    @FXML
    private void handleSupprimer() {
        if (produitSelectionne == null) {
            lblMessage.setStyle("-fx-text-fill: #e74c3c;");
            lblMessage.setText("Sélectionnez d'abord un produit.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer le produit \"" + produitSelectionne.getNom() + "\" ?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                boolean ok = ProduitService.delete(produitSelectionne.getId());
                if (ok) {
                    lblMessage.setStyle("-fx-text-fill: #27ae60;");
                    lblMessage.setText("✅ Produit supprimé.");
                    chargerProduits();
                    produitSelectionne = null;
                } else {
                    lblMessage.setStyle("-fx-text-fill: #e74c3c;");
                    lblMessage.setText("❌ Erreur lors de la suppression.");
                }
            }
        });
    }

    @FXML
    private void handleAnnuler() {
        viderFormulaire();
        afficherListe();
    }

    private void viderFormulaire() {
        txtNom.clear();
        txtCodeBarre.clear();
        txtPrixAchat.clear();
        txtPrixVente.clear();
        txtQuantite.clear();
        txtSeuilAlerte.clear();
        if (comboCategorie != null) comboCategorie.getSelectionModel().clearSelection();
        if (comboFournisseur != null) comboFournisseur.getSelectionModel().clearSelection();
        if (comboUnite != null) comboUnite.getSelectionModel().clearSelection();
        if (dpExpiration != null) dpExpiration.setValue(null);
        lblMessageAjout.setText("");
    }

    @FXML
    private void marquerToutLu() {
        lblMessageNotif.setStyle("-fx-text-fill: #27ae60;");
        lblMessageNotif.setText("✅ Toutes les notifications sont marquées comme lues.");
    }

    // ============================================================
    // UTILITAIRES
    // ============================================================
    private Double parseDouble(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0.0; }
    }
    private Integer parseInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    // ============================================================
    // CLASSE INTERNE : Notification (simple placeholder)
    // ============================================================
    public static class Notification {
        private final Long id;
        private final String type;
        private final String message;
        private final String date;
        private final String statut;

        public Notification(Long id, String type, String message, String date, String statut) {
            this.id = id; this.type = type; this.message = message;
            this.date = date; this.statut = statut;
        }
        public Long getId() { return id; }
        public String getType() { return type; }
        public String getMessage() { return message; }
        public String getDate() { return date; }
        public String getStatut() { return statut; }
    }
}