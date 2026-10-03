package com.supermarche.frontend.controllers;

import com.supermarche.frontend.models.Produit;
import com.supermarche.frontend.services.ProduitService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;

public class AjoutProduitController {

    // ============ CHAMPS TEXTE ============
    @FXML private TextField unite;
    @FXML private TextField nom;
    @FXML private TextField marque;
    @FXML private TextField famille;
    @FXML private TextField designation;
    @FXML private TextField rayonnage;
    @FXML private TextField codeproduit;
    @FXML private TextField qteinitiale;
    @FXML private TextField qteAlerte;
    @FXML private TextField joursalerte;
    @FXML private TextField prixachat;
    @FXML private TextField prixgros;
    @FXML private TextField prixdetail;
    @FXML private TextField tva;
    @FXML private DatePicker dateperemption;

    // ============ CODE PRODUIT ============
    @FXML private Label lblEtatCode;

    // ============ PHOTO ============
    @FXML private ImageView apercuPhoto;
    @FXML private Label lblPhotoPlaceholder;
    @FXML private Button btnAjouterPhoto;
    @FXML private Button btnSupprimerPhoto;
    @FXML private Label lblInfoPhoto;

    /** Le fichier photo actuellement sélectionné (null si aucun) */
    private File fichierPhoto = null;

    /** Callback après succès (pour rafraîchir la liste parente) */
    private Runnable onSuccess;

    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    // ============================================================
    // GÉNÉRER UN CODE
    // ============================================================
    @FXML
    private void genererCode() {
        lblEtatCode.setText("⏳ Génération...");
        lblEtatCode.setStyle("-fx-text-fill: #f39c12;");

        String code = ProduitService.genererCodeUnique();

        if (code != null && !code.isBlank()) {
            codeproduit.setText(code);
            lblEtatCode.setText("✅ Code généré et vérifié");
            lblEtatCode.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        } else {
            String localCode = "PRD-" + LocalDate.now().toString().replace("-", "")
                    + "-" + String.format("%04d", (int)(Math.random() * 10000));
            codeproduit.setText(localCode);
            lblEtatCode.setText("⚠️ Code généré localement");
            lblEtatCode.setStyle("-fx-text-fill: #e67e22;");
        }
    }

    // ============================================================
    // VÉRIFIER LE CODE
    // ============================================================
    @FXML
    private void verifierCode() {
        String code = codeproduit.getText();
        if (code == null || code.isBlank()) {
            lblEtatCode.setText("ℹ️ Code vide (optionnel)");
            lblEtatCode.setStyle("-fx-text-fill: #7f8c8d;");
            return;
        }
        lblEtatCode.setText("⏳ Vérification...");
        lblEtatCode.setStyle("-fx-text-fill: #f39c12;");

        boolean existe = ProduitService.codeExists(code.trim());

        if (existe) {
            lblEtatCode.setText("❌ Ce code existe déjà !");
            lblEtatCode.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        } else {
            lblEtatCode.setText("✅ Code disponible");
            lblEtatCode.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        }
    }

    // ============================================================
    // AJOUTER UNE PHOTO
    // ============================================================
    @FXML
    private void ajouterPhoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir une photo de produit");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images (*.png, *.jpg, *.jpeg, *.gif, *.bmp)",
                        "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("Toutes les images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        Stage stage = (Stage) nom.getScene().getWindow();
        File fichier = chooser.showOpenDialog(stage);

        if (fichier == null) return;  // Annulé

        // Vérification taille (max 10 MB)
        if (fichier.length() > 10 * 1024 * 1024) {
            alerte("La photo est trop volumineuse (max 10 MB).");
            return;
        }

        fichierPhoto = fichier;

