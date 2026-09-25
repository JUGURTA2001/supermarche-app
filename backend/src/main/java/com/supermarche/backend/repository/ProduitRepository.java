package com.supermarche.backend.repository;

import com.supermarche.backend.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Integer> {

    /** Recherche par nom (contient, insensible à la casse). */
    List<Produit> findByNomContainingIgnoreCase(String nom);

    /** Recherche par code produit (unique). */
    Produit findByCodeProduit(String codeProduit);

    /** Recherche par statut. */
    List<Produit> findByStatut(Produit.Statut statut);

    /** Recherche par catégorie. */
    List<Produit> findByCategorieId(Integer categorieId);
}