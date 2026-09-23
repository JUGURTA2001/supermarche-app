// dto/LoginResponseDTO.java
package com.supermarche.backend.dto;

import com.supermarche.backend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private Integer id;
    private String nom;
    private String prenom;
    private String email;
    private Role role;
    private String message;
}