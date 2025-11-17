package apx.inc.finance_web_services.iam.interfaces.rest.transform;

import apx.inc.finance_web_services.iam.domain.model.commands.SignUpCommand;
import apx.inc.finance_web_services.iam.interfaces.rest.resources.SignUpResource;

public class SignUpCommandFromResourceAssembler {
    public static SignUpCommand toCommandFromResource(SignUpResource signUpResource) {
        return new SignUpCommand(
                signUpResource.username(),
                signUpResource.password(),
                signUpResource.name(),
                signUpResource.surname(),
                signUpResource.email(),
                signUpResource.roles()
        );
    }
}
