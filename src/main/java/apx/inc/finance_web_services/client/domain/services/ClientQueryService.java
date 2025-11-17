package apx.inc.finance_web_services.client.domain.services;

import apx.inc.finance_web_services.client.domain.model.aggregates.Client;
import apx.inc.finance_web_services.client.domain.model.queries.GetAllClientsQuery;
import apx.inc.finance_web_services.client.domain.model.queries.GetClientByIdQuery;
import apx.inc.finance_web_services.client.domain.model.queries.GetClientsByUserIdQuery;

import java.util.List;

public interface ClientQueryService {

    Client handle(GetClientByIdQuery query);

    List<Client> handle(GetAllClientsQuery query);


    List<Client> handle(GetClientsByUserIdQuery query);
}
