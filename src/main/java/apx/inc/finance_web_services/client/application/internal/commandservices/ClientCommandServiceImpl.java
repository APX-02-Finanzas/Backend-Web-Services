package apx.inc.finance_web_services.client.application.internal.commandservices;

import apx.inc.finance_web_services.client.application.internal.AESEncryptionService;
import apx.inc.finance_web_services.client.application.internal.outboundservices.ExternalIamService;
import apx.inc.finance_web_services.client.domain.model.aggregates.Client;
import apx.inc.finance_web_services.client.domain.model.commands.CreateClientCommand;
import apx.inc.finance_web_services.client.domain.model.commands.DeleteClientCommand;
import apx.inc.finance_web_services.client.domain.model.commands.MarkClientAsPreviousStateHousingCommand;
import apx.inc.finance_web_services.client.domain.model.commands.UpdateClientCommand;
import apx.inc.finance_web_services.client.domain.services.ClientCommandService;
import apx.inc.finance_web_services.client.infrastructure.persistence.jpa.repositories.ClientRepository;
import apx.inc.finance_web_services.iam.application.internal.outboundservices.hashing.HashingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientCommandServiceImpl implements ClientCommandService {

    private final ClientRepository clientRepository;
    private final ExternalIamService externalIamService;
    private final AESEncryptionService encryptionService; // ✅ NUEVO servicio

    @Override
    public Long handle(CreateClientCommand command) {
        externalIamService.fetchSalesManUserById(command.salesManId());

        // ✅ ENCRIPTAR datos sensibles (reversible)
        String encryptedName = encryptionService.encrypt(command.name());
        String encryptedSurname = encryptionService.encrypt(command.surname());
        String encryptedDni = encryptionService.encrypt(command.dni());

        var encryptedCommand = new CreateClientCommand(
                encryptedName,      // ✅ Nombre encriptado
                encryptedSurname,   // ✅ Apellido encriptado
                command.civilState(),
                command.email(),
                command.phone(),
                encryptedDni,       // ✅ DNI encriptado
                command.salesManId(),
                command.monthlyIncome(),
                command.monthlyExpenses()
        );

        Client client = new Client(encryptedCommand);
        return clientRepository.save(client).getId();
    }

    @Override
    public void handle(UpdateClientCommand command) {
        Client client = clientRepository.findById(command.clientId())
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        // ✅ Encriptar solo los campos que se actualizan
        String encryptedName = command.name() != null ?
                encryptionService.encrypt(command.name()) : client.getName();

        String encryptedSurname = command.surname() != null ?
                encryptionService.encrypt(command.surname()) : client.getSurname();

        String encryptedDni = command.dni() != null ?
                encryptionService.encrypt(command.dni()) : client.getDni();

        var encryptedCommand = new UpdateClientCommand(
                command.clientId(),
                encryptedName,
                encryptedSurname,
                command.civilState() != null ? command.civilState() : client.getCivilState(),
                command.email() != null ? command.email() : client.getEmail(),
                command.phone() != null ? command.phone() : client.getPhone(),
                encryptedDni,
                command.monthlyIncome(),
                command.monthlyExpenses()
        );

        client.update(encryptedCommand);
        clientRepository.save(client);
    }

    @Override
    public void handle(MarkClientAsPreviousStateHousingCommand command) {
        Client client = clientRepository.findById(command.clientId())
                .orElseThrow(() -> new IllegalArgumentException("Client not found with id: " + command.clientId()));
        client.markAsPreviousStateHousing();
        clientRepository.save(client);
    }

    @Override
    public void handle(DeleteClientCommand command) {
        clientRepository.deleteById(command.clientId());
    }
}
