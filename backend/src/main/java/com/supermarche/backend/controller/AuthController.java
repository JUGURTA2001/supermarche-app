// controller/AuthController.java
package com.supermarche.backend.controller;

import com.supermarche.backend.dto.LoginRequestDTO;
import com.supermarche.backend.dto.LoginResponseDTO;
import com.supermarche.backend.model.Utilisateur;
import com.supermarche.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private com.supermarche.backend.repository.UtilisateurRepository utilisateurRepository;

    @PostMapping("/register-test")
    public Utilisateur registerTest(@RequestBody Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }
}