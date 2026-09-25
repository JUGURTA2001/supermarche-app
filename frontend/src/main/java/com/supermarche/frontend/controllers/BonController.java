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
import javafx.util.StringConverter;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.math.BigDecimal;

public class BonController {

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
    @FXML private ComboBox<Fournisseur> infoforniseur;
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

    // ============================================================
    // INITIALISATION
    // ============================================================
    @FXML
    private void initialize() {

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

        // ============================================================
        // 4. TABLE PRODUITS - ÉDITABLE
        // ============================================================

        if (tableProduits != null) {
            tableProduits.setItems(listeProduits);
            tableProduits.setEditable(true);  // ⭐ Activer l'édition
        }

        // Colonnes non-modifiables
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colNom != null) colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        if (colCode != null) colCode.setCellValueFactory(new PropertyValueFactory<>("codeProduit"));
        if (colDesignation != null) colDesignation.setCellValueFactory(new PropertyValueFactory<>("designation"));

        // ⭐ COLONNE QTÉ - Modifiable, entier uniquement
        if (colQte != null) {
            colQte.setCellValueFactory(new PropertyValueFactory<>("qteInitiale"));
            colQte.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter() {
                @Override
                public Integer fromString(String s) {
                    if (s == null || s.isBlank()) return 0;
                    // Filtre : uniquement des chiffres
                    String cleaned = s.replaceAll("[^0-9]", "");
                    return cleaned.isEmpty() ? 0 : Integer.parseInt(cleaned);
                }
            }));
            colQte.setOnEditCommit(e -> {
                Produit p = e.getRowValue();
                p.setQteInitiale(e.getNewValue() == null ? 0 : e.getNewValue());
                recalculerTotaux();
            });
        }

        // ⭐ COLONNE PRIX ACHAT - Modifiable, décimal uniquement
        if (colPrixachat != null) {
            colPrixachat.setCellValueFactory(new PropertyValueFactory<>("prixAchat"));
            colPrixachat.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter() {
                @Override
                public BigDecimal fromString(String s) {
                    if (s == null || s.isBlank()) return BigDecimal.ZERO;
                    // Filtre : chiffres + un seul point
                    String cleaned = s.replaceAll("[^0-9.]", "");
                    try { return new BigDecimal(cleaned); }
                    catch (Exception ex) { return BigDecimal.ZERO; }
                }
                @Override
                public String toString(BigDecimal b) {
                    return b == null ? "0.00" : b.toPlainString();
                }
            }));
            colPrixachat.setOnEditCommit(e -> {
                Produit p = e.getRowValue();
                p.setPrixAchat(e.getNewValue() == null ? BigDecimal.ZERO : e.getNewValue());
                recalculerTotaux();
            });
        }

        // ⭐ COLONNE PRIX GROS - Modifiable, décimal uniquement
        if (colPrixgros != null) {
            colPrixgros.setCellValueFactory(new PropertyValueFactory<>("prixGros"));
            colPrixgros.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter() {
                @Override
                public BigDecimal fromString(String s) {
                    if (s == null || s.isBlank()) return BigDecimal.ZERO;
                    String cleaned = s.replaceAll("[^0-9.]", "");
                    try { return new BigDecimal(cleaned); }
                    catch (Exception ex) { return BigDecimal.ZERO; }
                }
                @Override
                public String toString(BigDecimal b) {
                    return b == null ? "0.00" : b.toPlainString();
                }
            }));
            colPrixgros.setOnEditCommit(e -> {
                Produit p = e.getRowValue();
                p.setPrixGros(e.getNewValue() == null ? BigDecimal.ZERO : e.getNewValue());
                recalculerTotaux();
            });
        }

        // ⭐ COLONNE PRIX DÉTAIL - Modifiable, décimal uniquement
        if (colPrixdetaille != null) {
            colPrixdetaille.setCellValueFactory(new PropertyValueFactory<>("prixDetail"));
            colPrixdetaille.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter() {
                @Override
                public BigDecimal fromString(String s) {
                    if (s == null || s.isBlank()) return BigDecimal.ZERO;
                    String cleaned = s.replaceAll("[^0-9.]", "");
                    try { return new BigDecimal(cleaned); }
                    catch (Exception ex) { return BigDecimal.ZERO; }
                }
                @Override
                public String toString(BigDecimal b) {
                    return b == null ? "0.00" : b.toPlainString();
                }
            }));
            colPrixdetaille.setOnEditCommit(e -> {
                Produit p = e.getRowValue();
                p.setPrixDetail(e.getNewValue() == null ? BigDecimal.ZERO : e.getNewValue());
                recalculerTotaux();
            });
        }

        // ⭐ COLONNE TVA - Modifiable, décimal uniquement
        if (colTva != null) {
            colTva.setCellValueFactory(new PropertyValueFactory<>("tva"));
            colTva.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter() {
                @Override
                public BigDecimal fromString(String s) {
                    if (s == null || s.isBlank()) return new BigDecimal("20.00");
                    String cleaned = s.replaceAll("[^0-9.]", "");
                    try { return new BigDecimal(cleaned); }
                    catch (Exception ex) { return new BigDecimal("20.00"); }
                }
                @Override
                public String toString(BigDecimal b) {
                    return b == null ? "20.00" : b.toPlainString();
                }
            }));
            colTva.setOnEditCommit(e -> {
                Produit p = e.getRowValue();
                p.setTva(e.getNewValue() == null ? new BigDecimal("20.00") : e.getNewValue());
                recalculerTotaux();
            });
        }

        // ⭐ COLONNE MONTANT - Calculée (non modifiable)
        if (colMontant != null) {
            colMontant.setCellValueFactory(c -> {
                Produit p = c.getValue();
                if (p.getPrixAchat() == null || p.getQteInitiale() == null) {
                    return new SimpleObjectProperty<>(BigDecimal.ZERO);
                }
                BigDecimal montant = p.getPrixAchat()
                        .multiply(BigDecimal.valueOf(p.getQteInitiale()));
                return new SimpleObjectProperty<>(montant);
            });
            colMontant.setEditable(false);
        }

        // --- 5. Charger les données depuis l'API ---
        chargerTout();

        // --- 6. Configurer l'autocomplete fournisseur ---
        configurerComboFournisseur();

        // --- 7. Configurer la recherche produit ---
        configurerRechercheProduit();

        // --- 8. Listener sur le versement pour recalculer le reste ---
        if (versement != null) {
            versement.textProperty().addListener((obs, oldV, newV) -> recalculerTotaux());
        }

        // --- 9. Afficher la première vue ---
        afficherBonCommande();
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

        System.out.println("✅ " + listeBonsCommande.size() + " bons de commande chargés");
        System.out.println("✅ " + listeBonsAchat.size() + " bons d'achat chargés");
        System.out.println("✅ " + listeFournisseurs.size() + " fournisseurs chargés");
    }

    // ============================================================
    // COMBOBOX FOURNISSEUR AVEC AUTOCOMPLETE
    // ============================================================
    private void configurerComboFournisseur() {
        if (infoforniseur == null) return;

        infoforniseur.setEditable(true);
        infoforniseur.setItems(listeFournisseurs);

        infoforniseur.setConverter(new StringConverter<Fournisseur>() {
            @Override
            public String toString(Fournisseur f) {
                return f == null ? "" : f.getNomSociete();
            }
            @Override
            public Fournisseur fromString(String s) {
                return listeFournisseurs.stream()
                        .filter(f -> f.getNomSociete() != null && f.getNomSociete().equalsIgnoreCase(s))
                        .findFirst().orElse(null);
            }
        });

        infoforniseur.getEditor().textProperty().addListener((obs, oldV, newV) -> {
            if (newV == null || newV.isBlank()) {
                infoforniseur.setItems(listeFournisseurs);
            } else {
                ObservableList<Fournisseur> filtres = FournisseurService.search(newV);
                infoforniseur.setItems(filtres);
                if (!filtres.isEmpty()) {
                    infoforniseur.show();
                }
            }
        });

        infoforniseur.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && adressefournisseur != null) {
                adressefournisseur.setText(newV.getAdresse() == null ? "" : newV.getAdresse());
            }
        });
    }

    // ============================================================
    // RECHERCHE PRODUIT AVEC POPUP AUTOCOMPLETE
    // ============================================================
    private void configurerRechercheProduit() {
        if (rechercheproduit == null) return;

        popupProduits.setAutoHide(true);

        rechercheproduit.textProperty().addListener((obs, oldV, newV) -> {
            if (newV == null || newV.isBlank()) {
                popupProduits.hide();
                return;
            }

            ObservableList<Produit> resultats = ProduitService.search(newV);
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
                        if (!existe) {
                            listeProduits.add(p);
                        }
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

        // Rafraîchir la table pour recalculer la colonne Montant
        if (tableProduits != null) tableProduits.refresh();
    }

    // ============================================================
    // NAVIGATION
    // ============================================================
    @FXML private void afficherBonCommande()  { basculerVue(vueBonCommande, btnBonCommande); }
    @FXML private void afficherBonAchat()     { basculerVue(vueBonAchat, btnBonAchat); }
    @FXML private void afficherFournisseurs() { basculerVue(vueFournisseurs, btnFournisseurs); }
    @FXML private void afficherNouveauBon()   { basculerVue(vueNouveauBon, btnNouveauBon); }

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

            System.out.println("✅ Popup fermé.");
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir le popup : " + e.getMessage());
            alert.showAndWait();
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
            lblMessageCommande.setText("Sélectionnez un bon.");
            return;
        }
        listeBonsCommande.remove(sel);
        lblMessageCommande.setStyle("-fx-text-fill: #27ae60;");
        lblMessageCommande.setText("✅ Bon supprimé.");
    }

    @FXML
    private void handleAjouterProduit() {
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

    @FXML
    private void suppremmeligmedanstableau() {
        Produit sel = tableProduits.getSelectionModel().getSelectedItem();
        if (sel != null) {
            listeProduits.remove(sel);
            recalculerTotaux();
        }
    }

    @FXML
    private void fournisseurSelectionne() {
        Fournisseur f = infoforniseur.getValue();
        if (f != null && adressefournisseur != null) {
            adressefournisseur.setText(f.getAdresse() == null ? "" : f.getAdresse());
        }
    }

    @FXML private void ajoutef() { System.out.println("Ajouter fournisseur"); }

    @FXML
    private void enregistrerBonCommande() {
        if (infoforniseur.getValue() == null) {
            lblMessageCommande.setStyle("-fx-text-fill: #e74c3c;");
            lblMessageCommande.setText("Sélectionnez un fournisseur.");
            return;
        }
        if (listeProduits.isEmpty()) {
            lblMessageCommande.setStyle("-fx-text-fill: #e74c3c;");
            lblMessageCommande.setText("Ajoutez au moins un produit.");
            return;
        }
        lblMessageCommande.setStyle("-fx-text-fill: #27ae60;");
        lblMessageCommande.setText("✅ Bon enregistré (à implémenter).");
    }
}