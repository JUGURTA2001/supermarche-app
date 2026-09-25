package com.supermarche.frontend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.supermarche.frontend.models.BonCommande;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class BonCommandeService {

    /** GET /api/bons-commande */
    public static ObservableList<BonCommande> getAll() {
        try {
            String json = ApiClient.get("/api/bons-commande");
            List<BonCommande> liste = ApiClient.MAPPER.readValue(
                    json,
                    new TypeReference<List<BonCommande>>() {}
            );
            return FXCollections.observableArrayList(liste);
        } catch (Exception e) {
            System.err.println("❌ Erreur bons de commande : " + e.getMessage());
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }
}