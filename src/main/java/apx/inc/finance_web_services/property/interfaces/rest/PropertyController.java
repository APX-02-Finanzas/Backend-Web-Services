package apx.inc.finance_web_services.property.interfaces.rest;

import apx.inc.finance_web_services.property.domain.model.aggregates.Property;
import apx.inc.finance_web_services.property.domain.model.commands.CreatePropertyCommand;
import apx.inc.finance_web_services.property.domain.model.commands.DeletePropertyCommand;
import apx.inc.finance_web_services.property.domain.model.commands.UpdatePropertyCommand;
import apx.inc.finance_web_services.property.domain.model.queries.GetAllPropertiesQuery;
import apx.inc.finance_web_services.property.domain.model.queries.GetPropertiesByUserIdQuery;
import apx.inc.finance_web_services.property.domain.model.queries.GetPropertyByIdQuery;
import apx.inc.finance_web_services.property.domain.services.PropertyCommandService;
import apx.inc.finance_web_services.property.domain.services.PropertyQueryService;
import apx.inc.finance_web_services.property.interfaces.rest.resources.CreatePropertyResource;
import apx.inc.finance_web_services.property.interfaces.rest.resources.PropertyResource;
import apx.inc.finance_web_services.property.interfaces.rest.resources.UpdatePropertyResource;
import apx.inc.finance_web_services.property.interfaces.rest.transform.CreatePropertyCommandFromResourceAssembler;
import apx.inc.finance_web_services.property.interfaces.rest.transform.PropertyResourceFromEntityAssembler;
import apx.inc.finance_web_services.property.interfaces.rest.transform.UpdatePropertyCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/properties")
@RequiredArgsConstructor
@Tag(name = "Properties", description = "Property Management Endpoints")
public class PropertyController {

    private final PropertyCommandService propertyCommandService;
    private final PropertyQueryService propertyQueryService;

    @PostMapping
    public ResponseEntity<PropertyResource> createProperty(@RequestBody CreatePropertyResource resource) {
        CreatePropertyCommand command = CreatePropertyCommandFromResourceAssembler.toCommandFromResource(resource);
        Long propertyId = propertyCommandService.handle(command);

        Optional<Property> property = propertyQueryService.handle(new GetPropertyByIdQuery(propertyId));
        return property.map(value -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(PropertyResourceFromEntityAssembler.toResourceFromEntity(value)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{propertyId}")
    public ResponseEntity<PropertyResource> updateProperty(@PathVariable Long propertyId,
                                                           @RequestBody UpdatePropertyResource resource) {
        UpdatePropertyCommand command = UpdatePropertyCommandFromResourceAssembler.toCommandFromResource(propertyId, resource);
        propertyCommandService.handle(command);

        Optional<Property> property = propertyQueryService.handle(new GetPropertyByIdQuery(propertyId));
        return property.map(value -> ResponseEntity.ok(PropertyResourceFromEntityAssembler.toResourceFromEntity(value)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long propertyId) {
        try {
            propertyCommandService.handle(new DeletePropertyCommand(propertyId));
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<PropertyResource> getPropertyById(@PathVariable Long propertyId) {
        Optional<Property> property = propertyQueryService.handle(new GetPropertyByIdQuery(propertyId));
        return property.map(value -> ResponseEntity.ok(PropertyResourceFromEntityAssembler.toResourceFromEntity(value)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PropertyResource>> getAllProperties() {
        List<Property> properties = propertyQueryService.handle(new GetAllPropertiesQuery());
        List<PropertyResource> resources = properties.stream()
                .map(PropertyResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/salesman/{salesManId}")
    public ResponseEntity<List<PropertyResource>> getPropertiesBySalesManId(@PathVariable Long salesManId) {
        List<Property> properties = propertyQueryService.handle(new GetPropertiesByUserIdQuery(salesManId));
        List<PropertyResource> resources = properties.stream()
                .map(PropertyResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }
}
