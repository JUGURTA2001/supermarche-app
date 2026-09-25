package com.supermarche.frontend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.supermarche.frontend.models.Fournisseur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FournisseurService {

    /** GET /api/fournisseurs */
    public static ObservableList<Fournisseur> getAll() {
        try {
            String json = ApiClient.get("/api/fournisseurs");
            List<Fournisseur> liste = ApiClient.MAPPER.readValue(
                    json, new TypeReference<List<Fournisseur>>() {});
            return FXCollections.observableArrayList(liste);
        } catch (Exception e) {
            System.err.println("❌ Erreur fournisseurs : " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }

    /** GET /api/fournisseurs/search?nom=xxx  ⭐ RECHERCHE */
    public static ObservableList<Fournisseur> search(String nom) {
        try {
            String encoded = URLEncoder.encode(nom, StandardCharsets.UTF_8);
            String json = ApiClient.get("/api/fournisseurs/search?nom=" + encoded);
            List<Fournisseur> liste = ApiClient.MAPPER.readValue(
                    json, new TypeReference<List<Fournisseur>>() {});
            return FXCollections.observableArrayList(liste);
        } catch (Exception e) {
            System.err.println("❌ Erreur recherche fournisseur : " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }
}