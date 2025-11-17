package apx.inc.finance_web_services.iam.domain.services;

import apx.inc.finance_web_services.iam.domain.model.commands.SeedRolesCommand;

public interface RoleCommandService {

    void handle(SeedRolesCommand seedRolesCommand);

}
