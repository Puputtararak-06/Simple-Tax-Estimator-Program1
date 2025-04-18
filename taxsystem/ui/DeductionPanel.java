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
 * DeductionPanel represents the second page of the tax calculation wizard
 */
public class DeductionPanel extends JPanel {
    private NewTaxSystem taxSystem;
    private int userId;
    private Consumer<Integer> onNextPage;
    
    // Form fields
    private JRadioButton singleRadioButton;
    private JRadioButton marriedRadioButton;
    private JCheckBox spouseNoIncomeCheckBox;
    private JTextField childrenCountField;
    
    /**
     * Constructor
     * @param taxSystem The tax system
     * @param userId The user ID
     * @param onNextPage Callback for when the next page should be shown
     */
    public DeductionPanel(NewTaxSystem taxSystem, int userId, Consumer<Integer> onNextPage) {
        this.taxSystem = taxSystem;
        this.userId = userId;
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
        progressPanel.add(new JLabel("Step 2 of 3: Deduction Information"));
        headerPanel.add(progressPanel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Enter Your Deduction Information"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Marital status
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel statusLabel = new JLabel("Marital Status:");
        formPanel.add(statusLabel, gbc);
        
        // Radio buttons for marital status
        ButtonGroup statusGroup = new ButtonGroup();
        singleRadioButton = new JRadioButton("Single");
        marriedRadioButton = new JRadioButton("Married");
        
        statusGroup.add(singleRadioButton);
        statusGroup.add(marriedRadioButton);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(singleRadioButton, gbc);
        
        gbc.gridx = 1;
        formPanel.add(marriedRadioButton, gbc);
        
        // Spouse income checkbox (only visible when married)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        spouseNoIncomeCheckBox = new JCheckBox("Spouse has no income (30,000 THB deduction)");
        spouseNoIncomeCheckBox.setEnabled(false);
        formPanel.add(spouseNoIncomeCheckBox, gbc);
        
        // Children count
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Number of Children:"), gbc);
        
        gbc.gridx = 1;
        childrenCountField = new JTextField("0", 5);
        formPanel.add(childrenCountField, gbc);
        
        // Information label
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("<html>Personal allowance: 60,000 THB<br>Child allowance: 30,000 THB per child</html>");
        formPanel.add(infoLabel, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Go back to previous page
                // This would require restructuring the application to support going back
                JOptionPane.showMessageDialog(DeductionPanel.this, 
                    "Back functionality not implemented in this demo", 
                    "Information", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        buttonPanel.add(backButton);
        
        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processForm();
            }
        });
        buttonPanel.add(nextButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set up event handlers
        marriedRadioButton.addActionListener(e -> spouseNoIncomeCheckBox.setEnabled(true));
        singleRadioButton.addActionListener(e -> {
            spouseNoIncomeCheckBox.setEnabled(false);
            spouseNoIncomeCheckBox.setSelected(false);
        });
        
        // Set default selection
        singleRadioButton.setSelected(true);
    }
    
    /**
     * Process the form data and move to the next page
     */
    private void processForm() {
        try {
            // Validate form
            if (!singleRadioButton.isSelected() && !marriedRadioButton.isSelected()) {
                JOptionPane.showMessageDialog(this, 
                    "Please select your marital status", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int childrenCount = 0;
            try {
                childrenCount = Integer.parseInt(childrenCountField.getText().trim());
                if (childrenCount < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Number of children must be a non-negative integer", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                childrenCountField.requestFocus();
                return;
            }
            
            // Update user status
            User user = taxSystem.getAllUsers().stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User not found"));
            
            String status = singleRadioButton.isSelected() ? "Single" : "Married";
            user.setStatus(status);
            
            // Add personal allowance deduction
            taxSystem.addDeduction(userId, "Personal Allowance", 60000);
            
            // Add spouse deduction if applicable
            if (marriedRadioButton.isSelected() && spouseNoIncomeCheckBox.isSelected()) {
                taxSystem.addDeduction(userId, "Spouse Allowance", 30000);
            }
            
            // Add children deductions
            if (childrenCount > 0) {
                taxSystem.addDeduction(userId, "Children Allowance", childrenCount * 30000);
            }
            
            // Move to next page
            onNextPage.accept(userId);
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving data: " + e.getMessage(), 
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
