package taxsystem.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;

import taxsystem.model.User;

/**
 * IncomeStepPanel represents the first step of the tax calculation wizard
 */
public class IncomeStepPanel extends StepPanel {
    // Form fields
    private JTextField nameField;
    private JTextField monthlyIncomeField;
    private JTextField bonusField;
    private JTextField otherIncomeField;

    /**
     * Constructor
     * @param app The parent application
     */
    public IncomeStepPanel(StepBasedTaxApp app) {
        super(app);
    }

    /**
     * Initialize the UI components
     */
    @Override
    protected void initializeUI() {
        // Set up header
        JLabel titleLabel = createStyledTitle("Personal Income Tax Calculator");
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        // Add progress indicator
        JPanel progressPanel = createProgressIndicator(1);
        headerPanel.add(progressPanel, BorderLayout.CENTER);

        // Set up content
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 100, 20, 100));

        // Title
        JLabel formTitleLabel = createStyledTitle("Income Information");
        formTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formTitleLabel.setForeground(new Color(44, 102, 154));
        formPanel.add(formTitleLabel);
        formPanel.add(Box.createVerticalStrut(30));

        // Name field
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setBackground(Color.WHITE);
        namePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        namePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel nameLabel = createStyledLabel("Full Name");
        nameField = createStyledTextField("Enter your full name");
        setupPlaceholder(nameField, "Enter your full name");

        namePanel.add(nameLabel, BorderLayout.NORTH);
        namePanel.add(nameField, BorderLayout.CENTER);
        formPanel.add(namePanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Monthly income field
        JPanel monthlyIncomePanel = new JPanel(new BorderLayout());
        monthlyIncomePanel.setBackground(Color.WHITE);
        monthlyIncomePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        monthlyIncomePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel monthlyIncomeLabel = createStyledLabel("Monthly Income (THB)");
        monthlyIncomeField = createStyledTextField("Enter your monthly income");
        setupPlaceholder(monthlyIncomeField, "Enter your monthly income");

        monthlyIncomePanel.add(monthlyIncomeLabel, BorderLayout.NORTH);
        monthlyIncomePanel.add(monthlyIncomeField, BorderLayout.CENTER);
        formPanel.add(monthlyIncomePanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Bonus field
        JPanel bonusPanel = new JPanel(new BorderLayout());
        bonusPanel.setBackground(Color.WHITE);
        bonusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bonusPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel bonusLabel = createStyledLabel("Annual Bonus (THB)");
        bonusField = createStyledTextField("Enter your annual bonus");
        setupPlaceholder(bonusField, "Enter your annual bonus");

        bonusPanel.add(bonusLabel, BorderLayout.NORTH);
        bonusPanel.add(bonusField, BorderLayout.CENTER);
        formPanel.add(bonusPanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Other income field
        JPanel otherIncomePanel = new JPanel(new BorderLayout());
        otherIncomePanel.setBackground(Color.WHITE);
        otherIncomePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        otherIncomePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel otherIncomeLabel = createStyledLabel("Other Income e.g. freelance, online sales (THB)");
        otherIncomeField = createStyledTextField("Enter other income");
        setupPlaceholder(otherIncomeField, "Enter other income");

        otherIncomePanel.add(otherIncomeLabel, BorderLayout.NORTH);
        otherIncomePanel.add(otherIncomeField, BorderLayout.CENTER);
        formPanel.add(otherIncomePanel);

        contentPanel.add(formPanel, BorderLayout.CENTER);

        // Set up footer
        JButton nextButton = createStyledButton("NEXT", true); // All caps for better visibility
        nextButton.setFont(new Font("Arial", Font.BOLD, 16));
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processForm();
            }
        });
        footerPanel.add(nextButton);
    }

    /**
     * Set up placeholder text for a text field
     * @param field The text field
     * @param placeholder The placeholder text
     */
    private void setupPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    /**
     * Reset the form fields to their default values
     */
    public void resetForm() {
        // Reset all fields to their placeholder values
        nameField.setText("Enter your full name");
        nameField.setForeground(Color.GRAY);

        monthlyIncomeField.setText("Enter your monthly income");
        monthlyIncomeField.setForeground(Color.GRAY);

        bonusField.setText("Enter your annual bonus");
        bonusField.setForeground(Color.GRAY);

        otherIncomeField.setText("Enter other income");
        otherIncomeField.setForeground(Color.GRAY);
    }

    /**
     * Process the form data and move to the next step
     */
    private void processForm() {
        // Validate form
        String name = nameField.getText().trim();
        if (name.isEmpty() || name.equals("Enter your full name")) {
            JOptionPane.showMessageDialog(this,
                "Please enter your full name",
                "Incomplete Information",
                JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return;
        }

        // Validate name - only allow English letters (no numbers or special characters)
        if (!name.matches("^[a-zA-Z\\s]+$")) {
            JOptionPane.showMessageDialog(this,
                "Name must contain only English letters (no numbers or special characters)",
                "Invalid Name",
                JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return;
        }

        try {
            // Parse numeric fields
            double monthlyIncome = parseDoubleField(monthlyIncomeField, "Enter your monthly income");
            double bonus = parseDoubleField(bonusField, "Enter your annual bonus");
            double otherIncome = parseDoubleField(otherIncomeField, "Enter other income");

            // Create user
            User user = app.getTaxSystem().createUser(
                nameField.getText().trim(),
                "Single", // Default status, will be updated in the next step
                monthlyIncome,
                bonus,
                otherIncome
            );

            // Set current user ID
            app.setCurrentUserId(user.getId());

            // Move to next step
            app.showDeductionStep();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Invalid Information",
                JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error saving data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Parse a double value from a text field
     * @param field The text field
     * @param placeholder The placeholder text
     * @return The parsed double value
     * @throws NumberFormatException if the field does not contain a valid number
     */
    private double parseDoubleField(JTextField field, String placeholder) throws NumberFormatException {
        String text = field.getText().trim();
        if (text.isEmpty() || text.equals(placeholder)) {
            return 0.0;
        }

        try {
            // Remove any commas or other formatting
            text = text.replaceAll(",", "");
            double value = Double.parseDouble(text);
            if (value < 0) {
                throw new NumberFormatException("Value must be non-negative");
            }
            return value;
        } catch (NumberFormatException e) {
            field.requestFocus();
            throw new NumberFormatException("Please enter a valid number");
        }
    }
}
