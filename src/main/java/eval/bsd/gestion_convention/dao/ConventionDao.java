package eval.bsd.gestion_convention.dao;

import eval.bsd.gestion_convention.models.Convention;
import eval.bsd.gestion_convention.models.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConventionDao extends JpaRepository<Convention, Integer> {
    // Trouve toutes les conventions d'une entreprise
    List<Convention> findByEntreprise(Entreprise entreprise);

    // Trouve les conventions qui ont encore de la place
    @Query("SELECT c FROM Convention c WHERE c.salarieMaximum > (SELECT COUNT(s) FROM Salarie s WHERE s.convention = c)")
    List<Convention> findAvailableConventions();

    // Trouve les conventions par entreprise avec le nombre de salariés
    @Query("SELECT c, COUNT(s) FROM Convention c LEFT JOIN c.salaries s WHERE c.entreprise.id = :entrepriseId GROUP BY c")
    List<Object[]> findConventionsWithSalarieCount(@Param("entrepriseId") Integer entrepriseId);

    // Vérifie si une convention a atteint son nombre maximum de salariés
    @Query("SELECT CASE WHEN COUNT(s) >= c.salarieMaximum THEN true ELSE false END FROM Convention c LEFT JOIN c.salaries s WHERE c.id = :conventionId GROUP BY c")
    boolean isConventionFull(@Param("conventionId") Integer conventionId);
}