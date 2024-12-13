package eval.bsd.gestion_convention.security;

import eval.bsd.gestion_convention.models.Role;
import eval.bsd.gestion_convention.models.Utilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AppUserDetails implements UserDetails {

    private final Utilisateur utilisateur;

    public AppUserDetails(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convertit le rôle de l'utilisateur en autorité Spring Security
        if (utilisateur.getRole() == Role.ADMINISTRATEUR) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRATEUR"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_ENTREPRISE"));
    }

    // Méthode utilitaire pour accéder à l'utilisateur
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    @Override
    public String getPassword() {
        return utilisateur.getPassword();
    }

    @Override
    public String getUsername() {
        return utilisateur.getEmail();
    }

    // Par défaut, le compte ne peut pas expirer/être verrouillé
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}