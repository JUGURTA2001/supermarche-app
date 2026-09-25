// BonAchatRepository.java
package com.supermarche.backend.repository;

import com.supermarche.backend.model.BonAchat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BonAchatRepository extends JpaRepository<BonAchat, Integer> {
    List<BonAchat> findByBonCommandeId(Integer bonCommandeId);
    List<BonAchat> findByFournisseurId(Integer fournisseurId);
}