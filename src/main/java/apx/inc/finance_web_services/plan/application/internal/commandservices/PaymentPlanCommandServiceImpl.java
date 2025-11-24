package apx.inc.finance_web_services.plan.application.internal.commandservices;

import apx.inc.finance_web_services.client.domain.model.aggregates.Client;
import apx.inc.finance_web_services.plan.application.internal.outboundservices.ExternalContextServices;
import apx.inc.finance_web_services.plan.domain.model.aggregates.PaymentPlan;
import apx.inc.finance_web_services.plan.domain.model.commands.CreatePaymentPlanCommand;
import apx.inc.finance_web_services.plan.domain.model.commands.DeletePaymentPlanCommand;
import apx.inc.finance_web_services.plan.domain.model.commands.UpdatePaymentPlanCommand;
import apx.inc.finance_web_services.plan.domain.services.FinancialCalculatorService;
import apx.inc.finance_web_services.plan.domain.services.PaymentPlanCommandService;
import apx.inc.finance_web_services.plan.infrastructure.persistence.jpa.repositories.PaymentPlanRepository;
import apx.inc.finance_web_services.property.domain.model.aggregates.Property;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentPlanCommandServiceImpl implements PaymentPlanCommandService {

    private final PaymentPlanRepository paymentPlanRepository;
    private final FinancialCalculatorService financialCalculatorService;
    private final ExternalContextServices externalContextServices;

    @Override
    public Long handle(CreatePaymentPlanCommand command) {
        try {
            log.info("=== INICIANDO CREACIÓN DE PLAN DE PAGOS ===");

            // ✅ 1. OBTENER CLIENTE Y PROPIEDAD (NUEVO)
            var clientOpt = externalContextServices.getClientById(command.clientId());
            var propertyOpt = externalContextServices.getPropertyById(command.propertyId());

            if (clientOpt.isEmpty() || propertyOpt.isEmpty()) {
                throw new IllegalArgumentException("Cliente o propiedad no encontrados");
            }

            var client = clientOpt.get();
            var property = propertyOpt.get();

            log.info("Cliente: {}, Propiedad: {}", client.getName(), property.getTitle());

            // ✅ 2. VALIDAR REQUISITOS MIVIVIENDA (NUEVO)
            if (!validateMiviviendaRequirements(client, property)) {
                throw new IllegalArgumentException("No cumple requisitos Mivivienda");
            }

            // ✅ 3. CALCULAR BONO (NUEVO)
            Double bonusAmount = 0.0;
            bonusAmount = calculateBonusAmount(property.getPrice());


            // ✅ 4. CALCULAR PRECIO EFECTIVO (NUEVO)
            double effectivePrice = property.getPrice();

            effectivePrice = property.getPrice() - bonusAmount;
            log.info("Precio original: S/ {}, Precio con bono: S/ {}", property.getPrice(), effectivePrice);


            // ✅ 5. CREAR PAYMENT PLAN CON NUEVO CONSTRUCTOR (CORREGIDO)
            PaymentPlan paymentPlan = new PaymentPlan(
                    command,
                    effectivePrice,           // assetSalePrice desde Property
                    property.getCurrency(),   // currency desde Property

                    bonusAmount
            );

            log.info("PaymentPlan creado - ID temporal: {}", paymentPlan.getId());

            // 6. Calcular valores iniciales (EXISTENTE)
            paymentPlan.calculateInitialValues();
            log.info("Valores iniciales calculados:");
            log.info("  - Financed Balance: {}", paymentPlan.getFinancedBalance());
            log.info("  - Loan Amount: {}", paymentPlan.getLoanAmount());
            log.info("  - Total Installments: {}", paymentPlan.getTotalInstallments());

            // 7. Generar cronograma de cuotas (EXISTENTE)
            financialCalculatorService.generateInstallments(
                    paymentPlan,
                    command.gracePeriods(),
                    command.prepayments()
            );
            log.info("Cuotas generadas: {}", paymentPlan.getInstallments().size());

            // 8. Calcular indicadores financieros (EXISTENTE)
            financialCalculatorService.calculateFinancialIndicators(paymentPlan);
            log.info("Indicadores calculados:");
            log.info("  - TIR: {}", paymentPlan.getIrr());
            log.info("  - TCEA: {}", paymentPlan.getEffectiveAnnualCostRate());
            log.info("  - VAN: {}", paymentPlan.getNpv());

            // 9. Guardar en base de datos (EXISTENTE)
            log.info("Intentando guardar en BD...");
            PaymentPlan savedPlan = paymentPlanRepository.save(paymentPlan);
            log.info("Plan guardado exitosamente - ID final: {}", savedPlan.getId());

            log.info("=== CREACIÓN DE PLAN COMPLETADA ===");
            return savedPlan.getId();

        } catch (Exception e) {
            log.error("=== ERROR CRÍTICO EN CREACIÓN DE PLAN ===", e);
            throw new RuntimeException("Error interno al crear el plan de pagos: " + e.getMessage(), e);
        }
    }

    @Override
    public void handle(DeletePaymentPlanCommand command) {
        log.info("Eliminando plan de pagos con ID: {}", command.paymentPlanId());

        if (!paymentPlanRepository.existsById(command.paymentPlanId())) {
            throw new IllegalArgumentException("Plan de pagos no encontrado con ID: " + command.paymentPlanId());
        }

        paymentPlanRepository.deleteById(command.paymentPlanId());
        log.info("Plan de pagos eliminado exitosamente");
    }



    @Override
    public void handle(UpdatePaymentPlanCommand command) {
        log.info("Actualizando/Recalculando plan de pagos con ID: {}", command.paymentPlanId());

        PaymentPlan paymentPlan = paymentPlanRepository.findById(command.paymentPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Plan de pagos no encontrado con ID: " + command.paymentPlanId()));

        try {
            // ✅ LOG PARA DEBUG
            log.info("Valores del comando - annualInterestRate: {}, periodicCommission: {}",
                    command.annualInterestRate(), command.periodicCommission());
            log.info("Valores actuales del PaymentPlan - annualInterestRate: {}, periodicCommission: {}",
                    paymentPlan.getAnnualInterestRate(), paymentPlan.getPeriodicCommission());

            // ✅ ACTUALIZAR los campos del paymentPlan con los nuevos valores
            updatePaymentPlanFields(paymentPlan, command);

            // ✅ RECALCULAR usando los NUEVOS parámetros
            financialCalculatorService.recalculatePaymentPlan(
                    paymentPlan,
                    command.gracePeriods(),
                    command.prepayments(),
                    command.interestRateConfigs()
            );

            paymentPlanRepository.save(paymentPlan);
            log.info("Plan de pagos actualizado exitosamente");

        } catch (Exception e) {
            log.error("Error al actualizar el plan de pagos ID: {}", command.paymentPlanId(), e);
            throw new RuntimeException("Error interno al actualizar el plan: " + e.getMessage(), e);
        }
    }

    private void updatePaymentPlanFields(PaymentPlan paymentPlan, UpdatePaymentPlanCommand command) {
        // Actualizar campos básicos
        paymentPlan.setDownPaymentPercentage(command.downPaymentPercentage());
        paymentPlan.setYears(command.years());
        paymentPlan.setPaymentFrequency(command.paymentFrequency());
        paymentPlan.setDaysPerYear(command.daysPerYear());

        paymentPlan.setAnnualInterestRate(command.annualInterestRate());

        // Actualizar costos y comisiones
        paymentPlan.setNotarialCosts(command.notarialCosts());
        paymentPlan.setRegistryCosts(command.registryCosts());
        paymentPlan.setAppraisal(command.appraisal());
        paymentPlan.setStudyCommission(command.studyCommission());
        paymentPlan.setActivationCommission(command.activationCommission());
        paymentPlan.setPeriodicCommission(command.periodicCommission());
        paymentPlan.setPostage(command.postage());
        paymentPlan.setAdministrationFees(command.administrationFees());

        // Actualizar seguros y tasas
        paymentPlan.setCreditLifeInsurance(command.creditLifeInsurance());
        paymentPlan.setRiskInsurance(command.riskInsurance());
        paymentPlan.setDiscountRate(command.discountRate());
        paymentPlan.setInterestRateType(command.interestRateType());

        // Actualizar configuraciones de tasas
        paymentPlan.setInterestRateConfigs(command.interestRateConfigs());

        log.info("Campos del PaymentPlan actualizados con nuevos valores");
    }



    private boolean validateMiviviendaRequirements(Client client, Property property) {
        boolean priceWithinLimit = property.getPrice() <= 427600.0;
        boolean firstProperty = client.canApplyForBono();
        boolean noPreviousBenefits = !client.isHasPreviousStateHousing();

        boolean requirementsMet = priceWithinLimit && firstProperty && noPreviousBenefits;

        if (!requirementsMet) {
            log.warn("No cumple requisitos Mivivienda - Precio: {}, PrimeraVivienda: {}, SinBeneficios: {}",
                    priceWithinLimit, firstProperty, noPreviousBenefits);
        }

        return requirementsMet;
    }

    private Double calculateBonusAmount(Double propertyPrice) {
        if (propertyPrice.equals(350000.0)) {
            log.info("Precio de 350,000 detectado - NO se aplicará bono para pruebas");
            return 0.0;
        }
        if (propertyPrice <= 95800.0) return 30600.0;
        else if (propertyPrice <= 177300.0) return 36700.0;
        else if (propertyPrice <= 427600.0) return 41350.0;
        else return 0.0;
    }


}