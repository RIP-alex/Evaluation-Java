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

    @IsUser
    @PostMapping("/convention/{conventionId}")
    public ResponseEntity<Salarie> creer(
            @PathVariable Integer conventionId,
            @Valid @RequestBody Salarie salarie) {
        try {
            Salarie nouveauSalarie = salarieService.creer(salarie, conventionId);
            return ResponseEntity.ok(nouveauSalarie);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

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

    @IsUser
    @PutMapping("/{id}")
    public ResponseEntity<Salarie> mettreAJour(
            @PathVariable Integer id,
            @Valid @RequestBody Salarie salarie) {
        try {
            Salarie salarieMisAJour = salarieService.mettreAJour(id, salarie);
            return ResponseEntity.ok(salarieMisAJour);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

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

    @IsUser
    @GetMapping("/recherche/matricule/{matricule}")
    public ResponseEntity<Salarie> rechercherParMatricule(@PathVariable String matricule) {
        try {
            Salarie salarie = salarieService.findByMatricule(matricule);
            return ResponseEntity.ok(salarie);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}