package com.supermarche.frontend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Produit {
    private Long id;
    private String nom;
    private String codeBarre;
    private Double prixAchat;
    private Double prixVente;
    private Integer quantiteStock;
    private Integer seuilAlerte = 10; // Valeur par défaut

    public Produit() {}

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCodeBarre() { return codeBarre; }
    public void setCodeBarre(String codeBarre) { this.codeBarre = codeBarre; }

    public Double getPrixAchat() { return prixAchat; }
    public void setPrixAchat(Double prixAchat) { this.prixAchat = prixAchat; }

    public Double getPrixVente() { return prixVente; }
    public void setPrixVente(Double prixVente) { this.prixVente = prixVente; }

    public Integer getQuantiteStock() { return quantiteStock; }
    public void setQuantiteStock(Integer quantiteStock) { this.quantiteStock = quantiteStock; }

    public Integer getSeuilAlerte() { return seuilAlerte; }
    public void setSeuilAlerte(Integer seuilAlerte) { this.seuilAlerte = seuilAlerte; }
}