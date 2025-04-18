package taxsystem.model;

/**
 * User class represents a user in the tax system
 * This demonstrates the use of object-oriented programming
 */
public class User {
    private int id;
    private String name;
    private String status; // "Single" or "Married"
    private double monthlyIncome;
    private double bonus;
    private double otherIncome;
    private double annualIncome;
    
    // Constructor
    public User(int id, String name, String status, double monthlyIncome, double bonus, double otherIncome) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.monthlyIncome = monthlyIncome;
        this.bonus = bonus;
        this.otherIncome = otherIncome;
        calculateAnnualIncome();
    }
    
    // Calculate annual income based on monthly income, bonus, and other income
    private void calculateAnnualIncome() {
        this.annualIncome = (monthlyIncome * 12) + bonus + otherIncome;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public double getMonthlyIncome() {
        return monthlyIncome;
    }
    
    public void setMonthlyIncome(double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
        calculateAnnualIncome();
    }
    
    public double getBonus() {
        return bonus;
    }
    
    public void setBonus(double bonus) {
        this.bonus = bonus;
        calculateAnnualIncome();
    }
    
    public double getOtherIncome() {
        return otherIncome;
    }
    
    public void setOtherIncome(double otherIncome) {
        this.otherIncome = otherIncome;
        calculateAnnualIncome();
    }
    
    public double getAnnualIncome() {
        return annualIncome;
    }
    
    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", status=" + status + 
               ", monthlyIncome=" + monthlyIncome + ", bonus=" + bonus + 
               ", otherIncome=" + otherIncome + ", annualIncome=" + annualIncome + "]";
    }
}
