package apx.inc.finance_web_services.plan.application.internal.commandservices;

import apx.inc.finance_web_services.plan.domain.model.aggregates.PaymentPlan;
import apx.inc.finance_web_services.plan.domain.model.commands.CreatePaymentPlanCommand;
import apx.inc.finance_web_services.plan.domain.model.commands.DeletePaymentPlanCommand;
import apx.inc.finance_web_services.plan.domain.model.commands.UpdatePaymentPlanCommand;
import apx.inc.finance_web_services.plan.domain.services.FinancialCalculatorService;
import apx.inc.finance_web_services.plan.domain.services.PaymentPlanCommandService;
import apx.inc.finance_web_services.plan.infrastructure.persistence.jpa.repositories.PaymentPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentPlanCommandServiceImpl implements PaymentPlanCommandService {

    private final PaymentPlanRepository paymentPlanRepository;
    private final FinancialCalculatorService financialCalculatorService;

    @Override
    public Long handle(CreatePaymentPlanCommand command) {
        try {
            log.info("=== INICIANDO CREACIÓN DE PLAN DE PAGOS ===");

            // 1. Crear el aggregate root usando el constructor con Command
            PaymentPlan paymentPlan = new PaymentPlan(command);
            log.info("PaymentPlan creado - ID temporal: {}", paymentPlan.getId());

            // 2. Calcular valores iniciales
            paymentPlan.calculateInitialValues();
            log.info("Valores iniciales calculados:");
            log.info("  - Financed Balance: {}", paymentPlan.getFinancedBalance());
            log.info("  - Loan Amount: {}", paymentPlan.getLoanAmount());
            log.info("  - Total Installments: {}", paymentPlan.getTotalInstallments());

            // 3. Generar cronograma de cuotas
            financialCalculatorService.generateInstallments(
                    paymentPlan,
                    command.gracePeriods(),
                    command.prepayments()
            );
            log.info("Cuotas generadas: {}", paymentPlan.getInstallments().size());

            // 4. Calcular indicadores financieros
            financialCalculatorService.calculateFinancialIndicators(paymentPlan);
            log.info("Indicadores calculados:");
            log.info("  - TIR: {}", paymentPlan.getIrr());
            log.info("  - TCEA: {}", paymentPlan.getEffectiveAnnualCostRate());
            log.info("  - VAN: {}", paymentPlan.getNpv());

            // 5. Guardar en base de datos
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

        // Lógica de actualización simple: recálculo completo
        financialCalculatorService.recalculatePaymentPlan(paymentPlan);

        paymentPlanRepository.save(paymentPlan);
        log.info("Plan de pagos actualizado exitosamente");
    }
}