package apx.inc.finance_web_services.iam.interfaces.rest.resources;

import apx.inc.finance_web_services.iam.domain.model.valueobjects.Roles;

import java.util.List;

public record UserResource(
        Long id,
        String username,
        String name,
        String surname,
        String email,
        List<Roles> roles
) { }
