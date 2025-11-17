package apx.inc.finance_web_services.property.domain.model.aggregates;

import apx.inc.finance_web_services.plan.domain.model.valueobjects.Currency;
import apx.inc.finance_web_services.property.domain.model.commands.CreatePropertyCommand;
import apx.inc.finance_web_services.property.domain.model.commands.UpdatePropertyCommand;
import apx.inc.finance_web_services.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Property extends AuditableAbstractAggregateRoot<Property> {

    private String title;
    private String description;
    private double price;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private String address;

    // ✅ NUEVO: Para filtrar por salesman
    private Long salesManId;

    // ✅ Constructor vacío para JPA
    protected Property() {
    }

    // ✅ Constructor desde Create Command
    public Property(CreatePropertyCommand command) {
        this.title = command.title();
        this.description = command.description();
        this.price = command.price();
        this.currency = command.currency();
        this.address = command.address();
        this.salesManId = command.salesManId();
    }

    // ✅ Método para update
    public void update(UpdatePropertyCommand command) {
        this.title = command.title();
        this.description = command.description();
        this.price = command.price();
        this.currency = command.currency();
        this.address = command.address();
    }

    // ✅ Validación simple para bono
    public boolean isEligibleForBono() {
        return this.price <= 427600.0;
    }
}
