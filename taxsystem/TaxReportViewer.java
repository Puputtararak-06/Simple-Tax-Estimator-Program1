

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A viewer for tax reports and calculation history
 */
public class TaxReportViewer {
    private JFrame frame;
    private TaxSystem taxSystem;
    private JTable reportTable;
    private DefaultTableModel tableModel;

    public TaxReportViewer(TaxSystem taxSystem) {
        this.taxSystem = taxSystem;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Tax Report");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Tax Calculation Report", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create table
        createReportTable();
        JScrollPane scrollPane = new JScrollPane(reportTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> refreshData());
        buttonPanel.add(refreshButton);

        JButton exportButton = new JButton("Export Report");
        exportButton.addActionListener(e -> exportReport());
        buttonPanel.add(exportButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);

        // Load initial data
        loadDummyData();
    }

    private void createReportTable() {
        // Define table columns
        String[] columns = {
            "ID",
            "Name",
            "Annual Income",
            "Deductions",
            "Net Income",
            "Tax Due",
            "Tax Paid",
            "Balance",
            "Status",
            "Calculation Date"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        reportTable = new JTable(tableModel);
        reportTable.setFillsViewportHeight(true);
        reportTable.setFont(new Font("Arial", Font.PLAIN, 14));
        reportTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        // Set column widths
        reportTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        reportTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        reportTable.getColumnModel().getColumn(9).setPreferredWidth(150); // Date
    }

    private void loadDummyData() {
        // Clear existing data
        tableModel.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        try {
            // Try to load real data from file
            java.util.List<String[]> calculations = FileManager.loadCalculations();

            if (calculations.isEmpty()) {
                // If no data found, add some dummy data
                // Add some dummy rows
                Object[] row1 = {
                    "1",
                    "John Smith",
                    "600,000.00",
                    "100,000.00",
                    "500,000.00",
                    "32,500.00",
                    "30,000.00",
                    "2,500.00",
                    "Pay more",
                    now.minusDays(5).format(formatter)
                };
                tableModel.addRow(row1);

                Object[] row2 = {
                    "2",
                    "Jane Doe",
                    "450,000.00",
                    "80,000.00",
                    "370,000.00",
                    "19,500.00",
                    "25,000.00",
                    "-5,500.00",
                    "Tax refund",
                    now.minusDays(3).format(formatter)
                };
                tableModel.addRow(row2);

                Object[] row3 = {
                    "3",
                    "Robert Johnson",
                    "1,200,000.00",
                    "150,000.00",
                    "1,050,000.00",
                    "142,500.00",
                    "120,000.00",
                    "22,500.00",
                    "Pay more",
                    now.minusDays(1).format(formatter)
                };
                tableModel.addRow(row3);
            } else {
                // Add data from file
                for (String[] calculation : calculations) {
                    tableModel.addRow(calculation);
                }
            }
        } catch (java.io.IOException e) {
            // If error loading from file, show error and load dummy data
            JOptionPane.showMessageDialog(frame,
                "Error loading data: " + e.getMessage(),
                "Data Loading Error",
                JOptionPane.ERROR_MESSAGE);

            // Add dummy data as fallback
            Object[] row1 = {
                "1",
                "John Smith",
                "600,000.00",
                "100,000.00",
                "500,000.00",
                "32,500.00",
                "30,000.00",
                "2,500.00",
                "Pay more",
                now.minusDays(5).format(formatter)
            };
            tableModel.addRow(row1);
        }
    }

    private void refreshData() {
        // Reload data from file system
        loadDummyData();

        // Create a new calculation with the current TaxSystem to demonstrate its use
        try {
            // Create a test user and calculation
            User testUser = taxSystem.createUser("Test User", "Single", "Test", 500000);
            taxSystem.addDeduction(testUser.getId(), "Test Deduction", 50000);
            taxSystem.calculateTax(testUser, 25000);

            // Reload data to show the new calculation
            loadDummyData();

            JOptionPane.showMessageDialog(frame,
                "Data refreshed successfully and new test calculation added",
                "Refresh Data",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                "Data refreshed but could not create test calculation: " + e.getMessage(),
                "Refresh Data",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void exportReport() {
        // In a real application, this would export the data to a file
        JOptionPane.showMessageDialog(frame,
            "Export feature is not available yet",
            "Cannot Export",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            TaxSystem taxSystem = new TaxSystem();
            new TaxReportViewer(taxSystem);
        });
    }
}
