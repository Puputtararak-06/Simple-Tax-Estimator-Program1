package taxsystem.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import taxsystem.NewTaxSystem;
import taxsystem.model.TaxCalculation;
import taxsystem.model.User;

/**
 * AllResultsPanel displays all tax calculation results
 */
public class AllResultsPanel extends JPanel {
    private NewTaxSystem taxSystem;
    private Runnable onNewCalculation;
    
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    
    /**
     * Constructor
     * @param taxSystem The tax system
     * @param onNewCalculation Callback for when a new calculation should be started
     */
    public AllResultsPanel(NewTaxSystem taxSystem, Runnable onNewCalculation) {
        this.taxSystem = taxSystem;
        this.onNewCalculation = onNewCalculation;
        
        initializeUI();
        loadResults();
    }
    
    /**
     * Initialize the UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("All Tax Calculation Results", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Results table
        String[] columnNames = {
            "User ID", "Name", "Status", "Annual Income", "Deductions", "Net Income", "Tax Amount"
        };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        resultsTable = new JTable(tableModel);
        resultsTable.setFillsViewportHeight(true);
        resultsTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        add(scrollPane, BorderLayout.CENTER);
        
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
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Load and display all tax calculation results
     */
    private void loadResults() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get all users and calculations
        List<User> users = taxSystem.getAllUsers();
        List<TaxCalculation> calculations = taxSystem.getAllCalculations();
        
        // Map calculations by user ID
        Map<Integer, TaxCalculation> calculationsByUserId = calculations.stream()
            .collect(Collectors.toMap(TaxCalculation::getUserId, calc -> calc, (calc1, calc2) -> calc2));
        
        // Format numbers
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "TH"));
        
        // Add rows to table
        for (User user : users) {
            TaxCalculation calculation = calculationsByUserId.get(user.getId());
            if (calculation != null) {
                Object[] row = {
                    user.getId(),
                    user.getName(),
                    user.getStatus(),
                    currencyFormat.format(user.getAnnualIncome()),
                    currencyFormat.format(calculation.getTotalDeductions()),
                    currencyFormat.format(calculation.getNetIncome()),
                    currencyFormat.format(calculation.getTaxAmount())
                };
                tableModel.addRow(row);
            }
        }
        
        // If no results, show message
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, 
                "No tax calculations found", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
