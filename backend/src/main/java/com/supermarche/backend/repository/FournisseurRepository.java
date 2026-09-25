// FournisseurRepository.java
package com.supermarche.backend.repository;

import com.supermarche.backend.model.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, Integer> {
    boolean existsByNomSociete(String nomSociete);
    List<Fournisseur> findByNomSocieteContainingIgnoreCase(String nomSociete);
}