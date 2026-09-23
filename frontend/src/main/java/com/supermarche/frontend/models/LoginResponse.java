package com.supermarche.frontend.models;

public class LoginResponse {
    private String token;
    private String role; // "admin" ou "employe"
    private String email;
    private String nom;

    // Getters et Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
}