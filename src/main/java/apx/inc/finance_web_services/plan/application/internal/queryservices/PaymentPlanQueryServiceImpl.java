package apx.inc.finance_web_services.plan.application.internal.queryservices;

import apx.inc.finance_web_services.plan.domain.model.aggregates.PaymentPlan;
import apx.inc.finance_web_services.plan.domain.model.entities.Installment;
import apx.inc.finance_web_services.plan.domain.model.queries.GetAllPaymentPlansQuery;
import apx.inc.finance_web_services.plan.domain.model.queries.GetInstallmentsByPaymentPlanIdQuery;
import apx.inc.finance_web_services.plan.domain.model.queries.GetPaymentPlanByIdQuery;
import apx.inc.finance_web_services.plan.domain.services.PaymentPlanQueryService;
import apx.inc.finance_web_services.plan.infrastructure.persistence.jpa.repositories.PaymentPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentPlanQueryServiceImpl implements PaymentPlanQueryService {

    private final PaymentPlanRepository paymentPlanRepository;

    @Override
    public List<PaymentPlan> handle(GetAllPaymentPlansQuery query) {
        log.info("Obteniendo todos los planes de pagos");
        return paymentPlanRepository.findAll();
    }

    @Override
    public PaymentPlan handle(GetPaymentPlanByIdQuery query) {
        log.info("Obteniendo plan de pagos con ID: {}", query.paymentPlanId());
        return paymentPlanRepository.findById(query.paymentPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Plan de pagos no encontrado con ID: " + query.paymentPlanId()));
    }

    @Override
    public List<Installment> handle(GetInstallmentsByPaymentPlanIdQuery query) {
        log.info("Obteniendo cuotas del plan de pagos con ID: {}", query.paymentPlanId());

        PaymentPlan paymentPlan = paymentPlanRepository.findById(query.paymentPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Plan de pagos no encontrado con ID: " + query.paymentPlanId()));

        return paymentPlan.getInstallments();
    }


}