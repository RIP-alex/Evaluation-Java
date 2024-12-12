package eval.bsd.gestion_convention.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.Conventions;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "entreprises")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entreprise {
    // Identifiant unique de l'entreprise
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Le nom de l'entreprise doit être unique
    @Column(unique = true, nullable = false)
    private String nom;

    // Relation avec les utilisateurs : une entreprise peut avoir plusieurs utilisateurs
    // mappedBy indique que l'entreprise est référencée dans la classe Utilisateur
    // CascadeType.ALL signifie que les opérations sur l'entreprise affectent ses utilisateurs
    @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL)
    private List<Utilisateur> utilisateurs = new ArrayList<>();

    // Relation avec les conventions : une entreprise peut avoir plusieurs conventions
    @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL)
    private List<Convention> conventions = new ArrayList<>();
}