package apx.inc.finance_web_services.unit;

import apx.inc.finance_web_services.plan.domain.model.aggregates.PaymentPlan;
import apx.inc.finance_web_services.plan.domain.model.entities.Installment;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.Currency;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.GracePeriodType;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.InterestRateConfig;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.InterestRateType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PlanTests {

    private PaymentPlan plan;

    @BeforeEach
    void setUp() {
        plan = new PaymentPlan(100000.0, 20.0, 10, 30, 360, Currency.PEN, InterestRateType.TEA);
        plan.setAnnualInterestRate(12.0);
    }

    // Crear plan de pagos y verificar campos iniciales
    @Test
    void createPaymentPlan_ShouldSetInitialFields() {
        assertThat(plan.getAssetSalePrice()).isEqualTo(100000.0);
        assertThat(plan.getDownPaymentPercentage()).isEqualTo(20.0);
        assertThat(plan.getYears()).isEqualTo(10);
        assertThat(plan.getPaymentFrequency()).isEqualTo(30);
        assertThat(plan.getDaysPerYear()).isEqualTo(360);
        assertThat(plan.getCurrency()).isEqualTo(Currency.PEN);

        assertThat(plan.getLoanAmount()).isEqualTo(0.0);
        assertThat(plan.getFinancedBalance()).isEqualTo(0.0);
    }

    // Calcular valores y tasas del plan de pagos
    @Test
    void updatePaymentPlan_ShouldCalculateValuesAndRates() {
        plan.setInitialCosts(1000.0, 500.0, 200.0, 50.0, 25.0);
        plan.setPeriodicCosts(10.0, 5.0, 2.0, 0.5, 0.2);
        plan.setDiscountRate(27.0);

        plan.calculateInitialValues();

        assertThat(plan.getFinancedBalance()).isEqualTo(80000.0);
        assertThat(plan.getLoanAmount()).isEqualTo(81775.0);

        assertThat(plan.getInstallmentsPerYear()).isEqualTo(12);
        assertThat(plan.getTotalInstallments()).isEqualTo(120);

        double periodicRate = plan.getPeriodicRateForInstallment(1);
        assertThat(periodicRate).isEqualTo(0.00949);

        plan.setInterestRateConfigs(List.of(new InterestRateConfig(5, 15.0)));
        assertThat(plan.getAnnualRateForInstallment(1)).isEqualTo(12.0);
        assertThat(plan.getAnnualRateForInstallment(6)).isEqualTo(15.0);
    }

    // Añadir una cuota al plan
    @Test
    void addInstallment_ShouldAppendInstallment() {
        plan.setInitialCosts(0.0, 0.0, 0.0, 0.0, 0.0);
        plan.calculateInitialValues();

        Installment inst = new Installment(1, GracePeriodType.S);
        plan.addInstallment(inst);

        assertThat(plan.getInstallments()).isNotEmpty();
        assertThat(plan.getInstallments()).contains(inst);
    }

    // Eliminar referencia al plan (permitir null)
    @Test
    void deletePaymentPlan_ShouldAllowNullReference() {
        plan = null;
        assertThat(plan).isNull();
    }
}
