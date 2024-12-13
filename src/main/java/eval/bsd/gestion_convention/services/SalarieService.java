package eval.bsd.gestion_convention.services;

import eval.bsd.gestion_convention.dao.SalarieDao;
import eval.bsd.gestion_convention.dao.ConventionDao;
import eval.bsd.gestion_convention.models.Salarie;
import eval.bsd.gestion_convention.models.Convention;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SalarieService {

    @Autowired
    private static SalarieDao salarieDao;

    @Autowired
    private ConventionDao conventionDao;

    /**
     * Crée un nouveau salarié en vérifiant toutes les contraintes métier.
     * Cette méthode que j'utilise assure que :
     * - Le matricule est unique et respecte le format requis
     * - La convention n'a pas atteint son nombre maximum de salariés
     * - Le code barre n'est pas vide.
     */
    public Salarie creer(Salarie salarie, Integer conventionId) {
        // Vérifie que la convention existe
        Convention convention = conventionDao.findById(conventionId)
                .orElseThrow(() -> new EntityNotFoundException("Convention non trouvée avec l'ID : " + conventionId));

        // Vérifie que la convention n'a pas atteint son maximum de salariés
        long nombreSalariesActuel = salarieDao.countByConventionId(conventionId);
        if (nombreSalariesActuel >= convention.getSalarieMaximum()) {
            throw new IllegalStateException("La convention a atteint son nombre maximum de salariés");
        }

        // Vérifie le format du matricule
        validateMatricule(salarie.getMatricule());

        // Vérifie l'unicité du matricule
        if (salarieDao.existsByMatricule(salarie.getMatricule())) {
            throw new IllegalArgumentException("Ce matricule existe déjà");
        }

        // Vérifie que le code barre n'est pas vide
        if (salarie.getCodeBarre() == null || salarie.getCodeBarre().trim().isEmpty()) {
            throw new IllegalArgumentException("Le code barre est obligatoire");
        }

        // Associe la convention au salarié
        salarie.setConvention(convention);

        return salarieDao.save(salarie);
    }

    public static Salarie findByMatricule(String matricule) {
        return salarieDao.findByMatricule(matricule)
                .orElseThrow(() -> new EntityNotFoundException("Salarié non trouvé avec le matricule : " + matricule));
    }

    /**
     * Récupère un salarié par son identifiant.
     * On charge également les informations de la convention associée.
     */
    public Salarie getById(Integer id) {
        return salarieDao.findByIdWithConventionAndEntreprise(id)
                .orElseThrow(() -> new EntityNotFoundException("Salarié non trouvé avec l'ID : " + id));
    }

    /**
     * Met à jour les informations d'un salarié.
     * Seuls le matricule et le code barre peuvent être modifiés.
     */
    public Salarie mettreAJour(Integer id, Salarie nouveauSalarie) {
        Salarie salarieExistant = getById(id);

        // Vérifie si le nouveau matricule est différent et unique
        if (!salarieExistant.getMatricule().equals(nouveauSalarie.getMatricule())) {
            validateMatricule(nouveauSalarie.getMatricule());
            if (salarieDao.existsByMatricule(nouveauSalarie.getMatricule())) {
                throw new IllegalArgumentException("Ce matricule existe déjà");
            }
            salarieExistant.setMatricule(nouveauSalarie.getMatricule());
        }

        // Vérifie et met à jour le code barre
        if (nouveauSalarie.getCodeBarre() != null && !nouveauSalarie.getCodeBarre().trim().isEmpty()) {
            salarieExistant.setCodeBarre(nouveauSalarie.getCodeBarre());
        }

        return salarieDao.save(salarieExistant);
    }

    // Méthode pour vérifier le code barre
    public Optional<Salarie> findByCodeBarre(String codeBarre) {
        // On vérifie d'abord que le code barre n'est pas null ou vide
        if (codeBarre == null || codeBarre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le code barre ne peut pas être vide");
        }

        // On appelle la méthode du DAO pour chercher le salarié
        return salarieDao.findByCodeBarre(codeBarre);
    }

    /**
     * Liste tous les salariés d'une convention donnée.
     */
    public List<Salarie> getSalariesParConvention(Integer conventionId) {
        Convention convention = conventionDao.findById(conventionId)
                .orElseThrow(() -> new EntityNotFoundException("Convention non trouvée"));
        return salarieDao.findByConvention(convention);
    }

    /**
     * Supprime un salarié.
     */
    public void supprimer(Integer id) {
        if (!salarieDao.existsById(id)) {
            throw new EntityNotFoundException("Salarié non trouvé avec l'ID : " + id);
        }
        salarieDao.deleteById(id);
    }

    /**
     * Valide le format du matricule selon les règles métier.
     * Le matricule doit avoir entre 3 et 10 caractères.
     */
    private void validateMatricule(String matricule) {
        if (matricule == null || matricule.length() < 3 || matricule.length() > 10) {
            throw new IllegalArgumentException("Le matricule doit contenir entre 3 et 10 caractères");
        }
    }
}