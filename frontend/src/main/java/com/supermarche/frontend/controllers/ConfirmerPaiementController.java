package com.supermarche.frontend.controllers;

import com.supermarche.frontend.models.BonCommande;
import com.supermarche.frontend.services.BonCommandeService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class ConfirmerPaiementController {

    @FXML private Label lblNumeroBon;
    @FXML private Label lblFournisseur;
    @FXML private Label lblTotal;
    @FXML private Label lblVersementInitial;
    @FXML private Label lblReste;
    @FXML private TextField txtVersementRecu;
    @FXML private Label lblMessage;

    private BonCommande bon;
    private Runnable onSuccess;

    /** Appelé par BonController avant showAndWait() */
    public void setBon(BonCommande bon) {
        this.bon = bon;

        lblNumeroBon.setText("Bon N° " + bon.getId());
        lblFournisseur.setText("Fournisseur : "
                + (bon.getNomFournisseur() == null ? "-" : bon.getNomFournisseur()));

        BigDecimal total = bon.getTotal() == null ? BigDecimal.ZERO : bon.getTotal();
        BigDecimal versement = bon.getVersement() == null ? BigDecimal.ZERO : bon.getVersement();
        BigDecimal reste = bon.getReste() == null ? total.subtract(versement) : bon.getReste();

        lblTotal.setText(total + " DA");
        lblVersementInitial.setText(versement + " DA");
        lblReste.setText(reste + " DA");

        // Pré-remplir avec le reste
        txtVersementRecu.setText(reste.toPlainString());
    }

    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    @FXML
    private void confirmer() {
        if (bon == null) return;

        BigDecimal versementRecu = BigDecimal.ZERO;
        try {
            String txt = txtVersementRecu.getText();
            if (txt != null && !txt.isBlank()) {
                versementRecu = new BigDecimal(txt.trim().replaceAll("[^0-9.]", ""));
            }
        } catch (Exception e) {
            lblMessage.setText("❌ Versement invalide");
            lblMessage.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        if (versementRecu.compareTo(BigDecimal.ZERO) <= 0) {
            lblMessage.setText("❌ Le versement doit être > 0");
            lblMessage.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        // Appel API
        boolean ok = BonCommandeService.convertir(bon.getId(), versementRecu);

        if (!ok) {
            lblMessage.setText("❌ Erreur lors de la conversion.");
            lblMessage.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        // Succès
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("✅ Bon N° " + bon.getId()
                + " converti en bon d'achat.\nLe stock des produits a été mis à jour.");
        alert.showAndWait();

        if (onSuccess != null) onSuccess.run();
        fermerFenetre();
    }

    @FXML
    private void annuler() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) lblNumeroBon.getScene().getWindow();
        stage.close();
    }
}