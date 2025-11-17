package apx.inc.finance_web_services.iam.interfaces.rest.transform;

import apx.inc.finance_web_services.iam.domain.model.commands.SignInCommand;
import apx.inc.finance_web_services.iam.interfaces.rest.resources.SignInResource;

public class SignInCommandFromResourceAssembler {
    public static SignInCommand toCommandFromResource(SignInResource signInResource){
        return new SignInCommand(
                signInResource.username(),
                signInResource.password()
        );
    }
}
