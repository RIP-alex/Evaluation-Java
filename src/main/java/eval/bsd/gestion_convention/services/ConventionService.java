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

    public Convention creer(Convention convention, Integer entrepriseId) {
        Entreprise entreprise = entrepriseDao.findById(entrepriseId)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise non trouvée"));

        validateConvention(convention);
        convention.setEntreprise(entreprise);

        return conventionDao.save(convention);
    }

    public Convention getById(Integer id) {
        return conventionDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Convention non trouvée"));
    }

    public Convention mettreAJour(Integer id, Convention nouvelleConvention) {
        Convention conventionExistante = getById(id);

        validateConvention(nouvelleConvention);
        conventionExistante.setNom(nouvelleConvention.getNom());
        conventionExistante.setSubvention(nouvelleConvention.getSubvention());
        conventionExistante.setSalarieMaximum(nouvelleConvention.getSalarieMaximum());

        return conventionDao.save(conventionExistante);
    }

    public List<Convention> getConventionsParEntreprise(Integer entrepriseId) {
        Entreprise entreprise = entrepriseDao.findById(entrepriseId)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise non trouvée"));
        return conventionDao.findByEntreprise(entreprise);
    }

    public void supprimer(Integer id) {
        Convention convention = getById(id);

        if (!convention.getSalaries().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer la convention car elle a des salariés associés");
        }

        conventionDao.deleteById(id);
    }

    private void validateConvention(Convention convention) {
        if (convention.getSalarieMaximum() < 1) {
            throw new IllegalArgumentException("Le nombre maximum de salariés doit être au moins 1");
        }

        if (convention.getSubvention() < 0) {
            throw new IllegalArgumentException("La subvention ne peut pas être négative");
        }
    }
}