package com.supermarche.frontend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.supermarche.frontend.models.BonCommande;
import com.supermarche.frontend.models.Fournisseur;
import com.supermarche.frontend.models.Produit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BonCommandeService {

    /** GET /api/bons-commande */
    public static ObservableList<BonCommande> getAll() {
        try {
            String json = ApiClient.get("/api/bons-commande");
            List<BonCommande> liste = ApiClient.MAPPER.readValue(
                    json, new TypeReference<List<BonCommande>>() {});
            return FXCollections.observableArrayList(liste);
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement bons : " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }

    /** POST /api/bons-commande ⭐ CRÉATION */
    public static BonCommande create(String numero,
                                     Fournisseur fournisseur,
                                     LocalDate dateBon,
                                     BigDecimal versement,
                                     List<Produit> produits) {
        try {
            // --- Construire le payload JSON ---
            Map<String, Object> payload = new HashMap<>();
            payload.put("numero", numero);
            payload.put("fournisseurId", fournisseur.getId());
            payload.put("dateBon", dateBon.toString());
            payload.put("versement", versement);

            List<Map<String, Object>> lignes = new ArrayList<>();
            for (Produit p : produits) {
                Map<String, Object> l = new HashMap<>();
                l.put("produitId", p.getId());
                l.put("quantite", p.getQteInitiale());
                l.put("prixAchat", p.getPrixAchat());
                l.put("prixGros", p.getPrixGros() != null ? p.getPrixGros() : p.getPrixAchat());
                l.put("prixDetail", p.getPrixDetail() != null ? p.getPrixDetail() : p.getPrixAchat());
                l.put("tva", p.getTva() != null ? p.getTva() : new BigDecimal("20.00"));
                lignes.add(l);
            }
            payload.put("lignes", lignes);

            String json = ApiClient.MAPPER.writeValueAsString(payload);

            // --- POST ---
            String response = ApiClient.post("/api/bons-commande", json);
            return ApiClient.MAPPER.readValue(response, BonCommande.class);

        } catch (Exception e) {
            System.err.println("❌ Erreur création bon : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static boolean convertir(Integer bonCommandeId, java.math.BigDecimal versementSupplementaire) {
        try {
            String url = "/api/bons-commande/" + bonCommandeId + "/convertir";
            if (versementSupplementaire != null
                    && versementSupplementaire.compareTo(java.math.BigDecimal.ZERO) > 0) {
                url += "?versement=" + versementSupplementaire.toPlainString();
            }
            ApiClient.post(url, "");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erreur conversion : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}