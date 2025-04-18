/**
 * AdvancedTaxCalculator implements a more complex tax calculation algorithm
 * This demonstrates polymorphism by providing an alternative implementation
 * of the TaxCalculator interface
 */
public class AdvancedTaxCalculator implements TaxCalculator {
    private double personalAllowance;
    private double[] taxBrackets;
    private double[] taxRates;
    
    /**
     * Constructor with default tax brackets and rates
     */
    public AdvancedTaxCalculator() {
        // Default personal allowance
        this.personalAllowance = 60000;
        
        // Tax brackets (upper limits)
        this.taxBrackets = new double[] {
            150000,  // 0% bracket
            300000,  // 5% bracket
            500000,  // 10% bracket
            750000,  // 15% bracket
            1000000, // 20% bracket
            2000000, // 25% bracket
            Double.MAX_VALUE // 30% bracket
        };
        
        // Tax rates for each bracket
        this.taxRates = new double[] {
            0.00, // 0%
            0.05, // 5%
            0.10, // 10%
            0.15, // 15%
            0.20, // 20%
            0.25, // 25%
            0.30  // 30%
        };
    }
    
    /**
     * Constructor with custom tax parameters
     * @param personalAllowance Personal tax allowance amount
     * @param taxBrackets Array of tax bracket upper limits
     * @param taxRates Array of tax rates for each bracket
     */
    public AdvancedTaxCalculator(double personalAllowance, double[] taxBrackets, double[] taxRates) {
        if (taxBrackets.length != taxRates.length) {
            throw new IllegalArgumentException("Tax brackets and rates arrays must be the same length");
        }
        
        this.personalAllowance = personalAllowance;
        this.taxBrackets = taxBrackets;
        this.taxRates = taxRates;
    }
    
    /**
     * Calculate tax based on progressive tax brackets
     * @param netIncome The net income after deductions
     * @return The calculated tax amount
     */
    @Override
    public double calculateTax(double netIncome) {
        // Apply personal allowance
        double taxableIncome = Math.max(0, netIncome - personalAllowance);
        
        // No tax if income is below taxable threshold
        if (taxableIncome <= 0) {
            return 0;
        }
        
        double tax = 0;
        double remainingIncome = taxableIncome;
        double lowerLimit = 0;
        
        // Calculate tax for each bracket
        for (int i = 0; i < taxBrackets.length; i++) {
            double bracketSize = taxBrackets[i] - lowerLimit;
            double incomeInBracket = Math.min(remainingIncome, bracketSize);
            
            if (incomeInBracket <= 0) {
                break;
            }
            
            tax += incomeInBracket * taxRates[i];
            remainingIncome -= incomeInBracket;
            lowerLimit = taxBrackets[i];
            
            if (remainingIncome <= 0) {
                break;
            }
        }
        
        return tax;
    }
    
    /**
     * Get the personal allowance amount
     * @return The personal allowance
     */
    public double getPersonalAllowance() {
        return personalAllowance;
    }
    
    /**
     * Set the personal allowance amount
     * @param personalAllowance The new personal allowance
     */
    public void setPersonalAllowance(double personalAllowance) {
        this.personalAllowance = personalAllowance;
    }
}
