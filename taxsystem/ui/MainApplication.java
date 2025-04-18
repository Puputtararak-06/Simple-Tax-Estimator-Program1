package taxsystem.ui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import taxsystem.NewTaxSystem;

/**
 * MainApplication is the entry point for the tax calculation application
 */
public class MainApplication {
    private NewTaxSystem taxSystem;
    private JFrame mainFrame;
    
    /**
     * Constructor initializes the application
     */
    public MainApplication() {
        taxSystem = new NewTaxSystem();
        initializeUI();
    }
    
    /**
     * Initialize the main UI
     */
    private void initializeUI() {
        try {
            // Set look and feel to system default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create main frame
        mainFrame = new JFrame("Tax Calculation System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null);
        
        // Show the first page (income input)
        showIncomeInputPage();
        
        // Display the frame
        mainFrame.setVisible(true);
    }
    
    /**
     * Show the income input page (first page)
     */
    private void showIncomeInputPage() {
        mainFrame.getContentPane().removeAll();
        mainFrame.add(new IncomeInputPanel(taxSystem, this::showDeductionPage));
        mainFrame.revalidate();
        mainFrame.repaint();
    }
    
    /**
     * Show the deduction page (second page)
     * @param userId The user ID to show deductions for
     */
    private void showDeductionPage(int userId) {
        mainFrame.getContentPane().removeAll();
        mainFrame.add(new DeductionPanel(taxSystem, userId, this::showResultPage));
        mainFrame.revalidate();
        mainFrame.repaint();
    }
    
    /**
     * Show the result page (final page)
     * @param userId The user ID to show results for
     */
    private void showResultPage(int userId) {
        mainFrame.getContentPane().removeAll();
        mainFrame.add(new ResultPanel(taxSystem, userId, this::showIncomeInputPage, this::showAllResultsPage));
        mainFrame.revalidate();
        mainFrame.repaint();
    }
    
    /**
     * Show the all results page
     */
    private void showAllResultsPage() {
        mainFrame.getContentPane().removeAll();
        mainFrame.add(new AllResultsPanel(taxSystem, this::showIncomeInputPage));
        mainFrame.revalidate();
        mainFrame.repaint();
    }
    
    /**
     * Main method to start the application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Run the application on the EDT
        SwingUtilities.invokeLater(() -> {
            try {
                new MainApplication();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error starting application: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
