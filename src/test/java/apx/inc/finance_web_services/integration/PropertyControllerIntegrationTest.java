package apx.inc.finance_web_services.integration;

import apx.inc.finance_web_services.property.domain.model.aggregates.Property;
import apx.inc.finance_web_services.property.domain.model.commands.CreatePropertyCommand;
import apx.inc.finance_web_services.property.domain.model.commands.UpdatePropertyCommand;
import apx.inc.finance_web_services.property.domain.model.queries.GetAllPropertiesQuery;
import apx.inc.finance_web_services.property.domain.model.queries.GetPropertyByIdQuery;
import apx.inc.finance_web_services.property.domain.services.PropertyCommandService;
import apx.inc.finance_web_services.property.domain.services.PropertyQueryService;
import apx.inc.finance_web_services.property.interfaces.rest.PropertyController;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.Currency;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PropertyControllerIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private PropertyCommandService propertyCommandService;

    @Mock
    private PropertyQueryService propertyQueryService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        var controller = new PropertyController(propertyCommandService, propertyQueryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    // Verifica que POST /api/v1/properties crea una propiedad y devuelve 201 con el recurso creado
    @Test
    void createProperty_ReturnsCreated_WhenValidRequest() throws Exception {
        CreatePropertyCommand createCommand = new CreatePropertyCommand(
                "Casa Bonita",
                "Hermosa casa cerca del parque",
                350000.0,
                120.0,
                (short) 3,
                Currency.USD,
                "Av. Siempre Viva 742",
                42L
        );
        Property createdProperty = new Property(createCommand);

        when(propertyCommandService.handle(ArgumentMatchers.any(CreatePropertyCommand.class))).thenReturn(1L);
        when(propertyQueryService.handle(ArgumentMatchers.any(GetPropertyByIdQuery.class))).thenReturn(Optional.of(createdProperty));

        var payload = objectMapper.createObjectNode();
        payload.put("title", "Casa Bonita");
        payload.put("description", "Hermosa casa cerca del parque");
        payload.put("price", 350000.0);
        payload.put("m2", 120.0);
        payload.put("rooms", 3);
        payload.put("currency", "USD");
        payload.put("address", "Av. Siempre Viva 742");
        payload.put("salesManId", 42);

        mockMvc.perform(post("/api/v1/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Casa Bonita"));
    }

    // Verifica que GET /api/v1/properties/{id} devuelve 200 y el plan esperado cuando existe
    @Test
    void getPropertyById_ReturnsOk_WhenExists() throws Exception {
        CreatePropertyCommand createCommand = new CreatePropertyCommand(
                "Casa Bonita",
                "Hermosa casa cerca del parque",
                350000.0,
                120.0,
                (short) 3,
                Currency.USD,
                "Av. Siempre Viva 742",
                42L
        );
        Property property = new Property(createCommand);

        when(propertyQueryService.handle(ArgumentMatchers.any(GetPropertyByIdQuery.class))).thenReturn(Optional.of(property));

        mockMvc.perform(get("/api/v1/properties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Casa Bonita"));
    }

    // Verifica que PUT /api/v1/properties/{id} actualiza la propiedad y devuelve 200 con el recurso actualizado
    @Test
    void updateProperty_ReturnsOk_WhenExists() throws Exception {
        UpdatePropertyCommand update = new UpdatePropertyCommand(
                1L,
                "Casa Renovada",
                "Recién remodelada",
                400000.0,
                130.0,
                (short) 4,
                Currency.USD,
                "Calle Falsa 123"
        );
        CreatePropertyCommand createCommand = new CreatePropertyCommand(
                "Casa Bonita",
                "Hermosa casa cerca del parque",
                350000.0,
                120.0,
                (short) 3,
                Currency.USD,
                "Av. Siempre Viva 742",
                42L
        );
        Property updatedProperty = new Property(createCommand);
        updatedProperty.update(update);

        when(propertyQueryService.handle(ArgumentMatchers.any(GetPropertyByIdQuery.class))).thenReturn(Optional.of(updatedProperty));

        var payload = objectMapper.createObjectNode();
        payload.put("title", "Casa Renovada");
        payload.put("description", "Recién remodelada");
        payload.put("price", 400000.0);
        payload.put("m2", 130.0);
        payload.put("rooms", 4);
        payload.put("currency", "USD");
        payload.put("address", "Calle Falsa 123");

        mockMvc.perform(put("/api/v1/properties/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Casa Renovada"));
    }

    // Verifica que DELETE /api/v1/properties/{id} elimina la propiedad y devuelve 204
    @Test
    void deleteProperty_ReturnsNoContent_WhenDeleted() throws Exception {
        mockMvc.perform(delete("/api/v1/properties/1"))
                .andExpect(status().isNoContent());
    }

    // Verifica que GET /api/v1/properties devuelve una lista con las propiedades disponibles
    @Test
    void getAllProperties_ReturnsList_WhenExists() throws Exception {
        CreatePropertyCommand createCommand = new CreatePropertyCommand(
                "Casa Bonita",
                "Hermosa casa cerca del parque",
                350000.0,
                120.0,
                (short) 3,
                Currency.USD,
                "Av. Siempre Viva 742",
                42L
        );
        Property property = new Property(createCommand);

        when(propertyQueryService.handle(ArgumentMatchers.any(GetAllPropertiesQuery.class))).thenReturn(List.of(property));

        mockMvc.perform(get("/api/v1/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Casa Bonita"));
    }
}
