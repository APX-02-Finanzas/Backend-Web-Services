package apx.inc.finance_web_services.integration;

import apx.inc.finance_web_services.client.application.internal.AESEncryptionService;
import apx.inc.finance_web_services.client.domain.model.aggregates.Client;
import apx.inc.finance_web_services.client.domain.model.commands.CreateClientCommand;
import apx.inc.finance_web_services.client.domain.model.commands.UpdateClientCommand;
import apx.inc.finance_web_services.client.domain.model.queries.GetClientByIdQuery;
import apx.inc.finance_web_services.client.domain.model.valueobjects.CivilState;
import apx.inc.finance_web_services.client.domain.services.ClientCommandService;
import apx.inc.finance_web_services.client.domain.services.ClientQueryService;
import apx.inc.finance_web_services.client.interfaces.rest.ClientController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClientControllerIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ClientCommandService clientCommandService;

    @Mock
    private ClientQueryService clientQueryService;

    @Mock
    private AESEncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        var controller = new ClientController(clientCommandService, clientQueryService, encryptionService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    // Verifica que POST /api/v1/clients crea un cliente y devuelve 201 con el recurso creado
    @Test
    void createClient_ReturnsCreated_WhenValidRequest() throws Exception {
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
        Client createdClient = new Client(createCommand);

        when(clientCommandService.handle(ArgumentMatchers.any(CreateClientCommand.class))).thenReturn(1L);
        when(clientQueryService.handle(ArgumentMatchers.any(GetClientByIdQuery.class))).thenReturn(createdClient);

        var payload = objectMapper.createObjectNode();
        payload.put("name", "Juan");
        payload.put("surname", "Pérez");
        payload.put("civilState", "SINGLE");
        payload.put("email", "juan@example.com");
        payload.put("phone", "555-1234");
        payload.put("dni", "12345678");
        payload.put("salesManId", 10);
        payload.put("monthlyIncome", 1500.0);
        payload.put("monthlyExpenses", 600.0);

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@example.com"));
    }

    // Verifica que PUT /api/v1/clients/{id} actualiza un cliente existente y devuelve 200 con el recurso actualizado
    @Test
    void updateClient_ReturnsOk_WhenExists() throws Exception {
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

        var createForOriginal = new CreateClientCommand(
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
        Client updatedClient = new Client(createForOriginal);
        updatedClient.update(updateCommand);

        when(clientQueryService.handle(ArgumentMatchers.any(GetClientByIdQuery.class))).thenReturn(updatedClient);

        var payload = objectMapper.createObjectNode();
        payload.put("name", "Juanito");
        payload.put("surname", "Pérez-R");
        payload.put("civilState", "MARRIED");
        payload.put("email", "juanito@example.com");
        payload.put("phone", "555-9999");
        payload.put("dni", "87654321");
        payload.put("monthlyIncome", 1800.0);
        payload.put("monthlyExpenses", 700.0);

        mockMvc.perform(put("/api/v1/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juanito"))
                .andExpect(jsonPath("$.email").value("juanito@example.com"));
    }

    // Verifica que DELETE /api/v1/clients/{id} elimina el cliente y devuelve 204
    @Test
    void deleteClient_ReturnsNoContent_WhenDeleted() throws Exception {
        mockMvc.perform(delete("/api/v1/clients/1"))
                .andExpect(status().isNoContent());
    }
}
