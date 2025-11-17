package apx.inc.finance_web_services.plan.domain.model.entities;

import apx.inc.finance_web_services.plan.domain.model.valueobjects.GracePeriodType;
import apx.inc.finance_web_services.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Installment extends AuditableAbstractAggregateRoot<Installment> {

    private int number;                      // A: Nº
    private double annualEffectiveRate;      // B: TEA
    private double periodEffectiveRate;      // C: i' = TEP = TEM
    private double annualInflation;          // D: IA
    private double periodInflation;          // E: IP
    @Enumerated(EnumType.STRING)
    private GracePeriodType gracePeriodType; // F: P.G. (T, P, S)
    private double initialBalance;           // G: Saldo Inicial
    private double indexedInitialBalance;    // H: Saldo Inicial Indexado
    private double interest;                 // I: Interes
    private double installmentAmount;        // J: Cuota (inc Seg Des)
    private double amortization;             // K: Amort.
    private double prepayment;               // L: Prepago
    private double creditLifeInsurance;      // M: Seguro desgrav
    private double riskInsurance;            // N: Seguro riesgo
    private double commission;               // O: Comision
    private double postage;                  // P: Portes
    private double adminFees;                // Q: Gastos Adm.
    private double finalBalance;             // R: Saldo Final
    private double cashFlow;                 // S: Flujo


    protected Installment() {}

    // Constructor
    public Installment(int number, GracePeriodType gracePeriodType) {
        this.number = number;
        this.gracePeriodType = gracePeriodType;
        // Inicializar todos los valores en 0
        initializeToZero();
    }

    private void initializeToZero() {
        this.annualEffectiveRate = 0.0;
        this.periodEffectiveRate = 0.0;
        this.annualInflation = 0.0;
        this.periodInflation = 0.0;
        this.initialBalance = 0.0;
        this.indexedInitialBalance = 0.0;
        this.interest = 0.0;
        this.installmentAmount = 0.0;
        this.amortization = 0.0;
        this.prepayment = 0.0;
        this.creditLifeInsurance = 0.0;
        this.riskInsurance = 0.0;
        this.commission = 0.0;
        this.postage = 0.0;
        this.adminFees = 0.0;
        this.finalBalance = 0.0;
        this.cashFlow = 0.0;
    }

    // Método de negocio: Calcular flujo según tipo de periodo de gracia
    public void calculateCashFlow() {
        // Si ya se calculó manualmente (en grace periods), no sobreescribir
        if (this.cashFlow != 0) {
            return; // Mantener el valor calculado manualmente
        }

        // Solo calcular automáticamente si no se ha establecido
        if (this.number == 0) {
            // Cuota 0: desembolso (flujo positivo)
            this.cashFlow = this.installmentAmount;
        } else {
            // Cuotas 1+: pago (flujo negativo) - INCLUIR TODOS LOS COMPONENTES
            this.cashFlow = - (this.installmentAmount +
                    this.creditLifeInsurance +
                    this.riskInsurance +
                    this.commission +
                    this.postage +
                    this.adminFees);
        }
    }




}