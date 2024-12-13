package eval.bsd.gestion_convention.services;

import eval.bsd.gestion_convention.dao.EntrepriseDao;
import eval.bsd.gestion_convention.dao.UtilisateurDao;
import eval.bsd.gestion_convention.models.Entreprise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
public class EntrepriseService {
    @Autowired
    private EntrepriseDao entrepriseDao;

    @Autowired
    private UtilisateurDao utilisateurDao;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Crée une nouvelle entreprise après avoir vérifié que son nom n'existe pas déjà.
     * Cette vérification est importante pour maintenir l'unicité des entreprises.
     */
    public Entreprise creer(Entreprise entreprise) {
        // Vérifie si une entreprise avec ce nom existe déjà
        if (entrepriseDao.findByNom(entreprise.getNom()).isPresent()) {
            throw new IllegalArgumentException("Une entreprise avec ce nom existe déjà");
        }

        entreprise.getUtilisateurs().get(0).setPassword(bCryptPasswordEncoder.encode(entreprise.getUtilisateurs().get(0).getPassword()));
        utilisateurDao.save( entreprise.getUtilisateurs().get(0));

        return entrepriseDao.save(entreprise);
    }

    /**
     * Récupère une entreprise par son identifiant.
     * Lance une exception si l'entreprise n'est pas trouvée par une gestion claire des erreurs.
     */
    public Entreprise getById(Integer id) {
        return entrepriseDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise non trouvée avec l'ID : " + id));
    }

    /**
     * Met à jour les informations d'une entreprise existante.
     * Vérifie que le nouveau nom n'est pas déjà utilisé par une autre entreprise.
     */
    public Entreprise mettreAJour(Integer id, Entreprise nouvelleEntreprise) {
        Entreprise entrepriseExistante = getById(id);

        // Vérifie si le nouveau nom n'est pas déjà pris par une autre entreprise
        if (!entrepriseExistante.getNom().equals(nouvelleEntreprise.getNom()) &&
                entrepriseDao.findByNom(nouvelleEntreprise.getNom()).isPresent()) {
            throw new IllegalArgumentException("Ce nom d'entreprise est déjà utilisé");
        }

        entrepriseExistante.setNom(nouvelleEntreprise.getNom());
        return entrepriseDao.save(entrepriseExistante);
    }

    /**
     * Liste toutes les entreprises enregistrées.
     */
    public List<Entreprise> getTout() {
        return entrepriseDao.findAll();
    }

    /**
     * Supprime une entreprise si elle n'a pas de conventions ou d'utilisateurs associés.
     * Cette vérification permet de protéger l'intégrité des données.
     */
    public void supprimer(Integer id) {
        Entreprise entreprise = getById(id);

        // Vérifie si l'entreprise peut être supprimée en toute sécurité
        if (!entreprise.getConventions().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer l'entreprise car elle a des conventions associées");
        }
        if (!entreprise.getUtilisateurs().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer l'entreprise car elle a des utilisateurs associés");
        }

        entrepriseDao.deleteById(id);
    }
}