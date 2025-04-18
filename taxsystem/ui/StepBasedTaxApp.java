package taxsystem.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import taxsystem.NewTaxSystem;
import taxsystem.model.User;
import taxsystem.model.Deduction;
import taxsystem.model.TaxCalculation;
import taxsystem.tax.TaxCalculator;

/**
 * StepBasedTaxApp is the main application for the step-based tax calculation UI
 */
public class StepBasedTaxApp extends JFrame {
    private NewTaxSystem taxSystem;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // Step panels
    private IncomeStepPanel incomeStepPanel;
    private DeductionStepPanel deductionStepPanel;
    private TaxResultPanel resultStepPanel;

    // Current user ID
    private int currentUserId = -1;

    /**
     * Constructor
     */
    public StepBasedTaxApp() {
        try {
            taxSystem = new NewTaxSystem();
            initializeUI();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error initializing application: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    /**
     * Initialize the UI
     */
    private void initializeUI() {
        try {
            // Set look and feel to system default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set up the frame
        setTitle("Personal Income Tax Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Create card layout and panel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Create step panels
        incomeStepPanel = new IncomeStepPanel(this);
        deductionStepPanel = new DeductionStepPanel(this);
        resultStepPanel = new TaxResultPanel(this);

        // Add panels to card layout
        cardPanel.add(incomeStepPanel, "income");
        cardPanel.add(deductionStepPanel, "deduction");
        cardPanel.add(resultStepPanel, "result");

        // Show the first panel
        cardLayout.show(cardPanel, "income");

        // Add card panel to frame
        add(cardPanel);
    }

    /**
     * Show the income step panel and reset form fields
     */
    public void showIncomeStep() {
        // Reset the current user ID
        this.currentUserId = -1;

        // Reset the income step panel form fields
        incomeStepPanel.resetForm();

        // Show the income step panel
        cardLayout.show(cardPanel, "income");
    }

    /**
     * Show the deduction step panel
     */
    public void showDeductionStep() {
        cardLayout.show(cardPanel, "deduction");
    }

    /**
     * Show the result step panel
     */
    public void showResultStep() {
        cardLayout.show(cardPanel, "result");
    }

    /**
     * Get the tax system
     * @return The tax system
     */
    public NewTaxSystem getTaxSystem() {
        return taxSystem;
    }

    /**
     * Set the current user ID
     * @param userId The user ID
     */
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    /**
     * Get the current user ID
     * @return The current user ID
     */
    public int getCurrentUserId() {
        return currentUserId;
    }

    /**
     * Main method
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StepBasedTaxApp app = new StepBasedTaxApp();
            app.setVisible(true);
        });
    }
}
