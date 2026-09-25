package com.supermarche.backend.service;

import com.supermarche.backend.dto.BonCommandeDTO;
import com.supermarche.backend.model.*;
import com.supermarche.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BonCommandeService {

    @Autowired private BonCommandeRepository bonCommandeRepository;
    @Autowired private BonCommandeProduitRepository bonCommandeProduitRepository;
    @Autowired private BonAchatRepository bonAchatRepository;
    @Autowired private DetailBonAchatRepository detailBonAchatRepository;
    @Autowired private FournisseurRepository fournisseurRepository;
    @Autowired private ProduitRepository produitRepository;

    /**
     * Créer un bon de commande avec ses lignes.
     */
    @Transactional
    public BonCommande creerBonCommande(BonCommandeDTO dto) {
        // 1. Sauvegarder l'entête du bon
        BonCommande bon = new BonCommande();
        bon.setFournisseurId(dto.getFournisseurId());
        bon.setDateBon(dto.getDateBon());
        bon.setVersement(dto.getVersement() == null ? BigDecimal.ZERO : dto.getVersement());
        bon.setStatut("EN_ATTENTE");
        bon.setEstConverti(0);

        // Calculer le total depuis les lignes
        BigDecimal total = BigDecimal.ZERO;
        int nbArticles = 0;
        if (dto.getLignes() != null) {
            for (BonCommandeDTO.LigneBonDTO ligne : dto.getLignes()) {
                BigDecimal sousTotal = ligne.getPrixAchat()
                        .multiply(BigDecimal.valueOf(ligne.getQuantite()));
                total = total.add(sousTotal);
                nbArticles += ligne.getQuantite();
            }
        }
        bon.setTotal(total);
        bon.setNbArticles(nbArticles);
        bon.setReste(total.subtract(bon.getVersement()));

        BonCommande saved = bonCommandeRepository.save(bon);

        // 2. Sauvegarder les lignes
        if (dto.getLignes() != null) {
            for (BonCommandeDTO.LigneBonDTO ligne : dto.getLignes()) {
                BonCommandeProduit bcp = new BonCommandeProduit();
                bcp.setBonId(saved.getId());
                bcp.setProduitId(ligne.getProduitId());
                bcp.setQuantite(ligne.getQuantite());
                bcp.setPrixAchat(ligne.getPrixAchat());
                bcp.setPrixGros(ligne.getPrixGros() != null ? ligne.getPrixGros() : ligne.getPrixAchat());
                bcp.setPrixDetail(ligne.getPrixDetail() != null ? ligne.getPrixDetail() : ligne.getPrixAchat());
                bcp.setTva(ligne.getTva() != null ? ligne.getTva() : new BigDecimal("20.00"));
                bcp.setEstRecu(false);
                bonCommandeProduitRepository.save(bcp);
            }
        }

        return saved;
    }

    /**
     * Récupérer tous les bons avec infos fournisseur et lignes.
     */
    public List<BonCommandeDTO> getAll() {
        List<BonCommandeDTO> result = new ArrayList<>();
        for (BonCommande bon : bonCommandeRepository.findAll()) {
            result.add(toDTO(bon));
        }
        return result;
    }

    /**
     * Convertir un bon de commande en bon d'achat.
     */
    @Transactional
    public BonAchat convertirEnBonAchat(Integer bonCommandeId, BigDecimal versement) {
        BonCommande bon = bonCommandeRepository.findById(bonCommandeId)
                .orElseThrow(() -> new RuntimeException("Bon de commande introuvable : " + bonCommandeId));

        if (bon.getEstConverti() != null && bon.getEstConverti() == 1) {
            throw new RuntimeException("Ce bon a déjà été converti.");
        }

        // 1. Créer le bon d'achat
        BonAchat achat = new BonAchat();
        achat.setBonCommandeId(bon.getId());
        achat.setTotal(bon.getTotal());
        achat.setVersement(versement == null ? bon.getVersement() : versement);
        achat.setNbArticles(bon.getNbArticles());
        achat.setFournisseurId(bon.getFournisseurId());
        achat.setDateBon(LocalDateTime.now());
        achat.setReste(bon.getTotal().subtract(achat.getVersement()));
        achat.setEstRegle(achat.getReste().compareTo(BigDecimal.ZERO) == 0);

        BonAchat savedAchat = bonAchatRepository.save(achat);

        // 2. Créer les détails
        List<BonCommandeProduit> lignes = bonCommandeProduitRepository.findByBonId(bon.getId());
        for (BonCommandeProduit ligne : lignes) {
            DetailBonAchat detail = new DetailBonAchat();
            detail.setBonAchatId(savedAchat.getId());
            detail.setProduitId(ligne.getProduitId());
            detail.setQuantite(ligne.getQuantite());
            detail.setPrixAchat(ligne.getPrixAchat());
            detailBonAchatRepository.save(detail);

            // 3. Mettre à jour le stock du produit
            Produit produit = produitRepository.findById(ligne.getProduitId()).orElse(null);
            if (produit != null) {
                int stock = produit.getQteInitiale() == null ? 0 : produit.getQteInitiale();
                produit.setQteInitiale(stock + ligne.getQuantite());
                produit.setPrixAchat(ligne.getPrixAchat());
                produit.setPrixGros(ligne.getPrixGros());
                produit.setPrixDetail(ligne.getPrixDetail());
                produitRepository.save(produit);
            }
        }

        // 4. Marquer le bon de commande comme converti
        bon.setEstConverti(1);
        bon.setStatut("CONVERTI");
        bon.setDateConversion(LocalDateTime.now());
        bon.setBonAchatId(savedAchat.getId());
        bonCommandeRepository.save(bon);

        return savedAchat;
    }

    private BonCommandeDTO toDTO(BonCommande bon) {
        BonCommandeDTO dto = new BonCommandeDTO();
        dto.setId(bon.getId());
        dto.setFournisseurId(bon.getFournisseurId());
        dto.setDateBon(bon.getDateBon());
        dto.setTotal(bon.getTotal());
        dto.setVersement(bon.getVersement());
        dto.setReste(bon.getReste());
        dto.setNbArticles(bon.getNbArticles());
        dto.setStatut(bon.getStatut());
        dto.setEstConverti(bon.getEstConverti() != null && bon.getEstConverti() == 1);

        // Nom du fournisseur
        fournisseurRepository.findById(bon.getFournisseurId())
                .ifPresent(f -> dto.setNomFournisseur(f.getNomSociete()));

        // Lignes
        List<BonCommandeDTO.LigneBonDTO> lignesDTO = new ArrayList<>();
        for (BonCommandeProduit ligne : bonCommandeProduitRepository.findByBonId(bon.getId())) {
            BonCommandeDTO.LigneBonDTO l = new BonCommandeDTO.LigneBonDTO();
            l.setProduitId(ligne.getProduitId());
            l.setQuantite(ligne.getQuantite());
            l.setPrixAchat(ligne.getPrixAchat());
            l.setPrixGros(ligne.getPrixGros());
            l.setPrixDetail(ligne.getPrixDetail());
            l.setTva(ligne.getTva());
            produitRepository.findById(ligne.getProduitId())
                    .ifPresent(p -> l.setNomProduit(p.getNom()));
            lignesDTO.add(l);
        }
        dto.setLignes(lignesDTO);

        return dto;
    }

    public void delete(Integer id) {
        bonCommandeProduitRepository.deleteByBonId(id);
        bonCommandeRepository.deleteById(id);
    }
}