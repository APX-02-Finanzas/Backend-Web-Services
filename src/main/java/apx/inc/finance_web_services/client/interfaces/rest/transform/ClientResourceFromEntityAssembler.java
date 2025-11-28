package apx.inc.finance_web_services.client.interfaces.rest.transform;

import apx.inc.finance_web_services.client.application.internal.AESEncryptionService;
import apx.inc.finance_web_services.client.domain.model.aggregates.Client;
import apx.inc.finance_web_services.client.interfaces.rest.resources.ClientResource;

public class ClientResourceFromEntityAssembler {

    public static ClientResource toResourceFromEntity(Client client) {
        return new ClientResource(
                client.getId(),
                client.getName(),
                client.getSurname(),
                client.getCivilState(),
                client.getEmail(),
                client.getPhone(),
                client.getDni(),
                client.getSalesManId(),
                client.getMonthlyIncome(),
                client.getMonthlyExpenses(),
                client.isHasPreviousStateHousing(),
                client.canApplyForBono()
        );
    }

    public static ClientResource toResourceFromEntityStatic(Client client, AESEncryptionService encryptionService) {
        return new ClientResource(
                client.getId(),
                encryptionService.decrypt(client.getName()),
                encryptionService.decrypt(client.getSurname()),
                client.getCivilState(),
                client.getEmail(),
                client.getPhone(),
                encryptionService.decrypt(client.getDni()),
                client.getSalesManId(),
                client.getMonthlyIncome(),
                client.getMonthlyExpenses(),
                client.isHasPreviousStateHousing(),
                client.canApplyForBono()
        );
    }
}