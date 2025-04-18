package taxsystem.tax;

/**
 * SimplifiedTaxCalculator implements the TaxCalculator interface with a simpler calculation
 * This demonstrates polymorphism (requirement 2.6)
 */
public class SimplifiedTaxCalculator implements TaxCalculator {
    
    @Override
    public double calculateTax(double netIncome) {
        // Simplified tax calculation (flat rate with exemption)
        double taxableIncome = Math.max(0, netIncome - 150000);
        return taxableIncome * 0.10; // Flat 10% tax rate
    }
    
    @Override
    public String getCalculatorName() {
        return "Simplified Tax Calculator";
    }
}
