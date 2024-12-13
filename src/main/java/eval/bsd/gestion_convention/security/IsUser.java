package eval.bsd.gestion_convention.security;

import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('ROLE_ADMINISTRATEUR', 'ROLE_ENTREPRISE')")
public @interface IsUser {
}