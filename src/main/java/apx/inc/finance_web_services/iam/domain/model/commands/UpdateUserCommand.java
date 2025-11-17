package apx.inc.finance_web_services.iam.domain.model.commands;

import apx.inc.finance_web_services.iam.domain.model.entities.Role;
import apx.inc.finance_web_services.iam.domain.model.valueobjects.Roles;

import java.util.List;

public record UpdateUserCommand(
        String username,
        String password,
        String name,
        String surname,
        String email,
        List<Roles> roles) {
    public UpdateUserCommand{
        if (username==null || username.isBlank() ) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password==null || password.isBlank() ) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (roles==null || roles.isEmpty() ) {
            throw new IllegalArgumentException("Roles cannot be empty");
        }
    }
}
