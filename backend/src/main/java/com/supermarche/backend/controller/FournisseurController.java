package com.supermarche.backend.controller;

import com.supermarche.backend.model.Fournisseur;
import com.supermarche.backend.repository.FournisseurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fournisseurs")
@CrossOrigin(origins = "*")
public class FournisseurController {

    @Autowired private FournisseurRepository repository;

    @GetMapping
    public List<Fournisseur> getAll() {
        return repository.findAll();
    }

    // ============ AJOUT : Recherche ============
    @GetMapping("/search")
    public List<Fournisseur> search(@RequestParam String nom) {
        if (nom == null || nom.isBlank()) return repository.findAll();
        return repository.findByNomSocieteContainingIgnoreCase(nom);
    }

    @PostMapping
    public Fournisseur create(@RequestBody Fournisseur f) {
        return repository.save(f);
    }

    @PutMapping("/{id}")
    public Fournisseur update(@PathVariable Integer id, @RequestBody Fournisseur f) {
        f.setId(id);
        return repository.save(f);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        repository.deleteById(id);
    }
}