package apx.inc.finance_web_services.plan.interfaces.rest.resources;

import apx.inc.finance_web_services.plan.domain.model.valueobjects.*;

import java.util.List;


public record CreatePaymentPlanResource(
        // ✅ DATOS DE CONTEXTO (igual que tu Command)
        Long clientId,
        Long propertyId,
        Long salesManId,

        // ✅ DATOS DEL PRÉSTAMO (igual que tu Command)
        double downPaymentPercentage,
        int years,
        int paymentFrequency,
        int daysPerYear,

        // ✅ COSTOS INICIALES (igual que tu Command)
        double notarialCosts,
        double registryCosts,
        double appraisal,
        double studyCommission,
        double activationCommission,

        // ✅ COSTOS PERIÓDICOS (igual que tu Command)
        double periodicCommission,
        double postage,
        double administrationFees,
        double creditLifeInsurance,
        double riskInsurance,

        // ✅ COSTO DE OPORTUNIDAD (igual que tu Command)
        double discountRate,

        // ✅ CONFIGURACIÓN (igual que tu Command)
        InterestRateType interestRateType,  // String desde frontend
        double annualInterestRate,
        List<InterestRateConfig> interestRateConfigs,

        // ✅ OPCIONALES (igual que tu Command)
        List<GracePeriodConfig> gracePeriods,
        List<PrepaymentConfig> prepayments


) {}
