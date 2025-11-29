package apx.inc.finance_web_services.integration;

import apx.inc.finance_web_services.plan.domain.model.aggregates.PaymentPlan;
import apx.inc.finance_web_services.plan.domain.model.commands.CreatePaymentPlanCommand;
import apx.inc.finance_web_services.plan.domain.model.queries.GetAllPaymentPlansQuery;
import apx.inc.finance_web_services.plan.domain.model.queries.GetPaymentPlanByIdQuery;
import apx.inc.finance_web_services.plan.domain.services.PaymentPlanCommandService;
import apx.inc.finance_web_services.plan.domain.services.PaymentPlanQueryService;
import apx.inc.finance_web_services.plan.interfaces.rest.PaymentPlansController;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.Currency;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.InterestRateType;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentPlansControllerIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private PaymentPlanCommandService paymentPlanCommandService;

    @Mock
    private PaymentPlanQueryService paymentPlanQueryService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        var controller = new PaymentPlansController(paymentPlanCommandService, paymentPlanQueryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    // Verifica que POST /api/v1/payment-plans crea un plan y devuelve 201 con el recurso creado
    @Test
    void createPaymentPlan_ReturnsCreated_WhenValidRequest() throws Exception {
        PaymentPlan plan = new PaymentPlan(100000.0, 20.0, 10, 30, 360, Currency.PEN, InterestRateType.TEA);
        plan.setAnnualInterestRate(12.0);

        when(paymentPlanCommandService.handle(ArgumentMatchers.any(CreatePaymentPlanCommand.class))).thenReturn(1L);
        when(paymentPlanQueryService.handle(ArgumentMatchers.any(GetPaymentPlanByIdQuery.class))).thenReturn(plan);

        var payload = objectMapper.createObjectNode();
        // Contexto
        payload.put("clientId", 1);
        payload.put("propertyId", 1);
        payload.put("salesManId", 1);

        // Datos del préstamo
        payload.put("downPaymentPercentage", 20.0);
        payload.put("years", 10);
        payload.put("paymentFrequency", 30);
        payload.put("daysPerYear", 360);

        // Costos iniciales
        payload.put("notarialCosts", 0.0);
        payload.put("registryCosts", 0.0);
        payload.put("appraisal", 0.0);
        payload.put("studyCommission", 0.0);
        payload.put("activationCommission", 0.0);

        // Costos periódicos
        payload.put("periodicCommission", 0.0);
        payload.put("postage", 0.0);
        payload.put("administrationFees", 0.0);
        payload.put("creditLifeInsurance", 0.0);
        payload.put("riskInsurance", 0.0);

        // Costo de oportunidad
        payload.put("discountRate", 0.0);

        // Configuración y tasas
        payload.put("interestRateType", "TEA");
        payload.put("annualInterestRate", 12.0);

        // Listas opcionales (vacías)
        payload.set("interestRateConfigs", objectMapper.createArrayNode());
        payload.set("gracePeriods", objectMapper.createArrayNode());
        payload.set("prepayments", objectMapper.createArrayNode());

        mockMvc.perform(post("/api/v1/payment-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.assetSalePrice").value(100000.0));
    }

    // Verifica que GET /api/v1/payment-plans/{id} devuelve 200 y el plan esperado cuando existe
    @Test
    void getPaymentPlanById_ReturnsOk_WhenExists() throws Exception {
        PaymentPlan plan = new PaymentPlan(100000.0, 20.0, 10, 30, 360, Currency.PEN, InterestRateType.TEA);
        plan.setAnnualInterestRate(12.0);

        when(paymentPlanQueryService.handle(ArgumentMatchers.any(GetPaymentPlanByIdQuery.class))).thenReturn(plan);

        mockMvc.perform(get("/api/v1/payment-plans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetSalePrice").value(100000.0));
    }

    // Verifica que GET /api/v1/payment-plans devuelve una lista con los planes disponibles
    @Test
    void getAllPaymentPlans_ReturnsList_WhenExists() throws Exception {
        PaymentPlan plan = new PaymentPlan(100000.0, 20.0, 10, 30, 360, Currency.PEN, InterestRateType.TEA);
        plan.setAnnualInterestRate(12.0);

        when(paymentPlanQueryService.handle(ArgumentMatchers.any(GetAllPaymentPlansQuery.class))).thenReturn(List.of(plan));

        mockMvc.perform(get("/api/v1/payment-plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assetSalePrice").value(100000.0));
    }
}
