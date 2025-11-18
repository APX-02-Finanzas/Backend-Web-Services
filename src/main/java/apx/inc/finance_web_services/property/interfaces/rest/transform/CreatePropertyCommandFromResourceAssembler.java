package apx.inc.finance_web_services.property.interfaces.rest.transform;

import apx.inc.finance_web_services.property.domain.model.commands.CreatePropertyCommand;
import apx.inc.finance_web_services.property.interfaces.rest.resources.CreatePropertyResource;

public class CreatePropertyCommandFromResourceAssembler {

    public static CreatePropertyCommand toCommandFromResource(CreatePropertyResource resource) {
        return new CreatePropertyCommand(
                resource.title(),
                resource.description(),
                resource.price(),
                resource.currency(),
                resource.address(),
                resource.salesManId()
        );
    }
}