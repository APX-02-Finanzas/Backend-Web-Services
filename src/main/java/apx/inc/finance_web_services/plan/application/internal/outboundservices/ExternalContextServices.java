package apx.inc.finance_web_services.plan.application.internal.outboundservices.acl;

import apx.inc.finance_web_services.client.domain.model.aggregates.Client;
import apx.inc.finance_web_services.client.interfaces.acl.ClientContextFacade;
import apx.inc.finance_web_services.iam.domain.model.aggregates.User;
import apx.inc.finance_web_services.iam.interfaces.acl.IamContextFacade;
import apx.inc.finance_web_services.property.domain.model.aggregates.Property;
import apx.inc.finance_web_services.property.interfaces.acl.PropertyContextFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalContextServices {

    private final ClientContextFacade clientContextFacade;
    private final PropertyContextFacade propertyContextFacade;
    private final IamContextFacade iamContextFacade;

    // === CLIENT SERVICES ===
    public Optional<Client> getClientById(Long clientId) {
        log.debug("Buscando cliente por ID: {}", clientId);
        return clientContextFacade.fetchClientById(clientId);
    }

    public boolean validateClientForBono(Long clientId) {
        log.debug("Validando cliente para bono: {}", clientId);
        return clientContextFacade.validateClientForBono(clientId);
    }

    public Optional<Long> getClientSalesManId(Long clientId) {
        return getClientById(clientId)
                .map(Client::getSalesManId);
    }

    // === PROPERTY SERVICES ===
    public Optional<Property> getPropertyById(Long propertyId) {
        log.debug("Buscando propiedad por ID: {}", propertyId);
        return propertyContextFacade.fetchPropertyById(propertyId);
    }

    public boolean validatePropertyForBono(Long propertyId) {
        log.debug("Validando propiedad para bono: {}", propertyId);
        return propertyContextFacade.validatePropertyForBono(propertyId);
    }

    public Optional<Double> getPropertyPrice(Long propertyId) {
        return getPropertyById(propertyId)
                .map(Property::getPrice);
    }

    public Optional<String> getPropertyCurrency(Long propertyId) {
        return getPropertyById(propertyId)
                .map(property -> property.getCurrency().name());
    }

    public Optional<Long> getPropertySalesManId(Long propertyId) {
        return getPropertyById(propertyId)
                .map(Property::getSalesManId);
    }

    // === USER/IAM SERVICES ===
    public Optional<User> getUserById(Long userId) {
        log.debug("Buscando usuario por ID: {}", userId);
        return iamContextFacade.fetchUserById(userId);
    }

    public boolean validateSalesManExists(Long salesManId) {
        log.debug("Validando existencia de salesman: {}", salesManId);
        return iamContextFacade.fetchUserById(salesManId).isPresent();
    }

    // === VALIDACIÓN COMBINADA ===
    public boolean validateClientAndPropertyRelationship(Long clientId, Long propertyId) {
        log.debug("Validando relación cliente-propiedad: Client={}, Property={}", clientId, propertyId);

        Optional<Long> clientSalesManId = getClientSalesManId(clientId);
        Optional<Long> propertySalesManId = getPropertySalesManId(propertyId);

        if (clientSalesManId.isEmpty() || propertySalesManId.isEmpty()) {
            return false;
        }

        // Validar que ambos pertenecen al mismo salesman
        return clientSalesManId.get().equals(propertySalesManId.get());
    }

    public Optional<Long> getCommonSalesManId(Long clientId, Long propertyId) {
        Optional<Long> clientSalesManId = getClientSalesManId(clientId);
        Optional<Long> propertySalesManId = getPropertySalesManId(propertyId);

        if (clientSalesManId.isPresent() && propertySalesManId.isPresent() &&
                clientSalesManId.get().equals(propertySalesManId.get())) {
            return clientSalesManId;
        }

        return Optional.empty();
    }
}