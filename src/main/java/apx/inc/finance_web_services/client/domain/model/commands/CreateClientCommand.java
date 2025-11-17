package apx.inc.finance_web_services.client.domain.model.commands;

public record CreateClientCommand(
        String name,
        String email,
        String phone,
        String dni,
        Long salesManId,
        double monthlyIncome,
        double monthlyExpenses
) {}
