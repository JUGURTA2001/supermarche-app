package com.supermarche.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class BonCommandeDTO {
    private Integer id;
    private Integer fournisseurId;
    private String nomFournisseur;
    private LocalDate dateBon;
    private BigDecimal total;
    private BigDecimal versement;
    private BigDecimal reste;
    private Integer nbArticles;
    private String statut;
    private Boolean estConverti;
    private List<LigneBonDTO> lignes;

    @Data
    public static class LigneBonDTO {
        private Integer produitId;
        private String nomProduit;
        private Integer quantite;
        private BigDecimal prixAchat;
        private BigDecimal prixGros;
        private BigDecimal prixDetail;
        private BigDecimal tva;
    }
}