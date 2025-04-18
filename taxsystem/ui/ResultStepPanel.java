package taxsystem.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
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
 * ResultStepPanel represents the final step of the tax calculation wizard
 */
public class ResultStepPanel extends StepPanel {
    private JPanel resultPanel;
    private JPanel allResultsPanel;
    private CardLayout cardLayout;

    /**
     * Constructor
     * @param app The parent application
     */
    public ResultStepPanel(StepBasedTaxApp app) {
        super(app);
    }

    /**
     * Initialize the UI components
     */
    @Override
    protected void initializeUI() {
        // Set up header
        JLabel titleLabel = createStyledTitle("ช่วยคำนวณภาษีเงินได้บุคคลธรรมดาประจำปี");
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        // Add progress indicator
        JPanel progressPanel = createProgressIndicator(6);
        headerPanel.add(progressPanel, BorderLayout.CENTER);

        // Set up content with card layout
        cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setPreferredSize(new Dimension(900, 800));

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
        JButton newCalculationButton = createStyledButton("NEW CALCULATION", false);
        newCalculationButton.setPreferredSize(new Dimension(200, 40)); // Wider button to fit text
        newCalculationButton.setFont(new Font("Arial", Font.BOLD, 14)); // Slightly smaller font to fit
        newCalculationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.showIncomeStep();
            }
        });
        footerPanel.add(newCalculationButton);

        JButton viewAllResultsButton = createStyledButton("VIEW ALL RESULTS", true);
        viewAllResultsButton.setPreferredSize(new Dimension(200, 40)); // Wider button to fit text
        viewAllResultsButton.setFont(new Font("Arial", Font.BOLD, 14)); // Slightly smaller font to fit
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
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 100, 20, 100));

        // Add a scroll pane to handle overflow content
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Faster scrolling

        // Create a panel to hold the scroll pane
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Set preferred size to ensure all content is visible
        contentPanel.setPreferredSize(new Dimension(800, 1000));

        // Title
        JLabel resultTitleLabel = createStyledTitle("ผลการคำนวณภาษี");
        resultTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultTitleLabel.setForeground(new Color(44, 102, 154));
        contentPanel.add(resultTitleLabel);
        contentPanel.add(Box.createVerticalStrut(30));

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
        JLabel allResultsTitleLabel = createStyledTitle("ผลการคำนวณภาษีทั้งหมด");
        panel.add(allResultsTitleLabel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {
            "ลำดับ", "ชื่อ-นามสกุล", "รายได้ต่อปี", "ลดหย่อน", "รายได้สุทธิ", "ภาษีที่ต้องชำระ"
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

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Back button
        JButton backButton = createStyledButton("BACK", false);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(panel.getParent(), "result");
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(backButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Update the result panel with the current user's tax calculation
     */
    private void updateResultPanel() {
        // Find the content panel inside the scroll pane
        JScrollPane scrollPane = null;
        for (Component comp : resultPanel.getComponents()) {
            if (comp instanceof JScrollPane) {
                scrollPane = (JScrollPane) comp;
                break;
            }
        }

        if (scrollPane == null) {
            return;
        }

        // Get the content panel
        JPanel contentPanel = (JPanel) scrollPane.getViewport().getView();

        // Clear existing components
        contentPanel.removeAll();

        // Title
        JLabel resultTitleLabel = createStyledTitle("ผลการคำนวณภาษี");
        resultTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(resultTitleLabel);
        contentPanel.add(Box.createVerticalStrut(30));

        try {
            // Get current user
            int userId = app.getCurrentUserId();
            if (userId == -1) {
                throw new IllegalStateException("ไม่พบข้อมูลผู้ใช้");
            }

            // Get user
            User user = app.getTaxSystem().getAllUsers().stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("ไม่พบข้อมูลผู้ใช้"));

            // Get deductions
            List<Deduction> deductions = app.getTaxSystem().getUserDeductions(userId);

            // Calculate tax
            TaxCalculation calculation = app.getTaxSystem().calculateTax(userId);

            // Format numbers
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("th", "TH"));

            // User info
            JPanel userInfoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            userInfoPanel.setBackground(Color.WHITE);
            userInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            userInfoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

            userInfoPanel.add(createStyledLabel("ชื่อ-นามสกุล:"));
            userInfoPanel.add(createStyledLabel(user.getName()));

            userInfoPanel.add(createStyledLabel("สถานะ:"));
            userInfoPanel.add(createStyledLabel(user.getStatus().equals("Single") ? "โสด" : "สมรส"));

            contentPanel.add(userInfoPanel);
            contentPanel.add(Box.createVerticalStrut(20));

            // Income info
            JPanel incomePanel = new JPanel(new GridLayout(0, 2, 10, 10));
            incomePanel.setBackground(Color.WHITE);
            incomePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            incomePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

            incomePanel.add(createStyledLabel("เงินเดือน:"));
            incomePanel.add(createStyledLabel(currencyFormat.format(user.getMonthlyIncome()) + " บาท/เดือน"));

            incomePanel.add(createStyledLabel("รายได้ต่อปี (12 เดือน):"));
            incomePanel.add(createStyledLabel(currencyFormat.format(user.getMonthlyIncome() * 12) + " บาท"));

            incomePanel.add(createStyledLabel("โบนัส:"));
            incomePanel.add(createStyledLabel(currencyFormat.format(user.getBonus()) + " บาท"));

            incomePanel.add(createStyledLabel("รายได้อื่นๆ:"));
            incomePanel.add(createStyledLabel(currencyFormat.format(user.getOtherIncome()) + " บาท"));

            incomePanel.add(createStyledLabel("รายได้รวมทั้งปี:"));
            incomePanel.add(createStyledLabel(currencyFormat.format(user.getAnnualIncome()) + " บาท"));

            contentPanel.add(incomePanel);
            contentPanel.add(Box.createVerticalStrut(20));

            // Deductions
            JPanel deductionsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            deductionsPanel.setBackground(Color.WHITE);
            deductionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            deductionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100 + deductions.size() * 30));

            deductionsPanel.add(createStyledLabel("การลดหย่อน:"));
            deductionsPanel.add(new JLabel(""));

            for (Deduction deduction : deductions) {
                deductionsPanel.add(createStyledLabel("- " + deduction.getType() + ":"));
                deductionsPanel.add(createStyledLabel(currencyFormat.format(deduction.getAmount()) + " บาท"));
            }

            deductionsPanel.add(createStyledLabel("รวมลดหย่อนทั้งหมด:"));
            deductionsPanel.add(createStyledLabel(currencyFormat.format(calculation.getTotalDeductions()) + " บาท"));

            contentPanel.add(deductionsPanel);
            contentPanel.add(Box.createVerticalStrut(20));

            // Tax calculation - using a different layout to make tax amount more prominent
            JPanel taxPanel = new JPanel();
            taxPanel.setLayout(new BoxLayout(taxPanel, BoxLayout.Y_AXIS));
            taxPanel.setBackground(Color.WHITE);
            taxPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            taxPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 20, 60), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            taxPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250)); // Increased height for tax amount

            // Create a panel for net income
            JPanel netIncomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            netIncomePanel.setBackground(Color.WHITE);
            netIncomePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel netIncomeLabel = createStyledLabel("รายได้สุทธิ: " + currencyFormat.format(calculation.getNetIncome()) + " บาท");
            netIncomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
            netIncomePanel.add(netIncomeLabel);
            taxPanel.add(netIncomePanel);
            taxPanel.add(Box.createVerticalStrut(10));

            // Tax amount label with bold red text for emphasis - centered
            JPanel taxTitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            taxTitlePanel.setBackground(Color.WHITE);
            taxTitlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel taxTitleLabel = createStyledLabel("ภาษีที่ต้องชำระ");
            taxTitleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Even larger font
            taxTitleLabel.setForeground(new Color(220, 20, 60)); // Crimson red
            taxTitlePanel.add(taxTitleLabel);
            taxPanel.add(taxTitlePanel);

            // Calculate tax amount
            double taxAmount = calculation.getTaxAmount();
            double netIncome = calculation.getNetIncome();

            // Create a panel for tax calculation details
            JPanel taxDetailsPanel = new JPanel();
            taxDetailsPanel.setLayout(new BoxLayout(taxDetailsPanel, BoxLayout.Y_AXIS));
            taxDetailsPanel.setBackground(Color.WHITE);
            taxDetailsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

            // Add tax calculation details
            JLabel taxCalcTitle = new JLabel("การคำนวณภาษีแบบขั้นบันได:");
            taxCalcTitle.setFont(new Font("Arial", Font.BOLD, 16));
            taxCalcTitle.setForeground(new Color(220, 20, 60)); // Crimson red
            taxCalcTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            taxDetailsPanel.add(taxCalcTitle);

            // Add tax brackets calculation with correct rates
            taxDetailsPanel.add(Box.createVerticalStrut(10));

            // First bracket: 0-150,000 = 0%
            JLabel bracket1 = new JLabel("0 - 150,000 บาท = ไม่ต้องเสียภาษี (ช่วงนี้ไม่เสีย)");
            bracket1.setAlignmentX(Component.LEFT_ALIGNMENT);
            bracket1.setFont(new Font("Arial", Font.BOLD, 14));
            taxDetailsPanel.add(bracket1);

            // Calculate tax for each bracket
            double remainingIncome = netIncome;
            double taxBracket1 = 0; // 0-150,000 = 0%

            // Display calculation for first bracket
            String calc1 = String.format("%.2f บาท x 0%% = 0 บาท", Math.min(remainingIncome, 150000.0));
            JLabel calcLabel1 = new JLabel(calc1);
            calcLabel1.setAlignmentX(Component.LEFT_ALIGNMENT);
            calcLabel1.setFont(new Font("Arial", Font.PLAIN, 14));
            calcLabel1.setForeground(new Color(220, 20, 60)); // Crimson red
            taxDetailsPanel.add(calcLabel1);
            taxDetailsPanel.add(Box.createVerticalStrut(5));

            // Second bracket: 150,001-300,000 = 5%
            remainingIncome = Math.max(0, remainingIncome - 150000);
            double taxBracket2 = Math.min(remainingIncome, 150000) * 0.05;

            JLabel bracket2 = new JLabel("150,001 - 300,000 บาท = เสียภาษี 5%");
            bracket2.setAlignmentX(Component.LEFT_ALIGNMENT);
            bracket2.setFont(new Font("Arial", Font.BOLD, 14));
            taxDetailsPanel.add(bracket2);

            String calc2 = String.format("%.2f บาท x 5%% = %.2f บาท", Math.min(remainingIncome, 150000.0), taxBracket2);
            JLabel calcLabel2 = new JLabel(calc2);
            calcLabel2.setAlignmentX(Component.LEFT_ALIGNMENT);
            calcLabel2.setFont(new Font("Arial", Font.PLAIN, 14));
            calcLabel2.setForeground(new Color(220, 20, 60)); // Crimson red
            taxDetailsPanel.add(calcLabel2);
            taxDetailsPanel.add(Box.createVerticalStrut(5));

            // Third bracket: 300,001-500,000 = 10%
            remainingIncome = Math.max(0, remainingIncome - 150000);
            double taxBracket3 = Math.min(remainingIncome, 200000) * 0.10;

            JLabel bracket3 = new JLabel("300,001 - 500,000 บาท = เสียภาษี 10%");
            bracket3.setAlignmentX(Component.LEFT_ALIGNMENT);
            bracket3.setFont(new Font("Arial", Font.BOLD, 14));
            taxDetailsPanel.add(bracket3);

            String calc3 = String.format("%.2f บาท x 10%% = %.2f บาท", Math.min(remainingIncome, 200000.0), taxBracket3);
            JLabel calcLabel3 = new JLabel(calc3);
            calcLabel3.setAlignmentX(Component.LEFT_ALIGNMENT);
            calcLabel3.setFont(new Font("Arial", Font.PLAIN, 14));
            calcLabel3.setForeground(new Color(220, 20, 60)); // Crimson red
            taxDetailsPanel.add(calcLabel3);
            taxDetailsPanel.add(Box.createVerticalStrut(5));

            // Fourth bracket: 500,001-750,000 = 15%
            remainingIncome = Math.max(0, remainingIncome - 200000);
            double taxBracket4 = Math.min(remainingIncome, 250000) * 0.15;

            JLabel bracket4 = new JLabel("500,001 - 750,000 บาท = เสียภาษี 15%");
            bracket4.setAlignmentX(Component.LEFT_ALIGNMENT);
            bracket4.setFont(new Font("Arial", Font.BOLD, 14));
            taxDetailsPanel.add(bracket4);

            String calc4 = String.format("%.2f บาท x 15%% = %.2f บาท", Math.min(remainingIncome, 250000.0), taxBracket4);
            JLabel calcLabel4 = new JLabel(calc4);
            calcLabel4.setAlignmentX(Component.LEFT_ALIGNMENT);
            calcLabel4.setFont(new Font("Arial", Font.PLAIN, 14));
            calcLabel4.setForeground(new Color(220, 20, 60)); // Crimson red
            taxDetailsPanel.add(calcLabel4);
            taxDetailsPanel.add(Box.createVerticalStrut(5));

            // Fifth bracket: 750,001-1,000,000 = 20%
            remainingIncome = Math.max(0, remainingIncome - 250000);
            double taxBracket5 = Math.min(remainingIncome, 250000) * 0.20;

            JLabel bracket5 = new JLabel("750,001 - 1,000,000 บาท = เสียภาษี 20%");
            bracket5.setAlignmentX(Component.LEFT_ALIGNMENT);
            bracket5.setFont(new Font("Arial", Font.BOLD, 14));
            taxDetailsPanel.add(bracket5);

            String calc5 = String.format("%.2f บาท x 20%% = %.2f บาท", Math.min(remainingIncome, 250000.0), taxBracket5);
            JLabel calcLabel5 = new JLabel(calc5);
            calcLabel5.setAlignmentX(Component.LEFT_ALIGNMENT);
            calcLabel5.setFont(new Font("Arial", Font.PLAIN, 14));
            calcLabel5.setForeground(new Color(220, 20, 60)); // Crimson red
            taxDetailsPanel.add(calcLabel5);
            taxDetailsPanel.add(Box.createVerticalStrut(5));

            // Sixth bracket: > 1,000,000 = 25%
            remainingIncome = Math.max(0, remainingIncome - 250000);
            double taxBracket6 = remainingIncome * 0.25;

            JLabel bracket6 = new JLabel("มากกว่า 1,000,000 บาท = เสียภาษี 25%");
            bracket6.setAlignmentX(Component.LEFT_ALIGNMENT);
            bracket6.setFont(new Font("Arial", Font.BOLD, 14));
            taxDetailsPanel.add(bracket6);

            String calc6 = String.format("%.2f บาท x 25%% = %.2f บาท", remainingIncome, taxBracket6);
            JLabel calcLabel6 = new JLabel(calc6);
            calcLabel6.setAlignmentX(Component.LEFT_ALIGNMENT);
            calcLabel6.setFont(new Font("Arial", Font.PLAIN, 14));
            calcLabel6.setForeground(new Color(220, 20, 60)); // Crimson red
            taxDetailsPanel.add(calcLabel6);
            taxDetailsPanel.add(Box.createVerticalStrut(10));

            // Total tax calculation
            double totalTax = taxBracket1 + taxBracket2 + taxBracket3 + taxBracket4 + taxBracket5 + taxBracket6;
            JLabel totalCalc = new JLabel(String.format("ภาษีที่ต้องจ่ายทั้งหมด: %.2f บาท", totalTax));
            totalCalc.setAlignmentX(Component.LEFT_ALIGNMENT);
            totalCalc.setFont(new Font("Arial", Font.BOLD, 16));
            totalCalc.setForeground(new Color(220, 20, 60)); // Crimson red
            taxDetailsPanel.add(totalCalc);

            // Final tax amount - always show it prominently
            String taxText = currencyFormat.format(taxAmount) + " บาท";

            // Always show the tax amount, even if it's zero
            if (taxAmount <= 0) {
                taxText = "0.00 บาท (ไม่ต้องเสียภาษี)";
            }

            // Create a panel for the tax amount to make it stand out more - centered
            JPanel taxAmountPanel = new JPanel();
            taxAmountPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            taxAmountPanel.setBackground(new Color(255, 240, 240)); // Light red background
            taxAmountPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel taxAmountLabel = createStyledLabel(taxText);
            taxAmountLabel.setForeground(new Color(220, 20, 60)); // Crimson red
            taxAmountLabel.setFont(new Font("Arial", Font.BOLD, 28)); // Even larger font
            taxAmountPanel.add(taxAmountLabel);

            // Add a border to make it stand out
            taxAmountPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 20, 60), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));

            taxPanel.add(Box.createVerticalStrut(10));
            taxPanel.add(taxAmountPanel);
            taxPanel.add(Box.createVerticalStrut(10));

            // Add tax panel first (with the tax amount)
            contentPanel.add(taxPanel);

            // Then add tax details panel
            contentPanel.add(taxDetailsPanel);

        } catch (IOException e) {
            JLabel errorLabel = createStyledLabel("เกิดข้อผิดพลาดในการคำนวณภาษี: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            contentPanel.add(errorLabel);
        } catch (Exception e) {
            JLabel errorLabel = createStyledLabel("เกิดข้อผิดพลาด: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            contentPanel.add(errorLabel);
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
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("th", "TH"));

            // Add rows to table
            int rowNum = 1;
            for (User user : users) {
                try {
                    // Calculate tax for this user
                    TaxCalculation calculation = app.getTaxSystem().calculateTax(user.getId());

                    Object[] row = {
                        rowNum++,
                        user.getName(),
                        currencyFormat.format(user.getAnnualIncome()) + " บาท",
                        currencyFormat.format(calculation.getTotalDeductions()) + " บาท",
                        currencyFormat.format(calculation.getNetIncome()) + " บาท",
                        currencyFormat.format(calculation.getTaxAmount()) + " บาท"
                    };
                    model.addRow(row);
                } catch (Exception e) {
                    // Skip this user if there's an error
                    System.err.println("Error calculating tax for user " + user.getId() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "เกิดข้อผิดพลาดในการโหลดข้อมูล: " + e.getMessage(),
                "ข้อผิดพลาด",
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
