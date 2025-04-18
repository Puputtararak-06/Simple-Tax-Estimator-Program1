package taxsystem.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import taxsystem.model.Deduction;
import taxsystem.model.TaxCalculation;
import taxsystem.model.User;

/**
 * TaxResultPanel represents the final step of the tax calculation wizard
 */
public class TaxResultPanel extends StepPanel {
    private JPanel resultPanel;
    private JPanel allResultsPanel;
    private CardLayout cardLayout;

    /**
     * Constructor
     * @param app The parent application
     */
    public TaxResultPanel(StepBasedTaxApp app) {
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
        JPanel progressPanel = createProgressIndicator(3);
        headerPanel.add(progressPanel, BorderLayout.CENTER);

        // Set up content with card layout
        cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);

        // Create result panel
        resultPanel = createResultPanel();

        // Create all results panel
        allResultsPanel = createAllResultsPanel();

        // Add panels to card layout
        cardPanel.add(resultPanel, "result");
        cardPanel.add(allResultsPanel, "allResults");

        // Show the result panel by default
        cardLayout.show(cardPanel, "result");

        contentPanel.add(cardPanel, BorderLayout.CENTER);

        // Set up footer
        JButton newCalculationButton = createStyledButton("NEW CALCULATION", false); // All caps for better visibility
        newCalculationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.showIncomeStep();
            }
        });
        footerPanel.add(newCalculationButton);

        JButton viewAllResultsButton = createStyledButton("VIEW ALL RESULTS", true); // All caps for better visibility
        viewAllResultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "allResults");
            }
        });
        footerPanel.add(viewAllResultsButton);
    }

    /**
     * Create the result panel
     * @return The result panel
     */
    private JPanel createResultPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 100, 20, 100));

        // Title
        JLabel resultTitleLabel = createStyledTitle("Tax Calculation Results");
        resultTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultTitleLabel.setForeground(new Color(44, 102, 154));
        panel.add(resultTitleLabel);
        panel.add(Box.createVerticalStrut(30));

        // Result content will be populated when the panel is shown

        return panel;
    }

    /**
     * Create the all results panel
     * @return The all results panel
     */
    private JPanel createAllResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel allResultsTitleLabel = createStyledTitle("All Tax Calculation Results");
        allResultsTitleLabel.setForeground(new Color(44, 102, 154));
        panel.add(allResultsTitleLabel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {
            "No.", "Name", "Annual Income", "Deductions", "Net Income", "Tax Amount"
        };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable resultsTable = new JTable(tableModel);
        resultsTable.setFillsViewportHeight(true);
        resultsTable.getTableHeader().setReorderingAllowed(false);

        // Increase font size for table
        Font tableFont = new Font("Arial", Font.BOLD, 16);
        resultsTable.setFont(tableFont);
        resultsTable.getTableHeader().setFont(tableFont);

        // Set row height for better readability
        resultsTable.setRowHeight(30);

        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < resultsTable.getColumnCount(); i++) {
            resultsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Back button
        JButton backButton = createStyledButton("BACK", false); // All caps for better visibility
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(panel.getParent(), "result");
            }
        });

        JButton clearDataButton = createStyledButton("CLEAR ALL DATA", false); // All caps for better visibility
        clearDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Create custom buttons with colors
                    UIManager.put("OptionPane.yesButtonText", "YES");
                    UIManager.put("OptionPane.noButtonText", "NO");

                    // Set custom colors for this specific dialog
                    UIManager.put("OptionPane.background", new Color(255, 255, 255)); // White background
                    UIManager.put("OptionPane.messageForeground", Color.BLACK); // Black text for better readability
                    UIManager.put("OptionPane.messageFont", new Font("Arial", Font.BOLD, 20)); // Larger font
                    UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.BOLD, 18)); // Larger button font

                    // Set button colors
                    UIManager.put("Button.background", new Color(0, 120, 215)); // Blue button background
                    UIManager.put("Button.foreground", Color.WHITE); // White text on buttons

                    int option = JOptionPane.showConfirmDialog(app,
                        "Are you sure you want to clear all data? This cannot be undone.",
                        "Confirm Clear Data",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                    if (option == JOptionPane.YES_OPTION) {
                        app.getTaxSystem().clearAllData();
                        JOptionPane.showMessageDialog(app,
                            "All data has been cleared successfully.",
                            "Data Cleared",
                            JOptionPane.INFORMATION_MESSAGE);
                        updateAllResultsPanel();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(app,
                        "Error clearing data: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(clearDataButton);
        buttonPanel.add(backButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Update the result panel with the current user's tax calculation
     */
    private void updateResultPanel() {
        // Clear existing components
        resultPanel.removeAll();

        // Title
        JLabel resultTitleLabel = createStyledTitle("Tax Calculation Results");
        resultTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultTitleLabel.setForeground(new Color(44, 102, 154));
        resultPanel.add(resultTitleLabel);
        resultPanel.add(Box.createVerticalStrut(30));

        try {
            // Get current user
            int userId = app.getCurrentUserId();
            if (userId == -1) {
                throw new IllegalStateException("User not found");
            }

            // Get user
            User user = app.getTaxSystem().getAllUsers().stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User not found"));

            // Get deductions
            List<Deduction> deductions = app.getTaxSystem().getUserDeductions(userId);

            // Calculate tax
            TaxCalculation calculation = app.getTaxSystem().calculateTax(userId);

            // Format numbers
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

            // User info
            JPanel userInfoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            userInfoPanel.setBackground(Color.WHITE);
            userInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            userInfoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

            userInfoPanel.add(createStyledLabel("Name:"));
            userInfoPanel.add(createStyledLabel(user.getName()));

            userInfoPanel.add(createStyledLabel("Status:"));
            userInfoPanel.add(createStyledLabel(user.getStatus().equals("Single") ? "Single" : "Married"));

            resultPanel.add(userInfoPanel);
            resultPanel.add(Box.createVerticalStrut(20));

            // Income info
            JPanel incomePanel = new JPanel(new GridLayout(0, 2, 10, 10));
            incomePanel.setBackground(Color.WHITE);
            incomePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            incomePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

            incomePanel.add(createStyledLabel("Monthly Income:"));
            incomePanel.add(createStyledLabel(currencyFormat.format(user.getMonthlyIncome()) + " THB/month"));

            incomePanel.add(createStyledLabel("Annual Income (12 months):"));
            incomePanel.add(createStyledLabel(currencyFormat.format(user.getMonthlyIncome() * 12) + " THB"));

            incomePanel.add(createStyledLabel("Annual Bonus:"));
            incomePanel.add(createStyledLabel(currencyFormat.format(user.getBonus()) + " THB"));

            incomePanel.add(createStyledLabel("Other Income:"));
            incomePanel.add(createStyledLabel(currencyFormat.format(user.getOtherIncome()) + " THB"));

            incomePanel.add(createStyledLabel("Total Annual Income:"));
            incomePanel.add(createStyledLabel(currencyFormat.format(user.getAnnualIncome()) + " THB"));

            resultPanel.add(incomePanel);
            resultPanel.add(Box.createVerticalStrut(20));

            // Deductions
            JPanel deductionsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            deductionsPanel.setBackground(Color.WHITE);
            deductionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            deductionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100 + deductions.size() * 30));

            deductionsPanel.add(createStyledLabel("Deductions:"));
            deductionsPanel.add(new JLabel(""));

            for (Deduction deduction : deductions) {
                deductionsPanel.add(createStyledLabel("- " + deduction.getType() + ":"));
                deductionsPanel.add(createStyledLabel(currencyFormat.format(deduction.getAmount()) + " THB"));
            }

            deductionsPanel.add(createStyledLabel("Total Deductions:"));
            deductionsPanel.add(createStyledLabel(currencyFormat.format(calculation.getTotalDeductions()) + " THB"));

            resultPanel.add(deductionsPanel);
            resultPanel.add(Box.createVerticalStrut(20));

            // Net income panel (outside the red border)
            JPanel netIncomePanel = new JPanel(new GridLayout(0, 2, 10, 10));
            netIncomePanel.setBackground(Color.WHITE);
            netIncomePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            netIncomePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            netIncomePanel.add(createStyledLabel("Net Income:"));
            JLabel netIncomeValueLabel = createStyledLabel(currencyFormat.format(calculation.getNetIncome()) + " THB");
            netIncomeValueLabel.setFont(new Font("Arial", Font.BOLD, 16));
            netIncomePanel.add(netIncomeValueLabel);

            resultPanel.add(netIncomePanel);
            resultPanel.add(Box.createVerticalStrut(20));

            // Tax calculation panel with red border
            JPanel taxPanel = new JPanel();
            taxPanel.setLayout(new BoxLayout(taxPanel, BoxLayout.Y_AXIS));
            taxPanel.setBackground(Color.WHITE);
            taxPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            taxPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 20, 60), 4), // Thicker border
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            taxPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

            // No need for another net income panel here since we already have one above

            // Tax amount label with bold red text for emphasis - centered
            JPanel taxTitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            taxTitlePanel.setBackground(Color.WHITE);
            taxTitlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel taxTitleLabel = createStyledLabel("Tax Amount");
            taxTitleLabel.setFont(new Font("Arial", Font.BOLD, 22)); // Slightly smaller font
            taxTitleLabel.setForeground(new Color(220, 20, 60)); // Crimson red
            taxTitlePanel.add(taxTitleLabel);
            taxPanel.add(taxTitlePanel);

            // Final tax amount - always show it prominently
            String taxText = currencyFormat.format(calculation.getTaxAmount()) + " THB";

            // Always show the tax amount, even if it's zero
            if (calculation.getTaxAmount() <= 0) {
                taxText = "0.00 THB (No tax payable)";
            }

            // Print to console for debugging
            System.out.println("Tax Amount: " + calculation.getTaxAmount());
            System.out.println("Tax Text: " + taxText);

            // Create a panel for the tax amount to make it stand out more - centered
            JPanel taxAmountPanel = new JPanel();
            taxAmountPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            taxAmountPanel.setBackground(new Color(255, 240, 240)); // Light red background
            taxAmountPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel taxAmountLabel = createStyledLabel(taxText);
            taxAmountLabel.setForeground(new Color(220, 20, 60)); // Crimson red
            taxAmountLabel.setFont(new Font("Arial", Font.BOLD, 30)); // Slightly smaller font
            taxAmountPanel.add(taxAmountLabel);

            // Add a border to make it stand out
            taxAmountPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 20, 60), 3),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)
            ));

            taxPanel.add(Box.createVerticalStrut(10));
            taxPanel.add(taxAmountPanel);

            resultPanel.add(taxPanel);

        } catch (IOException e) {
            JLabel errorLabel = createStyledLabel("Error calculating tax: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            resultPanel.add(errorLabel);
        } catch (Exception e) {
            JLabel errorLabel = createStyledLabel("An error occurred: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            resultPanel.add(errorLabel);
        }

        // Refresh the panel
        resultPanel.revalidate();
        resultPanel.repaint();
    }

    /**
     * Update the all results panel with all tax calculations
     */
    private void updateAllResultsPanel() {
        // Get the table
        JTable table = null;
        for (Component comp : allResultsPanel.getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                if (scrollPane.getViewport().getView() instanceof JTable) {
                    table = (JTable) scrollPane.getViewport().getView();
                    break;
                }
            }
        }

        if (table == null) {
            return;
        }

        // Clear existing data
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try {
            // Get all users and calculations
            List<User> users = app.getTaxSystem().getAllUsers();

            // Format numbers
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

            // Add rows to table
            int rowNum = 1;
            for (User user : users) {
                try {
                    // Calculate tax for this user
                    TaxCalculation calculation = app.getTaxSystem().calculateTax(user.getId());

                    Object[] row = {
                        rowNum++,
                        user.getName(),
                        currencyFormat.format(user.getAnnualIncome()) + " THB",
                        currencyFormat.format(calculation.getTotalDeductions()) + " THB",
                        currencyFormat.format(calculation.getNetIncome()) + " THB",
                        currencyFormat.format(calculation.getTaxAmount()) + " THB"
                    };
                    model.addRow(row);
                } catch (Exception e) {
                    // Skip this user if there's an error
                    System.err.println("Error calculating tax for user " + user.getId() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Called when the panel is shown
     */
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            updateResultPanel();
            updateAllResultsPanel();
        }
        super.setVisible(visible);
    }
}
