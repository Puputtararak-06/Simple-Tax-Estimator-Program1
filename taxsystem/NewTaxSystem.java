package taxsystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import taxsystem.model.User;
import taxsystem.model.Deduction;
import taxsystem.model.TaxCalculation;
import taxsystem.tax.TaxCalculator;
import taxsystem.tax.StandardTaxCalculator;
import taxsystem.tax.TaxRateCalculator;
import taxsystem.util.FileManager;

/**
 * NewTaxSystem is the main class that manages the tax calculation system.
 * This class handles all tax-related operations including user management,
 * tax calculations, and data persistence.
 *
 * This demonstrates the use of collections (requirement 2.5).
 */
public class NewTaxSystem {
    private List<User> users;
    private Map<Integer, List<Deduction>> userDeductions;
    private List<TaxCalculation> calculations;
    private TaxCalculator calculator;

    private int nextUserId = 1;
    private int nextDeductionId = 1;
    private int nextCalculationId = 1;

    /**
     * Constructor initializes the system and loads data from files
     */
    public NewTaxSystem() {
        users = new ArrayList<>();
        userDeductions = new HashMap<>();
        calculations = new ArrayList<>();
        calculator = new StandardTaxCalculator();

        try {
            // Initialize file system
            FileManager.initialize();

            // Load existing data
            loadData();
        } catch (IOException e) {
            System.err.println("Error initializing file system: " + e.getMessage());
        }
    }

    /**
     * Load data from files
     * @throws IOException if there's an error reading from files
     */
    private void loadData() throws IOException {
        // Load users
        users = FileManager.loadUsers();
        if (!users.isEmpty()) {
            nextUserId = users.stream().mapToInt(User::getId).max().getAsInt() + 1;
        }

        // Load deductions
        List<Deduction> deductions = FileManager.loadDeductions();
        if (!deductions.isEmpty()) {
            nextDeductionId = deductions.stream().mapToInt(Deduction::getId).max().getAsInt() + 1;

            // Group deductions by user ID
            for (Deduction deduction : deductions) {
                int userId = deduction.getUserId();
                userDeductions.computeIfAbsent(userId, k -> new ArrayList<>()).add(deduction);
            }
        }

        // Load calculations
        calculations = FileManager.loadCalculations();
        if (!calculations.isEmpty()) {
            nextCalculationId = calculations.stream().mapToInt(TaxCalculation::getId).max().getAsInt() + 1;
        }
    }

    /**
     * Create a new user
     * @param name User's name
     * @param status User's marital status
     * @param monthlyIncome User's monthly income
     * @param bonus User's annual bonus
     * @param otherIncome User's other income
     * @return The created user
     * @throws IOException if there's an error saving to file
     */
    public User createUser(String name, String status, double monthlyIncome, double bonus, double otherIncome) throws IOException {
        User user = new User(nextUserId++, name, status, monthlyIncome, bonus, otherIncome);
        users.add(user);
        userDeductions.put(user.getId(), new ArrayList<>());

        // Save to file
        FileManager.saveUser(user);

        return user;
    }

    /**
     * Add a deduction for a user
     * @param userId The user ID
     * @param type The type of deduction
     * @param amount The deduction amount
     * @return The created deduction
     * @throws IOException if there's an error saving to file
     */
    public Deduction addDeduction(int userId, String type, double amount) throws IOException {
        // Check if user exists
        if (users.stream().noneMatch(u -> u.getId() == userId)) {
            throw new IllegalArgumentException("User with ID " + userId + " does not exist");
        }

        Deduction deduction = new Deduction(nextDeductionId++, userId, type, amount);
        userDeductions.computeIfAbsent(userId, k -> new ArrayList<>()).add(deduction);

        // Save to file
        FileManager.saveDeduction(deduction);

        return deduction;
    }

    /**
     * Calculate tax for a user
     * @param userId The user ID
     * @return The tax calculation result
     * @throws IOException if there's an error saving to file
     */
    public TaxCalculation calculateTax(int userId) throws IOException {
        // Find user
        User user = users.stream()
                        .filter(u -> u.getId() == userId)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " does not exist"));

        // Get user's deductions
        List<Deduction> deductions = userDeductions.getOrDefault(userId, new ArrayList<>());

        // Calculate total deductions
        double totalDeductions = deductions.stream().mapToDouble(Deduction::getAmount).sum();

        // Calculate net income (annual income - deductions)
        double netIncome = Math.max(0, user.getAnnualIncome() - totalDeductions);

        // Calculate tax using the TaxRateCalculator
        double taxAmount = TaxRateCalculator.calculateTaxAmount(netIncome);

        // Create and save calculation
        TaxCalculation calculation = new TaxCalculation(
            nextCalculationId++, userId, user.getAnnualIncome(), totalDeductions, netIncome, taxAmount);
        calculations.add(calculation);

        // Save to file
        FileManager.saveCalculation(calculation);

        return calculation;
    }

    /**
     * Get all users
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Get all deductions for a user
     * @param userId The user ID
     * @return List of user's deductions
     */
    public List<Deduction> getUserDeductions(int userId) {
        return new ArrayList<>(userDeductions.getOrDefault(userId, new ArrayList<>()));
    }

    /**
     * Get all calculations
     * @return List of all calculations
     */
    public List<TaxCalculation> getAllCalculations() {
        return new ArrayList<>(calculations);
    }

    /**
     * Clear all data
     * @throws IOException if there's an error clearing data
     */
    public void clearAllData() throws IOException {
        users.clear();
        userDeductions.clear();
        calculations.clear();
        nextUserId = 1;
        nextDeductionId = 1;
        nextCalculationId = 1;

        // Clear files
        FileManager.clearAllData();
    }

    /**
     * Set the tax calculator to use
     * @param calculator The calculator to use
     */
    public void setCalculator(TaxCalculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Get the current tax calculator
     * @return The current calculator
     */
    public TaxCalculator getCalculator() {
        return calculator;
    }
}
