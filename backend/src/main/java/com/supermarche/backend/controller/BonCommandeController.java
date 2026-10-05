package com.supermarche.backend.controller;

import com.supermarche.backend.dto.BonCommandeDTO;
import com.supermarche.backend.model.BonAchat;
import com.supermarche.backend.model.BonCommande;
import com.supermarche.backend.service.BonCommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bons-commande")
@CrossOrigin(origins = "*")
public class BonCommandeController {

    @Autowired private BonCommandeService service;

    @GetMapping
    public List<BonCommandeDTO> getAll() {
        return service.getAll();
    }

    // ⭐ Création : crée bon de commande + bon d'achat auto si reste=0
    @PostMapping
    public ResponseEntity<BonCommande> create(@RequestBody BonCommandeDTO dto) {
        BonCommande saved = service.creerBonCommande(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Conversion manuelle (pour les bons avec reste > 0)


    /**
     * Convertit un bon de commande en bon d'achat.
     * POST /api/bons-commande/{id}/convertir?versement=XXX
     */
    @PostMapping("/{id}/convertir")
    public ResponseEntity<?> convertir(
            @PathVariable Integer id,
            @RequestParam(required = false) BigDecimal versement) {
        try {
            BonAchat achat = service.convertirEnBonAchat(id, versement);
            return ResponseEntity.ok(achat);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}