package apx.inc.finance_web_services.iam.interfaces.rest.transform;

import apx.inc.finance_web_services.iam.domain.model.aggregates.User;
import apx.inc.finance_web_services.iam.domain.model.entities.Role;
import apx.inc.finance_web_services.iam.interfaces.rest.resources.UserResource;

public class UserResourceFromEntityAssembler {
    public static UserResource toResourceFromEntity(User user) {
        return new UserResource(
                user.getId(),
                user.getUsername(),
                user.getUserRoles().stream().map(Role::getName).toList()
        );
    }

}
