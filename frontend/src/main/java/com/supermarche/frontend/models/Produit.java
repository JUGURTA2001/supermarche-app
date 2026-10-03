package com.supermarche.frontend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Produit {

    // ===== CHAMPS =====
    private Integer id;
    private String nom;
    private String marque;
    private String codeProduit;
    private String designation;
    private String famille;
    private String rayonnage;
    private String unite;
    private Integer qteInitiale;
    private Integer qteAlerte;
    private BigDecimal prixAchat;
    private BigDecimal prixDetail;
    private BigDecimal prixGros;
    private BigDecimal tva;
    private LocalDate datePeremption;
    private Integer joursAlerte;

    // ⭐ UN SEUL champ photo (ex: "images/uuid.png")
    private String photo;

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private String statut;

    public Produit() {}

    // ===== GETTERS / SETTERS =====
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }

    public String getCodeProduit() { return codeProduit; }
    public void setCodeProduit(String codeProduit) { this.codeProduit = codeProduit; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getFamille() { return famille; }
    public void setFamille(String famille) { this.famille = famille; }

    public String getRayonnage() { return rayonnage; }
    public void setRayonnage(String rayonnage) { this.rayonnage = rayonnage; }

    public String getUnite() { return unite; }
    public void setUnite(String unite) { this.unite = unite; }

    public Integer getQteInitiale() { return qteInitiale; }
    public void setQteInitiale(Integer qteInitiale) { this.qteInitiale = qteInitiale; }

    public Integer getQteAlerte() { return qteAlerte; }
    public void setQteAlerte(Integer qteAlerte) { this.qteAlerte = qteAlerte; }

    public BigDecimal getPrixAchat() { return prixAchat; }
    public void setPrixAchat(BigDecimal prixAchat) { this.prixAchat = prixAchat; }

    public BigDecimal getPrixDetail() { return prixDetail; }
    public void setPrixDetail(BigDecimal prixDetail) { this.prixDetail = prixDetail; }

    public BigDecimal getPrixGros() { return prixGros; }
    public void setPrixGros(BigDecimal prixGros) { this.prixGros = prixGros; }

    public BigDecimal getTva() { return tva; }
    public void setTva(BigDecimal tva) { this.tva = tva; }

    public LocalDate getDatePeremption() { return datePeremption; }
    public void setDatePeremption(LocalDate datePeremption) { this.datePeremption = datePeremption; }

    public Integer getJoursAlerte() { return joursAlerte; }
    public void setJoursAlerte(Integer joursAlerte) { this.joursAlerte = joursAlerte; }

    // ⭐ UN SEUL getter/setter pour photo
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}