package apx.inc.finance_web_services.client.interfaces.rest.transform;

import apx.inc.finance_web_services.client.domain.model.aggregates.Client;
import apx.inc.finance_web_services.client.interfaces.rest.resources.ClientResource;

public class ClientResourceFromEntityAssembler {

    public static ClientResource toResourceFromEntity(Client client) {
        return new ClientResource(
                client.getId(),
                client.getName(),
                client.getSurname(),
                client.getCivilState(),
                client.getEmail(),
                client.getPhone(),
                client.getDni(),
                client.getSalesManId(),
                client.getMonthlyIncome(),
                client.getMonthlyExpenses(),
                client.isHasPreviousStateHousing(),
                client.canApplyForBono()
        );
    }
}