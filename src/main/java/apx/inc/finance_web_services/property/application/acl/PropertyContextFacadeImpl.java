package apx.inc.finance_web_services.property.application.acl;

import apx.inc.finance_web_services.property.interfaces.acl.PropertyContextFacade;

import apx.inc.finance_web_services.property.domain.model.aggregates.Property;
import apx.inc.finance_web_services.property.domain.model.queries.GetPropertyByIdQuery;
import apx.inc.finance_web_services.property.domain.model.queries.GetPropertiesByUserIdQuery;
import apx.inc.finance_web_services.property.domain.services.PropertyQueryService;
import apx.inc.finance_web_services.property.interfaces.acl.PropertyContextFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PropertyContextFacadeImpl implements PropertyContextFacade {

    private final PropertyQueryService propertyQueryService;

    @Override
    public Optional<Property> fetchPropertyById(Long propertyId) {
        if (propertyId == null || propertyId <= 0) {
            return Optional.empty();
        }
        try {
            var query = new GetPropertyByIdQuery(propertyId);
            var result = propertyQueryService.handle(query);
            return result;
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Property> fetchPropertiesBySalesManId(Long salesManId) {
        if (salesManId == null || salesManId <= 0) {
            return List.of();
        }
        try {
            var query = new GetPropertiesByUserIdQuery(salesManId);
            return propertyQueryService.handle(query);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public boolean validatePropertyForBono(Long propertyId) {
        return fetchPropertyById(propertyId)
                .map(Property::isEligibleForBono)
                .orElse(false);
    }
}