package apx.inc.finance_web_services.plan.interfaces.rest;

import apx.inc.finance_web_services.plan.domain.model.aggregates.PaymentPlan;
import apx.inc.finance_web_services.plan.domain.model.commands.CreatePaymentPlanCommand;
import apx.inc.finance_web_services.plan.domain.model.commands.DeletePaymentPlanCommand;
import apx.inc.finance_web_services.plan.domain.model.commands.UpdatePaymentPlanCommand;
import apx.inc.finance_web_services.plan.domain.model.entities.Installment;
import apx.inc.finance_web_services.plan.domain.model.queries.GetAllPaymentPlansQuery;
import apx.inc.finance_web_services.plan.domain.model.queries.GetInstallmentsByPaymentPlanIdQuery;
import apx.inc.finance_web_services.plan.domain.model.queries.GetPaymentPlanByIdQuery;
import apx.inc.finance_web_services.plan.domain.services.PaymentPlanCommandService;
import apx.inc.finance_web_services.plan.domain.services.PaymentPlanQueryService;
import apx.inc.finance_web_services.plan.interfaces.rest.resources.CreatePaymentPlanResource;
import apx.inc.finance_web_services.plan.interfaces.rest.resources.InstallmentResource;
import apx.inc.finance_web_services.plan.interfaces.rest.resources.PaymentPlanResource;
import apx.inc.finance_web_services.plan.interfaces.rest.resources.UpdatePaymentPlanResource;
import apx.inc.finance_web_services.plan.interfaces.rest.transform.CreatePaymentPlanCommandFromResourceAssembler;
import apx.inc.finance_web_services.plan.interfaces.rest.transform.InstallmentResourceFromEntityAssembler;
import apx.inc.finance_web_services.plan.interfaces.rest.transform.PaymentPlanResourceFromEntityAssembler;
import apx.inc.finance_web_services.plan.interfaces.rest.transform.UpdatePaymentPlanCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/payment-plans", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Payment Plans", description = "Operations related to payment plans")
public class PaymentPlansController {

    private final PaymentPlanCommandService paymentPlanCommandService;
    private final PaymentPlanQueryService paymentPlanQueryService;

    public PaymentPlansController(PaymentPlanCommandService paymentPlanCommandService,
                                  PaymentPlanQueryService paymentPlanQueryService) {
        this.paymentPlanCommandService = paymentPlanCommandService;
        this.paymentPlanQueryService = paymentPlanQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a new payment plan", description = "Creates a new payment plan with the provided financial details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment plan created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentPlanResource> createPaymentPlan(@RequestBody CreatePaymentPlanResource createPaymentPlanResource) {
        try {
            CreatePaymentPlanCommand createPaymentPlanCommand = CreatePaymentPlanCommandFromResourceAssembler.toCommandFromResource(createPaymentPlanResource);
            Long paymentPlanId = paymentPlanCommandService.handle(createPaymentPlanCommand);

            if (paymentPlanId == null || paymentPlanId == 0L) {
                return ResponseEntity.badRequest().build();
            }

            GetPaymentPlanByIdQuery getPaymentPlanByIdQuery = new GetPaymentPlanByIdQuery(paymentPlanId);
            PaymentPlan paymentPlan = paymentPlanQueryService.handle(getPaymentPlanByIdQuery);

            PaymentPlanResource paymentPlanResponse = PaymentPlanResourceFromEntityAssembler.toResourceFromEntity(paymentPlan);
            return new ResponseEntity<>(paymentPlanResponse, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{paymentPlanId}")
    @Operation(summary = "Update an existing payment plan", description = "Updates the details of an existing payment plan and recalculates all financial values.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment plan updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Payment plan not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentPlanResource> updatePaymentPlan(
            @PathVariable Long paymentPlanId,
            @RequestBody UpdatePaymentPlanResource updatePaymentPlanResource) {

        try {
            UpdatePaymentPlanCommand updatePaymentPlanCommand = UpdatePaymentPlanCommandFromResourceAssembler
                    .toCommandFromResource(paymentPlanId, updatePaymentPlanResource);

            paymentPlanCommandService.handle(updatePaymentPlanCommand);

            GetPaymentPlanByIdQuery getPaymentPlanByIdQuery = new GetPaymentPlanByIdQuery(paymentPlanId);
            PaymentPlan updatedPaymentPlan = paymentPlanQueryService.handle(getPaymentPlanByIdQuery);

            PaymentPlanResource paymentPlanResponse = PaymentPlanResourceFromEntityAssembler.toResourceFromEntity(updatedPaymentPlan);
            return ResponseEntity.ok(paymentPlanResponse);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{paymentPlanId}")
    @Operation(summary = "Delete a payment plan", description = "Deletes an existing payment plan by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Payment plan deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Payment plan not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deletePaymentPlan(@PathVariable Long paymentPlanId) {
        try {
            DeletePaymentPlanCommand deletePaymentPlanCommand = new DeletePaymentPlanCommand(paymentPlanId);
            paymentPlanCommandService.handle(deletePaymentPlanCommand);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{paymentPlanId}")
    @Operation(summary = "Get a payment plan by ID", description = "Retrieves the complete details of a payment plan by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment plan retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Payment plan not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentPlanResource> getPaymentPlanById(@PathVariable Long paymentPlanId) {
        try {
            GetPaymentPlanByIdQuery getPaymentPlanByIdQuery = new GetPaymentPlanByIdQuery(paymentPlanId);
            PaymentPlan paymentPlan = paymentPlanQueryService.handle(getPaymentPlanByIdQuery);

            PaymentPlanResource paymentPlanResponse = PaymentPlanResourceFromEntityAssembler.toResourceFromEntity(paymentPlan);
            return ResponseEntity.ok(paymentPlanResponse);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    @Operation(summary = "Get all payment plans", description = "Retrieves a list of all payment plans.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment plans retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PaymentPlanResource>> getAllPaymentPlans() {
        try {
            List<PaymentPlan> paymentPlans = paymentPlanQueryService.handle(new GetAllPaymentPlansQuery());

            List<PaymentPlanResource> paymentPlanResources = paymentPlans.stream()
                    .map(PaymentPlanResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();

            return ResponseEntity.ok(paymentPlanResources);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{paymentPlanId}/installments")
    @Operation(summary = "Get all installments by payment plan ID", description = "Retrieves all installments for a specific payment plan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Installments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Payment plan not found or has no installments"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<InstallmentResource>> getInstallmentsByPaymentPlanId(@PathVariable Long paymentPlanId) {
        try {
            GetInstallmentsByPaymentPlanIdQuery getInstallmentsByPaymentPlanIdQuery = new GetInstallmentsByPaymentPlanIdQuery(paymentPlanId);
            List<Installment> installments = paymentPlanQueryService.handle(getInstallmentsByPaymentPlanIdQuery);

            List<InstallmentResource> installmentResources = installments.stream()
                    .map(InstallmentResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();

            return ResponseEntity.ok(installmentResources);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}