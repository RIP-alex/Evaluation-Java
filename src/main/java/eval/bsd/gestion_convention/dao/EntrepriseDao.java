package eval.bsd.gestion_convention.dao;

import eval.bsd.gestion_convention.models.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntrepriseDao extends JpaRepository<Entreprise, Integer> {
    // Recherche par nom avec gestion des doublons
    Optional<Entreprise> findByNom(String nom);

    // Vérifie si le nom existe déjà (pour éviter les doublons)
    boolean existsByNom(String nom);

    // Charge l'entreprise avec toutes ses conventions
    @Query("SELECT e FROM Entreprise e LEFT JOIN FETCH e.conventions WHERE e.id = :id")
    Optional<Entreprise> findByIdWithConventions(@Param("id") Integer id);

    // Charge l'entreprise avec tous ses utilisateurs
    @Query("SELECT e FROM Entreprise e LEFT JOIN FETCH e.utilisateurs WHERE e.id = :id")
    Optional<Entreprise> findByIdWithUtilisateurs(@Param("id") Integer id);
}