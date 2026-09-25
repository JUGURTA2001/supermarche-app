package com.supermarche.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "bon_commandes_produits")
public class BonCommandeProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "bon_id", nullable = false)
    private Integer bonId;

    @Column(name = "produit_id", nullable = false)
    private Integer produitId;

    @Column(nullable = false)
    private Integer quantite;

    @Column(name = "prix_achat", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixAchat;

    @Column(name = "prix_gros", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixGros;

    @Column(name = "prix_detail", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixDetail;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal tva;

    @Column(name = "est_recu")
    private Boolean estRecu = false;
}