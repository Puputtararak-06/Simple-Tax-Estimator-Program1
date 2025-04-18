package taxsystem.tax;

/**
 * TaxCalculator interface defines methods for tax calculation
 * This demonstrates the use of interfaces (requirement 2.2)
 */
public interface TaxCalculator {
    /**
     * Calculate tax based on net income
     * @param netIncome The net income after deductions
     * @return The calculated tax amount
     */
    double calculateTax(double netIncome);
    
    /**
     * Get the name of the calculator
     * @return The calculator name
     */
    String getCalculatorName();
}
