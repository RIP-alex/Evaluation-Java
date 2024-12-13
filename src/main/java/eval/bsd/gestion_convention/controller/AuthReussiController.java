package eval.bsd.gestion_convention.controller;

import eval.bsd.gestion_convention.dto.LoginDTO;
import eval.bsd.gestion_convention.dto.AuthReussiDTO;
import eval.bsd.gestion_convention.security.AppUserDetails;
import eval.bsd.gestion_convention.security.JwtUtils;
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
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(),
                            loginDTO.getPassword()
                    )
            );

            AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
            String token = jwtUtils.generateToken(userDetails);

            AuthReussiDTO reponse = new AuthReussiDTO(
                    token,
                    userDetails.getAuthorities().iterator().next().getAuthority(),
                    userDetails.getUsername()
            );

            return ResponseEntity.ok(reponse);

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Erreur d'authentification: " + e.getMessage());
        }
    }
}