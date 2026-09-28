package com.supermarche.frontend.controllers;

import com.supermarche.frontend.models.BonAchat;
import com.supermarche.frontend.models.BonCommande;
import com.supermarche.frontend.models.Fournisseur;
import com.supermarche.frontend.models.Produit;
import com.supermarche.frontend.services.BonAchatService;
import com.supermarche.frontend.services.BonCommandeService;
import com.supermarche.frontend.services.FournisseurService;
import com.supermarche.frontend.services.ProduitService;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class BonController {

    private static final ZoneId ZONE_ALGER = ZoneId.of("Africa/Algiers");

    // ============ NAVIGATION ============
    @FXML private VBox vueBonCommande;
    @FXML private VBox vueBonAchat;
    @FXML private VBox vueFournisseurs;
    @FXML private VBox vueNouveauBon;
    @FXML private Button btnBonCommande;
    @FXML private Button btnBonAchat;
    @FXML private Button btnFournisseurs;
    @FXML private Button btnNouveauBon;
    @FXML private Label lblNbBons;

    // ============ VUE BON COMMANDE ============
    @FXML private TableView<BonCommande> tableBonCommande;
    @FXML private TableColumn<BonCommande, Integer> colnboncommande;
    @FXML private TableColumn<BonCommande, String>  codedateboncommande;
    @FXML private TableColumn<BonCommande, String>  colfourisseur;
    @FXML private TableColumn<BonCommande, Integer> colqtebon;
    @FXML private TableColumn<BonCommande, BigDecimal> colmontant;
    @FXML private TableColumn<BonCommande, BigDecimal> colvrsmt;
    @FXML private TableColumn<BonCommande, BigDecimal> rest;
    @FXML private TableColumn<BonCommande, String>  colregle;
    @FXML private Label lblTotalCommandes;
    @FXML private Label lblMessageCommande;

    // ============ VUE BON ACHAT ============
    @FXML private TableView<BonAchat> tableBonAchat;
    @FXML private TableColumn<BonAchat, Integer> idbon;
    @FXML private TableColumn<BonAchat, Integer> idcommande;
    @FXML private TableColumn<BonAchat, BigDecimal> totalachet;
    @FXML private TableColumn<BonAchat, BigDecimal> versment;
    @FXML private TableColumn<BonAchat, Integer> ntbarticle;
    @FXML private TableColumn<BonAchat, String> regleachet;
    @FXML private TableColumn<BonAchat, Integer> idfernissuer;
    @FXML private TableColumn<BonAchat, String> datebonachete;
    @FXML private TableColumn<BonAchat, BigDecimal> resteachet;
    @FXML private Label lblTotalAchats;
    @FXML private Label lblMessageAchat;

    // ============ VUE FOURNISSEURS ============
    @FXML private TableView<Fournisseur> tableFournisseurs;
    @FXML private TableColumn<Fournisseur, Integer> idf;
    @FXML private TableColumn<Fournisseur, String> nomsociaux;
    @FXML private TableColumn<Fournisseur, String> nif;
    @FXML private TableColumn<Fournisseur, String> registrecommerce;
    @FXML private TableColumn<Fournisseur, String> adresefernisseurs;
    @FXML private TableColumn<Fournisseur, String> telephoneferniiseur;
    @FXML private Label lblTotalFournisseurs;

    // ============ VUE NOUVEAU BON ============
    @FXML private TextField numeroBon;

    // ⭐ CHANGÉ : TextField au lieu de ComboBox<Fournisseur>
    @FXML private TextField infoforniseur;

    @FXML private TextField adressefournisseur;
    @FXML private DatePicker dateboncommande;
    @FXML private TextField rechercheproduit;
    @FXML private TableView<Produit> tableProduits;
    @FXML private TableColumn<Produit, Integer> colId;
    @FXML private TableColumn<Produit, String>  colNom;
    @FXML private TableColumn<Produit, String>  colCode;
    @FXML private TableColumn<Produit, String>  colDesignation;
    @FXML private TableColumn<Produit, Integer> colQte;
    @FXML private TableColumn<Produit, BigDecimal> colPrixachat;
    @FXML private TableColumn<Produit, BigDecimal> colPrixgros;
    @FXML private TableColumn<Produit, BigDecimal> colPrixdetaille;
    @FXML private TableColumn<Produit, BigDecimal> colTva;
    @FXML private TableColumn<Produit, BigDecimal> colMontant;
    @FXML private TextField total;
    @FXML private TextField versement;
    @FXML private TextField resteapaye;
    @FXML private TextField nbarticles;

    // ============ DONNÉES ============
    private final ObservableList<BonCommande> listeBonsCommande = FXCollections.observableArrayList();
    private final ObservableList<BonAchat>    listeBonsAchat    = FXCollections.observableArrayList();
    private final ObservableList<Fournisseur> listeFournisseurs = FXCollections.observableArrayList();
    private final ObservableList<Produit>     listeProduits     = FXCollections.observableArrayList();

    private final ContextMenu popupProduits = new ContextMenu();
    private final ContextMenu popupFournisseurs = new ContextMenu();

    // ⭐ Fournisseur actuellement sélectionné (remplace ComboBox.getValue())
    private Fournisseur fournisseurSelectionneCourant = null;

    // ============================================================
    // INITIALISATION
    // ============================================================
    @FXML
    private void initialize() {

        if (numeroBon != null) {
            numeroBon.setTextFormatter(new TextFormatter<>(change -> {
                if (change.getControlNewText().matches("[A-Za-z0-9\\-]*")) return change;
                return null;
            }));
        }

        // --- 1. Colonnes Bon Commande ---
        if (colnboncommande != null) colnboncommande.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (codedateboncommande != null) codedateboncommande.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDateBon() == null ? "-" : c.getValue().getDateBon().toString()));
        if (colfourisseur != null) colfourisseur.setCellValueFactory(new PropertyValueFactory<>("nomFournisseur"));
        if (colqtebon != null) colqtebon.setCellValueFactory(new PropertyValueFactory<>("nbArticles"));
        if (colmontant != null) colmontant.setCellValueFactory(new PropertyValueFactory<>("total"));
        if (colvrsmt != null) colvrsmt.setCellValueFactory(new PropertyValueFactory<>("versement"));
        if (rest != null) rest.setCellValueFactory(new PropertyValueFactory<>("reste"));
        if (colregle != null) colregle.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEstConverti() != null && c.getValue().getEstConverti()
                        ? "✅ Converti" : "⏳ En attente"));
        if (tableBonCommande != null) tableBonCommande.setItems(listeBonsCommande);

        // --- 2. Colonnes Bon Achat ---
        if (idbon != null) idbon.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (idcommande != null) idcommande.setCellValueFactory(new PropertyValueFactory<>("bonCommandeId"));
        if (totalachet != null) totalachet.setCellValueFactory(new PropertyValueFactory<>("total"));
        if (versment != null) versment.setCellValueFactory(new PropertyValueFactory<>("versement"));
        if (ntbarticle != null) ntbarticle.setCellValueFactory(new PropertyValueFactory<>("nbArticles"));
        if (regleachet != null) regleachet.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEstRegle() != null && c.getValue().getEstRegle()
                        ? "✅ Réglé" : "⏳ Non réglé"));
        if (idfernissuer != null) idfernissuer.setCellValueFactory(new PropertyValueFactory<>("fournisseurId"));
        if (datebonachete != null) datebonachete.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDateBon() == null ? "-" : c.getValue().getDateBon().toString()));
        if (resteachet != null) resteachet.setCellValueFactory(new PropertyValueFactory<>("reste"));
        if (tableBonAchat != null) tableBonAchat.setItems(listeBonsAchat);

        // --- 3. Colonnes Fournisseurs ---
        if (idf != null) idf.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (nomsociaux != null) nomsociaux.setCellValueFactory(new PropertyValueFactory<>("nomSociete"));
        if (nif != null) nif.setCellValueFactory(new PropertyValueFactory<>("nif"));
        if (registrecommerce != null) registrecommerce.setCellValueFactory(new PropertyValueFactory<>("registreCommerce"));
        if (adresefernisseurs != null) adresefernisseurs.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        if (telephoneferniiseur != null) telephoneferniiseur.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        if (tableFournisseurs != null) tableFournisseurs.setItems(listeFournisseurs);

        // --- 4. Table Produits (éditable) ---
        if (tableProduits != null) {
            tableProduits.setItems(listeProduits);
            tableProduits.setEditable(true);
        }

        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colNom != null) colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        if (colCode != null) colCode.setCellValueFactory(new PropertyValueFactory<>("codeProduit"));
        if (colDesignation != null) colDesignation.setCellValueFactory(new PropertyValueFactory<>("designation"));

        if (colQte != null) {
            colQte.setCellValueFactory(new PropertyValueFactory<>("qteInitiale"));
            colQte.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter() {
                @Override public Integer fromString(String s) {
                    if (s == null || s.isBlank()) return 0;
                    String c = s.replaceAll("[^0-9]", "");
                    return c.isEmpty() ? 0 : Integer.parseInt(c);
                }
            }));
            colQte.setOnEditCommit(e -> {
                e.getRowValue().setQteInitiale(e.getNewValue() == null ? 0 : e.getNewValue());
                recalculerTotaux();
            });
        }

        configurerColonneBigDecimal(colPrixachat, "prixAchat", BigDecimal.ZERO);
        configurerColonneBigDecimal(colPrixgros, "prixGros", BigDecimal.ZERO);
        configurerColonneBigDecimal(colPrixdetaille, "prixDetail", BigDecimal.ZERO);
        configurerColonneBigDecimal(colTva, "tva", new BigDecimal("20.00"));

        if (colMontant != null) {
            colMontant.setCellValueFactory(c -> {
                Produit p = c.getValue();
                if (p.getPrixAchat() == null || p.getQteInitiale() == null) {
                    return new SimpleObjectProperty<>(BigDecimal.ZERO);
                }
                return new SimpleObjectProperty<>(
                        p.getPrixAchat().multiply(BigDecimal.valueOf(p.getQteInitiale())));
            });
            colMontant.setEditable(false);
        }

        // --- 5. Charger les données ---
        chargerTout();

        // --- 6. Recherche fournisseur (nouveau système, stable) ---
        configurerRechercheFournisseur();

        // --- 7. Recherche produit ---
        configurerRechercheProduit();

        // --- 8. Listener versement ---
        if (versement != null) {
            versement.textProperty().addListener((obs, o, n) -> recalculerTotaux());
        }

        // --- 9. Préparer le formulaire ---
        preparerNouveauBon();

        // --- 10. Vue par défaut ---
        afficherBonCommande();
    }

    // ============================================================
    // UTILITAIRE : colonne BigDecimal éditable
    // ============================================================
    private void configurerColonneBigDecimal(TableColumn<Produit, BigDecimal> col,
                                             String property,
                                             BigDecimal defaultValue) {
        if (col == null) return;
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter() {
            @Override public BigDecimal fromString(String s) {
                if (s == null || s.isBlank()) return defaultValue;
                String c = s.replaceAll("[^0-9.]", "");
                try { return new BigDecimal(c); }
                catch (Exception ex) { return defaultValue; }
            }
            @Override public String toString(BigDecimal b) {
                return b == null ? defaultValue.toPlainString() : b.toPlainString();
            }
        }));
        col.setOnEditCommit(e -> {
            Produit p = e.getRowValue();
            BigDecimal val = e.getNewValue() == null ? defaultValue : e.getNewValue();
            switch (property) {
                case "prixAchat":  p.setPrixAchat(val);  break;
                case "prixGros":   p.setPrixGros(val);   break;
                case "prixDetail": p.setPrixDetail(val); break;
                case "tva":        p.setTva(val);        break;
            }
            recalculerTotaux();
        });
    }

    // ============================================================
    // PRÉPARER LE FORMULAIRE
    // ============================================================
    private void preparerNouveauBon() {
        if (numeroBon != null) numeroBon.setText(genererNumeroBon());
        if (dateboncommande != null) dateboncommande.setValue(LocalDate.now(ZONE_ALGER));
        if (rechercheproduit != null) rechercheproduit.clear();
        if (versement != null) versement.clear();

        if (infoforniseur != null) infoforniseur.clear();
        if (adressefournisseur != null) adressefournisseur.clear();
        fournisseurSelectionneCourant = null;

        listeProduits.clear();
        recalculerTotaux();
    }

    private String genererNumeroBon() {
        LocalDateTime now = LocalDateTime.now(ZONE_ALGER);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        return "BC-" + now.format(fmt);
    }

    // ============================================================
    // CHARGEMENT DES DONNÉES
    // ============================================================
    private void chargerTout() {
        listeBonsCommande.setAll(BonCommandeService.getAll());
        listeBonsAchat.setAll(BonAchatService.getAll());
        listeFournisseurs.setAll(FournisseurService.getAll());

        if (lblTotalCommandes != null) lblTotalCommandes.setText("Total : " + listeBonsCommande.size());
        if (lblTotalAchats != null)     lblTotalAchats.setText("Total : " + listeBonsAchat.size());
        if (lblTotalFournisseurs != null) lblTotalFournisseurs.setText("Total : " + listeFournisseurs.size());
        if (lblNbBons != null) lblNbBons.setText(listeBonsCommande.size() + " bon(s)");

        System.out.println("✅ " + listeBonsCommande.size() + " bons chargés");
        System.out.println("✅ " + listeFournisseurs.size() + " fournisseurs chargés");
    }

    // ============================================================
    // ⭐ NOUVELLE RECHERCHE FOURNISSEUR (TextField + ContextMenu)
    // Remplace le ComboBox éditable buggé
    // ============================================================
    private void configurerRechercheFournisseur() {
        if (infoforniseur == null) return;

        popupFournisseurs.setAutoHide(true);

        infoforniseur.textProperty().addListener((obs, oldVal, newVal) -> {
            // Si le texte correspond exactement au fournisseur déjà sélectionné, ne rien faire
            if (fournisseurSelectionneCourant != null
                    && fournisseurSelectionneCourant.getNomSociete() != null
                    && fournisseurSelectionneCourant.getNomSociete().equals(newVal)) {
                return;
            }

            // Si le texte change après sélection -> on désélectionne
            fournisseurSelectionneCourant = null;

            if (newVal == null || newVal.isBlank()) {
                popupFournisseurs.hide();
                return;
            }

            String filtre = newVal.trim().toLowerCase();
            java.util.List<Fournisseur> resultats = listeFournisseurs.stream()
                    .filter(f -> f.getNomSociete() != null
                            && f.getNomSociete().toLowerCase().contains(filtre))
                    .limit(10)
                    .toList();

            popupFournisseurs.getItems().clear();

            if (resultats.isEmpty()) {
                MenuItem aucun = new MenuItem("Aucun fournisseur trouvé");
                aucun.setDisable(true);
                popupFournisseurs.getItems().add(aucun);
            } else {
                for (Fournisseur f : resultats) {
                    MenuItem item = new MenuItem(f.getNomSociete());
                    item.setOnAction(e -> selectionnerFournisseur(f));
                    popupFournisseurs.getItems().add(item);
                }
            }

            if (infoforniseur.getScene() != null) {
                popupFournisseurs.show(infoforniseur, Side.BOTTOM, 0, 0);
            }
        });

        infoforniseur.focusedProperty().addListener((obs, o, focused) -> {
            if (!focused) {
                popupFournisseurs.hide();
                // Si le texte tapé correspond exactement à un fournisseur existant, on le valide
                if (fournisseurSelectionneCourant == null) {
                    String texte = infoforniseur.getText();
                    if (texte != null && !texte.isBlank()) {
                        listeFournisseurs.stream()
                                .filter(f -> f.getNomSociete() != null
                                        && f.getNomSociete().equalsIgnoreCase(texte.trim()))
                                .findFirst()
                                .ifPresent(this::selectionnerFournisseur);
                    }
                }
            }
        });
    }

    private void selectionnerFournisseur(Fournisseur f) {
        fournisseurSelectionneCourant = f;
        infoforniseur.setText(f.getNomSociete());
        if (adressefournisseur != null) {
            adressefournisseur.setText(f.getAdresse() == null ? "" : f.getAdresse());
        }
        popupFournisseurs.hide();
    }

    // ============================================================
    // RECHERCHE PRODUIT
    // ============================================================
    private void configurerRechercheProduit() {
        if (rechercheproduit == null) return;

        popupProduits.setAutoHide(true);

        rechercheproduit.textProperty().addListener((obs, o, n) -> {
            if (n == null || n.isBlank()) { popupProduits.hide(); return; }

            ObservableList<Produit> resultats = ProduitService.search(n);
            popupProduits.getItems().clear();

            if (resultats.isEmpty()) {
                MenuItem aucun = new MenuItem("Aucun produit trouvé");
                aucun.setDisable(true);
                popupProduits.getItems().add(aucun);
            } else {
                int limite = Math.min(resultats.size(), 10);
                for (int i = 0; i < limite; i++) {
                    Produit p = resultats.get(i);
                    String label = String.format("%s — %s — %.2f DA",
                            p.getNom() == null ? "" : p.getNom(),
                            p.getDesignation() == null ? "" : p.getDesignation(),
                            p.getPrixDetail() == null ? 0.0 : p.getPrixDetail().doubleValue());

                    MenuItem item = new MenuItem(label);
                    item.setOnAction(e -> {
                        boolean existe = listeProduits.stream()
                                .anyMatch(x -> x.getId() != null && x.getId().equals(p.getId()));
                        if (!existe) listeProduits.add(p);
                        rechercheproduit.clear();
                        popupProduits.hide();
                        recalculerTotaux();
                    });
                    popupProduits.getItems().add(item);
                }
            }

            if (!popupProduits.getItems().isEmpty() && rechercheproduit.getScene() != null) {
                popupProduits.show(rechercheproduit, Side.BOTTOM, 0, 0);
            }
        });

        rechercheproduit.focusedProperty().addListener((obs, o, focused) -> {
            if (!focused) popupProduits.hide();
        });
    }

    // ============================================================
    // RECALCUL DES TOTAUX
    // ============================================================
    private void recalculerTotaux() {
        BigDecimal t = BigDecimal.ZERO;
        int nb = 0;
        for (Produit p : listeProduits) {
            if (p.getPrixAchat() != null && p.getQteInitiale() != null) {
                t = t.add(p.getPrixAchat().multiply(BigDecimal.valueOf(p.getQteInitiale())));
                nb += p.getQteInitiale();
            }
        }
        if (total != null)      total.setText(t.toString());
        if (nbarticles != null) nbarticles.setText(String.valueOf(nb));

        BigDecimal v = BigDecimal.ZERO;
        if (versement != null && versement.getText() != null && !versement.getText().isBlank()) {
            try { v = new BigDecimal(versement.getText().trim()); } catch (Exception ignored) {}
        }
        if (resteapaye != null) resteapaye.setText(t.subtract(v).toString());

        if (tableProduits != null) tableProduits.refresh();
    }

    // ============================================================
    // NAVIGATION
    // ============================================================
    @FXML private void afficherBonCommande()  { basculerVue(vueBonCommande, btnBonCommande); }
    @FXML private void afficherBonAchat()     { basculerVue(vueBonAchat, btnBonAchat); }
    @FXML private void afficherFournisseurs() { basculerVue(vueFournisseurs, btnFournisseurs); }

    @FXML private void afficherNouveauBon() {
        preparerNouveauBon();
        basculerVue(vueNouveauBon, btnNouveauBon);
    }

    private void basculerVue(VBox vueActive, Button btnActif) {
        if (vueBonCommande != null)  { vueBonCommande.setVisible(false);  vueBonCommande.setManaged(false); }
        if (vueBonAchat != null)     { vueBonAchat.setVisible(false);     vueBonAchat.setManaged(false); }
        if (vueFournisseurs != null) { vueFournisseurs.setVisible(false); vueFournisseurs.setManaged(false); }
        if (vueNouveauBon != null)   { vueNouveauBon.setVisible(false);   vueNouveauBon.setManaged(false); }

        if (vueActive != null) { vueActive.setVisible(true); vueActive.setManaged(true); }

        if (btnBonCommande != null)  btnBonCommande.getStyleClass().remove("nav-btn-active");
        if (btnBonAchat != null)     btnBonAchat.getStyleClass().remove("nav-btn-active");
        if (btnFournisseurs != null) btnFournisseurs.getStyleClass().remove("nav-btn-active");
        if (btnNouveauBon != null)   btnNouveauBon.getStyleClass().remove("nav-btn-active");

        if (btnActif != null && !btnActif.getStyleClass().contains("nav-btn-active")) {
            btnActif.getStyleClass().add("nav-btn-active");
        }
    }

    // ============================================================
    // POPUP AJOUTER PRODUIT
    // ============================================================
    @FXML
    private void ouvrirPopupAjoutProduit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ajout-produit.fxml"));
            Parent root = loader.load();

            Stage popup = new Stage();
            popup.setTitle("Ajouter un produit");
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(vueNouveauBon.getScene().getWindow());
            popup.setScene(new Scene(root));
            popup.setResizable(false);
            popup.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    // ACTIONS
    // ============================================================
    @FXML private void retourMenu() { System.out.println("Retour"); }

    @FXML private void achteboncommande() {
        BonCommande sel = tableBonCommande.getSelectionModel().getSelectedItem();
        if (sel == null) {
            lblMessageCommande.setStyle("-fx-text-fill: #e74c3c;");
            lblMessageCommande.setText("Sélectionnez un bon.");
            return;
        }
        lblMessageCommande.setStyle("-fx-text-fill: #27ae60;");
        lblMessageCommande.setText("✅ Bon " + sel.getId() + " converti (à implémenter).");
    }

    @FXML private void btnupdatebonCommande() { System.out.println("Modifier bon"); }

    @FXML private void supprimerBon() {
        BonCommande sel = tableBonCommande.getSelectionModel().getSelectedItem();
        if (sel == null) {
            lblMessageCommande.setStyle("-fx-text-fill: #e74c3c;");
            lblMessageCommande.setText("Sélectionnez un bon à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer le bon N° " + sel.getId() + " ?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) {
                listeBonsCommande.remove(sel);
                lblMessageCommande.setStyle("-fx-text-fill: #27ae60;");
                lblMessageCommande.setText("✅ Bon supprimé.");
            }
        });
    }

    @FXML private void suppremmeligmedanstableau() {
        Produit sel = tableProduits.getSelectionModel().getSelectedItem();
        if (sel == null) {
            lblMessageCommande.setStyle("-fx-text-fill: #e74c3c;");
            lblMessageCommande.setText("Sélectionnez une ligne.");
            return;
        }
        listeProduits.remove(sel);
        recalculerTotaux();
    }

    @FXML private void handleAjouterProduit() {
        if (rechercheproduit != null && !rechercheproduit.getText().isBlank()) {
            ObservableList<Produit> resultats = ProduitService.search(rechercheproduit.getText());
            if (!resultats.isEmpty()) {
                Produit p = resultats.get(0);
                boolean existe = listeProduits.stream()
                        .anyMatch(x -> x.getId() != null && x.getId().equals(p.getId()));
                if (!existe) listeProduits.add(p);
                rechercheproduit.clear();
                recalculerTotaux();
            }
        }
    }

    // ⭐ Gardé pour compatibilité si le FXML l'appelle encore (Enter dans le champ)
    @FXML private void fournisseurSelectionne() {
        String texte = infoforniseur.getText();
        if (texte == null || texte.isBlank()) return;

        listeFournisseurs.stream()
                .filter(f -> f.getNomSociete() != null
                        && f.getNomSociete().equalsIgnoreCase(texte.trim()))
                .findFirst()
                .ifPresent(this::selectionnerFournisseur);
    }

    @FXML private void ajoutef() { System.out.println("Ajouter fournisseur"); }

    // ============================================================
    // ENREGISTRER LE BON
    // ============================================================
    @FXML
    private void enregistrerBonCommande() {

        // ⭐ Récupérer le fournisseur sélectionné (plus de ComboBox.getValue())
        Fournisseur fournisseur = fournisseurSelectionneCourant;

        if (fournisseur == null) {
            String saisie = infoforniseur.getText();
            if (saisie != null && !saisie.isBlank()) {
                fournisseur = listeFournisseurs.stream()
                        .filter(f -> f.getNomSociete() != null
                                && f.getNomSociete().equalsIgnoreCase(saisie.trim()))
                        .findFirst().orElse(null);
            }
        }

        if (fournisseur == null) {
            afficherErreur("Sélectionnez un fournisseur dans la liste.");
            return;
        }

        LocalDate date = dateboncommande.getValue();
        if (date == null) { afficherErreur("Sélectionnez une date."); return; }

        if (listeProduits.isEmpty()) {
            afficherErreur("Ajoutez au moins un produit.");
            return;
        }

        for (Produit p : listeProduits) {
            if (p.getQteInitiale() == null || p.getQteInitiale() <= 0) {
                afficherErreur("Le produit \"" + p.getNom() + "\" doit avoir une quantité > 0.");
                return;
            }
            if (p.getPrixAchat() == null || p.getPrixAchat().compareTo(BigDecimal.ZERO) <= 0) {
                afficherErreur("Le produit \"" + p.getNom() + "\" doit avoir un prix d'achat > 0.");
                return;
            }
        }

        BigDecimal totalCalc = BigDecimal.ZERO;
        for (Produit p : listeProduits) {
            totalCalc = totalCalc.add(p.getPrixAchat()
                    .multiply(BigDecimal.valueOf(p.getQteInitiale())));
        }

        BigDecimal versementValue = BigDecimal.ZERO;
        if (versement.getText() != null && !versement.getText().isBlank()) {
            try { versementValue = new BigDecimal(versement.getText().trim()); }
            catch (NumberFormatException e) {
                afficherErreur("Le versement doit être un nombre valide.");
                return;
            }
        }

        if (versementValue.compareTo(BigDecimal.ZERO) < 0) {
            afficherErreur("Le versement ne peut pas être négatif.");
            return;
        }
        if (versementValue.compareTo(totalCalc) > 0) {
            afficherErreur("Le versement ne peut pas dépasser le total.");
            return;
        }

        String numero = numeroBon.getText();
        if (numero == null || numero.isBlank()) {
            numero = genererNumeroBon();
            numeroBon.setText(numero);
        }

        try {
            BonCommande saved = BonCommandeService.create(
                    numero, fournisseur, date, versementValue, listeProduits);

            if (saved == null) {
                afficherErreur("Erreur lors de l'enregistrement (vérifiez le backend).");
                return;
            }

            lblMessageCommande.setStyle("-fx-text-fill: #27ae60;");
            lblMessageCommande.setText("✅ Bon N° " + saved.getId() + " enregistré.");

            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Succès");
            ok.setHeaderText(null);
            ok.setContentText("Bon N° " + saved.getId()
                    + " enregistré.\nTotal : " + saved.getTotal()
                    + " DA — Reste : " + saved.getReste() + " DA");
            ok.showAndWait();

            chargerTout();
            preparerNouveauBon();
            afficherBonCommande();

        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur : " + e.getMessage());
        }
    }

    private void afficherErreur(String message) {
        lblMessageCommande.setStyle("-fx-text-fill: #e74c3c;");
        lblMessageCommande.setText("❌ " + message);

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}