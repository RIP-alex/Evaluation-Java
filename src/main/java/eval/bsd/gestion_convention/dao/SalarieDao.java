package eval.bsd.gestion_convention.dao;

import eval.bsd.gestion_convention.models.Salarie;
import eval.bsd.gestion_convention.models.Convention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalarieDao extends JpaRepository<Salarie, Integer> {
    // Recherche par matricule
    Optional<Salarie> findByMatricule(String matricule);

    // Vérification de l'unicité du matricule
    boolean existsByMatricule(String matricule);

    // Recherche par code barre
    Optional<Salarie> findByCodeBarre(String codeBarre);

    // Trouve tous les salariés d'une convention
    List<Salarie> findByConvention(Convention convention);

    // Compte le nombre de salariés dans une convention
    @Query("SELECT COUNT(s) FROM Salarie s WHERE s.convention.id = :conventionId")
    long countByConventionId(@Param("conventionId") Integer conventionId);

    // Trouve les salariés avec leur convention et entreprise (optimisation des requêtes)
    @Query("SELECT s FROM Salarie s JOIN FETCH s.convention c JOIN FETCH c.entreprise WHERE s.id = :id")
    Optional<Salarie> findByIdWithConventionAndEntreprise(@Param("id") Integer id);
}