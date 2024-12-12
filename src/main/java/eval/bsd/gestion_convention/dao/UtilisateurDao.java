package eval.bsd.gestion_convention.dao;

import eval.bsd.gestion_convention.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UtilisateurDao extends JpaRepository<Utilisateur, Integer> {
    // Recherche par email avec gestion des cas null
    Optional<Utilisateur> findByEmail(String email);

    // Recherche des utilisateurs par entreprise
    @Query("SELECT u FROM Utilisateur u WHERE u.entreprise.id = :entrepriseId")
    List<Utilisateur> findByEntrepriseId(@Param("entrepriseId") Integer entrepriseId);

    // Recherche utilisateur avec chargement eager de l'entreprise
    @Query("SELECT u FROM Utilisateur u JOIN FETCH u.entreprise WHERE u.email = :email")
    Optional<Utilisateur> findByEmailWithEntreprise(@Param("email") String email);

    // Vérification si l'email existe déjà
    boolean existsByEmail(String email);
}