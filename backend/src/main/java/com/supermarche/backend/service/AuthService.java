// service/AuthService.java
package com.supermarche.backend.service;

import com.supermarche.backend.dto.LoginRequestDTO;
import com.supermarche.backend.dto.LoginResponseDTO;
import com.supermarche.backend.model.Utilisateur;
import com.supermarche.backend.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public LoginResponseDTO login(LoginRequestDTO request) {
        Utilisateur user = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        if (!user.getActif()) {
            throw new RuntimeException("Ce compte est désactivé");
        }

        return new LoginResponseDTO(
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getRole(),
                "Connexion réussie"
        );
    }
}