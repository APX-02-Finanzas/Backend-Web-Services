package apx.inc.finance_web_services.iam.interfaces.rest.transform;

import apx.inc.finance_web_services.iam.domain.model.commands.CreateUserCommand;
import apx.inc.finance_web_services.iam.interfaces.rest.resources.CreateUserResource;

public class CreateUserCommandFromResourceAssembler {
    public static CreateUserCommand toCommandFromResource(CreateUserResource createUserResource) {
        return new CreateUserCommand(
                createUserResource.username(),
                createUserResource.password(),
                createUserResource.name(),
                createUserResource.surname(),
                createUserResource.email(),
                createUserResource.roles().stream().toList());
    }
}
