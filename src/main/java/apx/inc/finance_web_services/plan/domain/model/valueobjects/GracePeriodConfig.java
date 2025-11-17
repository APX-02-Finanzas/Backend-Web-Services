package apx.inc.finance_web_services.plan.domain.model.valueobjects;

public record GracePeriodConfig(
        int installmentNumber,           // Número de cuota
        GracePeriodType gracePeriodType  // Tipo de gracia (T, P, S)
) {
    public GracePeriodConfig {
        if (installmentNumber <= 0) {
            throw new IllegalArgumentException("El número de cuota debe ser mayor a 0");
        }
        if (gracePeriodType == null) {
            throw new IllegalArgumentException("El tipo de periodo de gracia no puede ser nulo");
        }
    }
}