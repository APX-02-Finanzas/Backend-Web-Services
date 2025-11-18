package apx.inc.finance_web_services.client.interfaces.rest.transform;

import apx.inc.finance_web_services.client.domain.model.commands.UpdateClientCommand;
import apx.inc.finance_web_services.client.interfaces.rest.resources.UpdateClientResource;

public class UpdateClientCommandFromResourceAssembler {

    public static UpdateClientCommand toCommandFromResource(Long clientId, UpdateClientResource resource) {
        return new UpdateClientCommand(
                clientId,
                resource.name(),
                resource.email(),
                resource.phone(),
                resource.dni(),
                resource.monthlyIncome(),
                resource.monthlyExpenses()
        );
    }
}