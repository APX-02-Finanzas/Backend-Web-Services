package apx.inc.finance_web_services.property.interfaces.rest.transform;

import apx.inc.finance_web_services.property.domain.model.aggregates.Property;
import apx.inc.finance_web_services.property.interfaces.rest.resources.PropertyResource;

public class PropertyResourceFromEntityAssembler {

    public static PropertyResource toResourceFromEntity(Property property) {
        return new PropertyResource(
                property.getId(),
                property.getTitle(),
                property.getDescription(),
                property.getPrice(),
                property.getCurrency(),
                property.getAddress(),
                property.getSalesManId(),
                property.isEligibleForBono()
        );
    }
}