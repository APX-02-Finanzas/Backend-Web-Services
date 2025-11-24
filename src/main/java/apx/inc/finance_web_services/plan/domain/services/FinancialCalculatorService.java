package apx.inc.finance_web_services.plan.domain.services;

import apx.inc.finance_web_services.plan.domain.model.aggregates.PaymentPlan;
import apx.inc.finance_web_services.plan.domain.model.entities.Installment;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.GracePeriodConfig;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.GracePeriodType;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.InterestRateConfig;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.PrepaymentConfig;

import java.util.List;
import java.util.stream.Collectors;

public interface FinancialCalculatorService {

    void generateInstallments(PaymentPlan paymentPlan,List<GracePeriodConfig> gracePeriods,List<PrepaymentConfig> prepayments) ;


    void calculateFinancialIndicators(PaymentPlan paymentPlan);

    // Métodos auxiliares privados
    Installment createDisbursementInstallment(PaymentPlan paymentPlan);

    Installment calculateInstallment(int installmentNumber, double previousBalance,PaymentPlan paymentPlan, GracePeriodType gracePeriodType,double prepaymentAmount);

    void calculateGracePeriodTotal(Installment installment, PaymentPlan paymentPlan);

    void calculateGracePeriodPartial(Installment installment, PaymentPlan paymentPlan);

    void calculateNormalInstallment(Installment installment, PaymentPlan paymentPlan);

    void calculateTotals(PaymentPlan paymentPlan);

    double calculateIRR(List<Double> cashFlows);

    double calculateTCEA(double tir, int frequency, int daysPerYear);

    double calculateNPV(List<Double> cashFlows, double discountRate);

    GracePeriodType getGracePeriodTypeForInstallment(int installmentNumber, List<GracePeriodConfig> gracePeriods);

    double getPrepaymentForInstallment(int installmentNumber, List<PrepaymentConfig> prepayments);

    void recalculatePaymentPlan(PaymentPlan paymentPlan,
                                List<GracePeriodConfig> gracePeriods,
                                List<PrepaymentConfig> prepayments,
                                List<InterestRateConfig> interestRateConfigs);
}
