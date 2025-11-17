package apx.inc.finance_web_services.property.application.internal.queryservices;

import apx.inc.finance_web_services.property.domain.model.aggregates.Property;
import apx.inc.finance_web_services.property.domain.model.queries.*;
import apx.inc.finance_web_services.property.domain.services.PropertyQueryService;
import apx.inc.finance_web_services.property.infrastructure.persistence.jpa.repositories.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyQueryServiceImpl implements PropertyQueryService {

    private final PropertyRepository propertyRepository;

    @Override
    public Optional<Property> handle(GetPropertyByIdQuery query) {
        log.info("Buscando propiedad por ID: {}", query.propertyId());
        return propertyRepository.findById(query.propertyId());
    }

    @Override
    public List<Property> handle(GetAllPropertiesQuery query) {
        log.info("Obteniendo todas las propiedades");
        return propertyRepository.findAll();
    }

    @Override
    public List<Property> handle(GetPropertiesByUserIdQuery query) {
        log.info("Obteniendo propiedades del salesman: {}", query.salesManId());
        return propertyRepository.findBySalesManId(query.salesManId());
    }
}