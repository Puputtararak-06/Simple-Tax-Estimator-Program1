package taxsystem.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * StepPanel is the base class for all step panels in the tax calculation UI
 */
public abstract class StepPanel extends JPanel {
    protected StepBasedTaxApp app;
    protected JPanel headerPanel;
    protected JPanel contentPanel;
    protected JPanel footerPanel;

    /**
     * Constructor
     * @param app The parent application
     */
    public StepPanel(StepBasedTaxApp app) {
        this.app = app;
        initializeBaseUI();
        initializeUI();
    }

    /**
     * Initialize the base UI components
     */
    private void initializeBaseUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 0, 0, 0));

        // Create header panel
        headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create content panel
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create footer panel
        footerPanel = new JPanel();
        footerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        // Add panels to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Initialize the UI components specific to this step
     */
    protected abstract void initializeUI();

    /**
     * Create a progress indicator panel
     * @param currentStep The current step number
     * @return The progress indicator panel
     */
    protected JPanel createProgressIndicator(int currentStep) {
        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new GridBagLayout());
        progressPanel.setBackground(Color.WHITE);

        // Step labels and circles
        String[] stepLabels = {
            "Income",
            "Family Deductions",
            "Tax Calculation"
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);

        // Create step indicators
        for (int i = 0; i < stepLabels.length; i++) {
            // Create circle
            JPanel circlePanel = createStepCircle(i + 1, i + 1 <= currentStep);
            progressPanel.add(circlePanel, gbc);

            // Create dotted line if not the last step
            if (i < stepLabels.length - 1) {
                JPanel linePanel = createDottedLine(i + 1 < currentStep);
                gbc.gridx++;
                progressPanel.add(linePanel, gbc);
                gbc.gridx++;
            } else {
                gbc.gridx += 2;
            }
        }

        // Add labels below circles
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.insets = new Insets(5, 0, 0, 0);

        for (int i = 0; i < stepLabels.length; i++) {
            JLabel label = new JLabel(stepLabels[i], JLabel.CENTER);
            label.setFont(new Font("Arial", Font.PLAIN, 12));
            label.setForeground(i + 1 <= currentStep ? new Color(75, 175, 125) : Color.GRAY);
            progressPanel.add(label, gbc);
            gbc.gridx += 2;
        }

        return progressPanel;
    }

    /**
     * Create a step circle
     * @param stepNumber The step number
     * @param active Whether the step is active
     * @return The step circle panel
     */
    private JPanel createStepCircle(int stepNumber, boolean active) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int diameter = 30;
                int x = (getWidth() - diameter) / 2;
                int y = (getHeight() - diameter) / 2;

                if (active) {
                    g2d.setColor(new Color(75, 175, 125));
                    g2d.fillOval(x, y, diameter, diameter);
                    g2d.setColor(Color.WHITE);
                } else {
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.fillOval(x, y, diameter, diameter);
                    g2d.setColor(Color.WHITE);
                }

                String text = String.valueOf(stepNumber);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                g2d.drawString(text, x + (diameter - textWidth) / 2, y + (diameter + textHeight) / 2 - 2);

                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(40, 40);
            }
        };

        panel.setOpaque(false);
        return panel;
    }

    /**
     * Create a dotted line
     * @param active Whether the line is active
     * @return The dotted line panel
     */
    private JPanel createDottedLine(boolean active) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int y = getHeight() / 2;

                g2d.setColor(active ? new Color(75, 175, 125) : Color.LIGHT_GRAY);
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
                g2d.drawLine(0, y, getWidth(), y);

                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(80, 2);
            }
        };

        panel.setOpaque(false);
        return panel;
    }

    /**
     * Create a styled button
     * @param text The button text
     * @param primary Whether the button is primary
     * @return The styled button
     */
    protected JButton createStyledButton(String text, boolean primary) {
        JButton button = new JButton(text);

        // Both primary and non-primary buttons have the same style now
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(70, 130, 180)); // Steel blue
        button.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 1));

        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 16)); // Bold but not too large
        button.setPreferredSize(new Dimension(150, 40)); // Standard size

        return button;
    }

    /**
     * Create a styled text field
     * @param placeholder The placeholder text
     * @return The styled text field
     */
    protected JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(300, 40));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Add placeholder
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);

        return textField;
    }

    /**
     * Create a styled label
     * @param text The label text
     * @return The styled label
     */
    protected JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }

    /**
     * Create a styled title
     * @param text The title text
     * @return The styled title
     */
    protected JLabel createStyledTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        return label;
    }
}
