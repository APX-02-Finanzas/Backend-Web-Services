package apx.inc.finance_web_services.iam.interfaces.rest.resources;

import apx.inc.finance_web_services.iam.domain.model.entities.Role;
import apx.inc.finance_web_services.iam.domain.model.valueobjects.Roles;

import java.util.List;

public record UpdateUserResource(
        String username,
        String password,
        String name,
        String surname,
        String email,
        List<Roles> roles
) {
    public UpdateUserResource {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
    }
}
