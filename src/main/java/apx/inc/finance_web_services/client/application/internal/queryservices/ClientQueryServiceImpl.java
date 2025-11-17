package apx.inc.finance_web_services.client.application.internal.queryservices;

import apx.inc.finance_web_services.client.domain.model.aggregates.Client;
import apx.inc.finance_web_services.client.domain.model.queries.GetAllClientsQuery;
import apx.inc.finance_web_services.client.domain.model.queries.GetClientByIdQuery;
import apx.inc.finance_web_services.client.domain.model.queries.GetClientsByUserIdQuery;
import apx.inc.finance_web_services.client.domain.services.ClientQueryService;
import apx.inc.finance_web_services.client.infrastructure.persistence.jpa.repositories.ClientRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientQueryServiceImpl implements ClientQueryService {

    private final ClientRepository clientRepository;

    @Override
    public Client handle(GetClientByIdQuery query) {
        return clientRepository.findById(query.clientId())
                .orElseThrow(() -> new IllegalArgumentException("Client not found with id: " + query.clientId()));
    }

    @Override
    public List<Client> handle(GetAllClientsQuery query) {
        return clientRepository.findAll();
    }

    @Override
    public List<Client> handle(GetClientsByUserIdQuery query) {
        return clientRepository.findBySalesManId(query.salesManId());  // ✅ Filtro por salesman
    }
}
