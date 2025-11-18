package apx.inc.finance_web_services.client.application.acl;

import apx.inc.finance_web_services.client.domain.model.aggregates.Client;
import apx.inc.finance_web_services.client.domain.model.queries.GetClientByIdQuery;
import apx.inc.finance_web_services.client.domain.model.queries.GetClientsByUserIdQuery;
import apx.inc.finance_web_services.client.domain.services.ClientQueryService;
import apx.inc.finance_web_services.client.interfaces.acl.ClientContextFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientContextFacadeImpl implements ClientContextFacade {

    private final ClientQueryService clientQueryService;

    @Override
    public Optional<Client> fetchClientById(Long clientId) {
        if (clientId == null || clientId <= 0) {
            return Optional.empty();
        }
        try {
            var query = new GetClientByIdQuery(clientId);
            var result = clientQueryService.handle(query);
            return Optional.of(result);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Client> fetchClientsBySalesManId(Long salesManId) {
        if (salesManId == null || salesManId <= 0) {
            return List.of();
        }
        try {
            var query = new GetClientsByUserIdQuery(salesManId);
            return clientQueryService.handle(query);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public boolean validateClientForBono(Long clientId) {
        return fetchClientById(clientId)
                .map(Client::canApplyForBono)
                .orElse(false);
    }
}
