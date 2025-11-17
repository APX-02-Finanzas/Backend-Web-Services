package apx.inc.finance_web_services.plan.domain.model.commands;

public record DeletePaymentPlanCommand(
        Long paymentPlanId
) {
    public DeletePaymentPlanCommand {
        if (paymentPlanId == null || paymentPlanId <= 0) {
            throw new IllegalArgumentException("El ID del plan de pagos debe ser mayor a 0");
        }
    }
}