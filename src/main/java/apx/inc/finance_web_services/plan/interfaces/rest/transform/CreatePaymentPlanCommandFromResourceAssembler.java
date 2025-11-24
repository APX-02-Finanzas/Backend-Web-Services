package apx.inc.finance_web_services.plan.interfaces.rest.transform;

import apx.inc.finance_web_services.plan.domain.model.commands.CreatePaymentPlanCommand;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.*;
import apx.inc.finance_web_services.plan.interfaces.rest.resources.CreatePaymentPlanResource;

import java.util.List;

public class CreatePaymentPlanCommandFromResourceAssembler {
    public static CreatePaymentPlanCommand toCommandFromResource( CreatePaymentPlanResource resource) {
        return new CreatePaymentPlanCommand(
                resource.clientId(),
                resource.propertyId(),
                resource.salesManId(),

//                resource.assetSalePrice(),           // Precio de Venta del Activo
                resource.downPaymentPercentage(),    // % Cuota Inicial
                resource.years(),                    // Nº de Años
                resource.paymentFrequency(),         // Frecuencia de pago (días)
                resource.daysPerYear(),              // Nº de días por año

                resource.notarialCosts(),            // Costes Notariales
                resource.registryCosts(),            // Costes Registrales
                resource.appraisal(),                // Tasación
                resource.studyCommission(),          // Comisión de estudio
                resource.activationCommission(),     // Comisión activación


                resource.periodicCommission(),       // Comisión periodica
                resource.postage(),                  // Portes
                resource.administrationFees(),       // Gastos de Administración
                resource.creditLifeInsurance(),      // % de Seguro desgravamen
                resource.riskInsurance(),            // % de Seguro riesgo


                resource.discountRate(),             // Tasa de descuento


                resource.interestRateType(),         // Tipo de tasa
                resource.annualInterestRate(),
                resource.interestRateConfigs(),

                resource.gracePeriods(),


                resource.prepayments()



        );
    }
}
