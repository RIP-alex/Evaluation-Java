package eval.bsd.gestion_convention.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "utilisateurs")
@Data // Lombok pour générer getters, setters, etc.
@NoArgsConstructor
@AllArgsConstructor

    public class Utilisateur {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

    @Column(unique = true, nullable = false)
    @Email(message = "L'email doit être valide")
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    private Entreprise entreprise;
}