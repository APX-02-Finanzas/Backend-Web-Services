package apx.inc.finance_web_services.plan.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record InterestRateConfig(
    int installmentNumber,    // Número de cuota donde cambia la tasa
    double newAnnualRate     // Nueva tasa anual que se aplicará a partir de esa cuota
) {
    public InterestRateConfig {
            if (installmentNumber <= 0) {
                throw new IllegalArgumentException("El número de cuota debe ser mayor a 0");
            }
            if (newAnnualRate < 0) {
                throw new IllegalArgumentException("La tasa no puede ser negativa");
            }
    }
}
