package apx.inc.finance_web_services.plan.interfaces.rest.resources;

import apx.inc.finance_web_services.plan.domain.model.valueobjects.Currency;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.GracePeriodConfig;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.InterestRateType;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.PrepaymentConfig;

import java.util.List;

public record UpdatePaymentPlanResource(
        // Datos básicos del préstamo
        double assetSalePrice,
        double downPaymentPercentage,
        int years,
        int paymentFrequency,
        int daysPerYear,
        // Costos iniciales
        double notarialCosts,
        double registryCosts,
        double appraisal,
        double studyCommission,
        double activationCommission,
        // Costos periódicos
        double periodicCommission,
        double postage,
        double administrationFees,
        double creditLifeInsurance,
        double riskInsurance,
        // Costo oportunidad
        double discountRate,
        // Configuración
        Currency currency,
        InterestRateType interestRateType,
        // Periodos de gracia (opcional)
        List<GracePeriodConfig> gracePeriods,
        // Prepagos (opcional)
        List<PrepaymentConfig> prepayments
) {
}