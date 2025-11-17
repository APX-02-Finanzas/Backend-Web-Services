package apx.inc.finance_web_services.plan.domain.model.commands;

import apx.inc.finance_web_services.plan.domain.model.valueobjects.Currency;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.GracePeriodConfig;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.InterestRateType;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.PrepaymentConfig;

import java.util.List;

public record UpdatePaymentPlanCommand(
        Long paymentPlanId,

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
    public UpdatePaymentPlanCommand {
        if (paymentPlanId == null || paymentPlanId <= 0) {
            throw new IllegalArgumentException("El ID del plan de pagos debe ser mayor a 0");
        }
        if (assetSalePrice <= 0) {
            throw new IllegalArgumentException("El precio de venta del activo debe ser mayor a 0");
        }
        if (downPaymentPercentage < 0 || downPaymentPercentage > 100) {
            throw new IllegalArgumentException("El porcentaje de cuota inicial debe estar entre 0 y 100");
        }
        if (years <= 0) {
            throw new IllegalArgumentException("El número de años debe ser mayor a 0");
        }
        if (paymentFrequency <= 0) {
            throw new IllegalArgumentException("La frecuencia de pago debe ser mayor a 0");
        }
        if (daysPerYear <= 0) {
            throw new IllegalArgumentException("Los días por año deben ser mayor a 0");
        }
        if (notarialCosts<=0 || registryCosts<=0 || appraisal<=0 || studyCommission<=0 || activationCommission<=0 || periodicCommission<=0 || postage<=0 || administrationFees<=0 || creditLifeInsurance<=0 || riskInsurance<=0 || discountRate<=0) {
            throw new IllegalArgumentException("Los costos iniciales deben ser mayores a 0");
        }
        if (currency == null) {
            throw new IllegalArgumentException("La moneda no puede ser nula");
        }
        if (interestRateType == null) {
            throw new IllegalArgumentException("El tipo de tasa de interés no puede ser nulo");
        }
    }

    private static void validateNonNegative(String fieldName, double value) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " no puede ser negativo");
        }
    }
}