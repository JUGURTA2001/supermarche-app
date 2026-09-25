package com.supermarche.frontend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BonCommande {

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
    private List<LigneBon> lignes;

    public BonCommande() {}

    // Getters / Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getFournisseurId() { return fournisseurId; }
    public void setFournisseurId(Integer fournisseurId) { this.fournisseurId = fournisseurId; }

    public String getNomFournisseur() { return nomFournisseur; }
    public void setNomFournisseur(String nomFournisseur) { this.nomFournisseur = nomFournisseur; }

    public LocalDate getDateBon() { return dateBon; }
    public void setDateBon(LocalDate dateBon) { this.dateBon = dateBon; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public BigDecimal getVersement() { return versement; }
    public void setVersement(BigDecimal versement) { this.versement = versement; }

    public BigDecimal getReste() { return reste; }
    public void setReste(BigDecimal reste) { this.reste = reste; }

    public Integer getNbArticles() { return nbArticles; }
    public void setNbArticles(Integer nbArticles) { this.nbArticles = nbArticles; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Boolean getEstConverti() { return estConverti; }
    public void setEstConverti(Boolean estConverti) { this.estConverti = estConverti; }

    public List<LigneBon> getLignes() { return lignes; }
    public void setLignes(List<LigneBon> lignes) { this.lignes = lignes; }

    // ============ Classe interne LigneBon ============
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LigneBon {
        private Integer produitId;
        private String nomProduit;
        private Integer quantite;
        private BigDecimal prixAchat;
        private BigDecimal prixGros;
        private BigDecimal prixDetail;
        private BigDecimal tva;

        public LigneBon() {}

        public Integer getProduitId() { return produitId; }
        public void setProduitId(Integer produitId) { this.produitId = produitId; }

        public String getNomProduit() { return nomProduit; }
        public void setNomProduit(String nomProduit) { this.nomProduit = nomProduit; }

        public Integer getQuantite() { return quantite; }
        public void setQuantite(Integer quantite) { this.quantite = quantite; }

        public BigDecimal getPrixAchat() { return prixAchat; }
        public void setPrixAchat(BigDecimal prixAchat) { this.prixAchat = prixAchat; }

        public BigDecimal getPrixGros() { return prixGros; }
        public void setPrixGros(BigDecimal prixGros) { this.prixGros = prixGros; }

        public BigDecimal getPrixDetail() { return prixDetail; }
        public void setPrixDetail(BigDecimal prixDetail) { this.prixDetail = prixDetail; }

        public BigDecimal getTva() { return tva; }
        public void setTva(BigDecimal tva) { this.tva = tva; }
    }
}