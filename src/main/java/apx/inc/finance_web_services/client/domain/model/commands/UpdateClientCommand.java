package apx.inc.finance_web_services.client.domain.model.commands;

public record UpdateClientCommand(
        Long clientId,  // Para identificar cuál actualizar
        String name,
        String email,
        String phone,
        String dni,
        double monthlyIncome,
        double monthlyExpenses
) {}