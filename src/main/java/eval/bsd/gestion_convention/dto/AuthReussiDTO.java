package eval.bsd.gestion_convention.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthReussiDTO {
    // Le token JWT qui sera utilisé pour les requêtes ultérieures
    private String token;

    // Le rôle de l'utilisateur (ADMINISTRATEUR ou ENTREPRISE)
    private String role;

    // L'email de l'utilisateur
    private String email;
}