package apx.inc.finance_web_services.plan.interfaces.rest.transform;

import apx.inc.finance_web_services.plan.domain.model.entities.Installment;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.GracePeriodType;
import apx.inc.finance_web_services.plan.interfaces.rest.resources.InstallmentResource;

public class InstallmentResourceFromEntityAssembler {
    public static InstallmentResource toResourceFromEntity(Installment installment){
     return new InstallmentResource(
        installment.getNumber(),// A: Nº
        installment.getAnnualEffectiveRate(),  // B: TEA
        installment.getPeriodEffectiveRate(),      // C: i' = TEP = TEM
        installment.getAnnualInflation(),     // D: IA
        installment.getPeriodInflation(),        // E: IP
        installment.getGracePeriodType(), // F: P.G. (T, P, S)
        installment.getInitialBalance(),           // G: Saldo Inicial
        installment.getIndexedInitialBalance(),    // H: Saldo Inicial Indexado
        installment.getInterest(),                 // I: Interes
        installment.getInstallmentAmount(),        // J: Cuota (inc Seg Des)
        installment.getAmortization(),             // K: Amort.
        installment.getPrepayment(),               // L: Prepago
        installment.getCreditLifeInsurance(),      // M: Seguro desgrav
        installment.getRiskInsurance(),            // N: Seguro riesgo
        installment.getCommission(),               // O: Comision
        installment.getPostage(),                  // P: Portes
        installment.getAdminFees(),                // Q: Gastos Adm.
        installment.getFinalBalance(),             // R: Saldo Final
        installment.getCashFlow()                 // S: Flujo
     );
    }
}
