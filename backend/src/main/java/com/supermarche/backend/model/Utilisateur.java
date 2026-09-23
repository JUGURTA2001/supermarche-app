// model/Utilisateur.java
package com.supermarche.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 10)
    private String telephone;

    @Column(length = 255)
    private String adresse;

    @Column(length = 100)
    private String wilaya;

    @Column(length = 100)
    private String commune;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "numero_registre_commerce", length = 100)
    private String numeroRegistreCommerce;

    @Column(name = "matricule_fiscal", length = 100)
    private String matriculeFiscal;

    @Column(name = "numero_securite_sociale", length = 100)
    private String numeroSecuriteSociale;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean actif = true;
}