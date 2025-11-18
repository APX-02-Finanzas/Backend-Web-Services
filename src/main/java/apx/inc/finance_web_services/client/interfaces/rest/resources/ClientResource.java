package apx.inc.finance_web_services.client.interfaces.rest.resources;

import java.time.LocalDateTime;

public record ClientResource(
        Long id,
        String name,
        String email,
        String phone,
        String dni,
        Long salesManId,
        double monthlyIncome,
        double monthlyExpenses,
        boolean hasPreviousStateHousing,
        boolean canApplyForBono
) {}