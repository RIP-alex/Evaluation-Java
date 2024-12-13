package eval.bsd.gestion_convention.security;

import eval.bsd.gestion_convention.dao.UtilisateurDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private UtilisateurDao utilisateurDAO;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Tentative de chargement de l'utilisateur avec email: " + email);

        return utilisateurDAO.findByEmail(email)
                .map(utilisateur -> {
                    System.out.println("Utilisateur trouvé avec rôle: " + utilisateur.getRole());
                    return new AppUserDetails(utilisateur);
                })
                .orElseThrow(() -> {
                    System.out.println("Utilisateur non trouvé avec email: " + email);
                    return new UsernameNotFoundException("Utilisateur non trouvé: " + email);
                });
    }
}