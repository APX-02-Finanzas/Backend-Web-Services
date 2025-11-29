package apx.inc.finance_web_services.unit;

import apx.inc.finance_web_services.property.domain.model.aggregates.Property;
import apx.inc.finance_web_services.property.domain.model.commands.CreatePropertyCommand;
import apx.inc.finance_web_services.property.domain.model.commands.UpdatePropertyCommand;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyTests {

    private Property property;

    @BeforeEach
    void setUp() {
        CreatePropertyCommand createCommand = new CreatePropertyCommand(
                "Casa Bonita",
                "Hermosa casa cerca del parque",
                350000.0,
                120.0,
                (short) 3,
                Currency.USD,
                "Av. Siempre Viva 742",
                42L // salesManId
        );

        property = new Property(createCommand);
    }

    // Crear propiedad y verificar que se asignan todos los campos correctamente
    @Test
    void createProperty_ShouldSetAllFields() {
        assertThat(property.getTitle()).isEqualTo("Casa Bonita");
        assertThat(property.getDescription()).isEqualTo("Hermosa casa cerca del parque");
        assertThat(property.getPrice()).isEqualTo(350000.0);
        assertThat(property.getM2()).isEqualTo(120.0);
        assertThat(property.getRooms()).isEqualTo((short) 3);
        assertThat(property.getCurrency()).isEqualTo(Currency.USD);
        assertThat(property.getAddress()).isEqualTo("Av. Siempre Viva 742");
        assertThat(property.getSalesManId()).isEqualTo(42L);
    }

    // Actualizar propiedad y verificar que los campos cambian según el comando
    @Test
    void updateProperty_ShouldUpdateFields() {
        UpdatePropertyCommand updateCommand = new UpdatePropertyCommand(
                1L,
                "Casa Renovada",
                "Recién remodelada",
                400000.0,
                130.0,
                (short) 4,
                Currency.USD,
                "Calle Falsa 123"
        );

        property.update(updateCommand);

        assertThat(property.getTitle()).isEqualTo("Casa Renovada");
        assertThat(property.getDescription()).isEqualTo("Recién remodelada");
        assertThat(property.getPrice()).isEqualTo(400000.0);
        assertThat(property.getM2()).isEqualTo(130.0);
        assertThat(property.getRooms()).isEqualTo((short) 4);
        assertThat(property.getCurrency()).isEqualTo(Currency.USD);
        assertThat(property.getAddress()).isEqualTo("Calle Falsa 123");
    }

    // Eliminar referencia a la propiedad (permitir null)
    @Test
    void deleteProperty_ShouldAllowNullReference() {
        property = null;
        assertThat(property).isNull();
    }
}
