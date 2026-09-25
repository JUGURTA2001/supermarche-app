package com.supermarche.frontend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.supermarche.frontend.models.BonAchat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class BonAchatService {

    /** GET /api/bons-achat */
    public static ObservableList<BonAchat> getAll() {
        try {
            String json = ApiClient.get("/api/bons-achat");
            List<BonAchat> liste = ApiClient.MAPPER.readValue(
                    json,
                    new TypeReference<List<BonAchat>>() {}
            );
            return FXCollections.observableArrayList(liste);
        } catch (Exception e) {
            System.err.println("❌ Erreur bons d'achat : " + e.getMessage());
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }
}