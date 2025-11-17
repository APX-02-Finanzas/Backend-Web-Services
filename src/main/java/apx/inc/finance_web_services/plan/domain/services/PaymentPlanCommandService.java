package apx.inc.finance_web_services.plan.domain.services;

import apx.inc.finance_web_services.plan.domain.model.commands.CreatePaymentPlanCommand;
import apx.inc.finance_web_services.plan.domain.model.commands.DeletePaymentPlanCommand;
import apx.inc.finance_web_services.plan.domain.model.commands.UpdatePaymentPlanCommand;

public interface PaymentPlanCommandService {

    Long handle(CreatePaymentPlanCommand command);

    /**
     * Recalcula un plan de pagos existente
     */
    void handle(UpdatePaymentPlanCommand command);

    /**
     * Elimina un plan de pagos
     */
    void handle(DeletePaymentPlanCommand command);

}
