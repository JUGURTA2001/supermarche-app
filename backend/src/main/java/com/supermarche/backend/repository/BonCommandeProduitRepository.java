package com.supermarche.backend.repository;

import com.supermarche.backend.model.BonCommandeProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BonCommandeProduitRepository extends JpaRepository<BonCommandeProduit, Integer> {

    List<BonCommandeProduit> findByBonId(Integer bonId);

    @Modifying
    @Query("DELETE FROM BonCommandeProduit b WHERE b.bonId = :bonId")
    void deleteByBonId(@Param("bonId") Integer bonId);
}