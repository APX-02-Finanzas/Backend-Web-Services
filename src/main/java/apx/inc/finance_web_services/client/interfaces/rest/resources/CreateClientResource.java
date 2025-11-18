package apx.inc.finance_web_services.client.interfaces.rest.resources;

public record CreateClientResource(
        String name,
        String email,
        String phone,
        String dni,
        Long salesManId,
        double monthlyIncome,
        double monthlyExpenses
) {}