package apx.inc.finance_web_services.plan.interfaces.rest.transform;

import apx.inc.finance_web_services.plan.domain.model.commands.UpdatePaymentPlanCommand;
import apx.inc.finance_web_services.plan.interfaces.rest.resources.UpdatePaymentPlanResource;

public class UpdatePaymentPlanCommandFromResourceAssembler {

    public static UpdatePaymentPlanCommand toCommandFromResource(Long paymentPlanId, UpdatePaymentPlanResource resource) {
        return new UpdatePaymentPlanCommand(
                paymentPlanId,
                // Datos básicos del préstamo
                //resource.assetSalePrice(),
                resource.downPaymentPercentage(),
                resource.years(),
                resource.paymentFrequency(),
                resource.daysPerYear(),
                // Costos iniciales
                resource.notarialCosts(),
                resource.registryCosts(),
                resource.appraisal(),
                resource.studyCommission(),
                resource.activationCommission(),
                // Costos periódicos
                resource.periodicCommission(),
                resource.postage(),
                resource.administrationFees(),
                resource.creditLifeInsurance(),
                resource.riskInsurance(),
                // Costo oportunidad
                resource.discountRate(),

                // Configuración
                resource.interestRateType(),
                // Periodos de gracia (opcional)
                resource.annualInterestRate(),
                resource.interestRateConfigs(),
                resource.gracePeriods(),
                // Prepagos (opcional)
                resource.prepayments()
        );
    }
}