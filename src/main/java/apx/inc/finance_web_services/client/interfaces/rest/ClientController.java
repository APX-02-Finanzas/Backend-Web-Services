package apx.inc.finance_web_services.client.interfaces.rest;

import apx.inc.finance_web_services.client.application.internal.AESEncryptionService;
import apx.inc.finance_web_services.client.domain.model.aggregates.Client;
import apx.inc.finance_web_services.client.domain.model.commands.CreateClientCommand;
import apx.inc.finance_web_services.client.domain.model.commands.DeleteClientCommand;
import apx.inc.finance_web_services.client.domain.model.commands.MarkClientAsPreviousStateHousingCommand;
import apx.inc.finance_web_services.client.domain.model.commands.UpdateClientCommand;
import apx.inc.finance_web_services.client.domain.model.queries.GetAllClientsQuery;
import apx.inc.finance_web_services.client.domain.model.queries.GetClientByIdQuery;
import apx.inc.finance_web_services.client.domain.model.queries.GetClientsByUserIdQuery;
import apx.inc.finance_web_services.client.domain.services.ClientCommandService;
import apx.inc.finance_web_services.client.domain.services.ClientQueryService;
import apx.inc.finance_web_services.client.interfaces.rest.resources.ClientResource;
import apx.inc.finance_web_services.client.interfaces.rest.resources.CreateClientResource;
import apx.inc.finance_web_services.client.interfaces.rest.resources.UpdateClientResource;
import apx.inc.finance_web_services.client.interfaces.rest.transform.ClientResourceFromEntityAssembler;
import apx.inc.finance_web_services.client.interfaces.rest.transform.CreateClientCommandFromResourceAssembler;
import apx.inc.finance_web_services.client.interfaces.rest.transform.UpdateClientCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Client Management Endpoints")
public class ClientController {

    private final ClientCommandService clientCommandService;
    private final ClientQueryService clientQueryService;
    private final AESEncryptionService encryptionService;

    @PostMapping
    public ResponseEntity<ClientResource> createClient(@RequestBody CreateClientResource resource) {
        try {
            CreateClientCommand command = CreateClientCommandFromResourceAssembler.toCommandFromResource(resource);
            Long clientId = clientCommandService.handle(command);

            Client client = clientQueryService.handle(new GetClientByIdQuery(clientId));
            ClientResource clientResource = ClientResourceFromEntityAssembler.toResourceFromEntity(client);

            return ResponseEntity.status(HttpStatus.CREATED).body(clientResource);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<ClientResource> updateClient(@PathVariable Long clientId,
                                                       @RequestBody UpdateClientResource resource) {
        try {
            UpdateClientCommand command = UpdateClientCommandFromResourceAssembler.toCommandFromResource(clientId, resource);
            clientCommandService.handle(command);

            Client client = clientQueryService.handle(new GetClientByIdQuery(clientId));
            ClientResource clientResource = ClientResourceFromEntityAssembler.toResourceFromEntity(client);

            return ResponseEntity.ok(clientResource);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long clientId) {
        try {
            clientCommandService.handle(new DeleteClientCommand(clientId));
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResource> getClientById(@PathVariable Long clientId) {
        try {
            Client client = clientQueryService.handle(new GetClientByIdQuery(clientId));
            ClientResource clientResource = ClientResourceFromEntityAssembler.toResourceFromEntityStatic(client, encryptionService);
            return ResponseEntity.ok(clientResource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ClientResource>> getAllClients() {
        List<Client> clients = clientQueryService.handle(new GetAllClientsQuery());
        List<ClientResource> resources = clients.stream()
                .map(client -> ClientResourceFromEntityAssembler.toResourceFromEntityStatic(client, encryptionService))
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/salesman/{salesManId}")
    public ResponseEntity<List<ClientResource>> getClientsBySalesManId(@PathVariable Long salesManId) {
        List<Client> clients = clientQueryService.handle(new GetClientsByUserIdQuery(salesManId));
        List<ClientResource> resources = clients.stream()
                .map(client -> ClientResourceFromEntityAssembler.toResourceFromEntityStatic(client, encryptionService))
                .toList();
        return ResponseEntity.ok(resources);
    }

    @PatchMapping("/{clientId}/mark-previous-housing")
    public ResponseEntity<ClientResource> markClientAsPreviousStateHousing(@PathVariable Long clientId) {
        try {
            clientCommandService.handle(new MarkClientAsPreviousStateHousingCommand(clientId));

            Client client = clientQueryService.handle(new GetClientByIdQuery(clientId));
            ClientResource clientResource = ClientResourceFromEntityAssembler.toResourceFromEntity(client);

            return ResponseEntity.ok(clientResource);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}