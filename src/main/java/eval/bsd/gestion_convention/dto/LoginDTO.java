package eval.bsd.gestion_convention.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    // L'email doit être valide et ne peut pas être vide
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    // Le mot de passe ne peut pas être vide
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
}