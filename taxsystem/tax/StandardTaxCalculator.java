package taxsystem.tax;

/**
 * StandardTaxCalculator implements the TaxCalculator interface
 * This demonstrates polymorphism (requirement 2.6)
 */
public class StandardTaxCalculator implements TaxCalculator {
    
    @Override
    public double calculateTax(double netIncome) {
        // Progressive tax calculation based on Thai tax brackets
        double tax = 0;
        
        if (netIncome <= 150000) {
            tax = 0;
        } else if (netIncome <= 300000) {
            tax = (netIncome - 150000) * 0.05;
        } else if (netIncome <= 500000) {
            tax = (300000 - 150000) * 0.05 + (netIncome - 300000) * 0.10;
        } else if (netIncome <= 750000) {
            tax = (300000 - 150000) * 0.05 + (500000 - 300000) * 0.10 + (netIncome - 500000) * 0.15;
        } else if (netIncome <= 1000000) {
            tax = (300000 - 150000) * 0.05 + (500000 - 300000) * 0.10 + (750000 - 500000) * 0.15 + (netIncome - 750000) * 0.20;
        } else if (netIncome <= 2000000) {
            tax = (300000 - 150000) * 0.05 + (500000 - 300000) * 0.10 + (750000 - 500000) * 0.15 + (1000000 - 750000) * 0.20 + (netIncome - 1000000) * 0.25;
        } else {
            tax = (300000 - 150000) * 0.05 + (500000 - 300000) * 0.10 + (750000 - 500000) * 0.15 + (1000000 - 750000) * 0.20 + (2000000 - 1000000) * 0.25 + (netIncome - 2000000) * 0.30;
        }
        
        return tax;
    }
    
    @Override
    public String getCalculatorName() {
        return "Standard Tax Calculator";
    }
}
