package apx.inc.finance_web_services.client.interfaces.rest.resources;

import apx.inc.finance_web_services.client.domain.model.valueobjects.CivilState;

public record CreateClientResource(
        String name,
        String surname,
        CivilState civilState,
        String email,
        String phone,
        String dni,
        Long salesManId,
        double monthlyIncome,
        double monthlyExpenses
) {}