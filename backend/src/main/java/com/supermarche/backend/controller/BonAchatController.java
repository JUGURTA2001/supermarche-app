package com.supermarche.backend.controller;

import com.supermarche.backend.dto.BonAchatDTO;
import com.supermarche.backend.model.BonAchat;
import com.supermarche.backend.model.DetailBonAchat;
import com.supermarche.backend.repository.BonAchatRepository;
import com.supermarche.backend.repository.DetailBonAchatRepository;
import com.supermarche.backend.repository.FournisseurRepository;
import com.supermarche.backend.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/bons-achat")
@CrossOrigin(origins = "*")
public class BonAchatController {

    @Autowired private BonAchatRepository bonAchatRepository;
    @Autowired private DetailBonAchatRepository detailBonAchatRepository;
    @Autowired private FournisseurRepository fournisseurRepository;
    @Autowired private ProduitRepository produitRepository;

    /** GET /api/bons-achat → liste simple (sans détails) */
    @GetMapping
    public List<BonAchat> getAll() {
        return bonAchatRepository.findAll();
    }

    /** GET /api/bons-achat/{id} → un bon avec ses détails */
    @GetMapping("/{id}")
    public BonAchatDTO getById(@PathVariable Integer id) {
        BonAchat ba = bonAchatRepository.findById(id).orElse(null);
        if (ba == null) return null;

        BonAchatDTO dto = new BonAchatDTO();
        dto.setId(ba.getId());
        dto.setBonCommandeId(ba.getBonCommandeId());
        dto.setTotal(ba.getTotal());
        dto.setVersement(ba.getVersement());
        dto.setNbArticles(ba.getNbArticles());
        dto.setEstRegle(ba.getEstRegle());
        dto.setFournisseurId(ba.getFournisseurId());
        dto.setDateBon(ba.getDateBon());
        dto.setReste(ba.getReste());

        if (ba.getFournisseurId() != null) {
            fournisseurRepository.findById(ba.getFournisseurId())
                    .ifPresent(f -> dto.setNomFournisseur(f.getNomSociete()));
        }

        List<BonAchatDTO.DetailLigneDTO> details = new ArrayList<>();
        for (DetailBonAchat d : detailBonAchatRepository.findByBonAchatId(id)) {
            BonAchatDTO.DetailLigneDTO l = new BonAchatDTO.DetailLigneDTO();
            l.setProduitId(d.getProduitId());
            l.setQuantite(d.getQuantite());
            l.setPrixAchat(d.getPrixAchat());
            produitRepository.findById(d.getProduitId())
                    .ifPresent(p -> l.setNomProduit(p.getNom()));
            details.add(l);
        }
        dto.setDetails(details);
        return dto;
    }

    @Autowired private com.supermarche.backend.service.BonCommandeService bonCommandeService;

    /**
     * Ajoute un versement à un bon d'achat (paiement échelonné).
     * POST /api/bons-achat/{id}/versement?montant=XXX
     */
    @PostMapping("/{id}/versement")
    public ResponseEntity<?> ajouterVersement(
            @PathVariable Integer id,
            @RequestParam BigDecimal montant) {
        try {
            BonAchat updated = bonCommandeService.ajouterVersementBonAchat(id, montant);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            java.util.Map<String, Object> error = new java.util.HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}