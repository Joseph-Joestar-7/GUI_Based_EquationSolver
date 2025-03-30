import javax.swing.*;
import java.awt.*;    
import java.awt.event.*;


public class SolverPage extends JFrame {
    public SolverPage(String equationType) {
        setTitle(equationType + " Equation Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel promptLabel = new JLabel("Enter the " + equationType + " equation:");
        promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        promptLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 18));
        panel.add(promptLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Input field for the equation (you can also use a JTextArea if you prefer)
        JTextField equationInput = new JTextField();
        equationInput.setMaximumSize(new Dimension(500, 30));
        panel.add(equationInput);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel to hold the action buttons (Solve, Plot Graph, Back)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 5));

        JButton solveButton = new JButton("Solve");
        solveButton.setFont(new Font("JetBrains Mono", Font.BOLD, 16));
        buttonPanel.add(solveButton);

        JButton plotGraphButton = new JButton("Plot Graph");
        plotGraphButton.setFont(new Font("JetBrains Mono", Font.BOLD, 16));
        buttonPanel.add(plotGraphButton);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("JetBrains Mono", Font.BOLD, 16));
        buttonPanel.add(backButton);

        panel.add(buttonPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel resultLabel = new JLabel("Result:");
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 18));
        panel.add(resultLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextArea resultArea = new JTextArea(5, 40);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        panel.add(scrollPane);

        // Solve button action: parse input, solve the equation and display result.
        solveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String input = equationInput.getText();
                try {
                    // Create the Equation based on the type (e.g. Polynomial or Logarithmic)
                    Equation eq = EquationFactory.createEquation(equationType, input);
                    EquationResult result = eq.solve();
                    resultArea.setText(result.toString());
                } catch (EquationParseException ex) {
                    resultArea.setText("Error: " + ex.getMessage());
                } catch (Exception ex) {
                    resultArea.setText("An unexpected error occurred: " + ex.getMessage());
                }
            }
        });

        // Plot Graph button action: parse input and open a graph window.
        plotGraphButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String input = equationInput.getText();
                try {
                    // For this example, we assume that plotting is supported only for Polynomial and Logarithmic types.
                    if ("Matrix".equalsIgnoreCase(equationType)) {
                        JOptionPane.showMessageDialog(SolverPage.this,
                                "Graphing is not supported for this type",
                                "Not Supported",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    Equation eq = EquationFactory.createEquation(equationType, input);
                    GraphWindow graphWindow = new GraphWindow(eq);
                    graphWindow.setVisible(true);
                } catch (EquationParseException ex) {
                    JOptionPane.showMessageDialog(SolverPage.this,
                            "Error: " + ex.getMessage(),
                            "Parse Error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(SolverPage.this,
                            "An unexpected error occurred: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Back button action: return to the MainPage.
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new MainPage();
            }
        });

        add(panel);
        setVisible(true);
    }
}