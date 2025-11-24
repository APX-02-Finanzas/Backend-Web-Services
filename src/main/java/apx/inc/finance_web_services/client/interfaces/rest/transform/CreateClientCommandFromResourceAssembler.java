package apx.inc.finance_web_services.client.interfaces.rest.transform;

import apx.inc.finance_web_services.client.domain.model.commands.CreateClientCommand;
import apx.inc.finance_web_services.client.interfaces.rest.resources.CreateClientResource;

public class CreateClientCommandFromResourceAssembler {

    public static CreateClientCommand toCommandFromResource(CreateClientResource resource) {
        return new CreateClientCommand(
                resource.name(),
                resource.surname(),
                resource.civilState(),
                resource.email(),
                resource.phone(),
                resource.dni(),
                resource.salesManId(),
                resource.monthlyIncome(),
                resource.monthlyExpenses()
        );
    }
}