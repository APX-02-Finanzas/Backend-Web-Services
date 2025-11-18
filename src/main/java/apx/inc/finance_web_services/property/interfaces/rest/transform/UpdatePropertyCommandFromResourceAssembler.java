package apx.inc.finance_web_services.property.interfaces.rest.transform;

import apx.inc.finance_web_services.property.domain.model.commands.UpdatePropertyCommand;
import apx.inc.finance_web_services.property.interfaces.rest.resources.UpdatePropertyResource;

public class UpdatePropertyCommandFromResourceAssembler {

    public static UpdatePropertyCommand toCommandFromResource(Long propertyId, UpdatePropertyResource resource) {
        return new UpdatePropertyCommand(
                propertyId,
                resource.title(),
                resource.description(),
                resource.price(),
                resource.currency(),
                resource.address()
        );
    }
}