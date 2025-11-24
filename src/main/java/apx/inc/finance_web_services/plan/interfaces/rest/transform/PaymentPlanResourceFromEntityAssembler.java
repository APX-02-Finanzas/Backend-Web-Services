package apx.inc.finance_web_services.plan.interfaces.rest.transform;

import apx.inc.finance_web_services.plan.domain.model.aggregates.PaymentPlan;
import apx.inc.finance_web_services.plan.interfaces.rest.resources.PaymentPlanResource;

public class PaymentPlanResourceFromEntityAssembler {

    public static PaymentPlanResource toResourceFromEntity(PaymentPlan paymentPlan) {
        return new PaymentPlanResource(
                paymentPlan.getId(),

                // ===== DATOS DE ENTRADA =====
                // ... del préstamo
                paymentPlan.getAssetSalePrice(),
                paymentPlan.getDownPaymentPercentage(),
                paymentPlan.getYears(),
                paymentPlan.getPaymentFrequency(),
                paymentPlan.getDaysPerYear(),
                paymentPlan.getCurrency(),
                paymentPlan.getInterestRateType(),

                // ... de los costes/gastos iniciales
                paymentPlan.getNotarialCosts(),
                paymentPlan.getRegistryCosts(),
                paymentPlan.getAppraisal(),
                paymentPlan.getStudyCommission(),
                paymentPlan.getActivationCommission(),

                // ... de los costes/gastos periódicos
                paymentPlan.getPeriodicCommission(),
                paymentPlan.getPostage(),
                paymentPlan.getAdministrationFees(),
                paymentPlan.getCreditLifeInsurance(),
                paymentPlan.getRiskInsurance(),

                // ... del costo de oportunidad
                paymentPlan.getDiscountRate(),

                // ===== RESULTADOS CALCULADOS =====
                // ... del financiamiento
                paymentPlan.getFinancedBalance(),
                paymentPlan.getLoanAmount(),
                paymentPlan.getInstallmentsPerYear(),
                paymentPlan.getTotalInstallments(),

                // ... de los costes/gastos periódicos calculados
                paymentPlan.getPeriodicCreditLifeInsurance(),
                paymentPlan.getPeriodicRiskInsurance(),

                // ===== TOTALES =====
                paymentPlan.getTotalInterest(),
                paymentPlan.getTotalAmortization(),
                paymentPlan.getTotalCreditLifeInsurance(),
                paymentPlan.getTotalRiskInsurance(),
                paymentPlan.getTotalCommissions(),
                paymentPlan.getTotalPostageAndFees(),

                // ===== INDICADORES DE RENTABILIDAD =====
                paymentPlan.getPeriodicDiscountRate(),
                paymentPlan.getIrr(),
                paymentPlan.getEffectiveAnnualCostRate(),
                paymentPlan.getNpv(),

                // Extra fields
                paymentPlan.getClientId(),
                paymentPlan.getPropertyId(),
                paymentPlan.getSalesManId(),
                paymentPlan.getBonusAmount()
        );
    }
}