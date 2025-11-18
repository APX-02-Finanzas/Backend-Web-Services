package apx.inc.finance_web_services.client.interfaces.rest.resources;

public record UpdateClientResource(
        String name,
        String email,
        String phone,
        String dni,
        double monthlyIncome,
        double monthlyExpenses
) {}