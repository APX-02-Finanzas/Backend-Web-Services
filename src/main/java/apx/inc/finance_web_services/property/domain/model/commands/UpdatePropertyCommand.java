package apx.inc.finance_web_services.property.domain.model.commands;

import apx.inc.finance_web_services.plan.domain.model.valueobjects.Currency;

public record UpdatePropertyCommand(
        Long propertyId,  // Para identificar cuál actualizar
        String title,
        String description,
        double price,
        Currency currency,
        String address
) {}