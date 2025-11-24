package apx.inc.finance_web_services.plan.domain.model.aggregates;

import apx.inc.finance_web_services.plan.domain.model.commands.CreatePaymentPlanCommand;
import apx.inc.finance_web_services.plan.domain.model.entities.Installment;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.Currency;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.InterestRateConfig;
import apx.inc.finance_web_services.plan.domain.model.valueobjects.InterestRateType;
import apx.inc.finance_web_services.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class PaymentPlan extends AuditableAbstractAggregateRoot<PaymentPlan> {
    // Datos de entrada del préstamo
    private double assetSalePrice;           // Precio de Venta del Activo
    private double downPaymentPercentage;    // % Cuota Inicial
    private int years;                       // Nº de Años
    private int paymentFrequency;            // Frecuencia de pago (días)
    private int daysPerYear;                 // Nº de días por año

    // Costos iniciales
    private double notarialCosts;            // Costes Notariales
    private double registryCosts;            // Costes Registrales
    private double appraisal;                // Tasación
    private double studyCommission;          // Comisión de estudio
    private double activationCommission;     // Comisión activación

    // Costos periódicos
    private double periodicCommission;       // Comisión periodica
    private double postage;                  // Portes
    private double administrationFees;       // Gastos de Administración
    private double creditLifeInsurance;      // % de Seguro desgravamen
    private double riskInsurance;            // % de Seguro riesgo

    // Costo de oportunidad
    private double discountRate;             // Tasa de descuento


    @Enumerated(EnumType.STRING)
    private Currency currency;                  // Moneda

    // Configuración
    @Enumerated(EnumType.STRING)
    private InterestRateType interestRateType; // Tipo de tasa
    private double annualInterestRate;
    @ElementCollection
    private List<InterestRateConfig> interestRateConfigs = new ArrayList<>();

    // Resultados calculados
    private double financedBalance;          // Saldo a financiar
    private double loanAmount;               // Monto del préstamo
    private int installmentsPerYear;         // Nº Cuotas por Año
    private int totalInstallments;           // Nº Total de Cuotas
    private double periodicCreditLifeInsurance; // % de Seguro desgrav. per.
    private double periodicRiskInsurance;    // Seguro riesgo periódico

    // Cuotas del plan
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true) // ✅ FALTABA ESTA ANOTACIÓN
    @JoinColumn(name = "payment_plan_id")
    private List<Installment> installments = new ArrayList<>();

    // Totales
    private double totalInterest;
    private double totalAmortization;
    private double totalCreditLifeInsurance;
    private double totalRiskInsurance;
    private double totalCommissions;
    private double totalPostageAndFees;

    // Indicadores de rentabilidad
    private double periodicDiscountRate;
    private double irr;                      // TIR
    private double effectiveAnnualCostRate;  // TCEA
    private double npv;                      // VAN

      // Campo básico como los otros

    // NEW FIELDS
    private Long clientId;
    private Long propertyId;
    private Long salesManId;
    private double bonusAmount;




    protected PaymentPlan() {
        // Default constructor for JPA
    }

    // Constructor
    public PaymentPlan(double assetSalePrice, double downPaymentPercentage, int years,
                       int paymentFrequency, int daysPerYear, Currency currency,
                       InterestRateType interestRateType) {
        this.assetSalePrice = assetSalePrice;
        this.downPaymentPercentage = downPaymentPercentage;
        this.years = years;
        this.paymentFrequency = paymentFrequency;
        this.daysPerYear = daysPerYear;
        this.currency = currency;
        this.interestRateType = interestRateType;

        // Inicializar costos en 0
        initializeCostsToZero();
    }

    private void initializeCostsToZero() {
        this.notarialCosts = 0.0;
        this.registryCosts = 0.0;
        this.appraisal = 0.0;
        this.studyCommission = 0.0;
        this.activationCommission = 0.0;
        this.periodicCommission = 0.0;
        this.postage = 0.0;
        this.administrationFees = 0.0;
        this.creditLifeInsurance = 0.0;
        this.riskInsurance = 0.0;
        this.discountRate = 0.0;
    }

    public PaymentPlan(CreatePaymentPlanCommand command,
                       Double assetSalePrice,
                       Currency currency,
                       Double bonusAmount) {

        // Datos que vienen del Property (NO del Command)
        this.assetSalePrice = assetSalePrice;
        this.currency = currency;

        // Datos del bono
        this.bonusAmount = bonusAmount;

        // TODOS los datos del Command
        this.downPaymentPercentage = command.downPaymentPercentage();
        this.years = command.years();
        this.paymentFrequency = command.paymentFrequency();
        this.daysPerYear = command.daysPerYear();
        this.interestRateType = command.interestRateType();

        // Costos del Command
        this.notarialCosts = command.notarialCosts();
        this.registryCosts = command.registryCosts();
        this.appraisal = command.appraisal();
        this.studyCommission = command.studyCommission();
        this.activationCommission = command.activationCommission();
        this.periodicCommission = command.periodicCommission();
        this.postage = command.postage();
        this.administrationFees = command.administrationFees();
        this.creditLifeInsurance = command.creditLifeInsurance();
        this.riskInsurance = command.riskInsurance();
        this.discountRate = command.discountRate();

        this.annualInterestRate = command.annualInterestRate();
        this.interestRateConfigs = new ArrayList<>(command.interestRateConfigs());

        // Referencias nuevas del Command
        this.clientId = command.clientId();
        this.propertyId = command.propertyId();
        this.salesManId = command.salesManId();

        initializeCalculatedValuesToZero();
    }

    private void initializeCalculatedValuesToZero() {
        this.financedBalance = 0.0;
        this.loanAmount = 0.0;
        this.installmentsPerYear = 0;
        this.totalInstallments = 0;
        this.periodicCreditLifeInsurance = 0.0;
        this.periodicRiskInsurance = 0.0;
        this.totalInterest = 0.0;
        this.totalAmortization = 0.0;
        this.totalCreditLifeInsurance = 0.0;
        this.totalRiskInsurance = 0.0;
        this.totalCommissions = 0.0;
        this.totalPostageAndFees = 0.0;
        this.periodicDiscountRate = 0.0;
        this.irr = 0.0;
        this.effectiveAnnualCostRate = 0.0;
        this.npv = 0.0;
    }

    // Método de negocio: Agregar cuota al plan
    public void addInstallment(Installment installment) {
        // No contar la cuota 0 (desembolso) en la validación
        int installmentsWithoutDisbursement = (int) installments.stream()
                .filter(inst -> inst.getNumber() > 0)
                .count();

        if (installmentsWithoutDisbursement >= totalInstallments) {
            throw new IllegalStateException("No se pueden agregar más cuotas del total permitido");
        }
        this.installments.add(installment);
    }

    // Método de negocio: Calcular valores iniciales
    public void calculateInitialValues() {
        // Saldo a financiar = Precio Venta * (1 - % Cuota Inicial)
        this.financedBalance = assetSalePrice * (1 - downPaymentPercentage / 100.0);

        // Monto préstamo = Saldo financiar + Suma costos iniciales
        double totalInitialCosts = notarialCosts + registryCosts + appraisal +
                studyCommission + activationCommission;
        this.loanAmount = financedBalance + totalInitialCosts;

        // Número de cuotas
        this.installmentsPerYear = daysPerYear / paymentFrequency;
        this.totalInstallments = installmentsPerYear * years;

        // Seguros periódicos
        this.periodicCreditLifeInsurance = (creditLifeInsurance / 100.0) * (paymentFrequency / 30.0) * 0.9;
        this.periodicRiskInsurance = (riskInsurance / 100.0) * assetSalePrice / installmentsPerYear;

        this.periodicDiscountRate = calculatePeriodicDiscountRate();
    }

    private double calculatePeriodicDiscountRate() {
        if (discountRate == 0) {
            return 0;
        }
        // ✅ CORREGIR: discountRate es 27% ANUAL, convertir a periódica
        double annualDiscountRateDecimal = discountRate / 100.0; // 0.27

        double frequencyRatio = (double) paymentFrequency / daysPerYear; // 90/360 = 0.25

        // Fórmula: (1 + TEA)^(frecuencia/días_año) - 1
        double periodicRate = Math.pow(1 + annualDiscountRateDecimal, frequencyRatio) - 1;

        // Debería dar: (1.27)^0.25 - 1 ≈ 0.0615756 (6.15756%)
        return periodicRate;
    }

    public double getAnnualRateForInstallment(int installmentNumber) {
        if (interestRateConfigs == null || interestRateConfigs.isEmpty()) {
            return annualInterestRate; // Si no hay cambios, usar tasa base
        }

        // Buscar el último cambio aplicable
        return interestRateConfigs.stream()
                .filter(config -> config.installmentNumber() <= installmentNumber)
                .reduce((first, second) -> second)
                .map(InterestRateConfig::newAnnualRate)
                .orElse(annualInterestRate);
    }

    public double getPeriodicRateForInstallment(int installmentNumber) {
        double annualRate = getAnnualRateForInstallment(installmentNumber);

        // ✅ CORREGIR: Convertir porcentaje a decimal si es necesario
        double annualRateDecimal = annualRate;

        // Si la tasa viene como 11 (11%), convertir a 0.11
        if (annualRate > 1) {
            annualRateDecimal = annualRate / 100.0;
            System.out.println("DEBUG: Converted annualRate " + annualRate + " to " + annualRateDecimal);
        }

        // ✅ FÓRMULA CORRECTA: (1 + TEA)^(frecuencia/días_año) - 1
        double frequencyRatio = (double) paymentFrequency / daysPerYear;
        double exactPeriodicRate = Math.pow(1 + annualRateDecimal, frequencyRatio) - 1;

        System.out.println("DEBUG: annualRateDecimal=" + annualRateDecimal +
                ", frequencyRatio=" + frequencyRatio +
                ", periodicRate=" + exactPeriodicRate);

        // ✅ REDONDEAR como tu Excel (5 decimales)
        return Math.round(exactPeriodicRate * 100000.0) / 100000.0;
    }

    // Getters y Setters para costos
    public void setInitialCosts(double notarialCosts, double registryCosts, double appraisal,
                                double studyCommission, double activationCommission) {
        this.notarialCosts = notarialCosts;
        this.registryCosts = registryCosts;
        this.appraisal = appraisal;
        this.studyCommission = studyCommission;
        this.activationCommission = activationCommission;
    }

    public void setPeriodicCosts(double periodicCommission, double postage,
                                 double administrationFees, double creditLifeInsurance,
                                 double riskInsurance) {
        this.periodicCommission = periodicCommission;
        this.postage = postage;
        this.administrationFees = administrationFees;
        this.creditLifeInsurance = creditLifeInsurance;
        this.riskInsurance = riskInsurance;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }



    // Getters
    public double getAssetSalePrice() { return assetSalePrice; }
    public double getDownPaymentPercentage() { return downPaymentPercentage; }
    public int getYears() { return years; }
    public int getPaymentFrequency() { return paymentFrequency; }
    public int getDaysPerYear() { return daysPerYear; }
    public double getFinancedBalance() { return financedBalance; }
    public double getLoanAmount() { return loanAmount; }
    public int getInstallmentsPerYear() { return installmentsPerYear; }
    public int getTotalInstallments() { return totalInstallments; }
    public List<Installment> getInstallments() { return installments; }
    public Currency getCurrency() { return currency; }
    public InterestRateType getInterestRateType() { return interestRateType; }
}
