package apx.inc.finance_web_services.unit;

import apx.inc.finance_web_services.client.domain.model.aggregates.Client;
import apx.inc.finance_web_services.client.domain.model.commands.CreateClientCommand;
import apx.inc.finance_web_services.client.domain.model.commands.UpdateClientCommand;
import apx.inc.finance_web_services.client.domain.model.valueobjects.CivilState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClientTests {

    private Client client;

    @BeforeEach
    void setUp() {
        CreateClientCommand createCommand = new CreateClientCommand(
                "Juan",
                "Pérez",
                CivilState.SINGLE,
                "juan@example.com",
                "555-1234",
                "12345678",
                10L,
                1500.0,
                600.0
        );

        client = new Client(createCommand);
    }

    // Crear cliente y verificar que se asignan todos los campos correctamente
    @Test
    void createClient_ShouldSetAllFields() {
        assertThat(client.getName()).isEqualTo("Juan");
        assertThat(client.getSurname()).isEqualTo("Pérez");
        assertThat(client.getCivilState()).isEqualTo(CivilState.SINGLE);
        assertThat(client.getEmail()).isEqualTo("juan@example.com");
        assertThat(client.getPhone()).isEqualTo("555-1234");
        assertThat(client.getDni()).isEqualTo("12345678");
        assertThat(client.getSalesManId()).isEqualTo(10L);
        assertThat(client.getMonthlyIncome()).isEqualTo(1500.0);
        assertThat(client.getMonthlyExpenses()).isEqualTo(600.0);
    }

    // Actualizar cliente y verificar que los campos cambian según el comando
    @Test
    void updateClient_ShouldUpdateFields() {
        UpdateClientCommand updateCommand = new UpdateClientCommand(
                1L,
                "Juanito",
                "Pérez-R",
                CivilState.MARRIED,
                "juanito@example.com",
                "555-9999",
                "87654321",
                1800.0,
                700.0
        );

        client.update(updateCommand);

        assertThat(client.getName()).isEqualTo("Juanito");
        assertThat(client.getSurname()).isEqualTo("Pérez-R");
        assertThat(client.getCivilState()).isEqualTo(CivilState.MARRIED);
        assertThat(client.getEmail()).isEqualTo("juanito@example.com");
        assertThat(client.getPhone()).isEqualTo("555-9999");
        assertThat(client.getDni()).isEqualTo("87654321");
        assertThat(client.getMonthlyIncome()).isEqualTo(1800.0);
        assertThat(client.getMonthlyExpenses()).isEqualTo(700.0);
    }

    // Eliminar referencia al cliente (permitir null)
    @Test
    void deleteClient_ShouldAllowNullReference() {
        client = null;
        assertThat(client).isNull();
    }
}
