package apx.inc.finance_web_services.integration;

import apx.inc.finance_web_services.iam.domain.model.aggregates.User;
import apx.inc.finance_web_services.iam.domain.model.commands.SignInCommand;
import apx.inc.finance_web_services.iam.domain.model.commands.SignUpCommand;
import apx.inc.finance_web_services.iam.domain.services.UserCommandService;
import apx.inc.finance_web_services.iam.interfaces.rest.AuthenticationController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserCommandService userCommandService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        var controller = new AuthenticationController(userCommandService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    // Verifica que el endpoint sign-up crea un usuario y devuelve 201 cuando la petición es válida (servicio mockeado, sin conexión a BD)
    @Test
    void signUp_CreatesUser_WhenValidRequest() throws Exception {
        User createdUser = new User("testuser4", "encodedPass", "Test", "User", "testuser4@example.com");
        when(userCommandService.handle(ArgumentMatchers.any(SignUpCommand.class))).thenReturn(Optional.of(createdUser));

        var payload = objectMapper.createObjectNode();
        payload.put("username", "testuser4");
        payload.put("password", "testpassword");
        payload.put("name", "Test");
        payload.put("surname", "User");
        payload.put("email", "testuser4@example.com");
        payload.put("recaptchaToken", "fake-token");
        var rolesNode = objectMapper.createArrayNode();
        rolesNode.add("ROLE_SALESMAN");
        payload.set("roles", rolesNode);

        mockMvc.perform(post("/api/v1/authentication/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser4"));
    }

    // Verifica que se llama al servicio con el comando SignUpCommand correcto y los campos esperados
    @Test
    void signUp_VerifyServiceCalled_WithCorrectCommand() throws Exception {
        User createdUser = new User("testuserX", "encodedPass", "Nombre", "Apellido", "u@example.com");
        when(userCommandService.handle(ArgumentMatchers.any(SignUpCommand.class))).thenReturn(Optional.of(createdUser));

        var payload = objectMapper.createObjectNode();
        payload.put("username", "testuserX");
        payload.put("password", "pw");
        payload.put("name", "Nombre");
        payload.put("surname", "Apellido");
        payload.put("email", "u@example.com");
        payload.put("recaptchaToken", "tokenX");
        var rolesNode = objectMapper.createArrayNode();
        rolesNode.add("ROLE_SALESMAN");
        payload.set("roles", rolesNode);

        mockMvc.perform(post("/api/v1/authentication/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());

        ArgumentCaptor<SignUpCommand> captor = ArgumentCaptor.forClass(SignUpCommand.class);
        verify(userCommandService, times(1)).handle(captor.capture());
        var captured = captor.getValue();
        assertThat(captured.userName()).isEqualTo("testuserX");
        assertThat(captured.password()).isEqualTo("pw");
        assertThat(captured.name()).isEqualTo("Nombre");
        assertThat(captured.surname()).isEqualTo("Apellido");
        assertThat(captured.email()).isEqualTo("u@example.com");
        assertThat(captured.recaptchaToken()).isEqualTo("tokenX");
        assertThat(captured.roles()).isNotEmpty();
    }

    // Verifica que sign-up devuelve 400 cuando el servicio no crea el usuario (retorna Optional.empty)
    @Test
    void signUp_ReturnsBadRequest_WhenServiceReturnsEmpty() throws Exception {
        when(userCommandService.handle(ArgumentMatchers.any(SignUpCommand.class))).thenReturn(Optional.empty());

        var payload = objectMapper.createObjectNode();
        payload.put("username", "baduser");
        payload.put("password", "pw");
        payload.put("name", "N");
        payload.put("surname", "S");
        payload.put("email", "e@e.com");
        payload.put("recaptchaToken", "bad-token");
        var rolesNode = objectMapper.createArrayNode();
        rolesNode.add("ROLE_SALESMAN");
        payload.set("roles", rolesNode);

        mockMvc.perform(post("/api/v1/authentication/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    // Verifica que sign-in devuelve 200 con usuario y token cuando las credenciales son válidas
    @Test
    void signIn_ReturnsAuthenticatedUser_WhenCredentialsAreValid() throws Exception {
        User user = new User("loginuser5", "encodedPass", "Login", "User", "loginuser5@example.com");
        String token = "token-abc-123";

        when(userCommandService.handle(ArgumentMatchers.any(SignInCommand.class)))
                .thenReturn(Optional.of(ImmutablePair.of(user, token)));

        var payload = objectMapper.createObjectNode();
        payload.put("username", "loginuser5");
        payload.put("password", "loginpassword");

        mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("loginuser5"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    // Verifica que sign-in devuelve 404 cuando el servicio no encuentra al usuario
    @Test
    void signIn_ReturnsNotFound_WhenUserNotFound() throws Exception {
        when(userCommandService.handle(ArgumentMatchers.any(SignInCommand.class))).thenReturn(Optional.empty());

        var payload = objectMapper.createObjectNode();
        payload.put("username", "noexist");
        payload.put("password", "whatever");

        mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound());
    }
}
