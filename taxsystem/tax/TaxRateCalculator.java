package taxsystem.tax;

/**
 * TaxRateCalculator handles the calculation of tax rates based on income levels
 */
public class TaxRateCalculator {
    
    /**
     * Calculate tax amount based on net income
     * @param netIncome Net income after deductions
     * @return Tax amount to be paid
     */
    public static double calculateTaxAmount(double netIncome) {
        // Progressive tax rate calculation
        if (netIncome <= 150000) {
            return 0; // Tax exemption for first 150,000 THB
        }
        
        double taxAmount = 0;
        
        // Calculate tax for income between 150,001 - 500,000 (5%)
        if (netIncome > 150000) {
            double taxableAmount = Math.min(netIncome, 500000) - 150000;
            taxAmount += taxableAmount * 0.05;
        }
        
        // Calculate tax for income between 500,001 - 1,000,000 (10%)
        if (netIncome > 500000) {
            double taxableAmount = Math.min(netIncome, 1000000) - 500000;
            taxAmount += taxableAmount * 0.10;
        }
        
        // Calculate tax for income between 1,000,001 - 2,000,000 (15%)
        if (netIncome > 1000000) {
            double taxableAmount = Math.min(netIncome, 2000000) - 1000000;
            taxAmount += taxableAmount * 0.15;
        }
        
        // Calculate tax for income above 2,000,000 (20%)
        if (netIncome > 2000000) {
            double taxableAmount = netIncome - 2000000;
            taxAmount += taxableAmount * 0.20;
        }
        
        return taxAmount;
    }
    
    /**
     * Get the tax rate for a specific income level
     * @param income Income level
     * @return Tax rate as a percentage (0-20)
     */
    public static int getTaxRate(double income) {
        if (income <= 150000) {
            return 0;
        } else if (income <= 500000) {
            return 5;
        } else if (income <= 1000000) {
            return 10;
        } else if (income <= 2000000) {
            return 15;
        } else {
            return 20;
        }
    }
}
