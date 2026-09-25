package com.supermarche.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bons_achat")
public class BonAchat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "bon_commande_id", nullable = false)
    private Integer bonCommandeId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal versement = BigDecimal.ZERO;

    @Column(name = "nb_articles", nullable = false)
    private Integer nbArticles = 0;

    @Column(name = "est_regle", nullable = false)
    private Boolean estRegle = false;

    @Column(name = "fournisseur_id")
    private Integer fournisseurId;

    @Column(name = "date_bon")
    private LocalDateTime dateBon;

    @Column(precision = 10, scale = 2)
    private BigDecimal reste = BigDecimal.ZERO;
}