package com.supermarche.backend.controller;

import com.supermarche.backend.dto.BonCommandeDTO;
import com.supermarche.backend.model.BonAchat;
import com.supermarche.backend.model.BonCommande;
import com.supermarche.backend.service.BonCommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/bons-commande")
@CrossOrigin(origins = "*")
public class BonCommandeController {

    @Autowired private BonCommandeService service;

    @GetMapping
    public List<BonCommandeDTO> getAll() {
        return service.getAll();
    }

    @PostMapping
    public ResponseEntity<BonCommande> create(@RequestBody BonCommandeDTO dto) {
        return ResponseEntity.ok(service.creerBonCommande(dto));
    }

    @PostMapping("/{id}/convertir")
    public ResponseEntity<BonAchat> convertir(
            @PathVariable Integer id,
            @RequestParam(required = false) BigDecimal versement) {
        return ResponseEntity.ok(service.convertirEnBonAchat(id, versement));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}