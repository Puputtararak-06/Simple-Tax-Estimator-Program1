

import java.util.*;
import java.util.List;
import java.io.IOException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TaxSystem {
    private List<User> users = new ArrayList<>();
    private List<Deduction> deductions = new ArrayList<>();
    private int userIdSeq = 1;
    private int deductionIdSeq = 1;
    private int calculationIdSeq = 1;
    private TaxCalculator calculator;

    public TaxSystem() {
        // Default to standard calculator
        this.calculator = new StandardTaxCalculator();

        // Try to load existing users
        try {
            List<User> loadedUsers = FileManager.loadUsers();
            if (!loadedUsers.isEmpty()) {
                this.users.addAll(loadedUsers);
                // Update the sequence counter
                this.userIdSeq = loadedUsers.stream()
                    .mapToInt(User::getId)
                    .max()
                    .orElse(0) + 1;
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    public User createUser(String name, String status, String occupation, double income) {
        User user = new User(userIdSeq++, name, status, occupation, income);
        users.add(user);

        // Save user to file
        try {
            FileManager.saveUser(user);
        } catch (IOException e) {
            System.err.println("Error saving user: " + e.getMessage());
        }

        return user;
    }

    public void addDeduction(int userId, String type, double amount) {
        deductions.add(new Deduction(deductionIdSeq++, userId, type, amount));
    }

    public double getTotalDeduction(int userId) {
        return deductions.stream()
                .filter(d -> d.getUserId() == userId)
                .mapToDouble(Deduction::getAmount)
                .sum();
    }

    public TaxCalculation calculateTax(User user, double taxPaid) {
        double expense = Math.min(user.getIncome() * 0.5, 100000);
        double totalDeduction = getTotalDeduction(user.getId());
        double netIncome = user.getIncome() - expense - totalDeduction;

        // Use the current calculator (demonstrates polymorphism)
        double taxDue = calculator.calculateTax(netIncome);

        TaxCalculation result = new TaxCalculation(calculationIdSeq++, user.getId(), netIncome, taxDue, taxPaid);

        // Save calculation to file
        try {
            FileManager.saveCalculation(result, user);
        } catch (IOException e) {
            System.err.println("Error saving calculation: " + e.getMessage());
        }

        return result;
    }

    /**
     * Set the tax calculator to use (demonstrates polymorphism)
     * @param calculator The calculator implementation to use
     */
    public void setCalculator(TaxCalculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Get the current tax calculator
     * @return The current calculator implementation
     */
    public TaxCalculator getCalculator() {
        return this.calculator;
    }

    public static void main(String[] args) {
        // Initialize file system
        try {
            FileManager.initialize();
        } catch (Exception e) {
            System.err.println("Error initializing file system: " + e.getMessage());
        }

        TaxSystem system = new TaxSystem();

        JFrame frame = new JFrame("Tax Calculator");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(0, 1));

        JTextField nameField = new JTextField();
        JTextField incomeField = new JTextField();
        JTextField taxPaidField = new JTextField();
        JTextField deductionField = new JTextField();

        JButton calculateBtn = new JButton("Calculate Tax");
        JButton detailedFormBtn = new JButton("Detailed Form");
        JTextArea resultArea = new JTextArea();
        resultArea.setSize(600, 400);
        //resultArea.setEditable(false);
        //frame.setSize(500, 500);

        frame.add(new JLabel("Name:"));
        frame.add(nameField);
        frame.add(new JLabel("Annual Income (THB):"));
        frame.add(incomeField);
        frame.add(new JLabel("Tax Already Paid (THB):"));
        frame.add(taxPaidField);
        frame.add(new JLabel("Total Deductions (THB):"));
        frame.add(deductionField);
        frame.add(calculateBtn);
        frame.add(detailedFormBtn);
        frame.add(new JScrollPane(resultArea));

        calculateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText();
                    double income = Double.parseDouble(incomeField.getText());
                    double taxPaid = Double.parseDouble(taxPaidField.getText());
                    double deduction = Double.parseDouble(deductionField.getText());

                    User user = system.createUser(name, "Single", "Employee", income);
                    system.addDeduction(user.getId(), "Total Deduction", deduction);

                    TaxCalculation result = system.calculateTax(user, taxPaid);
                    resultArea.setText(result.getSummary());

                } catch (Exception ex) {
                    resultArea.setText("Please enter valid information");
                }
            }
        });

        detailedFormBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Open the detailed form
                new DetailedTaxForm(system);
            }
        });

        frame.setVisible(true);
    }
    // This method has compilation errors and is not used, so it's been removed
    // If you need to create a frame in another method, please implement it properly
}

// === ส่วน class อื่น ๆ (non-public) ===

class User {
    private int id;
    private String name;
    private String status;
    private String occupation;
    private double income;

    public User(int id, String name, String status, String occupation, double income) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.occupation = occupation;
        this.income = income;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public String getOccupation() { return occupation; }
    public double getIncome() { return income; }
}

class Deduction {
    // ID field removed as it was only used in constructor but not accessed elsewhere
    private int userId;
    // Type field removed as it was only used in constructor but not accessed elsewhere
    private double amount;

    public Deduction(int id, int userId, String type, double amount) {
        // id and type parameters kept for backward compatibility
        this.userId = userId;
        this.amount = amount;
    }

    public int getUserId() { return userId; }
    public double getAmount() { return amount; }
}

interface TaxCalculator {
    double calculateTax(double netIncome);
}

class StandardTaxCalculator implements TaxCalculator {
    public double calculateTax(double netIncome) {
        if (netIncome <= 150000) return 0;
        else if (netIncome <= 300000) return (netIncome - 150000) * 0.05;
        else if (netIncome <= 500000) return (150000 * 0.05) + (netIncome - 300000) * 0.1;
        else return (150000 * 0.05) + (200000 * 0.1) + (netIncome - 500000) * 0.15;
    }
}

class TaxCalculation {
    // ID field removed as it was only used in constructor but not accessed elsewhere
    // UserID field removed as it was only used in constructor but not accessed elsewhere
    private double netIncome;
    private double taxDue;
    private double taxPaid;
    private double taxBalance;
    private String status;
    // CreatedAt field removed as it was not used

    // Getters added for file operations
    public double getNetIncome() { return netIncome; }
    public double getTaxDue() { return taxDue; }
    public double getTaxPaid() { return taxPaid; }
    public double getTaxBalance() { return taxBalance; }
    public String getStatus() { return status; }

    public TaxCalculation(int id, int userId, double netIncome, double taxDue, double taxPaid) {
        // id and userId parameters kept for backward compatibility
        this.netIncome = netIncome;
        this.taxDue = taxDue;
        this.taxPaid = taxPaid;
        this.taxBalance = taxDue - taxPaid;
        this.status = (taxBalance > 0) ? "Pay more" : "Tax refund";
        // createdAt initialization removed
    }

    public String getSummary() {
        return "Net Income: " + netIncome +
               "\nTax Due: " + taxDue +
               "\nTax Already Paid: " + taxPaid +
               "\nDifference: " + taxBalance + " (" + status + ")\n";
    }
}
