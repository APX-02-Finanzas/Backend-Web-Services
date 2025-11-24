package apx.inc.finance_web_services.client.domain.model.commands;

import apx.inc.finance_web_services.client.domain.model.valueobjects.CivilState;

public record UpdateClientCommand(
        Long clientId,  // Para identificar cuál actualizar
        String name,
        String surname,
        CivilState civilState,
        String email,
        String phone,
        String dni,
        double monthlyIncome,
        double monthlyExpenses
) {}