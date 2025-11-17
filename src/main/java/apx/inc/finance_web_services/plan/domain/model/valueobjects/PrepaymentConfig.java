package apx.inc.finance_web_services.plan.domain.model.valueobjects;

public record PrepaymentConfig(
        int installmentNumber,           // Número de cuota
        double prepaymentAmount          // Monto del prepago
) {
    public PrepaymentConfig {
        if (installmentNumber <= 0) {
            throw new IllegalArgumentException("El número de cuota debe ser mayor a 0");
        }
        if (prepaymentAmount <= 0) {
            throw new IllegalArgumentException("El monto del prepago debe ser mayor a 0");
        }
    }
}
