package com.supermarche.frontend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.supermarche.frontend.models.Produit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ProduitService {

    /** GET /api/produits */
    public static ObservableList<Produit> getAll() {
        try {
            String json = ApiClient.get("/api/produits");
            List<Produit> liste = ApiClient.MAPPER.readValue(
                    json, new TypeReference<List<Produit>>() {});
            return FXCollections.observableArrayList(liste);
        } catch (Exception e) {
            System.err.println("❌ Erreur produits : " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }

    /** GET /api/produits/{id} */
    public static Produit getById(Integer id) {
        try {
            String json = ApiClient.get("/api/produits/" + id);
            return ApiClient.MAPPER.readValue(json, Produit.class);
        } catch (Exception e) {
            System.err.println("❌ Produit " + id + " introuvable : " + e.getMessage());
            return null;
        }
    }

    /** GET /api/produits/search?nom=xxx  ⭐ RECHERCHE */
    public static ObservableList<Produit> search(String nom) {
        try {
            String encoded = URLEncoder.encode(nom, StandardCharsets.UTF_8);
            String json = ApiClient.get("/api/produits/search?nom=" + encoded);
            List<Produit> liste = ApiClient.MAPPER.readValue(
                    json, new TypeReference<List<Produit>>() {});
            return FXCollections.observableArrayList(liste);
        } catch (Exception e) {
            System.err.println("❌ Erreur recherche produit : " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }
}