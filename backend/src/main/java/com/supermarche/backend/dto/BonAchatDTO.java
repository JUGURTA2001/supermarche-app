package com.supermarche.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BonAchatDTO {
    private Integer id;
    private Integer bonCommandeId;
    private BigDecimal total;
    private BigDecimal versement;
    private Integer nbArticles;
    private Boolean estRegle;
    private Integer fournisseurId;
    private String nomFournisseur;
    private LocalDateTime dateBon;
    private BigDecimal reste;
    private List<DetailLigneDTO> details;

    @Data
    public static class DetailLigneDTO {
        private Integer produitId;
        private String nomProduit;
        private Integer quantite;
        private BigDecimal prixAchat;
    }
}