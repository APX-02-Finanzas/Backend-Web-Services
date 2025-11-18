package apx.inc.finance_web_services.plan.domain.model.commands;

import apx.inc.finance_web_services.plan.domain.model.valueobjects.*;

import java.util.ArrayList;
import java.util.List;

public record CreatePaymentPlanCommand(

        // ✅ DATOS DE CONTEXTO (para validaciones)
        Long clientId,
        Long propertyId,
        Long salesManId,
        boolean applyBono,

        // Datos del préstamo
        //double assetSalePrice,           // Precio de Venta del Activo
        double downPaymentPercentage,    // % Cuota Inicial
        int years,                       // Nº de Años
        int paymentFrequency,            // Frecuencia de pago (días)
        int daysPerYear,                 // Nº de días por año

        // Costos iniciales
        double notarialCosts,            // Costes Notariales
        double registryCosts,            // Costes Registrales
        double appraisal,                // Tasación
        double studyCommission,          // Comisión de estudio
        double activationCommission,     // Comisión activación

        // Costos periódicos
        double periodicCommission,       // Comisión periodica
        double postage,                  // Portes
        double administrationFees,       // Gastos de Administración
        double creditLifeInsurance,      // % de Seguro desgravamen
        double riskInsurance,            // % de Seguro riesgo

        // Costo de oportunidad
        double discountRate,             // Tasa de descuento

        // Configuración
        //Currency currency,               // Moneda
        InterestRateType interestRateType, // Tipo de tasa

        // Periodos de gracia (opcional)
        List<GracePeriodConfig> gracePeriods,

        // Prepagos (opcional)
        List<PrepaymentConfig> prepayments,

        double annualInterestRate,
        List<InterestRateConfig> interestRateConfigs


) {
    public CreatePaymentPlanCommand {
//        if (assetSalePrice <= 0) {
//            throw new IllegalArgumentException("El precio de venta debe ser mayor a 0");
//        }
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
        if (notarialCosts < 0) {
            throw new IllegalArgumentException("Los costos notariales no pueden ser negativos");
        }
        if (registryCosts < 0) {
            throw new IllegalArgumentException("Los costos registrales no pueden ser negativos");
        }
        if (appraisal < 0) {
            throw new IllegalArgumentException("La tasación no puede ser negativa");
        }
        if (studyCommission < 0) {
            throw new IllegalArgumentException("La comisión de estudio no puede ser negativa");
        }
        if (activationCommission < 0) {
            throw new IllegalArgumentException("La comisión de activación no puede ser negativa");
        }
        if (periodicCommission < 0) {
            throw new IllegalArgumentException("La comisión periódica no puede ser negativa");
        }
        if (postage < 0) {
            throw new IllegalArgumentException("Los portes no pueden ser negativos");
        }
        if (administrationFees < 0) {
            throw new IllegalArgumentException("Los gastos de administración no pueden ser negativos");
        }
        if (creditLifeInsurance < 0) {
            throw new IllegalArgumentException("El seguro de desgravamen no puede ser negativo");
        }
        if (riskInsurance < 0) {
            throw new IllegalArgumentException("El seguro de riesgo no puede ser negativo");
        }
        if (discountRate < 0) {
            throw new IllegalArgumentException("La tasa de descuento no puede ser negativa");
        }
//        if (currency == null) {
//            throw new IllegalArgumentException("La moneda no puede ser nula");
//        }
        if (interestRateType == null) {
            throw new IllegalArgumentException("El tipo de tasa no puede ser nulo");
        }
        if (gracePeriods == null) {
            gracePeriods = new ArrayList<>();
        }
        if (prepayments == null) {
            prepayments = new ArrayList<>();
        }
    }
}
