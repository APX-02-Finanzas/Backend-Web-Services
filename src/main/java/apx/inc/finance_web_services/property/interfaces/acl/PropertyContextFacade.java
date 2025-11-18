package apx.inc.finance_web_services.property.interfaces.acl;

import apx.inc.finance_web_services.property.domain.model.aggregates.Property;
import java.util.List;
import java.util.Optional;

public interface PropertyContextFacade {
    Optional<Property> fetchPropertyById(Long propertyId);
    List<Property> fetchPropertiesBySalesManId(Long salesManId);
    boolean validatePropertyForBono(Long propertyId);
}
