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
    private SalarieDao salarieDao;

    @Autowired
    private ConventionDao conventionDao;

    public Salarie creer(Salarie salarie, Integer conventionId) {
        Convention convention = conventionDao.findById(conventionId)
                .orElseThrow(() -> new EntityNotFoundException("Convention non trouvée avec l'ID : " + conventionId));

        long nombreSalariesActuel = salarieDao.countByConventionId(conventionId);
        if (nombreSalariesActuel >= convention.getSalarieMaximum()) {
            throw new IllegalStateException("La convention a atteint son nombre maximum de salariés");
        }

        validateMatricule(salarie.getMatricule());

        if (salarieDao.existsByMatricule(salarie.getMatricule())) {
            throw new IllegalArgumentException("Ce matricule existe déjà");
        }

        if (salarie.getCodeBarre() == null || salarie.getCodeBarre().trim().isEmpty()) {
            throw new IllegalArgumentException("Le code barre est obligatoire");
        }

        salarie.setConvention(convention);
        return salarieDao.save(salarie);
    }

    public Salarie findByMatricule(String matricule) {
        return salarieDao.findByMatricule(matricule)
                .orElseThrow(() -> new EntityNotFoundException("Salarié non trouvé avec le matricule : " + matricule));
    }

    public Salarie getById(Integer id) {
        return salarieDao.findByIdWithConventionAndEntreprise(id)
                .orElseThrow(() -> new EntityNotFoundException("Salarié non trouvé avec l'ID : " + id));
    }

    public Salarie mettreAJour(Integer id, Salarie nouveauSalarie) {
        Salarie salarieExistant = getById(id);

        if (!salarieExistant.getMatricule().equals(nouveauSalarie.getMatricule())) {
            validateMatricule(nouveauSalarie.getMatricule());
            if (salarieDao.existsByMatricule(nouveauSalarie.getMatricule())) {
                throw new IllegalArgumentException("Ce matricule existe déjà");
            }
            salarieExistant.setMatricule(nouveauSalarie.getMatricule());
        }

        if (nouveauSalarie.getCodeBarre() != null && !nouveauSalarie.getCodeBarre().trim().isEmpty()) {
            salarieExistant.setCodeBarre(nouveauSalarie.getCodeBarre());
        }

        return salarieDao.save(salarieExistant);
    }

    public Optional<Salarie> findByCodeBarre(String codeBarre) {
        if (codeBarre == null || codeBarre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le code barre ne peut pas être vide");
        }
        return salarieDao.findByCodeBarre(codeBarre);
    }

    public List<Salarie> getSalariesParConvention(Integer conventionId) {
        Convention convention = conventionDao.findById(conventionId)
                .orElseThrow(() -> new EntityNotFoundException("Convention non trouvée"));
        return salarieDao.findByConvention(convention);
    }

    public void supprimer(Integer id) {
        if (!salarieDao.existsById(id)) {
            throw new EntityNotFoundException("Salarié non trouvé avec l'ID : " + id);
        }
        salarieDao.deleteById(id);
    }

    private void validateMatricule(String matricule) {
        if (matricule == null || matricule.length() < 3 || matricule.length() > 10) {
            throw new IllegalArgumentException("Le matricule doit contenir entre 3 et 10 caractères");
        }
    }
}