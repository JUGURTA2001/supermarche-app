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

    // ============================================================
    // ⭐ CRÉATION DU BON DE COMMANDE
    //    + création auto du bon d'achat si reste == 0
    //    + mise à jour du stock/prix des produits
    // ============================================================
    @Transactional
    public BonCommande creerBonCommande(BonCommandeDTO dto) {

        // ---------- 1. Validation ----------
        if (dto.getFournisseurId() == null)
            throw new RuntimeException("Fournisseur obligatoire.");
        if (dto.getDateBon() == null)
            throw new RuntimeException("Date obligatoire.");
        if (dto.getLignes() == null || dto.getLignes().isEmpty())
            throw new RuntimeException("Ajoutez au moins un produit.");

        fournisseurRepository.findById(dto.getFournisseurId())
                .orElseThrow(() -> new RuntimeException("Fournisseur introuvable."));

        // ---------- 2. Calcul du total + nb articles ----------
        BigDecimal total = BigDecimal.ZERO;
        int nbArticles = 0;
        for (BonCommandeDTO.LigneBonDTO l : dto.getLignes()) {
            if (l.getProduitId() == null)
                throw new RuntimeException("Produit manquant dans une ligne.");
            if (l.getQuantite() == null || l.getQuantite() <= 0)
                throw new RuntimeException("Quantité > 0 requise pour le produit " + l.getProduitId());
            if (l.getPrixAchat() == null || l.getPrixAchat().compareTo(BigDecimal.ZERO) <= 0)
                throw new RuntimeException("Prix d'achat > 0 requis pour le produit " + l.getProduitId());

            BigDecimal sousTotal = l.getPrixAchat().multiply(BigDecimal.valueOf(l.getQuantite()));
            total = total.add(sousTotal);
            nbArticles += l.getQuantite();
        }

        BigDecimal versement = dto.getVersement() == null ? BigDecimal.ZERO : dto.getVersement();
        if (versement.compareTo(BigDecimal.ZERO) < 0)
            throw new RuntimeException("Le versement ne peut pas être négatif.");
        if (versement.compareTo(total) > 0)
            throw new RuntimeException("Le versement ne peut pas dépasser le total.");

        BigDecimal reste = total.subtract(versement);

        // ---------- 3. Créer l'entête du bon de commande ----------
        BonCommande bon = new BonCommande();
        bon.setNumero(dto.getNumero());
        bon.setFournisseurId(dto.getFournisseurId());
        bon.setDateBon(dto.getDateBon());
        bon.setTotal(total);
        bon.setVersement(versement);
        bon.setReste(reste);
        bon.setNbArticles(nbArticles);
        bon.setEstRegle(reste.compareTo(BigDecimal.ZERO) == 0);
        bon.setStatut("EN_ATTENTE");
        bon.setEstConverti(0);

        BonCommande savedBon = bonCommandeRepository.save(bon);

        // ---------- 4. Créer les lignes du bon ----------
        List<BonCommandeProduit> lignesSauvees = new ArrayList<>();
        for (BonCommandeDTO.LigneBonDTO l : dto.getLignes()) {
            BonCommandeProduit bcp = new BonCommandeProduit();
            bcp.setBonId(savedBon.getId());
            bcp.setProduitId(l.getProduitId());
            bcp.setQuantite(l.getQuantite());
            bcp.setPrixAchat(l.getPrixAchat());
            bcp.setPrixGros(l.getPrixGros() != null ? l.getPrixGros() : l.getPrixAchat());
            bcp.setPrixDetail(l.getPrixDetail() != null ? l.getPrixDetail() : l.getPrixAchat());
            bcp.setTva(l.getTva() != null ? l.getTva() : new BigDecimal("20.00"));
            bcp.setEstRecu(false);
            bonCommandeProduitRepository.save(bcp);
            lignesSauvees.add(bcp);
        }

        // ============================================================
        // ⭐ SI RESTE == 0 → créer un bon d'achat + mettre à jour les produits
        // ============================================================
        if (reste.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("💰 Reste = 0 → création automatique du bon d'achat...");

            // ---------- 5. Créer le bon d'achat ----------
            BonAchat achat = new BonAchat();
            achat.setBonCommandeId(savedBon.getId());
            achat.setTotal(total);
            achat.setVersement(versement);
            achat.setReste(BigDecimal.ZERO);
            achat.setNbArticles(nbArticles);
            achat.setEstRegle(true);
            achat.setFournisseurId(savedBon.getFournisseurId());
            achat.setDateBon(LocalDateTime.now());

            BonAchat savedAchat = bonAchatRepository.save(achat);

            // ---------- 6. Créer les détails + mise à jour produit ----------
            for (BonCommandeProduit bcp : lignesSauvees) {

                // 6.1 Détail du bon d'achat
                DetailBonAchat detail = new DetailBonAchat();
                detail.setBonAchatId(savedAchat.getId());
                detail.setProduitId(bcp.getProduitId());
                detail.setQuantite(bcp.getQuantite());
                detail.setPrixAchat(bcp.getPrixAchat());
                detailBonAchatRepository.save(detail);

                // 6.2 Mettre à jour le produit (stock + prix)
                Produit produit = produitRepository.findById(bcp.getProduitId())
                        .orElseThrow(() -> new RuntimeException("Produit introuvable : " + bcp.getProduitId()));

                int stock = produit.getQteInitiale() == null ? 0 : produit.getQteInitiale();
                produit.setQteInitiale(stock + bcp.getQuantite());   // ⭐ stock += quantité
                produit.setPrixAchat(bcp.getPrixAchat());            // ⭐ prix achat = nouveau
                produit.setPrixGros(bcp.getPrixGros());              // ⭐ prix gros
                produit.setPrixDetail(bcp.getPrixDetail());          // ⭐ prix détail
                produit.setTva(bcp.getTva());
                produitRepository.save(produit);

                System.out.println("   ✅ Produit " + produit.getNom()
                        + " : stock = " + produit.getQteInitiale()
                        + ", prix achat = " + produit.getPrixAchat());
            }

            // ---------- 7. Marquer le bon comme converti ----------
            savedBon.setEstConverti(1);
            savedBon.setStatut("CONVERTI");
            savedBon.setDateConversion(LocalDateTime.now());
            savedBon.setBonAchatId(savedAchat.getId());
            bonCommandeRepository.save(savedBon);

            System.out.println("✅ Bon d'achat créé : id=" + savedAchat.getId());
        } else {
            System.out.println("⏳ Reste = " + reste + " → pas de bon d'achat.");
        }

        return savedBon;
    }

    // ============================================================
    // LISTE
    // ============================================================
    public List<BonCommandeDTO> getAll() {
        List<BonCommandeDTO> result = new ArrayList<>();
        for (BonCommande bon : bonCommandeRepository.findAll()) {
            result.add(toDTO(bon));
        }
        return result;
    }

    // ============================================================
    // CONVERSION MANUELLE (pour les bons avec reste > 0)
    // ============================================================
    @Transactional
    public BonAchat convertirEnBonAchat(Integer bonCommandeId, BigDecimal versementSupp) {
        BonCommande bon = bonCommandeRepository.findById(bonCommandeId)
                .orElseThrow(() -> new RuntimeException("Bon introuvable : " + bonCommandeId));

        if (bon.getEstConverti() != null && bon.getEstConverti() == 1)
            throw new RuntimeException("Ce bon est déjà converti.");

        BigDecimal nouveauVersement = bon.getVersement()
                .add(versementSupp == null ? BigDecimal.ZERO : versementSupp);

        if (nouveauVersement.compareTo(bon.getTotal()) < 0)
            throw new RuntimeException("Versement insuffisant pour convertir.");

        // Créer le bon d'achat
        BonAchat achat = new BonAchat();
        achat.setBonCommandeId(bon.getId());
        achat.setTotal(bon.getTotal());
        achat.setVersement(nouveauVersement);
        achat.setReste(BigDecimal.ZERO);
        achat.setNbArticles(bon.getNbArticles());
        achat.setEstRegle(true);
        achat.setFournisseurId(bon.getFournisseurId());
        achat.setDateBon(LocalDateTime.now());

        BonAchat savedAchat = bonAchatRepository.save(achat);

        // Détails + update produits
        for (BonCommandeProduit bcp : bonCommandeProduitRepository.findByBonId(bon.getId())) {
            DetailBonAchat d = new DetailBonAchat();
            d.setBonAchatId(savedAchat.getId());
            d.setProduitId(bcp.getProduitId());
            d.setQuantite(bcp.getQuantite());
            d.setPrixAchat(bcp.getPrixAchat());
            detailBonAchatRepository.save(d);

            produitRepository.findById(bcp.getProduitId()).ifPresent(p -> {
                int stock = p.getQteInitiale() == null ? 0 : p.getQteInitiale();
                p.setQteInitiale(stock + bcp.getQuantite());
                p.setPrixAchat(bcp.getPrixAchat());
                p.setPrixGros(bcp.getPrixGros());
                p.setPrixDetail(bcp.getPrixDetail());
                produitRepository.save(p);
            });
        }

        bon.setEstConverti(1);
        bon.setStatut("CONVERTI");
        bon.setDateConversion(LocalDateTime.now());
        bon.setBonAchatId(savedAchat.getId());
        bon.setVersement(nouveauVersement);
        bon.setReste(BigDecimal.ZERO);
        bon.setEstRegle(true);
        bonCommandeRepository.save(bon);

        return savedAchat;
    }

    // ============================================================
    // SUPPRESSION
    // ============================================================
    @Transactional
    public void delete(Integer id) {
        bonCommandeProduitRepository.deleteByBonId(id);
        bonCommandeRepository.deleteById(id);
    }

    // ============================================================
    // UTILITAIRE : ENTITÉ → DTO
    // ============================================================
    private BonCommandeDTO toDTO(BonCommande bon) {
        BonCommandeDTO dto = new BonCommandeDTO();
        dto.setId(bon.getId());
        dto.setNumero(bon.getNumero());
        dto.setFournisseurId(bon.getFournisseurId());
        dto.setDateBon(bon.getDateBon());
        dto.setTotal(bon.getTotal());
        dto.setVersement(bon.getVersement());
        dto.setReste(bon.getReste());
        dto.setNbArticles(bon.getNbArticles());
        dto.setStatut(bon.getStatut());
        dto.setEstConverti(bon.getEstConverti() != null && bon.getEstConverti() == 1);

        fournisseurRepository.findById(bon.getFournisseurId())
                .ifPresent(f -> dto.setNomFournisseur(f.getNomSociete()));

        List<BonCommandeDTO.LigneBonDTO> lignes = new ArrayList<>();
        for (BonCommandeProduit bcp : bonCommandeProduitRepository.findByBonId(bon.getId())) {
            BonCommandeDTO.LigneBonDTO l = new BonCommandeDTO.LigneBonDTO();
            l.setProduitId(bcp.getProduitId());
            l.setQuantite(bcp.getQuantite());
            l.setPrixAchat(bcp.getPrixAchat());
            l.setPrixGros(bcp.getPrixGros());
            l.setPrixDetail(bcp.getPrixDetail());
            l.setTva(bcp.getTva());
            produitRepository.findById(bcp.getProduitId())
                    .ifPresent(p -> l.setNomProduit(p.getNom()));
            lignes.add(l);
        }
        dto.setLignes(lignes);
        return dto;
    }
}