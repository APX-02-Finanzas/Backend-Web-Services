package apx.inc.finance_web_services.plan.interfaces.rest.resources;

import apx.inc.finance_web_services.plan.domain.model.valueobjects.GracePeriodType;

public record InstallmentResource(
        int number,
        double annualEffectiveRate,
        double periodEffectiveRate,
        double annualInflation,
        double periodInflation,
        GracePeriodType gracePeriodType,
        double initialBalance,
        double indexedInitialBalance,
        double interest,
        double installmentAmount,
        double amortization,
        double prepayment,
        double creditLifeInsurance,
        double riskInsurance,
        double commission,
        double postage,
        double adminFees,
        double finalBalance,
        double cashFlow
) {}