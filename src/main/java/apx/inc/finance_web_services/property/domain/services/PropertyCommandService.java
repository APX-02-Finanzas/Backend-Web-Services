package apx.inc.finance_web_services.property.domain.services;

import apx.inc.finance_web_services.property.domain.model.commands.CreatePropertyCommand;
import apx.inc.finance_web_services.property.domain.model.commands.DeletePropertyCommand;
import apx.inc.finance_web_services.property.domain.model.commands.UpdatePropertyCommand;

public interface PropertyCommandService {
    Long handle(CreatePropertyCommand command);
    void handle(UpdatePropertyCommand command);
    void handle(DeletePropertyCommand command);
}
