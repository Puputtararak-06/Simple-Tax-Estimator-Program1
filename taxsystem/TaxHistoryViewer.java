import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * TaxHistoryViewer displays tax calculation history from saved files
 * This demonstrates file reading functionality
 */
public class TaxHistoryViewer {
    private JFrame frame;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    
    public TaxHistoryViewer() {
        initialize();
        loadDataFromFile();
    }
    
    private void initialize() {
        frame = new JFrame("Tax Calculation History");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Tax Calculation History", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Create table
        String[] columns = {
            "User ID", 
            "Name", 
            "Net Income", 
            "Tax Due", 
            "Tax Paid", 
            "Balance", 
            "Status"
        };
        
        tableModel = new DefaultTableModel(columns, 0);
        historyTable = new JTable(tableModel);
        historyTable.setFillsViewportHeight(true);
        historyTable.setFont(new Font("Arial", Font.PLAIN, 14));
        historyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(historyTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> loadDataFromFile());
        buttonPanel.add(refreshButton);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        frame.add(mainPanel);
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);
    }
    
    private void loadDataFromFile() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        try {
            // Load calculations from file
            List<String[]> calculations = FileManager.loadCalculations();
            
            for (String[] calculation : calculations) {
                if (calculation.length >= 7) {
                    tableModel.addRow(calculation);
                }
            }
            
            if (calculations.isEmpty()) {
                JOptionPane.showMessageDialog(
                    frame,
                    "No calculation history found.",
                    "No Data",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                frame,
                "Error loading calculation history: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
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
            // Initialize file system
            try {
                FileManager.initialize();
            } catch (Exception e) {
                System.err.println("Error initializing file system: " + e.getMessage());
            }
            
            new TaxHistoryViewer();
        });
    }
}
