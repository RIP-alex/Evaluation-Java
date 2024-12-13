package eval.bsd.gestion_convention.services;

import eval.bsd.gestion_convention.dao.UtilisateurDao;
import eval.bsd.gestion_convention.models.Utilisateur;
import eval.bsd.gestion_convention.models.Role;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UtilisateurServices {

    @Autowired
    private UtilisateurDao utilisateurDao   ;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Crée un nouvel utilisateur avec validation des données et encodage du mot de passe.
     * Cette méthode vérifie également si l'email n'est pas déjà utilisé.
     */
    public Utilisateur creerUtilisateur(Utilisateur utilisateur) {
        // Vérification de l'unicité de l'email
        if (utilisateurDao.findByEmail(utilisateur.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }

        // Encodage du mot de passe
        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));

        // Si aucun rôle n'est spécifié, on attribue le rôle ENTREPRISE par défaut
        if (utilisateur.getRole() == null) {
            utilisateur.setRole(Role.ENTREPRISE);
        }

        return utilisateurDao.save(utilisateur);
    }

    /**
     * Récupère un utilisateur par son ID.
     * Lance une exception si l'utilisateur n'est pas trouvé.
     */
    public Utilisateur getById(Integer id) {
        return utilisateurDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
    }

    /**
     * Récupère un utilisateur par son email.
     * Cette méthode est particulièrement utile pour l'authentification.
     */
    public Optional<Utilisateur> getByEmail(String email) {
        return utilisateurDao.findByEmail(email);
    }

    /**
     * Met à jour les informations d'un utilisateur existant.
     * Seules certaines informations peuvent être modifiées.
     */
    public Utilisateur mettreAJour(Integer id, Utilisateur utilisateurModifie) {
        Utilisateur utilisateurExistant = getById(id);

        // Vérification de l'unicité de l'email si modifié
        if (!utilisateurExistant.getEmail().equals(utilisateurModifie.getEmail()) &&
                utilisateurDao.findByEmail(utilisateurModifie.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        // Mise à jour des champs modifiables
        utilisateurExistant.setEmail(utilisateurModifie.getEmail());
        if (utilisateurModifie.getPassword() != null) {
            utilisateurExistant.setPassword(passwordEncoder.encode(utilisateurModifie.getPassword()));
        }

        // Le rôle ne peut être modifié que par un administrateur
        // Cette logique sera gérée au niveau du contrôleur avec les annotations de sécurité

        return utilisateurDao.save(utilisateurExistant);
    }

    /**
     * Supprime un utilisateur par son ID.
     * Vérifie d'abord si l'utilisateur existe.
     */
    public void supprimer(Integer id) {
        if (!utilisateurDao.existsById(id)) {
            throw new EntityNotFoundException("Utilisateur non trouvé avec l'ID : " + id);
        }
        utilisateurDao.deleteById(id);
    }

    /**
     * Récupère la liste de tous les utilisateurs.
     */
    public List<Utilisateur> getTout() {
        return utilisateurDao.findAll();
    }
}