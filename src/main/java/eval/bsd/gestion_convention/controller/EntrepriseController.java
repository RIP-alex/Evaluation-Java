package eval.bsd.gestion_convention.controller;

import eval.bsd.gestion_convention.models.Entreprise;
import eval.bsd.gestion_convention.services.EntrepriseService;
import eval.bsd.gestion_convention.security.IsAdmin;
import eval.bsd.gestion_convention.security.IsUser;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entreprises")
@CrossOrigin(origins = "*")  // Permet les requêtes de n'importe quelle origine
public class EntrepriseController {

    @Autowired
    private EntrepriseService entrepriseService;

    // Création d'une nouvelle entreprise - réservé aux administrateurs
    @IsAdmin
    @PostMapping
    public ResponseEntity<Entreprise> creer(@Valid @RequestBody Entreprise entreprise) {
        try {
            Entreprise nouvelleEntreprise = entrepriseService.creer(entreprise);
            return ResponseEntity.ok(nouvelleEntreprise);
        } catch (IllegalArgumentException e) {
            // Cas où le nom d'entreprise existe déjà
            return ResponseEntity.badRequest().build();
        }
    }

    // Récupération de toutes les entreprises - accessible aux utilisateurs authentifiés
    @IsUser
    @GetMapping
    public ResponseEntity<List<Entreprise>> listerTout() {
        return ResponseEntity.ok(entrepriseService.getTout());
    }

    // Récupération d'une entreprise par son ID
    @IsUser
    @GetMapping("/{id}")
    public ResponseEntity<Entreprise> getById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(entrepriseService.getById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Mise à jour d'une entreprise - réservé aux administrateurs
    @IsAdmin
    @PutMapping("/{id}")
    public ResponseEntity<Entreprise> mettreAJour(
            @PathVariable Integer id,
            @Valid @RequestBody Entreprise entreprise) {
        try {
            Entreprise entrepriseMiseAJour = entrepriseService.mettreAJour(id, entreprise);
            return ResponseEntity.ok(entrepriseMiseAJour);
        } catch (IllegalArgumentException e) {
            // Cas où le nouveau nom existe déjà
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            // Cas où l'entreprise n'existe pas
            return ResponseEntity.notFound().build();
        }
    }

    // Suppression d'une entreprise - réservé aux administrateurs
    @IsAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimer(@PathVariable Integer id) {
        try {
            entrepriseService.supprimer(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            // Cas où l'entreprise ne peut pas être supprimée (a des conventions ou utilisateurs)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Cas où l'entreprise n'existe pas
            return ResponseEntity.notFound().build();
        }
    }
}