package apx.inc.finance_web_services.plan.interfaces.rest.resources;

import apx.inc.finance_web_services.plan.domain.model.valueobjects.Currency;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.InterestRateType;

public record PaymentPlanResource(
        Long id,

        // ===== DATOS DE ENTRADA =====

        // ... del préstamo
        double assetSalePrice,           // Precio de Venta del Activo
        double downPaymentPercentage,    // % Cuota Inicial
        int years,                       // Nº de Años
        int paymentFrequency,            // Frecuencia de pago (días)
        int daysPerYear,                 // Nº de días por año
        Currency currency,
        InterestRateType interestRateType,

        // ... de los costes/gastos iniciales
        double notarialCosts,            // Costes Notariales
        double registryCosts,            // Costes Registrales
        double appraisal,                // Tasación
        double studyCommission,          // Comisión de estudio
        double activationCommission,     // Comisión activación

        // ... de los costes/gastos periódicos
        double periodicCommission,       // Comisión periódica
        double postage,                  // Portes
        double administrationFees,       // Gastos de Administración
        double creditLifeInsurance,      // % de Seguro desgravamen
        double riskInsurance,            // % de Seguro riesgo

        // ... del costo de oportunidad
        double discountRate,             // Tasa de descuento

        // ===== RESULTADOS CALCULADOS =====

        // ... del financiamiento
        double financedBalance,          // Saldo a financiar
        double loanAmount,               // Monto del préstamo
        int installmentsPerYear,         // Nº Cuotas por Año
        int totalInstallments,           // Nº Total de Cuotas

        // ... de los costes/gastos periódicos calculados
        double periodicCreditLifeInsurance, // % de Seguro desgrav. per.
        double periodicRiskInsurance,    // Seguro riesgo periódico

        // ===== TOTALES =====
        double totalInterest,            // Intereses
        double totalAmortization,        // Amortización del capital
        double totalCreditLifeInsurance, // Seguro de desgravamen
        double totalRiskInsurance,       // Seguro contra todo riesgo
        double totalCommissions,         // Comisiones periódicas
        double totalPostageAndFees,      // Portes / Gastos de adm.

        // ===== INDICADORES DE RENTABILIDAD =====
        double periodicDiscountRate,     // Tasa de descuento periódica
        double irr,                      // TIR de la operación
        double effectiveAnnualCostRate,  // TCEA de la operación
        double npv,                       // VAN operación

        // Extra fields
        Long clientId,
        Long propertyId,
        Long SalesManId,
        double bonusAmount
) {}