        // Affichage aperçu
        try {
            Image img = new Image(fichier.toURI().toString());
            apercuPhoto.setImage(img);
            lblPhotoPlaceholder.setVisible(false);
            btnSupprimerPhoto.setDisable(false);
            lblInfoPhoto.setText("📎 " + fichier.getName()
                    + " (" + (fichier.length() / 1024) + " Ko)");
            lblInfoPhoto.setStyle("-fx-text-fill: #27ae60;");
        } catch (Exception e) {
            alerte("Impossible de lire l'image : " + e.getMessage());
            fichierPhoto = null;
        }
    }

    // ============================================================
    // SUPPRIMER LA PHOTO
    // ============================================================
    @FXML
    private void supprimerPhoto() {
        fichierPhoto = null;
        apercuPhoto.setImage(null);
        lblPhotoPlaceholder.setVisible(true);
        lblPhotoPlaceholder.setText("Aucune photo");
        btnSupprimerPhoto.setDisable(true);
        lblInfoPhoto.setText("");
    }

    // ============================================================
    // ENREGISTRER
    // ============================================================
    @FXML
    private void enregistrer() {

        // 1. Nom obligatoire
        if (nom.getText() == null || nom.getText().isBlank()) {
            alerte("Le nom du produit est obligatoire.");
            return;
        }

        // 2. Prix détail obligatoire
        if (prixdetail.getText() == null || prixdetail.getText().isBlank()) {
            alerte("Le prix de détail est obligatoire.");
            return;
        }

        // 3. Code produit s'il est renseigné
        String code = codeproduit.getText();
        if (code != null && !code.isBlank()) {
            if (ProduitService.codeExists(code.trim())) {
                alerte("Le code produit \"" + code + "\" existe déjà.");
                return;
            }
        }

        // 4. Construction de l'objet Produit
        Produit p = new Produit();
        p.setNom(nom.getText().trim());
        p.setMarque(texteOuNull(marque));
        p.setCodeProduit(code == null || code.isBlank() ? null : code.trim());
        p.setDesignation(texteOuNull(designation));
        p.setFamille(texteOuNull(famille));
        p.setRayonnage(texteOuNull(rayonnage));
        p.setUnite(texteOuNull(unite));
        p.setQteInitiale(parseInt(qteinitiale, 0));
        p.setQteAlerte(parseInt(qteAlerte, 5));
        p.setJoursAlerte(parseInt(joursalerte, 30));
        p.setPrixAchat(parseBigDecimal(prixachat, BigDecimal.ZERO));
        p.setPrixGros(parseBigDecimal(prixgros, BigDecimal.ZERO));
        p.setPrixDetail(parseBigDecimal(prixdetail, BigDecimal.ZERO));
        p.setTva(parseBigDecimal(tva, new BigDecimal("20.00")));
        p.setDatePeremption(dateperemption.getValue());

        // 5. Appel API avec la photo
        try {
            Produit saved = ProduitService.create(p, fichierPhoto);

            if (saved == null) {
                alerte("Erreur lors de l'enregistrement (vérifiez le backend).");
                return;
            }

            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Succès");
            ok.setHeaderText(null);
            ok.setContentText("✅ Produit \"" + saved.getNom() + "\" ajouté avec succès.\nID : " + saved.getId()
                    + (saved.getPhoto() != null ? "\n📷 Photo : " + saved.getPhoto() : ""));
            ok.showAndWait();

            if (onSuccess != null) onSuccess.run();

            fermerFenetre();

        } catch (Exception e) {
            e.printStackTrace();
            alerte("Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void annuler() {
        fermerFenetre();
    }

    // ============================================================
    // UTILITAIRES
    // ============================================================
    private String texteOuNull(TextField tf) {
        if (tf == null) return null;
        String t = tf.getText();
        return (t == null || t.isBlank()) ? null : t.trim();
    }

    private Integer parseInt(TextField tf, int defaut) {
        try {
            if (tf == null || tf.getText() == null || tf.getText().isBlank()) return defaut;
            String c = tf.getText().replaceAll("[^0-9]", "");
            return c.isEmpty() ? defaut : Integer.parseInt(c);
        } catch (Exception e) { return defaut; }
    }

    private BigDecimal parseBigDecimal(TextField tf, BigDecimal defaut) {
        try {
            if (tf == null || tf.getText() == null || tf.getText().isBlank()) return defaut;
            String c = tf.getText().replaceAll("[^0-9.]", "");
            return c.isEmpty() ? defaut : new BigDecimal(c);
        } catch (Exception e) { return defaut; }
    }

    private void alerte(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) nom.getScene().getWindow();
        stage.close();
    }
}