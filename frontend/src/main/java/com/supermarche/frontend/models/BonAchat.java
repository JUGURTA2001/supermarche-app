package com.supermarche.frontend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BonAchat {

    private Integer id;
    private Integer bonCommandeId;
    private BigDecimal total;
    private BigDecimal versement;
    private Integer nbArticles;
    private Boolean estRegle;
    private Integer fournisseurId;
    private LocalDateTime dateBon;
    private BigDecimal reste;

    public BonAchat() {}

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

    public LocalDateTime getDateBon() { return dateBon; }
    public void setDateBon(LocalDateTime dateBon) { this.dateBon = dateBon; }

    public BigDecimal getReste() { return reste; }
    public void setReste(BigDecimal reste) { this.reste = reste; }
}