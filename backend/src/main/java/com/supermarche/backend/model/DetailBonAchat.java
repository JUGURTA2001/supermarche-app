package com.supermarche.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "details_bon_achat")
public class DetailBonAchat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "bon_achat_id", nullable = false)
    private Integer bonAchatId;

    @Column(name = "produit_id", nullable = false)
    private Integer produitId;

    @Column(nullable = false)
    private Integer quantite;

    @Column(name = "prix_achat", precision = 10, scale = 2)
    private BigDecimal prixAchat;
}