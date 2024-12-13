package eval.bsd.gestion_convention.controller;

import eval.bsd.gestion_convention.models.Convention;
import eval.bsd.gestion_convention.services.ConventionService;
import eval.bsd.gestion_convention.security.IsAdmin;
import eval.bsd.gestion_convention.security.IsUser;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conventions")
@CrossOrigin(origins = "*")
public class ConventionController {

    @Autowired
    private ConventionService conventionService;

    // Endpoint pour créer une nouvelle convention
    // Nécessite l'ID de l'entreprise à laquelle la convention sera rattachée
    @IsAdmin
    @PostMapping("/entreprise/{entrepriseId}")
    public ResponseEntity<Convention> creer(
            @PathVariable Integer entrepriseId,
            @Valid @RequestBody Convention convention) {
        try {
            Convention nouvelleConvention = conventionService.creer(convention, entrepriseId);
            return ResponseEntity.ok(nouvelleConvention);
        } catch (IllegalArgumentException e) {
            // Cas où les règles métier ne sont pas respectées (ex: subvention négative)
            return ResponseEntity.badRequest().build();
        }
    }

    // Récupère toutes les conventions d'une entreprise spécifique
    @IsUser
    @GetMapping("/entreprise/{entrepriseId}")
    public ResponseEntity<List<Convention>> getConventionsParEntreprise(
            @PathVariable Integer entrepriseId) {
        try {
            List<Convention> conventions = conventionService.getConventionsParEntreprise(entrepriseId);
            return ResponseEntity.ok(conventions);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Récupère une convention spécifique par son ID
    @IsUser
    @GetMapping("/{id}")
    public ResponseEntity<Convention> getById(@PathVariable Integer id) {
        try {
            Convention convention = conventionService.getById(id);
            return ResponseEntity.ok(convention);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Met à jour une convention existante
    // Les règles métier sont vérifiées (nombre de salariés, subvention, etc.)
    @IsAdmin
    @PutMapping("/{id}")
    public ResponseEntity<Convention> mettreAJour(
            @PathVariable Integer id,
            @Valid @RequestBody Convention convention) {
        try {
            Convention conventionMiseAJour = conventionService.mettreAJour(id, convention);
            return ResponseEntity.ok(conventionMiseAJour);
        } catch (IllegalArgumentException e) {
            // Violation des règles métier
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            // Convention non trouvée
            return ResponseEntity.notFound().build();
        }
    }

    // Supprime une convention si elle n'a pas de salariés
    @IsAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimer(@PathVariable Integer id) {
        try {
            conventionService.supprimer(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            // La convention a des salariés et ne peut pas être supprimée
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Convention non trouvée
            return ResponseEntity.notFound().build();
        }
    }
}