package eval.bsd.gestion_convention.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration  // Indique que c'est une classe de configuration Spring
@EnableWebSecurity  // Active la sécurité Web de Spring Security
@EnableMethodSecurity  // Permet l'utilisation des annotations de sécurité comme @PreAuthorize
public class ConfigurationSecurite {

    @Autowired
    private AppUserDetailsService userDetailsService;  // Service qui charge les utilisateurs

    @Autowired
    private JwtFilter jwtFilter;  // Notre filtre JWT personnalisé

    // Configuration du gestionnaire d'authentification
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Configuration du fournisseur d'authentification
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);  // Définit le service qui charge les utilisateurs
        provider.setPasswordEncoder(passwordEncoder());      // Définit l'encodeur de mot de passe
        return provider;
    }

    // Configuration de l'encodeur de mot de passe
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Utilise BCrypt pour le hachage des mots de passe
    }

    // Configuration principale de la sécurité
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Désactive CSRF car nous utilisons des tokens JWT
                .csrf(csrf -> csrf.disable())

                // Configure CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configure la gestion des sessions (stateless car nous utilisons JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configure les autorisations des requêtes
                .authorizeHttpRequests(auth -> auth
                        // Les URLs publiques
                        .requestMatchers("/api/auth/**").permitAll()
                        // Toutes les autres requêtes nécessitent une authentification
                        .anyRequest().authenticated()
                )

                // Ajout du filtre JWT avant le filtre d'authentification standard
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Configuration CORS (Cross-Origin Resource Sharing)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Autorise toutes les origines (à adapter selon vos besoins de sécurité)
        configuration.setAllowedOrigins(List.of("*"));
        // Autorise les méthodes HTTP spécifiées
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Autorise tous les headers
        configuration.setAllowedHeaders(List.of("*"));
        // Permet l'envoi de credentials si nécessaire
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Applique cette configuration à toutes les URLs
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}