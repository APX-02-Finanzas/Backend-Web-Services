package apx.inc.finance_web_services.property.domain.services;

import apx.inc.finance_web_services.property.domain.model.aggregates.Property;
import apx.inc.finance_web_services.property.domain.model.queries.GetAllPropertiesQuery;
import apx.inc.finance_web_services.property.domain.model.queries.GetPropertiesByUserIdQuery;
import apx.inc.finance_web_services.property.domain.model.queries.GetPropertyByIdQuery;

import java.util.List;
import java.util.Optional;

public interface PropertyQueryService {

    Optional<Property> handle(GetPropertyByIdQuery query);
    List<Property> handle(GetAllPropertiesQuery query);
    List<Property> handle(GetPropertiesByUserIdQuery query);
}
