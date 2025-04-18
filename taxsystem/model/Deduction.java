package taxsystem.model;

/**
 * Deduction class represents a tax deduction for a user
 */
public class Deduction {
    private int id;
    private int userId;
    private String type;
    private double amount;
    
    // Constructor
    public Deduction(int id, int userId, String type, double amount) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    @Override
    public String toString() {
        return "Deduction [id=" + id + ", userId=" + userId + ", type=" + type + ", amount=" + amount + "]";
    }
}
