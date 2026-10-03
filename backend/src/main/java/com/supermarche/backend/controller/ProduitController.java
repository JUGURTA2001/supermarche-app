package com.supermarche.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.supermarche.backend.model.Produit;
import com.supermarche.backend.service.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/produits")
@CrossOrigin(origins = "*")
public class ProduitController {

    @Autowired private ProduitService service;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GetMapping
    public List<Produit> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produit> getById(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Produit> search(@RequestParam String nom) {
        return service.search(nom);
    }

    @GetMapping("/exists")
    public boolean codeExists(@RequestParam String code) {
        if (code == null || code.isBlank()) return false;
        return service.findByCode(code.trim()) != null;
    }

    @GetMapping("/generate-code")
    public String generateUniqueCode() {
        String code;
        int tentative = 0;
        do {
            code = "PRD-" + java.time.LocalDate.now().toString().replace("-", "")
                    + "-" + String.format("%04d", (int)(Math.random() * 10000));
            tentative++;
        } while (service.findByCode(code) != null && tentative < 100);
        return code;
    }

    /** ⭐ CRÉATION avec photo multipart */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Produit> create(
            @RequestPart("produit") String produitJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) throws IOException {
        Produit p = mapper.readValue(produitJson, Produit.class);
        Produit saved = service.create(p, photo);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /** ⭐ MISE À JOUR avec photo multipart */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Produit> update(
            @PathVariable Integer id,
            @RequestPart("produit") String produitJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) throws IOException {
        Produit data = mapper.readValue(produitJson, Produit.class);
        Produit updated = service.update(id, data, photo);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}