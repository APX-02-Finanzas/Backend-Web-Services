package apx.inc.finance_web_services.client.domain.model.aggregates;

import apx.inc.finance_web_services.client.domain.model.commands.CreateClientCommand;
import apx.inc.finance_web_services.client.domain.model.commands.UpdateClientCommand;
import apx.inc.finance_web_services.client.domain.model.valueobjects.CivilState;
import apx.inc.finance_web_services.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Client extends AuditableAbstractAggregateRoot<Client> {

    private String name;
    private String surname;
    private CivilState civilState;
    private String email;
    private String phone;
    private String dni;

    // NUEVO: Para filtrar por salesman
    private Long salesManId;

    // Información económica básica
    private double monthlyIncome;
    private double monthlyExpenses;

    // Validaciones bono Techo Propio (simplificado)
    private boolean hasPreviousStateHousing;

    // Constructor vacío para JPA
    protected Client() {
    }

    //  Constructor desde Create Command
    public Client(CreateClientCommand command) {
        this.name = command.name();
        this.surname= command.surname();
        this.civilState = command.civilState();
        this.email = command.email();
        this.phone = command.phone();
        this.dni = command.dni();
        this.salesManId = command.salesManId();
        this.monthlyIncome = command.monthlyIncome();
        this.monthlyExpenses = command.monthlyExpenses();
        this.hasPreviousStateHousing = false; // Por defecto
    }

    //  Método para update (sin constructor separado)
    public void update(UpdateClientCommand command) {
        this.name = command.name();
        this.surname= command.surname();
        this.civilState = command.civilState();
        this.email = command.email();
        this.phone = command.phone();
        this.dni = command.dni();
        this.monthlyIncome = command.monthlyIncome();
        this.monthlyExpenses = command.monthlyExpenses();
    }

    //  Método simplificado para bono
    public void markAsPreviousStateHousing() {
        this.hasPreviousStateHousing = true;
    }

    //  Validación simple
    public boolean canApplyForBono() {
        return !this.hasPreviousStateHousing;
    }
}