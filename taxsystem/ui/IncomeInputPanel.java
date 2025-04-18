package taxsystem.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.function.Consumer;

import taxsystem.NewTaxSystem;
import taxsystem.model.User;

/**
 * IncomeInputPanel represents the first page of the tax calculation wizard
 * This demonstrates input/output from keyboard (requirement 2.4)
 */
public class IncomeInputPanel extends JPanel {
    private NewTaxSystem taxSystem;
    private Consumer<Integer> onNextPage;
    
    // Form fields
    private JTextField nameField;
    private JTextField monthlyIncomeField;
    private JTextField bonusField;
    private JTextField otherIncomeField;
    
    /**
     * Constructor
     * @param taxSystem The tax system
     * @param onNextPage Callback for when the next page should be shown
     */
    public IncomeInputPanel(NewTaxSystem taxSystem, Consumer<Integer> onNextPage) {
        this.taxSystem = taxSystem;
        this.onNextPage = onNextPage;
        
        initializeUI();
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
        progressPanel.add(new JLabel("Step 1 of 3: Income Information"));
        headerPanel.add(progressPanel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Enter Your Income Information"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        // Monthly income field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Monthly Income (THB):"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        monthlyIncomeField = new JTextField(20);
        formPanel.add(monthlyIncomeField, gbc);
        
        // Bonus field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Annual Bonus (THB):"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        bonusField = new JTextField(20);
        formPanel.add(bonusField, gbc);
        
        // Other income field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Other Income (THB):"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        otherIncomeField = new JTextField(20);
        formPanel.add(otherIncomeField, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processForm();
            }
        });
        buttonPanel.add(nextButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Process the form data and move to the next page
     * This demonstrates exception handling (requirement 2.3)
     */
    private void processForm() {
        // Validate form
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter your name", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return;
        }
        
        try {
            // Parse numeric fields
            double monthlyIncome = parseDoubleField(monthlyIncomeField, "Monthly Income");
            double bonus = parseDoubleField(bonusField, "Annual Bonus");
            double otherIncome = parseDoubleField(otherIncomeField, "Other Income");
            
            // Create user
            User user = taxSystem.createUser(
                nameField.getText().trim(),
                "Single", // Default status, will be updated in the next page
                monthlyIncome,
                bonus,
                otherIncome
            );
            
            // Move to next page
            onNextPage.accept(user.getId());
            
        } catch (NumberFormatException e) {
            // Already handled in parseDoubleField
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving data: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Parse a double value from a text field
     * @param field The text field to parse
     * @param fieldName The name of the field for error messages
     * @return The parsed double value
     * @throws NumberFormatException if the field does not contain a valid number
     */
    private double parseDoubleField(JTextField field, String fieldName) throws NumberFormatException {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            return 0.0;
        }
        
        try {
            double value = Double.parseDouble(text);
            if (value < 0) {
                throw new NumberFormatException("Negative value");
            }
            return value;
        } catch (NumberFormatException e) {
            field.requestFocus();
            throw new NumberFormatException(fieldName + " must be a positive number");
        }
    }
}
