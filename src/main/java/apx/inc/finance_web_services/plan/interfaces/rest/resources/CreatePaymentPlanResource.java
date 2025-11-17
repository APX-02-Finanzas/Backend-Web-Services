package apx.inc.finance_web_services.plan.interfaces.rest.resources;

import apx.inc.finance_web_services.plan.domain.model.valueobjects.*;

import java.util.List;

public record CreatePaymentPlanResource(
        // Datos del préstamo
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
        List<PrepaymentConfig> prepayments,

        double annualInterestRate,
        List<InterestRateConfig> interestRateConfigs
) {}
