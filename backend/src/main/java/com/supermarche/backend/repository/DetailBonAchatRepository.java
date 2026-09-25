package com.supermarche.backend.repository;

import com.supermarche.backend.model.DetailBonAchat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailBonAchatRepository extends JpaRepository<DetailBonAchat, Integer> {

    List<DetailBonAchat> findByBonAchatId(Integer bonAchatId);
}