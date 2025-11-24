package apx.inc.finance_web_services.plan.interfaces.rest.resources;

import apx.inc.finance_web_services.plan.domain.model.valueobjects.*;

import java.util.List;

public record UpdatePaymentPlanResource(
        // Datos básicos del préstamo
        //double assetSalePrice,
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
        InterestRateType interestRateType,
        double annualInterestRate,
        List<InterestRateConfig> interestRateConfigs,
        // Periodos de gracia (opcional)
        List<GracePeriodConfig> gracePeriods,
        // Prepagos (opcional)
        List<PrepaymentConfig> prepayments
) {
}