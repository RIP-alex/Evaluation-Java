package eval.bsd.gestion_convention.services;

import eval.bsd.gestion_convention.dao.ConventionDao;
import eval.bsd.gestion_convention.dao.EntrepriseDao;
import eval.bsd.gestion_convention.models.Convention;
import eval.bsd.gestion_convention.models.Entreprise;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ConventionService {

    @Autowired
    private ConventionDao conventionDao;

    @Autowired
    private EntrepriseDao entrepriseDao;

    /**
     * Crée une nouvelle convention en vérifiant toutes les règles métier.
     * @param convention La convention à créer
     * @param entrepriseId L'ID de l'entreprise à laquelle rattacher la convention
     * @return La convention créée
     */
    public Convention creer(Convention convention, Integer entrepriseId) {
        // Vérifie que l'entreprise existe
        Entreprise entreprise = entrepriseDao.findById(entrepriseId)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise non trouvée avec l'ID : " + entrepriseId));

        // Vérifie les règles métier
        validateConvention(convention);

        // Associe l'entreprise à la convention
        convention.setEntreprise(entreprise);

        return conventionDao.save(convention);
    }

    /**
     * Récupère une convention par son identifiant.
     */
    public Convention getById(Integer id) {
        return conventionDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Convention non trouvée avec l'ID : " + id));
    }

    /**
     * Met à jour une convention existante.
     */
    public Convention mettreAJour(Integer id, Convention nouvelleConvention) {
        Convention conventionExistante = getById(id);

        // Vérifie les règles métier
        validateConvention(nouvelleConvention);

        // Met à jour les champs modifiables
        conventionExistante.setNom(nouvelleConvention.getNom());
        conventionExistante.setSubvention(nouvelleConvention.getSubvention());
        conventionExistante.setSalarieMaximum(nouvelleConvention.getSalarieMaximum());

        return conventionDao.save(conventionExistante);
    }

    /**
     * Liste toutes les conventions d'une entreprise.
     */
    public List<Convention> getConventionsParEntreprise(Integer entrepriseId) {
        Entreprise entreprise = entrepriseDao.findById(entrepriseId)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise non trouvée avec l'ID : " + entrepriseId));
        return conventionDao.findByEntreprise(entreprise);
    }

    /**
     * Supprime une convention si elle n'a pas de salariés.
     */
    public void supprimer(Integer id) {
        Convention convention = getById(id);

        if (!convention.getSalaries().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer la convention car elle a des salariés associés");
        }

        conventionDao.deleteById(id);
    }

    /**
     * Valide les règles métier pour une convention.
     */
    private void validateConvention(Convention convention) {
        if (convention.getSalarieMaximum() < 1) {
            throw new IllegalArgumentException("Le nombre maximum de salariés doit être au moins 1");
        }

        if (convention.getSubvention() < 0) {
            throw new IllegalArgumentException("La subvention ne peut pas être négative");
        }
    }
}