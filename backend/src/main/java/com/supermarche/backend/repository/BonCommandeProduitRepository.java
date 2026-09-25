// BonCommandeProduitRepository.java
package com.supermarche.backend.repository;

import com.supermarche.backend.model.BonCommandeProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BonCommandeProduitRepository extends JpaRepository<BonCommandeProduit, Integer> {
    List<BonCommandeProduit> findByBonId(Integer bonId);
    void deleteByBonId(Integer bonId);
}