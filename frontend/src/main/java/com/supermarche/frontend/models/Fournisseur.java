package com.supermarche.frontend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Fournisseur {

    private Integer id;
    private String nomSociete;
    private String nif;
    private String registreCommerce;
    private String adresse;
    private String telephone;
    private String email;

    public Fournisseur() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNomSociete() { return nomSociete; }
    public void setNomSociete(String nomSociete) { this.nomSociete = nomSociete; }

    public String getNif() { return nif; }
    public void setNif(String nif) { this.nif = nif; }

    public String getRegistreCommerce() { return registreCommerce; }
    public void setRegistreCommerce(String registreCommerce) { this.registreCommerce = registreCommerce; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return nomSociete; // pour l'affichage dans les ComboBox
    }
}