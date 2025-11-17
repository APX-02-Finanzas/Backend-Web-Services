package apx.inc.finance_web_services.plan.domain.model.valueobjects;

public enum GracePeriodType {
    T,  // Total - No paga intereses ni amortización
    P,  // Parcial - Solo paga intereses
    S   // Sin gracia - Pago normal
}