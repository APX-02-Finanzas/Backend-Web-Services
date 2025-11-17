package apx.inc.finance_web_services.iam.domain.model.commands;

import apx.inc.finance_web_services.iam.domain.model.entities.Role;
import apx.inc.finance_web_services.iam.domain.model.valueobjects.Roles;

import java.util.List;

public record SignUpCommand(
        String userName,
        String password,
        String name,
        String surname,
        String email,
        List<Roles> roles
) {
    public SignUpCommand {
        if (userName==null||userName.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password==null||password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (name==null||name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (surname==null||surname.isBlank()) {
            throw new IllegalArgumentException("Surname cannot be empty");
        }
        if (email==null||email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (roles==null||roles.isEmpty()) {
            throw new IllegalArgumentException("Roles cannot be empty");
        }
    }
}
