package taxsystem.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import taxsystem.NewTaxSystem;
import taxsystem.model.Deduction;
import taxsystem.model.TaxCalculation;
import taxsystem.model.User;

/**
 * ResultPanel represents the final page of the tax calculation wizard
 */
public class ResultPanel extends JPanel {
    private NewTaxSystem taxSystem;
    private int userId;
    private Runnable onNewCalculation;
    private Runnable onViewAllResults;
    
    private JTextArea resultTextArea;
    
    /**
     * Constructor
     * @param taxSystem The tax system
     * @param userId The user ID
     * @param onNewCalculation Callback for when a new calculation should be started
     * @param onViewAllResults Callback for when all results should be viewed
     */
    public ResultPanel(NewTaxSystem taxSystem, int userId, Runnable onNewCalculation, Runnable onViewAllResults) {
        this.taxSystem = taxSystem;
        this.userId = userId;
        this.onNewCalculation = onNewCalculation;
        this.onViewAllResults = onViewAllResults;
        
        initializeUI();
        calculateAndDisplayResults();
    }
    
    /**
     * Initialize the UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Tax Calculation System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Progress indicator
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        progressPanel.add(new JLabel("Step 3 of 3: Results"));
        headerPanel.add(progressPanel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Results panel
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Tax Calculation Results"));
        
        resultTextArea = new JTextArea(20, 40);
        resultTextArea.setEditable(false);
        resultTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(resultsPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton newCalculationButton = new JButton("New Calculation");
        newCalculationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNewCalculation.run();
            }
        });
        buttonPanel.add(newCalculationButton);
        
        JButton viewAllResultsButton = new JButton("View All Results");
        viewAllResultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onViewAllResults.run();
            }
        });
        buttonPanel.add(viewAllResultsButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Calculate and display the tax results
     */
    private void calculateAndDisplayResults() {
        try {
            // Get user
            User user = taxSystem.getAllUsers().stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User not found"));
            
            // Get deductions
            List<Deduction> deductions = taxSystem.getUserDeductions(userId);
            
            // Calculate tax
            TaxCalculation calculation = taxSystem.calculateTax(userId);
            
            // Format numbers
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "TH"));
            
            // Build result text
            StringBuilder sb = new StringBuilder();
            sb.append("TAX CALCULATION SUMMARY\n");
            sb.append("=======================\n\n");
            
            sb.append("User Information:\n");
            sb.append("-----------------\n");
            sb.append("Name: ").append(user.getName()).append("\n");
            sb.append("Status: ").append(user.getStatus()).append("\n\n");
            
            sb.append("Income Details:\n");
            sb.append("--------------\n");
            sb.append("Monthly Income: ").append(currencyFormat.format(user.getMonthlyIncome())).append("\n");
            sb.append("Annual Income (12 months): ").append(currencyFormat.format(user.getMonthlyIncome() * 12)).append("\n");
            sb.append("Annual Bonus: ").append(currencyFormat.format(user.getBonus())).append("\n");
            sb.append("Other Income: ").append(currencyFormat.format(user.getOtherIncome())).append("\n");
            sb.append("Total Annual Income: ").append(currencyFormat.format(user.getAnnualIncome())).append("\n\n");
            
            sb.append("Deductions:\n");
            sb.append("-----------\n");
            for (Deduction deduction : deductions) {
                sb.append(deduction.getType()).append(": ").append(currencyFormat.format(deduction.getAmount())).append("\n");
            }
            sb.append("Total Deductions: ").append(currencyFormat.format(calculation.getTotalDeductions())).append("\n\n");
            
            sb.append("Tax Calculation:\n");
            sb.append("---------------\n");
            sb.append("Net Taxable Income: ").append(currencyFormat.format(calculation.getNetIncome())).append("\n");
            sb.append("Tax Amount: ").append(currencyFormat.format(calculation.getTaxAmount())).append("\n\n");
            
            sb.append("Tax Calculator: ").append(taxSystem.getCalculator().getCalculatorName()).append("\n");
            
            resultTextArea.setText(sb.toString());
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error calculating tax: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "An error occurred: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
