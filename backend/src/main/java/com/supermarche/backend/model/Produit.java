package com.supermarche.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "produits")
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(length = 100)
    private String marque;

    @Column(name = "code_produit", length = 50, unique = true)
    private String codeProduit;

    @Column(columnDefinition = "TEXT")
    private String designation;

    @Column(length = 100)
    private String famille;

    @Column(length = 50)
    private String rayonnage;

    @Column(length = 50)
    private String unite;

    @Column(name = "qte_initiale")
    private Integer qteInitiale = 0;

    @Column(name = "qte_alerte")
    private Integer qteAlerte = 5;

    @Column(name = "prix_achat", precision = 10, scale = 2)
    private BigDecimal prixAchat;

    @Column(name = "prix_detail", precision = 10, scale = 2)
    private BigDecimal prixDetail;

    @Column(name = "prix_gros", precision = 10, scale = 2)
    private BigDecimal prixGros;

    @Column(precision = 5, scale = 2)
    private BigDecimal tva = new BigDecimal("20.00");

    @Column(name = "date_peremption")
    private LocalDate datePeremption;

    @Column(name = "jours_alerte")
    private Integer joursAlerte = 30;

    /** Chemin relatif vers l'image, ex: "images/uuid.png" */
    @Column(length = 255)
    private String photo;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "prix_neveau", precision = 10, scale = 2)
    private BigDecimal prixNeveau;

    @Column(name = "bon_id")
    private Integer bonId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('en_stock','en_commande','commande_confirmee')")
    private Statut statut = Statut.en_stock;

    @Column(name = "bon_commande_id")
    private Integer bonCommandeId;

    @Column(name = "categorie_id")
    private Integer categorieId;

    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    public enum Statut {
        en_stock, en_commande, commande_confirmee
    }
}