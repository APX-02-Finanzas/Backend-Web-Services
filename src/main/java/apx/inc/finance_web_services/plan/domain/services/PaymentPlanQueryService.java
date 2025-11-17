package apx.inc.finance_web_services.plan.domain.services;

import apx.inc.finance_web_services.plan.domain.model.aggregates.PaymentPlan;
import apx.inc.finance_web_services.plan.domain.model.entities.Installment;
import apx.inc.finance_web_services.plan.domain.model.queries.*;

import java.util.List;

public interface PaymentPlanQueryService {


    List<PaymentPlan> handle(GetAllPaymentPlansQuery query);

    /**
     * Obtiene un plan de pagos por ID
     */
    PaymentPlan handle(GetPaymentPlanByIdQuery query);



    /**
     * Obtiene las cuotas de un plan de pagos
     */
    List<Installment> handle(GetInstallmentsByPaymentPlanIdQuery query);

}
