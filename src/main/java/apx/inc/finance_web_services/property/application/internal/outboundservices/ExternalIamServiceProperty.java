package apx.inc.finance_web_services.property.application.internal.outboundservices;

import apx.inc.finance_web_services.iam.interfaces.acl.IamContextFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalIamServiceProperty {
    private final IamContextFacade iamContextFacade;

    public void fetchSalesManUserById(Long salesManId) {
        if (salesManId == null) {
            throw new IllegalArgumentException("SalesManId cannot be null");
        }

        var salesmanOpt = iamContextFacade.fetchSalesManUserById(salesManId);
        if (salesmanOpt.isEmpty()) {
            log.error("Usuario con ID {} no existe o no tiene rol ROLE_SALESMAN", salesManId);
            throw new IllegalArgumentException("Salesman not found or doesn't have required role: " + salesManId);
        }

        log.debug("Salesman validado correctamente: {}", salesManId);
    }
}
