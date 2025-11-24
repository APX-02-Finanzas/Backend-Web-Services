package apx.inc.finance_web_services.client.interfaces.rest.resources;

import apx.inc.finance_web_services.client.domain.model.valueobjects.CivilState;

public record UpdateClientResource(
        String name,
        String surname,
        CivilState civilState,
        String email,
        String phone,
        String dni,
        double monthlyIncome,
        double monthlyExpenses
) {}