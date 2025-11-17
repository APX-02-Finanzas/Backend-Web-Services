package apx.inc.finance_web_services.iam.interfaces.rest.transform;

import apx.inc.finance_web_services.iam.domain.model.commands.UpdateUserCommand;
import apx.inc.finance_web_services.iam.interfaces.rest.resources.UpdateUserResource;

public class UpdateUserCommandFromResourceAssembler {
    public static UpdateUserCommand toCommandFromResource(UpdateUserResource updateUserResource){
        return new UpdateUserCommand(
                updateUserResource.username(),
                updateUserResource.password(),
                updateUserResource.name(),
                updateUserResource.surname(),
                updateUserResource.email(),
                updateUserResource.roles());
    }
}
