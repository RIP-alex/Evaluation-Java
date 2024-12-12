package eval.bsd.gestion_convention.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "salaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Salarie {
    // Identifiant unique du salarié
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Le matricule doit avoir entre 3 et 10 caractères
    @Size(min = 3, max = 10, message = "Le matricule doit contenir entre 3 et 10 caractères")
    @Column(nullable = false)
    private String matricule;

    // Le code barre ne peut pas être vide
    @NotBlank(message = "Le code barre est obligatoire")
    @Column(name = "code_barre", nullable = false)
    private String codeBarre;

    // Relation avec la convention : un salarié appartient à une seule convention
    @ManyToOne
    @JoinColumn(name = "convention_id", nullable = false)
    private Convention convention;

    // Méthode utilitaire pour vérifier si le salarié peut être ajouté à la convention
    @PrePersist
    @PreUpdate
    private void validateConventionCapacity() {
        if (convention != null && !convention.peutAjouterSalarie()) {
            throw new IllegalStateException("Le nombre maximum de salariés pour cette convention est atteint");
        }
    }
}