
import javax.swing.*;
import java.awt.*;

/**
 * A simplified tax form for basic tax filing
 */
public class SimpleTaxForm {
    private JFrame frame;
    private TaxSystem taxSystem;

    // Form fields
    private JTextField nameField;
    private JTextField incomeField;
    private JTextField taxPaidField;
    private JTextField deductionField;
    private JTextArea resultArea;

    public SimpleTaxForm(TaxSystem taxSystem) {
        this.taxSystem = taxSystem;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Simple Tax Form");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Simple Tax Form");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Form fields
        mainPanel.add(createFormField("Full Name:", nameField = new JTextField()));
        mainPanel.add(createFormField("Annual Income (THB):", incomeField = new JTextField()));
        mainPanel.add(createFormField("Tax Already Paid (THB):", taxPaidField = new JTextField()));
        mainPanel.add(createFormField("Total Deductions (THB):", deductionField = new JTextField()));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton calculateButton = new JButton("Calculate Tax");
        calculateButton.addActionListener(e -> calculateTax());
        buttonPanel.add(calculateButton);

        JButton advancedCalcButton = new JButton("Use Advanced Calculator");
        advancedCalcButton.addActionListener(e -> switchCalculator());
        buttonPanel.add(advancedCalcButton);

        JButton clearButton = new JButton("Clear Form");
        clearButton.addActionListener(e -> clearForm());
        buttonPanel.add(clearButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Result area
        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Calculation Result"));
        mainPanel.add(scrollPane);

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);
    }

    private JPanel createFormField(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(label, BorderLayout.WEST);

        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(textField, BorderLayout.CENTER);

        return panel;
    }

    private void calculateTax() {
        try {
            String name = nameField.getText();
            double income = Double.parseDouble(incomeField.getText());
            double taxPaid = Double.parseDouble(taxPaidField.getText());
            double deduction = Double.parseDouble(deductionField.getText());

            if (name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter your name", "Incomplete Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            User user = taxSystem.createUser(name, "Single", "General", income);
            taxSystem.addDeduction(user.getId(), "Total Deduction", deduction);

            TaxCalculation result = taxSystem.calculateTax(user, taxPaid);

            StringBuilder sb = new StringBuilder();
            sb.append("Name: ").append(name).append("\n");
            sb.append("Annual Income: ").append(String.format("%,.2f", income)).append(" THB\n");
            sb.append("Tax Already Paid: ").append(String.format("%,.2f", taxPaid)).append(" THB\n");
            sb.append("Total Deductions: ").append(String.format("%,.2f", deduction)).append(" THB\n\n");
            sb.append(result.getSummary());

            resultArea.setText(sb.toString());

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame,
                "Please enter valid numeric values",
                "Invalid Data",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                "Error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        nameField.setText("");
        incomeField.setText("");
        taxPaidField.setText("");
        deductionField.setText("");
        resultArea.setText("");
    }

    /**
     * Switch between standard and advanced tax calculators
     * This demonstrates polymorphism by changing the calculator implementation at runtime
     */
    private void switchCalculator() {
        if (taxSystem.getCalculator() instanceof StandardTaxCalculator) {
            taxSystem.setCalculator(new AdvancedTaxCalculator());
            JOptionPane.showMessageDialog(
                frame,
                "Switched to Advanced Tax Calculator\n" +
                "This calculator uses more tax brackets and includes personal allowance.",
                "Calculator Changed",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            taxSystem.setCalculator(new StandardTaxCalculator());
            JOptionPane.showMessageDialog(
                frame,
                "Switched to Standard Tax Calculator\n" +
                "This calculator uses the basic tax calculation method.",
                "Calculator Changed",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            TaxSystem taxSystem = new TaxSystem();
            new SimpleTaxForm(taxSystem);
        });
    }
}
