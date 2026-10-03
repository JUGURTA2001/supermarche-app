package com.supermarche.frontend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BonAchat {

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
    private List<DetailLigne> details;

    public BonAchat() {}

    // ===== Getters / Setters =====
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getBonCommandeId() { return bonCommandeId; }
    public void setBonCommandeId(Integer bonCommandeId) { this.bonCommandeId = bonCommandeId; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public BigDecimal getVersement() { return versement; }
    public void setVersement(BigDecimal versement) { this.versement = versement; }

    public Integer getNbArticles() { return nbArticles; }
    public void setNbArticles(Integer nbArticles) { this.nbArticles = nbArticles; }

    public Boolean getEstRegle() { return estRegle; }
    public void setEstRegle(Boolean estRegle) { this.estRegle = estRegle; }

    public Integer getFournisseurId() { return fournisseurId; }
    public void setFournisseurId(Integer fournisseurId) { this.fournisseurId = fournisseurId; }

    public String getNomFournisseur() { return nomFournisseur; }
    public void setNomFournisseur(String nomFournisseur) { this.nomFournisseur = nomFournisseur; }

    public LocalDateTime getDateBon() { return dateBon; }
    public void setDateBon(LocalDateTime dateBon) { this.dateBon = dateBon; }

    public BigDecimal getReste() { return reste; }
    public void setReste(BigDecimal reste) { this.reste = reste; }

    public List<DetailLigne> getDetails() { return details; }
    public void setDetails(List<DetailLigne> details) { this.details = details; }

    // ============================================================
    // CLASSE INTERNE : DetailLigne
    // ============================================================
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DetailLigne {
        private Integer produitId;
        private String nomProduit;
        private Integer quantite;
        private BigDecimal prixAchat;

        public DetailLigne() {}

        public Integer getProduitId() { return produitId; }
        public void setProduitId(Integer produitId) { this.produitId = produitId; }

        public String getNomProduit() { return nomProduit; }
        public void setNomProduit(String nomProduit) { this.nomProduit = nomProduit; }

        public Integer getQuantite() { return quantite; }
        public void setQuantite(Integer quantite) { this.quantite = quantite; }

        public BigDecimal getPrixAchat() { return prixAchat; }
        public void setPrixAchat(BigDecimal prixAchat) { this.prixAchat = prixAchat; }
    }
}