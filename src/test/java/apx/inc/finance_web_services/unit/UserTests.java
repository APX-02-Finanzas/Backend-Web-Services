package apx.inc.finance_web_services.unit;

import apx.inc.finance_web_services.iam.domain.model.aggregates.User;
import apx.inc.finance_web_services.iam.domain.model.commands.UpdateUserCommand;
import apx.inc.finance_web_services.iam.domain.model.valueobjects.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserTests {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Donlox", "123", "Omar", "Luquillas", "omar@gmail.com", List.of());
    }

    // Crear usuario y verificar campos básicos
    @Test
    void createUser_ShouldSetAllBasicFields() {
        assertThat(user.getUsername()).isEqualTo("Donlox");
        assertThat(user.getPassword()).isEqualTo("123");
        assertThat(user.getName()).isEqualTo("Omar");
        assertThat(user.getSurname()).isEqualTo("Luquillas");
        assertThat(user.getEmail()).isEqualTo("omar@gmail.com");
    }

    // Actualizar usuario y verificar campos actualizados
    @Test
    void updateUserDetails_ShouldUpdateFields() {
        UpdateUserCommand command = new UpdateUserCommand(
                "Firtness",
                "123",
                "José",
                "Alejo",
                "jose@gmail.com",
                List.of(Roles.ROLE_SALESMAN)
        );

        user.updateUserDetails(command);

        assertThat(user.getUsername()).isEqualTo("Firtness");
        assertThat(user.getPassword()).isEqualTo("123");
        assertThat(user.getName()).isEqualTo("José");
        assertThat(user.getSurname()).isEqualTo("Alejo");
        assertThat(user.getEmail()).isEqualTo("jose@gmail.com");
    }
}
