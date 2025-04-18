package taxsystem.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import taxsystem.model.User;
import taxsystem.tax.DeductionCalculator;

/**
 * DeductionStepPanel represents the second step of the tax calculation wizard
 */
public class DeductionStepPanel extends StepPanel {
    // Form fields
    private JComboBox<String> statusComboBox;
    private JTextField personalDeductionField;
    private JCheckBox spouseNoIncomeCheckBox;
    private JSpinner childrenCountSpinner;
    private JPanel spouseOptionsPanel;

    /**
     * Constructor
     * @param app The parent application
     */
    public DeductionStepPanel(StepBasedTaxApp app) {
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
        JPanel progressPanel = createProgressIndicator(2);
        headerPanel.add(progressPanel, BorderLayout.CENTER);

        // Set up content
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 100, 20, 100));

        // Title
        JLabel formTitleLabel = createStyledTitle("Family Deductions");
        formTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formTitleLabel.setForeground(new Color(44, 102, 154));
        formPanel.add(formTitleLabel);
        formPanel.add(Box.createVerticalStrut(30));

        // Marital status
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel statusLabel = createStyledLabel("Marital Status");
        String[] statusOptions = {"Please select status", "Single", "Married"};
        statusComboBox = new JComboBox<>(statusOptions);
        statusComboBox.setPreferredSize(new Dimension(300, 40));

        statusPanel.add(statusLabel, BorderLayout.NORTH);
        statusPanel.add(statusComboBox, BorderLayout.CENTER);
        formPanel.add(statusPanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Spouse options panel (visible only when "Married" is selected)
        spouseOptionsPanel = new JPanel(new BorderLayout());
        spouseOptionsPanel.setBackground(new Color(240, 248, 255)); // Light blue background to make it stand out
        spouseOptionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        spouseOptionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        spouseOptionsPanel.setVisible(true); // Always visible for testing

        spouseNoIncomeCheckBox = new JCheckBox("Spouse has no income (60,000 THB deduction instead of 30,000 THB)");
        spouseNoIncomeCheckBox.setBackground(new Color(240, 248, 255)); // Light blue background
        spouseNoIncomeCheckBox.setForeground(Color.BLACK); // Black text
        spouseNoIncomeCheckBox.setFont(new Font("Arial", Font.PLAIN, 14)); // Normal font
        spouseOptionsPanel.add(spouseNoIncomeCheckBox, BorderLayout.CENTER);

        formPanel.add(spouseOptionsPanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Children deduction panel
        JPanel childrenPanel = new JPanel(new BorderLayout());
        childrenPanel.setBackground(new Color(240, 248, 255)); // Light blue background
        childrenPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        childrenPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150)); // Taller to fit radio buttons

        JLabel childrenLabel = createStyledLabel("Number of children for deduction (30,000 THB each)");
        childrenLabel.setForeground(Color.BLACK);
        childrenLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Create radio button panel for children selection
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        radioPanel.setBackground(new Color(240, 248, 255));

        ButtonGroup childrenGroup = new ButtonGroup();
        JRadioButton[] childrenOptions = new JRadioButton[4]; // 0-3 children

        for (int i = 0; i < 4; i++) {
            childrenOptions[i] = new JRadioButton(String.valueOf(i));
            childrenOptions[i].setFont(new Font("Arial", Font.BOLD, 14));
            childrenOptions[i].setBackground(new Color(240, 248, 255));
            childrenGroup.add(childrenOptions[i]);
            radioPanel.add(childrenOptions[i]);
        }

        // Select 0 by default
        childrenOptions[0].setSelected(true);

        // We'll still keep the spinner but make it invisible for compatibility
        // with existing code that uses it
        SpinnerNumberModel childrenModel = new SpinnerNumberModel(0, 0, 3, 1);
        childrenCountSpinner = new JSpinner(childrenModel);
        childrenCountSpinner.setVisible(false);

        // Add listener to radio buttons to update spinner value
        for (int i = 0; i < 4; i++) {
            final int value = i;
            childrenOptions[i].addActionListener(e -> {
                childrenCountSpinner.setValue(value);
            });
        }

        childrenPanel.add(childrenLabel, BorderLayout.NORTH);
        childrenPanel.add(radioPanel, BorderLayout.CENTER);
        childrenPanel.add(childrenCountSpinner, BorderLayout.SOUTH); // Hidden but still in the panel

        formPanel.add(childrenPanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Personal deduction
        JPanel personalDeductionPanel = new JPanel(new BorderLayout());
        personalDeductionPanel.setBackground(Color.WHITE);
        personalDeductionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        personalDeductionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel personalDeductionLabel = createStyledLabel("Personal Deduction");
        personalDeductionField = new JTextField("60,000");
        personalDeductionField.setEditable(false);
        personalDeductionField.setPreferredSize(new Dimension(300, 40));
        personalDeductionField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        personalDeductionPanel.add(personalDeductionLabel, BorderLayout.NORTH);
        personalDeductionPanel.add(personalDeductionField, BorderLayout.CENTER);
        formPanel.add(personalDeductionPanel);

        // Add listener to statusComboBox to show/hide spouse options
        statusComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show spouse options panel only when "Married" is selected
                spouseOptionsPanel.setVisible(statusComboBox.getSelectedIndex() == 2);
                formPanel.revalidate();
                formPanel.repaint();
            }
        });

        contentPanel.add(formPanel, BorderLayout.CENTER);

        // Set up footer
        JButton backButton = createStyledButton("BACK", false); // All caps for better visibility
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.showIncomeStep();
            }
        });
        footerPanel.add(backButton);

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
     * Process the form data and move to the next step
     */
    private void processForm() {
        // Validate form
        if (statusComboBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this,
                "Please select your marital status",
                "Incomplete Information",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Get current user
            int userId = app.getCurrentUserId();
            if (userId == -1) {
                throw new IllegalStateException("User not found");
            }

            // Update user status
            User user = app.getTaxSystem().getAllUsers().stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User not found"));

            String status = statusComboBox.getSelectedIndex() == 1 ? "Single" : "Married";
            user.setStatus(status);

            // Calculate and add personal allowance deduction
            int personalAllowance = DeductionCalculator.calculatePersonalAllowance();
            app.getTaxSystem().addDeduction(userId, "Personal Allowance", personalAllowance);

            // Calculate and add spouse deduction if married
            boolean isMarried = status.equals("Married");
            boolean spouseHasNoIncome = isMarried && spouseNoIncomeCheckBox.isSelected();
            int spouseDeduction = DeductionCalculator.calculateSpouseAllowance(isMarried, spouseHasNoIncome);

            if (spouseDeduction > 0) {
                app.getTaxSystem().addDeduction(userId, "Spouse Allowance", spouseDeduction);
            }

            // Calculate and add children deduction
            int childrenCount = (Integer) childrenCountSpinner.getValue();
            int childrenDeduction = DeductionCalculator.calculateChildrenAllowance(childrenCount);

            if (childrenDeduction > 0) {
                app.getTaxSystem().addDeduction(userId, "Children Allowance", childrenDeduction);
            }

            // Move to next step (for this demo, we'll skip to the result step)
            app.showResultStep();

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
