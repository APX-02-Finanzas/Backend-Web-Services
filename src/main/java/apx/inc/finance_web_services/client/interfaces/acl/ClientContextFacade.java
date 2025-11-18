package apx.inc.finance_web_services.client.interfaces.acl;

import apx.inc.finance_web_services.client.domain.model.aggregates.Client;
import java.util.List;
import java.util.Optional;

public interface ClientContextFacade {
    Optional<Client> fetchClientById(Long clientId);
    List<Client> fetchClientsBySalesManId(Long salesManId);
    boolean validateClientForBono(Long clientId);
}