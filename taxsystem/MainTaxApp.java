

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
   
/**
 * Main application class for the Tax Filing System
 * This serves as the entry point and main menu for the application
 */
public class MainTaxApp {
    private JFrame frame;
    private TaxSystem taxSystem;

    public MainTaxApp() {
        taxSystem = new TaxSystem();
        initialize();
    }

    private void initialize() {
        // Create main frame
        frame = new JFrame("Tax Filing System");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set application icon
        ImageIcon icon = createImageIcon("tax_icon.png");
        if (icon != null) {
            frame.setIconImage(icon.getImage());
        }

        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Add header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Add menu panel
        JPanel menuPanel = createMenuPanel();
        mainPanel.add(menuPanel, BorderLayout.CENTER);

        // Add footer panel
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Personal Income Tax Filing System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        panel.add(titleLabel, BorderLayout.CENTER);

        JLabel subtitleLabel = new JLabel("Tax Year 2023", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        panel.add(subtitleLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));

        // Simple Tax Form Button
        JButton simpleTaxButton = createMenuButton("Simple Tax Form", "simple_tax.png");
        simpleTaxButton.addActionListener(e -> openSimpleTaxForm());
        panel.add(simpleTaxButton);

        // Detailed Tax Form Button
        JButton detailedTaxButton = createMenuButton("Detailed Tax Form", "detailed_tax.png");
        detailedTaxButton.addActionListener(e -> openDetailedTaxForm());
        panel.add(detailedTaxButton);

        // Tax Report Button
        JButton reportButton = createMenuButton("Tax Reports", "tax_report.png");
        reportButton.addActionListener(e -> openTaxReport());
        panel.add(reportButton);

        // Exit Button
        JButton exitButton = createMenuButton("Exit", "exit.png");
        exitButton.addActionListener(e -> exitApplication());
        panel.add(exitButton);

        return panel;
    }

    private JButton createMenuButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setFont(new Font("TH SarabunPSK", Font.BOLD, 20));
        button.setPreferredSize(new Dimension(200, 100));

        // Set icon if available
        ImageIcon icon = createImageIcon(iconName);
        if (icon != null) {
            button.setIcon(icon);
            button.setHorizontalTextPosition(JButton.CENTER);
            button.setVerticalTextPosition(JButton.BOTTOM);
        }

        return button;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel copyrightLabel = new JLabel("© 2023 Tax Filing System - Developed with Java Swing", JLabel.CENTER);
        copyrightLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(copyrightLabel, BorderLayout.CENTER);

        return panel;
    }

    private void openSimpleTaxForm() {
        // ใช้ TaxSystem.main แทนเนื่องจากยังไม่มีคลาส SimpleTaxForm
        SwingUtilities.invokeLater(() -> {
            String[] args = {};
            TaxSystem.main(args);
        });
    }

    private void openDetailedTaxForm() {
        SwingUtilities.invokeLater(() -> new DetailedTaxForm(taxSystem));
    }

    private void openTaxReport() {
        // Open the tax history viewer to show saved calculations
        SwingUtilities.invokeLater(() -> new TaxHistoryViewer());
    }

    private void exitApplication() {
        int response = JOptionPane.showConfirmDialog(
            frame,
            "Do you want to exit the application?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            frame.dispose();
            System.exit(0);
        }
    }

    // Helper method to create ImageIcon from a file
    private ImageIcon createImageIcon(String path) {
        try {
            // For this example, we'll return null since we don't have actual image files
            // In a real application, you would load the image from resources
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start the application
        SwingUtilities.invokeLater(() -> new MainTaxApp());
    }
}
