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
    // CRÉATION DU BON DE COMMANDE
    // ============================================================
    @Transactional
    public BonCommande creerBonCommande(BonCommandeDTO dto) {

        if (dto.getFournisseurId() == null)
            throw new RuntimeException("Fournisseur obligatoire.");
        if (dto.getDateBon() == null)
            throw new RuntimeException("Date obligatoire.");
        if (dto.getLignes() == null || dto.getLignes().isEmpty())
            throw new RuntimeException("Ajoutez au moins un produit.");

        fournisseurRepository.findById(dto.getFournisseurId())
                .orElseThrow(() -> new RuntimeException("Fournisseur introuvable."));

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

        if (reste.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("💰 Reste = 0 → création automatique du bon d'achat...");
            creerBonAchatDepuisLignes(savedBon, total, versement, nbArticles, lignesSauvees);
        } else {
            System.out.println("⏳ Reste = " + reste + " → pas de bon d'achat.");
        }

        return savedBon;
    }

    // ============================================================
    // CONVERSION MANUELLE
    // ============================================================
    @Transactional
    public BonAchat convertirEnBonAchat(Integer bonCommandeId, BigDecimal versementSupplementaire) {

        BonCommande bon = bonCommandeRepository.findById(bonCommandeId)
                .orElseThrow(() -> new RuntimeException("Bon de commande introuvable : " + bonCommandeId));

        if (bon.getEstConverti() != null && bon.getEstConverti() == 1) {
            throw new RuntimeException("Ce bon a déjà été converti.");
        }

        BigDecimal versementInitial = bon.getVersement() == null ? BigDecimal.ZERO : bon.getVersement();
        BigDecimal supplement = versementSupplementaire == null ? BigDecimal.ZERO : versementSupplementaire;

        if (supplement.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Le versement ne peut pas être négatif.");
        }

        BigDecimal totalVerse = versementInitial.add(supplement);

        if (totalVerse.compareTo(bon.getTotal()) > 0) {
            throw new RuntimeException("Le versement dépasse le total du bon ("
                    + bon.getTotal() + " DA).");
        }

        BigDecimal reste = bon.getTotal().subtract(totalVerse);
        boolean estRegle = reste.compareTo(BigDecimal.ZERO) == 0;

        BonAchat achat = new BonAchat();
        achat.setBonCommandeId(bon.getId());
        achat.setTotal(bon.getTotal());
        achat.setVersement(totalVerse);
        achat.setReste(reste);
        achat.setNbArticles(bon.getNbArticles());
        achat.setEstRegle(estRegle);
        achat.setFournisseurId(bon.getFournisseurId());
        achat.setDateBon(LocalDateTime.now());

        BonAchat savedAchat = bonAchatRepository.save(achat);

        List<BonCommandeProduit> lignes = bonCommandeProduitRepository.findByBonId(bon.getId());

        for (BonCommandeProduit ligne : lignes) {
            DetailBonAchat detail = new DetailBonAchat();
            detail.setBonAchatId(savedAchat.getId());
            detail.setProduitId(ligne.getProduitId());
            detail.setQuantite(ligne.getQuantite());
            detail.setPrixAchat(ligne.getPrixAchat());
            detailBonAchatRepository.save(detail);

            Produit produit = produitRepository.findById(ligne.getProduitId()).orElse(null);
            if (produit != null) {
                int stock = produit.getQteInitiale() == null ? 0 : produit.getQteInitiale();
                produit.setQteInitiale(stock + ligne.getQuantite());
                produit.setPrixAchat(ligne.getPrixAchat());
                produit.setPrixGros(ligne.getPrixGros());
                produit.setPrixDetail(ligne.getPrixDetail());
                produit.setTva(ligne.getTva());
                produitRepository.save(produit);
            }
        }

        bon.setEstConverti(1);
        bon.setStatut(estRegle ? "CONVERTI" : "CONVERTI_CREDIT");
        bon.setDateConversion(LocalDateTime.now());
        bon.setBonAchatId(savedAchat.getId());
        bon.setVersement(totalVerse);
        bon.setReste(reste);
        bon.setEstRegle(estRegle);
        bonCommandeRepository.save(bon);

        System.out.println("✅ Bon " + bon.getId() + " → Bon d'achat " + savedAchat.getId()
                + " | Total = " + bon.getTotal()
                + " | Versé = " + totalVerse
                + " | Reste = " + reste
                + " | " + (estRegle ? "RÉGLÉ" : "À CRÉDIT"));

        return savedAchat;
    }

    // ============================================================
    // AJOUTER UN VERSEMENT
    // ============================================================
    @Transactional
    public BonAchat ajouterVersementBonAchat(Integer bonAchatId, BigDecimal versement) {

        if (versement == null || versement.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Le versement doit être > 0.");
        }

        BonAchat achat = bonAchatRepository.findById(bonAchatId)
                .orElseThrow(() -> new RuntimeException("Bon d'achat introuvable : " + bonAchatId));

        if (achat.getEstRegle() != null && achat.getEstRegle()) {
            throw new RuntimeException("Ce bon d'achat est déjà entièrement réglé.");
        }

        BigDecimal nouveauVersement = achat.getVersement().add(versement);
        if (nouveauVersement.compareTo(achat.getTotal()) > 0) {
            throw new RuntimeException("Le versement dépasse le total ("
                    + achat.getTotal() + " DA). Reste à payer : " + achat.getReste() + " DA.");
        }

        BigDecimal nouveauReste = achat.getTotal().subtract(nouveauVersement);
        boolean estRegle = nouveauReste.compareTo(BigDecimal.ZERO) == 0;

        achat.setVersement(nouveauVersement);
        achat.setReste(nouveauReste);
        achat.setEstRegle(estRegle);
        bonAchatRepository.save(achat);

        bonCommandeRepository.findById(achat.getBonCommandeId()).ifPresent(bon -> {
            bon.setVersement(nouveauVersement);
            bon.setReste(nouveauReste);
            bon.setEstRegle(estRegle);
            bon.setStatut(estRegle ? "CONVERTI" : "CONVERTI_CREDIT");
            bonCommandeRepository.save(bon);
        });

        System.out.println("💰 Bon d'achat " + bonAchatId
                + " | +" + versement + " DA"
                + " | Total versé = " + nouveauVersement
                + " | Reste = " + nouveauReste
                + " | " + (estRegle ? "✅ RÉGLÉ" : "💳 À CRÉDIT"));

        return achat;
    }

    // ============================================================
    // RÉCUPÉRER UN BON PAR ID
    // ============================================================
    public BonCommandeDTO getById(Integer id) {
        return bonCommandeRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    // ============================================================
    // MODIFIER UN BON DE COMMANDE
    // ============================================================
    // ============================================================
// MODIFIER UN BON DE COMMANDE
// ============================================================
    @Transactional
    public BonCommande modifierBonCommande(Integer bonCommandeId, BonCommandeDTO dto) {

        BonCommande bon = bonCommandeRepository.findById(bonCommandeId)
                .orElseThrow(() -> new RuntimeException("Bon introuvable : " + bonCommandeId));

        if (bon.getEstConverti() != null && bon.getEstConverti() == 1) {
            throw new RuntimeException("Ce bon a déjà été converti. Impossible de le modifier.");
        }

        if (dto.getFournisseurId() == null)
            throw new RuntimeException("Fournisseur obligatoire.");
        if (dto.getLignes() == null || dto.getLignes().isEmpty())
            throw new RuntimeException("Ajoutez au moins un produit.");

        fournisseurRepository.findById(dto.getFournisseurId())
                .orElseThrow(() -> new RuntimeException("Fournisseur introuvable."));

        // Recalculer le total
        BigDecimal total = BigDecimal.ZERO;
        int nbArticles = 0;
        for (BonCommandeDTO.LigneBonDTO l : dto.getLignes()) {
            if (l.getProduitId() == null)
                throw new RuntimeException("Produit manquant.");
            if (l.getQuantite() == null || l.getQuantite() <= 0)
                throw new RuntimeException("Quantité > 0 requise.");
            if (l.getPrixAchat() == null || l.getPrixAchat().compareTo(BigDecimal.ZERO) <= 0)
                throw new RuntimeException("Prix d'achat > 0 requis.");

            total = total.add(l.getPrixAchat().multiply(BigDecimal.valueOf(l.getQuantite())));
            nbArticles += l.getQuantite();
        }

        BigDecimal versement = dto.getVersement() == null ? BigDecimal.ZERO : dto.getVersement();
        if (versement.compareTo(BigDecimal.ZERO) < 0)
            throw new RuntimeException("Le versement ne peut pas être négatif.");
        if (versement.compareTo(total) > 0)
            throw new RuntimeException("Le versement ne peut pas dépasser le total.");

        BigDecimal reste = total.subtract(versement);

        // Mise à jour de l'entête
        bon.setNumero(dto.getNumero());
        bon.setFournisseurId(dto.getFournisseurId());
        bon.setDateBon(dto.getDateBon());
        bon.setTotal(total);
        bon.setVersement(versement);
        bon.setReste(reste);
        bon.setNbArticles(nbArticles);
        bon.setEstRegle(reste.compareTo(BigDecimal.ZERO) == 0);
        bonCommandeRepository.save(bon);

        // Supprimer les anciennes lignes
        bonCommandeProduitRepository.deleteByBonId(bon.getId());

        // Recréer les nouvelles lignes
        List<BonCommandeProduit> lignesSauvees = new ArrayList<>();
        for (BonCommandeDTO.LigneBonDTO l : dto.getLignes()) {
            BonCommandeProduit bcp = new BonCommandeProduit();
            bcp.setBonId(bon.getId());
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
        // ⭐ SI RESTE == 0 → CRÉER AUTOMATIQUEMENT LE BON D'ACHAT
        // ============================================================
        if (reste.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("💰 [MODIF] Reste = 0 → création automatique du bon d'achat...");
            creerBonAchatDepuisLignes(bon, total, versement, nbArticles, lignesSauvees);
        } else {
            System.out.println("⏳ [MODIF] Reste = " + reste + " → pas de bon d'achat.");
        }

        System.out.println("✅ Bon " + bon.getId() + " modifié | Total = " + total
                + " | Versé = " + versement + " | Reste = " + reste);

        return bon;
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
    // SUPPRESSION
    // ============================================================
    @Transactional
    public void delete(Integer id) {
        bonCommandeProduitRepository.deleteByBonId(id);
        bonCommandeRepository.deleteById(id);
    }

    // ============================================================
    // UTILITAIRE PRIVÉ : créer bon d'achat + détails + maj produits
    // ============================================================
    private BonAchat creerBonAchatDepuisLignes(BonCommande bon,
                                               BigDecimal total,
                                               BigDecimal versement,
                                               int nbArticles,
                                               List<BonCommandeProduit> lignes) {

        BonAchat achat = new BonAchat();
        achat.setBonCommandeId(bon.getId());
        achat.setTotal(total);
        achat.setVersement(versement);
        achat.setReste(BigDecimal.ZERO);
        achat.setNbArticles(nbArticles);
        achat.setEstRegle(true);
        achat.setFournisseurId(bon.getFournisseurId());
        achat.setDateBon(LocalDateTime.now());

        BonAchat savedAchat = bonAchatRepository.save(achat);

        for (BonCommandeProduit bcp : lignes) {
            DetailBonAchat detail = new DetailBonAchat();
            detail.setBonAchatId(savedAchat.getId());
            detail.setProduitId(bcp.getProduitId());
            detail.setQuantite(bcp.getQuantite());
            detail.setPrixAchat(bcp.getPrixAchat());
            detailBonAchatRepository.save(detail);

            Produit produit = produitRepository.findById(bcp.getProduitId())
                    .orElseThrow(() -> new RuntimeException("Produit introuvable : " + bcp.getProduitId()));

            int stock = produit.getQteInitiale() == null ? 0 : produit.getQteInitiale();
            produit.setQteInitiale(stock + bcp.getQuantite());
            produit.setPrixAchat(bcp.getPrixAchat());
            produit.setPrixGros(bcp.getPrixGros());
            produit.setPrixDetail(bcp.getPrixDetail());
            produit.setTva(bcp.getTva());
            produitRepository.save(produit);
        }

        bon.setEstConverti(1);
        bon.setStatut("CONVERTI");
        bon.setDateConversion(LocalDateTime.now());
        bon.setBonAchatId(savedAchat.getId());
        bonCommandeRepository.save(bon);

        System.out.println("✅ Bon d'achat créé automatiquement : id=" + savedAchat.getId());
        return savedAchat;
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