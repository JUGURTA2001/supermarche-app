// BonCommandeRepository.java
package com.supermarche.backend.repository;

import com.supermarche.backend.model.BonCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BonCommandeRepository extends JpaRepository<BonCommande, Integer> {
    List<BonCommande> findByStatut(String statut);
    List<BonCommande> findByFournisseurId(Integer fournisseurId);
    List<BonCommande> findByEstConverti(Integer estConverti);


}