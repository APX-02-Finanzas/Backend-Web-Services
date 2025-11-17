package apx.inc.finance_web_services.client.application.internal.commandservices;

import apx.inc.finance_web_services.client.domain.model.aggregates.Client;
import apx.inc.finance_web_services.client.domain.model.commands.CreateClientCommand;
import apx.inc.finance_web_services.client.domain.model.commands.DeleteClientCommand;
import apx.inc.finance_web_services.client.domain.model.commands.MarkClientAsPreviousStateHousingCommand;
import apx.inc.finance_web_services.client.domain.model.commands.UpdateClientCommand;
import apx.inc.finance_web_services.client.domain.services.ClientCommandService;
import apx.inc.finance_web_services.client.infrastructure.persistence.jpa.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientCommandServiceImpl implements ClientCommandService {

    private final ClientRepository clientRepository;

    @Override
    public Long handle(CreateClientCommand command) {
        Client client = new Client(command);
        return clientRepository.save(client).getId();
    }

    @Override
    public void handle(UpdateClientCommand command) {
        Client client = clientRepository.findById(command.clientId())
                .orElseThrow(() -> new IllegalArgumentException("Client not found with id: " + command.clientId()));
        client.update(command);
        clientRepository.save(client);
    }

    @Override
    public void handle(MarkClientAsPreviousStateHousingCommand command) {
        Client client = clientRepository.findById(command.clientId())
                .orElseThrow(() -> new IllegalArgumentException("Client not found with id: " + command.clientId()));
        client.markAsPreviousStateHousing();
        clientRepository.save(client);
    }

    public void handle(DeleteClientCommand command) {
        clientRepository.deleteById(command.clientId());
    }
}
