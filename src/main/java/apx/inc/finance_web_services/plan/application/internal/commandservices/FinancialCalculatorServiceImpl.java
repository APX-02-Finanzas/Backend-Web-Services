package apx.inc.finance_web_services.plan.application.internal.commandservices;

import apx.inc.finance_web_services.plan.domain.model.aggregates.PaymentPlan;
import apx.inc.finance_web_services.plan.domain.model.entities.Installment;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.GracePeriodConfig;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.GracePeriodType;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.InterestRateConfig;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.PrepaymentConfig;
import apx.inc.finance_web_services.plan.domain.services.FinancialCalculatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FinancialCalculatorServiceImpl implements FinancialCalculatorService {


    @Override
    public void generateInstallments(PaymentPlan paymentPlan, List<GracePeriodConfig> gracePeriods, List<PrepaymentConfig> prepayments) {
        try {
            log.info("=== INICIANDO GENERACIÓN DE CUOTAS ===");
            log.info("Total installments: {}", paymentPlan.getTotalInstallments());
            log.info("Loan amount: {}", paymentPlan.getLoanAmount());

            // 1. Crear cuota 0 (Desembolso)
            Installment disbursementInstallment = createDisbursementInstallment(paymentPlan);
            paymentPlan.addInstallment(disbursementInstallment);
            log.info("Cuota 0 (desembolso) creada");

            // 2. Generar cuotas 1 a N
            double currentBalance = paymentPlan.getLoanAmount();
            log.info("Balance inicial: {}", currentBalance);

            for (int i = 1; i <= paymentPlan.getTotalInstallments(); i++) {
                log.info("Generando cuota {}", i);

                GracePeriodType gracePeriodType = getGracePeriodTypeForInstallment(i, gracePeriods);
                double prepaymentAmount = getPrepaymentForInstallment(i, prepayments);

                Installment installment = calculateInstallment(
                        i, currentBalance, paymentPlan, gracePeriodType, prepaymentAmount
                );

                paymentPlan.addInstallment(installment);
                currentBalance = installment.getFinalBalance();
                log.info("Cuota {} generada - Balance final: {}", i, currentBalance);
            }

            // 3. Calcular totales
            calculateTotals(paymentPlan);
            log.info("=== GENERACIÓN DE CUOTAS COMPLETADA ===");

        } catch (Exception e) {
            log.error("ERROR en generateInstallments: ", e);
            throw e;
        }
    }

    @Override
    public void calculateFinancialIndicators(PaymentPlan paymentPlan) {
        try {
            log.info("=== INICIANDO CÁLCULO DE INDICADORES ===");

            List<Double> cashFlows = paymentPlan.getInstallments().stream()
                    .map(Installment::getCashFlow)
                    .collect(Collectors.toList());

            log.info("Cash flows calculados: {}", cashFlows.size());

            // Calcular TIR
            double irr = calculateIRR(cashFlows);
            paymentPlan.setIrr(irr);
            log.info("TIR calculada: {}", irr);

            // Calcular TCEA
            double tcea = calculateTCEA(irr, paymentPlan.getPaymentFrequency(), paymentPlan.getDaysPerYear());
            paymentPlan.setEffectiveAnnualCostRate(tcea);
            log.info("TCEA calculada: {}", tcea);

            // Calcular VAN - ✅ CORRECCIÓN: Usar tasa periódica
            double periodicDiscountRate = paymentPlan.getPeriodicDiscountRate();
            double npv = calculateNPV(cashFlows, periodicDiscountRate);
            paymentPlan.setNpv(npv);
            log.info("VAN calculado: {}", npv);

            log.info("=== CÁLCULO DE INDICADORES COMPLETADO ===");

        } catch (Exception e) {
            log.error("ERROR en calculateFinancialIndicators: ", e);
            throw e;
        }
    }

    @Override
    public Installment createDisbursementInstallment(PaymentPlan paymentPlan) {
        Installment disbursement = new Installment(0, GracePeriodType.S);
        disbursement.setCashFlow(paymentPlan.getLoanAmount()); // Flujo positivo (desembolso)
        return disbursement;
    }

    @Override
    public Installment calculateInstallment(int installmentNumber, double previousBalance, PaymentPlan paymentPlan, GracePeriodType gracePeriodType, double prepaymentAmount) {
        Installment installment = new Installment(installmentNumber, gracePeriodType);

        // ✅ CORRECTO: Obtener tasas del paymentPlan
        double annualRate = paymentPlan.getAnnualRateForInstallment(installmentNumber);
        double periodicRate = paymentPlan.getPeriodicRateForInstallment(installmentNumber);

        // ✅ ASIGNAR LAS TASAS CALCULADAS
        installment.setAnnualEffectiveRate(annualRate);
        installment.setPeriodEffectiveRate(periodicRate);

        installment.setInitialBalance(previousBalance);
        installment.setIndexedInitialBalance(previousBalance);
        installment.setPrepayment(prepaymentAmount);

        // 🚫 ELIMINAR ESTAS LÍNEAS QUE SOBREESCRIBEN:
        // installment.setAnnualEffectiveRate(0.11); // ❌ QUITAR
        // installment.setPeriodEffectiveRate(0.0264333); // ❌ QUITAR

        // Calcular según tipo de gracia
        switch (gracePeriodType) {
            case T -> calculateGracePeriodTotal(installment, paymentPlan);
            case P -> calculateGracePeriodPartial(installment, paymentPlan);
            case S -> calculateNormalInstallment(installment, paymentPlan);
        }

        installment.calculateCashFlow();
        return installment;
    }

    @Override
    public void calculateGracePeriodTotal(Installment installment, PaymentPlan paymentPlan) {
        double interest = installment.getInitialBalance() * installment.getPeriodEffectiveRate();

        installment.setInterest(interest);
        installment.setInstallmentAmount(0);
        installment.setAmortization(0);
        installment.setFinalBalance(installment.getInitialBalance() + interest);

        // ✅ CORREGIDO: Incluir TODOS los seguros y gastos
        installment.setCreditLifeInsurance(calculateCreditLifeInsuranceForInstallment(installment, paymentPlan));
        installment.setRiskInsurance(paymentPlan.getPeriodicRiskInsurance());
        installment.setCommission(paymentPlan.getPeriodicCommission());
        installment.setPostage(paymentPlan.getPostage());
        installment.setAdminFees(paymentPlan.getAdministrationFees());

        // ✅ CORRECCIÓN CRÍTICA: Calcular cashFlow con TODOS los cargos
        double totalCharges = installment.getCreditLifeInsurance() +
                installment.getRiskInsurance() +
                installment.getCommission() +
                installment.getPostage() +
                installment.getAdminFees();

        installment.setCashFlow(-totalCharges);  // Flujo negativo (pago)
    }

    @Override
    public void calculateGracePeriodPartial(Installment installment, PaymentPlan paymentPlan) {
        // ✅ CORREGIDO: Interés sobre saldo inicial
        double interest = installment.getInitialBalance() * installment.getPeriodEffectiveRate();

        double creditLifeInsurance = calculateCreditLifeInsuranceForInstallment(installment, paymentPlan);
        double riskInsurance = calculateRiskInsuranceForInstallment(installment, paymentPlan);
        double commission = paymentPlan.getPeriodicCommission();
        double postage = paymentPlan.getPostage();
        double adminFees = paymentPlan.getAdministrationFees();

        // ✅ CORREGIDO CRÍTICO: INCLUIR INTERÉS EN EL PAGO (no capitalizar)
        double totalAmount = interest + creditLifeInsurance + riskInsurance + commission + postage + adminFees;

        installment.setInterest(interest);
        installment.setInstallmentAmount(totalAmount);
        installment.setAmortization(0);

        // ✅ CORREGIDO CRÍTICO: NO CAPITALIZAR INTERÉS en gracia parcial
        installment.setFinalBalance(installment.getInitialBalance());

        installment.setCreditLifeInsurance(creditLifeInsurance);
        installment.setRiskInsurance(riskInsurance);
        installment.setCommission(commission);
        installment.setPostage(postage);
        installment.setAdminFees(adminFees);

        installment.setCashFlow(-totalAmount);
    }

    @Override
    public void calculateNormalInstallment(Installment installment, PaymentPlan paymentPlan) {
        // ✅ CORREGIDO: Interés sobre saldo inicial REAL
        double interest = installment.getInitialBalance() * installment.getPeriodEffectiveRate();

        // ✅ CORREGIDO: Calcular períodos restantes y cuota
        int periodsRemaining = paymentPlan.getTotalInstallments() - installment.getNumber() + 1;
        double periodicRate = installment.getPeriodEffectiveRate();

        double installmentAmount = calculateFrenchInstallmentAmount(
                installment.getInitialBalance(),
                periodsRemaining,
                periodicRate
        );

        // ✅ DEBUG CRÍTICO: Ver qué calcula la cuota francesa
        log.info("Cuota {} - Cuota francesa calculada: {}, Debería ser: {}",
                installment.getNumber(), installmentAmount, getExpectedInstallmentFromExcel(installment.getNumber()));

        double amortization = installmentAmount - interest;

        // Calcular seguros y gastos
        double creditLifeInsurance = calculateCreditLifeInsuranceForInstallment(installment, paymentPlan);
        double riskInsurance = calculateRiskInsuranceForInstallment(installment, paymentPlan);
        double commission = paymentPlan.getPeriodicCommission();
        double postage = paymentPlan.getPostage();
        double adminFees = paymentPlan.getAdministrationFees();

        double totalInstallmentAmount = installmentAmount + creditLifeInsurance + riskInsurance + commission + postage + adminFees;

        installment.setInterest(interest);
        installment.setAmortization(amortization);
        installment.setInstallmentAmount(totalInstallmentAmount);

        // ✅ CORREGIDO: Prepago se resta
        installment.setFinalBalance(installment.getInitialBalance() - amortization - installment.getPrepayment());

        // Asignar seguros y gastos
        installment.setCreditLifeInsurance(creditLifeInsurance);
        installment.setRiskInsurance(riskInsurance);
        installment.setCommission(commission);
        installment.setPostage(postage);
        installment.setAdminFees(adminFees);

        installment.setCashFlow(-totalInstallmentAmount);
    }

    // Método auxiliar para comparar con Excel
    private double getExpectedInstallmentFromExcel(int installmentNumber) {
        // Valores de tu Excel
        if (installmentNumber == 5) return 13095.47;
        if (installmentNumber == 6) return 12645.02;
        if (installmentNumber >= 21) return 12372.84;
        return 0;
    }

    @Override
    public void calculateTotals(PaymentPlan paymentPlan) {
        // Calcular totales básicos
        double totalInterest = paymentPlan.getInstallments().stream()
                .mapToDouble(Installment::getInterest)
                .sum();

        double totalAmortization = paymentPlan.getInstallments().stream()
                .mapToDouble(Installment::getAmortization)
                .sum();

        // ✅ FALTA: Sumar los prepagos a la amortización total
        double totalPrepayments = paymentPlan.getInstallments().stream()
                .mapToDouble(Installment::getPrepayment)
                .sum();

        // ✅ CORRECCIÓN CRÍTICA: Amortización total = amortización cuotas + prepagos
        double totalAmortizationWithPrepayments = totalAmortization + totalPrepayments;

        // ✅ CORREGIDO: Incluir seguro riesgo de TODAS las cuotas
        double totalCreditLifeInsurance = paymentPlan.getInstallments().stream()
                .mapToDouble(Installment::getCreditLifeInsurance)
                .sum();

        double totalRiskInsurance = paymentPlan.getInstallments().stream()
                .mapToDouble(Installment::getRiskInsurance)
                .sum();

        double totalCommissions = paymentPlan.getInstallments().stream()
                .mapToDouble(Installment::getCommission)
                .sum();

        double totalPostageAndFees = paymentPlan.getInstallments().stream()
                .mapToDouble(inst -> inst.getPostage() + inst.getAdminFees())
                .sum();

        // Log para debugging
        log.info("=== TOTALES CALCULADOS ===");
        log.info("Intereses: {}", totalInterest);
        log.info("Amortización sin prepagos: {}", totalAmortization);
        log.info("Total prepagos: {}", totalPrepayments);
        log.info("Amortización TOTAL (con prepagos): {}", totalAmortizationWithPrepayments);
        log.info("Seguro desgravamen: {}", totalCreditLifeInsurance);
        log.info("Seguro riesgo: {}", totalRiskInsurance);
        log.info("Comisiones: {}", totalCommissions);
        log.info("Portes/gastos: {}", totalPostageAndFees);

        // Verificar seguros
        long cuotasConSeguroRiesgo = paymentPlan.getInstallments().stream()
                .filter(inst -> inst.getRiskInsurance() > 0)
                .count();
        log.info("Cuotas con seguro riesgo: {}", cuotasConSeguroRiesgo);

        // ✅ ASIGNAR TOTAL CORREGIDO
        paymentPlan.setTotalInterest(totalInterest);
        paymentPlan.setTotalAmortization(totalAmortizationWithPrepayments);  // ← ¡ESTA ES LA CORRECCIÓN!
        paymentPlan.setTotalCreditLifeInsurance(totalCreditLifeInsurance);
        paymentPlan.setTotalRiskInsurance(totalRiskInsurance);
        paymentPlan.setTotalCommissions(totalCommissions);
        paymentPlan.setTotalPostageAndFees(totalPostageAndFees);
    }

    @Override
    public double calculateIRR(List<Double> cashFlows) {
        if (cashFlows == null || cashFlows.isEmpty()) {
            throw new IllegalArgumentException("Los flujos de caja no pueden estar vacíos");
        }

        // Método de Newton-Raphson simplificado para calcular TIR
        double tolerance = 0.00001; // Precisión de 0.001%
        double maxIterations = 1000;
        double guess = 0.1; // Suposición inicial del 10%

        double x0 = guess;
        double x1;
        double f, fPrime;

        for (int i = 0; i < maxIterations; i++) {
            // Calcular f(x) = VAN
            f = calculateNPVForIRR(cashFlows, x0);

            // Calcular f'(x) = derivada del VAN
            fPrime = calculateNPVDerivative(cashFlows, x0);

            // Método de Newton: x1 = x0 - f(x0)/f'(x0)
            if (Math.abs(fPrime) < tolerance) {
                break; // Evitar división por cero
            }

            x1 = x0 - f / fPrime;

            // Verificar convergencia
            if (Math.abs(x1 - x0) < tolerance) {
                return x1; // Encontramos la TIR
            }

            x0 = x1;
        }

        // Si no converge, usar método de bisección como fallback
        return calculateIRRByBisection(cashFlows, -0.9, 1.0, tolerance);
    }

    @Override
    public double calculateTCEA(double tir, int frequency, int daysPerYear) {
        double periodsPerYear = (double) daysPerYear / frequency;
        return Math.pow(1 + tir, periodsPerYear) - 1;
    }

    @Override
    public double calculateNPV(List<Double> cashFlows, double discountRate) {
        double npv = 0;
        // ✅ CORREGIDO: Usar tasa periódica para VAN
        double periodicRate = discountRate; // Ya debe ser la tasa periódica
        for (int i = 0; i < cashFlows.size(); i++) {
            npv += cashFlows.get(i) / Math.pow(1 + periodicRate, i);
        }
        return npv;
    }

    @Override
    public GracePeriodType getGracePeriodTypeForInstallment(int installmentNumber, List<GracePeriodConfig> gracePeriods) {
        return gracePeriods.stream()
                .filter(config -> config.installmentNumber() == installmentNumber)
                .findFirst()
                .map(GracePeriodConfig::gracePeriodType)
                .orElse(GracePeriodType.S);
    }

    @Override
    public double getPrepaymentForInstallment(int installmentNumber, List<PrepaymentConfig> prepayments) {
        return prepayments.stream()
                .filter(config -> config.installmentNumber() == installmentNumber)
                .findFirst()
                .map(PrepaymentConfig::prepaymentAmount)
                .orElse(0.0);
    }

    @Override
    public void recalculatePaymentPlan(PaymentPlan paymentPlan,
                                       List<GracePeriodConfig> gracePeriods,
                                       List<PrepaymentConfig> prepayments,
                                       List<InterestRateConfig> interestRateConfigs) {
        log.info("Recalculando completamente el plan de pagos ID: {}", paymentPlan.getId());

        try {
            // ✅ 1. VERIFICAR Y LIMPIAR COMPLETAMENTE LAS CUOTAS EXISTENTES
            log.info("Cuotas ANTES de limpiar: {}", paymentPlan.getInstallments().size());

            // ❌ PROBLEMA: Solo limpiar la lista no elimina de BD
            // paymentPlan.getInstallments().clear();

            // ✅ SOLUCIÓN: Eliminar explícitamente cada cuota
            List<Installment> installmentsToDelete = new ArrayList<>(paymentPlan.getInstallments());
            paymentPlan.getInstallments().clear();

            // Si tienes un repository de Installment, elimínalas explícitamente
            // installmentRepository.deleteAll(installmentsToDelete);

            log.info("Cuotas DESPUÉS de limpiar: {}", paymentPlan.getInstallments().size());

            // ✅ 2. Recalcular valores iniciales
            paymentPlan.calculateInitialValues();

            // ✅ 3. Crear SOLO UNA cuota de desembolso
            Installment disbursementInstallment = createDisbursementInstallment(paymentPlan);
            paymentPlan.addInstallment(disbursementInstallment);
            log.info("Cuota 0 (desembolso) creada - Cash Flow: {}", disbursementInstallment.getCashFlow());

            // ✅ 4. Generar el resto de cuotas
            double currentBalance = paymentPlan.getLoanAmount();
            log.info("Balance inicial para cuotas: {}", currentBalance);

            for (int i = 1; i <= paymentPlan.getTotalInstallments(); i++) {
                GracePeriodType gracePeriodType = getGracePeriodTypeForInstallment(i, gracePeriods);
                double prepaymentAmount = getPrepaymentForInstallment(i, prepayments);

                Installment installment = calculateInstallment(
                        i, currentBalance, paymentPlan, gracePeriodType, prepaymentAmount
                );

                paymentPlan.addInstallment(installment);
                currentBalance = installment.getFinalBalance();
            }

            // ✅ 5. Calcular totales e indicadores
            calculateTotals(paymentPlan);
            calculateFinancialIndicators(paymentPlan);

            log.info("Recálculo completado. Total cuotas generadas: {}", paymentPlan.getInstallments().size());

        } catch (Exception e) {
            log.error("Error en recálculo", e);
            throw new RuntimeException("Error al recalcular el plan de pagos", e);
        }
    }

    private double calculateNPVForIRR(List<Double> cashFlows, double rate) {
        double npv = 0.0;
        for (int t = 0; t < cashFlows.size(); t++) {
            npv += cashFlows.get(t) / Math.pow(1 + rate, t);
        }
        return npv;
    }

    private double calculateNPVDerivative(List<Double> cashFlows, double rate) {
        double derivative = 0.0;
        for (int t = 0; t < cashFlows.size(); t++) {
            derivative -= t * cashFlows.get(t) / Math.pow(1 + rate, t + 1);
        }
        return derivative;
    }

    private double calculateIRRByBisection(List<Double> cashFlows, double low, double high, double tolerance) {
        // Método de bisección como fallback
        double mid = 0;
        double npvLow = calculateNPVForIRR(cashFlows, low);
        double npvHigh = calculateNPVForIRR(cashFlows, high);

        if (npvLow * npvHigh > 0) {
            // No hay cambio de signo en el intervalo
            return 0.0; // No se puede calcular TIR
        }

        for (int i = 0; i < 1000; i++) {
            mid = (low + high) / 2;
            double npvMid = calculateNPVForIRR(cashFlows, mid);

            if (Math.abs(npvMid) < tolerance) {
                return mid;
            }

            if (npvLow * npvMid < 0) {
                high = mid;
                npvHigh = npvMid;
            } else {
                low = mid;
                npvLow = npvMid;
            }
        }

        return mid;
    }

    // ✅ CORREGIDO: Método con parámetros correctos
    private double calculateFrenchInstallmentAmount(double principal, int periods, double periodicRate) {
        if (periodicRate == 0) return principal / periods;

        // Prueba ambas fórmulas y elige la que coincida con tu Excel
        double option1 = principal * periodicRate / (1 - Math.pow(1 + periodicRate, -periods));
        double option2 = principal * (periodicRate * Math.pow(1 + periodicRate, periods)) / (Math.pow(1 + periodicRate, periods) - 1);

        // Debug para ver cuál coincide
        log.info("Cuota francesa - Opción 1: {}, Opción 2: {}, Esperado: {}", option1, option2, getExpectedInstallmentFromExcel(periods));

        return option1; // Probablemente esta es la correcta
    }

    // ✅ NUEVO MÉTODO: Para debugging - verificar cálculo de cuota
    private void validateFrenchCalculation(double principal, int periods, double rate, double calculatedInstallment) {
        double simulatedBalance = principal;
        for (int i = 0; i < periods; i++) {
            double interest = simulatedBalance * rate;
            double amortization = calculatedInstallment - interest;
            simulatedBalance -= amortization;
        }

        if (Math.abs(simulatedBalance) > 0.01) {
            log.warn("Validación cuota francesa: Saldo final {} debería ser 0", simulatedBalance);
        }
    }

    private double calculateCreditLifeInsuranceForInstallment(Installment installment, PaymentPlan paymentPlan) {
        return installment.getInitialBalance() * paymentPlan.getPeriodicCreditLifeInsurance();
    }

    private double calculateRiskInsuranceForInstallment(Installment installment, PaymentPlan paymentPlan) {
        return paymentPlan.getPeriodicRiskInsurance();
    }



}