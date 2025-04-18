

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DetailedTaxForm {
    private JFrame frame;
    private TaxSystem taxSystem;
    // Map removed as it was not used in current implementation

    // Form fields
    private JTextField nameField;
    private JTextField occupationField;
    private JComboBox<String> statusComboBox;
    private JTextField incomeField;
    private JTextField taxPaidField;

    // Deduction fields
    private JTextField personalDeductionField;
    private JTextField insuranceDeductionField;
    private JTextField donationDeductionField;
    private JTextField educationDeductionField;
    private JTextField homeInterestDeductionField;
    private JTextField otherDeductionField;

    public DetailedTaxForm(TaxSystem taxSystem) {
        this.taxSystem = taxSystem;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Tax Details Form");
        frame.setSize(600, 700);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Use a panel with GridBagLayout for more control
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Personal Information Section
        JPanel personalInfoPanel = createSectionPanel("Personal Information");
        personalInfoPanel.setLayout(new GridLayout(0, 2, 10, 10));

        personalInfoPanel.add(new JLabel("Full Name:"));
        nameField = new JTextField();
        personalInfoPanel.add(nameField);

        personalInfoPanel.add(new JLabel("Occupation:"));
        occupationField = new JTextField();
        personalInfoPanel.add(occupationField);

        personalInfoPanel.add(new JLabel("Status:"));
        String[] statusOptions = {"Single", "Married", "Divorced", "Widowed"};
        statusComboBox = new JComboBox<>(statusOptions);
        personalInfoPanel.add(statusComboBox);

        personalInfoPanel.add(new JLabel("Annual Income (THB):"));
        incomeField = new JTextField();
        personalInfoPanel.add(incomeField);

        personalInfoPanel.add(new JLabel("Tax Already Paid (THB):"));
        taxPaidField = new JTextField();
        personalInfoPanel.add(taxPaidField);

        // Add personal info panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(personalInfoPanel, gbc);

        // Deduction Section
        JPanel deductionPanel = createSectionPanel("Deduction Items");
        deductionPanel.setLayout(new GridLayout(0, 2, 10, 10));

        deductionPanel.add(new JLabel("Personal Allowance (THB):"));
        personalDeductionField = new JTextField("60000");
        deductionPanel.add(personalDeductionField);

        deductionPanel.add(new JLabel("Life/Health Insurance (THB):"));
        insuranceDeductionField = new JTextField("0");
        deductionPanel.add(insuranceDeductionField);

        deductionPanel.add(new JLabel("Donations (THB):"));
        donationDeductionField = new JTextField("0");
        deductionPanel.add(donationDeductionField);

        deductionPanel.add(new JLabel("Child Education (THB):"));
        educationDeductionField = new JTextField("0");
        deductionPanel.add(educationDeductionField);

        deductionPanel.add(new JLabel("Home Loan Interest (THB):"));
        homeInterestDeductionField = new JTextField("0");
        deductionPanel.add(homeInterestDeductionField);

        deductionPanel.add(new JLabel("Others (THB):"));
        otherDeductionField = new JTextField("0");
        deductionPanel.add(otherDeductionField);

        // Add deduction panel
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        mainPanel.add(deductionPanel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton calculateButton = new JButton("Calculate Tax");
        calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateTax();
            }
        });
        buttonPanel.add(calculateButton);

        JButton clearButton = new JButton("Clear Form");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        buttonPanel.add(clearButton);

        JButton backButton = new JButton("Back to Main");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        buttonPanel.add(backButton);

        // Add button panel
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        // Result area
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setRows(10);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollPane, gbc);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    private void calculateTax() {
        try {
            String name = nameField.getText();
            String occupation = occupationField.getText();
            String status = (String) statusComboBox.getSelectedItem();
            double income = Double.parseDouble(incomeField.getText());
            double taxPaid = Double.parseDouble(taxPaidField.getText());

            // Calculate total deduction
            double personalDeduction = Double.parseDouble(personalDeductionField.getText());
            double insuranceDeduction = Double.parseDouble(insuranceDeductionField.getText());
            double donationDeduction = Double.parseDouble(donationDeductionField.getText());
            double educationDeduction = Double.parseDouble(educationDeductionField.getText());
            double homeInterestDeduction = Double.parseDouble(homeInterestDeductionField.getText());
            double otherDeduction = Double.parseDouble(otherDeductionField.getText());

            double totalDeduction = personalDeduction + insuranceDeduction + donationDeduction +
                                   educationDeduction + homeInterestDeduction + otherDeduction;

            // Create user and calculate tax
            User user = taxSystem.createUser(name, status, occupation, income);
            taxSystem.addDeduction(user.getId(), "Total Deductions", totalDeduction);

            // Add individual deductions for detailed record
            if (personalDeduction > 0) taxSystem.addDeduction(user.getId(), "Personal Allowance", personalDeduction);
            if (insuranceDeduction > 0) taxSystem.addDeduction(user.getId(), "Life/Health Insurance", insuranceDeduction);
            if (donationDeduction > 0) taxSystem.addDeduction(user.getId(), "Donations", donationDeduction);
            if (educationDeduction > 0) taxSystem.addDeduction(user.getId(), "Child Education", educationDeduction);
            if (homeInterestDeduction > 0) taxSystem.addDeduction(user.getId(), "Home Loan Interest", homeInterestDeduction);
            if (otherDeduction > 0) taxSystem.addDeduction(user.getId(), "Others", otherDeduction);

            TaxCalculation result = taxSystem.calculateTax(user, taxPaid);

            // Create a custom dialog with better formatting
            JDialog resultDialog = new JDialog(frame, "Tax Calculation Result", true);
            resultDialog.setLayout(new BorderLayout());
            resultDialog.setSize(500, 600);
            resultDialog.setLocationRelativeTo(frame);

            // Create a panel for the content
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Add title
            JLabel titleLabel = new JLabel("Tax Calculation Result");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(titleLabel);
            contentPanel.add(Box.createVerticalStrut(20));

            // Add user info
            JPanel userInfoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            userInfoPanel.setBorder(BorderFactory.createTitledBorder("User Information"));

            userInfoPanel.add(new JLabel("Name:"));
            userInfoPanel.add(new JLabel(name));

            userInfoPanel.add(new JLabel("Status:"));
            userInfoPanel.add(new JLabel(status));

            userInfoPanel.add(new JLabel("Occupation:"));
            userInfoPanel.add(new JLabel(occupation));

            userInfoPanel.add(new JLabel("Annual Income:"));
            userInfoPanel.add(new JLabel(String.format("%.2f THB", income)));

            userInfoPanel.add(new JLabel("Total Deductions:"));
            userInfoPanel.add(new JLabel(String.format("%.2f THB", totalDeduction)));

            userInfoPanel.add(new JLabel("Net Income:"));
            userInfoPanel.add(new JLabel(String.format("%.2f THB", income - totalDeduction)));

            contentPanel.add(userInfoPanel);
            contentPanel.add(Box.createVerticalStrut(20));

            // Add tax result with highlighted tax amount
            JPanel taxResultPanel = new JPanel();
            taxResultPanel.setLayout(new BoxLayout(taxResultPanel, BoxLayout.Y_AXIS));
            taxResultPanel.setBorder(BorderFactory.createTitledBorder("Tax Calculation"));

            // Create a text area for the tax calculation details
            JTextArea taxDetailsArea = new JTextArea(result.getSummary());
            taxDetailsArea.setEditable(false);
            taxDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            taxDetailsArea.setBackground(taxResultPanel.getBackground());
            taxDetailsArea.setLineWrap(true);
            taxDetailsArea.setWrapStyleWord(true);

            // Create a panel for the tax amount with a border
            JPanel taxAmountPanel = new JPanel();
            taxAmountPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            taxAmountPanel.setBackground(new Color(255, 240, 240)); // Light red background

            // Format the tax amount - always show it, even if zero
            String taxText;
            double taxAmount = result.getTaxDue();
            if (taxAmount <= 0) {
                taxText = "Tax Amount: 0.00 THB (No tax payable)";
            } else {
                taxText = String.format("Tax Amount: %.2f THB", taxAmount);
            }

            JLabel taxAmountLabel = new JLabel(taxText);
            taxAmountLabel.setFont(new Font("Arial", Font.BOLD, 18));
            taxAmountLabel.setForeground(new Color(220, 20, 60)); // Crimson red
            taxAmountPanel.add(taxAmountLabel);

            // Add a border to make it stand out
            taxAmountPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 20, 60), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));

            taxResultPanel.add(taxAmountPanel);
            taxResultPanel.add(Box.createVerticalStrut(10));
            taxResultPanel.add(taxDetailsArea);

            contentPanel.add(taxResultPanel);

            // Add a close button
            JButton closeButton = new JButton("Close");
            closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            closeButton.addActionListener(e -> resultDialog.dispose());

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(closeButton);

            // Add components to the dialog
            resultDialog.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
            resultDialog.add(buttonPanel, BorderLayout.SOUTH);

            // Show the dialog
            resultDialog.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame,
                "Please enter valid information",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        nameField.setText("");
        occupationField.setText("");
        statusComboBox.setSelectedIndex(0);
        incomeField.setText("");
        taxPaidField.setText("");

        personalDeductionField.setText("60000");
        insuranceDeductionField.setText("0");
        donationDeductionField.setText("0");
        educationDeductionField.setText("0");
        homeInterestDeductionField.setText("0");
        otherDeductionField.setText("0");
    }

    public static void main(String[] args) {
        TaxSystem taxSystem = new TaxSystem();
        new DetailedTaxForm(taxSystem);
    }

    public JFrame getFrame() {
        return frame;
    }
}
