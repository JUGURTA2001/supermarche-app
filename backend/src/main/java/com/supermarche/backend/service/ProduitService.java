package com.supermarche.backend.service;

import com.supermarche.backend.model.Produit;
import com.supermarche.backend.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProduitService {

    @Autowired private ProduitRepository repository;
    @Autowired private FileStorageService fileStorage;

    // ============================================================
    // LECTURE
    // ============================================================

    public List<Produit> findAll() {
        return repository.findAll();
    }

    public Optional<Produit> findById(Integer id) {
        return repository.findById(id);
    }

    /**
     * Recherche par nom (contient, insensible à la casse).
     */
    public List<Produit> search(String nom) {
        if (nom == null || nom.isBlank()) {
            return repository.findAll();
        }
        return repository.findByNomContainingIgnoreCase(nom);
    }

    /**
     * Recherche par code produit.
     */
    public Produit findByCode(String code) {
        return repository.findByCodeProduit(code);
    }

    // ============================================================
    // ÉCRITURE
    // ============================================================

    /**
     * Création d'un produit avec photo optionnelle.
     */
    public Produit create(Produit p, MultipartFile photo) throws IOException {
        if (photo != null && !photo.isEmpty()) {
            String path = fileStorage.saveImage(photo);
            p.setPhoto(path);
        }
        // Valeurs par défaut
        if (p.getQteInitiale() == null) p.setQteInitiale(0);
        if (p.getQteAlerte() == null) p.setQteAlerte(5);
        if (p.getTva() == null) p.setTva(new BigDecimal("20.00"));
        if (p.getJoursAlerte() == null) p.setJoursAlerte(30);
        if (p.getStatut() == null) p.setStatut(Produit.Statut.en_stock);

        return repository.save(p);
    }

    /**
     * Mise à jour d'un produit.
     */
    public Produit update(Integer id, Produit data, MultipartFile photo) throws IOException {
        Produit existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable : " + id));

        // Copie des champs
        existing.setNom(data.getNom());
        existing.setMarque(data.getMarque());
        existing.setCodeProduit(data.getCodeProduit());
        existing.setDesignation(data.getDesignation());
        existing.setFamille(data.getFamille());
        existing.setRayonnage(data.getRayonnage());
        existing.setUnite(data.getUnite());
        existing.setQteInitiale(data.getQteInitiale());
        existing.setQteAlerte(data.getQteAlerte());
        existing.setPrixAchat(data.getPrixAchat());
        existing.setPrixDetail(data.getPrixDetail());
        existing.setPrixGros(data.getPrixGros());
        existing.setTva(data.getTva());
        existing.setDatePeremption(data.getDatePeremption());
        existing.setJoursAlerte(data.getJoursAlerte());
        existing.setCategorieId(data.getCategorieId());
        if (data.getStatut() != null) {
            existing.setStatut(data.getStatut());
        }

        // Nouvelle image ?
        if (photo != null && !photo.isEmpty()) {
            if (existing.getPhoto() != null) {
                fileStorage.deleteImage(existing.getPhoto());
            }
            String newPath = fileStorage.saveImage(photo);
            existing.setPhoto(newPath);
        }

        return repository.save(existing);
    }

    /**
     * Suppression d'un produit (et de son image).
     */
    public void delete(Integer id) {
        Produit p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable : " + id));

        if (p.getPhoto() != null) {
            fileStorage.deleteImage(p.getPhoto());
        }
        repository.deleteById(id);
    }

    /**
     * Incrémente le stock d'un produit (utilisé lors de la conversion d'un bon d'achat).
     */
    public Produit incrementerStock(Integer produitId, Integer quantite) {
        Produit p = repository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable : " + produitId));

        int stock = p.getQteInitiale() == null ? 0 : p.getQteInitiale();
        p.setQteInitiale(stock + quantite);
        return repository.save(p);
    }

    /**
     * Décrémente le stock d'un produit.
     */
    public Produit decrementerStock(Integer produitId, Integer quantite) {
        Produit p = repository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable : " + produitId));

        int stock = p.getQteInitiale() == null ? 0 : p.getQteInitiale();
        if (stock < quantite) {
            throw new RuntimeException("Stock insuffisant pour : " + p.getNom());
        }
        p.setQteInitiale(stock - quantite);
        return repository.save(p);
    }
}