package eval.bsd.gestion_convention.controller;

import eval.bsd.gestion_convention.models.Salarie;
import eval.bsd.gestion_convention.services.SalarieService;
import eval.bsd.gestion_convention.security.IsUser;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/salaries")
@CrossOrigin(origins = "*")
public class SalarieController {

    @Autowired
    private SalarieService salarieService;

    /// Crée un nouveau salarié.
    /// Cette méthode vérifie plusieurs règles métier avant de créer le salarié :
    /// - Le matricule doit être unique et avoir entre 3 et 10 caractères.
    /// - La convention ne doit pas avoir atteint son nombre maximum de salariés.
    /// - Le code barre doit être renseigné.
    /// En cas de violation d'une de ces règles, une réponse appropriée est renvoyée.

    @IsUser
    @PostMapping("/convention/{conventionId}")
    public ResponseEntity<Salarie> creer(
            @PathVariable Integer conventionId,
            @Valid @RequestBody Salarie salarie) {
        try {
            Salarie nouveauSalarie = salarieService.creer(salarie, conventionId);
            return ResponseEntity.ok(nouveauSalarie);
        } catch (IllegalStateException e) {
            // Cas où la convention a atteint son maximum de salariés
            return ResponseEntity.badRequest().body(null);
        } catch (IllegalArgumentException e) {
            // Cas où le matricule est invalide ou déjà utilisé
            return ResponseEntity.badRequest().body(null);
        }
    }

    /// Récupère la liste des salariés d'une convention spécifique.
    /// Si la convention n'est pas trouvée, une réponse 404 Not Found est renvoyée.

    @IsUser
    @GetMapping("/convention/{conventionId}")
    public ResponseEntity<List<Salarie>> getSalariesParConvention(
            @PathVariable Integer conventionId) {
        try {
            List<Salarie> salaries = salarieService.getSalariesParConvention(conventionId);
            return ResponseEntity.ok(salaries);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /// Récupère les informations détaillées d'un salarié.
    /// Inclut les informations sur la convention et l'entreprise du salarié.
    /// Si le salarié n'est pas trouvé, une réponse 404 Not Found est renvoyée.

    @IsUser
    @GetMapping("/{id}")
    public ResponseEntity<Salarie> getById(@PathVariable Integer id) {
        try {
            Salarie salarie = salarieService.getById(id);
            return ResponseEntity.ok(salarie);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /// Met à jour les informations d'un salarié.
    /// Seuls le matricule et le code barre peuvent être modifiés.
    /// La convention d'un salarié ne peut pas être changée.
    /// En cas de matricule invalide ou déjà utilisé, une réponse 400 Bad Request est renvoyée.
    /// Si le salarié n'est pas trouvé, une réponse 404 Not Found est renvoyée.
    @IsUser
    @PutMapping("/{id}")
    public ResponseEntity<Salarie> mettreAJour(
            @PathVariable Integer id,
            @Valid @RequestBody Salarie salarie) {
        try {
            Salarie salarieMisAJour = salarieService.mettreAJour(id, salarie);
            return ResponseEntity.ok(salarieMisAJour);
        } catch (IllegalArgumentException e) {
            // Cas où le nouveau matricule est invalide ou déjà utilisé
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            // Salarié non trouvé
            return ResponseEntity.notFound().build();
        }
    }

    /// Supprime un salarié de la base de données.
    /// Si le salarié n'est pas trouvé, une réponse 404 Not Found est renvoyée.
    @IsUser
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimer(@PathVariable Integer id) {
        try {
            salarieService.supprimer(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /// Recherche un salarié par son matricule.
    /// Permet de vérifier si un matricule existe déjà.
    /// Renvoie le salarié si trouvé, sinon une réponse 404 Not Found.
    /// En cas de paramètre invalide, une réponse 400 Bad Request est renvoyée.
    @IsUser
    @GetMapping("/recherche/matricule/{matricule}")
    public ResponseEntity<Salarie> rechercherParMatricule(@PathVariable String matricule) {
        try {
            Optional<Salarie> salarie = Optional.ofNullable(SalarieService.findByMatricule(matricule));
            return salarie.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}