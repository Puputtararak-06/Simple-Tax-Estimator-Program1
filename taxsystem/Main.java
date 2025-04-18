package taxsystem;

import javax.swing.*;
import java.awt.Color;
import taxsystem.ui.StepBasedTaxApp;

/**
 * Main class to run the step-based tax application
 */
public class Main {
    public static void main(String[] args) {
        // Set global UI properties for better visibility
        try {
            // Set button colors globally
            UIManager.put("Button.background", new Color(255, 0, 0)); // Bright red
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.font", new java.awt.Font("Arial", java.awt.Font.BOLD, 16));

            // Set checkbox colors
            UIManager.put("CheckBox.background", new Color(240, 248, 255)); // Light blue
            UIManager.put("CheckBox.foreground", Color.BLACK);
            UIManager.put("CheckBox.font", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));

            // Set spinner colors
            UIManager.put("Spinner.background", Color.WHITE);
            UIManager.put("Spinner.foreground", Color.BLACK);
            UIManager.put("Spinner.font", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));

            // Customize JOptionPane buttons
            UIManager.put("OptionPane.yesButtonText", "YES");
            UIManager.put("OptionPane.noButtonText", "NO");
            UIManager.put("OptionPane.cancelButtonText", "CANCEL");
            UIManager.put("OptionPane.okButtonText", "OK");

            // Set JOptionPane colors and fonts
            UIManager.put("OptionPane.background", Color.WHITE);
            UIManager.put("OptionPane.messageForeground", Color.BLACK); // Black text for better readability
            UIManager.put("OptionPane.messageFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 20)); // Larger font
            UIManager.put("OptionPane.buttonFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 18)); // Larger button font

            // Set button colors
            UIManager.put("OptionPane.buttonBackground", new Color(0, 120, 215)); // Blue button background
            UIManager.put("OptionPane.buttonForeground", Color.WHITE); // White text on buttons
        } catch (Exception e) {
            System.err.println("Error setting UI properties: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            StepBasedTaxApp app = new StepBasedTaxApp();
            app.setVisible(true);
        });
    }
}
