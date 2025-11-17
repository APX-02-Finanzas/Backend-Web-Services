package apx.inc.finance_web_services.property.application.internal.commandservices;

import apx.inc.finance_web_services.property.domain.model.aggregates.Property;
import apx.inc.finance_web_services.property.domain.model.commands.CreatePropertyCommand;
import apx.inc.finance_web_services.property.domain.model.commands.UpdatePropertyCommand;
import apx.inc.finance_web_services.property.domain.model.commands.DeletePropertyCommand;
import apx.inc.finance_web_services.property.domain.services.PropertyCommandService;
import apx.inc.finance_web_services.property.infrastructure.persistence.jpa.repositories.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyCommandServiceImpl implements PropertyCommandService {

    private final PropertyRepository propertyRepository;

    @Override
    public Long handle(CreatePropertyCommand command) {
        log.info("Creando nueva propiedad: {}", command.title());

        Property property = new Property(command);
        Property savedProperty = propertyRepository.save(property);

        log.info("Propiedad creada exitosamente con ID: {}", savedProperty.getId());
        return savedProperty.getId();
    }

    @Override
    public void handle(UpdatePropertyCommand command) {
        log.info("Actualizando propiedad con ID: {}", command.propertyId());

        Property property = propertyRepository.findById(command.propertyId())
                .orElseThrow(() -> new IllegalArgumentException("Propiedad no encontrada con ID: " + command.propertyId()));

        property.update(command);
        propertyRepository.save(property);

        log.info("Propiedad actualizada exitosamente");
    }

    @Override
    public void handle(DeletePropertyCommand command) {
        log.info("Eliminando propiedad con ID: {}", command.propertyId());

        if (!propertyRepository.existsById(command.propertyId())) {
            throw new IllegalArgumentException("Propiedad no encontrada con ID: " + command.propertyId());
        }

        propertyRepository.deleteById(command.propertyId());
        log.info("Propiedad eliminada exitosamente");
    }
}