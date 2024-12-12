package eval.bsd.gestion_convention.models;

 import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conventions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Convention {
    // Identifiant unique de la convention
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Le nom de la convention
    @Column(nullable = false)
    private String nom;

    // La subvention ne peut pas être négative
    @PositiveOrZero(message = "La subvention ne peut pas être négative")
    private float subvention;

    // Le nombre maximum de salariés doit être au moins 1
    @Min(value = 1, message = "Le nombre maximum de salariés doit être au moins 1")
    @Column(name = "salarie_maximum", nullable = false)
    private int salarieMaximum;

    // Relation avec l'entreprise : plusieurs conventions peuvent appartenir à une entreprise
    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    // Relation avec les salariés : une convention peut avoir plusieurs salariés
    @OneToMany(mappedBy = "convention", cascade = CascadeType.ALL)
    private List<Salarie> salaries = new ArrayList<>();

    // Méthode utilitaire pour vérifier si la convention peut accepter un nouveau salarié
    public boolean peutAjouterSalarie() {
        return salaries.size() < salarieMaximum;
    }
}
