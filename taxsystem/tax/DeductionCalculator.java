package taxsystem.tax;

/**
 * DeductionCalculator handles the calculation of various tax deductions
 * based on user's status and other factors.
 */
public class DeductionCalculator {
    
    /**
     * Calculate personal allowance deduction
     * @return Personal allowance deduction amount (60,000 THB)
     */
    public static int calculatePersonalAllowance() {
        return 60000; // Fixed personal allowance of 60,000 THB
    }
    
    /**
     * Calculate spouse allowance deduction based on spouse's income status
     * @param isMarried Whether the user is married
     * @param spouseHasNoIncome Whether the spouse has no income
     * @return Spouse allowance deduction amount (0, 30,000 or 60,000 THB)
     */
    public static int calculateSpouseAllowance(boolean isMarried, boolean spouseHasNoIncome) {
        if (!isMarried) {
            return 0; // No spouse allowance for single people
        }
        
        // If spouse has no income, deduction is 60,000 THB, otherwise 30,000 THB
        return spouseHasNoIncome ? 60000 : 30000;
    }
    
    /**
     * Calculate children allowance deduction based on number of children
     * @param childrenCount Number of children eligible for deduction
     * @return Children allowance deduction amount (30,000 THB per child)
     */
    public static int calculateChildrenAllowance(int childrenCount) {
        // 30,000 THB per child
        return childrenCount * 30000;
    }
    
    /**
     * Calculate total deductions
     * @param isMarried Whether the user is married
     * @param spouseHasNoIncome Whether the spouse has no income
     * @param childrenCount Number of children eligible for deduction
     * @return Total deduction amount
     */
    public static int calculateTotalDeductions(boolean isMarried, boolean spouseHasNoIncome, int childrenCount) {
        int personalAllowance = calculatePersonalAllowance();
        int spouseAllowance = calculateSpouseAllowance(isMarried, spouseHasNoIncome);
        int childrenAllowance = calculateChildrenAllowance(childrenCount);
        
        return personalAllowance + spouseAllowance + childrenAllowance;
    }
}
