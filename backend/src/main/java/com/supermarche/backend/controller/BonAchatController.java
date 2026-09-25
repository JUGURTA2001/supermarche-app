package com.supermarche.backend.controller;

import com.supermarche.backend.model.BonAchat;
import com.supermarche.backend.repository.BonAchatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bons-achat")
@CrossOrigin(origins = "*")
public class BonAchatController {

    @Autowired private BonAchatRepository repository;

    @GetMapping
    public List<BonAchat> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public BonAchat getById(@PathVariable Integer id) {
        return repository.findById(id).orElse(null);
    }
}