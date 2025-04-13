import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainPage extends JFrame {
    public MainPage() {
        this(null, 1);
        
    }

    public MainPage(String username, int userID) {
        setTitle("Equation Solver - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        if (username != null) {
            JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
            welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            welcomeLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 18));
            panel.add(welcomeLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JLabel selectLabel = new JLabel("Select the type of equation to solve:");
        selectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 18));
        panel.add(selectLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        String[] equationTypes = { "Polynomial", "Logarithmic", "Transcendental", "Matrix" };
        JComboBox<String> equationTypeBox = new JComboBox<>(equationTypes);
        equationTypeBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(equationTypeBox);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel subTypePanel = new JPanel();
        subTypePanel.setLayout(new BoxLayout(subTypePanel, BoxLayout.Y_AXIS));
        subTypePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subTypeLabel = new JLabel("Select method:");
        JComboBox<String> subTypeBox = new JComboBox<>();
        subTypePanel.add(subTypeLabel);
        subTypePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        subTypePanel.add(subTypeBox);
        subTypePanel.setVisible(false);
        panel.add(subTypePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        equationTypeBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selected = (String) equationTypeBox.getSelectedItem();
                if (selected.equals("Transcendental")) {
                    subTypeBox.removeAllItems();
                    subTypeBox.addItem("Bisection");
                    subTypeBox.addItem("Regula Falsi");
                    subTypeBox.addItem("Newton Raphson");
                } else if (selected.equals("Matrix")) {
                    subTypeBox.removeAllItems();
                    subTypeBox.addItem("Gaussian Elimination");
                    subTypeBox.addItem("Jacobi");
                    subTypeBox.addItem("Seidel");
                } else {
                    subTypePanel.setVisible(false);
                }
                panel.revalidate();
                panel.repaint();
            }
        });

        JButton proceedButton = new JButton("Proceed");
        proceedButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        proceedButton.setFont(new Font("JetBrains Mono", Font.BOLD, 16));
        panel.add(proceedButton);

        proceedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selected = (String) equationTypeBox.getSelectedItem();
                if (selected.equalsIgnoreCase("Polynomial") || selected.equalsIgnoreCase("Logarithmic")) {
                    dispose();
                    new SolverPage(selected, userID);
                } else if (selected.equalsIgnoreCase("Matrix")) {
                    dispose();
                    new MatrixSolverPage();
                } else if (selected.equalsIgnoreCase("Transcendental")) {
                    dispose();
                    new TranscendentalSolverPage(userID);
                } else {
                    JOptionPane.showMessageDialog(null, "Selected equation type is not supported yet.");
                }
            }
        });

        add(panel);
        setVisible(true);
    }
}
