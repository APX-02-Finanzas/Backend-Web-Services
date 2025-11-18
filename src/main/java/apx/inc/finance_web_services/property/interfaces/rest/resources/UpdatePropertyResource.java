package apx.inc.finance_web_services.property.interfaces.rest.resources;

import apx.inc.finance_web_services.plan.domain.model.valueobjects.Currency;

public record UpdatePropertyResource(
        String title,
        String description,
        double price,
        Currency currency,
        String address
) {}