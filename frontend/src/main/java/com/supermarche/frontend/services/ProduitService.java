package com.supermarche.frontend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.supermarche.frontend.models.Produit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ProduitService {

    // ============================================================
    // GET /api/produits → liste complète
    // ============================================================
    public static ObservableList<Produit> getAll() {
        try {
            String json = ApiClient.get("/api/produits");
            List<Produit> liste = ApiClient.MAPPER.readValue(
                    json, new TypeReference<List<Produit>>() {});
            return FXCollections.observableArrayList(liste);
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement produits : " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }

    // ============================================================
    // GET /api/produits/{id} → un produit
    // ============================================================
    public static Produit getById(Integer id) {
        try {
            String json = ApiClient.get("/api/produits/" + id);
            return ApiClient.MAPPER.readValue(json, Produit.class);
        } catch (Exception e) {
            System.err.println("❌ Produit " + id + " introuvable : " + e.getMessage());
            return null;
        }
    }

    // ============================================================
    // GET /api/produits/search?nom=xxx → recherche
    // ============================================================
    public static ObservableList<Produit> search(String nom) {
        try {
            String encoded = URLEncoder.encode(nom == null ? "" : nom, StandardCharsets.UTF_8);
            String json = ApiClient.get("/api/produits/search?nom=" + encoded);
            List<Produit> liste = ApiClient.MAPPER.readValue(
                    json, new TypeReference<List<Produit>>() {});
            return FXCollections.observableArrayList(liste);
        } catch (Exception e) {
            System.err.println("❌ Erreur recherche produit : " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }

    // ============================================================
    // GET /api/produits/exists?code=xxx → true / false
    // ============================================================
    public static boolean codeExists(String code) {
        if (code == null || code.isBlank()) return false;
        try {
            String encoded = URLEncoder.encode(code.trim(), StandardCharsets.UTF_8);
            String reponse = ApiClient.get("/api/produits/exists?code=" + encoded);
            return "true".equalsIgnoreCase(reponse.trim());
        } catch (Exception e) {
            System.err.println("❌ Erreur vérification code : " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // GET /api/produits/generate-code → code unique
    // ============================================================
    public static String genererCodeUnique() {
        try {
            return ApiClient.get("/api/produits/generate-code").trim();
        } catch (Exception e) {
            System.err.println("❌ Erreur génération code : " + e.getMessage());
            return null;
        }
    }

    // ============================================================
    // POST /api/produits → création (multipart avec photo optionnelle)
    // ============================================================
    public static Produit create(Produit p, File photo) {
        try {
            String json = ApiClient.MAPPER.writeValueAsString(p);
            String reponse = ApiClient.postProduitMultipart(json, photo);
            return ApiClient.MAPPER.readValue(reponse, Produit.class);
        } catch (Exception e) {
            System.err.println("❌ Erreur création produit : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ============================================================
    // PUT /api/produits/{id} → mise à jour (multipart)
    // ============================================================
    public static Produit update(Integer id, Produit p, File photo) {
        try {
            String json = ApiClient.MAPPER.writeValueAsString(p);
            String reponse = ApiClient.putProduitMultipart(id, json, photo);
            return ApiClient.MAPPER.readValue(reponse, Produit.class);
        } catch (Exception e) {
            System.err.println("❌ Erreur mise à jour produit : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ============================================================
    // DELETE /api/produits/{id} → suppression
    // ============================================================
    public static boolean delete(Integer id) {
        try {
            ApiClient.delete("/api/produits/" + id);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erreur suppression produit : " + e.getMessage());
            return false;
        }
    }
}