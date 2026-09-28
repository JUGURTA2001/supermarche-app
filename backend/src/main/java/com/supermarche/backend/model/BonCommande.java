package com.supermarche.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bon_commandes")
public class BonCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    private String numero;

    @Column(name = "fournisseur_id", nullable = false)
    private Integer fournisseurId;

    @Column(name = "date_bon", nullable = false)
    private LocalDate dateBon;

    @Column(precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal versement = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal reste = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal regle = BigDecimal.ZERO;

    @Column(name = "nb_articles")
    private Integer nbArticles = 0;

    @Column(name = "est_regle")
    private Boolean estRegle = false;

    @Column(name = "est_converti")
    private Integer estConverti = 0;

    @Column(length = 50)
    private String statut = "EN_ATTENTE";

    @Column(name = "date_conversion")
    private LocalDateTime dateConversion;

    @Column(name = "bon_achat_id")
    private Integer bonAchatId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}