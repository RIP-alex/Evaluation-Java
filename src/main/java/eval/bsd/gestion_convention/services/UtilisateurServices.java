package eval.bsd.gestion_convention.services;

import eval.bsd.gestion_convention.dao.EntrepriseDao;
import eval.bsd.gestion_convention.dao.UtilisateurDao;
import eval.bsd.gestion_convention.models.Entreprise;
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
    private UtilisateurDao utilisateurDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Utilisateur creerUtilisateur(Utilisateur utilisateur) {
        // Vérification de l'unicité de l'email
        if (utilisateurDao.findByEmail(utilisateur.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }

        // Encodage du mot de passe
        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));

        // Détermination du rôle
        if (utilisateur.getEntreprise() == null) {
            utilisateur.setRole(Role.ADMINISTRATEUR);
        } else {
            utilisateur.setRole(Role.ENTREPRISE);
        }

        return utilisateurDao.save(utilisateur);
    }

    public Utilisateur getById(Integer id) {
        return utilisateurDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
    }

    public Optional<Utilisateur> getByEmail(String email) {
        return utilisateurDao.findByEmail(email);
    }

    public Utilisateur mettreAJour(Integer id, Utilisateur utilisateurModifie) {
        Utilisateur utilisateurExistant = getById(id);

        // Vérification de l'unicité de l'email
        if (!utilisateurExistant.getEmail().equals(utilisateurModifie.getEmail()) &&
                utilisateurDao.findByEmail(utilisateurModifie.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        // Mise à jour des champs
        utilisateurExistant.setEmail(utilisateurModifie.getEmail());

        if (utilisateurModifie.getPassword() != null) {
            utilisateurExistant.setPassword(passwordEncoder.encode(utilisateurModifie.getPassword()));
        }

        // Mise à jour du rôle et de l'entreprise
        if (utilisateurModifie.getEntreprise() == null) {
            utilisateurExistant.setRole(Role.ADMINISTRATEUR);
            utilisateurExistant.setEntreprise(null);
        } else {
            utilisateurExistant.setRole(Role.ENTREPRISE);
            utilisateurExistant.setEntreprise(utilisateurModifie.getEntreprise());
        }

        return utilisateurDao.save(utilisateurExistant);
    }

    public void supprimer(Integer id) {
        if (!utilisateurDao.existsById(id)) {
            throw new EntityNotFoundException("Utilisateur non trouvé avec l'ID : " + id);
        }
        utilisateurDao.deleteById(id);
    }

    public List<Utilisateur> getTout() {
        return utilisateurDao.findAll();
    }
}