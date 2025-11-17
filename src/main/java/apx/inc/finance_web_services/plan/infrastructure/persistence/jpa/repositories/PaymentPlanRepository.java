package apx.inc.finance_web_services.plan.infrastructure.persistence.jpa.repositories;

import apx.inc.finance_web_services.plan.domain.model.aggregates.PaymentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentPlanRepository extends JpaRepository<PaymentPlan, Long> {
}
