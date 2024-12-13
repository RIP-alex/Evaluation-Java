package eval.bsd.gestion_convention.controller;

import eval.bsd.gestion_convention.dto.LoginDTO;
import eval.bsd.gestion_convention.dto.AuthReussiDTO;
import eval.bsd.gestion_convention.security.AppUserDetails;
import eval.bsd.gestion_convention.security.JwtUtils;
import eval.bsd.gestion_convention.services.UtilisateurServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthReussiController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            // Tente d'authentifier l'utilisateur
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(),
                            loginDTO.getPassword()
                    )
            );

            // Si l'authentification réussit, on récupère les détails de l'utilisateur
            AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();

            // On génère un token JWT
            String token = jwtUtils.generateToken(userDetails);

            // On crée la réponse avec le token et les informations de l'utilisateur
            return ResponseEntity.ok(new AuthReussiDTO(
                    token,
                    userDetails.getAuthorities().iterator().next().getAuthority(),
                    userDetails.getUsername()
            ));

        } catch (Exception e) {
            // En cas d'échec de l'authentification
            return ResponseEntity
                    .badRequest()
                    .body("Email ou mot de passe incorrect");
        }
    }
}