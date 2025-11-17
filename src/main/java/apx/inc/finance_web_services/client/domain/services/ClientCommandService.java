package apx.inc.finance_web_services.client.domain.services;

import apx.inc.finance_web_services.client.domain.model.commands.CreateClientCommand;
import apx.inc.finance_web_services.client.domain.model.commands.DeleteClientCommand;
import apx.inc.finance_web_services.client.domain.model.commands.MarkClientAsPreviousStateHousingCommand;
import apx.inc.finance_web_services.client.domain.model.commands.UpdateClientCommand;

public interface ClientCommandService {

    Long handle(CreateClientCommand command);

    void handle(UpdateClientCommand command);

    void handle(MarkClientAsPreviousStateHousingCommand command);

    void handle(DeleteClientCommand command);

}
