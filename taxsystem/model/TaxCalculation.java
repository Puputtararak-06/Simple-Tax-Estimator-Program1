package taxsystem.model;

import java.time.LocalDateTime;

/**
 * TaxCalculation class represents the result of a tax calculation
 */
public class TaxCalculation {
    private int id;
    private int userId;
    private double totalIncome;
    private double totalDeductions;
    private double netIncome;
    private double taxAmount;
    private LocalDateTime calculationDate;

    // Constructor
    public TaxCalculation(int id, int userId, double totalIncome, double totalDeductions, double netIncome, double taxAmount) {
        this.id = id;
        this.userId = userId;
        this.totalIncome = totalIncome;
        this.totalDeductions = totalDeductions;
        this.netIncome = netIncome;
        this.taxAmount = taxAmount;
        this.calculationDate = LocalDateTime.now();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalDeductions() {
        return totalDeductions;
    }

    public double getNetIncome() {
        return netIncome;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public LocalDateTime getCalculationDate() {
        return calculationDate;
    }

    @Override
    public String toString() {
        return "TaxCalculation [id=" + id + ", userId=" + userId +
               ", totalIncome=" + totalIncome + ", totalDeductions=" + totalDeductions +
               ", netIncome=" + netIncome + ", taxAmount=" + taxAmount +
               ", calculationDate=" + calculationDate + "]";
    }

    /**
     * Get a summary of the tax calculation in a user-friendly format
     * @return A formatted summary string
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Net Income: ").append(String.format("%.2f", netIncome)).append(" THB\n");

        // Add tax calculation details with brackets
        summary.append("\nTax Calculation (Progressive Brackets):\n");

        // First bracket: 0-150,000 = 0%
        double remainingIncome = netIncome;
        double taxBracket1 = 0; // 0-150,000 = 0%
        summary.append("0 - 150,000 THB (0%): 0.00 THB\n");

        // Second bracket: 150,001-300,000 = 5%
        remainingIncome = Math.max(0, remainingIncome - 150000);
        double taxBracket2 = Math.min(remainingIncome, 150000) * 0.05;
        summary.append("150,001 - 300,000 THB (5%): ").append(String.format("%.2f", taxBracket2)).append(" THB\n");

        // Third bracket: 300,001-500,000 = 10%
        remainingIncome = Math.max(0, remainingIncome - 150000);
        double taxBracket3 = Math.min(remainingIncome, 200000) * 0.10;
        summary.append("300,001 - 500,000 THB (10%): ").append(String.format("%.2f", taxBracket3)).append(" THB\n");

        // Fourth bracket: 500,001-750,000 = 15%
        remainingIncome = Math.max(0, remainingIncome - 200000);
        double taxBracket4 = Math.min(remainingIncome, 250000) * 0.15;
        summary.append("500,001 - 750,000 THB (15%): ").append(String.format("%.2f", taxBracket4)).append(" THB\n");

        // Fifth bracket: 750,001-1,000,000 = 20%
        remainingIncome = Math.max(0, remainingIncome - 250000);
        double taxBracket5 = Math.min(remainingIncome, 250000) * 0.20;
        summary.append("750,001 - 1,000,000 THB (20%): ").append(String.format("%.2f", taxBracket5)).append(" THB\n");

        // Sixth bracket: > 1,000,000 = 25%
        remainingIncome = Math.max(0, remainingIncome - 250000);
        double taxBracket6 = remainingIncome * 0.25;
        summary.append("Over 1,000,000 THB (25%): ").append(String.format("%.2f", taxBracket6)).append(" THB\n");

        // Total tax
        summary.append("\nTotal Tax: ").append(String.format("%.2f", taxAmount)).append(" THB");

        // Add note if tax is zero
        if (taxAmount <= 0) {
            summary.append(" (No tax payable)");
        }

        return summary.toString();
    }
}
