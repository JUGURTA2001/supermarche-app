package com.supermarche.frontend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.supermarche.frontend.models.BonCommande;
import com.supermarche.frontend.models.Fournisseur;
import com.supermarche.frontend.models.Produit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BonCommandeService {

    // ============================================================
    // LISTE
    // ============================================================
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

    // ============================================================
    // DÉTAIL
    // ============================================================
    public static BonCommande getById(Integer id) {
        try {
            String json = ApiClient.get("/api/bons-commande/" + id);
            return ApiClient.MAPPER.readValue(json, BonCommande.class);
        } catch (Exception e) {
            System.err.println("❌ Bon introuvable : " + e.getMessage());
            return null;
        }
    }

    // ============================================================
    // CRÉATION
    // ============================================================
    public static BonCommande create(String numero,
                                     Fournisseur fournisseur,
                                     LocalDate dateBon,
                                     BigDecimal versement,
                                     List<Produit> produits) {
        try {
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
            String response = ApiClient.post("/api/bons-commande", json);
            return ApiClient.MAPPER.readValue(response, BonCommande.class);

        } catch (Exception e) {
            System.err.println("❌ Erreur création bon : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ============================================================
    // MODIFICATION
    // ============================================================
    public static BonCommande update(Integer id, BonCommande bon) {
        try {
            String json = ApiClient.MAPPER.writeValueAsString(bon);
            String reponse = ApiClient.put("/api/bons-commande/" + id, json);
            return ApiClient.MAPPER.readValue(reponse, BonCommande.class);
        } catch (Exception e) {
            System.err.println("❌ Erreur modification bon : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ============================================================
    // CONVERSION
    // ============================================================
    public static boolean convertir(Integer bonCommandeId, BigDecimal versementSupplementaire) {
        try {
            String url = "/api/bons-commande/" + bonCommandeId + "/convertir";
            if (versementSupplementaire != null
                    && versementSupplementaire.compareTo(BigDecimal.ZERO) > 0) {
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

    // ============================================================
    // ⭐ SUPPRESSION
    // ============================================================
    /**
     * @return null si succès, sinon le message d'erreur renvoyé par le backend
     */
    public static String delete(Integer id) {
        try {
            HttpResponse<String> resp = ApiClient.deleteRaw("/api/bons-commande/" + id);

            // 204 No Content  ou  200 OK → succès
            if (resp.statusCode() == 204 || resp.statusCode() == 200) {
                return null;
            }

            // 409 / 404 / 500 → le backend renvoie {"message": "..."}
            String body = resp.body();
            if (body != null && !body.isBlank()) {
                try {
                    var node = ApiClient.MAPPER.readTree(body);
                    if (node.has("message")) {
                        return node.get("message").asText();
                    }
                } catch (Exception ignored) {}
                return body;
            }
            return "Erreur HTTP " + resp.statusCode();

        } catch (Exception e) {
            e.printStackTrace();
            return "Impossible de contacter le serveur : " + e.getMessage();
        }
    }
}